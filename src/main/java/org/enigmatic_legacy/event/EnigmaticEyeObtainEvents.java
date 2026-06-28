package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EnigmaticEye;

/**
 * 休眠之眼获取事件。
 * 复刻逻辑：
 * - 玩家第一次打开真正的战利品箱时，生成 1 个休眠之眼。
 * - 每个玩家只会生成 1 次。
 * - 生成后永久记录，不会在其它战利品箱里继续生成。
 * 重要修正版：
 * - 之前版本使用 RightClickBlock 记录位置，再等 PlayerContainerEvent.Open 塞入物品。
 * - 实测在当前 1.21.1 NeoForge 中不稳定，可能导致休眠之眼没有生成。
 * - 现在改为在玩家右键战利品容器时：
 *   1. 先手动展开原版战利品表；
 *   2. 再立即把休眠之眼放入容器；
 *   3. 成功后写入玩家永久标记。
 * 这样可以保证：
 * - 不会被原版战利品生成覆盖；
 * - 不依赖容器打开事件；
 * - 只生成一次。
 */
public final class EnigmaticEyeObtainEvents {

    /**
     * 永久标记：
     * 记录玩家是否已经生成过休眠之眼。
     * 只要这个值为 true，
     * 以后这个玩家再打开任何战利品箱，都不会再生成休眠之眼。
     */
    private static final String HAS_GENERATED_DORMANT_EYE_TAG =
            "EnigmaticLegacyHasGeneratedDormantEye";

    private EnigmaticEyeObtainEvents() {
    }

    /**
     * 玩家右键方块事件。
     * 触发时机：
     * - 玩家右键箱子、木桶、发射器、投掷器等方块时触发。
     * 本方法只处理“带战利品表的容器”。
     * 普通玩家自己放的箱子没有 LootTable，不会触发。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // 只处理主手。
        // 否则主手、副手可能各触发一次，导致逻辑重复。
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        // 只在服务端执行。
        // 客户端不能真正生成物品。
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        // 只处理服务端玩家。
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 如果玩家正在潜行，不处理。
        // 这样可以避免和 Carry On 这类“潜行右键搬箱子”的模组发生冲突。
        if (player.isShiftKeyDown()) {
            return;
        }

        // 如果玩家已经生成过休眠之眼，直接退出。
        // 这是保证“只生成一个”的核心。
        if (hasGeneratedDormantEye(player)) {
            return;
        }

        // 获取玩家右键的方块位置。
        BlockPos pos = event.getPos();

        // 获取这个位置的方块实体。
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (isLootrBlockEntity(blockEntity)) {
            return;
        }

        // RandomizableContainerBlockEntity 是原版可带战利品表容器的基类。
        // 地牢箱、神殿箱、沉船箱、村庄箱等战利品箱通常属于这个体系。
        if (!(blockEntity instanceof RandomizableContainerBlockEntity container)) {
            return;
        }

        // 如果没有 LootTable，说明它不是未打开过的战利品箱。
        // 玩家自己放置的普通箱子也会走到这里并直接 return。
        if (container.getLootTable() == null) {
            return;
        }

        // 如果容器因为锁或其它原因不能打开，就不生成。
        if (!container.canOpen(player)) {
            return;
        }

        /*
         * 关键修复：
         *
         * 主动展开原版战利品表。
         *
         * 原版战利品箱的内容不是生成世界时就确定的，
         * 而是玩家第一次打开时才根据 LootTable 展开。
         *
         * 如果我们在战利品表展开之前直接往容器里塞物品，
         * 可能会被原版战利品生成流程覆盖。
         *
         * 所以这里先调用 unpackLootTable(player)，
         * 让原版战利品先生成完毕，
         * 然后我们再把休眠之眼塞进去。
         */
        container.unpackLootTable(player);

        // 创建休眠之眼。
        ItemStack dormantEye = createDormantEye();

        // 发放休眠之眼。
        // 优先放进这个战利品容器；
        // 如果容器满了，就放进玩家背包；
        // 如果背包也满了，就掉在玩家脚下。
        //
        // 注意：这里额外传入 pos。
        // 因为奖励箱这类容器可能会出现“服务端已经塞入物品，
        // 但客户端打开界面没有立即刷新”的情况。
        // 后面会用 pos 主动同步方块和容器界面。
        giveDormantEye(player, pos, container, dormantEye);

