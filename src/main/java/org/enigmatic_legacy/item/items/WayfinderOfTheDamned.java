package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.enigmatic_legacy.entity.PermanentItemEntity;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 被诅咒者的寻路指针 / Wayfinder of the Damned
 * 复刻说明：
 * - 只有承受七咒之人才能使用。
 * - 指向当前维度内离玩家最近的灵魂水晶。
 * - 原版无法在灵魂沙峡谷工作，这里按用户要求改为：灵魂沙峡谷也能工作。
 * 当前项目适配：
 * - 你的项目里，玩家死亡后灵魂水晶会作为 PermanentItemEntity 掉落。
 * - 如果玩家死亡时有其它掉落物，灵魂水晶可能会被嵌入 STORAGE_CRYSTAL / 超维容器。
 * - 所以这里同时定位：
 *   1. PermanentItemEntity 中的 SOUL_CRYSTAL；
 *   2. PermanentItemEntity 中的 STORAGE_CRYSTAL；
 *   3. 普通 ItemEntity 中的 SOUL_CRYSTAL；
 *   4. 普通 ItemEntity 中的 STORAGE_CRYSTAL。
 * 注意：
 * - Minecraft 默认无法在未加载区块中直接搜索实体。
 * - 所以这里定位的是“当前维度中已加载区域内”的最近目标。
 * - 死亡地点通常在附近加载范围内时可以正常定位。
 */
public class WayfinderOfTheDamned extends Item {

    /**
     * 搜索范围。
     * 这里设置为 4096 格。
     * 对实体扫描来说，搜索整个维度的所有未加载区块不现实；
     * 但 4096 格已经足够覆盖大多数死亡找回场景。
     */
    private static final double SEARCH_RANGE = 4096.0D;

    /**
     * 存储目标 X 坐标的自定义数据 key。
     */
    private static final String TARGET_X_TAG = "TargetX";

    /**
     * 存储目标 Y 坐标的自定义数据 key。
     */
    private static final String TARGET_Y_TAG = "TargetY";

    /**
     * 存储目标 Z 坐标的自定义数据 key。
     */
    private static final String TARGET_Z_TAG = "TargetZ";

    /**
     * 是否已经找到目标。
     */
    private static final String HAS_TARGET_TAG = "HasTarget";

    public WayfinderOfTheDamned() {
        super(new Item.Properties()
                // 指针类遗物只允许堆叠 1 个。
                .stacksTo(1)
                // 设为稀有品质。
                .rarity(Rarity.RARE)
                // 不被火焰和岩浆烧毁。
                .fireResistant()
        );
    }

