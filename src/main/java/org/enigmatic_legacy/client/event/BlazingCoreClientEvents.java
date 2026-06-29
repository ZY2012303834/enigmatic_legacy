package org.enigmatic_legacy.client.event;

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
import org.enigmatic_legacy.util.ScorchedCharmHelper;

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

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        if (!player.isInLava()) {
            return;
        }

        /*
         * 岩浆高能见度：
         * 1. 烈焰之核拥有原本的岩浆能见度提升；
         * 2. 阳灼护符复用同一套客户端视觉逻辑；
         * 3. 两者任意一个满足即可生效。
         */
        if (!BlazingCoreHelper.hasBlazingCore(player)
                && !ScorchedCharmHelper.hasScorchedCharm(player)) {
            return;
        }

        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(96.0F);
        event.setCanceled(true);
    }

    /**
     * 岩浆雾颜色改善。
     * 功能：
     * 1. 烈焰之核拥有原本的岩浆雾颜色改善；
     * 2. 阳灼护符复用同一套客户端视觉逻辑；
     * 3. 两者任意一个满足即可生效。
     * 说明：
     * 这里只改变客户端视觉效果，不影响服务端逻辑。
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

        /*
         * 只要佩戴烈焰之核或阳灼护符，就改善岩浆雾颜色。
         */
        if (!BlazingCoreHelper.hasBlazingCore(player)
                && !ScorchedCharmHelper.hasScorchedCharm(player)) {
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