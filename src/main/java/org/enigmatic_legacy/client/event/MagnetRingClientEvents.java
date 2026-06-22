package org.enigmatic_legacy.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.MagnetRingHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * 磁力之戒客户端 UI 事件。
 * 功能：
 * 1. 在背包 / 创造背包 / Curios 背包中添加磁力开关按钮；
 * 2. 按钮图标使用磁力之戒；
 * 3. 按钮固定在末影箱按钮左侧；
 * 4. 只有佩戴磁力之戒或转位之戒时才显示；
 * 5. 隐藏时不渲染、不响应点击、不显示 tooltip。
 * 注意：
 * 本类只处理客户端 UI。
 * 真正的开关状态修改由服务端 MagnetRingEvents 处理。
 */
public final class MagnetRingClientEvents {
    /**
     * 普通背包 GUI 宽度。
     * 与 EnderRingClientEvents 保持一致。
     */
    private static final int INVENTORY_GUI_WIDTH = 176;

    /**
     * 普通背包 GUI 高度。
     */
    private static final int INVENTORY_GUI_HEIGHT = 166;

    /**
     * 创造背包 GUI 宽度。
     */
    private static final int CREATIVE_GUI_WIDTH = 195;

    /**
     * 创造背包 GUI 高度。
     */
    private static final int CREATIVE_GUI_HEIGHT = 136;

    /**
     * 按钮尺寸。
     */
    private static final int BUTTON_SIZE = 20;

    /**
     * 磁力按钮与末影箱按钮之间的间距。
     */
    private static final int BUTTON_GAP = 2;

    private static MagnetRingButton magnetButton;

    private MagnetRingClientEvents() {
    }

    /**
     * 背包界面初始化后添加磁力按钮。
     * 关键点：
     * 这里不能检查玩家是否佩戴磁力之戒。
     * 如果这里因为未佩戴而 return，那么玩家在背包打开后再装备戒指，
     * 当前 Screen 不会重新初始化，按钮就不会立即出现。
     * 所以这里始终添加按钮。
     * 是否显示由 MagnetRingButton 每帧动态判断。
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        if (!isSupportedInventoryScreen(screen)) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || Minecraft.getInstance().player.connection == null) {
            return;
        }

        // 复用末影之戒按钮开关。
        // 后续如果你想单独控制磁力按钮，可以改成独立的 MAGNET_RING_BUTTON_ENABLED。
        if (!ConfigClient.ENDER_RING_BUTTON_ENABLED.get()) {
            return;
        }

        int enderX;
        int enderY;

        /*
         * 复刻末影箱按钮坐标。
         * 然后把磁力按钮放在它左侧。
         */
        if (screen instanceof CreativeModeInventoryScreen) {
            int left = (screen.width - CREATIVE_GUI_WIDTH) / 2;
            int top = (screen.height - CREATIVE_GUI_HEIGHT) / 2;

            enderX = left
                    + CREATIVE_GUI_WIDTH
                    - BUTTON_SIZE
                    - 12
                    + ConfigClient.ENDER_RING_BUTTON_OFFSET_X_CREATIVE.get();

            enderY = top
                    + 32
                    + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y_CREATIVE.get();
        } else {
            int left = (screen.width - INVENTORY_GUI_WIDTH) / 2;
            int top = (screen.height - INVENTORY_GUI_HEIGHT) / 2;

            enderX = left
                    + INVENTORY_GUI_WIDTH
                    - BUTTON_SIZE
                    - 12
                    + ConfigClient.ENDER_RING_BUTTON_OFFSET_X.get();

            enderY = top
                    + 54
                    + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y.get();
        }

        int magnetX = enderX - BUTTON_SIZE - BUTTON_GAP;
        int magnetY = enderY;

        magnetButton = new MagnetRingButton(
                magnetX,
                magnetY,
                () -> shouldShowOnCurrentPage(screen),
                pressed -> requestToggleMagnetRing()
        );

