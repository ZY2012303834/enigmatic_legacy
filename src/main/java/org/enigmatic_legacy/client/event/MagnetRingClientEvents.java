package org.enigmatic_legacy.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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
 * 修复目标：
 * 1. 佩戴磁力之戒 / 转位之戒后，按钮立即出现；
 * 2. 摘下磁力之戒 / 转位之戒后，按钮立即消失；
 * 3. 按钮使用磁力之戒图标；
 * 4. 按钮固定放在末影箱按钮左侧。
 * 注意：
 * 本类只负责客户端 UI。
 * 真正的开关状态修改由服务端 MagnetRingEvents 处理。
 */
public final class MagnetRingClientEvents {
    /**
     * 普通背包 GUI 宽度。
     * 和 EnderRingClientEvents 保持一致，
     * 这样磁力按钮可以准确放到末影箱按钮左侧。
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
     * 末影箱按钮是 20x20，
     * 磁力按钮也使用 20x20。
     */
    private static final int BUTTON_SIZE = 20;

    /**
     * 磁力按钮与末影箱按钮之间的间距。
     */
    private static final int BUTTON_GAP = 2;

    private MagnetRingClientEvents() {
    }

    /**
     * 背包界面初始化完成后添加磁力按钮。
     * 关键点：
     * 这里不能检查“是否佩戴磁力之戒”。
     * 因为玩家可能在背包打开之后才把戒指放进 Curios 槽，
     * 当前 Screen 不会因此重新触发 Init.Post。
     * 所以这里始终添加按钮。
     * 按钮是否实际显示，由按钮自己的 renderWidget 每帧动态判断。
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        // 只在普通背包、创造背包、Curios 背包中添加这个按钮。
        if (!isSupportedInventoryScreen(screen)) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || Minecraft.getInstance().player.connection == null) {
            return;
        }

        // 复用末影之戒按钮显示开关。
        // 如果你后面想单独控制磁力按钮，再改成 MAGNET_RING_BUTTON_ENABLED。
        if (!ConfigClient.ENDER_RING_BUTTON_ENABLED.get()) {
            return;
        }

        int enderX;
        int enderY;

        /*
         * 这里复刻末影箱按钮的位置算法。
         * 当前项目的 EnderRingClientEvents 也是这样计算按钮位置的。
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

        // 磁力按钮固定在末影箱按钮左侧。
        int magnetX = enderX - BUTTON_SIZE - BUTTON_GAP;
        int magnetY = enderY;

        Button button = new MagnetRingButton(
                magnetX,
                magnetY,
                () -> shouldShowOnCurrentPage(screen),
                pressed -> requestToggleMagnetRing()
        );

        event.addListener(button);
    }

    /**
     * 判断当前 Screen 是否支持显示磁力按钮。
     */
    private static boolean isSupportedInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen
                || screen instanceof CreativeModeInventoryScreen
                || screen instanceof CuriosScreen;
    }

    /**
     * 创造模式下，只在生存背包页显示按钮。
     * 这样避免按钮出现在创造物品分页上。
     */
    private static boolean shouldShowOnCurrentPage(Screen screen) {
        return !(screen instanceof CreativeModeInventoryScreen creativeScreen)
                || creativeScreen.isInventoryOpen();
    }

    /**
     * 每帧判断当前是否应该显示磁力按钮。
     * 这个方法会在 renderWidget / mouseClicked 中实时调用，
     * 因此玩家在背包打开状态下佩戴或摘下戒指时，UI 会立即变化。
     */
    private static boolean shouldShowMagnetButton(BooleanSupplier pageCondition) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return false;
        }

        if (!pageCondition.getAsBoolean()) {
            return false;
        }

        // 佩戴磁力之戒或转位之戒时才显示。
        return MagnetRingHelper.hasMagnetControlRing(minecraft.player);
    }

    /**
     * 获取当前佩戴的磁力控制戒指。
     * 磁力控制戒指包括：
     * 1. 磁力之戒；
     * 2. 转位之戒。
     */
    private static Optional<ItemStack> getCurrentControlRing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return Optional.empty();
        }

        return MagnetRingHelper.findEquippedMagnetControlRing(minecraft.player);
    }

    /**
     * 请求服务器切换磁力开关。
     * 客户端不直接修改真实状态。
     */
    private static void requestToggleMagnetRing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand(MagnetRingHelper.TOGGLE_COMMAND);
    }

    /**
     * 根据当前磁力状态生成 tooltip。
     */
    private static Component buttonTooltip(boolean enabled) {
        return Component.translatable(enabled
                ? "gui.enigmatic_legacy.magnet_ring.tooltip.enabled"
                : "gui.enigmatic_legacy.magnet_ring.tooltip.disabled");
    }

    /**
     * 磁力之戒图标按钮。
     * 这个按钮在 Screen 初始化时始终添加。
     * 是否真正显示，由 shouldShowMagnetButton 每帧动态决定。
     */
    private static final class MagnetRingButton extends Button {
        private final BooleanSupplier pageCondition;

        private MagnetRingButton(
                int x,
                int y,
                BooleanSupplier pageCondition,
                OnPress onPress
        ) {
            super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.empty(), onPress, DEFAULT_NARRATION);
            this.pageCondition = pageCondition;

            /*
             * 重要：
             * visible 必须一直保持 true。
             *
             * 如果设置为 false，很多情况下下一帧不会再进入 renderWidget，
             * 这样按钮隐藏后就无法重新显示。
             *
             * 所以隐藏逻辑只在 renderWidget 里 return，不修改 visible。
             */
            this.visible = true;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            boolean shouldShow = shouldShowMagnetButton(this.pageCondition);

            // 不该显示时，不渲染，也不允许点击。
            this.active = shouldShow;

            if (!shouldShow) {
                return;
            }

            Optional<ItemStack> ring = getCurrentControlRing();

            // 默认开启，兼容没有写入 MagnetEnabled 的旧戒指。
            boolean enabled = ring.map(MagnetRingHelper::isMagnetEnabled).orElse(true);

            // 每帧刷新 tooltip，保证点击开关后提示能同步。
            this.setTooltip(Tooltip.create(buttonTooltip(enabled)));

            // 渲染原版按钮背景。
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            // 渲染磁力之戒图标。
            guiGraphics.renderItem(new ItemStack(ModItems.MAGNET_RING.get()), getX() + 2, getY() + 2);

            // 关闭时添加暗色遮罩。
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

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // 隐藏状态下，即使点到按钮原位置也不触发。
            return shouldShowMagnetButton(this.pageCondition)
                    && super.mouseClicked(mouseX, mouseY, button);
        }
    }
}