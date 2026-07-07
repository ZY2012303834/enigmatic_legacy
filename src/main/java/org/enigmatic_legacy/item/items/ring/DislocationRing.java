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
 * 转位之戒 / Dislocation Ring。
 * 定位：
 * 磁力之戒的高级版本。
 * 与磁力之戒区别：
 * 1. 磁力之戒会把物品“拉向玩家”；
 * 2. 转位之戒会直接尝试让附近物品被玩家拾取；
 * 3. 转位之戒范围更大，默认 16 格；
 * 4. 转位之戒和磁力之戒不能同时佩戴；
 * 5. 复用磁力之戒 UI 开关。
 */
public class DislocationRing extends Item implements ICurioItem {
    /**
     * 兼容其他模组的“禁止远程移动”标记。
     * 如果物品实体带有这个标记，转位之戒不会处理它。
     */
    private static final String PREVENT_REMOTE_MOVEMENT_TAG = "PreventRemoteMovement";

    /**
     * 单 tick 最大处理数量。
     * 原项目对转位之戒的处理上限更高。
     * 这里使用 512，避免大型物品堆造成过高开销。
     */
    private static final int MAX_TELEPORTED_ITEMS_PER_TICK = 512;

    public DislocationRing() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
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
     * 规则：
     * 1. 转位之戒只能佩戴一个；
     * 2. 磁力之戒和转位之戒不能同时佩戴；
     * 3. 因此只要玩家已经佩戴任意“磁力控制戒指”，就不允许再装备转位之戒。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        return CuriosLookupApi.findFirstSlot(entity, MagnetRingHelper::isMagnetControlRing).isEmpty();
    }

    /**
     * Curios 每 tick 调用。
     * 只有服务端执行真正的物品拾取逻辑。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        // 只对玩家生效。
        if (!(entity instanceof Player player)) {
            return;
        }

        // 只在服务端处理物品实体。
        if (player.level().isClientSide) {
            return;
        }

        // 复用磁力之戒 UI 开关。
        // 关闭后，转位之戒也不会拾取物品。
        if (!MagnetRingHelper.isMagnetEnabled(stack)) {
            return;
        }

        // 默认按住 Shift 暂停远程拾取。
        // 如果配置禁用 Shift 抑制，则潜行时也照常生效。
        if (player.isShiftKeyDown() && !ConfigCommon.DISABLE_AOE_SHIFT_SUPPRESSION.get()) {
            return;
        }

        collectNearbyItems(player);
    }

    /**
     * 搜索并直接拾取附近物品。
     *
     * 注意：
     * 转位之戒不是磁力吸附。
     * 它不会修改掉落物速度，而是直接触发玩家拾取逻辑。
     */
    private void collectNearbyItems(Player player) {
        double range = ConfigCommon.DISLOCATION_RING_RANGE.get();

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
                this::canTeleportItem
        );

        int collected = 0;

        for (ItemEntity item : items) {
            if (collected >= MAX_TELEPORTED_ITEMS_PER_TICK) {
                break;
            }

            if (!canPlayerAcceptItem(player, item.getItem())) {
                continue;
            }

            item.setNoPickUpDelay();
            item.playerTouch(player);

            collected++;
        }
    }

    /**
     * 判断物品实体是否允许被转位之戒处理。
     */
    private boolean canTeleportItem(@NotNull ItemEntity item) {
        ItemStack stack = item.getItem();

        if (!item.isAlive()) {
            return false;
        }

        if (stack.isEmpty()) {
            return false;
        }

        return !item.getPersistentData().getBoolean(PREVENT_REMOTE_MOVEMENT_TAG);
    }

    /**
     * 判断玩家背包是否还能接收目标物品。
     *
     * 不直接调用 addItem，因为这里只做预检查。
     */
    private boolean canPlayerAcceptItem(Player player, ItemStack stack) {
        if (player.isCreative()) {
            return true;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack slotStack = player.getInventory().getItem(slot);

            if (slotStack.isEmpty()) {
                return true;
            }

            if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int maxStackSize = Math.min(
                        slotStack.getMaxStackSize(),
                        player.getInventory().getMaxStackSize()
                );

                if (slotStack.getCount() < maxStackSize) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Tooltip。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.dislocation_ring.1",
                formatRange(ConfigCommon.DISLOCATION_RING_RANGE.get())
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.dislocation_ring.2")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.dislocation_ring.3")
                .withStyle(ChatFormatting.GRAY));

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
