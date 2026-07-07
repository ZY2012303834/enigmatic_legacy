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
        addForgottenIceCrystalTables(cachedOutput, futures);
        addRevivalLeavesTables(cachedOutput, futures);

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

        // 灵液滴
        addIchorDropletTables(cachedOutput, futures);

        // 星尘
        addAstralDustTables(cachedOutput, futures);

        // 以太矿石
        addEtheriumOreTables(cachedOutput, futures);

        // 壮丽鞘翅
        addMajesticElytraTables(cachedOutput, futures);

        // 天体果实
        addAstralFruitTables(cachedOutput, futures);

        // 修补混合物
        addMendingMixtureTables(cachedOutput, futures);

        // 救赎药水
        addRedemptionPotionTables(cachedOutput, futures);

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

        // 古旧书袋
        addAntiqueBookBagTables(cachedOutput, futures);

        // 地牢浮现之时：天际挑战者结构中的天使之祝。
        addDungeonsAriseTables(cachedOutput, futures);


        /*
         * 等待所有 loot table 文件全部写入完成。
         */
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * 生成复苏之叶 loot table。
     *
     * <p>Enigmatic Addons 原项目把复苏之叶加入丛林神庙战利品。
     * 当前项目使用统一的术石箱子概率：15% 出现，85% 空结果。</p>
     */
    private void addRevivalLeavesTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "revival_leaf/jungle_temple",
                1.0D,
                1.0D,
                itemEntry(ModItems.REVIVAL_LEAVES.get(), 15),
                emptyEntry(85)
        ));
    }

    /**
     * 生成地牢浮现之时兼容用 loot table。
     *
     * <p>天际挑战者是空中/天界主题结构，因此只注入天使之祝，不复用通用空气术石表。
     * 概率保持和本项目普通空气术石箱子一致：15% 出现，85% 空结果。</p>
     */
    private void addDungeonsAriseTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "angel_blessing/heavenly_challenger",
                1.0D,
                1.0D,
                itemEntry(ModItems.ANGEL_BLESSING.get(), 15),
                emptyEntry(85)
        ));
    }

    private void addAntiqueBookBagTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "antique_book_bag/overworld_epic",
                1.0D,
                1.0D,
                itemEntry(ModItems.ANTIQUE_BOOK_BAG.get(), 5),
                emptyEntry(95)
        ));
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
                5.0D,

                itemEntry(ModItems.EARTH_HEART_FRAGMENT.get(), 74),
                emptyEntry(933)
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
        futures.add(
                saveTable(
                        cachedOutput,
                        "earth_heart/overworld_epic",
                        1.0D,
                        1.0D,

                        itemEntry(ModItems.EARTH_HEART.get(), 15),
                        emptyEntry(85)
                )
        );
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
                itemEntry(ModItems.MENDING_MIXTURE.get(), 5),
                emptyEntry(95)
        ));
    }

    // 救赎药水
    private void addRedemptionPotionTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "redemption_potion/village",
                1.0D,
                1.0D,
                itemEntry(ModItems.REDEMPTION_POTION.get(), 4),
                emptyEntry(96)
        ));
    }

    /**
     * 生成以太矿石 loot table。
     * 获取方式：
     * - 末地城宝藏箱。
     * 当前设置：
     * - rolls 固定为 1；
     * - 以太矿石权重 = 10；
     * - empty 空条目权重 = 90；
     * - 出现概率 = 10 / (10 + 90) = 10%；
     * - 出现时数量 = 1~3 个。
     * 效果：
     * - 每个末地城宝藏箱有 10% 概率出现以太矿石；
     * - 成功出现时数量为 1~3 个；
     * - 因为 rolls 固定为 1，所以一个箱子最多只会出现 1 组以太矿石；
     * - 因此单个箱子最多出现 3 个以太矿石。
     */
    private void addEtheriumOreTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "etherium_ore/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.ETHERIUM_ORE.get(), 10, 1.0D, 3.0D),
                emptyEntry(90)
        ));
    }

    private void addMajesticElytraTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "majestic_elytra/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.MAJESTIC_ELYTRA.get(), 4),
                emptyEntry(96)
        ));
    }

    private void addAstralFruitTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "astral_fruit/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.ASTRAL_FRUIT.get(), 4),
                emptyEntry(96)
        ));
    }

    /**
     * 生成术石 loot table。
     * 概率调整：
     * 1. 所有术石箱子的“任意术石出现概率”从 10% 提高到 15%；
     * 2. 单术石表：目标术石 15%，空结果 85%；
     * 3. 多术石表：保持原有内部比例，只提高总出现概率；
     * 4. rolls 固定为 1，所以每个箱子最多只会出现 1 个术石。
     */
    private void addSpellstoneTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        /*
         * 空气 + 大地术石表。
         *
         * 用途：
         * - 沙漠神殿；
         * - 丛林神庙。
         *
         * 原比例：
         * - 魔像之心：35%
         * - 天使之祝：65%
         *
         * 当前总出现概率：
         * - 任意术石：15%
         *
         * 具体概率：
         * - 魔像之心：105 / 2000 = 5.25%
         * - 天使之祝：195 / 2000 = 9.75%
         * - 空结果：1700 / 2000 = 85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/air_earthen",
                1.0D,
                1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 105),
                itemEntry(ModItems.ANGEL_BLESSING.get(), 195),
                emptyEntry(1700)
        ));

        /*
         * 末影 + 大地术石表。
         *
         * 用途：
         * - 要塞走廊；
         * - 要塞十字路口。
         *
         * 原比例：
         * - 星云之眼：35%
         * - 魔像之心：65%
         *
         * 当前总出现概率：
         * - 任意术石：15%
         *
         * 具体概率：
         * - 星云之眼：105 / 2000 = 5.25%
         * - 魔像之心：195 / 2000 = 9.75%
         * - 空结果：1700 / 2000 = 85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/ender_earthen",
                1.0D,
                1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 105),
                itemEntry(ModItems.GOLEM_HEART.get(), 195),
                emptyEntry(1700)
        ));

        /*
         * 空气术石表。
         *
         * 用途：
         * - 村庄教堂箱子。
         *
         * 当前概率：
         * - 天使之祝：15%
         * - 空结果：85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/air",
                1.0D,
                1.0D,
                itemEntry(ModItems.ANGEL_BLESSING.get(), 15),
                emptyEntry(85)
        ));

        /*
         * 大地术石表。
         *
         * 用途：
         * - 地牢；
         * - 废弃矿井；
         * - 村庄盔甲匠箱子。
         *
         * 当前概率：
         * - 魔像之心：15%
         * - 空结果：85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/earthen",
                1.0D,
                1.0D,
                itemEntry(ModItems.GOLEM_HEART.get(), 15),
                emptyEntry(85)
        ));

        /*
         * 下界术石表。
         *
         * 用途：
         * - 下界要塞；
         * - 堡垒藏宝室；
         * - 堡垒普通箱；
         * - 堡垒桥；
         * - 堡垒疣猪兽棚；
         * - 废弃传送门。
         *
         * 当前概率：
         * - 烈焰之核：15%
         * - 空结果：85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/nether",
                1.0D,
                1.0D,
                itemEntry(ModItems.BLAZING_CORE.get(), 15),
                emptyEntry(85)
        ));

        /*
         * 海洋术石表。
         *
         * 用途：
         * - 大型水下遗迹；
         * - 小型水下遗迹；
         * - 沉船宝藏；
         * - 埋藏的宝藏。
         *
         * 当前概率：
         * - 海洋意志：15%
         * - 空结果：85%
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/water",
                1.0D,
                1.0D,
                itemEntry(ModItems.OCEAN_STONE.get(), 15),
                emptyEntry(85)
        ));

        /*
         * 末影术石表。
         *
         * 用途：
         * - 末地城宝藏箱。
         *
         * 当前总出现概率：
         * - 任意术石：20%
         *
         * 具体概率：
         * - 星云之眼：140 / 1000 = 14%
         * - 虚空珍珠：60 / 1000 = 6%
         * - 空结果：800 / 1000 = 80%
         *
         * 说明：
         * - 虚空珍珠概率从 2% 提高到 6%；
         * - 末影术石总概率仍然保持 20%；
         * - 每个箱子最多只会出现 1 个术石。
         */
        futures.add(saveTable(
                cachedOutput,
                "spellstones/ender",
                1.0D,
                1.0D,
                itemEntry(ModItems.EYE_OF_NEBULA.get(), 105),
                itemEntry(ModItems.VOID_PEARL.get(), 45),
                emptyEntry(850)
        ));
    }

    /**
     * 生成至暗卷轴 loot table。
     * 获取方式：
     * - 古城宝箱。
     * 当前设置：
     * - rolls 固定为 1；
     * - 至暗卷轴权重 = 8；
     * - empty 空条目权重 = 92；
     * - 出现概率 = 8 / (8 + 92) = 8%；
     * - 每次命中只给 1 个至暗卷轴。
     * 效果：
     * - 每个目标古城箱子有 8% 概率出现至暗卷轴；
     * - 因为 rolls 固定为 1，所以一个箱子最多只会出现 1 个至暗卷轴。
     */
    private void addDarkestScrollTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "darkest_scroll/ancient_city",
                1.0D,
                1.0D,
                itemEntry(ModItems.DARKEST_SCROLL.get(), 8),
                emptyEntry(92)
        ));
    }

    /**
     * 生成不洁圣杯 loot table。
     * 获取方式：
     * - 主世界遗迹类箱子；
     * - 部分水下遗迹 / 掠夺者前哨站箱子。
     * 当前设置：
     * - rolls 固定为 1；
     * - 不洁圣杯权重 = 4；
     * - empty 空条目权重 = 96；
     * - 出现概率 = 4 / (4 + 96) = 4%；
     * - 每次命中只给 1 个不洁圣杯。
     * 效果：
     * - 每个目标箱子有 4% 概率出现不洁圣杯；
     * - 因为 rolls 固定为 1，所以一个箱子最多只会出现 1 个不洁圣杯。
     */
    private void addUnholyGrailTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "unholy_grail/overworld_epic",
                1.0D,
                1.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 4),
                emptyEntry(96)
        ));

        futures.add(saveTable(
                cachedOutput,
                "unholy_grail/overworld_epic_without_earth_heart",
                1.0D,
                1.0D,
                itemEntry(ModItems.UNHOLY_GRAIL.get(), 4),
                emptyEntry(96)
        ));
    }

    /**
     * 生成禁忌之果 loot table。
     * 获取方式：
     * - 堡垒遗迹普通相关箱子。
     * 当前设置：
     * - rolls 固定为 1；
     * - 禁忌之果权重 = 4；
     * - empty 空条目权重 = 96；
     * - 出现概率 = 4 / (4 + 96) = 4%；
     * - 每次命中只给 1 个禁忌之果。
     * 效果：
     * - 每个目标箱子有 4% 概率出现禁忌之果；
     * - 因为 rolls 固定为 1，所以一个箱子最多只会出现 1 个禁忌之果。
     */
    private void addForbiddenFruitTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "forbidden_fruit/bastion_common",
                1.0D,
                1.0D,
                itemEntry(ModItems.FORBIDDEN_FRUIT.get(), 4),
                emptyEntry(96)
        ));
    }

    /**
     * 生成灵液滴 loot table。
     * 获取方式：
     * - 下界大多数战利品箱。
     * 当前设置：
     * - rolls 固定为 1；
     * - 灵液滴权重 = 15；
     * - empty 空条目权重 = 85；
     * - 出现概率 = 15 / (15 + 85) = 15%；
     * - 出现时数量 = 1~2 个。
     * 说明：
     * - 这里只有注入用 loot table 本体；
     * - 实际注入到哪些原版箱子，在 GlobalLootModifierGenerator 中处理。
     */
    private void addIchorDropletTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "ichor_droplet/nether_common",
                1.0D,
                1.0D,
                itemEntry(ModItems.ICHOR_DROPLET.get(), 15, 1.0D, 2.0D),
                emptyEntry(85)
        ));
    }

    /**
     * 生成星尘 loot table。
     * 获取方式：
     * - 末地城宝藏箱。
     * 当前设置：
     * - rolls 固定为 1；
     * - 星尘权重 = 20；
     * - empty 空条目权重 = 80；
     * - 出现概率 = 20 / (20 + 80) = 20%；
     * - 数量 = 1~5 个。
     * 效果：
     * - 每个末地城宝藏箱有 20% 概率出现星尘；
     * - 成功出现时数量为 1~5 个；
     * - 因为 rolls 固定为 1，所以一个箱子最多只会出现 1 组星尘。
     */
    private void addAstralDustTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "astral_dust/end_city_treasure",
                1.0D,
                1.0D,
                itemEntry(ModItems.ASTRAL_DUST.get(), 20, 1.0D, 5.0D),
                emptyEntry(80)
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


    /**
     * 生成冰系术石 loot table。
     *
     * <p>按本项目术石统一概率：
     * 忘却冰晶权重 15，空结果权重 85。
     * 即单个注入表触发时，有 15% 概率出现忘却冰晶。</p>
     */
    private void addForgottenIceCrystalTables(CachedOutput cachedOutput, List<CompletableFuture<?>> futures) {
        futures.add(saveTable(
                cachedOutput,
                "spellstones/ice",
                1.0D,
                1.0D,
                itemEntry(ModItems.FORGOTTEN_ICE_CRYSTAL.get(), 15),
                emptyEntry(85)
        ));
    }

}
