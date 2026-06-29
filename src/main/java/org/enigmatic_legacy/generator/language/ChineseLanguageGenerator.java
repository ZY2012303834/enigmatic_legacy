package org.enigmatic_legacy.generator.language;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;

public class ChineseLanguageGenerator extends LanguageProvider {

    public ChineseLanguageGenerator(PackOutput output) {
        super(output, EnigmaticLegacy.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
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
        addItem(ModItems.ICHOR_DROPLET, "灵液滴");

        addItem(ModItems.PURE_HEART, "纯净之心");

        add("tooltip.enigmatic_legacy.pure_heart.1",
                "纯净之物，却由诅咒塑成。");

        add("tooltip.enigmatic_legacy.pure_heart.2",
                "这颗心属于七咒净化路线。");

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
        add("tooltip.enigmatic_legacy.soulCrystal1", "死亡后取回水晶，");
        add("tooltip.enigmatic_legacy.soulCrystal2", "即可恢复一颗破碎的灵魂水晶。");

        addItem(ModItems.TWISTED_MIRROR, "扭曲魔镜");
        add("tooltip.enigmatic_legacy.twisted_mirror1", "将你传送回重生点。");
        add("tooltip.enigmatic_legacy.twisted_mirror2", "只能在原版维度中使用。");
        add("tooltip.enigmatic_legacy.twisted_mirror3", "需要佩戴七咒之戒。");

        add("effect.enigmatic_legacy.recall", "召回");
        add("effect.enigmatic_legacy.forbidden_fruit", "§5禁忌之果");

        addItem(ModItems.RECALL_POTION, "召回药水");
        add("tooltip.enigmatic_legacy.recall_potion.1", "饮用后传送回重生点。");
        add("tooltip.enigmatic_legacy.recall_potion.2", "在末地使用时，会传送至末地主岛平台附近。");
        addItem(ModItems.REDEMPTION_POTION, "救赎药水");
        add("tooltip.enigmatic_legacy.redemption_potion.1", "饮用后清除所有负面效果，包括禁忌之果。");
        add("tooltip.enigmatic_legacy.redemption_potion.2", "可在村庄宝箱中稀有发现。");

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

        addItem(ModItems.ASTRAL_FRUIT, "天体果实");
        add("tooltip.enigmatic_legacy.astral_fruit.1", "只有七咒之戒的佩戴者才能食用。");
        add("tooltip.enigmatic_legacy.astral_fruit.2", "给予强大的再生、抗性、力量与抗火效果。");
        add("tooltip.enigmatic_legacy.astral_fruit.3", "星辰以禁忌的生命力回应你。");

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

        add("tooltip.enigmatic_legacy.magnet_ring.enabled", "磁场状态：已开启");
        add("tooltip.enigmatic_legacy.magnet_ring.disabled", "磁场状态：已关闭");

        addItem(ModItems.DISLOCATION_RING, "转位之戒");

        add("tooltip.enigmatic_legacy.dislocation_ring.1", "瞬间收集 %s 格范围内的附近掉落物。");
        add("tooltip.enigmatic_legacy.dislocation_ring.3", "不能与磁力之戒同时佩戴。");

        add("tooltip.enigmatic_legacy.magnet_ring.2", "按下潜行键时不生效。");
        add("tooltip.enigmatic_legacy.dislocation_ring.2", "按下潜行键时不生效。");

        add("message.enigmatic_legacy.magnet_control.enabled", "%s已开启。");
        add("message.enigmatic_legacy.magnet_control.disabled", "%s已关闭。");
        add("message.enigmatic_legacy.magnet_ring.no_ring", "你没有佩戴磁力之戒或转位之戒。");

        add("gui.enigmatic_legacy.magnet_control.tooltip.enabled", "%s当前已开启。点击关闭。");
        add("gui.enigmatic_legacy.magnet_control.tooltip.disabled", "%s当前已关闭。点击开启。");

        addItem(ModItems.MONSTER_CHARM, "怪物猎人勋章");

        add("tooltip.enigmatic_legacy.monster_charm.1", "对亡灵生物造成的伤害提高 %s。");
        add("tooltip.enigmatic_legacy.monster_charm.2", "对敌对生物造成的伤害提高 %s。");
        add("tooltip.enigmatic_legacy.monster_charm.3", "提供 +1 抢夺。");
        add("tooltip.enigmatic_legacy.monster_charm.4", "怪物掉落经验翻倍。");

        addItem(ModItems.TREASURE_HUNTER_CHARM, "猎宝者护符");

        add("tooltip.enigmatic_legacy.treasure_hunter_charm.1", "佩戴时获得夜视。");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.2", "提供 +1 时运。");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.3", "挖掘速度提高 %s。");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.4", "右键可切换夜视。");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled", "夜视当前已开启。");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled", "夜视当前已关闭。");

        add("message.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled", "夜视已开启。");
        add("message.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled", "夜视已关闭。");

