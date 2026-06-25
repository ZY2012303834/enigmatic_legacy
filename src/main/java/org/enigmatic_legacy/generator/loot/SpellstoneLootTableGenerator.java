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
 * 术石地牢战利品表生成器。
 *
 * 作用：
 * 通过 DataGenerator 自动生成术石相关的地牢箱子注入战利品表。
 *
 * 生成位置：
 * data/enigmatic_legacy/loot_table/inject/chests/spellstones/*.json
 *
 * 说明：
 * 这里生成的不是普通方块/实体掉落表，
 * 而是用于后续注入地牢箱子战利品池的自定义 loot table。
 */
public class SpellstoneLootTableGenerator implements DataProvider {
    private final PackOutput output;

    public SpellstoneLootTableGenerator(PackOutput output) {
        this.output = output;
    }

    /**
     * DataGenerator 执行入口。
     *
     * 每一个 saveSpellstoneTable(...) 都会生成一个单独的术石战利品表。
     *
     * 参数说明：
     * name      生成的 json 文件名
     * minRolls  最小抽取次数
     * maxRolls  最大抽取次数
     * entries   可抽取的物品条目和权重
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        /*
         * 空气 + 大地类型战利品表。
         *
         * 掉落池：
         * - 魔像之心：35 权重
         * - 天使祝福：65 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "air_earthen", -12.0D, 1.0D,
                entry(ModItems.GOLEM_HEART.get(), 35),
                entry(ModItems.ANGEL_BLESSING.get(), 65)
        ));

        /*
         * 末影 + 大地类型战利品表。
         *
         * 掉落池：
         * - 星云之眼：35 权重
         * - 魔像之心：65 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "ender_earthen", -10.0D, 1.0D,
                entry(ModItems.EYE_OF_NEBULA.get(), 35),
                entry(ModItems.GOLEM_HEART.get(), 65)
        ));

        /*
         * 空气类型战利品表。
         *
         * 掉落池：
         * - 天使祝福：100 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "air", -10.0D, 1.0D,
                entry(ModItems.ANGEL_BLESSING.get(), 100)
        ));

        /*
         * 大地类型战利品表。
         *
         * 掉落池：
         * - 魔像之心：100 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "earthen", -20.0D, 1.0D,
                entry(ModItems.GOLEM_HEART.get(), 100)
        ));

        /*
         * 下界类型战利品表。
         *
         * 掉落池：
         * - 炽热核心：100 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "nether", -24.0D, 1.0D,
                entry(ModItems.BLAZING_CORE.get(), 100)
        ));

        /*
         * 水域类型战利品表。
         *
         * 掉落池：
         * - 海洋之石：100 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "water", -20.0D, 1.0D,
                entry(ModItems.OCEAN_STONE.get(), 100)
        ));

        /*
         * 末影类型战利品表。
         * 掉落池：
         * - 星云之眼：90 权重
         * - 虚空珍珠：10 权重
         */
        futures.add(saveSpellstoneTable(cachedOutput, "ender", -12.0D, 1.0D,
                entry(ModItems.EYE_OF_NEBULA.get(), 90),
                entry(ModItems.VOID_PEARL.get(), 10)
        ));

        /*
         * 等待所有战利品表文件全部写入完成。
         */
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 保存一个术石战利品表。
     * 注意：
     * 当前 minRolls 有负数，这是按照你现有逻辑保留的。
     * 如果后续游戏启动时报 loot table 数值非法，再统一改为 0 或正数。
     */
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

        /*
         * 设置抽取次数。
         * minecraft:uniform 表示在 min 和 max 之间随机取值。
         */
        JsonObject rolls = new JsonObject();
        rolls.addProperty("type", "minecraft:uniform");
        rolls.addProperty("min", minRolls);
        rolls.addProperty("max", maxRolls);
        pool.add("rolls", rolls);

        /*
         * 写入该战利品池中的所有物品条目。
         */
        JsonArray entryArray = new JsonArray();
        for (JsonObject entry : entries) {
            entryArray.add(entry);
        }

        pool.add("entries", entryArray);
        pools.add(pool);
        table.add("pools", pools);

        /*
         * 输出路径：
         * src/generated/resources/data/enigmatic_legacy/loot_table/inject/chests/spellstones/{name}.json
         */
        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("loot_table")
                .resolve("inject")
                .resolve("chests")
                .resolve("spellstones")
                .resolve(name + ".json");

        return DataProvider.saveStable(cachedOutput, table, path);
    }

    /**
     * 创建一个战利品条目。
     * item   要加入战利品池的物品
     * weight 权重，数值越大，被抽中的概率越高
     */
    private static JsonObject entry(ItemLike item, int weight) {
        JsonObject entry = new JsonObject();

        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
        entry.addProperty("weight", weight);

        return entry;
    }

    /**
     * DataProvider 名称。
     * Gradle runData 输出日志里会显示这个名字。
     */
    @Override
    public @NotNull String getName() {
        return "Spellstone Dungeon Loot Tables";
    }

    /**
     * 注册该 DataProvider。
     * 需要在主 DataGenerator 注册流程里调用这个方法，
     * 否则 runData 不会生成这些战利品表。
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new SpellstoneLootTableGenerator(event.getGenerator().getPackOutput())
        );
    }
}