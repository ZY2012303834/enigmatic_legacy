package org.enigmatic_legacy.generator.patchouli;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 启示之证 Patchouli 手册中文内容生成器。
 * 设计目标：
 * 1. 按原项目 The Acknowledgment 的定位，将启示之证作为整个模组的手册入口；
 * 2. 不是只介绍启示之证本身，而是记录世界规则、材料、遗物、术石、卷轴、装备和战利品来源；
 * 3. 覆盖当前项目中主要已注册、可获取或有玩法意义的物品；
 * 4. 内容尽量保持 Patchouli 手册风格：短段落、分类清晰、适合游戏内阅读；
 * 5. JSON 继续由 datagen 生成，不手写静态 JSON。
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
     * 中文手册内容入口。
     */
    @Override
    protected void addContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        addCategories(
                output,
                futures,
                "zh_cn",

                "世界",
                "关于此世法则、代价、探索与隐藏机制的记录。",

                "材料",
                "用于制作神秘遗物、术石、卷轴与装备的异常材料。",

                "遗物",
                "古老戒指、护符、圣杯、魔镜、果实与其它神秘造物。"
        );

        addExtraCategories(output, futures);

        addWorldEntries(output, futures);
        addMaterialEntries(output, futures);
        addRelicEntries(output, futures);
        addSpellstoneEntries(output, futures);
        addScrollEntries(output, futures);
        addEquipmentEntries(output, futures);
    }

    /**
     * 生成额外分类。
     */
    private void addExtraCategories(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "categories/spellstones", category(
                "术石",
                "佩戴后提供主动与被动能力的强大遗物。它们通常决定一名玩家的核心战斗或探索风格。",
                "enigmatic_legacy:golem_heart",
                30
        )));

        futures.add(save(output, "zh_cn", "categories/scrolls", category(
                "奥秘卷轴",
                "改变飞行、经验、诅咒、收益与其它规则的卷轴和契约。",
                "enigmatic_legacy:xp_scroll",
                40
        )));

        futures.add(save(output, "zh_cn", "categories/equipment", category(
                "装备",
                "由异常材料锻造而成的武器、工具、护甲、盾牌与特殊装备。",
                "enigmatic_legacy:etherium_sword",
                50
        )));
    }

    /**
     * 世界机制条目。
     */
    private void addWorldEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/world/getting_started", entry(
                "开始阅读",
                "world",
                "enigmatic_legacy:the_acknowledgment",
                0,

                textPage(
                        "开始阅读",
                        "启示之证是本项目的核心手册。$(br2)" +
                                "它记录材料、遗物、术石、奥秘卷轴、装备、战利品来源，以及七咒之戒相关机制。$(br2)" +
                                "如果你不知道下一步该寻找什么，先阅读这里。"
                ),

                textPage(
                        "推荐路线",
                        "第一次游玩时，建议按下面的顺序阅读：$(br2)" +
                                "$(li)材料：了解星尘、以太、大地之心等来源。$(br)" +
                                "$(li)遗物：了解戒指、护符和早期辅助道具。$(br)" +
                                "$(li)术石：选择适合自己的核心能力。$(br)" +
                                "$(li)卷轴：了解飞行、经验、诅咒与收益规则。$(br)" +
                                "$(li)装备：规划中后期武器、工具和护甲。$(br)" +
                                "$(li)七咒：在承受诅咒前阅读相关内容。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/world/soul_loss", entry(
                "灵魂流失",
                "world",
                "minecraft:soul_lantern",
                10,

                textPage(
                        "灵魂流失",
                        "死亡很少没有代价。$(br2)" +
                                "某些力量可以归还失去之物，但世界总会记住你曾经欠下什么。$(br2)" +
                                "灵魂水晶、超维容器和部分七咒相关物品，都与死亡后的保存和寻找机制有关。"
                ),

                textPage(
                        "提示",
                        "如果你在死亡后丢失了重要物品，先确认它是否被保存到了灵魂水晶或超维容器中。$(br2)" +
                                "某些寻路道具可以帮助承受七咒的人寻找这些容器。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/world/loot_and_exploration", entry(
                "探索与战利品",
                "world",
                "minecraft:chest",
                20,

                textPage(
                        "探索与战利品",
                        "并非所有物品都来自合成。$(br2)" +
                                "部分材料、术石、果实、卷轴和高阶物品会出现在结构宝箱中。$(br2)" +
                                "如果你在工作台或 JEI 中找不到合成方式，它很可能需要通过探索获得。"
                ),

                textPage(
                        "常见结构",
                        "你应该重点搜索这些结构：$(br2)" +
                                "$(li)古城宝箱$(br)" +
                                "$(li)末地城宝藏箱$(br)" +
                                "$(li)堡垒遗迹箱子$(br)" +
                                "$(li)废弃传送门箱子$(br)" +
                                "$(li)地牢和废弃矿井$(br)" +
                                "$(li)沙漠神殿和丛林神庙$(br)" +
                                "$(li)水下遗迹、沉船和埋藏宝藏$(br)" +
                                "$(li)要塞走廊和要塞十字路口"
                ),

                textPage(
                        "测试说明",
                        "结构宝箱通常在第一次打开时生成内容。$(br2)" +
                                "如果一个箱子已经打开过，之后修改战利品表不会让它重新刷新。$(br2)" +
                                "测试战利品时，建议使用新世界、新生成区块，或使用 /loot 命令。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/world/the_architects_favor", entry(
                "造物者的眷顾",
                "world",
                "minecraft:structure_block",
                30,

                textPage(
                        "造物者的眷顾",
                        "在普通合成与普通奖励之外，存在着并非人人都能看见的眷顾。$(br2)" +
                                "它不属于常规进度，也不应该被当作普通物品路线的一部分。"
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
                "微微闪烁的异常粉尘，仿佛来自未知边界。$(br2)" +
                        "许多早期遗物、戒指和神秘造物都以它为起点。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/astral_dust_sack", simpleSpotlight(
                "星尘袋",
                "materials",
                "enigmatic_legacy:astral_dust_sack",
                5,
                "由大量星尘压缩而成的储存方块。$(br2)" +
                        "适合保存星尘，也可以作为神秘工艺中的材料储备。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/ender_rod", recipeEntry(
                "末影棒",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "灌注末影共鸣的棒材。$(br2)" +
                        "它常作为空间、传送和末影相关造物的稳定组件。",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/thicc_scroll", recipeEntry(
                "厚重卷轴",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "一张空白却异常坚韧的卷轴。$(br2)" +
                        "它能承载普通纸张无法容纳的知识，是奥秘卷轴路线的重要基础。",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/darkest_scroll", simpleSpotlight(
                "至暗卷轴",
                "materials",
                "enigmatic_legacy:darkest_scroll",
                25,
                "一张被深暗力量浸染的卷轴。$(br2)" +
                        "它本身并不是可装备的奥秘卷轴，而是通向更危险制作路线的重要材料。$(br2)" +
                        "至暗卷轴只能在古城宝箱中发现。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/ichor_droplet", entry(
                "灵液滴",
                "materials",
                "enigmatic_legacy:ichor_droplet",
                26,

                spotlightPage(
                        "enigmatic_legacy:ichor_droplet",
                        "灵液滴",
                        "被称作“神之血”的下界材料。$(br2)" +
                                "它通常会在下界要塞与堡垒遗迹的大多数战利品箱中出现。"
                ),

                textPage(
                        "下界获取",
                        "灵液滴可以在下界的大多数战利品箱中发现。$(br2)" +
                                "你应该重点搜索：$(br2)" +
                                "$(li)下界要塞箱子$(br)" +
                                "$(li)堡垒遗迹藏宝室$(br)" +
                                "$(li)堡垒遗迹普通箱$(br)" +
                                "$(li)堡垒遗迹桥箱子$(br)" +
                                "$(li)堡垒遗迹疣猪兽棚箱子"
                ),

                textPage(
                        "七咒掉落",
                        "佩戴七咒之戒击杀恶魂时，恶魂会额外掉落灵液滴。$(br2)" +
                                "诅咒的力量会激发恶魂血泪中残留的微弱神性，使这种材料重新凝结。$(br2)" +
                                "这也是稳定获取灵液滴的方法之一。"
                ),

                textPage(
                        "用途",
                        "灵液滴本身是材料，不是可以装备的遗物或卷轴。$(br2)" +
                                "它将用于后续与净化、神性、下界、特殊药水和更高阶造物相关的制作路线。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/materials/etherium_ore", simpleSpotlight(
                "以太矿石",
                "materials",
                "enigmatic_legacy:etherium_ore",
                30,
                "蕴含以太能量的稀有矿石。$(br2)" +
                        "它通常不是普通地下矿脉，而是高阶探索奖励。$(br2)" +
                        "将其熔炼后可以得到以太锭。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/etherium_ingot", entry(
                "以太锭",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                40,

                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "以太锭",
                        "用于高级遗物构造的精炼材料。$(br2)" +
                                "它常被用于制作以太工具、武器与护甲。"
                ),

                craftingPage(
                        "enigmatic_legacy:etherium_block_uncrafting",
                        "以太块可以分解回以太锭。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/materials/etherium_block", simpleSpotlight(
                "以太块",
                "materials",
                "enigmatic_legacy:etherium_block",
                45,
                "以太锭压缩而成的高阶材料方块。$(br2)" +
                        "它既可用于储存以太，也能分解回以太锭。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/cosmic_heart", recipeEntry(
                "宇宙之心",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                50,
                "心形的宇宙潜能凝聚物。$(br2)" +
                        "它是空间、距离、传送和高阶遗物的重要材料。",
                "enigmatic_legacy:cosmic_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/earth_heart_fragment", simpleSpotlight(
                "大地之心碎片",
                "materials",
                "enigmatic_legacy:earth_heart_fragment",
                55,
                "大地之心破碎后留下的残片。$(br2)" +
                        "收集足够碎片后，可以重新凝聚为完整的大地之心。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/earth_heart", recipeEntry(
                "大地之心",
                "materials",
                "enigmatic_legacy:earth_heart",
                60,
                "大地碎片被凝聚成一颗心脏，其中脉动着深埋地下的生命力。$(br2)" +
                        "许多防御型遗物、术石和装备都会用到它。",
                "enigmatic_legacy:earth_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/twisted_heart", recipeEntry(
                "扭曲之心",
                "materials",
                "enigmatic_legacy:twisted_heart",
                70,
                "被敌意能量扭曲的心脏。$(br2)" +
                        "它很危险，但许多遗物都需要危险的材料。",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/abyssal_heart", simpleSpotlight(
                "深渊之心",
                "materials",
                "enigmatic_legacy:abyssal_heart",
                75,
                "来自深渊力量的心脏状材料。$(br2)" +
                        "它通常与虚空、恶意和高阶遗物有关。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_essence", simpleSpotlight(
                "邪恶精华",
                "materials",
                "enigmatic_legacy:evil_essence",
                80,
                "凝聚成形的恶意。$(br2)" +
                        "它不稳定、会腐化，却很有用。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_ingot", recipeEntry(
                "邪恶锭",
                "materials",
                "enigmatic_legacy:evil_ingot",
                90,
                "由过于恶毒而不能放任不管的精华锻造成的锭。$(br2)" +
                        "它常用于诅咒、恶意和高阶遗物路线。",
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
                                "右键使用它可以打开这本手册，查看遗物、术石、卷轴、诅咒、装备与隐藏知识。"
                ),

                textPage(
                        "它记录什么？",
                        "启示之证记录本项目的大部分核心内容：$(br2)" +
                                "$(li)基础材料与稀有材料$(br)" +
                                "$(li)戒指、护符、圣杯、魔镜和果实$(br)" +
                                "$(li)术石的主动与被动能力$(br)" +
                                "$(li)奥秘卷轴与契约$(br)" +
                                "$(li)以太装备、特殊武器和盾牌$(br)" +
                                "$(li)七咒之戒相关限制与奖励$(br)" +
                                "$(li)部分世界探索和战利品来源"
                ),

                textPage(
                        "作为手册",
                        "启示之证不会被消耗，也不需要装备到饰品栏。$(br2)" +
                                "手持它并右键即可阅读当前记录的知识。$(br2)" +
                                "当你获得陌生物品时，可以先在本书中查找同名条目。"
                ),

                textPage(
                        "作为武器",
                        "启示之证也可以被当作武器挥动。$(br2)" +
                                "它能造成少量伤害，使被击中的敌人燃烧，并且可以附魔。$(br2)" +
                                "它不是强大的战斗武器，但足以在早期探索时临时防身。"
                ),

                textPage(
                        "诅咒共鸣",
                        "当持有者承受七咒之戒时，启示之证会与诅咒产生共鸣。$(br2)" +
                                "它不能解除诅咒，也不能让七咒变得安全。$(br2)" +
                                "它只是让承受者在痛苦中多一点继续前进的余地。"
                ),

                craftingPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "合成启示之证后，手持右键即可打开这本手册。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/cursed_ring", entry(
                "七咒之戒",
                "relics",
                "enigmatic_legacy:cursed_ring",
                5,

                spotlightPage(
                        "enigmatic_legacy:cursed_ring",
                        "七咒之戒",
                        "将佩戴者绑定到七重惩罚的诅咒戒指。$(br2)" +
                                "它不是普通饰品，而是一条危险路线。"
                ),

                textPage(
                        "七重代价",
                        "七咒之戒会持续惩罚佩戴者。$(br2)" +
                                "这些惩罚会让战斗、生存、恢复、物品保存和探索变得更加危险。$(br2)" +
                                "但许多强大遗物只会回应能够承受其负担的人。"
                ),

                textPage(
                        "使用建议",
                        "不要在不了解后果的情况下佩戴它。$(br2)" +
                                "当一个物品要求承受七咒，或要求七咒折磨时间达到一定比例时，它的力量通常也伴随着极高风险。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/iron_ring", recipeEntry(
                "铁戒指",
                "relics",
                "enigmatic_legacy:iron_ring",
                10,
                "朴素的戒指基底。$(br2)" +
                        "许多更强大的戒指都从这类普通形态开始。",
                "enigmatic_legacy:iron_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/exquisite_ring", recipeEntry(
                "精致戒指",
                "relics",
                "enigmatic_legacy:golden_ring",
                20,
                "拥有更珍贵框架的精炼戒指。$(br2)" +
                        "它适合进一步附魔、转化或作为其它戒指的材料。",
                "enigmatic_legacy:golden_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/ender_ring", recipeEntry(
                "末影之戒",
                "relics",
                "enigmatic_legacy:ender_ring",
                30,
                "与末影储物相连的戒指。$(br2)" +
                        "佩戴后可以更方便地访问末影箱。",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/magnet_ring", recipeEntry(
                "磁力之戒",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "会将附近掉落物吸向佩戴者的戒指。$(br2)" +
                        "采矿、刷怪或清理大量掉落物时尤其方便。",
                "enigmatic_legacy:magnet_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/dislocation_ring", recipeEntry(
                "转位之戒",
                "relics",
                "enigmatic_legacy:dislocation_ring",
                50,
                "更强的磁力戒指。$(br2)" +
                        "它不只是慢慢吸引掉落物，而是将其直接转位到触手可及的位置。",
                "enigmatic_legacy:dislocation_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/magic_quartz_ring", entry(
                "魔法石英戒指",
                "relics",
                "enigmatic_legacy:magic_quartz_ring",
                60,

                spotlightPage(
                        "enigmatic_legacy:magic_quartz_ring",
                        "魔法石英戒指",
                        "由石英与魔力材料制成的戒指。$(br2)" +
                                "佩戴时，它会提升护甲与幸运，并削弱部分魔法伤害。"
                ),

                textPage(
                        "佩戴效果",
                        "魔法石英戒指提供：$(br2)" +
                                "$(li)+2 护甲$(br)" +
                                "$(li)+1.5 幸运$(br)" +
                                "$(li)+30% 魔法伤害抗性$(br2)" +
                                "它对原版魔法、凋零、龙息，以及部分兼容模组的魔法伤害有效。"
                ),

                textPage(
                        "佩戴限制",
                        "同一时间只能佩戴一个魔法石英戒指。$(br2)" +
                                "即使你拥有多个戒指栏位，也不能通过重复佩戴来叠加它的效果。"
                ),

                craftingPage(
                        "enigmatic_legacy:magic_quartz_ring",
                        "魔法石英戒指的合成配方。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/twisted_mirror", recipeEntry(
                "扭曲魔镜",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                70,
                "扭曲回归与记忆的魔镜。$(br2)" +
                        "它围绕召回药水和扭曲之心制作而成。",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/recall_potion", simpleSpotlight(
                "召回药水",
                "relics",
                "enigmatic_legacy:recall_potion",
                80,
                "饮下后会尝试将使用者带回安全或绑定的位置。$(br2)" +
                        "它常作为回归、传送与扭曲魔镜相关制作的基础。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/redemption_potion", simpleSpotlight(
                "救赎药水",
                "relics",
                "enigmatic_legacy:redemption_potion",
                90,
                "蕴含救赎力量的药水。$(br2)" +
                        "它并不常见，通常需要通过高阶探索获得。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/mending_mixture", simpleSpotlight(
                "修补混合物",
                "relics",
                "enigmatic_legacy:mending_mixture",
                100,
                "用于修复装备的特殊混合物。$(br2)" +
                        "它可以在工作台中完全修复受损的可损坏物品。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/unholy_grail", simpleSpotlight(
                "不洁圣杯",
                "relics",
                "enigmatic_legacy:unholy_grail",
                110,
                "将暴力转化为恢复的遗物。$(br2)" +
                        "它奖励激进的生存方式，但绝不代表安全。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/animal_guidebook", entry(
                "兽友指南",
                "relics",
                "enigmatic_legacy:animal_guidebook",
                115,

                spotlightPage(
                        "enigmatic_legacy:animal_guidebook",
                        "兽友指南",
                        "一本记录动物陪伴知识的指南。$(br2)" +
                                "只要持有在物品栏、主手或副手中，它就会帮助保护部分动物。"
                ),

                textPage(
                        "动物保护",
                        "兽友指南会让部分动物受到保护，尤其是普通动物和部分可驯服生物。$(br2)" +
                                "它也能削弱七咒之戒第二诅咒对可驯服动物造成的影响。"
                ),

                textPage(
                        "创造模式检测",
                        "创造模式玩家可以用它右键生物，检测该生物是否会被视为可驯服动物。$(br2)" +
                                "这个功能主要用于调试和确认兼容生物。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/hunter_guidebook", entry(
                "野猎指南",
                "relics",
                "enigmatic_legacy:hunter_guidebook",
                116,

                spotlightPage(
                        "enigmatic_legacy:hunter_guidebook",
                        "野猎指南",
                        "一本记录野性狩猎与伙伴协同的指南。$(br2)" +
                                "只要持有在物品栏、主手或副手中，它就会影响附近宠物受到伤害的方式。"
                ),

                textPage(
                        "伤害转移",
                        "当附近宠物受到伤害时，野猎指南会尝试将部分危险转移给主人。$(br2)" +
                                "这能保护宠物，但也意味着主人必须承担更多风险。"
                ),

                textPage(
                        "协同效果",
                        "如果同时配合兽友指南使用，野猎指南的伤害转移会获得额外减免。$(br2)" +
                                "适合经常携带宠物战斗或探索的玩家。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/guardian_heart", simpleSpotlight(
                "守卫者之心",
                "relics",
                "enigmatic_legacy:guardian_heart",
                120,
                "承载远古守卫记忆的心脏。$(br2)" +
                        "它与防护、耐久和深海有关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/monster_charm", recipeEntry(
                "怪物猎人勋章",
                "relics",
                "enigmatic_legacy:monster_charm",
                130,
                "献给猎杀怪物者的勋章。$(br2)" +
                        "它强化对敌对生物的战斗，并可能带来额外收益。",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/treasure_hunter_charm", recipeEntry(
                "猎宝者护符",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                140,
                "适合矿工和探险家的护符。$(br2)" +
                        "它提升挖掘、时运与探索舒适度。",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "血染勇气勋章",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                150,
                "献给受诅者的勋章。$(br2)" +
                        "佩戴者越接近死亡，它回应得越猛烈。",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/mega_sponge", recipeEntry(
                "超维海绵",
                "relics",
                "enigmatic_legacy:mega_sponge",
                160,
                "类似护符的海绵。$(br2)" +
                        "佩戴者接触水体时，它会尝试消耗附近的水。",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enchanter_pearl", recipeEntry(
                "附魔师珍珠",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                170,
                "只有受诅者才能真正利用的珍珠。$(br2)" +
                        "装备时，它会提供额外护符栏位。",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enigmatic_eye", entry(
                "休眠之眼 / 全知之眼",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                180,

                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "休眠之眼",
                        "这只眼在被唤醒前沉睡着。$(br2)" +
                                "它只会为每名玩家出现一次，通常来自玩家第一次发现的合适战利品。"
                ),

                textPage(
                        "醒来的视线",
                        "唤醒后，它可以作为护符装备。$(br2)" +
                                "它提供额外护符栏位，增加交互距离，并可能让观察者发声。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/extradimensional_eye", simpleSpotlight(
                "超维之眼",
                "relics",
                "enigmatic_legacy:extradimensional_eye",
                190,
                "窥视普通空间之外的高阶眼睛。$(br2)" +
                        "它与额外交互距离、隐藏容器和异样感知相关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enchantment_transposer", simpleSpotlight(
                "求知之书",
                "relics",
                "enigmatic_legacy:enchantment_transposer",
                200,
                "饥渴于附魔知识的书。$(br2)" +
                        "它能以普通书本无法做到的方式操纵附魔力量。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/curse_transposer", simpleSpotlight(
                "噬咒之书",
                "relics",
                "enigmatic_legacy:curse_transposer",
                210,
                "吞食恶意与诅咒的书。$(br2)" +
                        "它与诅咒，以及愿意处理诅咒知识的人相关。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/unwitnessed_amulet", entry(
                "未见之护符",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                220,

                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "未见之护符",
                        "性质尚未被见证的护符。$(br2)" +
                                "使用它可以揭示其中一种神秘变体。"
                ),

                textPage(
                        "显现形态",
                        "每种显现护符都有不同颜色与能力：$(br2)" +
                                "$(li)红色：攻击伤害$(br)" +
                                "$(li)青色：疾跑速度$(br)" +
                                "$(li)紫色：弹射物偏转$(br)" +
                                "$(li)品红色：重力变化$(br)" +
                                "$(li)绿色：挖掘效率$(br)" +
                                "$(li)黑色：生命偷取$(br)" +
                                "$(li)蓝色：游泳速度"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enigmatic_amulets", withExtraRecipeMappings(entry(
                        "七色神秘护符",
                        "relics",
                        "enigmatic_legacy:enigmatic_amulet_red",
                        230,

                        spotlightPage(
                                "enigmatic_legacy:enigmatic_amulet_red,enigmatic_legacy:enigmatic_amulet_aqua,enigmatic_legacy:enigmatic_amulet_violet,enigmatic_legacy:enigmatic_amulet_magenta,enigmatic_legacy:enigmatic_amulet_green,enigmatic_legacy:enigmatic_amulet_black,enigmatic_legacy:enigmatic_amulet_blue",
                                "七色神秘护符",
                                "未见之护符在被见证后，会显现为七种不同颜色的神秘护符之一。$(br2)" +
                                        "每种颜色都代表一种独立的能力方向。"
                        ),

                        textPage(
                                "七种形态",
                                "七色神秘护符包括：$(br2)" +
                                        "$(li)红色：攻击伤害$(br)" +
                                        "$(li)青色：疾跑速度$(br)" +
                                        "$(li)紫色：弹射物偏转$(br)" +
                                        "$(li)品红色：重力变化$(br)" +
                                        "$(li)绿色：挖掘效率$(br)" +
                                        "$(li)黑色：生命偷取$(br)" +
                                        "$(li)蓝色：游泳速度"
                        ),

                        textPage(
                                "进阶",
                                "七种颜色的护符可以进一步汇聚为飞升护符。$(br2)" +
                                        "飞升护符继承全部七种神秘护符的力量，并能继续通向更加危险的轻蔑之约。"
                        )
                ), 0,
                "enigmatic_legacy:enigmatic_amulet_red",
                "enigmatic_legacy:enigmatic_amulet_aqua",
                "enigmatic_legacy:enigmatic_amulet_violet",
                "enigmatic_legacy:enigmatic_amulet_magenta",
                "enigmatic_legacy:enigmatic_amulet_green",
                "enigmatic_legacy:enigmatic_amulet_black",
                "enigmatic_legacy:enigmatic_amulet_blue"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/ascension_amulet", entry(
                "飞升护符",
                "relics",
                "enigmatic_legacy:ascension_amulet",
                240,

                spotlightPage(
                        "enigmatic_legacy:ascension_amulet",
                        "飞升护符",
                        "由七种神秘护符汇聚而成的高阶护符。$(br2)" +
                                "它同时继承七种颜色神秘护符的力量。"
                ),

                textPage(
                        "佩戴效果",
                        "飞升护符提供攻击、速度、弹射物偏转、重力、挖掘、生命偷取和游泳相关加成。$(br2)" +
                                "它是神秘护符路线的完整形态，也是通向轻蔑之约的关键材料。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/eldritch_amulet", recipeEntry(
                "轻蔑之约",
                "relics",
                "enigmatic_legacy:eldritch_amulet",
                250,
                "飞升护符或许已臻完美，但完美仍可被扭曲为残酷之物。$(br2)" +
                        "轻蔑之约拥有全部七种护符特质，额外提供攻击伤害与生命偷取；被你凝视的生物会被削弱。死亡时也会保留你的背包，带有消失诅咒的物品仍会消失。",
                "enigmatic_legacy:eldritch_amulet"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/storage_crystal", simpleSpotlight(
                "超维容器",
                "relics",
                "enigmatic_legacy:storage_crystal",
                260,
                "用于容纳被打断生命的容器。$(br2)" +
                        "它会保存那些被七咒拯救时本该失去的东西。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/soul_crystal", simpleSpotlight(
                "灵魂水晶",
                "relics",
                "enigmatic_legacy:soul_crystal",
                270,
                "被拯救灵魂的结晶残片。$(br2)" +
                        "它绑定着死亡的地点与瞬间。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/wayfinder_of_the_damned", simpleSpotlight(
                "被诅咒者的寻路指针",
                "relics",
                "enigmatic_legacy:wayfinder_of_the_damned",
                280,
                "只回应受七咒折磨之人的指针。$(br2)" +
                        "它可以帮助寻找当前维度内最近的灵魂水晶或装有死亡掉落的超维容器。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/forbidden_fruit", simpleSpotlight(
                "禁忌之果",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                290,
                "甜美得超出常理的果实。$(br2)" +
                        "它的馈赠很强大，但代价绝不应该被忽略。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/astral_fruit", entry(
                "天体果实",
                "relics",
                "enigmatic_legacy:astral_fruit",
                300,

                spotlightPage(
                        "enigmatic_legacy:astral_fruit",
                        "天体果实",
                        "带有星辉力量的稀有果实。$(br2)" +
                                "食用后会给予一组强力增益效果。"
                ),

                textPage(
                        "基础效果",
                        "无论是否佩戴七咒之戒，食用天体果实都会获得基础增益效果。$(br2)" +
                                "这些效果适合在危险战斗、探索或逃生前使用。"
                ),

                textPage(
                        "七咒共鸣",
                        "如果玩家佩戴七咒之戒，并且此前从未吸收过天体果实的星辉，食用后会永久增加 1 个戒指栏位。$(br2)" +
                                "这个栏位增加效果每名玩家只能触发一次。$(br2)" +
                                "未佩戴七咒之戒，或第二次及以后食用，只会获得基础增益效果。"
                )
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
                        "防御型术石，会让佩戴者变得更接近铁魔像。$(br2)" +
                                "它提供护甲、护甲韧性、击退抗性，并在未穿护甲时给予特殊保护。"
                ),

                textPage(
                        "弱点",
                        "强大的防护并非没有代价。$(br2)" +
                                "魔法、中毒、凋零等效果会变得更加危险。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/angel_blessing", entry(
                "天使之祝",
                "spellstones",
                "enigmatic_legacy:angel_blessing",
                10,

                spotlightPage(
                        "enigmatic_legacy:angel_blessing",
                        "天使之祝",
                        "与空气和轻盈相关的机动型术石。$(br2)" +
                                "它能帮助佩戴者更安全地移动、坠落与探索。"
                ),

                textPage(
                        "用途",
                        "适合探索峭壁、高山、浮岛和各种垂直空间。$(br2)" +
                                "如果你经常因为地形或坠落而陷入危险，它会非常有用。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/ocean_stone", entry(
                "海洋意志",
                "spellstones",
                "enigmatic_legacy:ocean_stone",
                20,

                spotlightPage(
                        "enigmatic_legacy:ocean_stone",
                        "海洋意志",
                        "水域属性术石，适合水下探索。$(br2)" +
                                "它强化水中的移动与生存能力，但其力量与火焰相冲。"
                ),

                textPage(
                        "警告",
                        "佩戴时火焰与高温会变得更加危险。$(br2)" +
                                "在下界、岩浆湖或烈焰人附近使用时要格外谨慎。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/blazing_core", entry(
                "烈焰核心",
                "spellstones",
                "enigmatic_legacy:blazing_core",
                30,

                spotlightPage(
                        "enigmatic_legacy:blazing_core",
                        "烈焰核心",
                        "与火焰、岩浆和下界相关的术石。$(br2)" +
                                "它提供强大的高温防护，但这种防护并非没有极限。"
                ),

                textPage(
                        "过热",
                        "不要把抗性误认为无敌。$(br2)" +
                                "如果烈焰核心被推到极限，佩戴者仍可能承受火焰和岩浆的后果。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/eye_of_nebula", entry(
                "星云之眼",
                "spellstones",
                "enigmatic_legacy:eye_of_nebula",
                40,

                spotlightPage(
                        "enigmatic_legacy:eye_of_nebula",
                        "星云之眼",
                        "与遥远空间相连的传送术石。$(br2)" +
                                "主动使用时，会将佩戴者传送到其注视生物的身后。"
                ),

                textPage(
                        "战斗用途",
                        "传送之后，下一次攻击会变得更危险。$(br2)" +
                                "它也强化魔法伤害与魔法抗性，但在水中受到的伤害会更加严重。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/void_pearl", entry(
                "虚空珍珠",
                "spellstones",
                "enigmatic_legacy:void_pearl",
                50,

                spotlightPage(
                        "enigmatic_legacy:void_pearl",
                        "虚空珍珠",
                        "禁忌的术石，会让佩戴者更接近虚空。$(br2)" +
                                "它移除呼吸需求，并抵抗大多数状态效果。"
                ),

                textPage(
                        "黑暗",
                        "黑暗中靠近佩戴者的敌人可能受到虚空伤害和多种削弱效果。$(br2)" +
                                "它也可能帮助佩戴者抵挡致命伤害。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/the_cube", entry(
                "非欧立方",
                "spellstones",
                "enigmatic_legacy:the_cube",
                60,

                spotlightPage(
                        "enigmatic_legacy:the_cube",
                        "非欧立方",
                        "打破几何规则的术石。$(br2)" +
                                "它提升移动、挖掘、攻击速度、幸运与时运，并扭曲部分伤害规则。"
                ),

                textPage(
                        "主动能力",
                        "主动使用时，会将佩戴者送往当前维度中的随机结构。$(br2)" +
                                "谨慎使用。非欧旅行从不保证舒适。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/heart_of_creation", entry(
                "创造之心",
                "spellstones",
                "enigmatic_legacy:heart_of_creation",
                70,

                spotlightPage(
                        "enigmatic_legacy:heart_of_creation",
                        "创造之心",
                        "与创造和生存相连的至高术石。$(br2)" +
                                "它提供飞行，抵御多种伤害，并能向附近敌人召下雷霆。"
                ),

                textPage(
                        "不朽",
                        "携带或佩戴时，它可以在死亡边缘保住佩戴者的生命。$(br2)" +
                                "这份保护很强大，但并不意味着可以鲁莽行事。"
                )
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

                spotlightPage(
                        "enigmatic_legacy:xp_scroll",
                        "永恒智慧卷轴",
                        "用于储存经验的奥秘卷轴。$(br2)" +
                                "启用后，它可以吸收附近经验，也能将储存经验返还给持有者。"
                ),

                textPage(
                        "使用",
                        "使用 Shift + 右键切换模式。$(br2)" +
                                "适合采矿、附魔或进入危险战斗前携带。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/heaven_scroll", entry(
                "天堂之礼",
                "scrolls",
                "enigmatic_legacy:heaven_scroll",
                10,

                spotlightPage(
                        "enigmatic_legacy:heaven_scroll",
                        "天堂之礼",
                        "在信标范围内赋予飞行能力的卷轴。$(br2)" +
                                "飞行会消耗经验，并在满足条件时免疫摔落伤害。"
                ),

                textPage(
                        "信标限制",
                        "离开信标范围会失去飞行，并短暂获得缓降。$(br2)" +
                                "如果缓降结束后仍未落地，重力会重新索取代价。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/cursed_scroll", entry(
                "千咒卷轴",
                "scrolls",
                "enigmatic_legacy:cursed_scroll",
                20,

                spotlightPage(
                        "enigmatic_legacy:cursed_scroll",
                        "千咒卷轴",
                        "只有承受七咒之人才能使用的卷轴。$(br2)" +
                                "它的加成会随装备上的诅咒附魔数量提升。"
                ),

                textPage(
                        "成长",
                        "它提升攻击、挖掘速度与生命恢复。$(br2)" +
                                "装备越受诅咒，卷轴回应得越强。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/fabulous_scroll", entry(
                "创造者的恩赐",
                "scrolls",
                "enigmatic_legacy:fabulous_scroll",
                30,

                spotlightPage(
                        "enigmatic_legacy:fabulous_scroll",
                        "创造者的恩赐",
                        "更高阶的飞行卷轴。$(br2)" +
                                "它允许在任何地方飞行；若不在信标范围内，会持续消耗经验。"
                ),

                textPage(
                        "限制",
                        "它不能与天堂之礼同时佩戴。$(br2)" +
                                "二者都赐予飞行，但恩赐不会彼此叠加。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/avarice_scroll", entry(
                "无尽贪婪契约",
                "scrolls",
                "enigmatic_legacy:avarice_scroll",
                40,

                spotlightPage(
                        "enigmatic_legacy:avarice_scroll",
                        "无尽贪婪契约",
                        "献给受诅者的贪婪契约。$(br2)" +
                                "它提升时运，影响猪灵，增加收益，并让交易更加有利。"
                ),

                textPage(
                        "贪婪",
                        "当诅咒足够深时，物资收益可以翻倍。$(br2)" +
                                "击杀生物可能掉落绿宝石，村民也可能给出更低价格。"
                )
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
                "一把残酷的斧头，有概率砍下被击杀者的头颅。$(br2)" +
                        "基础斩首概率为 15%，每级抢夺额外增加 5%。",
                "enigmatic_legacy:axe_of_executioner"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/ender_slayer", simpleSpotlight(
                "末影之屠",
                "equipment",
                "enigmatic_legacy:ender_slayer",
                10,
                "专门用于对抗末地生物的七咒遗物武器。$(br2)" +
                        "它对末影类敌人尤为危险，是探索末地时值得准备的武器。"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_sword", recipeEntry(
                "以太阔剑",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                20,
                "一把沉重的以太剑刃，为正面战斗而锻造。$(br2)" +
                        "它拥有优秀的耐久和稳定的近战表现。",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_pickaxe", recipeEntry(
                "以太镐",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                30,
                "由以太锻造的耐用矿镐。$(br2)" +
                        "适合开采坚硬材料，并能正常接受工具类附魔。",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_shovel", recipeEntry(
                "以太锹",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                40,
                "由以太锻造的铲具。$(br2)" +
                        "用于快速挖掘泥土、沙砾、沙子与类似方块。",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_axe", recipeEntry(
                "以太斧",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                50,
                "既是工具，也是武器的以太战斧。$(br2)" +
                        "它适合伐木，也可以作为可靠的近战装备。",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_armor", entry(
                "以太套装",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                60,

                spotlightPage(
                        "enigmatic_legacy:etherium_chestplate",
                        "以太套装",
                        "由以太锻造的完整护甲套装。$(br2)" +
                                "它适合后期防护，也能自然搭配其它高阶遗物。"
                ),

                craftingPage("enigmatic_legacy:etherium_helmet", "以太头盔配方。"),
                craftingPage("enigmatic_legacy:etherium_chestplate", "以太胸甲配方。"),
                craftingPage("enigmatic_legacy:etherium_leggings", "以太护腿配方。"),
                craftingPage("enigmatic_legacy:etherium_boots", "以太靴子配方。")
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/the_twist", simpleSpotlight(
                "倒转之启",
                "equipment",
                "enigmatic_legacy:the_twist",
                70,
                "启示之证在七咒影响下扭曲出的形态。$(br2)" +
                        "它既是书，也是武器，只会回应承受七咒之人。"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/the_infinitum", simpleSpotlight(
                "无止之言",
                "equipment",
                "enigmatic_legacy:the_infinitum",
                80,
                "比倒转之启更加深邃的高阶形态。$(br2)" +
                        "它承载着几乎没有尽头的知识与暴力。"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/majestic_elytra", simpleSpotlight(
                "壮丽鞘翅",
                "equipment",
                "enigmatic_legacy:majestic_elytra",
                90,
                "经过神秘力量强化的鞘翅。$(br2)" +
                        "它适合长距离探索，也可以和其它高阶装备共同使用。"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/bulwark_of_blazing_pride", simpleSpotlight(
                "烈焰之傲壁垒",
                "equipment",
                "enigmatic_legacy:infernal_shield",
                100,
                "只有承受七咒之人才能真正使用的防火盾牌。$(br2)" +
                        "它与火焰、防御和傲慢有关，是危险战斗中的坚固壁垒。"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/voracious_pan", simpleSpotlight(
                "饕餮之锅",
                "equipment",
                "enigmatic_legacy:eldritch_pan",
                110,
                "看似荒唐，却极其危险的七咒遗物。$(br2)" +
                        "它会随着不同生物击杀逐渐增强，也能像武器一样被使用。"
        )));
    }
}