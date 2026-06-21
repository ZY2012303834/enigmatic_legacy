package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;

public class LanguageGenerator extends LanguageProvider {

    private final String locale;

    public LanguageGenerator(PackOutput output, String locale) {
        super(output, EnigmaticLegacy.MODID, locale);
        this.locale = locale;
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new LanguageGenerator(output, "en_us"));
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new LanguageGenerator(output, "zh_cn"));
    }

    @Override
    protected void addTranslations() {
        switch (locale) {
            case "en_us" -> addEnglishTranslations();
            case "zh_cn" -> addChineseTranslations();
            default -> throw new IllegalStateException("Unsupported locale: " + locale);
        }
    }

    private void addEnglishTranslations() {
        add("itemGroup.enigmatic_legacy", "Enigmatic Legacy");      // 神秘遗物
        addItem(ModItems.ASTRAL_DUST, "Astral Dust");   // 星尘
        addItem(ModItems.ENDER_ROD, "Ender Rod");   // 末影棒
        addItem(ModItems.ETHERIUM_ORE, "Etherium Ore"); // 以太矿石
        addItem(ModItems.ETHERIUM_INGOT, "Etherium Ingot"); // 以太锭
        addItem(ModItems.THICC_SCROLL, "Blank Scroll"); // 空卷轴
        addItem(ModItems.DARKEST_SCROLL, "Darkest Scroll"); // 至暗卷轴
        addBlock(ModBlocks.ASTRAL_DUST_SACK, "Astral Block"); // 袋装星尘
        addBlock(ModBlocks.ETHERIUM_BLOCK, "Block of Etherium"); // 以太块
        addItem(ModItems.COSMIC_HEART, "Heart of the Cosmos"); // 寰宇之心
        addBlock(ModBlocks.BIG_LAMP, "Lamp");   // 大灯笼
        addBlock(ModBlocks.BIG_SHROOMLAMP, "Shroomlamp");   //菌光体灯笼
        addItem(ModItems.EARTH_HEART_FRAGMENT, "Fragment of the Earth"); // 大地之心碎片
    }

    private void addChineseTranslations() {
        add("itemGroup.enigmatic_legacy", "神秘遗物");
        addItem(ModItems.ASTRAL_DUST, "星尘");
        addItem(ModItems.ENDER_ROD, "末影棒");
        addItem(ModItems.ETHERIUM_ORE, "以太矿石");
        addItem(ModItems.ETHERIUM_INGOT, "以太锭");
        addItem(ModItems.THICC_SCROLL, "空卷轴");
        addItem(ModItems.DARKEST_SCROLL, "至暗卷轴");
        addBlock(ModBlocks.ASTRAL_DUST_SACK, "袋装星尘");
        addBlock(ModBlocks.ETHERIUM_BLOCK, "以太块");
        addItem(ModItems.COSMIC_HEART, "寰宇之心");
        addBlock(ModBlocks.BIG_LAMP, "大灯笼");
        addBlock(ModBlocks.BIG_SHROOMLAMP, "菌光体灯笼");
        addItem(ModItems.EARTH_HEART_FRAGMENT, "大地之心碎片");
    }
}
