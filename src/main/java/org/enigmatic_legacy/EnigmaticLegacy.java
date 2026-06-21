package org.enigmatic_legacy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.creative.ModCreativeModeTabs;
import org.enigmatic_legacy.generator.BlockGenerator;
import org.enigmatic_legacy.generator.FurnaceRecipeGenerator;
import org.enigmatic_legacy.generator.ItemGenerator;
import org.enigmatic_legacy.generator.LanguageGenerator;
import org.enigmatic_legacy.generator.RecipeGenerator;
import org.enigmatic_legacy.item.ModItems;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {

    public static final String MODID = "enigmatic_legacy";
    public EnigmaticLegacy(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        modEventBus.addListener(BlockGenerator::gatherData);
        modEventBus.addListener(ItemGenerator::gatherData);
        modEventBus.addListener(LanguageGenerator::gatherData);
        modEventBus.addListener(RecipeGenerator::gatherData);
        modEventBus.addListener(FurnaceRecipeGenerator::gatherData);
    }

}
