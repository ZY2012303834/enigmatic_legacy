package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class OceanStoneConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.IntValue swimSpeedBonus;
    public final ModConfigSpec.IntValue aquaticDamageResistance;
    public final ModConfigSpec.DoubleValue xpCostModifier;
    public final ModConfigSpec.IntValue nightVisionDuration;
    public final ModConfigSpec.DoubleValue fireVulnerability;

    public OceanStoneConfig(ModConfigSpec.Builder builder) {
        builder.push("Ocean Stone");

        cooldown = builder
                .comment("海洋意志主动能力的冷却时间，单位为刻。20 刻 = 1 秒。")
                .defineInRange("cooldown", 600, 0, Integer.MAX_VALUE);

        swimSpeedBonus = builder
                .comment("海洋意志提供的游泳速度加成，单位为百分比。默认 200 表示 +200% 游泳速度。")
                .defineInRange("swimSpeedBonus", 200, 0, 10000);

        aquaticDamageResistance = builder
                .comment("佩戴海洋意志时，对水生生物造成的伤害获得的减伤比例，单位为百分比。默认 40 表示减免 40% 伤害。")
                .defineInRange("aquaticDamageResistance", 40, 0, 100);

        xpCostModifier = builder
                .comment("海洋意志主动能力消耗经验的倍率。默认 1.0 表示使用原始消耗。")
                .defineInRange("xpCostModifier", 1.0D, 0.0D, 100.0D);

        nightVisionDuration = builder
                .comment("在水下刷新夜视效果的持续时间，单位为刻。该值应略高于刷新间隔，以避免画面闪烁。")
                .defineInRange("nightVisionDuration", 310, 20, 1200);

        fireVulnerability = builder
                .comment("佩戴海洋意志时受到火焰伤害的倍率。默认 2.0 表示受到 2 倍火焰伤害。")
                .defineInRange("fireVulnerability", 2.0D, 0.0D, 100.0D);

        builder.pop();
    }
}
