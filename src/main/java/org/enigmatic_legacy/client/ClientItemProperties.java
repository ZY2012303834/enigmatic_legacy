package org.enigmatic_legacy.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EnigmaticEye;
import org.enigmatic_legacy.item.items.TwistedHeart;

public final class ClientItemProperties {

    private ClientItemProperties() {
    }

    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.TWISTED_HEART.get(),
                    ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "activated"),
                    (stack, level, entity, seed) -> TwistedHeart.isActivated(stack) ? 1.0F : 0.0F
            );

            ItemProperties.register(
                    ModItems.ENIGMATIC_EYE.get(),
                    ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "enigmatic_eye_activated"),
                    (stack, level, entity, seed) -> EnigmaticEye.getModelProperty(stack)
            );
        });
    }
}
