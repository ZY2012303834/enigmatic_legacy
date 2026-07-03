package org.enigmatic_legacy.item.items.ring;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.MagnetRingHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 磁力之戒 / Magnetic Ring。
 * 功能：
 * 1. 装备在 Curios 的 ring 戒指栏后生效。
 * 2. 自动吸取附近掉落物。
 * 3. 玩家潜行时暂停吸取，避免整理箱子、农场时误吸一堆东西。
 * 4. 尊重物品实体的 PreventRemoteMovement 标记，避免和其他模组的“禁止远程移动”逻辑冲突。
 */
public class MagnetRing extends Item implements ICurioItem {
    /**
     * 原项目中使用的 NBT 标记。
     * 如果某个 ItemEntity 带有这个布尔标记，并且值为 true，
     * 磁力之戒不会移动它。
     */
    private static final String PREVENT_REMOTE_MOVEMENT_TAG = "PreventRemoteMovement";

    /**
     * 单次 tick 最多处理多少个掉落物。
     * 原项目也有限制，避免玩家附近堆积大量掉落物时造成明显卡顿。
     */
    private static final int MAX_PULLED_ITEMS_PER_TICK = 200;

    /**
     * 附近掉落物扫描比较重，隔 tick 执行仍然足够顺滑。
     */
    private static final int PULL_INTERVAL_TICKS = 2;

    /**
     * 基础吸取速度。
     * 距离越远会稍微加速，但不会无限变快。
     */
    private static final double BASE_PULL_SPEED = 0.15D;

    /**
     * 距离速度倍率。
     * 物品离玩家越远，吸取速度越高一点点。
     */
    private static final double DISTANCE_PULL_SPEED = 0.06D;

    /**
     * 最大吸取速度。
     * 防止物品以过高速度乱飞。
     */
    private static final double MAX_PULL_SPEED = 0.80D;

    public MagnetRing() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 允许右键直接装备到 Curios 戒指栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 装备限制。
     * 转位之戒只能佩戴一个。
     * 转位之戒和磁力之戒不能同时佩戴。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        return CuriosLookupApi.findFirstSlot(entity, MagnetRingHelper::isMagnetControlRing).isEmpty();
    }
    /**
     * Curios 每 tick 调用一次。
     * 这里不需要额外注册 NeoForge 事件；
     * 只要物品实现 ICurioItem，并且被放进 Curios 槽位，Curios 就会调用 curioTick。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        // 磁力之戒只对玩家生效。
        if (!(entity instanceof Player player)) {
            return;
        }

        // 只在服务端移动掉落物。
        // 客户端只负责显示，真实实体运动由服务端同步。
        if (player.level().isClientSide) {
            return;
        }

        // UI 开关关闭时，不吸取物品。
        if (!MagnetRingHelper.isMagnetEnabled(stack)) {
            return;
        }

        // 按下潜行键时，磁力之戒不生效。
        if (player.isShiftKeyDown() && !ConfigCommon.DISABLE_AOE_SHIFT_SUPPRESSION.get()) {
            return;
        }

        if (player.tickCount % PULL_INTERVAL_TICKS != 0) {
            return;
        }

        pullNearbyItems(player);
    }

    /**
     * 吸取玩家附近掉落物。
     */
    private void pullNearbyItems(Player player) {
        double range = ConfigCommon.MAGNET_RING_RANGE.get();

        // 原项目以玩家身体附近为中心，这里使用眼睛以下一点的位置，
        // 让物品被拉向玩家身体中部，而不是脚底。
        Vec3 center = player.position().add(0.0D, 0.75D, 0.0D);

        AABB area = new AABB(
                center.x - range,
                center.y - range,
                center.z - range,
                center.x + range,
                center.y + range,
                center.z + range
        );

        List<ItemEntity> items = player.level().getEntitiesOfClass(
                ItemEntity.class,
                area,
                this::canPullItem
        );

        int pulled = 0;

        for (ItemEntity item : items) {
            if (pulled >= MAX_PULLED_ITEMS_PER_TICK) {
                break;
            }

            // 如果玩家背包完全装不下这个物品，就不要继续吸它。
            // 否则满背包时物品会一直围着玩家转，观感很糟糕。
            if (!canPlayerAcceptItem(player, item.getItem())) {
                continue;
            }

            pullItemToPlayer(player, item, center);
            pulled++;
        }
    }

    /**
     * 判断某个掉落物能不能被磁力之戒移动。
     */
    private boolean canPullItem(@NotNull ItemEntity item) {
        ItemStack stack = item.getItem();

        if (!item.isAlive()) {
            return false;
        }

        if (stack.isEmpty()) {
            return false;
        }

        // 兼容其他模组：有些物品会主动给 ItemEntity 写这个标记，
        // 表示“不要被磁铁、传送器、远程收集器移动”。
        return !item.getPersistentData().getBoolean(PREVENT_REMOTE_MOVEMENT_TAG);
    }

    /**
     * 判断玩家背包是否还能接收这个物品。
     * <p>
     * 这里不直接调用 addItem，因为那会真的把物品塞进背包；
     * 我们只想判断能不能装下，然后继续让原版拾取逻辑完成最终拾取。
     */
    private boolean canPlayerAcceptItem(Player player, ItemStack stack) {
        // 创造模式玩家不用检查背包空间。
        if (player.isCreative()) {
            return true;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack slotStack = player.getInventory().getItem(slot);

            // 有空槽，肯定能装。
            if (slotStack.isEmpty()) {
                return true;
            }

            // 同物品、同 Data Components，且还没叠满，也能装。
            if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int maxStackSize = Math.min(slotStack.getMaxStackSize(), player.getInventory().getMaxStackSize());

                if (slotStack.getCount() < maxStackSize) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 把掉落物拉向玩家。
     */
    private void pullItemToPlayer(Player player, ItemEntity item, Vec3 center) {
        Vec3 toPlayer = center.subtract(item.position());
        double distance = toPlayer.length();

        // 距离太近时只清除拾取延迟，让原版拾取逻辑接手。
        if (distance < 0.05D) {
            item.setNoPickUpDelay();
            return;
        }

        double speed = Math.min(
                MAX_PULL_SPEED,
                BASE_PULL_SPEED + distance * DISTANCE_PULL_SPEED
        );

        item.setDeltaMovement(toPlayer.normalize().scale(speed));
        item.setNoPickUpDelay();

        // 标记实体速度发生变化，帮助服务端同步。
        item.hasImpulse = true;
    }

    /**
     * Tooltip。
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.magnet_ring.1",
                formatRange(ConfigCommon.MAGNET_RING_RANGE.get())
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.magnet_ring.2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                MagnetRingHelper.isMagnetEnabled(stack)
                        ? "tooltip.enigmatic_legacy.magnet_ring.enabled"
                        : "tooltip.enigmatic_legacy.magnet_ring.disabled"
        ).withStyle(MagnetRingHelper.isMagnetEnabled(stack)
                ? ChatFormatting.GREEN
                : ChatFormatting.RED));
    }

    private String formatRange(double range) {
        if (range == Math.rint(range)) {
            return Integer.toString((int) range);
        }

        return String.format("%.1f", range);
    }
}
