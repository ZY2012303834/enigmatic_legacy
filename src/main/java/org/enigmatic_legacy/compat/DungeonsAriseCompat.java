package org.enigmatic_legacy.compat;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * When Dungeons Arise / 地牢浮现之时兼容常量。
 *
 * <p>这个类只保存外部模组的命名空间和战利品表 ID，不直接引用外部模组类。
 * 这样即使整合包没有安装地牢浮现之时，本项目也不会因为类加载缺失而崩溃。</p>
 *
 * <p>Lootr 兼容和全局战利品注入都应该从这里读取同一组表 ID。
 * 如果只改其中一边，安装 Lootr 后玩家独立箱子可能拿不到本项目注入的额外战利品。</p>
 */
public final class DungeonsAriseCompat {
    public static final String MODID = "dungeons_arise";

    public static final String HEAVENLY_CHALLENGER_ANGEL_BLESSING_INJECT =
            "inject/chests/angel_blessing/heavenly_challenger";

    public static final List<String> FOUNDRY_NORMAL_INJECTS = List.of(
            "inject/chests/earth_heart_fragment/overworld_epic",
            "inject/chests/earth_heart/overworld_epic"
    );

    public static final List<String> FOUNDRY_TREASURE_INJECTS = List.of(
            "inject/chests/earth_heart/overworld_epic",
            "inject/chests/spellstones/nether",
            "inject/chests/spellstones/earthen",
            "inject/chests/earth_heart/overworld_epic",
            "inject/chests/unholy_grail/overworld_epic"
    );

    /**
     * 天际挑战者 / Heavenly Challenger。
     */
    public static final List<ResourceLocation> HEAVENLY_CHALLENGER_CHEST_TABLES = List.of(
            chest("heavenly_challenger/heavenly_challenger_treasure")
    );

    /**
     * 天际骑士团战舰 / Heavenly Rider。
     */
    public static final List<ResourceLocation> HEAVENLY_RIDER_CHEST_TABLES = List.of(
            chest("heavenly_rider/heavenly_rider_treasure")
    );

    /**
     * 天堂征服者 / Heavenly Conqueror。
     */
    public static final List<ResourceLocation> HEAVENLY_CONQUEROR_CHEST_TABLES = List.of(
            chest("heavenly_conqueror/heavenly_conqueror_treasure")
    );

    /**
     * 所有“天空 / 天堂”主题结构的战利品表。
     *
     * <p>这些结构共享同一张天使之祝注入表，但只允许出现在 treasure 战利品箱中。
     * Global Loot Modifier 和 Lootr 兼容都遍历这个列表，避免后续新增表时只改一边导致 Lootr 箱子漏掉额外战利品。</p>
     */
    public static final List<ResourceLocation> HEAVENLY_ANGEL_BLESSING_CHEST_TABLES = List.of(
            chest("heavenly_challenger/heavenly_challenger_treasure"),
            chest("heavenly_rider/heavenly_rider_treasure"),
            chest("heavenly_conqueror/heavenly_conqueror_treasure")
    );

    /**
     * 铸造厂 / Foundry 的普通类型战利品箱。
     *
     * <p>地牢浮现之时在铸造厂里同时使用 foundry_normal 和 foundry_passage_normal。
     * 这两个表都属于普通箱池，因此统一加入大地之心碎片与大地之心。</p>
     */
    public static final List<ResourceLocation> FOUNDRY_NORMAL_CHEST_TABLES = List.of(
            chest("foundry/foundry_normal"),
            chest("foundry/foundry_passage_normal")
    );

    /**
     * 铸造厂 / Foundry 的宝藏类型战利品箱。
     *
     * <p>只对应 foundry_treasure，不影响 chains、lava_pit、passage_exterior 等特殊箱池。
     * 宝藏箱会额外抽取大地之心、烈焰之核、魔像之心、大地之心、不洁圣杯；
     * 其中大地之心按需求保留两次独立注入。</p>
     */
    public static final List<ResourceLocation> FOUNDRY_TREASURE_CHEST_TABLES = List.of(
            chest("foundry/foundry_treasure")
    );

    private DungeonsAriseCompat() {
    }

    private static ResourceLocation chest(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "chests/" + path);
    }
}
