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
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MajesticElytra extends ElytraItem {
    private static final int ENCHANTMENT_VALUE = 1;

    public MajesticElytra() {
        super(new Item.Properties()
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
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return ElytraItem.isFlyEnabled(stack);
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
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return ENCHANTMENT_VALUE;
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
