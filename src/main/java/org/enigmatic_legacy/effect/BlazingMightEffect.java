package org.enigmatic_legacy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Blazing Might / 烈焰巨力。
 *
 * <p>实际攻击增伤在 BulwarkOfBlazingPrideEvents 中处理，避免和原版力量的属性
 * UUID 冲突，也方便在受到真实伤害后精确移除。
 */
public class BlazingMightEffect extends MobEffect {

    public static final float DAMAGE_BOOST_PER_LEVEL = 0.25F;

    public BlazingMightEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6A00);
    }
}
