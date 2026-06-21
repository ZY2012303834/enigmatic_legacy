package org.enigmatic_legacy.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.enigmatic_legacy.entity.PermanentItemEntity;

/**
 * 用原物品模型渲染 PermanentItemEntity。
 */
public class PermanentItemRenderer extends EntityRenderer<PermanentItemEntity> {

    private final ItemRenderer itemRenderer;

    public PermanentItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
    }

    @Override
    public void render(PermanentItemEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.18D + Math.sin((entity.getAge() + partialTick) / 10.0D) * 0.08D, 0.0D);
        poseStack.mulPose(Axis.YP.rotation((entity.getAge() + partialTick) / 20.0F + entity.hoverStart));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PermanentItemEntity entity) {
        return null;
    }
}
