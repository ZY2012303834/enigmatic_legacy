package org.enigmatic_legacy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Pure Resistance / 纯化抗性.
 *
 * <p>The damage reduction itself is handled in EarthPromiseEvents so it can
 * cancel or scale the final damage after the normal damage pipeline.
 */
public class PureResistanceEffect extends MobEffect {
    public PureResistanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBF4B);
    }
}
