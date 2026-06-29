package org.enigmatic_legacy.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.enigmatic_legacy.item.items.charm.ScorchedCharm;
import org.enigmatic_legacy.util.BlazingCoreHelper;
import org.enigmatic_legacy.util.ScorchedCharmHelper;
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

        RenderSystem.setShaderFogStart(0.0F);
        RenderSystem.setShaderFogEnd(256.0F);
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

        RenderSystem.clearColor(1.0F, 0.58F, 0.22F, 0.0F);
    }

    @Inject(method = "levelFogColor", at = @At("RETURN"))
    private static void enigmaticLegacy$applyBrightLavaFogColor(CallbackInfo callback) {
        if (!enigmaticLegacy$lavaVisionActive) {
            return;
        }

        RenderSystem.setShaderFogColor(1.0F, 0.58F, 0.22F);
    }

    @Unique
    private static boolean enigmaticLegacy$hasLavaVision(Camera camera) {
        Entity entity = camera.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) {
            enigmaticLegacy$lavaVisionActive = false;
            return false;
        }

        boolean hasVisionSource = BlazingCoreHelper.hasBlazingCore(livingEntity)
                || ScorchedCharmHelper.hasScorchedCharm(livingEntity)
                || livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)
                || livingEntity.getPersistentData().getInt(ScorchedCharm.CLIENT_TICK_TAG) >= livingEntity.tickCount - 2;

        if (!hasVisionSource) {
            enigmaticLegacy$lavaVisionActive = false;
            return false;
        }

        boolean inLavaView = camera.getFluidInCamera() == FogType.LAVA
                || livingEntity.isInLava()
                || livingEntity.level().getFluidState(camera.getBlockPosition()).is(FluidTags.LAVA)
                || livingEntity.level().getFluidState(livingEntity.blockPosition()).is(FluidTags.LAVA);

        enigmaticLegacy$lavaVisionActive = inLavaView;
        return inLavaView;
    }
}
