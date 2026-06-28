package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstralFruit extends Item {
    public static final String CONSUMED_FRUIT_TAG = "ConsumedAstralFruit";

    public AstralFruit() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
                .food(new FoodProperties.Builder()
                        .nutrition(5)
                        .saturationModifier(20.0F)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!CursedRingHelper.hasCursedRing(player)) {
            return InteractionResultHolder.fail(stack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull LivingEntity entity
    ) {
        if (entity instanceof Player player && !level.isClientSide()) {
            player.getPersistentData().putBoolean(CONSUMED_FRUIT_TAG, true);

            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3000, 3, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3000, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4000, 3, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5000, 0, false, true));
        }

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.1")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.2")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.3")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
