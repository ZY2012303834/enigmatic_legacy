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

        addItem(ModItems.EVIL_ESSENCE, "Nefarious Essence");
        add("tooltip.enigmatic_legacy.evilEssence1", "Embodies raw, unrefined energy torn out");
        add("tooltip.enigmatic_legacy.evilEssence2", "from the Wither's soul.");
        addItem(ModItems.IRON_RING, "Iron Ring");

        addItem(ModItems.EXQUISITE_RING, "Exquisite Ring");
        add("tooltip.enigmatic_legacy.exquisite_ring.luck", "Grants +1 Luck while equipped.");
        add("tooltip.enigmatic_legacy.exquisite_ring.piglin", "Piglins regard the wearer as one bearing gold.");
        add("tooltip.enigmatic_legacy.exquisite_ring.cursed_warning", "This piglin-neutralizing effect fails for bearers of the Seven Curses.");

        addItem(ModItems.EVIL_INGOT, "Nefarious Ingot");
        addItem(ModItems.STORAGE_CRYSTAL, "Extradimensional Vessel");
        add("tooltip.enigmatic_legacy.storageCrystal1", "Contains items and experience lost on death.");
        add("tooltip.enigmatic_legacy.storageCrystal2", "Stored stacks: %1$s");
        add("tooltip.enigmatic_legacy.storageCrystal3", "Stored experience: %1$s");

        addItem(ModItems.SOUL_CRYSTAL, "Soul Crystal");
        add("tooltip.enigmatic_legacy.soulCrystal1", "Right-Click to absorb the crystal and");
        add("tooltip.enigmatic_legacy.soulCrystal2", "restore one of your lost Soul Crystals.");

        addItem(ModItems.TWISTED_MIRROR, "Twisted Mirror");
        add("tooltip.enigmatic_legacy.twisted_mirror1", "Returns you to your spawn point.");
        add("tooltip.enigmatic_legacy.twisted_mirror2", "Only works in vanilla dimensions.");
        add("tooltip.enigmatic_legacy.twisted_mirror3", "Requires the Ring of the Seven Curses.");

        add("effect.enigmatic_legacy.recall", "Recall");
        add("effect.enigmatic_legacy.forbidden_fruit", "\u00A75The Forbidden Fruit");

        add("item.minecraft.potion.effect.recall", "Potion of Recall");
        add("item.minecraft.splash_potion.effect.recall", "Splash Potion of Recall");
        add("item.minecraft.lingering_potion.effect.recall", "Lingering Potion of Recall");
        add("item.minecraft.tipped_arrow.effect.recall", "Arrow of Recall");

        addItem(ModItems.UNHOLY_GRAIL, "Unholy Grail");
        add("tooltip.enigmatic_legacy.unholy_grail1", "Drink from it to draw upon forbidden power.");
        add("tooltip.enigmatic_legacy.unholy_grail2", "The unworthy will suffer for their arrogance.");

        addItem(ModItems.GUARDIAN_HEART, "Heart of the Guardian");
        add("tooltip.enigmatic_legacy.guardian_heart1", "While in your hotbar, looking at a monster within %s blocks turns it against nearby monsters.");
        add("tooltip.enigmatic_legacy.guardian_heart2", "Nearby monsters within %s blocks will retaliate against the marked creature.");
        add("tooltip.enigmatic_legacy.guardian_heart3", "Ability cooldown: %s seconds.");

        addItem(ModItems.FORBIDDEN_FRUIT, "The Forbidden Fruit");
        add("tooltip.enigmatic_legacy.forbidden_fruit_lore", "Knowledge is the highest gift.");
        add("tooltip.enigmatic_legacy.forbidden_fruit1", "After eating it, you will no longer");
        add("tooltip.enigmatic_legacy.forbidden_fruit2", "feel hunger, but any form of");
        add("tooltip.enigmatic_legacy.forbidden_fruit3", "regeneration is reduced by %1$s.");

        addItem(ModItems.ENDER_RING, "Ring of Ender");
        add("tooltip.enigmatic_legacy.ender_ring1", "Allows access to your Ender Chest while equipped.");
        add("tooltip.enigmatic_legacy.ender_ring2", "Press the keybind or use the inventory button.");
        add("key.categories.enigmatic_legacy", "Enigmatic Legacy");
        add("key.enigmatic_legacy.ender_ring", "Open Ender Chest");
        add("button.enigmatic_legacy.open_ender_chest", "Open Ender Chest");
        add("message.enigmatic_legacy.ender_ring.no_access", "You need the Ring of Ender to do that.");

        addItem(ModItems.UNWITNESSED_AMULET, "Unwitnessed Amulet");

        addItem(ModItems.ENIGMATIC_AMULET_RED, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_AQUA, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_VIOLET, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_MAGENTA, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_GREEN, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_BLACK, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_BLUE, "Enigmatic Amulet");

        add("tooltip.enigmatic_legacy.unwitnessed_amulet.1", "An amulet without witness, name, or fate.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.2", "It waits to be claimed by a mortal soul.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.3", "Right-click to reveal its true color.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.use", "Right-click to witness it.");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.1", "A relic marked by an unknown force.");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.2", "Only one amulet may answer your call.");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.owner", "Witnessed by: %s");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.red", "Color: Crimson");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.aqua", "Color: Aqua");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.violet", "Color: Violet");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.magenta", "Color: Magenta");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.green", "Color: Green");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.black", "Color: Black");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.blue", "Color: Blue");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red", "+2 Attack Damage");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua", "+15% Movement Speed while sprinting");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet", "15% chance to deflect incoming projectiles");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta", "-20% Gravity");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green", "+2 Mining Efficiency");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black", "10% Lifesteal");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue", "+25% Swim Speed");

        addItem(ModItems.MAGNET_RING, "Magnetic Ring");
        add("tooltip.enigmatic_legacy.magnet_ring.1", "Attracts nearby items within %s blocks.");
        add("tooltip.enigmatic_legacy.magnet_ring.2", "Hold Shift to suppress the magnetic field.");

        add("tooltip.enigmatic_legacy.magnet_ring.enabled", "Magnetic field: Enabled");
        add("tooltip.enigmatic_legacy.magnet_ring.disabled", "Magnetic field: Disabled");

        add("message.enigmatic_legacy.magnet_ring.enabled", "Magnetic field enabled.");
        add("message.enigmatic_legacy.magnet_ring.disabled", "Magnetic field disabled.");
        add("message.enigmatic_legacy.magnet_ring.no_ring", "You are not wearing a Magnetic Ring or Dislocation Ring.");

        add("gui.enigmatic_legacy.magnet_ring.tooltip.enabled", "Magnetic Ring is enabled. Click to disable.");
        add("gui.enigmatic_legacy.magnet_ring.tooltip.disabled", "Magnetic Ring is disabled. Click to enable.");
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

        addItem(ModItems.EVIL_ESSENCE, "邪恶精髓");
        add("tooltip.enigmatic_legacy.evilEssence1", "蕴含着从凋灵灵魂中撕裂而出的");
        add("tooltip.enigmatic_legacy.evilEssence2", "原始而未经提炼的邪恶能量。");
        addItem(ModItems.IRON_RING, "铁指环");

        addItem(ModItems.EXQUISITE_RING, "精美戒指");
        add("tooltip.enigmatic_legacy.exquisite_ring.luck", "装备时提供 +1 幸运。");
        add("tooltip.enigmatic_legacy.exquisite_ring.piglin", "猪灵会将佩戴者视作佩戴金制品。");
        add("tooltip.enigmatic_legacy.exquisite_ring.cursed_warning", "该猪灵中立效果对七咒佩戴者无效。");

        addItem(ModItems.EVIL_INGOT, "极恶锭");
        addItem(ModItems.STORAGE_CRYSTAL, "超维容器");
        add("tooltip.enigmatic_legacy.storageCrystal1", "保存死亡时遗失的物品与经验。");
        add("tooltip.enigmatic_legacy.storageCrystal2", "已保存物品堆：%1$s");
        add("tooltip.enigmatic_legacy.storageCrystal3", "已保存经验：%1$s");
        addItem(ModItems.SOUL_CRYSTAL, "灵魂水晶");
        add("tooltip.enigmatic_legacy.soulCrystal1", "右击吸收水晶，");
        add("tooltip.enigmatic_legacy.soulCrystal2", "并恢复一颗破碎的灵魂水晶。");

        addItem(ModItems.TWISTED_MIRROR, "扭曲魔镜");
        add("tooltip.enigmatic_legacy.twisted_mirror1", "将你传送回重生点。");
        add("tooltip.enigmatic_legacy.twisted_mirror2", "只能在原版维度中使用。");
        add("tooltip.enigmatic_legacy.twisted_mirror3", "需要佩戴七咒之戒。");

        add("effect.enigmatic_legacy.recall", "召回");
        add("effect.enigmatic_legacy.forbidden_fruit", "\u00A75禁忌之果");

        add("item.minecraft.potion.effect.recall", "召回药水");
        add("item.minecraft.splash_potion.effect.recall", "喷溅型召回药水");
        add("item.minecraft.lingering_potion.effect.recall", "滞留型召回药水");
        add("item.minecraft.tipped_arrow.effect.recall", "召回之箭");

        addItem(ModItems.UNHOLY_GRAIL, "不洁圣杯");
        add("tooltip.enigmatic_legacy.unholy_grail1", "饮下其中之物，以汲取禁忌之力。");
        add("tooltip.enigmatic_legacy.unholy_grail2", "无资格者将因傲慢付出代价。");

        addItem(ModItems.GUARDIAN_HEART, "守卫者之心");
        add("tooltip.enigmatic_legacy.guardian_heart1", "放在快捷栏时，注视 %s 格内的怪物会使其攻击附近怪物。");
        add("tooltip.enigmatic_legacy.guardian_heart2", "%s 格内的附近怪物会反过来攻击被标记的怪物。");
        add("tooltip.enigmatic_legacy.guardian_heart3", "能力冷却：%s 秒。");


        addItem(ModItems.FORBIDDEN_FRUIT, "禁忌之果");
        add("tooltip.enigmatic_legacy.forbidden_fruit_lore", "知识乃最高的馈赠。");
        add("tooltip.enigmatic_legacy.forbidden_fruit1", "食用后，你再也不会");
        add("tooltip.enigmatic_legacy.forbidden_fruit2", "感到饥饿，但任何形式的");
        add("tooltip.enigmatic_legacy.forbidden_fruit3", "再生效果都会削弱 %1$s。");

        addItem(ModItems.ENDER_RING, "末影之戒");
        add("tooltip.enigmatic_legacy.ender_ring1", "装备时可访问你的末影箱。");
        add("tooltip.enigmatic_legacy.ender_ring2", "按下绑定按键，或使用背包界面按钮。");
        add("key.categories.enigmatic_legacy", "神秘遗物");
        add("key.enigmatic_legacy.ender_ring", "打开末影箱");
        add("button.enigmatic_legacy.open_ender_chest", "打开末影箱");
        add("message.enigmatic_legacy.ender_ring.no_access", "你需要末影之戒才能这么做。");

        addItem(ModItems.UNWITNESSED_AMULET, "无主护身符");

        addItem(ModItems.ENIGMATIC_AMULET_RED, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_AQUA, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_VIOLET, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_MAGENTA, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_GREEN, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_BLACK, "神秘护身符");
        addItem(ModItems.ENIGMATIC_AMULET_BLUE, "神秘护身符");

        add("tooltip.enigmatic_legacy.unwitnessed_amulet.1", "无人见证、无人命名、无人拥有的护身符。");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.2", "它静候某个凡俗灵魂将其认领。");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.3", "右击后，它将显露真正的颜色。");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.use", "右击以见证它。");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.1", "被未知力量标记的遗物。");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.2", "同一时间只有一枚护身符会回应你。");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.owner", "见证者：%s");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.red", "颜色：赤红");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.aqua", "颜色：青蓝");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.violet", "颜色：紫罗兰");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.magenta", "颜色：品红");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.green", "颜色：翠绿");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.black", "颜色：漆黑");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.blue", "颜色：湛蓝");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red", "+2 攻击伤害");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua", "疾跑时 +15% 移动速度");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet", "15% 概率偏转来袭弹射物");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta", "-20% 重力");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green", "+2 挖掘效率");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black", "10% 生命偷取");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue", "+25% 游泳速度");

        addItem(ModItems.MAGNET_RING, "磁力之戒");
        add("tooltip.enigmatic_legacy.magnet_ring.1", "吸取 %s 格范围内的附近掉落物。");
        add("tooltip.enigmatic_legacy.magnet_ring.2", "按住 Shift 可暂时抑制磁场。");

        add("tooltip.enigmatic_legacy.magnet_ring.enabled", "磁场状态：已开启");
        add("tooltip.enigmatic_legacy.magnet_ring.disabled", "磁场状态：已关闭");

        add("message.enigmatic_legacy.magnet_ring.enabled", "磁力之戒已开启。");
        add("message.enigmatic_legacy.magnet_ring.disabled", "磁力之戒已关闭。");
        add("message.enigmatic_legacy.magnet_ring.no_ring", "你没有佩戴磁力之戒或转位之戒。");

        add("gui.enigmatic_legacy.magnet_ring.tooltip.enabled", "磁力之戒当前已开启。点击关闭。");
        add("gui.enigmatic_legacy.magnet_ring.tooltip.disabled", "磁力之戒当前已关闭。点击开启。");
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
