package org.enigmatic_legacy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 禁忌之果的隐藏同步标记。
 *
 * <p>实际玩法状态仍保存在玩家 persistent data 中；这个效果只负责随原版效果同步
 * 到客户端，让重登后的饱食度 HUD 能立即知道玩家已食用禁忌之果。
 */
public class ForbiddenFruitEffect extends MobEffect {

    public ForbiddenFruitEffect() {
        super(MobEffectCategory.NEUTRAL, 0x6E1E78);
    }
}