    /**
     * 右键使用。
     * 按原项目风格：
     * - 不弹出坐标；
     * - 不弹出方向；
     * - 不提示“没有找到灵魂水晶”；
     * - 只刷新一次目标数据。
     * 真正的方向表现由客户端模型属性 angle 控制。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        // 客户端不做搜索，只等待服务端同步物品数据。
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        // 没有佩戴七咒之戒时，清除目标，让指针随机旋转。
        if (!CursedRingHelper.hasCursedRing(serverPlayer)) {
            clearTarget(stack);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        // 搜索当前维度内最近的灵魂水晶 / 超维容器。
        Entity target = findNearestSoulTarget(serverPlayer);

        // 没找到目标时，清除目标，让指针随机旋转。
        if (target == null) {
            clearTarget(stack);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        // 找到目标时保存坐标，客户端模型属性会读取这个坐标并指向它。
        storeTarget(stack, target.blockPosition());

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /**
     * 背包 tick。
     * 用途：
     * - 当玩家把寻路指针拿在主手或副手时，每秒自动刷新一次目标；
     * - 这样玩家不需要一直右键，也能持续看到目标方向。
     * 注意：
     * - 为避免性能问题，只在服务端、每 20 tick 执行一次。
     * - 只在主手或副手时刷新，不在背包其它位置刷新。
     */
    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slotId,
            boolean isSelected
    ) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // 客户端不执行搜索。
        if (level.isClientSide()) {
            return;
        }

        // 只处理服务端玩家。
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        // 每秒刷新一次，避免每 tick 搜索实体造成性能浪费。
        if (player.tickCount % 20 != 0) {
            return;
        }

        // 只在玩家拿在主手或副手时刷新。
        boolean inMainHand = player.getMainHandItem() == stack;
        boolean inOffHand = player.getOffhandItem() == stack;

        if (!inMainHand && !inOffHand) {
            return;
        }

        // 没有佩戴七咒之戒时，不给目标。
        if (!CursedRingHelper.hasCursedRing(player)) {
            clearTarget(stack);
            return;
        }

        // 查找最近目标。
        Entity target = findNearestSoulTarget(player);

        if (target == null) {
            clearTarget(stack);
            return;
        }

        // 记录目标坐标，供 tooltip 显示。
        storeTarget(stack, target.blockPosition());
    }

    /**
     * 搜索当前维度内最近的灵魂目标。
     * 当前会搜索：
     * - PermanentItemEntity 中的 SOUL_CRYSTAL；
     * - PermanentItemEntity 中的 STORAGE_CRYSTAL；
     * - 普通 ItemEntity 中的 SOUL_CRYSTAL；
     * - 普通 ItemEntity 中的 STORAGE_CRYSTAL。
     * 为什么包含 STORAGE_CRYSTAL：
     * - 你当前死亡逻辑中，如果玩家死亡时有其它掉落物，
     *   灵魂水晶会被嵌入超维容器中。
     * - 此时世界里不一定有单独的 SOUL_CRYSTAL 实体。
     * - 如果只查 SOUL_CRYSTAL，会出现指针找不到目标的问题。
     */
    private static Entity findNearestSoulTarget(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return null;
        }

        AABB searchBox = player.getBoundingBox().inflate(SEARCH_RANGE);

        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        /*
         * 查找 PermanentItemEntity。
         *
         * 你的项目里灵魂水晶和超维容器死亡后都会用 PermanentItemEntity 承载。
         */
        List<PermanentItemEntity> permanentItems = level.getEntitiesOfClass(
                PermanentItemEntity.class,
                searchBox,
                entity -> entity.isAlive() && isSoulTargetStack(entity.getItem())
        );

        for (PermanentItemEntity entity : permanentItems) {
            double distance = entity.distanceToSqr(player);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }

        /*
         * 兜底查找普通 ItemEntity。
         *
         * 正常情况下你的灵魂水晶多半是 PermanentItemEntity；
         * 但保留普通 ItemEntity 检测可以兼容命令生成、其它模组或后续逻辑。
         */
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                entity -> entity.isAlive() && isSoulTargetStack(entity.getItem())
        );

        for (ItemEntity entity : itemEntities) {
            double distance = entity.distanceToSqr(player);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }

        return nearest;
    }

    /**
     * 判断一个 ItemStack 是否是寻路指针应该定位的目标。
     */
    private static boolean isSoulTargetStack(ItemStack stack) {
        return stack.is(ModItems.SOUL_CRYSTAL.get())
                || stack.is(ModItems.STORAGE_CRYSTAL.get());
    }

    /**
     * 把目标坐标写入物品自定义数据。
     * 使用 DataComponents.CUSTOM_DATA：
     * - 这是 1.21+ 推荐的物品自定义数据存储方式；
     * - 你的 EnigmaticEye 也已经使用这种方式保存状态。
     */
    private static void storeTarget(ItemStack stack, BlockPos pos) {
        CompoundTag tag = copyCustomTag(stack);

        tag.putBoolean(HAS_TARGET_TAG, true);
        tag.putInt(TARGET_X_TAG, pos.getX());
        tag.putInt(TARGET_Y_TAG, pos.getY());
        tag.putInt(TARGET_Z_TAG, pos.getZ());

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 清除目标坐标。
     */
    private static void clearTarget(ItemStack stack) {
        CompoundTag tag = copyCustomTag(stack);

        tag.putBoolean(HAS_TARGET_TAG, false);
        tag.remove(TARGET_X_TAG);
        tag.remove(TARGET_Y_TAG);
        tag.remove(TARGET_Z_TAG);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 复制物品的 CUSTOM_DATA。
     */
    private static CompoundTag copyCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    /**
     * 判断物品是否保存了目标。
     */
    private static boolean hasTarget(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return false;
        }

        CompoundTag tag = data.copyTag();
        return tag.getBoolean(HAS_TARGET_TAG);
    }

    /**
     * 给客户端模型属性读取：当前指针是否已经保存目标。
     * 用途：
     * - 客户端 angle 属性会调用这个方法；
     * - 如果有目标，就正常指向目标；
     * - 如果没有目标，就像原版指南针失效一样随机旋转。
     */
    public static boolean hasStoredTarget(ItemStack stack) {
        return hasTarget(stack);
    }

    /**
     * 给客户端模型属性读取：当前指针保存的目标坐标。
     * 用途：
     * - 客户端 angle 属性需要根据玩家朝向和目标坐标计算贴图角度。
     */
    public static BlockPos getStoredTargetPosition(ItemStack stack) {
        return getStoredTarget(stack);
    }

    /**
     * 从物品中读取目标坐标。
     */
    private static BlockPos getStoredTarget(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return BlockPos.ZERO;
        }

        CompoundTag tag = data.copyTag();

        return new BlockPos(
                tag.getInt(TARGET_X_TAG),
                tag.getInt(TARGET_Y_TAG),
                tag.getInt(TARGET_Z_TAG)
        );
    }

    /**
     * 物品提示文本。
     * 按原项目风格：
     * - 只说明物品用途；
     * - 不显示目标坐标；
     * - 不显示距离；
     * - 不显示方向。
     * 目标方向应通过指南针贴图表现，而不是文字提示。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.wayfinder_of_the_damned.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.wayfinder_of_the_damned.2")
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * 有目标时显示附魔光效。
     * 这样玩家可以直观看出指针是否已经找到目标。
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return hasTarget(stack);
    }
}