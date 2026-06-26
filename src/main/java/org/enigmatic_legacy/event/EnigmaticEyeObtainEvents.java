package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
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

        // 如果玩家已经生成过休眠之眼，直接退出。
        // 这是保证“只生成一个”的核心。
        if (hasGeneratedDormantEye(player)) {
            return;
        }

        // 获取玩家右键的方块位置。
        BlockPos pos = event.getPos();

        // 获取这个位置的方块实体。
        BlockEntity blockEntity = level.getBlockEntity(pos);

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
        giveDormantEye(player, container, dormantEye);

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
     * 优先级：
     * 1. 放入玩家正在打开的战利品容器；
     * 2. 容器满了，放进玩家背包；
     * 3. 背包也满了，掉落在玩家脚下。
     */
    private static void giveDormantEye(ServerPlayer player, Container container, ItemStack dormantEye) {
        // 优先尝试放进战利品容器。
        if (tryInsertIntoContainer(container, dormantEye)) {
            container.setChanged();
            return;
        }

        // 如果箱子满了，尝试放进玩家背包。
        if (player.getInventory().add(dormantEye)) {
            return;
        }

        // 如果背包也满了，就掉在玩家脚下。
        ItemEntity droppedEye = new ItemEntity(
                player.level(),
                player.getX(),
                player.getY(),
                player.getZ(),
                dormantEye
        );

        droppedEye.setDefaultPickUpDelay();
        player.level().addFreshEntity(droppedEye);
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
}