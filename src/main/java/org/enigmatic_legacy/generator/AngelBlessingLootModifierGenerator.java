package org.enigmatic_legacy.generator;

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
 * 天使之祝全局战利品修饰器数据生成器。
 */
public class AngelBlessingLootModifierGenerator extends GlobalLootModifierProvider {
    private static final ResourceKey<LootTable> ANGEL_BLESSING_INJECT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(
                    EnigmaticLegacy.MODID,
                    "inject/chests/angel_blessing"
            )
    );

    public AngelBlessingLootModifierGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, EnigmaticLegacy.MODID);
    }

    @Override
    protected void start() {
        add(
                "angel_blessing_desert_pyramid",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        BuiltInLootTables.DESERT_PYRAMID.location()
                                ).build()
                        },
                        ANGEL_BLESSING_INJECT_TABLE
                )
        );

        add(
                "angel_blessing_jungle_temple",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        BuiltInLootTables.JUNGLE_TEMPLE.location()
                                ).build()
                        },
                        ANGEL_BLESSING_INJECT_TABLE
                )
        );
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new AngelBlessingLootModifierGenerator(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider()
                )
        );
    }
}