package org.enigmatic_legacy.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.enigmatic_legacy.client.util.ClientLavaVisionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {

    @Unique
    private static boolean enigmaticLegacy$lavaVisionActive;
    @Unique
    private static Object enigmaticLegacy$irisCapturedState;
    @Unique
    private static java.lang.reflect.Method enigmaticLegacy$irisSetFogColor;
    @Unique
    private static java.lang.reflect.Method enigmaticLegacy$irisSetFogDensity;
    @Unique
    private static boolean enigmaticLegacy$irisLookupAttempted;

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
        if (!enigmaticLegacy$resolveIrisFogHooks()) {
            return;
        }

        try {
            enigmaticLegacy$irisSetFogColor.invoke(enigmaticLegacy$irisCapturedState, red, green, blue);
            enigmaticLegacy$irisSetFogDensity.invoke(enigmaticLegacy$irisCapturedState, density);
        } catch (ReflectiveOperationException ignored) {
            enigmaticLegacy$irisCapturedState = null;
            enigmaticLegacy$irisSetFogColor = null;
            enigmaticLegacy$irisSetFogDensity = null;
        }
    }

    @Unique
    private static boolean enigmaticLegacy$resolveIrisFogHooks() {
        if (enigmaticLegacy$irisCapturedState != null
                && enigmaticLegacy$irisSetFogColor != null
                && enigmaticLegacy$irisSetFogDensity != null) {
            return true;
        }

        if (enigmaticLegacy$irisLookupAttempted) {
            return false;
        }

        enigmaticLegacy$irisLookupAttempted = true;

        try {
            Class<?> capturedStateClass = Class.forName("net.irisshaders.iris.uniforms.CapturedRenderingState");
            enigmaticLegacy$irisCapturedState = capturedStateClass.getField("INSTANCE").get(null);
            enigmaticLegacy$irisSetFogColor = capturedStateClass.getMethod("setFogColor", float.class, float.class, float.class);
            enigmaticLegacy$irisSetFogDensity = capturedStateClass.getMethod("setFogDensity", float.class);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Unique
    private static boolean enigmaticLegacy$hasLavaVision(Camera camera) {
        enigmaticLegacy$lavaVisionActive = ClientLavaVisionHelper.hasLavaVision(camera);
        return enigmaticLegacy$lavaVisionActive;
    }
}
