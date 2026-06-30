package org.enigmatic_legacy.item.items.ring;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.EarthPromiseHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.enigmatic_legacy.util.TreasureHunterCharmHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class EarthPromise extends Item implements ICurioItem {
    private static final String RING_SLOT = "ring";

    public EarthPromise() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return RING_SLOT.equals(context.identifier());
    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        int bonus = 2;

        if (TreasureHunterCharmHelper.hasTreasureHunterCharm(entity)) {
            bonus += 1;
        }

        return ICurioItem.super.getFortuneLevel(slotContext, lootContext, stack) + bonus;
    }

    @Override
    public @NotNull Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

        if (!(slotContext.entity() instanceof Player player) || !EarthPromiseHelper.canUseEarthPromise(player)) {
            return builder.build();
        }

        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "earth_promise_armor"),
                        ConfigCommon.EARTH_PROMISE_ARMOR_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        builder.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "earth_promise_toughness"),
                        ConfigCommon.EARTH_PROMISE_TOUGHNESS_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift());
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_ones_only"));
            return;
        }

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.1",
                SpellstoneTooltip.percent(ConfigCommon.EARTH_PROMISE_ABILITY_TRIGGER_PERCENT.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.2",
                SpellstoneTooltip.number(ConfigCommon.EARTH_PROMISE_COOLDOWN.get() / 20)
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.earth_promise.3")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.4",
                SpellstoneTooltip.percent(ConfigCommon.EARTH_PROMISE_TOTAL_RESISTANCE.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(Component.translatable("curios.modifiers.ring")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.armor",
                SpellstoneTooltip.number("+" + format(ConfigCommon.EARTH_PROMISE_ARMOR_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.toughness",
                SpellstoneTooltip.number("+" + format(ConfigCommon.EARTH_PROMISE_TOUGHNESS_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.mining_speed",
                SpellstoneTooltip.percent("+" + ConfigCommon.EARTH_PROMISE_BREAK_SPEED_BONUS.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.fortune",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_ones_only"));
    }

    private static String format(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }

        return Double.toString(value);
    }
}
