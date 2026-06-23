package org.enigmatic_legacy.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 创造之心客户端 HUD 保护特效。

 * 服务端不朽触发后，客户端会在血量条位置绘制
 * Plus 项目里的 ethereal_shield 遮罩。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class HeartOfCreationClientEffects {
    private static final ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "heart_of_creation_guard"
    );

    /**
     * 注意：
     * 这里写的是 GUI sprite ID，不是完整 textures 路径。

     * 对应文件：
     * assets/enigmatic_legacy/textures/gui/sprites/hud/ethereal_shield.png
     */
    private static final ResourceLocation ETHEREAL_SHIELD = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "hud/ethereal_shield"
    );

    /**
     * 对应文件：
     * assets/enigmatic_legacy/textures/gui/sprites/hud/ethereal_shield_highlight.png
     */
    private static final ResourceLocation ETHEREAL_SHIELD_HIGHLIGHT = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "hud/ethereal_shield_highlight"
    );

    /**
     * 特效剩余时间。
     * 20 tick 大约 1 秒。
     */
    private static int guardFlashTicks = 0;

    private HeartOfCreationClientEffects() {
    }

    /**
     * 网络包收到后调用这个方法。
     */
    public static void triggerGuardFlash() {
        guardFlashTicks = 20;
    }

    /**
     * 注册 HUD 渲染层。
     */
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, HeartOfCreationClientEffects::renderGuardOverlay);
    }

    /**
     * 渲染血量条保护遮罩。
     */
    private static void renderGuardOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        if (guardFlashTicks <= 0) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        /*
         * 原版血量条大致位置：
         * X = 屏幕中心 - 91
         * Y = 屏幕底部 - 39
         */
        int left = screenWidth / 2 - 91;
        int top = screenHeight - 39;

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float progress = Mth.clamp((guardFlashTicks - partialTick) / 20.0F, 0.0F, 1.0F);

        /*
         * 白色半透明效果。
         * alpha 越高越明显。
         */
        float alpha = 0.35F * progress;

        int hearts = Mth.ceil(minecraft.player.getMaxHealth() / 2.0F);

        /*
         * 最多覆盖 20 个心，也就是两行。
         * 避免高血量模组导致遮罩铺满屏幕。
         */
        hearts = Math.min(hearts, 20);

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);

        for (int i = 0; i < hearts; i++) {
            int x = left + (i % 10) * 8;
            int y = top - (i / 10) * 10;

            /*
             * 基础护盾遮罩。
             */
            guiGraphics.blitSprite(
                    ETHEREAL_SHIELD,
                    x,
                    y,
                    9,
                    9
            );

            /*
             * 高亮遮罩。
             * 每隔几 tick 闪一次，更接近保护触发效果。
             */
            if (guardFlashTicks % 6 < 3) {
                guiGraphics.blitSprite(
                        ETHEREAL_SHIELD_HIGHLIGHT,
                        x,
                        y,
                        9,
                        9
                );
            }
        }

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        guardFlashTicks--;
    }
}