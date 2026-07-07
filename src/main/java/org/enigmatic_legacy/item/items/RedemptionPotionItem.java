package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.potion.ModEffects;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Potion of Redemption.
 *
 * <p>This is a standalone item instead of a vanilla PotionContents potion,
 * so splash and lingering variants are not generated.
 */
public class RedemptionPotionItem extends Item {
    private static final int DRINK_DURATION = 32;

    public RedemptionPotionItem() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return DRINK_DURATION;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull LivingEntity livingEntity
    ) {
        if (!level.isClientSide()) {
            removeNegativeEffects(livingEntity);

            if (livingEntity instanceof Player player) {
                ForbiddenFruit.setConsumedFruit(player, false);
                player.removeEffect(ModEffects.FORBIDDEN_FRUIT);
            }
        }

        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);

            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

            if (stack.isEmpty()) {
                return bottle;
            }

            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }

        return stack;
    }

    private static void removeNegativeEffects(LivingEntity entity) {
        if (entity.getActiveEffects().isEmpty()) {
            return;
        }

        List<Holder<MobEffect>> toRemove = new ArrayList<>();

        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (!instance.getEffect().value().isBeneficial() || instance.is(ModEffects.FORBIDDEN_FRUIT)) {
                toRemove.add(instance.getEffect());
            }
        }

        for (Holder<MobEffect> effect : toRemove) {
            entity.removeEffect(effect);
        }
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.redemption_potion.1")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.redemption_potion.2")
                .withStyle(ChatFormatting.GRAY));
    }
}
