package org.enigmatic_legacy.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Growing Hunger / 增长饥饿。
 * 原项目机制：
 * - 玩家主手持有饕餮之锅时逐渐增强；
 * - 每 300 tick 提升 1 级；
 * - 最高 9 级；
 * - 每 4 tick 造成饥饿消耗；
 * - 每级还会增加攻击伤害。
 * 攻击伤害加成不写在这里，
 * 而是在 VoraciousPanEvents 的伤害事件中处理，
 * 这样可以避免 1.21.1 属性组件兼容问题。
 */
public class GrowingHungerEffect extends MobEffect {

    /**
     * 原项目默认：
     * DamageBoost = 0.1
     * 这里表示每级 +10% 伤害。
     */
    public static final float DAMAGE_BOOST_PER_LEVEL = 0.10F;

    /**
     * 原项目默认：
     * ExhaustionGain = 0.5
     * 每 4 tick 每级增加 0.5 exhaustion。
     */
    public static final float EXHAUSTION_GAIN_PER_LEVEL = 0.5F;

    /**
     * 原项目默认：
     * TicksPerLevel = 300
     */
    public static final int TICKS_PER_LEVEL = 300;

    public GrowingHungerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xBD1BE5);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            player.causeFoodExhaustion(EXHAUSTION_GAIN_PER_LEVEL * (1 + amplifier));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 4 == 0;
    }
}