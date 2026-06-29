package org.enigmatic_legacy.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import org.enigmatic_legacy.util.ScorchedCharmHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void enigmaticLegacy$scorchedCharmCanStandOnLava(
            FluidState fluidState,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (callback.getReturnValue()) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isCrouching()) {
            return;
        }

        if (fluidState.is(FluidTags.LAVA) && ScorchedCharmHelper.hasScorchedCharm(entity)) {
            callback.setReturnValue(true);
        }
    }
}
