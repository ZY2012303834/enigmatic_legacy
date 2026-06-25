package org.enigmatic_legacy.generator.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpellstoneLootTableGenerator implements DataProvider {
    private final PackOutput output;

    public SpellstoneLootTableGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(saveSpellstoneTable(cachedOutput, "air_earthen", -12.0D, 1.0D,
                entry(ModItems.GOLEM_HEART.get(), 35),
                entry(ModItems.ANGEL_BLESSING.get(), 65)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "ender_earthen", -10.0D, 1.0D,
                entry(ModItems.EYE_OF_NEBULA.get(), 35),
                entry(ModItems.GOLEM_HEART.get(), 65)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "air", -10.0D, 1.0D,
                entry(ModItems.ANGEL_BLESSING.get(), 100)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "earthen", -20.0D, 1.0D,
                entry(ModItems.GOLEM_HEART.get(), 100)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "nether", -24.0D, 1.0D,
                entry(ModItems.BLAZING_CORE.get(), 100)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "water", -20.0D, 1.0D,
                entry(ModItems.OCEAN_STONE.get(), 100)
        ));

        futures.add(saveSpellstoneTable(cachedOutput, "ender", -12.0D, 1.0D,
                entry(ModItems.EYE_OF_NEBULA.get(), 90),
                entry(ModItems.VOID_PEARL.get(), 10)
        ));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> saveSpellstoneTable(
            CachedOutput cachedOutput,
            String name,
            double minRolls,
            double maxRolls,
            JsonObject... entries
    ) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:chest");

        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();

        JsonObject rolls = new JsonObject();
        rolls.addProperty("type", "minecraft:uniform");
        rolls.addProperty("min", minRolls);
        rolls.addProperty("max", maxRolls);
        pool.add("rolls", rolls);

        JsonArray entryArray = new JsonArray();
        for (JsonObject entry : entries) {
            entryArray.add(entry);
        }
        pool.add("entries", entryArray);
        pools.add(pool);
        table.add("pools", pools);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("loot_table")
                .resolve("inject")
                .resolve("chests")
                .resolve("spellstones")
                .resolve(name + ".json");

        return DataProvider.saveStable(cachedOutput, table, path);
    }

    private static JsonObject entry(ItemLike item, int weight) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", item.asItem().builtInRegistryHolder().key().location().toString());
        entry.addProperty("weight", weight);
        return entry;
    }

    @Override
    public @NotNull String getName() {
        return "Spellstone Dungeon Loot Tables";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new SpellstoneLootTableGenerator(event.getGenerator().getPackOutput())
        );
    }
}
