package org.enigmatic_legacy.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * 天使之祝额外战利品表数据生成器。
 */
public class AngelBlessingLootTableGenerator implements DataProvider {
    private final PackOutput output;

    public AngelBlessingLootTableGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:chest");

        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0D);

        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", EnigmaticLegacy.MODID + ":angel_blessing");

        JsonArray conditions = new JsonArray();
        JsonObject chance = new JsonObject();
        chance.addProperty("condition", "minecraft:random_chance");
        chance.addProperty("chance", 0.08D);
        conditions.add(chance);

        entry.add("conditions", conditions);
        entries.add(entry);

        pool.add("entries", entries);
        pools.add(pool);
        table.add("pools", pools);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("loot_table")
                .resolve("inject")
                .resolve("chests")
                .resolve("angel_blessing.json");

        return DataProvider.saveStable(cachedOutput, table, path);
    }

    @Override
    public @NotNull String getName() {
        return "Angel Blessing Inject Loot Table";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new AngelBlessingLootTableGenerator(event.getGenerator().getPackOutput())
        );
    }
}