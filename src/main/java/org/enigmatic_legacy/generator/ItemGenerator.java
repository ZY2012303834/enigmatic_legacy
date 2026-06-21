package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

public class ItemGenerator extends ItemModelProvider {

    public ItemGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new ItemGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.ASTRAL_DUST.getId());
        basicItem(ModItems.ENDER_ROD.getId());
        basicItem(ModItems.ETHERIUM_ORE.getId());
        basicItem(ModItems.ETHERIUM_INGOT.getId());
        basicItem(ModItems.THICC_SCROLL.getId());
        basicItem(ModItems.DARKEST_SCROLL.getId());
    }
}
