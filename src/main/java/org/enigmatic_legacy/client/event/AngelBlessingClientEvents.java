package org.enigmatic_legacy.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.network.SpellstoneUsePayload;
import org.lwjgl.glfw.GLFW;

/**
 * 术石主动技能客户端按键。
 * 只允许通过绑定按键触发。
 * 默认按键：K。
 */
public final class AngelBlessingClientEvents {
    public static final KeyMapping USE_SPELLSTONE = new KeyMapping(
            "key.enigmatic_legacy.use_spellstone",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.enigmatic_legacy"
    );

    private AngelBlessingClientEvents() {
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(USE_SPELLSTONE);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (USE_SPELLSTONE.consumeClick()) {
            requestUseSpellstone();
        }
    }

    private static void requestUseSpellstone() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        PacketDistributor.sendToServer(new SpellstoneUsePayload());
    }
}