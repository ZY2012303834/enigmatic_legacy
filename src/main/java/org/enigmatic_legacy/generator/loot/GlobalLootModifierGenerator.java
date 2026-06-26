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
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;

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
        addDarkestScrollModifiers();
        addUnholyGrailModifiers();
        addForbiddenFruitModifiers();
        addAstralDustModifiers();
        addEtheriumOreModifiers();
        addMendingMixtureModifiers();

        // 大地之心奖励箱获取方式。
        // 按原作者项目的 Overworld epic 池注入范围复刻。
        addEarthHeartModifiers();

        // 大地之心碎片奖励箱获取方式
        // 先按原项目奖励箱范围与概率注入，保证编译与数据生成正常。
        addEarthHeartFragmentModifiers();

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
     * 原项目：
     * 至暗卷轴只注入堡垒遗迹藏宝室箱子。
     * 对应自定义表：
     * data/enigmatic_legacy/loot_table/inject/chests/darkest_scroll/bastion_treasure.json
     */
    private void addDarkestScrollModifiers() {
        addTableModifier(
                "darkest_scroll_bastion_treasure",
                BuiltInLootTables.BASTION_TREASURE,
                "inject/chests/darkest_scroll/bastion_treasure"
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
        add(
                name,
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(targetTable.location()).build()
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
}