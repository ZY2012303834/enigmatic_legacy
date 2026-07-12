package org.enigmatic_legacy.item.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.gameevent.GameEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.MajesticElytraHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class MajesticElytra extends ElytraItem implements ICurioItem {
    public MajesticElytra() {
        super(new Properties()
                .stacksTo(1)
                .durability(5000)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.ETHERIUM_INGOT.get());
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return ElytraItem.isFlyEnabled(stack);
    }

    /**
     * 允许玩家手持右键时把壮丽鞘翅交给 Curios 装备流程。
     * <p>
     * 真正的槽位限制仍然放在 {@link #canEquip(SlotContext, ItemStack)} 中，
     * 这样右键装备和手动拖入背饰栏会遵守同一套规则。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 限制壮丽鞘翅只能放入 Curios 的 back 背饰栏。
     * <p>
     * 加入 curios:back 标签只负责让 Curios 认为物品“属于背饰类型”；
     * 这里再用代码限制具体槽位，避免被其他同名或兼容槽错误接收。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return MajesticElytraHelper.BACK_SLOT.equals(context.identifier());
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        int nextFlightTick = flightTicks + 1;

        if (!entity.level().isClientSide) {
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }

                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.majestic_elytra.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.majestic_elytra.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.majestic_elytra.3"));
    }
}
