package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.potion.ModEffects;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Forbidden Fruit / 禁忌之果。
 *
 * <p>复刻原版核心效果：
 * <ul>
 *     <li>每名玩家只能食用一次；</li>
 *     <li>食用后永久不再饥饿，服务端会持续锁定饱食度并清除饥饿效果；</li>
 *     <li>代价是自然回血等小额治疗会按配置被削弱；</li>
 *     <li>食用瞬间施加原版同款负面效果。</li>
 * </ul>
 */
public class ForbiddenFruit extends Item {

    public static final String CONSUMED_FRUIT_TAG = "ConsumedForbiddenFruit";

    public ForbiddenFruit() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    public static boolean hasConsumedFruit(Player player) {
        return player.getPersistentData().getBoolean(CONSUMED_FRUIT_TAG);
    }

    public static void setConsumedFruit(Player player, boolean consumed) {
        player.getPersistentData().putBoolean(CONSUMED_FRUIT_TAG, consumed);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hasConsumedFruit(player)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack,
                                              @NotNull Level level,
                                              @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }

        setConsumedFruit(player, true);

        if (!level.isClientSide()) {
            applySyncMarker(player);
            applyConsumptionPenalty(player);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }
        }

        entity.gameEvent(GameEvent.EAT);
        return stack;
    }

    private static void applyConsumptionPenalty(Player player) {
        double durationMultiplier = ConfigCommon.FORBIDDEN_FRUIT_DEBUFF_DURATION_MULTIPLIER.get();

        player.addEffect(new MobEffectInstance(MobEffects.WITHER, scaledDuration(300, durationMultiplier), 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, scaledDuration(300, durationMultiplier), 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, scaledDuration(400, durationMultiplier), 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, scaledDuration(500, durationMultiplier), 2, false, true));
    }

    public static void applySyncMarker(Player player) {
        if (!player.hasEffect(ModEffects.FORBIDDEN_FRUIT)) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.FORBIDDEN_FRUIT,
                    MobEffectInstance.INFINITE_DURATION,
                    0,
                    false,
                    false,
                    false
            ));
        }
    }

    private static int scaledDuration(int duration, double multiplier) {
        return Math.max(1, (int) Math.round(duration * multiplier));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.forbidden_fruit_lore")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            return;
        }

        String subtraction = ConfigCommon.FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION.get() + "%";
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.forbidden_fruit1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.forbidden_fruit2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.forbidden_fruit3", subtraction)
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
