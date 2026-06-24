package org.enigmatic_legacy.item.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class OceanStone extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    private static final ResourceLocation SWIM_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID, "ocean_stone_swim_speed"
    );

    private static final ResourceLocation UNDERWATER_GRAVITY_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID, "ocean_stone_underwater_gravity"
    );

    private static final int XP_COST_BASE = 150;

    public OceanStone() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    private boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        entity.getAttributes().removeAttributeModifiers(getSwimSpeedModifiers());
        entity.getAttributes().removeAttributeModifiers(getUnderwaterGravityModifiers());

        entity.getAttributes().addTransientAttributeModifiers(getSwimSpeedModifiers());

        if (isEyeInWater(entity)) {
            entity.getAttributes().addTransientAttributeModifiers(getUnderwaterGravityModifiers());

            if (!entity.level().isClientSide()) {
                entity.setAirSupply(entity.getMaxAirSupply());

                entity.addEffect(new MobEffectInstance(
                        MobEffects.WATER_BREATHING,
                        260,
                        0,
                        true,
                        false
                ));

                entity.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION,
                        ConfigCommon.OCEAN_STONE_NIGHT_VISION_DURATION.get(),
                        0,
                        true,
                        false
                ));
            }
        }
    }

    private static boolean isEyeInWater(LivingEntity entity) {
        return entity.getEyeInFluidType() == NeoForgeMod.WATER_TYPE.value();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        entity.getAttributes().removeAttributeModifiers(getSwimSpeedModifiers());
        entity.getAttributes().removeAttributeModifiers(getUnderwaterGravityModifiers());

        ICurioItem.super.onUnequip(slotContext, newStack, stack);
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getSwimSpeedModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                NeoForgeMod.SWIM_SPEED,
                new AttributeModifier(
                        SWIM_SPEED_ID,
                        ConfigCommon.OCEAN_STONE_SWIM_SPEED_BONUS.get() / 100.0D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );

        return modifiers;
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getUnderwaterGravityModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.GRAVITY,
                new AttributeModifier(
                        UNDERWATER_GRAVITY_ID,
                        -1.0D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        return modifiers;
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        if (level.dimension() == Level.NETHER || level.dimension() == Level.END) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.ocean_stone.wrong_dimension")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        if (level.getLevelData().isThundering()) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.ocean_stone.already_thundering")
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
            return;
        }

        int requiredExperience = Mth.floor(XP_COST_BASE * 2.0D * ConfigCommon.OCEAN_STONE_XP_COST_MODIFIER.get());

        if (!player.isCreative() && getTotalExperience(player) < requiredExperience) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.ocean_stone.not_enough_xp")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        if (!player.isCreative()) {
            int cost = Mth.floor(
                    (XP_COST_BASE + player.getRandom().nextInt(XP_COST_BASE + 1))
                            * ConfigCommon.OCEAN_STONE_XP_COST_MODIFIER.get()
            );

            player.giveExperiencePoints(-cost);
        }

        int thunderTime = 10000 + player.getRandom().nextInt(20001);
        level.setWeatherParameters(0, thunderTime, true, true);

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.WEATHER,
                5.0F,
                0.8F + player.getRandom().nextFloat() * 0.2F
        );

        player.getCooldowns().addCooldown(this, ConfigCommon.OCEAN_STONE_COOLDOWN.get());

        player.displayClientMessage(
                Component.translatable("message.enigmatic_legacy.ocean_stone.summoned")
                        .withStyle(ChatFormatting.AQUA),
                true
        );
    }

    private static int getTotalExperience(ServerPlayer player) {
        return getExperienceForLevel(player.experienceLevel)
                + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int getExperienceForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }

        if (level <= 31) {
            return Mth.floor(2.5D * level * level - 40.5D * level + 360.0D);
        }

        return Mth.floor(4.5D * level * level - 162.5D * level + 2220.0D);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            Item.@NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.active"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ocean_stone.active"));

            tooltip.add(Component.empty());

            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.passive"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ocean_stone.passive.1",
                    SpellstoneTooltip.number(ConfigCommon.OCEAN_STONE_AQUATIC_DAMAGE_RESISTANCE.get() + "%")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ocean_stone.passive.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ocean_stone.passive.3",
                    SpellstoneTooltip.number(ConfigCommon.OCEAN_STONE_SWIM_SPEED_BONUS.get() + "%")));
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.ocean_stone.passive.4"));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
