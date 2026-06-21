package org.enigmatic_legacy.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.event.EnderRingEvents;
import org.enigmatic_legacy.util.EnderRingHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.util.function.BooleanSupplier;

/**
 * 末影之戒客户端事件。
 *
 * <p>负责：
 * <ul>
 *     <li>注册按键；</li>
 *     <li>在背包界面添加打开末影箱的 UI 按钮；</li>
 *     <li>向服务器发送打开末影箱命令。</li>
 * </ul>
 */
public final class EnderRingClientEvents {

    private static final int INVENTORY_GUI_WIDTH = 176;
    private static final int INVENTORY_GUI_HEIGHT = 166;
    private static final int CREATIVE_GUI_WIDTH = 195;
    private static final int CREATIVE_GUI_HEIGHT = 136;
    private static final int BUTTON_SIZE = 20;
    private static final ItemStack ENDER_CHEST_ICON = new ItemStack(Items.ENDER_CHEST);

    public static final KeyMapping OPEN_ENDER_CHEST = new KeyMapping(
            "key.enigmatic_legacy.ender_ring",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.enigmatic_legacy"
    );

    private EnderRingClientEvents() {
    }

    /**
     * 客户端 tick 中处理按键。
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (OPEN_ENDER_CHEST.consumeClick()) {
            requestOpenEnderChest();
        }
    }

    /**
     * 背包界面添加按钮。
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        if (!isSupportedInventoryScreen(screen)) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        if (!ConfigClient.ENDER_RING_BUTTON_ENABLED.get()) {
            return;
        }

        if (!EnderRingHelper.hasEnderChestAccess(player)) {
            return;
        }

        int x;
        int y;

        if (screen instanceof CreativeModeInventoryScreen) {
            int left = (screen.width - CREATIVE_GUI_WIDTH) / 2;
            int top = (screen.height - CREATIVE_GUI_HEIGHT) / 2;
            x = left + CREATIVE_GUI_WIDTH - BUTTON_SIZE - 12 + ConfigClient.ENDER_RING_BUTTON_OFFSET_X_CREATIVE.get();
            y = top + 32 + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y_CREATIVE.get();
        } else {
            int left = (screen.width - INVENTORY_GUI_WIDTH) / 2;
            int top = (screen.height - INVENTORY_GUI_HEIGHT) / 2;
            x = left + INVENTORY_GUI_WIDTH - BUTTON_SIZE - 12 + ConfigClient.ENDER_RING_BUTTON_OFFSET_X.get();
            y = top + 54 + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y.get();
        }

        Button button = new EnderChestButton(
                x,
                y,
                () -> shouldShowOnCurrentPage(screen),
                pressed -> requestOpenEnderChest()
        );
        button.setTooltip(Tooltip.create(Component.translatable("button.enigmatic_legacy.open_ender_chest")));

        event.addListener(button);
    }

    private static boolean isSupportedInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen
                || screen instanceof CreativeModeInventoryScreen
                || screen instanceof CuriosScreen;
    }

    private static boolean shouldShowOnCurrentPage(Screen screen) {
        return !(screen instanceof CreativeModeInventoryScreen creativeScreen) || creativeScreen.isInventoryOpen();
    }

    private static void requestOpenEnderChest() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand(EnderRingEvents.OPEN_ENDER_CHEST_COMMAND);
    }

    /**
     * Mod Bus 客户端事件。
     */
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_ENDER_CHEST);
    }

    private static final class EnderChestButton extends Button {

        private final BooleanSupplier displayCondition;

        private EnderChestButton(int x, int y, BooleanSupplier displayCondition, OnPress onPress) {
            super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.empty(), onPress, DEFAULT_NARRATION);
            this.displayCondition = displayCondition;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            active = displayCondition.getAsBoolean();

            if (!active) {
                return;
            }

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.renderItem(ENDER_CHEST_ICON, getX() + 2, getY() + 2);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return displayCondition.getAsBoolean() && super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
