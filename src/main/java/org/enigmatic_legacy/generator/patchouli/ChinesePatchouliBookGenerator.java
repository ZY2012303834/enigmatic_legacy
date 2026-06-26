package org.enigmatic_legacy.generator.patchouli;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 启示之证 Patchouli 手册中文内容生成器。
 * 作用：
 * - 生成中文分类；
 * - 生成中文物品介绍；
 * - 补全当前项目已有的术石、卷轴、装备和主要遗物说明。
 */
final class ChinesePatchouliBookGenerator extends AbstractPatchouliBookContentGenerator {
    ChinesePatchouliBookGenerator(PackOutput output) {
        super(output);
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Books: zh_cn";
    }

    /**
     * 中文内容入口。
     */
    @Override
    protected void addContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        // 基础分类。
        addCategories(output, futures, "zh_cn",
                "世界", "关于此世法则、代价与隐秘真相的记录。",
                "材料", "用于制作神秘遗物的异常材料。",
                "遗物", "古老戒指、护符、书卷与神秘造物。"
        );

        // 额外分类：术石、卷轴、装备。
        addExtraCategories(output, futures);

        // 各分类条目。
        addWorldEntries(output, futures);
        addMaterialEntries(output, futures);
        addRelicEntries(output, futures);
        addSpellstoneEntries(output, futures);
        addScrollEntries(output, futures);
        addEquipmentEntries(output, futures);
    }

    /**
     * 生成新增分类。
     */
    private void addExtraCategories(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "categories/spellstones", category(
                "术石",
                "佩戴后提供主动与被动能力的强大遗物。",
                "enigmatic_legacy:golem_heart",
                30
        )));

        futures.add(save(output, "zh_cn", "categories/scrolls", category(
                "奥秘卷轴",
                "改变飞行、经验、诅咒与收益规则的卷轴和契约。",
                "enigmatic_legacy:xp_scroll",
                40
        )));

        futures.add(save(output, "zh_cn", "categories/equipment", category(
                "装备",
                "由异常材料锻造而成的武器、工具与护甲。",
                "enigmatic_legacy:etherium_sword",
                50
        )));
    }

    /**
     * 世界机制条目。
     */
    private void addWorldEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/world/soul_loss", entry(
                "灵魂流失",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "灵魂流失",
                        "死亡很少没有代价。$(br2)" +
                                "某些力量可以归还失去之物，但世界总会记住你曾经欠下什么。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/world/the_architects_favor", entry(
                "造物者的眷顾",
                "world",
                "minecraft:structure_block",
                10,
                textPage(
                        "造物者的眷顾",
                        "在普通合成与普通奖励之外，存在着并非人人都能看见的眷顾。"
                ),
                textPage(
                        "提示",
                        "启示之证只记录这份真相的一部分。$(br2)" +
                                "有些知识必须通过探索、诅咒与生存来获得。"
                )
        )));
    }

    /**
     * 材料条目。
     */
    private void addMaterialEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/materials/astral_dust", simpleSpotlight(
                "星尘",
                "materials",
                "enigmatic_legacy:astral_dust",
                0,
                "微微闪烁的异常粉尘，仿佛来自未知边界。$(br2)许多早期遗物都以它为起点。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/ender_rod", recipeEntry(
                "末影棒",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "灌注末影共鸣的棒材。$(br2)它常作为奇异造物的稳定组件。",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/thicc_scroll", recipeEntry(
                "厚重卷轴",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "一张空白却异常坚韧的卷轴。$(br2)它能承载普通纸张无法容纳的知识。",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/etherium_ingot", entry(
                "以太锭",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                30,
                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "以太锭",
                        "用于高级遗物构造的精炼材料。$(br2)它常被用于制作以太工具、武器与护甲。"
                ),
                craftingPage("enigmatic_legacy:etherium_block_uncrafting", "以太块可以分解回以太锭。")
        )));

        futures.add(save(output, "zh_cn", "entries/materials/cosmic_heart", recipeEntry(
                "宇宙之心",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                40,
                "心形的宇宙潜能凝聚物。$(br2)它是空间与距离类遗物的重要材料。",
                "enigmatic_legacy:cosmic_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/earth_heart", recipeEntry(
                "大地之心",
                "materials",
                "enigmatic_legacy:earth_heart",
                50,
                "大地碎片被凝聚成一颗心脏，其中脉动着深埋地下的生命力。",
                "enigmatic_legacy:earth_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/twisted_heart", recipeEntry(
                "扭曲之心",
                "materials",
                "enigmatic_legacy:twisted_heart",
                60,
                "被敌意能量扭曲的心脏。$(br2)它很危险，但许多遗物都需要危险的材料。",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_essence", simpleSpotlight(
                "邪恶精华",
                "materials",
                "enigmatic_legacy:evil_essence",
                70,
                "凝聚成形的恶意。$(br2)它不稳定、会腐化，却很有用。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_ingot", recipeEntry(
                "邪恶锭",
                "materials",
                "enigmatic_legacy:evil_ingot",
                80,
                "由过于恶毒而不能放任不管的精华锻造成的锭。",
                "enigmatic_legacy:evil_ingot"
        )));
    }

    /**
     * 遗物条目。
     */
    private void addRelicEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/relics/the_acknowledgment", entry(
                "启示之证",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "启示之证",
                        "启示之证既是神秘手册，也是一件古老遗物。$(br2)" +
                                "右键使用它可以打开这本手册，查看遗物、术石、卷轴、诅咒与隐藏知识。"
                ),
                textPage(
                        "作为手册",
                        "启示之证不会被消耗，也不需要装备到饰品栏。$(br2)手持它并右键即可阅读当前记录的知识。"
                ),
                textPage(
                        "作为武器",
                        "启示之证也可以被当作武器挥动。$(br2)它能造成少量伤害，使被击中的敌人燃烧，并且可以附魔。"
                ),
                textPage(
                        "诅咒共鸣",
                        "当持有者承受七咒之戒时，启示之证会削弱部分诅咒痛苦。$(br2)它不能完全移除诅咒，但能让生存稍微宽容一些。"
                ),
                craftingPage("enigmatic_legacy:the_acknowledgment", "合成启示之证后，右键即可打开这本手册。")
        )));

        futures.add(save(output, "zh_cn", "entries/relics/cursed_ring", simpleSpotlight(
                "七咒之戒",
                "relics",
                "enigmatic_legacy:cursed_ring",
                5,
                "将佩戴者绑定到七重惩罚的诅咒戒指。$(br2)许多强大遗物只会回应能够承受其负担的人。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/iron_ring", recipeEntry(
                "铁戒指",
                "relics",
                "enigmatic_legacy:iron_ring",
                10,
                "朴素的戒指基底。$(br2)许多更强大的戒指都从这类普通形态开始。",
                "enigmatic_legacy:iron_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/exquisite_ring", recipeEntry(
                "精致戒指",
                "relics",
                "enigmatic_legacy:golden_ring",
                20,
                "拥有更珍贵框架的精炼戒指，适合进一步附魔或转化。",
                "enigmatic_legacy:golden_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/ender_ring", recipeEntry(
                "末影之戒",
                "relics",
                "enigmatic_legacy:ender_ring",
                30,
                "与末影储物相连的戒指。$(br2)它能通过自己的界面方便地访问末影箱。",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/magnet_ring", recipeEntry(
                "磁力之戒",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "会将附近掉落物吸向佩戴者的戒指。$(br2)采矿、刷怪或清理大量掉落物时尤其方便。",
                "enigmatic_legacy:magnet_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/dislocation_ring", recipeEntry(
                "转位之戒",
                "relics",
                "enigmatic_legacy:dislocation_ring",
                50,
                "更强的磁力戒指。$(br2)它不只是慢慢吸引掉落物，而是将其直接转位到触手可及的位置。",
                "enigmatic_legacy:dislocation_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/twisted_mirror", recipeEntry(
                "扭曲魔镜",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                60,
                "扭曲回归与记忆的魔镜。$(br2)它围绕召回药水和扭曲之心制作而成。",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/unholy_grail", simpleSpotlight(
                "不洁圣杯",
                "relics",
                "enigmatic_legacy:unholy_grail",
                70,
                "将暴力转化为恢复的遗物。$(br2)它奖励激进的生存方式，但绝不代表安全。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/guardian_heart", simpleSpotlight(
                "守卫者之心",
                "relics",
                "enigmatic_legacy:guardian_heart",
                80,
                "承载远古守卫记忆的心脏。$(br2)它与防护、耐久和深海有关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/monster_charm", recipeEntry(
                "怪物猎人勋章",
                "relics",
                "enigmatic_legacy:monster_charm",
                90,
                "献给猎杀怪物者的勋章。$(br2)它强化对敌对生物的战斗，并可能带来额外收益。",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/treasure_hunter_charm", recipeEntry(
                "猎宝者护符",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                100,
                "适合矿工和探险家的护符。$(br2)它提升挖掘、时运与探索舒适度。",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "血染勇气勋章",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                110,
                "献给受诅者的勋章。$(br2)佩戴者越接近死亡，它回应得越猛烈。",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/mega_sponge", recipeEntry(
                "超维海绵",
                "relics",
                "enigmatic_legacy:mega_sponge",
                120,
                "类似护符的海绵，佩戴者接触水体时会消耗附近的水。",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enchanter_pearl", recipeEntry(
                "附魔师珍珠",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                130,
                "只有受诅者才能真正利用的珍珠。$(br2)装备时，它会提供额外护符栏位。",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enigmatic_eye", entry(
                "休眠之眼 / 全知之眼",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                140,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "休眠之眼",
                        "这只眼在被唤醒前沉睡着。$(br2)它只会为每名玩家出现一次，通常来自玩家第一次发现的合适战利品。"
                ),
                textPage(
                        "醒来的视线",
                        "唤醒后，它可以作为护符装备。$(br2)它提供额外护符栏位，增加交互距离，并可能让观察者发声。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/extradimensional_eye", simpleSpotlight(
                "超维之眼",
                "relics",
                "enigmatic_legacy:extradimensional_eye",
                150,
                "窥视普通空间之外的高阶眼睛。$(br2)它与额外交互距离、隐藏容器和异样感知相关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enchantment_transposer", simpleSpotlight(
                "求知之书",
                "relics",
                "enigmatic_legacy:enchantment_transposer",
                160,
                "饥渴于附魔知识的书。$(br2)它能以普通书本无法做到的方式操纵附魔力量。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/curse_transposer", simpleSpotlight(
                "噬咒之书",
                "relics",
                "enigmatic_legacy:curse_transposer",
                170,
                "吞食恶意与诅咒的书。$(br2)它与诅咒，以及愿意处理诅咒知识的人相关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/unwitnessed_amulet", entry(
                "未见之护符",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                180,
                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "未见之护符",
                        "性质尚未被见证的护符。$(br2)使用它可以揭示其中一种神秘变体。"
                ),
                textPage(
                        "显现形态",
                        "每种显现护符都有不同颜色与能力：伤害、速度、弹射物偏转、重力、挖掘、吸血或游泳。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/storage_crystal", simpleSpotlight(
                "超维容器",
                "relics",
                "enigmatic_legacy:storage_crystal",
                190,
                "用于容纳被打断生命的容器。$(br2)它会保存那些被七咒拯救时本该失去的东西。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/soul_crystal", simpleSpotlight(
                "灵魂水晶",
                "relics",
                "enigmatic_legacy:soul_crystal",
                200,
                "被拯救灵魂的结晶残片，绑定着死亡的地点与瞬间。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/forbidden_fruit", simpleSpotlight(
                "禁忌之果",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                210,
                "甜美得超出常理的果实。$(br2)它的馈赠很强大，但代价绝不应该被忽略。"
        )));
    }

    /**
     * 术石条目。
     */
    private void addSpellstoneEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/spellstones/golem_heart", entry(
                "魔像之心",
                "spellstones",
                "enigmatic_legacy:golem_heart",
                0,
                spotlightPage(
                        "enigmatic_legacy:golem_heart",
                        "魔像之心",
                        "防御型术石，会让佩戴者变得更接近铁魔像。$(br2)它提供护甲、护甲韧性、击退抗性，并在未穿护甲时给予特殊保护。"
                ),
                textPage("弱点", "强大的防护并非没有代价。$(br2)魔法、中毒、凋零等效果会变得更加危险。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/angel_blessing", entry(
                "天使之祝",
                "spellstones",
                "enigmatic_legacy:angel_blessing",
                10,
                spotlightPage("enigmatic_legacy:angel_blessing", "天使之祝", "与空气和轻盈相关的机动型术石。$(br2)它能帮助佩戴者更安全地移动、坠落与探索。"),
                textPage("用途", "适合探索峭壁、高山、浮岛和各种垂直空间。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/ocean_stone", entry(
                "海洋意志",
                "spellstones",
                "enigmatic_legacy:ocean_stone",
                20,
                spotlightPage("enigmatic_legacy:ocean_stone", "海洋意志", "水域属性术石，适合水下探索。$(br2)它强化水中的移动与生存能力，但其力量与火焰相冲。"),
                textPage("警告", "佩戴时火焰与高温会变得更加危险。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/blazing_core", entry(
                "烈焰核心",
                "spellstones",
                "enigmatic_legacy:blazing_core",
                30,
                spotlightPage("enigmatic_legacy:blazing_core", "烈焰核心", "与火焰、岩浆和下界相关的术石。$(br2)它提供强大的高温防护，但这种防护并非没有极限。"),
                textPage("过热", "不要把抗性误认为无敌。$(br2)如果烈焰核心被推到极限，佩戴者仍可能承受火焰和岩浆的后果。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/eye_of_nebula", entry(
                "星云之眼",
                "spellstones",
                "enigmatic_legacy:eye_of_nebula",
                40,
                spotlightPage("enigmatic_legacy:eye_of_nebula", "星云之眼", "与遥远空间相连的传送术石。$(br2)主动使用时，会将佩戴者传送到其注视生物的身后。"),
                textPage("战斗用途", "传送之后，下一次攻击会变得更危险。$(br2)它也强化魔法伤害与魔法抗性，但在水中受到的伤害会更加严重。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/void_pearl", entry(
                "虚空珍珠",
                "spellstones",
                "enigmatic_legacy:void_pearl",
                50,
                spotlightPage("enigmatic_legacy:void_pearl", "虚空珍珠", "禁忌的术石，会让佩戴者更接近虚空。$(br2)它移除呼吸需求，并抵抗大多数状态效果。"),
                textPage("黑暗", "黑暗中靠近佩戴者的敌人可能受到虚空伤害和多种削弱效果。$(br2)它也可能帮助佩戴者抵挡致命伤害。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/the_cube", entry(
                "非欧立方",
                "spellstones",
                "enigmatic_legacy:the_cube",
                60,
                spotlightPage("enigmatic_legacy:the_cube", "非欧立方", "打破几何规则的术石。$(br2)它提升移动、挖掘、攻击速度、幸运与时运，并扭曲部分伤害规则。"),
                textPage("主动能力", "主动使用时，会将佩戴者送往当前维度中的随机结构。$(br2)谨慎使用。非欧旅行从不保证舒适。")
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/heart_of_creation", entry(
                "创造之心",
                "spellstones",
                "enigmatic_legacy:heart_of_creation",
                70,
                spotlightPage("enigmatic_legacy:heart_of_creation", "创造之心", "与创造和生存相连的至高术石。$(br2)它提供飞行，抵御多种伤害，并能向附近敌人召下雷霆。"),
                textPage("不朽", "携带或佩戴时，它可以在死亡边缘保住佩戴者的生命。$(br2)这份保护很强大，但并不意味着可以鲁莽行事。")
        )));
    }

    /**
     * 奥秘卷轴条目。
     */
    private void addScrollEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/scrolls/xp_scroll", entry(
                "永恒智慧卷轴",
                "scrolls",
                "enigmatic_legacy:xp_scroll",
                0,
                spotlightPage("enigmatic_legacy:xp_scroll", "永恒智慧卷轴", "用于储存经验的奥秘卷轴。$(br2)启用后，它可以吸收附近经验，也能将储存经验返还给持有者。"),
                textPage("使用", "使用 Shift + 右键切换模式。$(br2)适合采矿、附魔或进入危险战斗前携带。")
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/heaven_scroll", entry(
                "天堂之礼",
                "scrolls",
                "enigmatic_legacy:heaven_scroll",
                10,
                spotlightPage("enigmatic_legacy:heaven_scroll", "天堂之礼", "在信标范围内赋予飞行能力的卷轴。$(br2)飞行会消耗经验，并在满足条件时免疫摔落伤害。"),
                textPage("信标限制", "离开信标范围会失去飞行，并短暂获得缓降。$(br2)如果缓降结束后仍未落地，重力会重新索取代价。")
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/cursed_scroll", entry(
                "千咒卷轴",
                "scrolls",
                "enigmatic_legacy:cursed_scroll",
                20,
                spotlightPage("enigmatic_legacy:cursed_scroll", "千咒卷轴", "只有承受七咒之人才能使用的卷轴。$(br2)它的加成会随装备上的诅咒附魔数量提升。"),
                textPage("成长", "它提升攻击、挖掘速度与生命恢复。$(br2)装备越受诅咒，卷轴回应得越强。")
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/fabulous_scroll", entry(
                "创造者的恩赐",
                "scrolls",
                "enigmatic_legacy:fabulous_scroll",
                30,
                spotlightPage("enigmatic_legacy:fabulous_scroll", "创造者的恩赐", "更高阶的飞行卷轴。$(br2)它允许在任何地方飞行；若不在信标范围内，会持续消耗经验。"),
                textPage("限制", "它不能与天堂之礼同时佩戴。$(br2)二者都赐予飞行，但恩赐不会彼此叠加。")
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/avarice_scroll", entry(
                "无尽贪婪契约",
                "scrolls",
                "enigmatic_legacy:avarice_scroll",
                40,
                spotlightPage("enigmatic_legacy:avarice_scroll", "无尽贪婪契约", "献给受诅者的贪婪契约。$(br2)它提升时运，影响猪灵，增加收益，并让交易更加有利。"),
                textPage("贪婪", "当诅咒足够深时，物资收益可以翻倍。$(br2)击杀生物可能掉落绿宝石，村民也可能给出更低价格。")
        )));
    }

    /**
     * 装备条目。
     */
    private void addEquipmentEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/equipment/axe_of_executioner", recipeEntry(
                "行刑者之斧",
                "equipment",
                "enigmatic_legacy:axe_of_executioner",
                0,
                "一把残酷的斧头，有概率砍下被击杀者的头颅。$(br2)基础斩首概率为 15%，每级抢夺额外增加 5%。",
                "enigmatic_legacy:axe_of_executioner"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_sword", recipeEntry(
                "以太阔剑",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                10,
                "一把沉重的以太剑刃，为正面战斗而锻造。",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_pickaxe", recipeEntry(
                "以太镐",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                20,
                "由以太锻造的耐用矿镐，适合开采坚硬材料。",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_shovel", recipeEntry(
                "以太锹",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                30,
                "由以太锻造的铲具，用于快速挖掘泥土、沙砾与类似方块。",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_axe", recipeEntry(
                "以太斧",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                40,
                "既是工具，也是武器的以太战斧。",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_armor", entry(
                "以太套装",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                50,
                spotlightPage("enigmatic_legacy:etherium_chestplate", "以太套装", "由以太锻造的完整护甲套装。$(br2)它适合后期防护，也能自然搭配其它高阶遗物。"),
                craftingPage("enigmatic_legacy:etherium_helmet", "头盔配方。"),
                craftingPage("enigmatic_legacy:etherium_chestplate", "胸甲配方。"),
                craftingPage("enigmatic_legacy:etherium_leggings", "护腿配方。"),
                craftingPage("enigmatic_legacy:etherium_boots", "靴子配方。")
        )));
    }
}
