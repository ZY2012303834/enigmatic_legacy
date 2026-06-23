package org.enigmatic_legacy.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.BlazingCoreHelper;

/**
 * 烈焰之核客户端 GUI 事件。

 * 效果：
 * 佩戴烈焰之核并接触岩浆后，
 * 原版经验条会临时变成过热条。

 * 注意：
 * 这里只改变 GUI 显示，不改变玩家真实经验。
 * 创造模式不显示，也不参与这个过热 GUI 功能。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class BlazingCoreClientEvents {
    /**
     * 必须和 BlazingCoreEvents 中的热量 NBT 名称一致。
     */
    private static final String LAVA_HEAT_TAG = "enigmatic_legacy.blazing_core_lava_heat";

    /**
     * 原版经验条 sprite。

     * 使用 Minecraft 原版 HUD sprite，
     * 避免之前方块 fill 画出来的样式和原版经验条差异太大。
     */
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/experience_bar_background");

    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/experience_bar_progress");

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;

    private BlazingCoreClientEvents() {
    }

    /**
     * 烈焰之核：提升熔岩中的能见度。
     *
     * 说明：
     * 这个效果不是 GUI，也不是经验条；
     * 它通过拉远 LAVA 雾的起止距离，让玩家在岩浆里看得更远。
     */
    @SubscribeEvent
    public static void onRenderLavaFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        /*
         * 只处理镜头真的位于岩浆中的情况。
         * FogType.LAVA 是原版用于判断镜头处于岩浆雾中的类型。
         */
        if (event.getType() != FogType.LAVA) {
            return;
        }

        /*
         * 必须佩戴烈焰之核才提升岩浆能见度。
         */
        if (!BlazingCoreHelper.hasBlazingCore(player)) {
            return;
        }

        /*
         * 极大地提高熔岩中的能见度。
         *
         * near = 0.0F：雾从镜头位置开始，但不糊脸；
         * far  = 32.0F：比原版岩浆可视距离远很多。
         *
         * 如果你觉得还不够清楚，可以把 32.0F 改成 48.0F 或 64.0F。
         */
        event.setNearPlaneDistance(0.5F);
        event.setFarPlaneDistance(4.0F);
        event.setFogShape(FogShape.CYLINDER);

        /*
         * NeoForge 要求：
         * RenderFog 里修改 near/far/fogShape 后，必须 cancel 才会生效。
         */
        event.setCanceled(true);
    }

    /**
     * 烈焰之核：稍微改善熔岩雾颜色。
     * 这一步不是必须，但能让岩浆里不再那么黑红糊成一片。
     */
    @SubscribeEvent
    public static void onComputeLavaFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        if (event.getCamera().getFluidInCamera() != FogType.LAVA) {
            return;
        }

        if (!BlazingCoreHelper.hasBlazingCore(player)) {
            return;
        }

        /*
         * 保持熔岩的橙红色调，但略微提亮。
         * 数值范围是 0.0F ~ 1.0F。
         */
        event.setRed(1.0F);
        event.setGreen(0.42F);
        event.setBlue(0.12F);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.options.hideGui) {
            return;
        }

        if (!shouldReplaceExperienceBar(player)) {
            return;
        }

        /*
         * 取消原版经验条，改画烈焰之核过热条。
         */
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            event.setCanceled(true);
            renderHeatBar(event.getGuiGraphics(), minecraft, player);
            return;
        }

        /*
         * 过热条显示期间隐藏经验等级数字。
         * 否则经验等级会压在过热条上方，看起来不自然。
         */
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            event.setCanceled(true);
        }
    }

    /**
     * 是否替换经验条。

     * 重点：
     * 1. 创造模式不显示；
     * 2. 旁观模式不显示；
     * 3. 必须佩戴烈焰之核；
     * 4. 只要热量还没完全消退，就继续显示 GUI；
     * 5. 刚进入岩浆时，即使热量还是 0，也先显示空过热条。
     */
    private static boolean shouldReplaceExperienceBar(Player player) {
        if (player.isSpectator()) {
            return false;
        }

        if (player.getAbilities().instabuild) {
            return false;
        }

        int heat = player.getPersistentData().getInt(LAVA_HEAT_TAG);
        boolean hasBlazingCore = BlazingCoreHelper.hasBlazingCore(player);

        /*
         * 佩戴烈焰之核并在岩浆中时，显示热条。
         * 脱下后，只要热力值还没降到 0，也继续显示热条。
         */
        return heat > 0 || (hasBlazingCore && player.isInLava());
    }
    /**
     * 绘制过热条。

     * 坐标完全参考原版经验条：
     * x = 屏幕中心 - 91
     * y = 屏幕高度 - 29
     */
    private static void renderHeatBar(GuiGraphics guiGraphics, Minecraft minecraft, Player player) {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2 - 91;
        int y = screenHeight - 29;

        int maxHeat = Math.max(1, ConfigCommon.BLAZING_CORE_LAVA_IMMUNITY_TICKS.get());
        int heat = Mth.clamp(player.getPersistentData().getInt(LAVA_HEAT_TAG), 0, maxHeat);

        float progress = heat / (float) maxHeat;

        /*
         * 缓动曲线：
         * - 接近 0 时变化较慢；
         * - 中段变化较快；
         * - 接近满载临界点时再次变慢。
         *
         * 这样进入临界和离开临界时都不会显得突兀。
         * 数学上仍保证：
         * progress = 0 时显示 0；
         * progress = 1 时显示满载。
         */
        float visualProgress = smoothStep(progress);

        int filledWidth = Mth.floor(visualProgress * BAR_WIDTH);

        /*
         * 先绘制原版经验条背景。
         */
        guiGraphics.blitSprite(
                EXPERIENCE_BAR_BACKGROUND,
                x,
                y,
                BAR_WIDTH,
                BAR_HEIGHT
        );

        /*
         * 再绘制原版经验条进度 sprite。
         * 这里只给它轻微染成橙红色，形状仍然是原版经验条样式。
         */
        if (filledWidth > 0) {
            guiGraphics.setColor(1.0F, 0.42F, 0.05F, 1.0F);

            guiGraphics.blitSprite(
                    EXPERIENCE_BAR_PROGRESS,
                    BAR_WIDTH,
                    BAR_HEIGHT,
                    0,
                    0,
                    x,
                    y,
                    filledWidth,
                    BAR_HEIGHT
            );

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        /*
         * 满载后稍微偏红，提示已经到达临界。
         * 不加文字，所以不需要语言文件。
         */
        if (heat >= maxHeat) {
            guiGraphics.setColor(1.0F, 0.12F, 0.04F, 1.0F);

            guiGraphics.blitSprite(
                    EXPERIENCE_BAR_PROGRESS,
                    BAR_WIDTH,
                    BAR_HEIGHT,
                    0,
                    0,
                    x,
                    y,
                    BAR_WIDTH,
                    BAR_HEIGHT
            );

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * 平滑步进函数。
     * 这个函数会让进度条在 0 和 1 两端变慢，
     * 也就是你说的“到达临界缓慢上升，下降到临界也是这个效果”。
     */
    private static float smoothStep(float value) {
        value = Mth.clamp(value, 0.0F, 1.0F);
        return value * value * (3.0F - 2.0F * value);
    }
}