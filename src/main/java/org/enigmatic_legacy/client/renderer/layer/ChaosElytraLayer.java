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
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

/**
 * 混沌之傲的客户端鞘翅渲染层。
 *
 * <p>物品栏图标只负责背包中的 2D 动态贴图；玩家身上的展开鞘翅需要额外挂载
 * ElytraModel 渲染层，并指定 {@code textures/item/3d/chaos_elytra.png}。
 * 原版 {@link net.minecraft.client.renderer.entity.layers.ElytraLayer} 只识别
 * {@code minecraft:elytra}，不会自动渲染本模组的自定义鞘翅，因此这里单独补上。</p>
 */
public class ChaosElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "textures/item/3d/chaos_elytra.png"
    );

    private final ElytraModel<T> elytraModel;

    public ChaosElytraLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
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

        if (!stack.is(ModItems.CHAOS_ELYTRA.get())) {
            return;
        }

        /*
         * 与原版 ElytraLayer 保持相同位移和动画同步方式：
         * 父模型复制当前姿势后再让 ElytraModel 计算翅膀角度，保证站立、潜行和滑翔时都能对齐玩家身体。
         */
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
