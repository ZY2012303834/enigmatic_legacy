package org.enigmatic_legacy.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.enigmatic_legacy.util.GolemHeartHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 魔像之心专用中毒修正。
 *
 * 原版中毒在低血量时不会继续造成伤害，因此无法杀死玩家。
 * 魔像之心要求“魔法伤害易伤”能让毒伤继续结算并变得致命，
 * 所以佩戴魔像之心时直接替换 PoisonMobEffect 的 tick 逻辑。
 */
@Mixin(targets = "net.minecraft.world.effect.PoisonMobEffect")
public abstract class MixinPoisonMobEffect {

    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    private void enigmatic_legacy$golemHeartPoisonCanKill(
            LivingEntity entity,
            int amplifier,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!GolemHeartHelper.hasGolemHeart(entity)) {
            return;
        }

        // 使用 magic 伤害源，随后 GolemHeartEvents 会按魔法易伤倍率放大。
        entity.hurt(entity.damageSources().magic(), 1.0F);
        cir.setReturnValue(true);
    }
}