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

 * Shift + 卷轴按键：
 * 如果卷轴装备在 scroll 栏，则启用 / 停用。
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

        while (ModKeyMappings.SCROLL_KEY.consumeClick()) {
            if (minecraft.player.isShiftKeyDown()) {
                PacketDistributor.sendToServer(new ScrollUsePayload());
            }
        }
    }
}