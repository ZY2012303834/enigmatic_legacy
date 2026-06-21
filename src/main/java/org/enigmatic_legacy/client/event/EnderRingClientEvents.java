package org.enigmatic_legacy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.event.EnderRingEvents;
import org.enigmatic_legacy.util.EnderRingHelper;
import org.lwjgl.glfw.GLFW;

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
@EventBusSubscriber(modid = EnigmaticLegacy.MODID, value = Dist.CLIENT)
public final class EnderRingClientEvents {

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

        if (!(screen instanceof InventoryScreen) && !(screen instanceof CreativeModeInventoryScreen)) {
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
            x = screen.width / 2 + 90 + ConfigClient.ENDER_RING_BUTTON_OFFSET_X_CREATIVE.get();
            y = screen.height / 2 - 70 + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y_CREATIVE.get();
        } else {
            x = screen.width / 2 + 76 + ConfigClient.ENDER_RING_BUTTON_OFFSET_X.get();
            y = screen.height / 2 - 83 + ConfigClient.ENDER_RING_BUTTON_OFFSET_Y.get();
        }

        Button button = Button.builder(
                        Component.literal("E"),
                        pressed -> requestOpenEnderChest()
                )
                .bounds(x, y, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("button.enigmatic_legacy.open_ender_chest")))
                .build();

        event.addListener(button);
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
    @EventBusSubscriber(
            modid = EnigmaticLegacy.MODID,
            bus = EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static final class ModBusEvents {

        private ModBusEvents() {
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_ENDER_CHEST);
        }
    }
}