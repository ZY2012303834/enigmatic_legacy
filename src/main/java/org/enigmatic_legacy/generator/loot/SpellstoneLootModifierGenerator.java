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

public class SpellstoneLootModifierGenerator extends GlobalLootModifierProvider {
    public SpellstoneLootModifierGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, EnigmaticLegacy.MODID);
    }

    @Override
    protected void start() {
        addSpellstoneModifier("spellstones_air_earthen_desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, "air_earthen");
        addSpellstoneModifier("spellstones_air_earthen_jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, "air_earthen");

        addSpellstoneModifier("spellstones_ender_earthen_stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, "ender_earthen");
        addSpellstoneModifier("spellstones_ender_earthen_stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, "ender_earthen");

        addSpellstoneModifier("spellstones_air_village_temple", BuiltInLootTables.VILLAGE_TEMPLE, "air");

        addSpellstoneModifier("spellstones_earthen_simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, "earthen");
        addSpellstoneModifier("spellstones_earthen_abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, "earthen");
        addSpellstoneModifier("spellstones_earthen_village_armorer", BuiltInLootTables.VILLAGE_ARMORER, "earthen");

        addSpellstoneModifier("spellstones_nether_bridge", BuiltInLootTables.NETHER_BRIDGE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_treasure", BuiltInLootTables.BASTION_TREASURE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_other", BuiltInLootTables.BASTION_OTHER, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_bridge", BuiltInLootTables.BASTION_BRIDGE, "nether");
        addSpellstoneModifier("spellstones_nether_bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE, "nether");
        addSpellstoneModifier("spellstones_nether_ruined_portal", BuiltInLootTables.RUINED_PORTAL, "nether");

        addSpellstoneModifier("spellstones_water_underwater_ruin_big", BuiltInLootTables.UNDERWATER_RUIN_BIG, "water");
        addSpellstoneModifier("spellstones_water_underwater_ruin_small", BuiltInLootTables.UNDERWATER_RUIN_SMALL, "water");
        addSpellstoneModifier("spellstones_water_shipwreck_treasure", BuiltInLootTables.SHIPWRECK_TREASURE, "water");
        addSpellstoneModifier("spellstones_water_buried_treasure", BuiltInLootTables.BURIED_TREASURE, "water");

        addSpellstoneModifier("spellstones_ender_end_city_treasure", BuiltInLootTables.END_CITY_TREASURE, "ender");
    }

    private void addSpellstoneModifier(String name, ResourceKey<LootTable> targetTable, String injectTable) {
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
                                        "inject/chests/spellstones/" + injectTable
                                )
                        )
                )
        );
    }

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
