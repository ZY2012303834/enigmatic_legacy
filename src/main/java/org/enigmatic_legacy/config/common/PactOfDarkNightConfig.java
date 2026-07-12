package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 暗夜契约卷轴服务器配置。
 *
 * <p>这里的三个数值复刻 Enigmatic Addons 的 PactofDarkNight 配置项。
 * 实际效果会再乘以暗度倍率：越黑越强，拥有黑暗效果或处于极暗环境时最高。</p>
 */
public class PactOfDarkNightConfig {
    public final ModConfigSpec.DoubleValue averageDamageBoost;
    public final ModConfigSpec.DoubleValue averageDamageResistance;
    public final ModConfigSpec.DoubleValue averageLifeSteal;

    public PactOfDarkNightConfig(ModConfigSpec.Builder builder) {
        builder.comment("暗夜契约卷轴配置。").push("Pact of Dark Night");

        averageDamageBoost = builder
                .comment("暗夜契约卷轴提供的基础攻击伤害加成，按百分比计算。最终数值会乘以当前暗度倍率。")
                .defineInRange("AverageDamageBoost", 20.0D, 0.0D, 100.0D);

        averageDamageResistance = builder
                .comment("暗夜契约卷轴提供的基础伤害抗性，按百分比计算。最终数值会乘以当前暗度倍率。")
                .defineInRange("AverageDamageResistance", 16.0D, 0.0D, 100.0D);

        averageLifeSteal = builder
                .comment("暗夜契约卷轴提供的基础吸血比例，按百分比计算。最终数值会乘以当前暗度倍率。")
                .defineInRange("AverageLifeSteal", 8.0D, 0.0D, 100.0D);

        builder.pop();
    }
}
