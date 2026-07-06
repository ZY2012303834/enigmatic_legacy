package org.enigmatic_legacy.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.enigmatic_legacy.client.util.ClientLavaVisionHelper;
import org.enigmatic_legacy.compat.IrisCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {

    @Unique
    private static boolean enigmaticLegacy$lavaVisionActive;

    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void enigmaticLegacy$extendLavaFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float renderDistance,
            boolean thickFog,
            float partialTick,
            CallbackInfo callback
    ) {
        if (!enigmaticLegacy$hasLavaVision(camera)) {
            return;
        }

        RenderSystem.setShaderFogStart(ClientLavaVisionHelper.LAVA_FOG_START);
        RenderSystem.setShaderFogEnd(ClientLavaVisionHelper.LAVA_FOG_END);
        enigmaticLegacy$setConfiguredIrisFog();
    }

    @Inject(method = "setupColor", at = @At("RETURN"))
    private static void enigmaticLegacy$brightenLavaFog(
            Camera camera,
            float partialTick,
            ClientLevel level,
            int renderDistance,
            float darkenWorldAmount,
            CallbackInfo callback
    ) {
        if (!enigmaticLegacy$hasLavaVision(camera)) {
            return;
        }

        RenderSystem.clearColor(
                ClientLavaVisionHelper.LAVA_FOG_RED,
                ClientLavaVisionHelper.LAVA_FOG_GREEN,
                ClientLavaVisionHelper.LAVA_FOG_BLUE,
                ClientLavaVisionHelper.LAVA_CLEAR_ALPHA
        );
        enigmaticLegacy$setConfiguredIrisFog();
    }

    @Inject(method = "levelFogColor", at = @At("RETURN"))
    private static void enigmaticLegacy$applyBrightLavaFogColor(CallbackInfo callback) {
        if (!enigmaticLegacy$lavaVisionActive) {
            return;
        }

        RenderSystem.setShaderFogColor(
                ClientLavaVisionHelper.LAVA_FOG_RED,
                ClientLavaVisionHelper.LAVA_FOG_GREEN,
                ClientLavaVisionHelper.LAVA_FOG_BLUE
        );
        enigmaticLegacy$setConfiguredIrisFog();
    }

    @Unique
    private static void enigmaticLegacy$setConfiguredIrisFog() {
        enigmaticLegacy$setIrisFog(
                ClientLavaVisionHelper.LAVA_FOG_RED,
                ClientLavaVisionHelper.LAVA_FOG_GREEN,
                ClientLavaVisionHelper.LAVA_FOG_BLUE,
                ClientLavaVisionHelper.IRIS_LAVA_FOG_DENSITY
        );
    }

    @Unique
    private static void enigmaticLegacy$setIrisFog(float red, float green, float blue, float density) {
        IrisCompat.setFog(red, green, blue, density);
    }

    @Unique
    private static boolean enigmaticLegacy$hasLavaVision(Camera camera) {
        enigmaticLegacy$lavaVisionActive = ClientLavaVisionHelper.hasLavaVision(camera);
        return enigmaticLegacy$lavaVisionActive;
    }
}
