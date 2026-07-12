package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 暴戾之咒服务器配置。
 *
 * <p>该配置组复刻 Enigmatic Addons 中 CurseofViolence 的核心数值。
 * 暴戾之咒会吸收诅咒附魔并把它们转化为战斗收益，因此这里的多数配置都是
 * “每个已吸收诅咒提供多少收益”或“怒意能量最多储存多少”。</p>
 */
public class CurseOfViolenceConfig {
    public final ModConfigSpec.DoubleValue baseCurseModifier;
    public final ModConfigSpec.DoubleValue boostPerCurseModifier;
    public final ModConfigSpec.DoubleValue invulnerableAttackModifier;
    public final ModConfigSpec.DoubleValue invulnerableHealMultiplier;
    public final ModConfigSpec.DoubleValue attackSpeedBoost;
    public final ModConfigSpec.DoubleValue entityReachBoost;
    public final ModConfigSpec.DoubleValue knockbackResistanceBoost;
    public final ModConfigSpec.IntValue maxDurability;

    public CurseOfViolenceConfig(ModConfigSpec.Builder builder) {
        builder.comment("暴戾之咒配置。").push("Curse of Violence");

        baseCurseModifier = builder
                .comment("暴戾之咒连续攻击同一目标时的基础额外伤害比例，按百分比计算。")
                .defineInRange("BaseCurseModifier", 10.0D, 0.0D, 95.0D);

        boostPerCurseModifier = builder
                .comment("每个已吸收诅咒参与额外伤害成长公式的基础数值，按百分比计算。")
                .defineInRange("BoostPerCurseModifier", 5.0D, 0.0D, 100.0D);

        invulnerableAttackModifier = builder
                .comment("玩家处于受击无敌时间内攻击时，造成的额外伤害比例，按百分比计算；200 表示额外 +200%，即最终乘以 3。")
                .defineInRange("InvulnerableAttackModifier", 200.0D, 100.0D, 1000.0D);

        invulnerableHealMultiplier = builder
                .comment("玩家处于受击无敌时间内攻击时，按造成伤害恢复生命的比例，按百分比计算。")
                .defineInRange("InvulnerableHealMultiplier", 40.0D, 0.0D, 100.0D);

        attackSpeedBoost = builder
                .comment("每个已吸收诅咒提供的攻击速度加成，按百分比计算。")
                .defineInRange("AttackSpeedBoost", 4.0D, 1.0D, 10.0D);

        entityReachBoost = builder
                .comment("每个已吸收诅咒提供的实体交互距离加成，按百分比计算。")
                .defineInRange("EntityReachBoost", 3.0D, 1.0D, 10.0D);

        knockbackResistanceBoost = builder
                .comment("每个已吸收诅咒提供的击退抗性，直接加到原版击退抗性属性上。")
                .defineInRange("KnockbackResistanceBoost", 0.025D, 0.0D, 1.0D);

        maxDurability = builder
                .comment("暴戾之咒可储存的最大怒意能量。吸收诅咒会增加能量，连续攻击会逐渐消耗能量。")
                .defineInRange("MaxDurability", 200, 100, 10000);

        builder.pop();
    }
}
