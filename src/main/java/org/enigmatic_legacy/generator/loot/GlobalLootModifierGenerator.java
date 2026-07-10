package org.enigmatic_legacy.generator.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.compat.DungeonsAriseCompat;
import org.enigmatic_legacy.loot.ConfigurableAddTableLootModifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 全局战利品注入生成器。
 * 注意：
 * 同一个 modid 只能注册一个 GlobalLootModifierProvider，
 * 否则 runData 会报：
 * Duplicate provider: Global Loot Modifiers : enigmatic_legacy
 * 所以术石、至暗卷轴、不洁圣杯的 loot modifier
 * 都统一放在这个 Provider 里生成。
 */
public class GlobalLootModifierGenerator extends GlobalLootModifierProvider {
    public GlobalLootModifierGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, EnigmaticLegacy.MODID);
    }

    @Override
    protected void start() {
        addSpellstoneModifiers();
        addForgottenIceCrystalModifiers();
        addRevivalLeavesModifiers();
        addDarkestScrollModifiers();
        addUnholyGrailModifiers();
        addForbiddenFruitModifiers();
        addIchorDropletModifiers();
        addAstralDustModifiers();
        addEtheriumOreModifiers();
        addMajesticElytraModifiers();
        addAstralFruitModifiers();
        addMendingMixtureModifiers();
        addRedemptionPotionModifiers();

        // 大地之心奖励箱获取方式。
        // 按原作者项目的 Overworld epic 池注入范围复刻。
        addEarthHeartModifiers();

        // 大地之心碎片奖励箱获取方式
        // 先按原项目奖励箱范围与概率注入，保证编译与数据生成正常。
        addEarthHeartFragmentModifiers();

        addAntiqueBookBagModifiers();
        addDungeonsAriseModifiers();

    }

    /**
     * 地牢浮现之时兼容战利品注入。
     *
     * <p>天际挑战者、天际骑士团战舰、天堂征服者都属于天空/天堂主题结构。
     * 这里全部注入同一张“天使之祝”附加表，但只注入 treasure 战利品箱。</p>
     */
    private void addDungeonsAriseModifiers() {
        addDungeonsAriseHeavenlyModifiers();
        addDungeonsAriseFoundryModifiers();
    }

    private void addDungeonsAriseHeavenlyModifiers() {
        for (ResourceLocation table : DungeonsAriseCompat.HEAVENLY_ANGEL_BLESSING_CHEST_TABLES) {
            addTableModifier(
                    "dungeons_arise_angel_blessing_" + table.getPath().replace('/', '_'),
                    table,
                    DungeonsAriseCompat.HEAVENLY_CHALLENGER_ANGEL_BLESSING_INJECT
            );
        }
    }

    /**
     * 铸造厂 / Foundry 兼容战利品注入。
     *
     * <p>普通箱只处理 foundry_normal 与 foundry_passage_normal；
     * 宝藏箱只处理 foundry_treasure。chains、lava_pit、passage_exterior 等特殊箱池不注入，
     * 避免把稀有物品扩散到非目标箱子。</p>
     */
    private void addDungeonsAriseFoundryModifiers() {
        addDungeonsAriseModifiers(
                "foundry_normal",
                DungeonsAriseCompat.FOUNDRY_NORMAL_CHEST_TABLES,
                DungeonsAriseCompat.FOUNDRY_NORMAL_INJECTS
        );

        addDungeonsAriseModifiers(
                "foundry_treasure",
                DungeonsAriseCompat.FOUNDRY_TREASURE_CHEST_TABLES,
                DungeonsAriseCompat.FOUNDRY_TREASURE_INJECTS
        );
    }

    private void addDungeonsAriseModifiers(String groupName, List<ResourceLocation> targetTables, List<String> injectPaths) {
        for (ResourceLocation table : targetTables) {
            for (int index = 0; index < injectPaths.size(); index++) {
                addTableModifier(
                        "dungeons_arise_" + groupName + "_" + table.getPath().replace('/', '_') + "_" + index,
                        table,
                        injectPaths.get(index)
                );
            }
        }
    }

    /**
     * 复苏之叶获取方式。
     *
     * <p>复刻 Enigmatic Addons：复苏之叶可以在丛林神庙战利品箱中发现。</p>
     */
    private void addRevivalLeavesModifiers() {
        addTableModifier(
                "revival_leaf_jungle_temple",
                BuiltInLootTables.JUNGLE_TEMPLE,
                "inject/chests/revival_leaf/jungle_temple"
        );
    }

    private void addAntiqueBookBagModifiers() {
        addTableModifier("antique_book_bag_simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_igloo_chest", BuiltInLootTables.IGLOO_CHEST, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_woodland_mansion", BuiltInLootTables.WOODLAND_MANSION, "inject/chests/antique_book_bag/overworld_epic");
        addTableModifier("antique_book_bag_shipwreck_supply", BuiltInLootTables.SHIPWRECK_SUPPLY, "inject/chests/antique_book_bag/overworld_epic");
    }

    /**
     * 修补混合物箱子注入。
     * 原版设定：
     * - 修补混合物可以小概率在末地城宝藏箱中找到。
     * 注意：
     * - 这里只决定“出现在哪类箱子”。
     * - 实际概率在 InjectLootTableGenerator 的
     *   mending_mixture/end_city_treasure 表中控制。
     */
    private void addMendingMixtureModifiers() {
        addTableModifier(
                "mending_mixture_end_city_treasure",
                BuiltInLootTables.END_CITY_TREASURE,
                "inject/chests/mending_mixture/end_city_treasure"
        );
    }

    private void addRedemptionPotionModifiers() {
        addVillageModifier("redemption_potion_village_weaponsmith", BuiltInLootTables.VILLAGE_WEAPONSMITH);
        addVillageModifier("redemption_potion_village_toolsmith", BuiltInLootTables.VILLAGE_TOOLSMITH);
        addVillageModifier("redemption_potion_village_armorer", BuiltInLootTables.VILLAGE_ARMORER);
        addVillageModifier("redemption_potion_village_cartographer", BuiltInLootTables.VILLAGE_CARTOGRAPHER);
        addVillageModifier("redemption_potion_village_mason", BuiltInLootTables.VILLAGE_MASON);
        addVillageModifier("redemption_potion_village_shepherd", BuiltInLootTables.VILLAGE_SHEPHERD);
        addVillageModifier("redemption_potion_village_butcher", BuiltInLootTables.VILLAGE_BUTCHER);
        addVillageModifier("redemption_potion_village_fletcher", BuiltInLootTables.VILLAGE_FLETCHER);
        addVillageModifier("redemption_potion_village_fisher", BuiltInLootTables.VILLAGE_FISHER);
        addVillageModifier("redemption_potion_village_tannery", BuiltInLootTables.VILLAGE_TANNERY);
        addVillageModifier("redemption_potion_village_temple", BuiltInLootTables.VILLAGE_TEMPLE);
        addVillageModifier("redemption_potion_village_desert_house", BuiltInLootTables.VILLAGE_DESERT_HOUSE);
        addVillageModifier("redemption_potion_village_plains_house", BuiltInLootTables.VILLAGE_PLAINS_HOUSE);
        addVillageModifier("redemption_potion_village_taiga_house", BuiltInLootTables.VILLAGE_TAIGA_HOUSE);
        addVillageModifier("redemption_potion_village_snowy_house", BuiltInLootTables.VILLAGE_SNOWY_HOUSE);
        addVillageModifier("redemption_potion_village_savanna_house", BuiltInLootTables.VILLAGE_SAVANNA_HOUSE);
    }

    private void addVillageModifier(String name, ResourceKey<LootTable> targetTable) {
        addTableModifier(name, targetTable, "inject/chests/redemption_potion/village");
    }

    /**
     * 以太矿石获取方式。
     * 原项目：
     * 以太矿石可以在末地城宝藏箱子中找到。
     * 原项目数据：
     * - 注入目标：minecraft:chests/end_city_treasure
     * - rolls：-11 ~ 2
     * - 以太矿石权重：60
     * - 数量：1 ~ 2
     */
    private void addEtheriumOreModifiers() {
        addTableModifier(
                "etherium_ore_end_city_treasure",
                BuiltInLootTables.END_CITY_TREASURE,
                "inject/chests/etherium_ore/end_city_treasure"
        );
    }

    private void addMajesticElytraModifiers() {
        addTableModifier(
                "majestic_elytra_end_city_treasure",
                BuiltInLootTables.END_CITY_TREASURE,
                "inject/chests/majestic_elytra/end_city_treasure"
        );
    }

    private void addAstralFruitModifiers() {
        addTableModifier(
                "astral_fruit_end_city_treasure",
                BuiltInLootTables.END_CITY_TREASURE,
                "inject/chests/astral_fruit/end_city_treasure"
        );
    }

    /**
     * 星尘获取方式。
     * 原项目：
     * 星尘可以在末地城宝藏箱子中找到。
     * 原项目数据：
     * - 注入目标：minecraft:chests/end_city_treasure
     * - 星尘权重：85
     * - 数量：1 ~ 4
     */
    private void addAstralDustModifiers() {
        addTableModifier(
                "astral_dust_end_city_treasure",
                BuiltInLootTables.END_CITY_TREASURE,
                "inject/chests/astral_dust/end_city_treasure"
        );
    }

    /**
     * 禁忌之果获取方式。
     * 来源：
     * - 堡垒桥：BASTION_BRIDGE
     * - 堡垒疣猪兽棚：BASTION_HOGLIN_STABLE
     * 兼容说明：
     * Wiki 上还提到其它堡垒奖励池也可能获得，
     * 这里额外加入 BASTION_OTHER，
     * 用来覆盖没有独立居民区奖励池的堡垒变种。
     * 不加入 BASTION_TREASURE：
     * 藏宝室已经用于至暗卷轴，不把禁忌之果放进藏宝室池。
     */
    private void addForbiddenFruitModifiers() {
        addTableModifier(
                "forbidden_fruit_bastion_bridge",
                BuiltInLootTables.BASTION_BRIDGE,
                "inject/chests/forbidden_fruit/bastion_common"
        );

        addTableModifier(
                "forbidden_fruit_bastion_hoglin_stable",
                BuiltInLootTables.BASTION_HOGLIN_STABLE,
                "inject/chests/forbidden_fruit/bastion_common"
        );

        addTableModifier(
                "forbidden_fruit_bastion_other",
                BuiltInLootTables.BASTION_OTHER,
                "inject/chests/forbidden_fruit/bastion_common"
        );


    }

    /**
     * 灵液滴获取方式。
     * 复刻拓展项目设定：
     * - 一种可以在下界获得的材料；
     * - 出现在下界大多数战利品箱中。
     * 注入范围：
     * 1. 下界要塞；
     * 2. 堡垒遗迹藏宝室；
     * 3. 堡垒遗迹普通箱；
     * 4. 堡垒遗迹桥；
     * 5. 堡垒遗迹疣猪兽棚。
     * 注意：
     * - 这里不注入 RUINED_PORTAL；
     * - 因为废弃传送门 loot table 同时用于主世界和下界，
     *   如果加入会导致主世界废弃传送门也能刷出灵液滴。
     */
    private void addIchorDropletModifiers() {
        addTableModifier(
                "ichor_droplet_nether_bridge",
                BuiltInLootTables.NETHER_BRIDGE,
                "inject/chests/ichor_droplet/nether_common"
        );

        addTableModifier(
                "ichor_droplet_bastion_treasure",
                BuiltInLootTables.BASTION_TREASURE,
                "inject/chests/ichor_droplet/nether_common"
        );

        addTableModifier(
                "ichor_droplet_bastion_other",
                BuiltInLootTables.BASTION_OTHER,
                "inject/chests/ichor_droplet/nether_common"
        );

        addTableModifier(
                "ichor_droplet_bastion_bridge",
                BuiltInLootTables.BASTION_BRIDGE,
                "inject/chests/ichor_droplet/nether_common"
        );

        addTableModifier(
                "ichor_droplet_bastion_hoglin_stable",
                BuiltInLootTables.BASTION_HOGLIN_STABLE,
                "inject/chests/ichor_droplet/nether_common"
        );
    }

    /**
     * 术石战利品注入。
     */
    private void addSpellstoneModifiers() {
        addTableModifier("spellstones_air_earthen_desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, "inject/chests/spellstones/air_earthen");
        addTableModifier("spellstones_air_earthen_jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, "inject/chests/spellstones/air_earthen");

        addTableModifier("spellstones_ender_earthen_stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, "inject/chests/spellstones/ender_earthen");
        addTableModifier("spellstones_ender_earthen_stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, "inject/chests/spellstones/ender_earthen");

        addTableModifier("spellstones_air_village_temple", BuiltInLootTables.VILLAGE_TEMPLE, "inject/chests/spellstones/air");

        addTableModifier("spellstones_earthen_simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, "inject/chests/spellstones/earthen");
        addTableModifier("spellstones_earthen_abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, "inject/chests/spellstones/earthen");
        addTableModifier("spellstones_earthen_village_armorer", BuiltInLootTables.VILLAGE_ARMORER, "inject/chests/spellstones/earthen");

        addTableModifier("spellstones_nether_bridge", BuiltInLootTables.NETHER_BRIDGE, "inject/chests/spellstones/nether");
        addTableModifier("spellstones_nether_bastion_treasure", BuiltInLootTables.BASTION_TREASURE, "inject/chests/spellstones/nether");
        addTableModifier("spellstones_nether_bastion_other", BuiltInLootTables.BASTION_OTHER, "inject/chests/spellstones/nether");
        addTableModifier("spellstones_nether_bastion_bridge", BuiltInLootTables.BASTION_BRIDGE, "inject/chests/spellstones/nether");
        addTableModifier("spellstones_nether_bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE, "inject/chests/spellstones/nether");
        addTableModifier("spellstones_nether_ruined_portal", BuiltInLootTables.RUINED_PORTAL, "inject/chests/spellstones/nether");

        addTableModifier("spellstones_water_underwater_ruin_big", BuiltInLootTables.UNDERWATER_RUIN_BIG, "inject/chests/spellstones/water");
        addTableModifier("spellstones_water_underwater_ruin_small", BuiltInLootTables.UNDERWATER_RUIN_SMALL, "inject/chests/spellstones/water");
        addTableModifier("spellstones_water_shipwreck_treasure", BuiltInLootTables.SHIPWRECK_TREASURE, "inject/chests/spellstones/water");
        addTableModifier("spellstones_water_buried_treasure", BuiltInLootTables.BURIED_TREASURE, "inject/chests/spellstones/water");

        addTableModifier("spellstones_ender_end_city_treasure", BuiltInLootTables.END_CITY_TREASURE, "inject/chests/spellstones/ender");
    }

    /**
     * 至暗卷轴获取方式。
     * 当前设定：
     * - 至暗卷轴只能在古城发现；
     * - 不再注入堡垒遗迹藏宝室；
     * - 普通古城箱子和古城冰窖箱子都属于古城结构，因此都注入。
     * 对应自定义表：
     * data/enigmatic_legacy/loot_table/inject/chests/darkest_scroll/ancient_city.json
     */
    private void addDarkestScrollModifiers() {
        addTableModifier(
                "darkest_scroll_ancient_city",
                BuiltInLootTables.ANCIENT_CITY,
                "inject/chests/darkest_scroll/ancient_city"
        );

        addTableModifier(
                "darkest_scroll_ancient_city_ice_box",
                BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
                "inject/chests/darkest_scroll/ancient_city"
        );
    }

    /**
     * 大地之心奖励箱获取方式。
     * 按原作者项目 Overworld epic 池复刻。
     * 出现奖励箱：
     * - 地牢
     * - 废弃矿井
     * - 要塞十字路口
     * - 要塞走廊
     * - 沙漠神殿
     * - 丛林神庙
     * - 雪屋地下室
     * - 林地府邸
     * - 沉船补给箱
     * 概率：
     * - 由 InjectLootTableGenerator 中的
     *   earth_heart/overworld_epic 表控制。
     * - 单次抽取约 3.03%。
     * - rolls = 1 ~ 2，综合约 4.5%。
     */
    private void addEarthHeartModifiers() {
        addTableModifier(
                "earth_heart_simple_dungeon",
                BuiltInLootTables.SIMPLE_DUNGEON,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_abandoned_mineshaft",
                BuiltInLootTables.ABANDONED_MINESHAFT,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_stronghold_crossing",
                BuiltInLootTables.STRONGHOLD_CROSSING,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_stronghold_corridor",
                BuiltInLootTables.STRONGHOLD_CORRIDOR,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_desert_pyramid",
                BuiltInLootTables.DESERT_PYRAMID,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_jungle_temple",
                BuiltInLootTables.JUNGLE_TEMPLE,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_igloo_chest",
                BuiltInLootTables.IGLOO_CHEST,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_woodland_mansion",
                BuiltInLootTables.WOODLAND_MANSION,
                "inject/chests/earth_heart/overworld_epic"
        );

        addTableModifier(
                "earth_heart_shipwreck_supply",
                BuiltInLootTables.SHIPWRECK_SUPPLY,
                "inject/chests/earth_heart/overworld_epic"
        );
    }

    /**
     * 大地之心碎片奖励箱获取方式。
     * 按原项目同类 Overworld epic 池概率复刻。
     * 注意：
     * - 这里先使用和大地之心相同的奖励箱范围。
     * - 概率由 InjectLootTableGenerator 中的
     *   earth_heart_fragment/overworld_epic 表控制。
     * - 不在这里写 JsonObject 条件，否则会导致 GlobalLootModifierProvider 编译失败。
     * 概率：
     * - 单次抽取约 3.03%。
     * - rolls = 1 ~ 2，综合约 4.5%。
     */
    private void addEarthHeartFragmentModifiers() {
        addTableModifier(
                "earth_heart_fragment_simple_dungeon",
                BuiltInLootTables.SIMPLE_DUNGEON,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_abandoned_mineshaft",
                BuiltInLootTables.ABANDONED_MINESHAFT,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_stronghold_crossing",
                BuiltInLootTables.STRONGHOLD_CROSSING,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_stronghold_corridor",
                BuiltInLootTables.STRONGHOLD_CORRIDOR,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_desert_pyramid",
                BuiltInLootTables.DESERT_PYRAMID,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_jungle_temple",
                BuiltInLootTables.JUNGLE_TEMPLE,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_igloo_chest",
                BuiltInLootTables.IGLOO_CHEST,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_woodland_mansion",
                BuiltInLootTables.WOODLAND_MANSION,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );

        addTableModifier(
                "earth_heart_fragment_shipwreck_supply",
                BuiltInLootTables.SHIPWRECK_SUPPLY,
                "inject/chests/earth_heart_fragment/overworld_epic"
        );
    }

    /**
     * 不洁圣杯获取方式。
     * 原项目：
     * 不洁圣杯加入主世界类地牢箱子的稀有战利品池。
     */
    private void addUnholyGrailModifiers() {
        addTableModifier("unholy_grail_simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_igloo_chest", BuiltInLootTables.IGLOO_CHEST, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_woodland_mansion", BuiltInLootTables.WOODLAND_MANSION, "inject/chests/unholy_grail/overworld_epic");
        addTableModifier("unholy_grail_shipwreck_supply", BuiltInLootTables.SHIPWRECK_SUPPLY, "inject/chests/unholy_grail/overworld_epic");

        addTableModifier("unholy_grail_underwater_ruin_small", BuiltInLootTables.UNDERWATER_RUIN_SMALL, "inject/chests/unholy_grail/overworld_epic_without_earth_heart");
        addTableModifier("unholy_grail_underwater_ruin_big", BuiltInLootTables.UNDERWATER_RUIN_BIG, "inject/chests/unholy_grail/overworld_epic_without_earth_heart");
        addTableModifier("unholy_grail_pillager_outpost", BuiltInLootTables.PILLAGER_OUTPOST, "inject/chests/unholy_grail/overworld_epic_without_earth_heart");
    }

    /**
     * 添加一个战利品表注入规则。
     * name：
     * - 生成的 loot modifier 文件名。
     * targetTable：
     * - 被注入的原版箱子战利品表。
     * - 1.21.1 中 BuiltInLootTables 返回的是 ResourceKey<LootTable>。
     * injectPath：
     * - 要追加进去的自定义 loot table 路径，不带 .json 后缀。
     */
    private void addTableModifier(String name, ResourceKey<LootTable> targetTable, String injectPath) {
        addTableModifier(name, targetTable.location(), injectPath);
    }

    /**
     * 添加一个战利品表注入规则。
     *
     * <p>外部模组的战利品表没有 BuiltInLootTables 常量，使用 ResourceLocation 重载处理。</p>
     */
    private void addTableModifier(String name, ResourceLocation targetTable, String injectPath) {
        add(
                name,
                new ConfigurableAddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(targetTable).build()
                        },
                        ResourceKey.create(
                                Registries.LOOT_TABLE,
                                ResourceLocation.fromNamespaceAndPath(
                                        EnigmaticLegacy.MODID,
                                        injectPath
                                )
                        )
                )
        );
    }

    /**
     * 注册唯一的 GlobalLootModifierProvider。
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new GlobalLootModifierGenerator(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider()
                )
        );
    }


    /**
     * 忘却冰晶获取方式。
     *
     * <p>按本项目术石战利品表风格处理：
     * 注入目标只负责“出现在哪些箱子”，实际概率由 inject/chests/spellstones/ice 控制。</p>
     */
    private void addForgottenIceCrystalModifiers() {
        addTableModifier(
                "spellstones_ice_igloo_chest",
                BuiltInLootTables.IGLOO_CHEST,
                "inject/chests/spellstones/ice"
        );

        addTableModifier(
                "spellstones_ice_ancient_city_ice_box",
                BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
                "inject/chests/spellstones/ice"
        );
    }

}
