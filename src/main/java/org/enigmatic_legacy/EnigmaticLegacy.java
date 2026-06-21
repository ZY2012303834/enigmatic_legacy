package org.enigmatic_legacy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.client.ClientItemProperties;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.event.CursedRingEvents;
import org.enigmatic_legacy.event.EvilEssenceEvents;
import org.enigmatic_legacy.generator.*;
import org.enigmatic_legacy.tab.ModeTabs;
import org.enigmatic_legacy.item.ModItems;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {

    public static final String MODID = "enigmatic_legacy";

    public EnigmaticLegacy(IEventBus modEventBus, ModContainer modContainer) {
        // 注册服务器配置
        modContainer.registerConfig(
                ModConfig.Type.SERVER,
                ConfigCommon.SPEC,
                "enigmatic_legacy-server.toml"
        );

        // 注册客户端配置
        modContainer.registerConfig(
                ModConfig.Type.CLIENT,
                ConfigClient.SPEC,
                "enigmatic_legacy-client.toml"
        );

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModeTabs.register(modEventBus);

        modEventBus.addListener(BlockGenerator::gatherData);
        modEventBus.addListener(ItemGenerator::gatherData);
        modEventBus.addListener(LanguageGenerator::gatherData);
        modEventBus.addListener(RecipeGenerator::gatherData);
        modEventBus.addListener(FurnaceRecipeGenerator::gatherData);
        modEventBus.addListener(CuriosGenerator::gatherData);

        // 注册游戏事件
        NeoForge.EVENT_BUS.register(CursedRingEvents.class);    // 七咒相关
        NeoForge.EVENT_BUS.register(EvilEssenceEvents.class);   // 邪恶精髓
    }
}
