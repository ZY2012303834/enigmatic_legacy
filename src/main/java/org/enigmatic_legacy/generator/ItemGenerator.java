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
        basicItem(ModItems.COSMIC_HEART.getId());
        basicItem(ModItems.EARTH_HEART_FRAGMENT.getId());
        basicItem(ModItems.EARTH_HEART.getId());

        var twistedHeartOn = withExistingParent("item/twisted_heart_on", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart_on"));

        withExistingParent("item/twisted_heart", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart"))
                .override()
                .predicate(modLoc("activated"), 1.0F)
                .model(twistedHeartOn)
                .end();

        basicItem(ModItems.EVIL_ESSENCE.getId());
        basicItem(ModItems.CURSED_RING.getId());
        basicItem(ModItems.IRON_RING.getId());
        basicItem(ModItems.EXQUISITE_RING.getId());
        basicItem(ModItems.EVIL_INGOT.getId());
        basicItem(ModItems.STORAGE_CRYSTAL.getId());
        basicItem(ModItems.SOUL_CRYSTAL.getId());
    }
}
