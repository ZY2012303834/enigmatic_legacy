package org.enigmatic_legacy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端按键注册。
 * 默认：
 * Shift + V = 启用 / 停用永恒智慧卷轴。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class ModKeyMappings {
    public static final KeyMapping SCROLL_KEY = new KeyMapping(
            "key.enigmatic_legacy.scroll",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.enigmatic_legacy"
    );

    private ModKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SCROLL_KEY);
    }
}