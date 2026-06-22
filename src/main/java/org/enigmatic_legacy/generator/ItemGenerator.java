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
        basicItem(ModItems.FORBIDDEN_FRUIT.getId());
        // 扭曲魔镜
        withExistingParent("item/twisted_mirror", mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/twisted_mirror"));
        basicItem(ModItems.UNHOLY_GRAIL.getId()); // 不洁圣杯
        basicItem(ModItems.GUARDIAN_HEART.getId()); // 守卫者之心
        basicItem(ModItems.ENDER_RING.getId());

        basicItem(ModItems.UNWITNESSED_AMULET.getId());

        basicItem(ModItems.ENIGMATIC_AMULET_RED.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_AQUA.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_VIOLET.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_MAGENTA.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_GREEN.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_BLACK.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_BLUE.getId());

        basicItem(ModItems.MAGNET_RING.getId());
        basicItem(ModItems.DISLOCATION_RING.getId());
        basicItem(ModItems.MONSTER_CHARM.getId());
        basicItem(ModItems.TREASURE_HUNTER_CHARM.getId());
    }
}
