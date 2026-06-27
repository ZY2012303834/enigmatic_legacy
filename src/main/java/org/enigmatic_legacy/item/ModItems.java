package org.enigmatic_legacy.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.items.*;
import org.enigmatic_legacy.item.items.armor.EtheriumArmorItem;
import org.enigmatic_legacy.item.items.charm.*;
import org.enigmatic_legacy.item.items.scroll.*;
import org.enigmatic_legacy.item.items.spellstone.*;
import org.enigmatic_legacy.item.items.sword.AxeOfExecutioner;
import org.enigmatic_legacy.item.items.sword.EnderSlayer;
import org.enigmatic_legacy.item.items.sword.EtheriumBroadsword;
import org.enigmatic_legacy.item.items.tool.EtheriumPickaxe;
import org.enigmatic_legacy.item.items.tool.EtheriumShovel;
import org.enigmatic_legacy.item.items.tool.EtheriumWaraxe;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnigmaticLegacy.MODID);

    public static final DeferredItem<AstralDust> ASTRAL_DUST = ITEMS.register("astral_dust", AstralDust::new);
    public static final DeferredItem<EnderRod> ENDER_ROD = ITEMS.register("ender_rod", EnderRod::new);
    public static final DeferredItem<EtheriumOre> ETHERIUM_ORE = ITEMS.register("etherium_ore", EtheriumOre::new);
    public static final DeferredItem<EtheriumIngot> ETHERIUM_INGOT = ITEMS.register("etherium_ingot", EtheriumIngot::new);
    public static final DeferredItem<ThiccScroll> THICC_SCROLL = ITEMS.register("thicc_scroll", ThiccScroll::new);
    public static final DeferredItem<DarkestScroll> DARKEST_SCROLL = ITEMS.register("darkest_scroll", DarkestScroll::new);
    public static final DeferredItem<BlockItem> ASTRAL_DUST_SACK = ITEMS.registerSimpleBlockItem(ModBlocks.ASTRAL_DUST_SACK);
    public static final DeferredItem<BlockItem> ETHERIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ETHERIUM_BLOCK, new BlockItem.Properties().fireResistant());
    public static final DeferredItem<CosmicHeart> COSMIC_HEART = ITEMS.register("cosmic_heart", CosmicHeart::new);
    public static final DeferredItem<BlockItem> BIG_LAMP = ITEMS.registerSimpleBlockItem(ModBlocks.BIG_LAMP);
    public static final DeferredItem<BlockItem> BIG_SHROOMLAMP = ITEMS.registerSimpleBlockItem(ModBlocks.BIG_SHROOMLAMP);
    public static final DeferredItem<EarthHeartFragment> EARTH_HEART_FRAGMENT = ITEMS.register("earth_heart_fragment", EarthHeartFragment::new);
    public static final DeferredItem<EarthHeart> EARTH_HEART = ITEMS.register("earth_heart", EarthHeart::new);
    public static final DeferredItem<TwistedHeart> TWISTED_HEART = ITEMS.register("twisted_heart", TwistedHeart::new);
    public static final DeferredItem<CursedRing> CURSED_RING = ITEMS.register("cursed_ring", CursedRing::new);
    public static final DeferredItem<EvilEssence> EVIL_ESSENCE = ITEMS.register("evil_essence", EvilEssence::new);
    public static final DeferredItem<IronRing> IRON_RING = ITEMS.register("iron_ring", IronRing::new);
    public static final DeferredItem<ExquisiteRing> EXQUISITE_RING = ITEMS.register("golden_ring", ExquisiteRing::new);
    public static final DeferredItem<EvilIngot> EVIL_INGOT = ITEMS.register("evil_ingot", EvilIngot::new);
    public static final DeferredItem<TwistedMirror> TWISTED_MIRROR = ITEMS.register("twisted_mirror", TwistedMirror::new); // 扭曲魔镜
    public static final DeferredItem<UnholyGrail> UNHOLY_GRAIL = ITEMS.register("unholy_grail", UnholyGrail::new); // 不洁圣杯
    public static final DeferredItem<GuardianHeart> GUARDIAN_HEART = ITEMS.register("guardian_heart", GuardianHeart::new); // 守卫者之心
    public static final DeferredItem<EnderRing> ENDER_RING = ITEMS.register("ender_ring", EnderRing::new); // 末影之戒
    public static final DeferredItem<MagnetRing> MAGNET_RING = ITEMS.register("magnet_ring", MagnetRing::new);  // 磁力之戒
    public static final DeferredItem<DislocationRing> DISLOCATION_RING = ITEMS.register("dislocation_ring", DislocationRing::new); // 转位之戒
    public static final DeferredItem<AbyssalHeart> ABYSSAL_HEART = ITEMS.register("abyssal_heart", AbyssalHeart::new); // 深渊之心
    public static final DeferredItem<ExtradimensionalEye> EXTRADIMENSIONAL_EYE = ITEMS.register("extradimensional_eye", ExtradimensionalEye::new); // 超维之眼
    public static final DeferredItem<TomeOfHungeringKnowledge> ENCHANTMENT_TRANSPOSER = ITEMS.register("enchantment_transposer", TomeOfHungeringKnowledge::new); // 求知之书
    public static final DeferredItem<TomeOfDevouredMalignancy> CURSE_TRANSPOSER = ITEMS.register("curse_transposer", TomeOfDevouredMalignancy::new); // 噬咒之书
    public static final DeferredItem<RecallPotionItem> RECALL_POTION = ITEMS.register("recall_potion", RecallPotionItem::new); // 召回药水
    // 修补混合物
    // 用于在工作台中完全修复任意受损的可损坏物品。
    public static final DeferredItem<MendingMixture> MENDING_MIXTURE = ITEMS.register("mending_mixture", MendingMixture::new);
    // 被诅咒者的寻路指针
    // 只有佩戴七咒之戒的玩家可以使用。
    // 用于定位当前维度内最近的灵魂水晶或装有死亡掉落的超维容器。
    public static final DeferredItem<WayfinderOfTheDamned> WAYFINDER_OF_THE_DAMNED = ITEMS.register("wayfinder_of_the_damned", WayfinderOfTheDamned::new);
    public static final DeferredItem<AnimalGuidebook> ANIMAL_GUIDEBOOK = ITEMS.register("animal_guidebook", AnimalGuidebook::new);
    public static final DeferredItem<HunterGuidebook> HUNTER_GUIDEBOOK = ITEMS.register("hunter_guidebook", HunterGuidebook::new);

    public static final DeferredItem<UnwitnessedAmulet> UNWITNESSED_AMULET = ITEMS.register("unwitnessed_amulet", UnwitnessedAmulet::new);
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_RED = ITEMS.register("enigmatic_amulet_red", () -> new EnigmaticAmulet(AmuletVariant.RED));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_AQUA = ITEMS.register("enigmatic_amulet_aqua", () -> new EnigmaticAmulet(AmuletVariant.AQUA));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_VIOLET = ITEMS.register("enigmatic_amulet_violet", () -> new EnigmaticAmulet(AmuletVariant.VIOLET));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_MAGENTA = ITEMS.register("enigmatic_amulet_magenta", () -> new EnigmaticAmulet(AmuletVariant.MAGENTA));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_GREEN = ITEMS.register("enigmatic_amulet_green", () -> new EnigmaticAmulet(AmuletVariant.GREEN));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_BLACK = ITEMS.register("enigmatic_amulet_black", () -> new EnigmaticAmulet(AmuletVariant.BLACK));
    public static final DeferredItem<EnigmaticAmulet> ENIGMATIC_AMULET_BLUE = ITEMS.register("enigmatic_amulet_blue", () -> new EnigmaticAmulet(AmuletVariant.BLUE));
    // 飞升护符 / Amulet of Ascension
    // 神秘护身符的升级版，同时拥有七种神秘护身符效果。
    // 注意：不再参与灵魂水晶 / 超维容器死亡保存逻辑。
    public static final DeferredItem<AscensionAmulet> ASCENSION_AMULET = ITEMS.register("ascension_amulet", AscensionAmulet::new);

    public static final DeferredItem<MonsterCharm> MONSTER_CHARM = ITEMS.register("monster_charm", MonsterCharm::new); // 怪物猎人勋章
    public static final DeferredItem<TreasureHunterCharm> TREASURE_HUNTER_CHARM = ITEMS.register("treasure_hunter_charm", TreasureHunterCharm::new); // 猎宝者护符
    public static final DeferredItem<BloodstainedValorEmblem> BLOODSTAINED_VALOR_EMBLEM = ITEMS.register("bloodstained_valor_emblem", BloodstainedValorEmblem::new); //
    public static final DeferredItem<MegaSponge> MEGA_SPONGE = ITEMS.register("mega_sponge", MegaSponge::new); // 超级海绵
    public static final DeferredItem<EnchanterPearl> ENCHANTER_PEARL = ITEMS.register("enchanter_pearl", EnchanterPearl::new); // 附魔师的珍珠
    public static final DeferredItem<EnigmaticEye> ENIGMATIC_EYE = ITEMS.register("enigmatic_eye", EnigmaticEye::new); // 休眠之眼

    // 武器工具
    public static final DeferredItem<TheAcknowledgment> THE_ACKNOWLEDGMENT = ITEMS.register("the_acknowledgment", TheAcknowledgment::new); // 启示之证 / The Acknowledgment
    // 倒转之启 / The Twist
    // 七咒专属启示之证变体，既是手册，也是武器。
    public static final DeferredItem<TheTwist> THE_TWIST = ITEMS.register("the_twist", TheTwist::new);
    public static final DeferredItem<TheInfinitum> THE_INFINITUM = ITEMS.register("the_infinitum", TheInfinitum::new);
    public static final DeferredItem<AxeOfExecutioner> AXE_OF_EXECUTIONER = ITEMS.register("axe_of_executioner", AxeOfExecutioner::new);
    public static final DeferredItem<EtheriumBroadsword> ETHERIUM_SWORD = ITEMS.register("etherium_sword", EtheriumBroadsword::new); // 以太阔剑
    public static final DeferredItem<EtheriumPickaxe> ETHERIUM_PICKAXE = ITEMS.register("etherium_pickaxe", EtheriumPickaxe::new); // 以太镐
    public static final DeferredItem<EtheriumShovel> ETHERIUM_SHOVEL = ITEMS.register("etherium_shovel", EtheriumShovel::new); // 以太锹
    public static final DeferredItem<EtheriumWaraxe> ETHERIUM_AXE = ITEMS.register("etherium_axe", EtheriumWaraxe::new); // 以太斧
    public static final DeferredItem<EtheriumArmorItem> ETHERIUM_HELMET = ITEMS.register("etherium_helmet", () -> new EtheriumArmorItem(ArmorItem.Type.HELMET)); // 以太头盔
    public static final DeferredItem<EtheriumArmorItem> ETHERIUM_CHESTPLATE = ITEMS.register("etherium_chestplate", () -> new EtheriumArmorItem(ArmorItem.Type.CHESTPLATE)); // 以太胸甲
    public static final DeferredItem<EtheriumArmorItem> ETHERIUM_LEGGINGS = ITEMS.register("etherium_leggings", () -> new EtheriumArmorItem(ArmorItem.Type.LEGGINGS)); // 以太护腿
    public static final DeferredItem<EtheriumArmorItem> ETHERIUM_BOOTS = ITEMS.register("etherium_boots", () -> new EtheriumArmorItem(ArmorItem.Type.BOOTS)); // 以太靴子
    // 末影之屠 / The Ender Slayer
    // 七咒遗物，专门用于对抗末地生物。
    public static final DeferredItem<EnderSlayer> ENDER_SLAYER = ITEMS.register("ender_slayer", EnderSlayer::new);
    // 烈焰之傲壁垒 / Bulwark of Blazing Pride
    // 原项目类名 InfernalShield。
    // 只有承受七咒之人才能使用的防火盾牌。
    public static final DeferredItem<BulwarkOfBlazingPride> BULWARK_OF_BLAZING_PRIDE = ITEMS.register("infernal_shield", BulwarkOfBlazingPride::new);

    // 饕餮之锅 / The Voracious Pan
    // 原项目类名 EldritchPan。
    // 只有承受七咒之人才能使用，会随不同生物击杀逐渐增强。
    public static final DeferredItem<VoraciousPan> VORACIOUS_PAN = ITEMS.register("eldritch_pan", VoraciousPan::new);

    // 术石
    public static final DeferredItem<GolemHeart> GOLEM_HEART = ITEMS.register("golem_heart", GolemHeart::new); // 魔像之心
    public static final DeferredItem<AngelBlessing> ANGEL_BLESSING = ITEMS.register("angel_blessing", AngelBlessing::new); // 天使之祝
    public static final DeferredItem<OceanStone> OCEAN_STONE = ITEMS.register("ocean_stone", OceanStone::new);  // 海洋意志
    public static final DeferredItem<BlazingCore> BLAZING_CORE = ITEMS.register("blazing_core", BlazingCore::new); // 烈焰核心
    public static final DeferredItem<EyeOfNebula> EYE_OF_NEBULA = ITEMS.register("eye_of_nebula", EyeOfNebula::new); // 星云之眼
    public static final DeferredItem<PearlOfTheVoid> VOID_PEARL = ITEMS.register("void_pearl", PearlOfTheVoid::new); // 虚空珍珠
    public static final DeferredItem<NonEuclideanCube> THE_CUBE = ITEMS.register("the_cube", NonEuclideanCube::new); // 非欧立方
    public static final DeferredItem<HeartOfCreation> HEART_OF_CREATION = ITEMS.register("heart_of_creation", HeartOfCreation::new); // 创造之心

    // 卷轴
    public static final DeferredItem<ScrollOfAgelessWisdom> XP_SCROLL = ITEMS.register("xp_scroll", ScrollOfAgelessWisdom::new); // 永恒智慧卷轴
    public static final DeferredItem<GiftOfTheHeaven> HEAVEN_SCROLL = ITEMS.register("heaven_scroll", GiftOfTheHeaven::new); // 天堂之礼
    public static final DeferredItem<ScrollOfThousandCurses> CURSED_SCROLL = ITEMS.register("cursed_scroll", ScrollOfThousandCurses::new); // 千咒卷轴
    public static final DeferredItem<GraceOfTheCreator> FABULOUS_SCROLL = ITEMS.register("fabulous_scroll", GraceOfTheCreator::new); // 创造者的恩赐
    public static final DeferredItem<PactOfInfiniteAvarice> AVARICE_SCROLL = ITEMS.register("avarice_scroll", PactOfInfiniteAvarice::new); // 无尽贪婪契约


    // 原版名为 storage_crystal，显示名是 Extradimensional Vessel / 超维容器。
    public static final DeferredItem<StorageCrystal> STORAGE_CRYSTAL = ITEMS.register("storage_crystal", StorageCrystal::new);
    public static final DeferredItem<SoulCrystal> SOUL_CRYSTAL = ITEMS.register("soul_crystal", SoulCrystal::new);
    public static final DeferredItem<ForbiddenFruit> FORBIDDEN_FRUIT = ITEMS.register("forbidden_fruit", ForbiddenFruit::new);

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
