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
        addItem(ModItems.EARTH_HEART, "Heart of the Earth"); // 大地之心
        // 扭曲之心
        addItem(ModItems.TWISTED_HEART, "Twisted Heart");
        add("tooltip.enigmatic_legacy.cursed_ones_only", "Only those bearing the curse may comprehend its purpose.");
        add("tooltip.enigmatic_legacy.twisted_heart.active", "Activated by the Ring of the Seven Curses.");
        add("tooltip.enigmatic_legacy.twisted_heart.inactive", "Dormant.");
        // end
        addItem(ModItems.CURSED_RING, "Ring of the Seven Curses"); // 七咒之戒
        addEnglishCursedRingTooltips();
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
        addItem(ModItems.EARTH_HEART, "大地之心");
        addItem(ModItems.TWISTED_HEART, "扭曲之心");
        add("tooltip.enigmatic_legacy.cursed_ones_only", "唯有背负诅咒者方能理解它的用途。");
        add("tooltip.enigmatic_legacy.twisted_heart.active", "已被七咒之戒激活。");
        add("tooltip.enigmatic_legacy.twisted_heart.inactive", "尚未激活。");
        addItem(ModItems.CURSED_RING, "七咒之戒");
        addChineseCursedRingTooltips();

    }

    private void addEnglishCursedRingTooltips() {
        add("tooltip.enigmatic_legacy.void", " ");
        add("tooltip.enigmatic_legacy.holdShift", "§5Hold §6Shift§5 to see details.");
        add("tooltip.enigmatic_legacy.eternallyBound1", "§5Once worn, it becomes a part of you.");
        add("tooltip.enigmatic_legacy.eternallyBound2", "§4This ring will persist with you forever.");
        add("tooltip.enigmatic_legacy.eternallyBound2_creative", "§6With the power of god, you can unequip it.");
        add("tooltip.enigmatic_legacy.cursedRing3", "§dSeven curses will befall whoever bears it:");
        add("tooltip.enigmatic_legacy.cursedRing4", "§5- You receive double damage from §6ANY§5 source.");
        add("tooltip.enigmatic_legacy.cursedRing4_alt", "§5- You receive §6%1$s§5 damage from §6ANY§5 source.");
        add("tooltip.enigmatic_legacy.cursedRing5", "§5- Neutral creatures are aggressive towards you.");
        add("tooltip.enigmatic_legacy.cursedRing6", "§5- Armor is §6%1$s§5 less effective.");
        add("tooltip.enigmatic_legacy.cursedRing7", "§5- Monsters receive §6%1$s§5 less damage from you.");
        add("tooltip.enigmatic_legacy.cursedRing8", "§5- When on fire, you burn forever.");
        add("tooltip.enigmatic_legacy.cursedRing9", "§5- Every death tears your soul apart.");
        add("tooltip.enigmatic_legacy.cursedRing10", "§5- You suffer from incurable insomnia.");
        add("tooltip.enigmatic_legacy.cursedRing11", "§dSeven blessings will reward those who withstand:");
        add("tooltip.enigmatic_legacy.cursedRing12", "§5- §6+%1$s§d Looting Level");
        add("tooltip.enigmatic_legacy.cursedRing13", "§5- §6+%1$s§d Fortune Level");
        add("tooltip.enigmatic_legacy.cursedRing14", "§5- §6+%1$s§5 §dExperience§5 dropped.");
        add("tooltip.enigmatic_legacy.cursedRing15", "§5- §6+%1$s§d Enchanting Power§5 in §6Enchanting Table§5.");
        add("tooltip.enigmatic_legacy.cursedRing16", "§5- Unique drops from some creatures.");
        add("tooltip.enigmatic_legacy.cursedRing17", "§5- Functionality of §6Ring of Ender§5.");
        add("tooltip.enigmatic_legacy.cursedRing18", "§5- You can create and use unique relics.");
        add("tooltip.enigmatic_legacy.cursedRingLore1", "§5Once forged by antediluvian god, it beckons");
        add("tooltip.enigmatic_legacy.cursedRingLore2", "§5mortals and higher beings alike with promise");
        add("tooltip.enigmatic_legacy.cursedRingLore3", "§5of untold riches and immeasurable might...");
        add("tooltip.enigmatic_legacy.cursedRingLore4", "§5Be it arrogance or ignorance that leads them");
        add("tooltip.enigmatic_legacy.cursedRingLore5", "§5to believe they can harness ring's power,");
        add("tooltip.enigmatic_legacy.cursedRingLore6", "§5both are paid for in suffering the extent of");
        add("tooltip.enigmatic_legacy.cursedRingLore7", "§5which defies description.");
    }

    private void addChineseCursedRingTooltips() {
        add("tooltip.enigmatic_legacy.void", "");
        add("tooltip.enigmatic_legacy.holdShift", "§5按住 §6Shift§5 查看详情。");
        add("tooltip.enigmatic_legacy.eternallyBound1", "§5一旦佩戴，它便成为你的一部分。");
        add("tooltip.enigmatic_legacy.eternallyBound2", "§4这枚戒指将永远陪伴着你。");
        add("tooltip.enigmatic_legacy.eternallyBound2_creative", "§6需要神的能力才能取下它。");
        add("tooltip.enigmatic_legacy.cursedRing3", "§d七个诅咒将会降临到佩戴者身上：");
        add("tooltip.enigmatic_legacy.cursedRing4", "§5- 使受到的§6任何§5来源的伤害加倍");
        add("tooltip.enigmatic_legacy.cursedRing4_alt", "§5- 使受到的§6任何§5来源的伤害变化为原先的 §6%1$s");
        add("tooltip.enigmatic_legacy.cursedRing5", "§5- 中立生物会主动攻击你");
        add("tooltip.enigmatic_legacy.cursedRing6", "§5- 盔甲效力降低 §6%1$s");
        add("tooltip.enigmatic_legacy.cursedRing7", "§5- 你对怪物的伤害降低 §6%1$s§5");
        add("tooltip.enigmatic_legacy.cursedRing8", "§5- 一旦着火，火焰将会永远灼烧着你");
        add("tooltip.enigmatic_legacy.cursedRing9", "§5- 每次死亡都会使你的灵魂破裂");
        add("tooltip.enigmatic_legacy.cursedRing10", "§5- 无药可医的失眠症困扰着你");
        add("tooltip.enigmatic_legacy.cursedRing11", "§d而那些坚韧顽强之人，将会被给予七个祝福：");
        add("tooltip.enigmatic_legacy.cursedRing12", "§5- §6+%1$s§d 抢夺等级");
        add("tooltip.enigmatic_legacy.cursedRing13", "§5- §6+%1$s§d 时运等级");
        add("tooltip.enigmatic_legacy.cursedRing14", "§5- §6+%1$s§5 掉落的§d经验§");
        add("tooltip.enigmatic_legacy.cursedRing15", "§5- §6+%1$s§5 在附魔台中的§d附魔能力");
        add("tooltip.enigmatic_legacy.cursedRing16", "§5- 一些生物开始产出独特掉落物。");
        add("tooltip.enigmatic_legacy.cursedRing17", "§5- 获得§6末影之戒§5的效果");
        add("tooltip.enigmatic_legacy.cursedRing18", "§5- 你可以制作和使用独特遗物");
        add("tooltip.enigmatic_legacy.cursedRingLore1", "§5由远古时代的神明铸造，无论是凡人");
        add("tooltip.enigmatic_legacy.cursedRingLore2", "§5还是那些更高等的存在都会受到其");
        add("tooltip.enigmatic_legacy.cursedRingLore3", "§5承诺的无数财富与无穷力量的诱惑……");
        add("tooltip.enigmatic_legacy.cursedRingLore4", "§5而前人们无一例外都产生了自己能");
        add("tooltip.enigmatic_legacy.cursedRingLore5", "§5驾驭戒指力量的自负想法，而最终");
        add("tooltip.enigmatic_legacy.cursedRingLore6", "§5他们也同样为自己的傲慢或无知");
        add("tooltip.enigmatic_legacy.cursedRingLore7", "§5付出了不可思议的痛苦代价。");
    }
}
