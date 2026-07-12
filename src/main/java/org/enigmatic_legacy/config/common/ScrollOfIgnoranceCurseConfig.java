package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 无知诅咒卷轴配置。
 * <p>
 * 该类只负责定义无知诅咒卷轴自身的成长上限与经验换算上限。
 * 从 GenericConfig 拆出后，通用配置类只保留真正的通用机制，后续维护卷轴数值时不需要再翻找通用配置。
 */
public class ScrollOfIgnoranceCurseConfig {
    public final ModConfigSpec.DoubleValue damageBoostLimit;
    public final ModConfigSpec.DoubleValue healBoostLimit;
    public final ModConfigSpec.DoubleValue knockbackResistanceLimit;
    public final ModConfigSpec.IntValue xpLevelUpperLimit;

    public ScrollOfIgnoranceCurseConfig(ModConfigSpec.Builder builder) {
        builder.comment("无知诅咒卷轴配置。").push("Scroll of Ignorance Curse");

        damageBoostLimit = builder
                .comment("无知诅咒卷轴提供的攻击伤害加成上限，按百分比计算。")
                .defineInRange("DamageBoostLimit", 100.0D, 0.0D, 1000.0D);

        healBoostLimit = builder
                .comment("无知诅咒卷轴提供的生命恢复加成上限，按百分比计算。")
                .defineInRange("HealBoostLimit", 50.0D, 0.0D, 1000.0D);

        knockbackResistanceLimit = builder
                .comment("无知诅咒卷轴提供的击退抗性加成上限，按百分比计算。")
                .defineInRange("KnockbackResistanceBoostLimit", 160.0D, 0.0D, 1000.0D);

        xpLevelUpperLimit = builder
                .comment("储存经验达到多少等级时，无知诅咒卷轴的加成达到上限。")
                .defineInRange("XPLevelUpperLimit", 1000, 1, 1000);

        builder.pop();
    }
}
