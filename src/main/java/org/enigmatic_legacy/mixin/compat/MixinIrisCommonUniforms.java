package org.enigmatic_legacy.mixin.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.util.ClientLavaVisionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.irisshaders.iris.uniforms.CommonUniforms", remap = false)
public abstract class MixinIrisCommonUniforms {

    @Unique
    private static boolean enigmaticLegacy$loggedIrisLavaVisionOverride;

    @Inject(method = "isEyeInWater", at = @At("RETURN"), cancellable = true, remap = false)
    private static void enigmaticLegacy$hideLavaFromShaderpacks(CallbackInfoReturnable<Integer> callback) {
        if (callback.getReturnValueI() != 2) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.gameRenderer == null || minecraft.gameRenderer.getMainCamera().getFluidInCamera() != FogType.LAVA) {
            return;
        }

        if (ClientLavaVisionHelper.hasLavaVision(minecraft.gameRenderer.getMainCamera())) {
            if (!enigmaticLegacy$loggedIrisLavaVisionOverride) {
                EnigmaticLegacy.LOGGER.info("Overriding Iris isEyeInWater lava value for Enigmatic Legacy lava vision shader compatibility.");
                enigmaticLegacy$loggedIrisLavaVisionOverride = true;
            }

            callback.setReturnValue(0);
        }
    }
}
