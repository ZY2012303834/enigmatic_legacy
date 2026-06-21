package org.enigmatic_legacy.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.items.AstralDust;
import org.enigmatic_legacy.item.items.DarkestScroll;
import org.enigmatic_legacy.item.items.EnderRod;
import org.enigmatic_legacy.item.items.EtheriumIngot;
import org.enigmatic_legacy.item.items.EtheriumOre;
import org.enigmatic_legacy.item.items.ThiccScroll;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(EnigmaticLegacy.MODID);

    public static final DeferredItem<AstralDust> ASTRAL_DUST = ITEMS.register("astral_dust", AstralDust::new);
    public static final DeferredItem<EnderRod> ENDER_ROD = ITEMS.register("ender_rod", EnderRod::new);
    public static final DeferredItem<EtheriumOre> ETHERIUM_ORE = ITEMS.register("etherium_ore", EtheriumOre::new);
    public static final DeferredItem<EtheriumIngot> ETHERIUM_INGOT = ITEMS.register("etherium_ingot", EtheriumIngot::new);
    public static final DeferredItem<ThiccScroll> THICC_SCROLL = ITEMS.register("thicc_scroll", ThiccScroll::new);
    public static final DeferredItem<DarkestScroll> DARKEST_SCROLL = ITEMS.register("darkest_scroll", DarkestScroll::new);
    public static final DeferredItem<BlockItem> ASTRAL_DUST_SACK = ITEMS.registerSimpleBlockItem(ModBlocks.ASTRAL_DUST_SACK);
    public static final DeferredItem<BlockItem> ETHERIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ETHERIUM_BLOCK);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
