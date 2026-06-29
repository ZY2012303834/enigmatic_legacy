package org.enigmatic_legacy.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.util.ClientLavaVisionHelper;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.charm.ScorchedCharm;
import org.enigmatic_legacy.item.items.spellstone.BlazingCore;
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
     * 客户端视觉热量。
     * 注意：
     * 服务端 PersistentData 不会稳定同步到客户端，
     * 所以 GUI 不能直接读取服务端的 LAVA_HEAT_TAG。
     * 这个值只用于客户端绘制过热条，
     * 不参与真实伤害判定。
     */
    private static int clientVisualLavaHeat = 0;

    /**
     * 防止一帧多次渲染导致热量一 tick 增加多次。
     */
    private static int lastClientVisualTick = -1;

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
    private static boolean hasLocalLavaVision;

    private BlazingCoreClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && ScorchedCharmHelper.hasScorchedCharm(player)) {
            player.getPersistentData().putInt(ScorchedCharm.CLIENT_TICK_TAG, player.tickCount);
        }

        if (player != null && BlazingCoreHelper.hasBlazingCore(player)) {
            player.getPersistentData().putInt(BlazingCore.CLIENT_TICK_TAG, player.tickCount);
        }

        hasLocalLavaVision = player != null && hasLavaVision(player);

        if (hasLocalLavaVision && player.isOnFire()) {
            player.clearFire();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        if (event.getCamera().getFluidInCamera() != FogType.LAVA) {
            return;
        }

        /*
         * 岩浆高能见度：
         * 1. 烈焰之核拥有原本的岩浆能见度提升；
         * 2. 阳灼护符复用同一套客户端视觉逻辑；
         * 3. 两者任意一个满足即可生效。
         */
        if (!hasLocalLavaVision && !hasLavaVision(player)) {
            return;
        }

        RenderSystem.setShaderFogStart(ClientLavaVisionHelper.LAVA_FOG_START);
        RenderSystem.setShaderFogEnd(ClientLavaVisionHelper.LAVA_FOG_END);
        event.setNearPlaneDistance(ClientLavaVisionHelper.LAVA_FOG_START);
        event.setFarPlaneDistance(ClientLavaVisionHelper.LAVA_FOG_END);
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
    @SubscribeEvent(priority = EventPriority.LOWEST)
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
        if (!hasLocalLavaVision && !hasLavaVision(player)) {
            return;
        }

        /*
         * 保持熔岩的橙红色调，但略微提亮。
         * 数值范围是 0.0F ~ 1.0F。
         */
        event.setRed(ClientLavaVisionHelper.LAVA_FOG_RED);
        event.setGreen(ClientLavaVisionHelper.LAVA_FOG_GREEN);
        event.setBlue(ClientLavaVisionHelper.LAVA_FOG_BLUE);
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen != null) {
            return;
        }

        renderShaderLavaOverlay(event.getGuiGraphics(), minecraft);
    }

    @SubscribeEvent
    public static void onRenderScreenPre(ScreenEvent.Render.Pre event) {
        renderShaderLavaOverlay(event.getGuiGraphics(), Minecraft.getInstance());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderBlockScreenEffect(RenderBlockScreenEffectEvent event) {
        if (!(event.getPlayer() instanceof LocalPlayer player)) {
            return;
        }

        if (!hasLavaVision(player)) {
            return;
        }

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE
                || event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.BLOCK
                && event.getBlockState().getFluidState().is(FluidTags.LAVA)) {
            event.setCanceled(true);
        }
    }

    private static boolean hasLavaVision(LocalPlayer player) {
        return ClientLavaVisionHelper.hasLavaVisionSource(player);
    }

    private static int lavaOverlayColor() {
        int alpha = Mth.clamp(Math.round(ClientLavaVisionHelper.SHADER_LAVA_OVERLAY_ALPHA * 255.0F), 0, 255);
        int red = Mth.clamp(Math.round(ClientLavaVisionHelper.LAVA_FOG_RED * 255.0F), 0, 255);
        int green = Mth.clamp(Math.round(ClientLavaVisionHelper.LAVA_FOG_GREEN * 255.0F), 0, 255);
        int blue = Mth.clamp(Math.round(ClientLavaVisionHelper.LAVA_FOG_BLUE * 255.0F), 0, 255);

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static void renderShaderLavaOverlay(GuiGraphics guiGraphics, Minecraft minecraft) {
        if (minecraft.gameRenderer == null) {
            return;
        }

        if (!ClientLavaVisionHelper.isIrisShaderPackInUse()) {
            return;
        }

        if (!ClientLavaVisionHelper.hasLavaVision(minecraft.gameRenderer.getMainCamera())) {
            return;
        }

        int color = lavaOverlayColor();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        guiGraphics.fill(0, 0, width, height, color);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            clientVisualLavaHeat = 0;
            lastClientVisualTick = -1;
            return;
        }

        if (minecraft.options.hideGui) {
            return;
        }

        /*
         * 每 tick 更新一次客户端视觉热量。
         * 这只影响 GUI，不影响真实岩浆伤害。
         */
        updateClientVisualLavaHeat(player);

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

     /**
     * 是否替换经验条。
     * 重点：
     * 1. 创造模式不显示；
     * 2. 旁观模式不显示；
     * 3. GUI 使用客户端视觉热量，不再读取服务端 PersistentData；
     * 4. 这样岩浆游泳、岩浆表面行走时也能正常显示进度条。
     */
    private static boolean shouldReplaceExperienceBar(Player player) {
        if (player.isSpectator()) {
            return false;
        }

        if (player.getAbilities().instabuild) {
            return false;
        }

        boolean hasBlazingCore = BlazingCoreHelper.hasBlazingCore(player);

        /*
         * 佩戴烈焰之核并接触岩浆时显示；
         * 离开岩浆后，只要视觉热量还没冷却完，也继续显示。
         */
        return clientVisualLavaHeat > 0 || (hasBlazingCore && isTouchingLava(player));
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
        /*
         * GUI 只读取客户端视觉热量。
         * 服务端真实热量仍然由 BlazingCoreEvents 控制。
         */
        int heat = Mth.clamp(clientVisualLavaHeat, 0, maxHeat);

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

    /**
     * 更新客户端视觉热量。
     * 说明：
     * 服务端真实热量不自动同步到客户端，
     * 所以这里复制一份客户端视觉计数。
     * 这个方法只影响 GUI 显示，
     * 不会改变烈焰之核真实保护时间。
     */
    private static void updateClientVisualLavaHeat(Player player) {
        if (player.tickCount == lastClientVisualTick) {
            return;
        }

        lastClientVisualTick = player.tickCount;

        int maxHeat = Math.max(1, ConfigCommon.BLAZING_CORE_LAVA_IMMUNITY_TICKS.get());
        boolean hasBlazingCore = BlazingCoreHelper.hasBlazingCore(player);

        /*
         * 佩戴烈焰之核并接触岩浆：
         * 客户端视觉热量每 tick +1。
         */
        if (hasBlazingCore && isTouchingLava(player)) {
            clientVisualLavaHeat = Math.min(maxHeat, clientVisualLavaHeat + 1);
            return;
        }

        /*
         * 离开岩浆或脱下烈焰之核：
         * 视觉热量按配置冷却。
         */
        if (clientVisualLavaHeat > 0) {
            int cooldown = Math.max(1, ConfigCommon.BLAZING_CORE_LAVA_COOLDOWN_PER_TICK.get());
            clientVisualLavaHeat = Math.max(0, clientVisualLavaHeat - cooldown);
        }
    }

    /**
     * 判断客户端玩家是否正在接触岩浆。
     * 不能只用 player.isInLava()，
     * 因为现在烈焰之核支持岩浆游泳 / 岩浆表面行动，
     * 某些状态下 isInLava() 不够稳定。
     */
    private static boolean isTouchingLava(Player player) {
        if (player.isInLava()) {
            return true;
        }

        BlockPos bodyPos = player.blockPosition();
        BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());

        return player.level().getFluidState(bodyPos).is(FluidTags.LAVA)
                || player.level().getFluidState(bodyPos.below()).is(FluidTags.LAVA)
                || player.level().getFluidState(eyePos).is(FluidTags.LAVA);
    }
}
