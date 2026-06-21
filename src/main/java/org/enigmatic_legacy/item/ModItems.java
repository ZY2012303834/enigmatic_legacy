package org.enigmatic_legacy.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.items.*;

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

    // 原版名为 storage_crystal，显示名是 Extradimensional Vessel / 超维容器。
    public static final DeferredItem<StorageCrystal> STORAGE_CRYSTAL = ITEMS.register("storage_crystal", StorageCrystal::new);
    public static final DeferredItem<SoulCrystal> SOUL_CRYSTAL = ITEMS.register("soul_crystal", SoulCrystal::new);

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
