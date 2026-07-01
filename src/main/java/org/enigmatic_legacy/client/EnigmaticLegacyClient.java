package org.enigmatic_legacy.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.event.AngelBlessingClientEvents;
import org.enigmatic_legacy.client.event.EnderRingClientEvents;
import org.enigmatic_legacy.client.event.ForbiddenFruitClientEvents;
import org.enigmatic_legacy.client.event.MagnetRingClientEvents;
import org.enigmatic_legacy.client.event.MajesticElytraClientEvents;
import org.enigmatic_legacy.client.quote.QuoteHandler;
import org.enigmatic_legacy.client.renderer.PermanentItemRenderer;
import org.enigmatic_legacy.client.screen.AntiqueBookBagScreen;
import org.enigmatic_legacy.entity.ModEntities;
import org.enigmatic_legacy.menu.ModMenus;

@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
public final class EnigmaticLegacyClient {

    public EnigmaticLegacyClient(IEventBus modEventBus) {
        modEventBus.addListener(ClientItemProperties::onClientSetup);
        modEventBus.addListener(EnigmaticLegacyClient::registerEntityRenderers);
        modEventBus.addListener(EnigmaticLegacyClient::registerMenuScreens);
        modEventBus.addListener(EnderRingClientEvents::registerKeyMappings);

        modEventBus.addListener(AngelBlessingClientEvents::registerKeyMappings);
        NeoForge.EVENT_BUS.register(AngelBlessingClientEvents.class);

        NeoForge.EVENT_BUS.register(EnderRingClientEvents.class);
        NeoForge.EVENT_BUS.register(ForbiddenFruitClientEvents.class);
        NeoForge.EVENT_BUS.register(MagnetRingClientEvents.class);
        NeoForge.EVENT_BUS.register(MajesticElytraClientEvents.class);
        NeoForge.EVENT_BUS.register(QuoteHandler.INSTANCE);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PERMANENT_ITEM.get(), PermanentItemRenderer::new);
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANTIQUE_BOOK_BAG.get(), AntiqueBookBagScreen::new);
    }
}
