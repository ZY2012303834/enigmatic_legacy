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

/**
 * Curios 数据生成器。
 *
 * <p>这个类负责生成 Curios 模组需要的数据文件，
 * 例如戒指栏位、玩家实体可用栏位，以及哪些物品可以放进戒指栏。</p>
 *
 * <p>生成后的文件会放在：</p>
 * <ul>
 *     <li>{@code src/generated/resources/data/enigmatic_legacy/curios/slots/ring.json}</li>
 *     <li>{@code src/generated/resources/data/enigmatic_legacy/curios/entities/player.json}</li>
 *     <li>{@code src/generated/resources/data/curios/tags/item/ring.json}</li>
 * </ul>
 */
public class CuriosGenerator implements DataProvider {

    /**
     * 数据生成输出对象。
     *
     * <p>通过它可以拿到数据包输出目录，
     * 最终把 JSON 文件保存到 {@code src/generated/resources} 下。</p>
     */
    private final PackOutput output;

    /**
     * 创建 Curios 数据生成器。
     *
     * @param output 数据生成输出对象
     */
    public CuriosGenerator(PackOutput output) {
        this.output = output;
    }

    /**
     * 执行 Curios 数据生成。
     *
     * <p>这里会同时生成三个文件：</p>
     * <ul>
     *     <li>戒指栏位类型配置：控制 ring 栏位数量、顺序和图标；</li>
     *     <li>玩家实体栏位配置：让玩家拥有 ring 栏位；</li>
     *     <li>Curios 物品标签：让铁指环、七咒之戒等物品可以放进 ring 栏位。</li>
     * </ul>
     *
     * @param cachedOutput Minecraft 数据生成缓存输出
     * @return 所有数据生成任务的异步结果
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // 注册 ring 戒指栏位类型，控制玩家默认有几个戒指栏。
        futures.add(generateRingSlotType(cachedOutput));

        // 为玩家实体分配 ring 戒指栏位。
        futures.add(generatePlayerRingSlot(cachedOutput));

        // 生成 curios:ring 标签，让戒指物品可以放入 ring 栏位。
        futures.add(generateRingTag(cachedOutput));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 生成玩家 Curios 栏位配置。
     *
     * <p>生成路径：</p>
     * <pre>
     * src/generated/resources/data/enigmatic_legacy/curios/entities/player.json
     * </pre>
     *
     * <p>作用：</p>
     * <p>告诉 Curios：玩家实体拥有 {@code ring} 戒指栏位。</p>
     *
     * @param cachedOutput Minecraft 数据生成缓存输出
     * @return 数据生成异步结果
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
     * 生成 ring 戒指栏位类型配置。
     *
     * <p>生成路径：</p>
     * <pre>
     * src/generated/resources/data/enigmatic_legacy/curios/slots/ring.json
     * </pre>
     *
     * <p>作用：</p>
     * <p>设置 {@code ring} 类型栏位的数量、排序和空槽图标。</p>
     *
     * <p>当前 {@code size = 5}，表示玩家拥有 5 个戒指栏位。
     * 如果只想要 1 个戒指栏，把它改成 {@code 1}。</p>
     *
     * @param cachedOutput Minecraft 数据生成缓存输出
     * @return 数据生成异步结果
     */
    private CompletableFuture<?> generateRingSlotType(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();

        // 设置玩家拥有的 ring 戒指栏数量。
        // 5 = 添加 5 个戒指栏位。
        json.addProperty("size", 5);

        // SET 表示直接设置最终数量。
        // 如果有其他数据包也修改 ring 槽位，SET 会覆盖为这里指定的数量。
        json.addProperty("operation", "SET");

        // 控制该槽位在 Curios GUI 中的排序。
        // 数字越小越靠前。
        json.addProperty("order", 100);

        // 使用 Curios 自带的戒指空槽图标。
        json.addProperty("icon", "curios:slot/empty_ring_slot");

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("curios")
                .resolve("slots")
                .resolve("ring.json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * 生成 Curios 戒指物品标签。
     *
     * <p>生成路径：</p>
     * <pre>
     * src/generated/resources/data/curios/tags/item/ring.json
     * </pre>
     *
     * <p>作用：</p>
     * <p>Curios 会读取 {@code curios:ring} 物品标签。
     * 被加入这个标签的物品，可以放入 {@code ring} 戒指栏位。</p>
     *
     * @param cachedOutput Minecraft 数据生成缓存输出
     * @return 数据生成异步结果
     */
    private CompletableFuture<?> generateRingTag(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);

        JsonArray values = new JsonArray();

        // 将戒指类物品加入 curios:ring 标签。
        values.add(EnigmaticLegacy.MODID + ":iron_ring");
        values.add(EnigmaticLegacy.MODID + ":cursed_ring");
        values.add(EnigmaticLegacy.MODID + ":cursed_ring");

        json.add("values", values);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve("curios")
                .resolve("tags")
                .resolve("item")
                .resolve("ring.json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * 返回当前数据生成器的名称。
     *
     * <p>这个名称会显示在 Gradle runData 的日志中，
     * 方便你知道当前正在执行哪个数据生成器。</p>
     *
     * @return 数据生成器名称
     */
    @Override
    public @NotNull String getName() {
        return "Curios 数据生成器";
    }

    /**
     * 把 Curios 数据生成器注册到 NeoForge 数据生成事件中。
     *
     * <p>这个方法通常会在主类的数据生成事件里被调用。</p>
     *
     * @param event NeoForge 数据生成事件
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new CuriosGenerator(event.getGenerator().getPackOutput())
        );
    }
}