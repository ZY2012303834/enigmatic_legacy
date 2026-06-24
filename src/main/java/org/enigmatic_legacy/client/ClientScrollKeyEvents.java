package org.enigmatic_legacy.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.network.ScrollUsePayload;

/**
 * 客户端卷轴按键监听。
 * Shift + 绑定按键：
 * 如果永恒智慧卷轴装备在奥秘卷轴栏，则启用 / 停用。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class ClientScrollKeyEvents {
    private ClientScrollKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        // 打开聊天框、背包、菜单时，不触发快捷键。
        if (minecraft.screen != null) {
            return;
        }

        while (ModKeyMappings.SCROLL_KEY.consumeClick()) {
            if (minecraft.player.isShiftKeyDown()) {
                PacketDistributor.sendToServer(new ScrollUsePayload());
            }
        }
    }
}