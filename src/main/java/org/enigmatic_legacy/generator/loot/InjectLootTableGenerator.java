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

        // 星尘
        addAstralDustTables(cachedOutput, futures);

        // 以太矿石
        addEtheriumOreTables(cachedOutput, futures);

        // 修补混合物
        addMendingMixtureTables(cachedOutput, futures);

        // 大地之心
        // 按原作者项目的 Overworld epic 池概率生成。
        // 这里只生成注入用 loot table，具体注入到哪些奖励箱，
        // 在 GlobalLootModifierGenerator 中处理。
        addEarthHeartTables(cachedOutput, futures);

        // 大地之心碎片
        // 按 Enigmatic Addons 的逻辑：
        // 需要玩家佩戴七咒之戒时，才会从主世界地牢 / 遗迹奖励箱中出现。
        // 这里只生成注入用 loot table，具体注入位置在 GlobalLootModifierGenerator 中处理。
        addEarthHeartFragmentTables(cachedOutput, futures);



        /*
         * 等待所有 loot table 文件全部写入完成。
         */
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 生成大地之心碎片 loot table。
     * 按大地之心同类 Overworld epic 池概率复刻：
     * - rolls = 1 ~ 2
     * - 大地之心碎片权重 = 7
     * - empty 权重 = 224
     * - 总权重 = 231
     * 单次抽取概率：
     * - 7 / 231 ≈ 3.03%
     * 综合概率：
     * - rolls = 1 ~ 2，约 4.5%。
     */
    private void addEarthHeartFragmentTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "earth_heart_fragment/overworld_epic",
                1.0D,
                2.0D,
                itemEntry(ModItems.EARTH_HEART_FRAGMENT.get(), 7),
                emptyEntry(224)
        ));
    }

    /**
     * 生成大地之心 loot table。
     * 按原作者项目 Overworld epic 池概率复刻：
     * - rolls = 1 ~ 2
     * - 大地之心权重 = 7
     * - empty 权重 = 224
     * - 总权重 = 231
     * 单次抽取概率：
     * - 7 / 231 ≈ 3.03%
     * 综合概率：
     * - rolls = 1 ~ 2，约 4.5%。
     */
    private void addEarthHeartTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "earth_heart/overworld_epic",
                1.0D,
                2.0D,
                itemEntry(ModItems.EARTH_HEART.get(), 7),
                emptyEntry(224)
        ));
    }

    /**
     * 生成修补混合物 loot table。
     * 原版获取方式：
     * - 修补混合物可以小概率在末地城箱子中找到。
     * 设计：
     * - rolls 固定为 1。
     * - 修补混合物权重 = 1。
     * - empty 空条目权重 = 19。
     * 概率：
     * - 1 / (1 + 19) = 5%
     * 生成路径：
     * data/enigmatic_legacy/loot_table/inject/chests/mending_mixture/end_city_treasure.json
     */
    private void addMendingMixtureTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "mending_mixture/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.MENDING_MIXTURE.get(), 1),
                emptyEntry(19)
        ));
    }

    /**
     * 生成以太矿石 loot table。
     * 获取方式：
     * - 末地城宝藏箱。
     * 调整原因：
     * - 原来的 rolls 是 -11 ~ 2。
     * - 这种写法不直观，实测概率也不好判断。
     * - 原注释写数量 1~2，但代码实际是 1~4。
     * 新设计：
     * - rolls 固定为 1。
     * - 以太矿石权重 = 1。
     * - empty 空条目权重 = 19。
     * 概率：
     * - 1 / (1 + 19) = 5%
     * 数量：
     * - 1~2 个。
     * 注意：
     * - 不改变出现箱子。
     * - 仍然只注入到末地城宝藏箱。
     */
    private void addEtheriumOreTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "etherium_ore/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.ETHERIUM_ORE.get(), 1, 1.0D, 2.0D),
                emptyEntry(19)
        ));
    }

    /**
     * 生成术石类 loot table。
     * 重要调整：
     * - 之前术石出现概率过高。
     * - 原因是每张术石注入表只有术石条目，没有 minecraft:empty 空条目。
     * - 这样只要注入表被抽取，就很容易直接给术石。
     * 新设计：
     * - 每张术石表固定 rolls = 1。
     * - 术石总权重 = 100。
     * - 空条目权重 = 1900。
     * - 所以每次打开对应战利品箱时，术石总出现概率约为：
     *   100 / (100 + 1900) = 5%
     * 效果：
     * - 术石仍然可以从对应结构中获得；
     * - 但不会像之前那样频繁出现；
     * - 多个术石共用一张表时，仍然按原来的内部比例分配。
     * 举例：
     * - air_earthen 表中：
     *   魔像之心 35 权重
     *   天使之祝 65 权重
     *   empty 1900 权重
     *   总术石概率约 5%。
     *   在成功抽中术石的情况下：
     *   魔像之心占 35%
     *   天使之祝占 65%
     */
    private void addSpellstoneTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        /*
         * 空气 + 大地类型术石表。
         *
         * 当前用于：
         * - 沙漠神殿
         * - 丛林神庙
         *
         * 总出现率：
         * - 约 5%
         *
         * 成功出现术石后：
         * - 魔像之心：35%
         * - 天使之祝：65%
         */
        futures.add(saveTable(cachedOutput, "spellstones/air_earthen", 1.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 35),
                itemEntry(ModItems.ANGEL_BLESSING.get(), 65),
                emptyEntry(900)
        ));

        /*
         * 末影 + 大地类型术石表。
         *
         * 当前用于：
         * - 要塞走廊
         * - 要塞十字路口
         *
         * 总出现率：
         * - 约 5%
         *
         * 成功出现术石后：
         * - 星云之眼：35%
         * - 魔像之心：65%
         */
        futures.add(saveTable(cachedOutput, "spellstones/ender_earthen", 1.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 35),
                itemEntry(ModItems.GOLEM_HEART.get(), 65),
                emptyEntry(900)
        ));

        /*
         * 空气类型术石表。
         *
         * 当前用于：
         * - 村庄教堂箱子
         *
         * 总出现率：
         * - 约 5%
         */
        futures.add(saveTable(cachedOutput, "spellstones/air", 1.0D, 1.0D,
                itemEntry(ModItems.ANGEL_BLESSING.get(), 100),
                emptyEntry(900)
        ));

        /*
         * 大地类型术石表。
         *
         * 当前用于：
         * - 地牢
         * - 废弃矿井
         * - 村庄盔甲匠箱子
         *
         * 总出现率：
         * - 约 5%
         */
        futures.add(saveTable(cachedOutput, "spellstones/earthen", 1.0D, 1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 100),
                emptyEntry(900)
        ));

        /*
         * 下界类型术石表。
         *
         * 当前用于：
         * - 下界要塞
         * - 堡垒遗迹各类箱子
         * - 废弃传送门箱子
         *
         * 总出现率：
         * - 约 5%
         */
        futures.add(saveTable(cachedOutput, "spellstones/nether", 1.0D, 1.0D,
                itemEntry(ModItems.BLAZING_CORE.get(), 100),
                emptyEntry(900)
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
         * 总出现率：
         * - 约 5%
         */
        futures.add(saveTable(cachedOutput, "spellstones/water", 1.0D, 1.0D,
                itemEntry(ModItems.OCEAN_STONE.get(), 100),
                emptyEntry(900)
        ));

        /*
         * 末影类型术石表。
         *
         * 当前用于：
         * - 末地城宝藏
         *
         * 总出现率：
         * - 约 5%
         *
         * 成功出现术石后：
         * - 星云之眼：90%
         * - 虚空珍珠：10%
         *
         * 也就是说：
         * - 星云之眼实际约 4.5%
         * - 虚空珍珠实际约 0.5%
         */
        futures.add(saveTable(cachedOutput, "spellstones/ender", 1.0D, 1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 90),
                itemEntry(ModItems.VOID_PEARL.get(), 10),
                emptyEntry(900)
        ));
    }

    /**
     * 生成至暗卷轴 loot table。
     * 获取方式：
     * - 只会在堡垒遗迹藏宝室箱子中概率生成。
     * 调整原因：
     * - 之前只有至暗卷轴条目，没有 empty 空条目。
     * - 一旦该注入表发生抽取，就会直接生成至暗卷轴。
     * - 实测容易显得过多。
     * 新设计：
     * - rolls 固定为 1。
     * - 至暗卷轴权重 = 1。
     * - empty 空条目权重 = 49。
     * 概率：
     * - 1 / (1 + 49) = 2%
     * 注意：
     * - 不改变出现箱子。
     * - 仍然只注入到 BuiltInLootTables.BASTION_TREASURE。
     */
    private void addDarkestScrollTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "darkest_scroll/bastion_treasure", 1.0D, 1.0D,
                itemEntry(ModItems.DARKEST_SCROLL.get(), 1),
                emptyEntry(24)
        ));
    }

    /**
     * 生成不洁圣杯 loot table。
     * 获取方式：
     * - 不改变原本出现的箱子条目。
     * - 仍然由 GlobalLootModifierGenerator 注入到对应主世界类战利品箱中。
     * 调整原因：
     * - 原来的圣杯概率过低：
     *   不洁圣杯权重 = 1
     *   empty 权重 = 230 或 223
     * - 单次抽取概率不到 0.5%。
     * 新设计：
     * - rolls 固定为 1。
     * - 不洁圣杯权重 = 1。
     * - empty 空条目权重 = 49。
     * 概率：
     * - 1 / (1 + 49) = 2%
     * 效果：
     * - 圣杯仍然是稀有物品；
     * - 但不会像之前那样几乎看不到；
     * - 每个被注入的目标箱子约 2% 概率生成不洁圣杯；
     * - rolls 改为 1，可以避免同一个箱子里重复刷出多个圣杯。
     */
    private void addUnholyGrailTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        /*
         * 普通主世界 epic 池。
         *
         * 新概率：
         * - 不洁圣杯：1 权重
         * - 空条目：49 权重
         * - 总概率：2%
         */
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic", 1.0D, 1.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(49)
        ));

        /*
         * 不包含大地之心权重的主世界 epic 池。
         *
         * 新概率：
         * - 不洁圣杯：1 权重
         * - 空条目：49 权重
         * - 总概率：2%
         */
        futures.add(saveTable(cachedOutput, "unholy_grail/overworld_epic_without_earth_heart", 1.0D, 1.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 1),
                emptyEntry(49)
        ));
    }

    /**
     * 生成禁忌之果 loot table。
     * 调整原因：
     * - 之前禁忌之果概率过高。
     * - 原来的表只有禁忌之果，没有 minecraft:empty 空条目。
     * - 只要这个注入表被抽中，就很容易直接生成禁忌之果。
     * 新设计：
     * - rolls 固定为 1。
     * - 禁忌之果权重 = 1。
     * - 空条目权重 = 49。
     * 概率：
     * - 1 / (1 + 49) = 2%
     * 也就是说：
     * - 每个被注入的堡垒箱子，约 2% 概率额外出现禁忌之果。
     * - 不再像之前那样频繁出现。
     */
    private void addForbiddenFruitTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(cachedOutput, "forbidden_fruit/bastion_common", 1.0D, 1.0D,
                itemEntry(ModItems.FORBIDDEN_FRUIT.get(), 1),
                emptyEntry(49)
        ));
    }

    /**
     * 生成星尘 loot table。
     * 获取方式：
     * - 末地城宝藏箱。
     * 调整原因：
     * - 原来星尘权重为 85，empty 权重为 322。
     * - 单次抽取概率约 20.9%。
     * - 并且 rolls 是 1~2，实际出现率会更高。
     * - 对稀有材料来说偏高。
     * 新设计：
     * - rolls 固定为 1。
     * - 星尘权重 = 1。
     * - empty 空条目权重 = 9。
     * 概率：
     * - 1 / (1 + 9) = 10%
     * 数量：
     * - 保持 1~4 个。
     * 注意：
     * - 不改变出现箱子。
     * - 仍然只注入到末地城宝藏箱。
     */
    private void addAstralDustTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "astral_dust/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.ASTRAL_DUST.get(), 1, 1.0D, 4.0D),
                emptyEntry(9)
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
     * 创建一个带随机数量的物品条目。
     */
    private static JsonObject itemEntry(ItemLike item, int weight, double minCount, double maxCount) {
        JsonObject entry = itemEntry(item, weight);

        JsonObject count = new JsonObject();
        count.addProperty("type", "minecraft:uniform");
        count.addProperty("min", minCount);
        count.addProperty("max", maxCount);

        JsonObject setCount = new JsonObject();
        setCount.addProperty("function", "minecraft:set_count");
        setCount.add("count", count);

        JsonArray functions = new JsonArray();
        functions.add(setCount);

        entry.add("functions", functions);
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