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
 * 注入用箱子战利品表生成器。
 * 作用：
 * 生成所有需要被 GlobalLootModifierGenerator 注入到原版箱子里的自定义 loot table。
 * 注意：
 * 这个类只负责生成被注入的 loot table 本体。
 * 它不会直接修改原版箱子。
 * 实际把这些 loot table 注入到原版箱子的逻辑，
 * 在 GlobalLootModifierGenerator 中完成。
 * 生成路径：
 * src/generated/resources/data/enigmatic_legacy/loot_table/inject/chests/...
 */
public class InjectLootTableGenerator implements DataProvider {
    private final PackOutput output;

    public InjectLootTableGenerator(PackOutput output) {
        this.output = output;
    }

    /**
     * DataGenerator 执行入口。
     * runData 时会调用此方法，
     * 并把所有需要生成的 loot table 写入 futures。
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        /*
         * 生成术石相关箱子注入表。
         */
        addSpellstoneTables(cachedOutput, futures);

        /*
         * 生成至暗卷轴箱子注入表。
         */
        addDarkestScrollTables(cachedOutput, futures);

        /*
         * 生成不洁圣杯箱子注入表。
         */
        addUnholyGrailTables(cachedOutput, futures);

        // 禁忌之果
        addForbiddenFruitTables(cachedOutput, futures);

