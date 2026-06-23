package org.enigmatic_legacy.client;

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
 * 创造之心客户端 GUI 特效。

 * 作用：
 * 服务端不朽触发后，客户端在血量条位置绘制一层金白色保护闪烁。

 * 注意：
 * 这个类必须只在客户端加载，所以使用 Dist.CLIENT。
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
     * 特效剩余 tick。
     * 客户端收到包后设置为 20，大约显示 1 秒。
     */
    private static int guardFlashTicks = 0;

    private HeartOfCreationClientEffects() {
    }

    /**
     * 服务端包调用这个方法，触发血条保护特效。
     */
    public static void triggerGuardFlash() {
        guardFlashTicks = 20;
    }

    /**
     * 注册 HUD 渲染层。

     * registerAboveAll：画在 HUD 最上层，避免被原版血条覆盖。
     */
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, HeartOfCreationClientEffects::renderGuardOverlay);
    }

    /**
     * 渲染创造之心保护特效。
     */
    private static void renderGuardOverlay(GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker deltaTracker) {
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
         * 左侧起点 = 屏幕中心 - 91
         * Y = 屏幕底部 - 39
         *
         * 这和原版 HUD 血条位置基本一致。
         */
        int left = screenWidth / 2 - 91;
        int top = screenHeight - 39;

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float progress = (guardFlashTicks - partialTick) / 20.0F;
        progress = Mth.clamp(progress, 0.0F, 1.0F);

        /*
         * 闪烁强度。
         * 通过 sin 让颜色忽明忽暗，更接近“保护触发”的感觉。
         */
        float pulse = 0.5F + 0.5F * Mth.sin((20 - guardFlashTicks + partialTick) * 0.8F);
        int alpha = (int) (90 + 120 * progress * pulse);

        int gold = (alpha << 24) | 0xFFD966;
        int white = ((int) (alpha * 0.75F) << 24) | 0xFFFFFF;

        /*
         * 按当前玩家最大生命值绘制覆盖层。
         * 最多画 2 行，避免高血量时覆盖太多 HUD。
         */
        int hearts = Mth.ceil(minecraft.player.getMaxHealth() / 2.0F);
        hearts = Math.min(hearts, 20);

        for (int i = 0; i < hearts; i++) {
            int x = left + (i % 10) * 8;
            int y = top - (i / 10) * 10;

            /*
             * 画一个外层金色方框 + 内层白色闪光。
             * 不依赖额外贴图，所以不会缺资源。
             */
            guiGraphics.fill(x - 1, y - 1, x + 10, y + 10, gold);
            guiGraphics.fill(x + 1, y + 1, x + 8, y + 8, white);
        }

        guardFlashTicks--;
    }
}