package org.enigmatic_legacy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.entity.ModEntities;
import org.enigmatic_legacy.event.*;
import org.enigmatic_legacy.generator.*;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.potion.ModPotions;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.tab.ModeTabs;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {

    public static final String MODID = "enigmatic_legacy";

    public EnigmaticLegacy(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigCommon.SPEC, "enigmatic_legacy-server.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC, "enigmatic_legacy-client.toml");

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModeTabs.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);

        ModSounds.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(BlockGenerator::gatherData);
        modEventBus.addListener(ItemGenerator::gatherData);
        modEventBus.addListener(LanguageGenerator::gatherData);
        modEventBus.addListener(RecipeGenerator::gatherData);
        modEventBus.addListener(FurnaceRecipeGenerator::gatherData);
        modEventBus.addListener(CuriosGenerator::gatherData);
        modEventBus.addListener(SoundGenerator::gatherData);

        NeoForge.EVENT_BUS.register(MonsterCharmEvents.class);
        NeoForge.EVENT_BUS.register(EnchanterPearlEvents.class);
        NeoForge.EVENT_BUS.register(EnigmaticAmuletEvents.class);
        NeoForge.EVENT_BUS.register(TeleportParticleEvents.class); // 传送粒子
        NeoForge.EVENT_BUS.register(EnderRingEvents.class); // 末影之戒
        NeoForge.EVENT_BUS.register(MagnetRingEvents.class); // 磁力之戒
        NeoForge.EVENT_BUS.register(TreasureHunterCharmEvents.class); // 猎宝者护符
        NeoForge.EVENT_BUS.register(BloodstainedValorEvents.class); // 血战沙场之证
        NeoForge.EVENT_BUS.register(CursedRingEvents.class);
        NeoForge.EVENT_BUS.register(EvilEssenceEvents.class);
        NeoForge.EVENT_BUS.register(EvilIngotEvents.class);
        NeoForge.EVENT_BUS.register(SoulCrystalEvents.class);
        NeoForge.EVENT_BUS.register(ForbiddenFruitEvents.class);


    }
}
