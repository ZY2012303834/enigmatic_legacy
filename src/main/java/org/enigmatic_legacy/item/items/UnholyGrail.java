package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
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
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 不洁圣杯 / Unholy Grail。
 *
 * <p>原项目效果：
 * <ul>
 *     <li>饮用后不会消耗圣杯；</li>
 *     <li>不合格者饮用会获得多种负面效果；</li>
 *     <li>合格者饮用会获得强力增益；</li>
 *     <li>饮用动作持续 32 tick；</li>
 *     <li>使用蜂蜜饮用音效。</li>
 * </ul>
 *
 * <p>注意：
 * 原项目的合格条件是“七咒者 + 已食用禁果”。
 * 当前项目还没有禁果系统，所以暂时只用“佩戴七咒之戒”判断。
 */
public class UnholyGrail extends Item{

    public UnholyGrail() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
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

        if (!level.isClientSide()) {
            boolean worthy = isWorthyOne(player);

            if (worthy) {
                applyWorthyEffects(player);
            } else {
                applyUnworthyEffects(player);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }
        }

        return stack;
    }

    /**
     * 判断玩家是否有资格承受不洁圣杯的力量。
     *
     * <p>临时逻辑：佩戴七咒之戒即可。
     *
     * <p>后续复刻禁果后，应改成：
     * <pre>
     * CursedRingHelper.hasCursedRing(player) && ForbiddenFruitHelper.hasConsumedFruit(player)
     * </pre>
     */
    private static boolean isWorthyOne(Player player) {
        return CursedRingHelper.hasCursedRing(player);
    }

    /**
     * 不合格者饮用后的惩罚效果。
     *
     * <p>复刻原项目数值：
     * <ul>
     *     <li>凋零 III，5 秒；</li>
     *     <li>中毒 II，8 秒；</li>
     *     <li>反胃 I，12 秒；</li>
     *     <li>虚弱 II，10 秒；</li>
     *     <li>饥饿 III，8 秒；</li>
     *     <li>缓慢 I，12 秒。</li>
     * </ul>
     */
    private static void applyUnworthyEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 240, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 240, 0, false, true));
    }

    /**
     * 合格者饮用后的增益效果。
     *
     * <p>复刻原项目数值：
     * <ul>
     *     <li>生命恢复 III，25 秒；</li>
     *     <li>伤害吸收 II，40 秒；</li>
     *     <li>抗性提升 II，60 秒；</li>
     *     <li>力量 II，50 秒；</li>
     *     <li>速度 I，60 秒。</li>
     * </ul>
     */
    private static void applyWorthyEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 800, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0, false, true));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unholy_grail1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unholy_grail2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}