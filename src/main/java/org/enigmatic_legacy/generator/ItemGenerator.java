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
        basicItem(ModItems.ASTRAL_DUST.getId());    // 星尘
        basicItem(ModItems.ENDER_ROD.getId());      // 末影棒
        basicItem(ModItems.ETHERIUM_ORE.getId());   // 以太矿石
        basicItem(ModItems.ETHERIUM_INGOT.getId()); // 以太锭
        basicItem(ModItems.THICC_SCROLL.getId());   // 空卷轴
        basicItem(ModItems.DARKEST_SCROLL.getId()); // 至暗卷轴
        basicItem(ModItems.COSMIC_HEART.getId());   // 寰宇之心
        basicItem(ModItems.EARTH_HEART_FRAGMENT.getId()); // 大地之心碎片
        basicItem(ModItems.EARTH_HEART.getId()); // 大地之心
        // 扭曲之心
        var twistedHeartOn = withExistingParent("item/twisted_heart_on", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart_on"));

        withExistingParent("item/twisted_heart", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart"))
                .override()
                .predicate(modLoc("activated"), 1.0F)
                .model(twistedHeartOn)
                .end();
        // end
        basicItem(ModItems.EVIL_ESSENCE.getId()); // 邪恶精髓
        basicItem(ModItems.CURSED_RING.getId()); // 七咒之戒
        basicItem(ModItems.IRON_RING.getId()); // 铁指环
    }
}
