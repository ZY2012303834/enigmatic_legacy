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
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EnigmaticEye;

/**
 * 休眠之眼获取事件。
 * 复刻原版逻辑：
 * - 玩家第一次打开战利品箱时，生成 1 个休眠之眼。
 * - 每个玩家只会生成 1 次。
 * - 即使玩家死亡、换维度，也不会再次生成。
 * 实现思路：
 * 1. 玩家右键方块时，先判断这个方块是不是带战利品表的容器。
 * 2. 如果是，就把这个容器的位置临时记录到玩家 NBT。
 * 3. 等容器真正打开后，再往这个容器里塞入 1 个休眠之眼。
 * 4. 成功生成后，给玩家写入永久标记，防止以后再次生成。
 */
public final class EnigmaticEyeObtainEvents {

    /**
     * 永久标记：
     * 记录玩家是否已经生成过休眠之眼。
     * 只要这个值为 true，
     * 以后不管玩家再打开多少战利品箱，都不会再生成休眠之眼。
     */
    private static final String HAS_GENERATED_DORMANT_EYE_TAG =
            "EnigmaticLegacyHasGeneratedDormantEye";

    /**
     * 临时标记：
     * 记录玩家刚刚右键的战利品容器位置。
     * 为什么需要临时标记：
     * - RightClickBlock 发生在容器真正打开之前。
     * - PlayerContainerEvent.Open 发生在容器真正打开之后。
     * - 我们需要先记住“玩家点的是哪个战利品箱”，
     *   然后等箱子打开后再把休眠之眼放进去。
     */
    private static final String PENDING_LOOT_CONTAINER_POS_TAG =
            "EnigmaticLegacyPendingDormantEyeContainerPos";

    private EnigmaticEyeObtainEvents() {
    }

    /**
     * 玩家右键方块事件。
     * 这个事件会在玩家真正打开箱子之前触发。
     * 所以我们在这里判断：
     * 1. 玩家是不是服务端玩家；
     * 2. 玩家是否已经生成过休眠之眼；
     * 3. 右键的方块是不是带战利品表的容器；
     * 4. 如果满足条件，就先把容器位置记下来。
     * 注意：
     * 这里不会直接发放休眠之眼。
     * 真正发放在 onOpenContainer(...) 里处理。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // 只处理主手，避免主手和副手各触发一次导致逻辑重复。
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        // 只在服务端处理。
        // 客户端只负责显示，不能生成真实物品。
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        // 只处理服务端玩家。
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 如果玩家已经生成过休眠之眼，则直接退出。
        // 这是防止重复生成的核心判断。
        if (hasGeneratedDormantEye(player)) {
            return;
        }

        // 获取玩家右键的方块实体。
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        // RandomizableContainerBlockEntity 是原版“可带战利品表的容器”基类。
        // 箱子、木桶、发射器、投掷器、漏斗、潜影盒等都可能属于这个体系。
        if (!(blockEntity instanceof RandomizableContainerBlockEntity container)) {
            return;
        }

        // 只有真正带 LootTable 的容器才算“战利品箱”。
        // 玩家自己放的普通箱子没有 loot table，不会触发。
        if (container.getLootTable() == null) {
            return;
        }

        // 如果容器因为锁、权限或其它原因不能打开，则不记录。
        if (!container.canOpen(player)) {
            return;
        }

        // 到这里说明：
        // 玩家右键的是一个“尚未生成内容的战利品容器”。
        // 先把容器位置记到玩家身上，等容器真正打开后再生成休眠之眼。
        player.getPersistentData().putLong(PENDING_LOOT_CONTAINER_POS_TAG, pos.asLong());
    }

    /**
     * 玩家打开容器事件。
     * 当玩家真正打开容器后，才把休眠之眼放进去。
     * 为什么不在 RightClickBlock 里直接放：
     * - 因为战利品箱的原版战利品通常会在打开时才展开。
     * - 如果太早塞物品，可能被原版战利品生成覆盖。
     * - 所以这里等容器真正打开后再放，最稳。
     */
    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        // 只处理服务端玩家。
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag playerData = player.getPersistentData();

        // 如果玩家已经生成过休眠之眼，则清掉临时标记并退出。
        if (hasGeneratedDormantEye(player)) {
            playerData.remove(PENDING_LOOT_CONTAINER_POS_TAG);
            return;
        }

        // 如果没有临时记录，说明这次打开的不是战利品容器。
        if (!playerData.contains(PENDING_LOOT_CONTAINER_POS_TAG)) {
            return;
        }

        // 读取之前记录的战利品容器位置。
        BlockPos pos = BlockPos.of(playerData.getLong(PENDING_LOOT_CONTAINER_POS_TAG));

        // 临时标记只用一次，用完立刻删除。
        // 防止玩家之后打开其它容器时误触发。
        playerData.remove(PENDING_LOOT_CONTAINER_POS_TAG);

        // 获取这个位置当前的方块实体。
        BlockEntity blockEntity = player.level().getBlockEntity(pos);

        // 再确认一次它是可带战利品表的容器。
        if (!(blockEntity instanceof RandomizableContainerBlockEntity container)) {
            return;
        }

        // 再确认一次玩家现在仍然可以打开它。
        if (!container.canOpen(player)) {
            return;
        }

        // 创建休眠之眼。
        ItemStack dormantEye = createDormantEye();

        // 尝试把休眠之眼放进容器里。
        // 如果箱子满了，就放进玩家背包。
        // 如果玩家背包也满了，就掉落到玩家脚下。
        giveDormantEye(player, container, dormantEye);

        // 写入永久标记。
        // 这是最关键的一步：
        // 从现在开始，这名玩家再也不会触发休眠之眼生成。
        setGeneratedDormantEye(player);
    }

    /**
     * 玩家克隆事件。
     * 玩家死亡重生、从末地回主世界等情况可能会创建新的玩家实体。
     * 为了保证“已经生成过休眠之眼”的记录不会丢失，
     * 这里把旧玩家身上的标记复制到新玩家身上。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        // 复制永久生成标记。
        // 只要旧玩家生成过，新玩家也算生成过。
        if (oldData.getBoolean(HAS_GENERATED_DORMANT_EYE_TAG)) {
            newData.putBoolean(HAS_GENERATED_DORMANT_EYE_TAG, true);
        }
    }

    /**
     * 创建一个“休眠状态”的休眠之眼。
     * EnigmaticEye 默认没有写入 NBT 时就会被视为休眠状态。
     * 这里仍然显式调用 setDormant(true)，让生成结果更明确。
     */
    private static ItemStack createDormantEye() {
        ItemStack stack = new ItemStack(ModItems.ENIGMATIC_EYE.get());

        // 显式设置为休眠状态。
        EnigmaticEye.setDormant(stack, true);

        return stack;
    }

    /**
     * 把休眠之眼发放给玩家。
     * 优先级：
     * 1. 优先放进玩家打开的战利品容器里；
     * 2. 如果容器满了，放进玩家背包；
     * 3. 如果背包也满了，掉落到玩家脚下。
     */
    private static void giveDormantEye(ServerPlayer player, Container container, ItemStack dormantEye) {
        // 先尝试放入容器。
        if (tryInsertIntoContainer(container, dormantEye)) {
            container.setChanged();
            return;
        }

        // 如果容器没有空位，就尝试放进玩家背包。
        if (player.getInventory().add(dormantEye)) {
            return;
        }

        // 如果背包也满了，就掉落到玩家脚下。
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
     * - 容器没有空位。
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