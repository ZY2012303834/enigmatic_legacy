package org.enigmatic_legacy;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.config.ConfigFileHelper;
import org.enigmatic_legacy.entity.ModEntities;
import org.enigmatic_legacy.event.*;
import org.enigmatic_legacy.generator.*;
import org.enigmatic_legacy.generator.language.LanguageGenerator;
import org.enigmatic_legacy.generator.loot.*;
import org.enigmatic_legacy.generator.patchouli.PatchouliBookGenerator;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.network.ModNetwork;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.potion.ModPotions;
import org.enigmatic_legacy.recipe.ModRecipeSerializers;
import org.enigmatic_legacy.sound.ModSounds;
import org.slf4j.Logger;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {
    public static final String MODID = "enigmatic_legacy";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EnigmaticLegacy(IEventBus modEventBus, ModContainer modContainer) {
        ConfigFileHelper.ensureDefaultConfig("enigmatic_legacy-server.toml", ConfigCommon.SPEC);
        ConfigFileHelper.ensureDefaultConfig("enigmatic_legacy-client.toml", ConfigClient.SPEC);

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigCommon.SPEC, "enigmatic_legacy-server.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC, "enigmatic_legacy-client.toml");

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModeTabs.register(modEventBus);

        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(ModNetwork::registerPayloads);
        modEventBus.addListener(BlockGenerator::gatherData);
        modEventBus.addListener(ItemGenerator::gatherData);
        modEventBus.addListener(LanguageGenerator::gatherData);     // 语言文件
        modEventBus.addListener(RecipeGenerator::gatherData);
        modEventBus.addListener(FurnaceRecipeGenerator::gatherData);
        modEventBus.addListener(AdvancementGenerator::gatherData);
        modEventBus.addListener(CuriosGenerator::gatherData);
        modEventBus.addListener(SoundGenerator::gatherData);
        modEventBus.addListener(PatchouliBookGenerator::gatherData);
        modEventBus.addListener(DamageTagGenerator::gatherData);
        modEventBus.addListener(DamageTypeGenerator::gatherData);

        modEventBus.addListener(ItemTagGenerator::gatherData);

        modEventBus.addListener(InjectLootTableGenerator::gatherData);
        modEventBus.addListener(GlobalLootModifierGenerator::gatherData);

        ModRecipeSerializers.register(modEventBus);

        NeoForge.EVENT_BUS.register(MonsterCharmEvents.class);
        NeoForge.EVENT_BUS.register(EnchanterPearlEvents.class);
        NeoForge.EVENT_BUS.register(EnigmaticAmuletEvents.class);
        NeoForge.EVENT_BUS.register(EldritchAmuletEvents.class);
        NeoForge.EVENT_BUS.register(EnigmaticEyeQuoteEvents.class);
        // 休眠之眼获取方式
        // 玩家第一次打开战利品箱时生成 1 个休眠之眼，并且每个玩家只会生成一次。
        NeoForge.EVENT_BUS.register(EnigmaticEyeObtainEvents.class);
        // 末影之屠事件
        NeoForge.EVENT_BUS.register(EnderSlayerEvents.class);
        // 烈焰之傲壁垒事件
        NeoForge.EVENT_BUS.register(BulwarkOfBlazingPrideEvents.class);
        // 饕餮之锅事件
        NeoForge.EVENT_BUS.register(VoraciousPanEvents.class);
        // 倒转之启事件
        NeoForge.EVENT_BUS.register(TheTwistEvents.class);
        // 无止之言事件
        NeoForge.EVENT_BUS.register(TheInfinitumEvents.class);

        NeoForge.EVENT_BUS.register(TeleportParticleEvents.class);
        NeoForge.EVENT_BUS.register(EnderRingEvents.class);
        NeoForge.EVENT_BUS.register(MagnetRingEvents.class);
        NeoForge.EVENT_BUS.register(TreasureHunterCharmEvents.class);
        NeoForge.EVENT_BUS.register(ScorchedCharmEvents.class); // 阳灼护符事件
        NeoForge.EVENT_BUS.register(BloodstainedValorEvents.class);
        NeoForge.EVENT_BUS.register(CursedRingEvents.class);
        NeoForge.EVENT_BUS.register(EnigmaticAdvancementEvents.class); // 本项目进度触发与聊天样式
        NeoForge.EVENT_BUS.register(AnimalGuidebookEvents.class);
        NeoForge.EVENT_BUS.register(HunterGuidebookEvents.class);
        NeoForge.EVENT_BUS.register(EvilEssenceEvents.class);
        NeoForge.EVENT_BUS.register(EvilIngotEvents.class);
        NeoForge.EVENT_BUS.register(SoulCrystalEvents.class);
        NeoForge.EVENT_BUS.register(ForbiddenFruitEvents.class);
        NeoForge.EVENT_BUS.register(GolemHeartEvents.class);
        NeoForge.EVENT_BUS.register(AngelBlessingEvents.class);
        NeoForge.EVENT_BUS.register(OceanStoneEvents.class);
        NeoForge.EVENT_BUS.register(BlazingCoreEvents.class);
        NeoForge.EVENT_BUS.register(EyeOfNebulaEvents.class);
        NeoForge.EVENT_BUS.register(PearlOfTheVoidEvents.class);
        NeoForge.EVENT_BUS.register(NonEuclideanCubeEvents.class);
        NeoForge.EVENT_BUS.register(HeartOfCreationEvents.class);
        NeoForge.EVENT_BUS.register(GiftOfTheHeavenEvents.class);
        NeoForge.EVENT_BUS.register(ScrollOfThousandCursesEvents.class);
        NeoForge.EVENT_BUS.register(GraceOfTheCreatorEvents.class);
        NeoForge.EVENT_BUS.register(PactOfInfiniteAvariceEvents.class);
        NeoForge.EVENT_BUS.register(AbyssalHeartEvents.class);
        NeoForge.EVENT_BUS.register(ExtradimensionalEyeEvents.class);
        NeoForge.EVENT_BUS.register(MajesticElytraEvents.class);
        NeoForge.EVENT_BUS.register(EtheriumArmorEvents.class);     //以太装甲

        if (ModList.get().isLoaded("lootr")) {
            NeoForge.EVENT_BUS.register(LootrCompatEvents.class);
        }

    }
}
