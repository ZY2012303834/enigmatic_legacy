package org.enigmatic_legacy.generator.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
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

/**
 * Custom loot tables injected by {@link GlobalLootModifierGenerator}.
 */
public class InjectLootTableGenerator implements DataProvider {
    private final PackOutput output;

    public InjectLootTableGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        addSpellstoneTables(cachedOutput, futures);
        addDarkestScrollTables(cachedOutput, futures);
        addUnholyGrailTables(cachedOutput, futures);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void addSpellstoneTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "spellstones/air_earthen", 0.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 35),
                itemEntry(ModItems.ANGEL_BLESSING.get(), 65)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/ender_earthen", 0.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 35),
                itemEntry(ModItems.GOLEM_HEART.get(), 65)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/air", 0.0D, 1.0D,
                itemEntry(ModItems.ANGEL_BLESSING.get(), 100)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/earthen", 0.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 100)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/nether", 0.0D, 1.0D,
                itemEntry(ModItems.BLAZING_CORE.get(), 100)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/water", 0.0D, 1.0D,
                itemEntry(ModItems.OCEAN_STONE.get(), 100)
        ));
        futures.add(saveTable(cachedOutput, "spellstones/ender", 0.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 90),
                itemEntry(ModItems.VOID_PEARL.get(), 10)
        ));
    }

    private void addDarkestScrollTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "darkest_scroll/bastion_treasure", 0.0D, 1.0D,
                itemEntry(ModItems.DARKEST_SCROLL.get(), 100)
        ));
    }

    private void addUnholyGrailTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic", 1.0D, 2.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(230)
        ));
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic_without_earth_heart", 1.0D, 2.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(223)
        ));
    }

    private CompletableFuture<?> saveTable(
            CachedOutput cachedOutput,
            String path,
            double minRolls,
            double maxRolls,
            JsonObject... entries
    ) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:chest");

        JsonObject rolls = new JsonObject();
        rolls.addProperty("type", "minecraft:uniform");
        rolls.addProperty("min", minRolls);
        rolls.addProperty("max", maxRolls);

        JsonArray entryArray = new JsonArray();
        for (JsonObject entry : entries) {
            entryArray.add(entry);
        }

        JsonObject pool = new JsonObject();
        pool.add("rolls", rolls);
        pool.add("entries", entryArray);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        table.add("pools", pools);

        return DataProvider.saveStable(cachedOutput, table, outputPath(path));
    }

    private Path outputPath(String path) {
        return output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("loot_table")
                .resolve("inject")
                .resolve("chests")
                .resolve(path + ".json");
    }

    private static JsonObject itemEntry(ItemLike item, int weight) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
        entry.addProperty("weight", weight);
        return entry;
    }

    private static JsonObject emptyEntry(int weight) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:empty");
        entry.addProperty("weight", weight);
        return entry;
    }

    @Override
    public @NotNull String getName() {
        return "Injected Chest Loot Tables";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new InjectLootTableGenerator(event.getGenerator().getPackOutput())
        );
    }
}
