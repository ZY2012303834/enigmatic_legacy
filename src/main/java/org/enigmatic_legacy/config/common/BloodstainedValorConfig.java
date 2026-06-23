package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 血战沙场之证配置。
 */
public class BloodstainedValorConfig {
    public final ModConfigSpec.DoubleValue attackDamage;
    public final ModConfigSpec.DoubleValue attackSpeed;
    public final ModConfigSpec.DoubleValue movementSpeed;
    public final ModConfigSpec.DoubleValue damageResistance;

    public BloodstainedValorConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "血战沙场之证配置",
                "数值含义：每缺失 1% 生命，提供多少百分比加成。"
        ).push("Bloodstained Valor Emblem");

        attackDamage = builder
                .comment("血战沙场之证每缺失 1% 生命提供的攻击伤害加成。")
                .defineInRange("BloodstainedValorAttackDamage", 1.0D, 0.0D, 100.0D);

        attackSpeed = builder
                .comment("血战沙场之证每缺失 1% 生命提供的攻击速度加成。")
                .defineInRange("BloodstainedValorAttackSpeed", 1.0D, 0.0D, 100.0D);

        movementSpeed = builder
                .comment("血战沙场之证每缺失 1% 生命提供的移动速度加成。")
                .defineInRange("BloodstainedValorMovementSpeed", 0.5D, 0.0D, 100.0D);

        damageResistance = builder
                .comment("血战沙场之证每缺失 1% 生命提供的伤害抗性。")
                .defineInRange("BloodstainedValorDamageResistance", 0.5D, 0.0D, 1.0D);

        builder.pop();
    }
}