package org.enigmatic_legacy.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.item.items.spellstone.EtheriumCore;
import org.enigmatic_legacy.util.EtheriumCoreHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$hideArmorWithEtheriumCore(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            LivingEntity livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo callback
    ) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        boolean hidden = EtheriumCoreHelper.findEtheriumCore(player)
                .map(EtheriumCore::isArmorHidden)
                .orElse(false);

        if (hidden) {
            callback.cancel();
        }
    }
}
