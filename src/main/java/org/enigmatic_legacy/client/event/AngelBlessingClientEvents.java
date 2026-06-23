package org.enigmatic_legacy.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.network.SpellstoneUsePayload;
import org.lwjgl.glfw.GLFW;

/**
 * 术石主动技能客户端按键。
 */
public final class AngelBlessingClientEvents {
    public static final KeyMapping USE_SPELLSTONE = new KeyMapping(
            "key.enigmatic_legacy.use_spellstone",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.enigmatic_legacy"
    );

    private static boolean wasJumpDown;

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

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            wasJumpDown = false;
            return;
        }

        boolean jumpDown = minecraft.options.keyJump.isDown();

        // 空中按空格触发主动技能。
        if (jumpDown && !wasJumpDown && !player.onGround() && !player.getAbilities().flying) {
            requestUseSpellstone();
        }

        wasJumpDown = jumpDown;
    }

    private static void requestUseSpellstone() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        PacketDistributor.sendToServer(new SpellstoneUsePayload());
    }
}