package org.enigmatic_legacy.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.event.ForbiddenFruitClientEvents;
import org.enigmatic_legacy.client.renderer.PermanentItemRenderer;
import org.enigmatic_legacy.entity.ModEntities;

@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
public final class EnigmaticLegacyClient {

    public EnigmaticLegacyClient(IEventBus modEventBus) {
        modEventBus.addListener(ClientItemProperties::onClientSetup);
        modEventBus.addListener(EnigmaticLegacyClient::registerEntityRenderers);
        NeoForge.EVENT_BUS.register(ForbiddenFruitClientEvents.class);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PERMANENT_ITEM.get(), PermanentItemRenderer::new);
    }
}
