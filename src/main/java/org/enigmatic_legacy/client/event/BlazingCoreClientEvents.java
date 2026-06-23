package org.enigmatic_legacy.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.BlazingCoreHelper;

/**
 * 烈焰核心客户端 GUI 事件。

 * 效果：
 * 玩家佩戴烈焰核心进入岩浆时，
 * 原版经验条位置会临时显示为“过热条”。

 * 注意：
 * 这里只改变 GUI 显示，不修改玩家真实经验。
 * 真实岩浆免疫/过热受伤逻辑仍由 BlazingCoreEvents 处理。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class BlazingCoreClientEvents {
    /**
     * 必须和 BlazingCoreEvents 里的热量 NBT 名称一致。

     * 这里直接复制字符串，避免因为 BlazingCoreEvents 里的字段是 private 导致无法访问。
     */
    private static final String LAVA_HEAT_TAG = "enigmatic_legacy.blazing_core_lava_heat";

    /**
     * 原版经验条宽度。
     */
    private static final int BAR_WIDTH = 182;

    /**
     * 原版经验条高度。
     */
    private static final int BAR_HEIGHT = 5;

    private BlazingCoreClientEvents() {
    }

    /**
     * 在原版 GUI 层绘制前拦截经验条。
     */
    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        if (minecraft.options.hideGui) {
            return;
        }

        /*
         * 只有佩戴烈焰核心并进入岩浆时，才替换经验条。
         */
        if (!shouldRenderHeatBar(player)) {
            return;
        }

        /*
         * 取消原版经验条，改画烈焰核心过热条。
         */
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            event.setCanceled(true);
            renderHeatBar(event.getGuiGraphics(), minecraft, player);
            return;
        }

        /*
         * 取消经验等级数字，避免经验等级文字盖在过热条上。
         */
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            event.setCanceled(true);
        }
    }

    /**
     * 判断是否显示过热条。
     */
    private static boolean shouldRenderHeatBar(Player player) {
        if (player.isSpectator()) {
            return false;
        }

        if (!player.isInLava()) {
            return false;
        }

        return BlazingCoreHelper.hasBlazingCore(player);
    }

    /**
     * 绘制过热条。

     * 坐标参考原版经验条：
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
        int filledWidth = Mth.floor(progress * BAR_WIDTH);

        /*
         * 背景槽：暗红色。
         */
        guiGraphics.fill(
                x,
                y,
                x + BAR_WIDTH,
                y + BAR_HEIGHT,
                0xAA2B1200
        );

        /*
         * 边框。
         */
        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y, 0xFF5A1B00);
        guiGraphics.fill(x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF5A1B00);
        guiGraphics.fill(x - 1, y, x, y + BAR_HEIGHT, 0xFF5A1B00);
        guiGraphics.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, 0xFF5A1B00);

        /*
         * 前景进度：橙色热量条。
         */
        if (filledWidth > 0) {
            guiGraphics.fill(
                    x,
                    y,
                    x + filledWidth,
                    y + BAR_HEIGHT,
                    0xFFFF6A00
            );

            /*
             * 顶部高亮线。
             */
            guiGraphics.fill(
                    x,
                    y,
                    x + filledWidth,
                    y + 1,
                    0xFFFFD36A
            );
        }

        /*
         * 满载后用红色覆盖整条，表示已经过热。
         * 不显示文字，因此无需语言文件。
         */
        if (heat >= maxHeat) {
            guiGraphics.fill(
                    x,
                    y,
                    x + BAR_WIDTH,
                    y + BAR_HEIGHT,
                    0xFFFF2222
            );

            guiGraphics.fill(
                    x,
                    y,
                    x + BAR_WIDTH,
                    y + 1,
                    0xFFFF8888
            );
        }
    }
}