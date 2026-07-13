package org.enigmatic_legacy.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.enigmatic_legacy.client.renderer.layer.ChaosElytraLayer;
import org.enigmatic_legacy.client.renderer.layer.MajesticElytraLayer;
import org.enigmatic_legacy.client.screen.AntiqueBookBagScreen;
import org.enigmatic_legacy.entity.ModEntities;
import org.enigmatic_legacy.menu.ModMenus;

@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
public final class EnigmaticLegacyClient {

    public EnigmaticLegacyClient(IEventBus modEventBus) {
        modEventBus.addListener(ClientItemProperties::onClientSetup);
        modEventBus.addListener(EnigmaticLegacyClient::registerEntityRenderers);
        modEventBus.addListener(EnigmaticLegacyClient::addEntityLayers);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addEntityLayers(EntityRenderersEvent.AddLayers event) {
        /*
         * 玩家渲染器分为宽手臂与细手臂两套皮肤模型。
         * 混沌之傲使用独立的 ElytraModel 渲染层，因此两套玩家模型都要挂载同一个自定义层。
         */
        for (PlayerSkin.Model skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);

            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new MajesticElytraLayer<LivingEntity, EntityModel<LivingEntity>>(
                        livingRenderer,
                        event.getEntityModels()
                ));
                livingRenderer.addLayer(new ChaosElytraLayer<LivingEntity, EntityModel<LivingEntity>>(
                        livingRenderer,
                        event.getEntityModels()
                ));
            }
        }
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANTIQUE_BOOK_BAG.get(), AntiqueBookBagScreen::new);
    }
}