        // 血战沙场之证
        addItem(ModItems.BLOODSTAINED_VALOR_EMBLEM, "血战沙场之证");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.1", "每缺失 1% 生命，获得 +%s 攻击伤害。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.2", "每缺失 1% 生命，获得 +%s 攻击速度。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.3", "每缺失 1% 生命，获得 +%s 移动速度。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.4", "每缺失 1% 生命，获得 +%s 伤害抗性。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.5", "这些属性会根据你当前缺失的生命值提升。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.6", "越接近死亡，它的力量越强。");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.cursed_only", "只有七咒之戒的佩戴者才能使用此物。");
        // end


        // 超级海绵
        addItem(ModItems.MEGA_SPONGE, "超级海绵");
        add("tooltip.enigmatic_legacy.mega_sponge.1", "佩戴时会自动吸收附近水体。");
        add("tooltip.enigmatic_legacy.mega_sponge.2", "吸水半径：%s 格。");
        // end

        // 附魔师的珍珠
        addItem(ModItems.ENCHANTER_PEARL, "附魔师的珍珠");

        add("tooltip.enigmatic_legacy.enchanter_pearl.1", "佩戴时提供 +1 个护符栏位。");
        add("tooltip.enigmatic_legacy.enchanter_pearl.2", "允许佩戴者携带更多魔法饰品。");
        add("tooltip.enigmatic_legacy.enchanter_pearl.3", "额外栏位只会在此珍珠被佩戴时存在。");
        add("tooltip.enigmatic_legacy.enchanter_pearl.cursed_only", "只有七咒之戒的佩戴者才能使用此物。");
        // end

        // 莫测之眼
        add("item.enigmatic_legacy.enigmatic_eye_dormant", "休眠之眼");
        add("item.enigmatic_legacy.enigmatic_eye_active", "全知之眼");

        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.1", "它注视着，却仍在沉睡。");
        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.2", "右键以唤醒此眼。");
        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.3", "有些事物，最好永远不要被看见。");

        add("tooltip.enigmatic_legacy.enigmatic_eye.active.1", "%s 护符栏位");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.2", "%s 方块交互距离");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.3", "眼已苏醒，而它正在注视。");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.4", "只有已唤醒的眼才能被佩戴。");

        add("message.enigmatic_legacy.enigmatic_eye.awakening", "眼开始苏醒……");
        add("message.enigmatic_legacy.enigmatic_eye.awakened", "眼已苏醒。");
        // end

        // 全知之眼
        add("quote.enigmatic_legacy.enigmatic_eye.prefix", "[眼]");
        add("quote.enigmatic_legacy.enigmatic_eye.first_wear", "终于，盲者重新拥有了视界。");
        add("quote.enigmatic_legacy.enigmatic_eye.first_nether", "此地燃烧不息，但它并非最深的伤口。");
        add("quote.enigmatic_legacy.enigmatic_eye.first_end", "空洞的天空，死寂的王国。何其熟悉。");
        add("quote.enigmatic_legacy.enigmatic_eye.deep_underground", "岩石之下，古老之物仍记得你的脚步。");
        add("quote.enigmatic_legacy.enigmatic_eye.low_health", "你比自己想象中更接近沉寂。");
        add("quote.enigmatic_legacy.enigmatic_eye.midnight", "世界此刻很安静。仔细听。");

        add("quote.no_peril_1", "没有危险的任务，便谈不上完成后的荣耀。");
        add("quote.end_doorstep_1", "末地的门阶……\n如今，你与它只隔一步之遥。");
        add("quote.only_because_1", "你曾倒下，并不意味着你已经退缩。");
        add("quote.demise_is_1", "对你这样的半神而言，死亡不过是一时的不便。");
        add("quote.we_fall_1", "我们跌倒，是为了学会如何重新站起。");
        add("quote.you_will_endure_1", "你会承受这次失败，并从中学到东西。");
        add("quote.oblivion_rejects_1", "湮灭拒绝了你……\n趁你仍有机会，好好利用它。");
        add("quote.setback_1", "这是挫折，但不是败北。");
        add("quote.death_may_1", "死亡或许会阻碍你的征服，但只有破碎的意志才能真正让它停下。");
        add("quote.eternity_to_keep_1", "你有永恒的时间继续尝试。去做必须做的事。");
        add("quote.violence_calls_1", "暴力呼唤复仇。\n回去，将它奉还！");
        add("quote.immortal_1", "你是不朽的！回去，让那可悲的生物见识这一事实。");
        add("quote.appaling_presence_1", "躯壳已逝……但那骇人的存在感，将永远铭刻在此界之中。");
        add("quote.its_destruction_1", "它的毁灭只能算是微不足道的慰藉……\n考虑到它那可怖存在所暗示的一切。");

        add("quote.i_wandered_1", "是的，我曾无数次游荡于那片土地……");
        add("quote.i_wandered_2", "那是对一切曾经存在之物，以及一切本可能存在之物的荒凉提醒。");

        add("quote.another_demigod_1", "又一位半神徘徊在这片被遗弃的土地上……");
        add("quote.another_demigod_2", "让我伴随你的旅途吧。也许，你能做到其他人未能做到的事。");

        add("quote.another_eon_1", "又一个纪元，又一个游荡者。\n又一个将被讲述的故事……");
        add("quote.another_eon_2", "我会怀着极大的好奇见证你的故事。");

        add("quote.perhaps_you_1", "也许，你会成为揭开这片土地久远遗史之人……");
        add("quote.perhaps_you_2", "若真如此，我必须见证你的成就。");

        add("quote.sulfur_air_1", "空气中弥漫着硫磺，烧灼岩石与血肉的刺鼻气味……");
        add("quote.sulfur_air_2", "此地，确是一片地狱。");

        add("quote.tortured_rocks_1", "受尽折磨的岩石，苍白如骨，悬挂在无尽而吞噬一切的漆黑之上。");
        add("quote.tortured_rocks_2", "末地……\n几乎就像一场高热中的梦魇。");

        add("quote.breathes_relieved_1", "没有了那恐怖存在的沉重压迫，世界终于松了一口气……");
        add("quote.breathes_relieved_2", "至少暂时如此。");

        add("quote.whether_it_is_1", "无论你反复将凋灵从坟墓中唤回又送回，是为了力量，还是为了救赎……");
        add("quote.whether_it_is_2", "……至少，你给了那该死的生物一个目的。");

        add("quote.poor_creature_1", "可怜的生灵……\n她是同族最后的遗孤，被束缚在那被神遗弃的岛屿上……");
        add("quote.poor_creature_2", "……位于一片虚无的中央。让她得到应有的安息吧。");

        add("quote.horrible_existence_1", "何等可怖的存在……\n从死亡中被带回，只为再一次被杀死。");
        add("quote.horrible_existence_2", "你必定已经没有半点怜悯。");

        add("quote.countless_dead_1", "无数亡魂融合成了如此骇人的憎恶之物，以至于言语都无法描述它的形态。");
        add("quote.countless_dead_2", "仅仅是这污秽生物的存在，便已玷污了大地。");

        add("quote.with_dragons_1", "随着巨龙即将消亡，此地似乎比以往更加空洞，也更加失去了意义。");
        add("quote.with_dragons_2", "你会继续游荡在这些破碎而荒芜的土地上……");
        add("quote.with_dragons_3", "……还是回到主世界所提供的那层理智与生机的伪装之中？");
        add("quote.with_dragons_4", "当然，我们都知道答案……\n你无法抗拒探索未知，无论是现在，还是以后。");

        add("quote.terrifying_form_1", "……它那可怖的形体可以被毁灭，但它的灵魂不行。");
        add("quote.terrifying_form_2", "它背负着与你并无二致的诅咒……\n不朽的诅咒。");

        add("quote.toll_paid_1", "代价已付，你终于从那膨胀的憎恨与悲伤中解脱……");
        add("quote.toll_paid_2", "……那些被囚禁在戒指中的憎恨与悲伤。");
        // end

        // 启示之证
        addItem(ModItems.THE_ACKNOWLEDGMENT, "启示之证");

        add("book.enigmatic_legacy.landing_text", "辽阔的土地在你面前延展，等待你揭开被遗忘的秘密与神秘遗物。");

        add("tooltip.enigmatic_legacy.the_acknowledgment.1", "记录神秘遗物知识的指南书。");
        add("tooltip.enigmatic_legacy.the_acknowledgment.2", "右键打开《启示之证》。");
        add("tooltip.enigmatic_legacy.the_acknowledgment.shift.1", "改变第四诅咒。");
        add("tooltip.enigmatic_legacy.the_acknowledgment.shift.2", "第四诅咒削弱 %s。");
        // end

        // 兽友指南
        addItem(ModItems.ANIMAL_GUIDEBOOK, "兽友指南");
        add("tooltip.enigmatic_legacy.animal_guidebook.1", "放在物品栏中时，你的攻击不会");
        add("tooltip.enigmatic_legacy.animal_guidebook.2", "对动物造成伤害。");
        add("tooltip.enigmatic_legacy.animal_guidebook.3", "你周围的疣猪兽会保持被动。");
        add("tooltip.enigmatic_legacy.animal_guidebook.4", "第二诅咒的修正：");
        add("tooltip.enigmatic_legacy.animal_guidebook.5", "- 可驯服动物会对你保持中立。");
        add("tooltip.enigmatic_legacy.animal_guidebook.creative.1", "右键任意实体，查看它是否会被");
        add("tooltip.enigmatic_legacy.animal_guidebook.creative.2", "视为可削弱诅咒效果的可驯服动物。");
        add("message.enigmatic_legacy.animal_guidebook.tamable", "是的，这个动物似乎可以驯服。");
        add("message.enigmatic_legacy.animal_guidebook.not_tamable", "不，这个动物不会被视为可驯服。");
        // end

        // 野猎指南
        addItem(ModItems.HUNTER_GUIDEBOOK, "野猎指南");
        add("tooltip.enigmatic_legacy.hunter_guidebook.1", "放在物品栏中时，你的宠物在");
        add("tooltip.enigmatic_legacy.hunter_guidebook.2", "距离你 %1$s 格内受到的所有伤害");
        add("tooltip.enigmatic_legacy.hunter_guidebook.3", "都会转移到你身上。");
        add("tooltip.enigmatic_legacy.hunter_guidebook.4", "如果你同时持有兽友指南，");
        add("tooltip.enigmatic_legacy.hunter_guidebook.5", "转移伤害会降低 %1$s。");
        // end

        // 术石
        add("curios.identifier.spellstone", "术石");

        // 魔像之心
        addItem(ModItems.GOLEM_HEART, "魔像之心");

        add("tooltip.enigmatic_legacy.spellstone.passive", "被动能力：");
        add("tooltip.enigmatic_legacy.golem_heart.1", "+%s 护甲。");
        add("tooltip.enigmatic_legacy.golem_heart.2", "如果你没有穿戴任何护甲：");
        add("tooltip.enigmatic_legacy.golem_heart.3", "+%s 护甲与 +%s 护甲韧性。");
        add("tooltip.enigmatic_legacy.golem_heart.4", "%s 爆炸伤害抗性。");
        add("tooltip.enigmatic_legacy.golem_heart.5", "%s 近战伤害抗性。");
        add("tooltip.enigmatic_legacy.golem_heart.6", "%s 击退抗性。");
        add("tooltip.enigmatic_legacy.golem_heart.7", "免疫挤压、窒息、仙人掌与钟乳石刺伤害。");
        add("tooltip.enigmatic_legacy.golem_heart.8", "受到的魔法伤害变为 %s 倍。");
        // end

        // 天使之祝
        addItem(ModItems.ANGEL_BLESSING, "天使之祝");

        add("key.enigmatic_legacy.use_spellstone", "使用术石");

        add("tooltip.enigmatic_legacy.spellstone.active", "主动能力：");
        add("tooltip.enigmatic_legacy.spellstone.cooldown", "冷却：%s 秒。");
        add("tooltip.enigmatic_legacy.angel_blessing.active", "向视野方向加速。");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.1", "免疫摔落和碰撞伤害。");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.2", "%s 概率反射接近的弹射物。");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.3", "你自己射出的弹射物会被加速。");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.4", "按下术石快捷键可激活主动冲刺。");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.5", "受到的凋零和虚空伤害增加。");
        // end

        // 海洋意志
        addItem(ModItems.OCEAN_STONE, "海洋意志");

        add("tooltip.enigmatic_legacy.ocean_stone.active",
                "消耗经验，在主世界召来雷暴。");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.1",
                "受到水生生物伤害时减免 %s。");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.2",
                "在水下获得水下呼吸、水下夜视，并大幅改善水下挖掘速度。");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.3",
                "游泳速度提高 %s，并抵消水下重力。");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.4",
                "受到的火焰伤害提高。");

        add("message.enigmatic_legacy.ocean_stone.wrong_dimension",
                "海洋的意志无法抵达这个维度。");
        add("message.enigmatic_legacy.ocean_stone.already_thundering",
                "风暴已经在怒吼。");
        add("message.enigmatic_legacy.ocean_stone.not_enough_xp",
                "你的经验不足以呼唤风暴。");
        add("message.enigmatic_legacy.ocean_stone.summoned",
                "海洋回应了你的意志。");
        // end

        // 烈焰之核
        addItem(ModItems.BLAZING_CORE, "烈焰之核");

        add("tooltip.enigmatic_legacy.blazing_core.active",
                "无主动效果。");
        add("tooltip.enigmatic_legacy.blazing_core.passive.1",
                "免疫火焰伤害，并会自动熄灭自身燃烧。");
        add("tooltip.enigmatic_legacy.blazing_core.passive.2",
                "暂时免疫岩浆伤害；在岩浆中停留过久后会过热并开始受伤。");
        add("tooltip.enigmatic_legacy.blazing_core.passive.3",
                "受到近战攻击时，对攻击者造成 %s 点火焰反馈伤害并点燃攻击者。");
        add("tooltip.enigmatic_legacy.blazing_core.passive.4",
                "大多数状态效果持续时间变为 %s；抗火类效果持续时间翻倍。");
        add("tooltip.enigmatic_legacy.blazing_core.passive.5",
                "来自水生生物的伤害变为 %s 倍。");
        // end

        // 星云之眼
        addItem(ModItems.EYE_OF_NEBULA, "星云之眼");

        add("message.enigmatic_legacy.eye_of_nebula.no_target", "星云之眼没有找到可传送的目标。");

        add("tooltip.enigmatic_legacy.eye_of_nebula.active", "将你传送到你注视着的生物背后。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.1", "增加 %s 魔法伤害。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.2", "获得 %s 魔法抗性。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.3", "受到攻击时，有 %s 概率传送到别处，并免疫本次伤害。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.4", "使用主动技能后，下一次攻击额外造成 %s 伤害。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.5", "免疫星云之眼传送后的摔落型传送伤害。");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.6", "当你在水中时，受到的所有伤害翻倍。");
        // end

        // 虚空珍珠
        addItem(ModItems.VOID_PEARL, "虚空珍珠");

        add("tooltip.enigmatic_legacy.void_pearl.passive.1", "不再需要呼吸空气，并免疫溺水伤害。");
        add("tooltip.enigmatic_legacy.void_pearl.passive.2", "免疫绝大多数状态效果，但禁忌之果等特殊效果除外。");
        add("tooltip.enigmatic_legacy.void_pearl.passive.3", "你的攻击会使目标 %s。");
        add("tooltip.enigmatic_legacy.void_pearl.passive.4", "每 %s 秒，对 %s 格内暴露在黑暗中的生物造成 %s 点虚空伤害，并施加 %s。");
        add("tooltip.enigmatic_legacy.void_pearl.passive.5", "拥有 %s 的概率抵挡致命伤害。");
        add("tooltip.enigmatic_legacy.void_pearl.passive.6", "每 tick 熄灭自身火焰，并免疫墙内窒息伤害。");

        add("death.attack.enigmatic_legacy.darkness", "%1$s 被黑暗吞噬了");
        add("death.attack.enigmatic_legacy.darkness.player", "%1$s 被 %2$s 身边的虚空黑暗吞噬了");
        // end

        // 非欧立方
        addItem(ModItems.THE_CUBE, "非欧立方");

        add("message.enigmatic_legacy.non_euclidean_cube.no_structure", "非欧立方没有找到可传送的结构。");
        add("message.enigmatic_legacy.non_euclidean_cube.teleported", "空间折叠，非欧几何将你送往未知结构。");

        add("tooltip.enigmatic_legacy.non_euclidean_cube.active", "将你传送到当前维度的随机结构附近。");

        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.1", "%s 疾跑速度，%s 游泳速度，%s 挖掘速度，%s 攻击速度。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.2", "%s 时运等级，%s 幸运。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.3", "无视高于 %s 点的伤害；佩戴七咒之戒时改为无视高于 %s 点的伤害。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.4", "%s 概率反弹投射物，或将伤害返还给攻击者。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.5", "受到非投射物伤害时，逐步给予攻击者 %s。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.6", "击败生物后获得随机正面效果，但不会获得缓降。");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.7", "免疫 %s、挤压、摔落、碰撞、荆棘、传送、火焰与熔岩伤害，并可在濒死时折叠空间保命。");
        // end

        // 创造之心
        addItem(ModItems.HEART_OF_CREATION, "创造之心");

        add("message.enigmatic_legacy.heart_of_creation.no_targets", "创造之心没有找到可审判的敌人。");

        add("tooltip.enigmatic_legacy.heart_of_creation.active", "对 %s 格范围内的所有敌人降下闪电，造成 %s 点伤害并附加 %s。");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.1", "免疫窒息、坠落、碰撞、挤压、饥饿、虚空、荆棘、火焰和岩浆伤害。");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.2", "免疫大部分 %s。");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.3", "免疫击退，并给予你飞行能力。");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.4", "补偿飞行时的挖掘速度损失。");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.5", "装备它或将其放在物品栏中时，你将不朽；");
        // end

        // 永恒智慧卷轴
        addItem(ModItems.XP_SCROLL, "永恒智慧卷轴");

        add("curios.identifier.scroll", "奥秘卷轴");
        add("curios.modifiers.scroll", "装备在奥秘卷轴栏时：");

        add("key.enigmatic_legacy.scroll", "使用奥秘卷轴");

        add("message.enigmatic_legacy.xp_scroll.enabled", "永恒智慧卷轴已启用。");
        add("message.enigmatic_legacy.xp_scroll.disabled", "永恒智慧卷轴已停用。");
        add("message.enigmatic_legacy.xp_scroll.mode_absorption", "永恒智慧卷轴切换为吸收模式。");
        add("message.enigmatic_legacy.xp_scroll.mode_extraction", "永恒智慧卷轴切换为提取模式。");
        add("message.enigmatic_legacy.xp_scroll.extracted", "永恒智慧卷轴已返还所有存储经验。");

        add("tooltip.enigmatic_legacy.xp_scroll.stored", "已存储经验：%s");
        add("tooltip.enigmatic_legacy.xp_scroll.active", "状态：已启用");
        add("tooltip.enigmatic_legacy.xp_scroll.inactive", "状态：已停用");
        add("tooltip.enigmatic_legacy.xp_scroll.mode_absorption", "模式：吸收");
        add("tooltip.enigmatic_legacy.xp_scroll.mode_extraction", "模式：提取");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.1", "Shift + 右键切换吸收 / 提取模式。");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.2", "按卷轴快捷键启用或停用卷轴。");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.3", "启用时会收集 %s 格内的经验球。");
        // end

        // 天堂之礼
        addItem(ModItems.HEAVEN_SCROLL, "天堂之礼");

        add("tooltip.enigmatic_legacy.heaven_scroll.1", "在激活信标范围内给予自由飞行能力。");
        add("tooltip.enigmatic_legacy.heaven_scroll.2", "飞行时会缓慢消耗经验值，并提高挖掘速度。");
        add("tooltip.enigmatic_legacy.heaven_scroll.3", "离开信标范围后会失去飞行，并获得 %s 秒缓降。");
        add("tooltip.enigmatic_legacy.heaven_scroll.4", "在信标范围内且未丧失飞行能力时免疫摔落伤害。");
        // end

        // 千咒卷轴
        addItem(ModItems.CURSED_SCROLL, "千咒卷轴");

        add("tooltip.enigmatic_legacy.cursed_scroll.1", "根据你装备物品上的诅咒附魔项数提供加成。");
        add("tooltip.enigmatic_legacy.cursed_scroll.2", "主手、副手、盔甲与饰品栏中的诅咒附魔都会被统计。");
        add("tooltip.enigmatic_legacy.cursed_scroll.3", "每个诅咒附魔只按 %s 项计算，附魔等级不会额外提高倍率。");
        add("tooltip.enigmatic_legacy.cursed_scroll.4", "七咒之戒会被额外视为 %s 项诅咒。");

        add("tooltip.enigmatic_legacy.cursed_scroll.attack", "每项诅咒：+%s 攻击伤害");
        add("tooltip.enigmatic_legacy.cursed_scroll.mining", "每项诅咒：+%s 挖掘速度");
        add("tooltip.enigmatic_legacy.cursed_scroll.healing", "每项诅咒：+%s 生命恢复");

        add("tooltip.enigmatic_legacy.cursed_scroll.cursed_only", "只有承受七咒之人才能使用。");

        add("tooltip.enigmatic_legacy.cursed_scroll.current.factor", "当前诅咒项数：%s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.attack", "当前攻击伤害：+%s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.mining", "当前挖掘速度：+%s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.healing", "当前生命恢复：+%s");
        // end

        // 创造者的恩赐
        addItem(ModItems.FABULOUS_SCROLL, "创造者的恩赐");

        add("tooltip.enigmatic_legacy.fabulous_scroll.1", "给予你自由飞行的能力。");
        add("tooltip.enigmatic_legacy.fabulous_scroll.2", "飞行会快速消耗经验值。");
        add("tooltip.enigmatic_legacy.fabulous_scroll.3", "在激活信标范围内飞行不消耗经验。");
        add("tooltip.enigmatic_legacy.fabulous_scroll.4", "补偿飞行时的挖掘速度损失，并在未丧失飞行能力时免疫摔落伤害。");
        // end

        // 无尽贪婪契约
        addItem(ModItems.AVARICE_SCROLL, "无尽贪婪契约");

        add("tooltip.enigmatic_legacy.avarice_scroll.1", "%s 时运等级。");
        add("tooltip.enigmatic_legacy.avarice_scroll.2", "猪灵对你保持中立，即使你承受第二诅咒。");
        add("tooltip.enigmatic_legacy.avarice_scroll.3", "猪灵以物易物收益增加 %s。");
        add("tooltip.enigmatic_legacy.avarice_scroll.4", "杀死任意生物有 %s 概率额外掉落 %s 枚绿宝石。");
        add("tooltip.enigmatic_legacy.avarice_scroll.5", "村民交易提供 %s 折扣。");
        add("tooltip.enigmatic_legacy.avarice_scroll.cursed_only", "只有承受七咒之人才能使用。");
        // end

        // 深渊之心
        addItem(ModItems.ABYSSAL_HEART, "深渊之心");

        add("tooltip.enigmatic_legacy.abyssal_heart.short", "来自终末深渊的心脏。");
        add("tooltip.enigmatic_legacy.abyssal_heart.1", "佩戴七咒之戒击败末影龙后生成。");
        add("tooltip.enigmatic_legacy.abyssal_heart.2", "它会悬浮在末影龙死亡的位置。");
        add("tooltip.enigmatic_legacy.abyssal_heart.3", "只有真正承受七咒折磨之人才能触碰它。");
        add("tooltip.enigmatic_legacy.abyssal_heart.4", "你必须在七咒之戒的折磨下度过总游戏时间的 99.5%。");

        add("message.enigmatic_legacy.abyssal_heart.unworthy", "深渊之心拒绝了你，当前七咒佩戴时间百分比：%s。");
        // end

        // 超维之眼
        addItem(ModItems.EXTRADIMENSIONAL_EYE, "超维之眼");

        add("tooltip.enigmatic_legacy.extradimensional_eye.1", "按住 Shift + 右键绑定你当前所在的位置。");
        add("tooltip.enigmatic_legacy.extradimensional_eye.2", "手持它左键点击生物，可将目标传送到绑定位置。");
        add("tooltip.enigmatic_legacy.extradimensional_eye.3", "只有目标和绑定点处于同一维度时才会生效。");
        add("tooltip.enigmatic_legacy.extradimensional_eye.4", "使用后会消耗此物品。");
        add("tooltip.enigmatic_legacy.extradimensional_eye.location", "已绑定位置：");
        add("tooltip.enigmatic_legacy.extradimensional_eye.x", "X：%s");
        add("tooltip.enigmatic_legacy.extradimensional_eye.y", "Y：%s");
        add("tooltip.enigmatic_legacy.extradimensional_eye.z", "Z：%s");
        add("tooltip.enigmatic_legacy.extradimensional_eye.dimension", "维度：%s");

        add("message.enigmatic_legacy.extradimensional_eye.bound", "超维之眼已绑定当前位置：%s, %s, %s");
        add("message.enigmatic_legacy.extradimensional_eye.not_bound", "这个超维之眼还没有绑定位置。");
        add("message.enigmatic_legacy.extradimensional_eye.wrong_dimension", "目标不在绑定位置所在的维度。");
        // end

        // 求知之书
        addItem(ModItems.ENCHANTMENT_TRANSPOSER, "求知之书");

        add("tooltip.enigmatic_legacy.enchantment_transposer.1", "与任意带附魔的物品一起合成。");
        add("tooltip.enigmatic_legacy.enchantment_transposer.2", "会吞噬该物品，并将其全部附魔转移到一本附魔书上。");
        add("tooltip.enigmatic_legacy.enchantment_transposer.3", "知识从不消失，它只是换了一种载体。");
        // end

        // 噬咒之书
        addItem(ModItems.CURSE_TRANSPOSER, "噬咒之书");
        add("tooltip.enigmatic_legacy.curse_transposer.1", "与任意除附魔书以外的附魔物品合成。");
        add("tooltip.enigmatic_legacy.curse_transposer.2", "会消耗该物品，并将其全部诅咒附魔转移到一本附魔书上。");
        add("tooltip.enigmatic_legacy.curse_transposer.3", "恶咒被吞噬，而非净化。");
        add("tooltip.enigmatic_legacy.curse_transposer.cursed_only", "只有承受七咒之人才能使用该物品。");
        // end

        // 终极夜视药水
        add("item.minecraft.potion.effect.ultimate_night_vision", "终极夜视药水");
        add("item.minecraft.splash_potion.effect.ultimate_night_vision", "喷溅型终极夜视药水");
        add("item.minecraft.lingering_potion.effect.ultimate_night_vision", "滞留型终极夜视药水");
        add("item.minecraft.tipped_arrow.effect.ultimate_night_vision", "终极夜视之箭");
        // end

        // 以太阔剑
        addItem(ModItems.ETHERIUM_SWORD, "以太阔剑");

        add("tooltip.enigmatic_legacy.etherium_sword.1", "右键使用以向后跃退。");
        add("tooltip.enigmatic_legacy.etherium_sword.2", "冷却时间：%s 秒。");
        add("tooltip.enigmatic_legacy.etherium_sword.3", "副手持盾时不会触发主动能力。");
        // end

        // 以太镐
        addItem(ModItems.ETHERIUM_PICKAXE, "以太镐");

        add("tooltip.enigmatic_legacy.etherium_pickaxe.1", "挖掘时会破坏 3×3×1 范围内的方块。");
        add("tooltip.enigmatic_legacy.etherium_pickaxe.2", "潜行挖掘时，区域挖掘不会生效。");
        add("tooltip.enigmatic_legacy.etherium_pickaxe.3", "潜行右键可以切换区域挖掘效果。");
        add("tooltip.enigmatic_legacy.etherium_pickaxe.4", "默认启用区域挖掘。");
        add("tooltip.enigmatic_legacy.etherium_pickaxe.disabled", "区域挖掘当前已关闭。");

        add("message.enigmatic_legacy.etherium_pickaxe.area_enabled", "以太镐区域挖掘：已启用");
        add("message.enigmatic_legacy.etherium_pickaxe.area_disabled", "以太镐区域挖掘：已关闭");
        // end

        // 以太锹
        addItem(ModItems.ETHERIUM_SHOVEL, "以太锹");

        add("tooltip.enigmatic_legacy.etherium_shovel.1", "挖掘时会破坏 3×3×1 范围内的方块。");
        add("tooltip.enigmatic_legacy.etherium_shovel.2", "潜行挖掘时，区域挖掘不会生效。");
        add("tooltip.enigmatic_legacy.etherium_shovel.3", "潜行右键可以切换区域挖掘效果。");
        add("tooltip.enigmatic_legacy.etherium_shovel.4", "默认启用区域挖掘。");
        add("tooltip.enigmatic_legacy.etherium_shovel.disabled", "区域挖掘当前已关闭。");

        add("message.enigmatic_legacy.etherium_shovel.area_enabled", "以太锹区域挖掘：已启用");
        add("message.enigmatic_legacy.etherium_shovel.area_disabled", "以太锹区域挖掘：已关闭");
        // end

        // 以太套装
        addItem(ModItems.ETHERIUM_HELMET, "以太头盔");
        addItem(ModItems.ETHERIUM_CHESTPLATE, "以太胸甲");
        addItem(ModItems.ETHERIUM_LEGGINGS, "以太护腿");
        addItem(ModItems.ETHERIUM_BOOTS, "以太靴子");

        add("tooltip.enigmatic_legacy.etherium_armor.single", "单件效果：穿戴时不显示。");
        add("tooltip.enigmatic_legacy.etherium_armor.set.1", "套装效果：生命值低于 40% 时生成强力护盾。");
        add("tooltip.enigmatic_legacy.etherium_armor.set.2", "护盾会反弹大多数弹射物。");
        add("tooltip.enigmatic_legacy.etherium_armor.set.3", "护盾激活时获得 50% 伤害抗性。");
        add("tooltip.enigmatic_legacy.etherium_armor.set.4", "攻击你的生物会被击退。");
        addItem(ModItems.MAJESTIC_ELYTRA, "壮丽鞘翅");
        add("tooltip.enigmatic_legacy.majestic_elytra.1", "拥有惊人耐久的华美之翼。");
        add("tooltip.enigmatic_legacy.majestic_elytra.2", "滑翔时按住跳跃键可向前加速。");
        add("tooltip.enigmatic_legacy.majestic_elytra.3", "可使用以太锭修复。");
        // end

        // 以太斧
        addItem(ModItems.ETHERIUM_AXE, "以太斧");

        add("tooltip.enigmatic_legacy.etherium_axe.1", "以太制成的战斧,比普通斧头轻便");
        // end

        // 行刑者之斧
        addItem(ModItems.AXE_OF_EXECUTIONER, "行刑者之斧");

        add("tooltip.enigmatic_legacy.axe_of_executioner.1", "击杀可斩首怪物时，有 %s 的概率砍下其头颅。");

        add("tooltip.enigmatic_legacy.axe_of_executioner.2", "每级抢夺都会额外增加 %s 斩首概率。");
        // end

        // 修补混合物
        addItem(ModItems.MENDING_MIXTURE, "修补混合物");

        add("tooltip.enigmatic_legacy.mending_mixture.1",
                "在工作台中与受损物品合成，可以完全修复其耐久。");

        add("tooltip.enigmatic_legacy.mending_mixture.2",
                "修复后的物品会保留附魔、名称和其它数据。");

        add("tooltip.enigmatic_legacy.mending_mixture.3",
                "不要喝下它。");
        // end

        // Wayfinder of the Damned / 被诅咒者的寻路指针
        addItem(ModItems.WAYFINDER_OF_THE_DAMNED, "被诅咒者的寻路指针");

        add("tooltip.enigmatic_legacy.wayfinder_of_the_damned.1",
                "只有承受七咒之人才能使用。");

        add("tooltip.enigmatic_legacy.wayfinder_of_the_damned.2",
                "指向当前维度内最近的灵魂水晶。");

        // 按住 Shift 查看详情
        add("tooltip.enigmatic_legacy.hold_shift", "§5按住 §6Shift§5 查看详情。");
        // end

        // The Ender Slayer / 末影之屠
        addItem(ModItems.ENDER_SLAYER, "末影之屠");

        add("tooltip.enigmatic_legacy.ender_slayer.1",
                "一柄为终结末地眷族而铸成的利刃。");

        add("tooltip.enigmatic_legacy.ender_slayer.2",
                "它的锋刃渴求着末影之血。");

        add("tooltip.enigmatic_legacy.ender_slayer.3",
                "对末地生物造成 %s 伤害。");

        add("tooltip.enigmatic_legacy.ender_slayer.4",
                "对末地生物造成 %s 击退。");

        add("tooltip.enigmatic_legacy.ender_slayer.5",
                "命中末影人和潜影贝时，会暂时压制它们的传送。");

        add("tooltip.enigmatic_legacy.ender_slayer.6",
                "命中玩家时，会压制末影珍珠、召回药水、扭曲魔镜、");

        add("tooltip.enigmatic_legacy.ender_slayer.7",
                "星云之眼和非欧立方的传送能力。");

        add("tooltip.enigmatic_legacy.ender_slayer.8",
                "在末地中，满蓄力攻击末影人会变得极其致命。");

        // Bulwark of Blazing Pride / 烈焰之傲壁垒
        addItem(ModItems.BULWARK_OF_BLAZING_PRIDE, "烈焰之傲壁垒");

        add("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.1",
                "一面由傲慢与炼狱烈焰铸成的盾牌。");

        add("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.2",
                "只有承受七咒之人才能举起它。");

        add("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.3",
                "手持时会熄灭你身上的火焰，但身处岩浆中时不会生效。");

        add("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.4",
                "可以使用黑曜石修复。");

        add("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.5",
                "举盾时，来自背后的攻击会造成更高伤害。");

        // The Voracious Pan / 饕餮之锅
        addItem(ModItems.VORACIOUS_PAN, "饕餮之锅");

        add("tooltip.enigmatic_legacy.voracious_pan.lore",
                "它永远饥饿。");

        add("tooltip.enigmatic_legacy.voracious_pan.1",
                "一口会从猎物身上吞噬力量的诅咒之锅。");

        add("tooltip.enigmatic_legacy.voracious_pan.2",
                "也可以举起它来格挡来袭的攻击。");

        add("tooltip.enigmatic_legacy.voracious_pan.lifesteal",
                "将造成伤害的 %s 转化为生命恢复。");

        add("tooltip.enigmatic_legacy.voracious_pan.hungersteal",
                "命中时夺取 %s 点饥饿值。");

        add("tooltip.enigmatic_legacy.voracious_pan.3",
                "持有它太久，会唤醒不断增长的饥饿。");

        add("tooltip.enigmatic_legacy.voracious_pan.4",
                "这口锅会记住它吞噬过的每一种不同生物。");

        add("tooltip.enigmatic_legacy.voracious_pan.damage_gain",
                "每种独特击杀提供 %s 攻击伤害。");

        add("tooltip.enigmatic_legacy.voracious_pan.armor_gain",
                "每种独特击杀提供 %s 护甲。");

        add("tooltip.enigmatic_legacy.voracious_pan.kills",
                "独特击杀：%s / %s");

        add("tooltip.enigmatic_legacy.voracious_pan.kills_max",
                "这口锅已无法继续成长。");

        add("message.enigmatic_legacy.voracious_pan_buff",
                "饕餮之锅变得更强了。");
        // 饕餮之锅效果名。
        // Growing Hunger 在中文资料中对应“无止饥饿”。
        // Growing Bloodlust 按同一命名风格翻译为“无止嗜血”。
        add("effect.enigmatic_legacy.growing_hunger", "无止饥饿");
        add("effect.enigmatic_legacy.growing_bloodlust", "无止嗜血");

        // The Twist / 倒转之启
        addItem(ModItems.THE_TWIST, "倒转之启");

        add("tooltip.enigmatic_legacy.the_twist.1",
                "在夺走一切的道路上，");

        add("tooltip.enigmatic_legacy.the_twist.2",
                "没有比知晓更好的方式……");

        add("tooltip.enigmatic_legacy.the_twist.4",
                "第四诅咒的修正：");

        add("tooltip.enigmatic_legacy.the_twist.5",
                "- 始终造成全额伤害。");

        add("tooltip.enigmatic_legacy.the_twist.6",
                "对 Boss 和玩家造成 %s 伤害。");

        add("tooltip.enigmatic_legacy.the_twist.7",
                "%s 击退");

        // The Infinitum / 无止之言
        addItem(ModItems.THE_INFINITUM, "无止之言");
        add("tooltip.enigmatic_legacy.the_infinitum.1", "深渊会回望最无畏的凝视。");
        add("tooltip.enigmatic_legacy.the_infinitum.2", "第四诅咒的修正：");
        add("tooltip.enigmatic_legacy.the_infinitum.3", "- 始终造成全额伤害。");
        add("tooltip.enigmatic_legacy.the_infinitum.4", "对 Boss 和玩家造成 %s 伤害。");
        add("tooltip.enigmatic_legacy.the_infinitum.5", "%s 击退");
        add("tooltip.enigmatic_legacy.the_infinitum.6", "%s 生命偷取");
        add("tooltip.enigmatic_legacy.the_infinitum.7", "可以承载大多数剑类附魔。");
        add("tooltip.enigmatic_legacy.the_infinitum.8", "每次攻击都会施加恐怖的负面效果。");
        add("tooltip.enigmatic_legacy.the_infinitum.9", "手持时，有 %s 概率使本应致命的");
        add("tooltip.enigmatic_legacy.the_infinitum.10", "伤害无法杀死你。");


        // 轻蔑之约 / The Testament of Contempt
        addItem(ModItems.ELDRITCH_AMULET, "轻蔑之约");

        add("tooltip.enigmatic_legacy.eldritch_amulet.short",
                "一枚被深渊污染的完美护符。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.1",
                "一枚自愿舍弃纯净的完美护符。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.intro.1",
                "它继承了飞升护符的全部力量。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.intro.2",
                "同时，它也会将你的轻蔑化为凝视中的压迫。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.gaze.title",
                "凝视效果：");

        add("tooltip.enigmatic_legacy.eldritch_amulet.2",
                "被你凝视的生物会受到 %s。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.3",
                "被你凝视的生物会受到 %s。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.6",
                "被你凝视的生物会受到 %s。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.7",
                "凝视范围：%s 格，影响半径：%s 格。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.death.title",
                "死亡保护：");

        add("tooltip.enigmatic_legacy.eldritch_amulet.4",
                "死亡时保留你的背包。");

        add("tooltip.enigmatic_legacy.eldritch_amulet.5",
                "带有消失诅咒的物品仍会消失。");

        // 轻蔑之约：佩戴属性。数字全部由 Java 传入，方便单独染成金色。
        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.attack_total",
                "%s 攻击伤害");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.aqua",
                "疾跑时 %s 移动速度");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.violet",
                "%s 概率偏转来袭弹射物");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.magenta",
                "%s 重力");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.green",
                "%s 挖掘效率");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.blue",
                "%s 游泳速度");

        add("tooltip.enigmatic_legacy.eldritch_amulet.modifier.lifesteal_total",
                "%s 生命偷取");

        // 魔法石英戒指 / Magic Quartz Ring
        addItem(ModItems.MAGIC_QUARTZ_RING, "魔法石英戒指");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.1",
                "地狱石英与青金石引导出稳定的魔法防护。");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.2",
                "佩戴时，它会强化你的防御与幸运，并削弱部分魔法伤害。");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.modifier.magic_resistance",
                "%s 魔法伤害抗性");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.modifier.armor",
                "%s 护甲");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.modifier.luck",
                "%s 幸运");

        add("tooltip.enigmatic_legacy.magic_quartz_ring.limit",
                "同一时间只能佩戴一个魔法石英戒指。");

        addAdvancementTranslations();


        // 飞升护符 / Amulet of Ascension
        addItem(ModItems.ASCENSION_AMULET, "飞升护符");

        add("tooltip.enigmatic_legacy.ascension_amulet.1",
                "命运定义了你的起点。");

        add("tooltip.enigmatic_legacy.ascension_amulet.2",
                "而你将亲手塑造自己的终点。");

        add("tooltip.enigmatic_legacy.ascension_amulet.3",
                "融合了全部七色神秘护身符的特质。");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.red",
                "%s 攻击伤害");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.aqua",
                "疾跑时 %s 移动速度");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.violet",
                "%s 概率偏转来袭弹射物");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.magenta",
                "%s 重力");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.green",
                "%s 挖掘效率");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.black",
                "%s 生命偷取");

        add("tooltip.enigmatic_legacy.ascension_amulet.modifier.blue",
                "%s 游泳速度");

        add("tooltip.enigmatic_legacy.ascension_amulet.no_vessel",
                "灵魂水晶与超维容器的死亡恢复逻辑已由七咒之戒处理。");

        addItem(ModItems.SCORCHED_CHARM, "阳灼护符");

        add("tooltip.enigmatic_legacy.scorched_charm.1",
                "免疫大多数火焰伤害，并允许你行走在岩浆之上。");

        add("tooltip.enigmatic_legacy.scorched_charm.2",
                "接触岩浆时，每秒恢复 2 点生命。");

        add("tooltip.enigmatic_legacy.scorched_charm.3",
                "攻击着火目标时，恢复造成伤害的 20%。");

        add("tooltip.enigmatic_legacy.scorched_charm.4",
                "有 10% 概率抵御下一次伤害；接触岩浆时概率翻倍。");

        add("tooltip.enigmatic_legacy.scorched_charm.5",
                "下蹲时可以潜入岩浆，并在岩浆中拥有更高能见度。");

        // 七咒折磨 99.5% 使用资格提示，所有需要该资格的物品共用这一组语言 key。
        add("tooltip.enigmatic_legacy.cursed_suffering.requirement", "你需要在七咒之戒的折磨下度过总游戏时间的 %s 才能使用该物品。");
        add("tooltip.enigmatic_legacy.cursed_suffering.current_percentage", "当前受七咒折磨的时间百分比：%s");

        // Tooltip 通用词条：用于局部染色
        add("tooltip.enigmatic_legacy.term.negative_effects", "负面效果");
        add("tooltip.enigmatic_legacy.term.severe_negative_effects", "严重负面效果");
    }

    private void addAdvancementTranslations() {
        add("advancementTab.enigmatic_legacy", "§5神秘遗物");
        add("advancementTab.enigmatic_legacy.desc", "昔日残遗");
        add("advancements.toast.task", "§5达成进度！");
        add("advancements.toast.goal", "§5达成目标！");
        add("advancements.toast.challenge", "§5完成挑战！");

        addAdvancement("discoverSpellstone", "元素之力", "找到你的第一块术石");
        addAdvancement("discoverScroll", "专业的巫师", "获得你的第一个奥秘卷轴");
        addAdvancement("discoverRing", "疯狂的 0.5 克拉钻石", "找到或制作你的第一个戒指");
        addAdvancement("recallPotion", "跑路", "酿造召回药水");
        addAdvancement("magnetRing", "富有磁性", "找到或制作磁力之戒");
        addAdvancement("superMagnetRing", "饕餮之眼", "找到或制作错位之戒");
        addAdvancement("heavenScroll", "天堂之卷轴", "找到或制作天堂之礼");
        addAdvancement("voidPearl", "希望消逝", "认领虚空珍珠");
        addAdvancement("forbiddenAxe", "骇人报复", "用行刑者之斧砍下敌人的头颅。");
        addAdvancement("megasponge", "谁住在最深处？", "找到或制作无内海绵");
        addAdvancement("mendingMixture", "外星物质", "找到或制作修补混合物");
        addAdvancement("unholyGrail", "不听老人言", "饮用不洁圣杯内的水");
        addAdvancement("unholyGrailWorthy", "罪恶霸业", "驾驭不洁圣杯的力量……但代价是什么？");
        addAdvancement("astralDust", "原始催化剂", "发现星尘");
        addAdvancement("smeltEtherium", "并没有那么虚无缥缈", "熔炼以太锭");
        addAdvancement("etheriumTool", "激进工具设计师", "制作任意以太工具");
        addAdvancement("etheriumGear", "终极防御", "制作全套以太护甲");
        addAdvancement("ultimatePotion", "酿造大师", "第一次酿造终极药水");
        addAdvancement("loreInscriber", "铭文撰者", "获得设计师的墨水");
        addAdvancement("burnTheTome", "你怎敢", "尝试并使用远古巨著作为熔炉燃料");
        addAdvancement("forbiddenFruit", "原初罪行", "吃下禁果，摆脱饥饿的诅咒");
        addAdvancement("cursedRing", "前途黑暗", "带上七咒之戒并接受七个诅咒");
        addAdvancement("twistedHeart", "它将背叛你", "创造扭曲的心");
        addAdvancement("guardianHeart", "恶意之眼", "杀死远古守护者并获得守卫者之心");
        addAdvancement("infernalShield", "有本事刺穿§l它§r啊！", "锻造烈焰之傲壁垒，一个终极防御工具");
        addAdvancement("twistedMirror", "嫉妒美人", "创建扭曲魔镜");
        addAdvancement("theTwist", "命运倒转", "击杀凋灵并使用邪恶精髓制作倒转之启");
        addAdvancement("astralFruit", "绝妙的果子", "在末地城内找到天体果实");
        addAdvancement("abyssalHeart", "邪异的恐怖", "杀死末影龙并获得深渊之心");
        addAdvancement("theInfinitum", "超然象外", "获得无止之言，成为深渊的使者");
        addAdvancement("desolationRing", "现实者鄙", "铸造荒芜之负，并忍受其结果");
        addAdvancement("allSpellstones", "石头做的魔法书", "收集每一个独特的术石。也许有某种途径将它们全部合并为一……");
        addAdvancement("theCube", "钨块治愈了我的腐朽", "将所有的术石都强扭成一个强力而不自然的形体——非欧立方");
        addAdvancement("fabulousScroll", "手触苍穹", "获得创造者的恩赐");
        addAdvancement("cosmicScroll", "神圣守护者", "前往建筑师的领域朝圣，成为他的忠实信徒，以佩戴他的印记、获得他的祝福");
    }

    private void addAdvancement(String key, String title, String description) {
        add("advancement.enigmatic_legacy:" + key, "§5" + title);
        add("advancement.enigmatic_legacy:" + key + ".desc", description);
    }

    private void addChineseCursedRingTooltips() {
        add("tooltip.enigmatic_legacy.void", "");

        add("message.enigmatic_legacy.cursed_ring.no_sleep", "七咒之戒的失眠诅咒阻止了你的睡眠。");

        add("tooltip.enigmatic_legacy.eternallyBound1", "§5一旦佩戴，它便成为你的一部分。");
        add("tooltip.enigmatic_legacy.eternallyBound2", "§4这枚戒指将永远陪伴着你。");
        add("tooltip.enigmatic_legacy.eternallyBound2_creative", "§6需要神的能力才能取下它。");
        add("tooltip.enigmatic_legacy.cursedRingTimer", "§5当前七咒佩戴时间百分比：§6%1$s");
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
