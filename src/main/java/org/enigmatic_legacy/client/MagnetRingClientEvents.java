package org.enigmatic_legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.util.MagnetRingHelper;

import java.util.Optional;

/**
 * 磁力之戒客户端 UI。
 * 作用：
 * 当玩家佩戴磁力之戒并打开背包时，
 * 在背包界面右侧添加一个开关按钮。
 * 注意：
 * 这个类只在物理客户端加载。
 * 不要在这里执行真正的吸物逻辑，也不要直接修改服务器状态。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class MagnetRingClientEvents {
    /**
     * 按钮尺寸。
     * 24x20 可以显示英文 ON/OFF，也能显示中文“开/关”。
     */
    private static final int BUTTON_WIDTH = 24;
    private static final int BUTTON_HEIGHT = 20;

    private MagnetRingClientEvents() {
    }

    /**
     * 背包界面初始化完成后添加按钮。
     * ScreenEvent.Init.Post 会在 Screen#init 执行后触发，
     * 这时可以安全地往界面里 addListener。
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!ConfigClient.MAGNET_RING_BUTTON_ENABLED.get()) {
            return;
        }

        Screen screen = event.getScreen();

        // 普通玩家背包和创造模式背包都尝试支持。
        // 如果你只想普通生存背包显示，可以删掉 CreativeModeInventoryScreen 判断。
        boolean supportedScreen =
                screen instanceof InventoryScreen
                        || screen instanceof CreativeModeInventoryScreen;

        if (!supportedScreen) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        Optional<ItemStack> ring = MagnetRingHelper.findEquippedMagnetRing(minecraft.player);

        // 未佩戴磁力之戒时，不显示按钮。
        if (ring.isEmpty()) {
            return;
        }

        AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;

        boolean creative = screen instanceof CreativeModeInventoryScreen;

        int offsetX = creative
                ? ConfigClient.MAGNET_RING_BUTTON_OFFSET_X_CREATIVE.get()
                : ConfigClient.MAGNET_RING_BUTTON_OFFSET_X.get();

        int offsetY = creative
                ? ConfigClient.MAGNET_RING_BUTTON_OFFSET_Y_CREATIVE.get()
                : ConfigClient.MAGNET_RING_BUTTON_OFFSET_Y.get();

        /*
         * 默认位置：
         * 背包 GUI 右侧。
         *
         * getGuiLeft/getGuiTop 是容器界面的左上角坐标。
         * 原版生存背包主体宽度通常是 176，所以 left + 176 + 4
         * 基本就是贴在背包右边一点的位置。
         */
        int x = containerScreen.getGuiLeft() + 176 + 4 + offsetX;
        int y = containerScreen.getGuiTop() + 52 + offsetY;

        boolean enabled = MagnetRingHelper.isMagnetEnabled(ring.get());

        final Button[] buttonRef = new Button[1];

        Button button = Button.builder(buttonText(enabled), pressed -> {
                    Minecraft client = Minecraft.getInstance();

                    if (client.player == null || client.player.connection == null) {
                        return;
                    }

                    Optional<ItemStack> currentRing =
                            MagnetRingHelper.findEquippedMagnetRing(client.player);

                    /*
                     * 客户端这里只做“预估显示”。
                     * 真正状态切换由服务器命令完成。
                     * 服务端之后会同步 ItemStack CustomData，下一次打开界面会显示真实状态。
                     */
                    boolean currentEnabled = currentRing
                            .map(MagnetRingHelper::isMagnetEnabled)
                            .orElse(enabled);

                    boolean nextEnabled = !currentEnabled;

                    client.player.connection.sendCommand(MagnetRingHelper.TOGGLE_COMMAND);

                    // 立即更新按钮文字和提示，让点击反馈更明显。
                    if (buttonRef[0] != null) {
                        buttonRef[0].setMessage(buttonText(nextEnabled));
                        buttonRef[0].setTooltip(Tooltip.create(buttonTooltip(nextEnabled)));
                    }
                })
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(buttonTooltip(enabled)))
                .build();

        buttonRef[0] = button;
        event.addListener(button);
    }

    /**
     * 按钮短文本。
     * 用翻译键，而不是硬编码中文/英文。
     */
    private static Component buttonText(boolean enabled) {
        return Component.translatable(enabled
                ? "gui.enigmatic_legacy.magnet_ring.button.enabled"
                : "gui.enigmatic_legacy.magnet_ring.button.disabled");
    }

    /**
     * 鼠标悬停提示。
     */
    private static Component buttonTooltip(boolean enabled) {
        return Component.translatable(enabled
                ? "gui.enigmatic_legacy.magnet_ring.tooltip.enabled"
                : "gui.enigmatic_legacy.magnet_ring.tooltip.disabled");
    }
}