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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CuriosGenerator implements DataProvider {

    private final PackOutput output;

    public CuriosGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // 为玩家生成 Curios 戒指栏位
        futures.add(generatePlayerRingSlot(cachedOutput));

        // 生成 curios:ring 标签，让七咒之戒可以放入戒指栏位
        futures.add(generateCursedRingTag(cachedOutput));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 生成玩家 Curios 栏位配置。
     *
     * 生成路径：
     * src/generated/resources/data/enigmatic_legacy/curios/entities/player.json
     *
     * 作用：
     * 给玩家实体添加 ring 戒指栏位。
     */
    private CompletableFuture<?> generatePlayerRingSlot(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();

        JsonArray entities = new JsonArray();
        entities.add("player");

        JsonArray slots = new JsonArray();
        slots.add("ring");

        json.add("entities", entities);
        json.add("slots", slots);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("curios")
                .resolve("entities")
                .resolve("player.json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * 生成 Curios 戒指标签。
     *
     * 生成路径：
     * src/generated/resources/data/curios/tags/item/ring.json
     *
     * 作用：
     * Curios 会读取 curios:ring 标签。
     * 加入该标签的物品可以被放入 ring 戒指栏位。
     */
    private CompletableFuture<?> generateCursedRingTag(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);

        JsonArray values = new JsonArray();

        // 将七咒之戒加入 curios:ring 标签
        values.add(EnigmaticLegacy.MODID + ":cursed_ring");

        json.add("values", values);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve("curios")
                .resolve("tags")
                .resolve("item")
                .resolve("ring.json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    @Override
    public @NotNull String getName() {
        return "Curios 数据生成器";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new CuriosGenerator(event.getGenerator().getPackOutput())
        );
    }
}