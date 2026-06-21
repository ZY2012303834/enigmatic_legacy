package org.enigmatic_legacy.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.items.*;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(EnigmaticLegacy.MODID);

    public static final DeferredItem<AstralDust> ASTRAL_DUST = ITEMS.register("astral_dust", AstralDust::new);  // 星尘
    public static final DeferredItem<EnderRod> ENDER_ROD = ITEMS.register("ender_rod", EnderRod::new);  // 末影棒
    public static final DeferredItem<EtheriumOre> ETHERIUM_ORE = ITEMS.register("etherium_ore", EtheriumOre::new);  // 以太矿石
    public static final DeferredItem<EtheriumIngot> ETHERIUM_INGOT = ITEMS.register("etherium_ingot", EtheriumIngot::new);  // 以太锭
    public static final DeferredItem<ThiccScroll> THICC_SCROLL = ITEMS.register("thicc_scroll", ThiccScroll::new);  // 空卷轴
    public static final DeferredItem<DarkestScroll> DARKEST_SCROLL = ITEMS.register("darkest_scroll", DarkestScroll::new);  // 至暗卷轴
    public static final DeferredItem<BlockItem> ASTRAL_DUST_SACK = ITEMS.registerSimpleBlockItem(ModBlocks.ASTRAL_DUST_SACK);   // 袋装星尘
    public static final DeferredItem<BlockItem> ETHERIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ETHERIUM_BLOCK);   // 以太块
    public static final DeferredItem<CosmicHeart> COSMIC_HEART = ITEMS.register("cosmic_heart", CosmicHeart::new);  // 寰宇之心
    public static final DeferredItem<BlockItem> BIG_LAMP = ITEMS.registerSimpleBlockItem(ModBlocks.BIG_LAMP); // 大灯笼
    public static final DeferredItem<BlockItem> MASSIVE_LAMP = ITEMS.registerSimpleBlockItem(ModBlocks.MASSIVE_LAMP); // 封装的大灯笼
    public static final DeferredItem<BlockItem> BIG_SHROOMLAMP = ITEMS.registerSimpleBlockItem(ModBlocks.BIG_SHROOMLAMP); // 菌光体灯笼

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
