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
 * 作用：
 * 1. 玩家佩戴磁力之戒时，在背包界面显示磁力开关按钮；
 * 2. 按钮使用“磁力之戒”物品图标，而不是文字 ON/OFF；
 * 3. 按钮固定显示在末影箱按钮左侧；
 * 4. 点击按钮后向服务器发送切换命令。
 * 注意：
 * 这里只负责客户端 UI。
 * 真正的磁力开关状态必须由服务端 MagnetRingEvents 处理。
 */
public final class MagnetRingClientEvents {
    /**
     * 普通生存背包 GUI 宽度。
     * 和 EnderRingClientEvents 保持一致，
     * 这样磁力按钮才能准确贴在末影箱按钮左边。
     */
    private static final int INVENTORY_GUI_WIDTH = 176;

    /**
     * 普通生存背包 GUI 高度。
     */
    private static final int INVENTORY_GUI_HEIGHT = 166;

    /**
     * 创造模式背包 GUI 宽度。
     * 和 EnderRingClientEvents 保持一致。
     */
    private static final int CREATIVE_GUI_WIDTH = 195;

    /**
     * 创造模式背包 GUI 高度。
     */
    private static final int CREATIVE_GUI_HEIGHT = 136;

    /**
     * 按钮大小。
     * 末影箱按钮是 20x20，
     * 磁力之戒按钮也用 20x20，视觉上对齐。
     */
    private static final int BUTTON_SIZE = 20;

    /**
     * 磁力按钮和末影箱按钮之间的间隔。
     * 末影箱按钮在右侧，磁力按钮放在它左侧：
     * magnetX = enderX - BUTTON_SIZE - BUTTON_GAP
     */
    private static final int BUTTON_GAP = 2;

    private MagnetRingClientEvents() {
    }

    /**
     * 背包界面初始化完成后添加磁力之戒按钮。
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        // 只在普通背包、创造背包、Curios 背包界面显示。
        if (!isSupportedInventoryScreen(screen)) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || Minecraft.getInstance().player.connection == null) {
            return;
        }

        // 复用末影之戒按钮开关。
        // 这样不需要额外新增 MAGNET_RING_BUTTON_ENABLED 配置项。
        if (!ConfigClient.ENDER_RING_BUTTON_ENABLED.get()) {
            return;
        }

        // 佩戴磁力之戒或转位之戒时都显示磁力开关按钮。
        Optional<ItemStack> ring = MagnetRingHelper.findEquippedMagnetControlRing(player);

        // 未佩戴磁力之戒时，不显示磁力按钮。
        if (ring.isEmpty()) {
            return;
        }

        int enderX;
        int enderY;

        /*
         * 这里完全复刻末影箱按钮的坐标算法。
         * 然后把磁力按钮放到末影箱按钮左侧。
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

        boolean enabled = MagnetRingHelper.isMagnetEnabled(ring.get());

        Button button = new MagnetRingButton(
                magnetX,
                magnetY,
                enabled,
                () -> shouldShowOnCurrentPage(screen),
                pressed -> requestToggleMagnetRing()
        );

        button.setTooltip(Tooltip.create(buttonTooltip(enabled)));
        event.addListener(button);
    }

    /**
     * 判断当前界面是否支持显示按钮。
     */
    private static boolean isSupportedInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen
                || screen instanceof CreativeModeInventoryScreen
                || screen instanceof CuriosScreen;
    }

    /**
     * 创造模式下只有打开“生存背包页”时才显示按钮。
     * 这样可以避免按钮出现在创造物品分页上。
     */
    private static boolean shouldShowOnCurrentPage(Screen screen) {
        return !(screen instanceof CreativeModeInventoryScreen creativeScreen)
                || creativeScreen.isInventoryOpen();
    }

    /**
     * 向服务器请求切换磁力之戒状态。
     * 客户端不直接修改真实状态。
     * 服务端会检查玩家是否真的佩戴磁力之戒，然后修改 ItemStack CustomData。
     */
    private static void requestToggleMagnetRing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand(MagnetRingHelper.TOGGLE_COMMAND);
    }

    /**
     * 按钮悬浮提示。
     */
    private static Component buttonTooltip(boolean enabled) {
        return Component.translatable(enabled
                ? "gui.enigmatic_legacy.magnet_ring.tooltip.enabled"
                : "gui.enigmatic_legacy.magnet_ring.tooltip.disabled");
    }

    /**
     * 磁力之戒图标按钮。
     * 这个按钮不显示文字，只渲染磁力之戒物品图标。
     */
    private static final class MagnetRingButton extends Button {
        private final BooleanSupplier displayCondition;

        /**
         * 客户端预览状态。
         * 点击后先本地翻转，用来立即更新 tooltip 和暗色遮罩。
         * 最终真实状态仍由服务端同步。
         */
        private boolean previewEnabled;

        private MagnetRingButton(
                int x,
                int y,
                boolean enabled,
                BooleanSupplier displayCondition,
                OnPress onPress
        ) {
            super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.empty(), onPress, DEFAULT_NARRATION);
            this.previewEnabled = enabled;
            this.displayCondition = displayCondition;
        }

        @Override
        public void onPress() {
            super.onPress();

            // 点击后立即更新客户端显示反馈。
            // 服务端稍后会同步真实 ItemStack 数据。
            this.previewEnabled = !this.previewEnabled;
            this.setTooltip(Tooltip.create(buttonTooltip(this.previewEnabled)));
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            active = displayCondition.getAsBoolean();

            if (!active) {
                return;
            }

            // 先渲染原版按钮背景。
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            // 渲染磁力之戒物品图标。
            guiGraphics.renderItem(new ItemStack(ModItems.MAGNET_RING.get()), getX() + 2, getY() + 2);

            /*
             * 如果磁力被关闭，给图标盖一层半透明黑色遮罩。
             * 这样玩家不用看 tooltip，也能大概知道当前是关闭状态。
             */
            if (!this.previewEnabled) {
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
            return displayCondition.getAsBoolean() && super.mouseClicked(mouseX, mouseY, button);
        }
    }
}