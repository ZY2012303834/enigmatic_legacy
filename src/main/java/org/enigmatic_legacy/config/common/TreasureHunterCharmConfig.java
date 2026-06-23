package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 猎宝者护符配置。
 */
public class TreasureHunterCharmConfig {
    public final ModConfigSpec.IntValue miningSpeedBonus;
    public final ModConfigSpec.BooleanValue fortuneEnabled;
    public final ModConfigSpec.BooleanValue nightVisionEnabled;
    public final ModConfigSpec.IntValue nightVisionDuration;

    public TreasureHunterCharmConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "猎宝者护符配置"
        ).push("Treasure Hunter Charm");

        miningSpeedBonus = builder
                .comment("猎宝者护符提供的挖掘速度加成，单位为百分比。", "原项目后续默认值为 30。")
                .defineInRange("TreasureHunterCharmMiningSpeedBonus", 30, 0, 1000);

        fortuneEnabled = builder
                .comment("猎宝者护符是否提供 +1 时运。")
                .define("TreasureHunterCharmFortuneEnabled", true);

        nightVisionEnabled = builder
                .comment("猎宝者护符是否提供夜视。")
                .define("TreasureHunterCharmNightVisionEnabled", true);

        nightVisionDuration = builder
                .comment("猎宝者护符刷新夜视时给予的持续时间，单位为 tick。", "20 tick = 1 秒。")
                .defineInRange("TreasureHunterCharmNightVisionDuration", 400, 40, 12000);

        builder.pop();
    }
}