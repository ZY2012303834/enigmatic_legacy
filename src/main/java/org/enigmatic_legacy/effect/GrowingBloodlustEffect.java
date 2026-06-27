package org.enigmatic_legacy.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Growing Bloodlust / 增长嗜血。
 * 原项目机制：
 * - 如果玩家不会饥饿，例如吃过禁忌之果；
 * - 持有饕餮之锅时不再获得 Growing Hunger；
 * - 改为获得 Growing Bloodlust；
 * - 它增加伤害和吸血；
 * - 但会周期性扣血，最低保留 30% 最大生命。
 */
public class GrowingBloodlustEffect extends MobEffect {

    /**
     * 原项目默认：
     * DamageBoost = 0.05
     * 每级 +5% 伤害。
     */
    public static final float DAMAGE_BOOST_PER_LEVEL = 0.05F;

    /**
     * 原项目默认：
     * LifestealBoost = 0.025
     * 每级 +2.5% 吸血。
     */
    public static final float LIFESTEAL_BOOST_PER_LEVEL = 0.025F;

    /**
     * 原项目默认：
     * HealthLossTicks = 160
     * 1 级时每 160 tick 扣 1 点生命，
     * 等级越高扣血越快。
     */
    public static final int HEALTH_LOSS_TICKS = 160;

    /**
     * 原项目默认：
     * HealthLossLimit = 0.3
     * 最多扣到 30% 最大生命。
     */
    public static final float HEALTH_LOSS_LIMIT = 0.30F;

    /**
     * 原项目默认：
     * TicksPerLevel = 300
     */
    public static final int TICKS_PER_LEVEL = 300;

    public GrowingBloodlustEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xC30018);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (player.isCreative() || player.isSpectator()) {
            return true;
        }

        float currentFraction = player.getHealth() / player.getMaxHealth();

        if (currentFraction > HEALTH_LOSS_LIMIT) {
            player.setHealth(Math.max(
                    player.getMaxHealth() * HEALTH_LOSS_LIMIT,
                    player.getHealth() - 1.0F
            ));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int period = Math.max(1, HEALTH_LOSS_TICKS / (1 + amplifier));
        return duration % period == 0;
    }
}