        // 写入永久标记。
        // 从这一刻开始，这个玩家不会再生成第二个休眠之眼。
        setGeneratedDormantEye(player);
    }

    /**
     * 玩家克隆事件。
     * 玩家死亡重生、从末地回主世界等情况，
     * 可能会创建一个新的玩家实体。
     * 这里把旧玩家身上的“已经生成过休眠之眼”标记复制给新玩家，
     * 防止玩家死亡后再次打开战利品箱又生成一个。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        // 如果旧玩家已经生成过休眠之眼，
        // 新玩家也继承这个状态。
        if (oldData.getBoolean(HAS_GENERATED_DORMANT_EYE_TAG)) {
            newData.putBoolean(HAS_GENERATED_DORMANT_EYE_TAG, true);
        }
    }

    /**
     * 创建一个休眠状态的休眠之眼。
     * EnigmaticEye 默认没有数据时就是休眠状态。
     * 这里仍然显式写入休眠状态，避免以后逻辑变化。
     */
    private static ItemStack createDormantEye() {
        ItemStack stack = new ItemStack(ModItems.ENIGMATIC_EYE.get());

        // 显式设置为休眠。
        EnigmaticEye.setDormant(stack, true);

        return stack;
    }

    /**
     * 发放休眠之眼。
     * 新逻辑：
     * 1. 优先放入玩家正在打开的战利品容器；
     * 2. 如果容器已经满了，不再直接给予玩家；
     * 3. 而是随机替换容器中的一个物品。
     * 为什么这样改：
     * - 原版逻辑更像“休眠之眼出现在第一个战利品箱中”；
     * - 如果箱子满了却直接给玩家，会让获取方式看起来不像箱子奖励；
     * - 所以满箱时直接随机替换箱子中的一个格子。
     * 注意：
     * - 被替换掉的物品会消失；
     * - 休眠之眼仍然只会生成一次；
     * - 不会额外进入玩家背包。
     */
    private static void giveDormantEye(ServerPlayer player, BlockPos containerPos, Container container, ItemStack dormantEye) {
        // 优先尝试放进战利品容器的空格子。
        if (tryInsertIntoContainer(container, dormantEye)) {
            // 标记容器内容已经改变。
            container.setChanged();

            // 主动同步容器和方块到客户端。
            // 修复“服务端有物品，客户端界面不刷新”的问题。
            syncContainerToClient(player, containerPos);

            return;
        }

        /*
         * 如果走到这里，说明箱子已经满了。
         *
         * 旧逻辑：
         * - 放进玩家背包；
         * - 背包满了再掉落。
         *
         * 新逻辑：
         * - 不再给玩家；
         * - 直接随机替换箱子里的一个格子。
         */
        replaceRandomContainerItem(player, container, dormantEye);

        // 标记容器内容已经改变。
        container.setChanged();

        // 同步容器内容到客户端。
        // 这样玩家打开箱子时能直接看到休眠之眼。
        syncContainerToClient(player, containerPos);
    }

    /**
     * 随机替换容器中的一个物品。
     * 用途：
     * - 当奖励箱已经满了，没有空格子可以放入休眠之眼时调用。
     * 逻辑：
     * - 从容器所有格子中随机选择一个格子；
     * - 用休眠之眼替换该格子中的原物品；
     * - 原物品不会掉落，也不会返还给玩家。
     * 为什么不掉落被替换物：
     * - 用户要求“随机替换其中的一个物品”；
     * - 替换意味着箱子中的某个奖励被休眠之眼取代。
     */
    private static void replaceRandomContainerItem(ServerPlayer player, Container container, ItemStack dormantEye) {
        // 获取容器格子数量。
        int containerSize = container.getContainerSize();

        // 安全判断。
        // 正常箱子、奖励箱、木桶等容器不会出现 size <= 0。
        // 这里是为了避免极端情况下随机数报错。
        if (containerSize <= 0) {
            return;
        }

        // 随机选择一个格子。
        int randomSlot = player.getRandom().nextInt(containerSize);

        // 用休眠之眼替换该格子中的原物品。
        // 使用 copy()，避免外部 ItemStack 引用被后续逻辑意外修改。
        container.setItem(randomSlot, dormantEye.copy());
    }

    /**
     * 主动同步容器内容到客户端。
     * 重要说明：
     * - 这个方法会延迟 1 tick 执行。
     * - 原因是 RightClickBlock 事件触发时，箱子菜单通常还没有真正打开。
     * - 如果此时立刻 broadcastChanges，可能同步的是玩家背包菜单，不是箱子菜单。
     * 延迟 1 tick 后：
     * - 玩家已经打开箱子界面；
     * - player.containerMenu 才会变成真正的箱子菜单；
     * - 此时再 broadcastChanges，客户端才能看到刚刚塞进去的休眠之眼。
     */
    private static void syncContainerToClient(ServerPlayer player, BlockPos containerPos) {
        // 获取服务器实例。
        MinecraftServer server = player.getServer();

        // 理论上服务端玩家一定有 server，但这里做一次安全判断。
        if (server == null) {
            return;
        }

        // 延迟到下一 tick 执行同步。
        server.execute(() -> {
            // 当前逻辑只应该在服务端执行。
            if (!(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            // 获取容器所在方块状态。
            BlockState state = serverLevel.getBlockState(containerPos);

            // 通知客户端这个方块状态需要刷新。
            // flags = 3 表示：
            // - 更新客户端；
            // - 通知邻近方块。
            serverLevel.sendBlockUpdated(containerPos, state, state, 3);

            // 同步玩家当前打开的菜单。
            // 延迟 1 tick 后，这里通常已经是箱子菜单。
            player.containerMenu.broadcastChanges();

            // 同步玩家背包菜单。
            player.inventoryMenu.broadcastChanges();
        });
    }

    /**
     * 尝试把物品插入容器的第一个空格。
     * 返回 true：
     * - 成功放入容器。
     * 返回 false：
     * - 容器没有空格。
     */
    private static boolean tryInsertIntoContainer(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existingStack = container.getItem(slot);

            if (existingStack.isEmpty()) {
                container.setItem(slot, stack.copy());
                return true;
            }
        }

        return false;
    }

    /**
     * 判断玩家是否已经生成过休眠之眼。
     */
    private static boolean hasGeneratedDormantEye(ServerPlayer player) {
        return player.getPersistentData().getBoolean(HAS_GENERATED_DORMANT_EYE_TAG);
    }

    /**
     * 标记玩家已经生成过休眠之眼。
     */
    private static void setGeneratedDormantEye(ServerPlayer player) {
        player.getPersistentData().putBoolean(HAS_GENERATED_DORMANT_EYE_TAG, true);
    }

    private static boolean isLootrBlockEntity(BlockEntity blockEntity) {
        return blockEntity != null
                && blockEntity.getClass().getName().startsWith("noobanidus.mods.lootr.");
    }
}
