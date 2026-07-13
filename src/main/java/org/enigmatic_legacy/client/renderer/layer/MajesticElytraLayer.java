package org.enigmatic_legacy.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;

/**
 * 壮丽鞘翅的客户端鞘翅渲染层。
 *
 * <p>原版 {@link net.minecraft.client.renderer.entity.layers.ElytraLayer} 只从胸甲栏读取
 * {@code minecraft:elytra}。壮丽鞘翅既是自定义物品，又允许放在 Curios back 背饰栏，
 * 因此背饰栏飞行功能虽然已经由 mixin 接入，客户端外观仍需要单独渲染。</p>
 *
 * <p>原项目没有提供壮丽鞘翅专用的 64x32 装备态贴图；本层先使用原版鞘翅贴图，
 * 解决“背部完全不显示”的问题。如果之后补到专用贴图，只需要替换 {@link #TEXTURE}。</p>
 */
public class MajesticElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");

    private final ElytraModel<T> elytraModel;

    public MajesticElytraLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack stack = CustomElytraRenderHelper.getRenderableCustomElytra(livingEntity);

        if (!stack.is(ModItems.MAJESTIC_ELYTRA.get())) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        this.getParentModel().copyPropertiesTo(this.elytraModel);
        this.elytraModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                buffer,
                RenderType.armorCutoutNoCull(TEXTURE),
                stack.hasFoil()
        );
        this.elytraModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

}
