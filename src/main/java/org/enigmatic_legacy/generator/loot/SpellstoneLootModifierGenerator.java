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
 * 术石战利品注入生成器。
 * 作用：
 * 通过 NeoForge 的 Global Loot Modifier 系统，
 * 把自定义的术石战利品表注入到原版地牢、神殿、堡垒、沉船、末地城等箱子里。
 * 注意：
 * 这个类本身不直接生成术石战利品内容。
 * 具体掉落内容由 SpellstoneLootTableGenerator 生成。
 * 本类负责的是：
 * “把某个自定义术石 loot table 注入到某个原版 loot table 里”。
 * 生成位置：
 * data/enigmatic_legacy/loot_modifiers/*.json
 */
public class SpellstoneLootModifierGenerator extends GlobalLootModifierProvider {

    public SpellstoneLootModifierGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        /*
         * 第三个参数是 modid。
         * 生成的 Global Loot Modifier JSON 会归属到 enigmatic_legacy 命名空间下。
         */
        super(output, lookupProvider, EnigmaticLegacy.MODID);
    }

    /**
     * Global Loot Modifier 生成入口。
     * 每一个 addSpellstoneModifier(...) 都会生成一个 loot modifier。
     * 参数说明：
     * name        生成的 modifier 文件名
     * targetTable 要注入的原版箱子战利品表
     * injectTable 要追加进去的自定义术石战利品表名称
     */
    @Override
    protected void start() {
        /*
         * 沙漠神殿、丛林神庙：
         * 注入空气 + 大地类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/air_earthen.json
         */
        addSpellstoneModifier("spellstones_air_earthen_desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, "air_earthen");
        addSpellstoneModifier("spellstones_air_earthen_jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, "air_earthen");

        /*
         * 要塞走廊、要塞十字路口：
         * 注入末影 + 大地类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/ender_earthen.json
         */
        addSpellstoneModifier("spellstones_ender_earthen_stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, "ender_earthen");
        addSpellstoneModifier("spellstones_ender_earthen_stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, "ender_earthen");

        /*
         * 村庄教堂：
         * 注入空气类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/air.json
         */
        addSpellstoneModifier("spellstones_air_village_temple", BuiltInLootTables.VILLAGE_TEMPLE, "air");

        /*
         * 地牢、废弃矿井、村庄盔甲匠：
         * 注入大地类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/earthen.json
         */
        addSpellstoneModifier("spellstones_earthen_simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, "earthen");
        addSpellstoneModifier("spellstones_earthen_abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, "earthen");
        addSpellstoneModifier("spellstones_earthen_village_armorer", BuiltInLootTables.VILLAGE_ARMORER, "earthen");

        /*
         * 下界要塞、堡垒遗迹、废弃传送门：
         * 注入下界类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/nether.json
         */
        addSpellstoneModifier("spellstones_nether_bridge", BuiltInLootTables.NETHER_BRIDGE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_treasure", BuiltInLootTables.BASTION_TREASURE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_other", BuiltInLootTables.BASTION_OTHER, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_bridge", BuiltInLootTables.BASTION_BRIDGE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE, "nether");
        addSpellstoneModifier("spellstones_nether_ruined_portal", BuiltInLootTables.RUINED_PORTAL, "nether");

        /*
         * 水下遗迹、沉船宝藏、埋藏的宝藏：
         * 注入水域类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/water.json
         */
        addSpellstoneModifier("spellstones_water_underwater_ruin_big", BuiltInLootTables.UNDERWATER_RUIN_BIG, "water");
        addSpellstoneModifier("spellstones_water_underwater_ruin_small", BuiltInLootTables.UNDERWATER_RUIN_SMALL, "water");
        addSpellstoneModifier("spellstones_water_shipwreck_treasure", BuiltInLootTables.SHIPWRECK_TREASURE, "water");
        addSpellstoneModifier("spellstones_water_buried_treasure", BuiltInLootTables.BURIED_TREASURE, "water");

        /*
         * 末地城宝藏：
         * 注入末影类型术石表。
         *
         * 对应：
         * inject/chests/spellstones/ender.json
         */
        addSpellstoneModifier("spellstones_ender_end_city_treasure", BuiltInLootTables.END_CITY_TREASURE, "ender");
    }

    /**
     * 添加一个术石战利品注入规则。
     * name:
     * 生成出来的 Global Loot Modifier 文件名。
     * targetTable:
     * 被注入的原版战利品表，例如沙漠神殿、地牢、末地城等。
     * injectTable:
     * 要追加进去的自定义术石战利品表名称。
     * 例如：
     * injectTable = "ender"
     * 最终会指向：
     * enigmatic_legacy:inject/chests/spellstones/ender
     */
    private void addSpellstoneModifier(String name, ResourceKey<LootTable> targetTable, String injectTable) {
        add(
                name,
                new AddTableLootModifier(
                        /*
                         * 条件：
                         * 只有当前正在加载的箱子战利品表 ID 等于 targetTable 时，
                         * 这个 modifier 才会生效。
                         */
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(targetTable.location()).build()
                        },

                        /*
                         * 要追加进去的自定义战利品表。
                         *
                         * 注意：
                         * 这里没有写 .json 后缀。
                         * Minecraft 资源定位符只需要命名空间和路径。
                         */
                        ResourceKey.create(
                                Registries.LOOT_TABLE,
                                ResourceLocation.fromNamespaceAndPath(
                                        EnigmaticLegacy.MODID,
                                        "inject/chests/spellstones/" + injectTable
                                )
                        )
                )
        );
    }

    /**
     * 注册该 Global Loot Modifier Provider。
     * 需要在数据生成入口中调用：
     * SpellstoneLootModifierGenerator.gatherData(event);
     * 否则 runData 不会生成这些 loot modifier JSON。
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new SpellstoneLootModifierGenerator(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider()
                )
        );
    }
}