        event.addListener(magnetButton);
    }

    /**
     * 判断哪些界面支持磁力按钮。
     */
    private static boolean isSupportedInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen
                || screen instanceof CreativeModeInventoryScreen
                || screen instanceof CuriosScreen;
    }

    /**
     * 创造模式只在生存背包页显示。
     */
    private static boolean shouldShowOnCurrentPage(Screen screen) {
        return !(screen instanceof CreativeModeInventoryScreen creativeScreen)
                || creativeScreen.isInventoryOpen();
    }

    /**
     * 每帧判断磁力按钮是否应该显示。
     * 条件：
     * 1. 当前页面允许显示；
     * 2. 玩家存在；
     * 3. 玩家佩戴磁力之戒或转位之戒。
     */
    private static boolean shouldShowMagnetButton(BooleanSupplier pageCondition) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!pageCondition.getAsBoolean()) {
            return false;
        }

        if (minecraft.player == null) {
            return false;
        }

        return MagnetRingHelper.hasMagnetControlRing(minecraft.player);
    }

    /**
     * 获取当前佩戴的磁力控制戒指。
     */
    private static Optional<ItemStack> getCurrentControlRing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return Optional.empty();
        }

        return MagnetRingHelper.findEquippedMagnetControlRing(minecraft.player);
    }

    /**
     * 请求服务端切换磁力开关。
     */
    private static void requestToggleMagnetRing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand(MagnetRingHelper.TOGGLE_COMMAND);
    }

    /**
     * 按钮 tooltip。
     * 根据当前佩戴的戒指动态显示：
     * - 磁力之戒当前已开启/关闭
     * - 转位之戒当前已开启/关闭
     */
    private static Component buttonTooltip(ItemStack ring, boolean enabled) {
        return Component.translatable(
                enabled
                        ? "gui.enigmatic_legacy.magnet_control.tooltip.enabled"
                        : "gui.enigmatic_legacy.magnet_control.tooltip.disabled",
                Component.translatable(MagnetRingHelper.getMagnetControlRingNameKey(ring))
        );
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (magnetButton == null) {
            return;
        }

        if (!isSupportedInventoryScreen(event.getScreen())) {
            return;
        }

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        if (!magnetButton.shouldDisplay()) {
            return;
        }

        if (!magnetButton.isMouseActuallyOver(mouseX, mouseY)) {
            return;
        }

        Optional<ItemStack> ring = getCurrentControlRing();

        if (ring.isEmpty()) {
            return;
        }

        boolean enabled = MagnetRingHelper.isMagnetEnabled(ring.get());

        event.getGuiGraphics().renderTooltip(
                Minecraft.getInstance().font,
                buttonTooltip(ring.get(), enabled),
                (int) mouseX,
                (int) mouseY
        );
    }

    /**
     * 磁力之戒图标按钮。
     * 这个按钮对象会一直存在于 Screen 中，
     * 但只有满足 shouldShowMagnetButton 时才渲染、才响应鼠标、才显示 tooltip。
     */
    private static final class MagnetRingButton extends Button {
        private final BooleanSupplier pageCondition;

        /**
         * 当前帧鼠标是否悬停在按钮上。
         *
         * 这个值在 renderWidget 里用按钮自己的鼠标坐标计算，
         * 避免 Render.Post 里重新计算导致命中范围偏移。
         */
        private boolean hoveredThisFrame;

        private MagnetRingButton(
                int x,
                int y,
                BooleanSupplier pageCondition,
                OnPress onPress
        ) {
            super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.empty(), onPress, DEFAULT_NARRATION);
            this.pageCondition = pageCondition;
        }

        /**
         * 当前按钮是否应该显示。
         */
        private boolean shouldDisplay() {
            return shouldShowMagnetButton(this.pageCondition);
        }


        /**
         * 当前帧鼠标是否真的悬停在按钮上。
         * 不在这里重新计算坐标。
         * 坐标计算统一放在 renderWidget 里，避免 Render.Post 阶段坐标偏移。
         */
        private boolean isMouseActuallyOver(double mouseX, double mouseY) {
            return this.shouldDisplay() && this.hoveredThisFrame;
        }
        /**
         * 渲染按钮。
         * 关键点：
         * 这里不再使用 Button 自带的 Tooltip 系统。
         * 原因：
         * Button 即使不渲染，只要曾经设置过 Tooltip，
         * 某些情况下 Screen 仍可能在旧按钮位置读取到 tooltip。
         * 所以这里改成：
         * 1. 隐藏时不渲染、不点击、不设置 tooltip；
         * 2. 显示时只渲染按钮和图标；
         * 3. 只有按钮显示且鼠标真正悬停时，才手动渲染 tooltip。
         */
        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            boolean shouldShow = this.shouldDisplay();

            this.active = shouldShow;
            this.hoveredThisFrame = false;

            if (!shouldShow) {
                return;
            }

            Optional<ItemStack> ring = getCurrentControlRing();

            if (ring.isEmpty()) {
                return;
            }

            boolean enabled = MagnetRingHelper.isMagnetEnabled(ring.get());

            /*
             * 关键：
             * 悬停判断必须在按钮自己的 renderWidget 里做。
             * 这里的 mouseX / mouseY 与 Button 渲染流程使用的是同一套坐标，
             * 不会出现 tooltip 命中框相对按钮偏移的问题。
             */
            this.hoveredThisFrame = mouseX >= this.getX()
                    && mouseX < this.getX() + BUTTON_SIZE
                    && mouseY >= this.getY()
                    && mouseY < this.getY() + BUTTON_SIZE;

            // 渲染原版按钮背景。
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            // 渲染磁力之戒图标。
            guiGraphics.renderItem(
                    new ItemStack(ModItems.MAGNET_RING.get()),
                    getX() + 2,
                    getY() + 2
            );

            // 磁力关闭时给图标盖一层暗色遮罩。
            if (!enabled) {
                guiGraphics.fill(
                        getX() + 1,
                        getY() + 1,
                        getX() + BUTTON_SIZE - 1,
                        getY() + BUTTON_SIZE - 1,
                        0x88000000
                );
            }
        }

        /**
         * 隐藏时禁止鼠标悬浮判定。
         * 这是解决“按钮隐藏了但 tooltip 还显示”的关键。
         * Screen 的 tooltip 逻辑可能会根据 isMouseOver 判断悬浮组件，
         * 所以隐藏状态必须让 isMouseOver 返回 false。
         */
        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.shouldDisplay()
                    && mouseX >= this.getX()
                    && mouseX < this.getX() + BUTTON_SIZE
                    && mouseY >= this.getY()
                    && mouseY < this.getY() + BUTTON_SIZE;
        }
        /**
         * 隐藏时禁止点击。
         */
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.shouldDisplay()
                    && mouseX >= this.getX()
                    && mouseX < this.getX() + BUTTON_SIZE
                    && mouseY >= this.getY()
                    && mouseY < this.getY() + BUTTON_SIZE
                    && super.mouseClicked(mouseX, mouseY, button);
        }
    }
}