        /*
         * 等待所有 loot table 文件全部写入完成。
         */
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 生成术石类 loot table。
     * 这些表会由 GlobalLootModifierGenerator 注入到不同地牢、神殿、遗迹、堡垒和末地城箱子中。
     * 路径示例：
     * data/enigmatic_legacy/loot_table/inject/chests/spellstones/air_earthen.json
     */
    private void addSpellstoneTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        /*
         * 空气 + 大地类型术石表。
         *
         * 当前用于：
         * - 沙漠神殿
         * - 丛林神庙
         *
         * 条目：
         * - 魔像之心：35 权重
         * - 天使之祝：65 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/air_earthen", 0.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 35),
                itemEntry(ModItems.ANGEL_BLESSING.get(), 65)
        ));

        /*
         * 末影 + 大地类型术石表。
         *
         * 当前用于：
         * - 要塞走廊
         * - 要塞十字路口
         *
         * 条目：
         * - 星云之眼：35 权重
         * - 魔像之心：65 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/ender_earthen", 0.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 35),
                itemEntry(ModItems.GOLEM_HEART.get(), 65)
        ));

        /*
         * 空气类型术石表。
         *
         * 当前用于：
         * - 村庄教堂箱子
         *
         * 条目：
         * - 天使之祝：100 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/air", 0.0D, 1.0D,
                itemEntry(ModItems.ANGEL_BLESSING.get(), 100)
        ));

        /*
         * 大地类型术石表。
         *
         * 当前用于：
         * - 地牢
         * - 废弃矿井
         * - 村庄盔甲匠箱子
         *
         * 条目：
         * - 魔像之心：100 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/earthen", 0.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 100)
        ));

        /*
         * 下界类型术石表。
         *
         * 当前用于：
         * - 下界要塞
         * - 堡垒遗迹各类箱子
         * - 废弃传送门箱子
         *
         * 条目：
         * - 烈焰核心：100 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/nether", 0.0D, 1.0D,
                itemEntry(ModItems.BLAZING_CORE.get(), 100)
        ));

        /*
         * 水域类型术石表。
         *
         * 当前用于：
         * - 大型水下遗迹
         * - 小型水下遗迹
         * - 沉船宝藏
         * - 埋藏的宝藏
         *
         * 条目：
         * - 海洋意志：100 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/water", 0.0D, 1.0D,
                itemEntry(ModItems.OCEAN_STONE.get(), 100)
        ));

        /*
         * 末影类型术石表。
         *
         * 当前用于：
         * - 末地城宝藏
         *
         * 条目：
         * - 星云之眼：90 权重
         * - 虚空珍珠：10 权重
         */
        futures.add(saveTable(cachedOutput, "spellstones/ender", 0.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 90),
                itemEntry(ModItems.VOID_PEARL.get(), 10)
        ));
    }

    /**
     * 生成至暗卷轴 loot table。
     * 原项目获取方式：
     * 至暗卷轴只会在堡垒遗迹藏宝室箱子中概率生成。
     * 当前生成路径：
     * data/enigmatic_legacy/loot_table/inject/chests/darkest_scroll/bastion_treasure.json
     * 注入目标：
     * BuiltInLootTables.BASTION_TREASURE
     */
    private void addDarkestScrollTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "darkest_scroll/bastion_treasure", 0.0D, 1.0D,
                itemEntry(ModItems.DARKEST_SCROLL.get(), 100)
        ));
    }

    /**
     * 生成不洁圣杯 loot table。
     * 原项目获取方式：
     * 不洁圣杯加入主世界类地牢箱子的稀有战利品池。
     * 这里使用：
     * - 不洁圣杯条目；
     * - minecraft:empty 空条目；
     * 通过空条目稀释概率，
     * 避免把原项目整个 epic 池全部复制进来。
     */
    private void addUnholyGrailTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        /*
         * 普通主世界 epic 池。
         *
         * 设计：
         * - 不洁圣杯：1 权重
         * - 空条目：230 权重
         *
         * rolls：
         * - 1 ~ 2
         *
         * 作用：
         * 保持不洁圣杯极低概率出现。
         */
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic", 1.0D, 2.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(230)
        ));

        /*
         * 不包含大地之心权重的主世界 epic 池。
         *
         * 设计：
         * - 不洁圣杯：1 权重
         * - 空条目：223 权重
         *
         * 用于部分原项目中不加入 Earth Heart 的箱子类别，
         * 例如水下遗迹、掠夺者前哨站等。
         */
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic_without_earth_heart", 1.0D, 2.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(223)
        ));
    }

    /**
     * 生成禁忌之果 loot table。
     * 获取方式：
     * 可以在猪灵堡垒中的堡垒桥和堡垒疣猪兽棚找到。
     * 额外说明：
     * 其它堡垒变种中也可能使用相近奖励池，
     * 因此这里额外给 BASTION_OTHER 使用同一张注入表。
     * 当前生成路径：
     * data/enigmatic_legacy/loot_table/inject/chests/forbidden_fruit/bastion_common.json
     */
    private void addForbiddenFruitTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "forbidden_fruit/bastion_common", 0.0D, 1.0D,
                itemEntry(ModItems.FORBIDDEN_FRUIT.get(), 100)
        ));
    }

    /**
     * 保存一个注入用 loot table。
     * 参数说明：
     * path：
     * 输出路径，位于 inject/chests/ 下方，不需要写 .json。
     * minRolls：
     * 最小抽取次数。
     * maxRolls：
     * 最大抽取次数。
     * entries：
     * 该 loot table 内的所有条目。
     */
    private CompletableFuture<?> saveTable(
            CachedOutput cachedOutput,
            String path,
            double minRolls,
            double maxRolls,
            JsonObject... entries
    ) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:chest");

        /*
         * 抽取次数。
         *
         * minecraft:uniform 表示在 min 和 max 之间随机取值。
         */
        JsonObject rolls = new JsonObject();
        rolls.addProperty("type", "minecraft:uniform");
        rolls.addProperty("min", minRolls);
        rolls.addProperty("max", maxRolls);

        /*
         * 写入所有战利品条目。
         */
        JsonArray entryArray = new JsonArray();

        for (JsonObject entry : entries) {
            entryArray.add(entry);
        }

        /*
         * 创建 loot pool。
         */
        JsonObject pool = new JsonObject();
        pool.add("rolls", rolls);
        pool.add("entries", entryArray);

        /*
         * 一个 loot table 可以有多个 pool。
         * 这里每张表只生成一个 pool。
         */
        JsonArray pools = new JsonArray();
        pools.add(pool);
        table.add("pools", pools);

        return DataProvider.saveStable(cachedOutput, table, outputPath(path));
    }

    /**
     * 生成输出路径。
     * 输入：
     * path = "spellstones/ender"
     * 输出：
     * data/enigmatic_legacy/loot_table/inject/chests/spellstones/ender.json
     */
    private Path outputPath(String path) {
        return output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("loot_table")
                .resolve("inject")
                .resolve("chests")
                .resolve(path + ".json");
    }

    /**
     * 创建一个物品条目。
     * item：
     * 要加入 loot table 的物品。
     * weight：
     * 权重。数值越大，被抽中的概率越高。
     */
    private static JsonObject itemEntry(ItemLike item, int weight) {
        JsonObject entry = new JsonObject();

        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
        entry.addProperty("weight", weight);

        return entry;
    }

    /**
     * 创建一个空条目。
     * 用途：
     * 通过 minecraft:empty 稀释稀有物品出现概率。
     * 例如：
     * 不洁圣杯权重 1，空条目权重 230，
     * 就表示大多数情况下不会额外加入不洁圣杯。
     */
    private static JsonObject emptyEntry(int weight) {
        JsonObject entry = new JsonObject();

        entry.addProperty("type", "minecraft:empty");
        entry.addProperty("weight", weight);

        return entry;
    }

    /**
     * DataProvider 名称。
     * runData 日志中会显示这个名称。
     */
    @Override
    public @NotNull String getName() {
        return "Injected Chest Loot Tables";
    }

    /**
     * 注册该 DataProvider。
     * 你的项目没有单独的 DataGenerator.java。
     * 所以需要在 EnigmaticLegacy.java 中通过：
     * modEventBus.addListener(InjectLootTableGenerator::gatherData);
     * 注册这个生成器。
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new InjectLootTableGenerator(event.getGenerator().getPackOutput())
        );
    }
}