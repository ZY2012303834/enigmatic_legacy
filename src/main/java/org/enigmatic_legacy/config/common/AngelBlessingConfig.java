package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 天使之祝配置。
 */
public class AngelBlessingConfig {
    public final ModConfigSpec.DoubleValue accelerationModifier;
    public final ModConfigSpec.DoubleValue accelerationModifierElytra;
    public final ModConfigSpec.IntValue deflectChance;
    public final ModConfigSpec.DoubleValue vulnerabilityModifier;
    public final ModConfigSpec.IntValue cooldown;

    public AngelBlessingConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "天使之祝配置"
        ).push("Angel's Blessing");

        accelerationModifier = builder
                .comment("普通状态下主动冲刺的加速度。")
                .defineInRange("AngelBlessingAccelerationModifier", 1.0D, 0.0D, 256.0D);

        accelerationModifierElytra = builder
                .comment("鞘翅飞行状态下主动冲刺的加速度。")
                .defineInRange("AngelBlessingAccelerationModifierElytra", 0.6D, 0.0D, 256.0D);

        deflectChance = builder
                .comment("反射接近弹射物的概率，单位为百分比。")
                .defineInRange("AngelBlessingDeflectChance", 50, 0, 100);

        vulnerabilityModifier = builder
                .comment("受到凋零和虚空伤害时的伤害倍率。")
                .defineInRange("AngelBlessingVulnerabilityModifier", 2.0D, 1.0D, 20.0D);

        cooldown = builder
                .comment("主动技能冷却，单位为 tick。20 tick = 1 秒。默认 40 tick = 2 秒。")
                .defineInRange("AngelBlessingCooldown", 40, 1, 1200);

        builder.pop();
    }
}