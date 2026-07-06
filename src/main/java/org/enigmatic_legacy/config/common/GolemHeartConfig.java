package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 魔像之心配置。
 */
public class GolemHeartConfig {
    public final ModConfigSpec.DoubleValue defaultArmorBonus;
    public final ModConfigSpec.DoubleValue superArmorBonus;
    public final ModConfigSpec.DoubleValue superArmorToughnessBonus;
    public final ModConfigSpec.DoubleValue knockbackResistance;
    public final ModConfigSpec.IntValue meleeResistance;
    public final ModConfigSpec.IntValue explosionResistance;
    public final ModConfigSpec.DoubleValue magicVulnerability;

    public GolemHeartConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "魔像之心配置"
        ).push("Golem Heart");

        defaultArmorBonus = builder
                .comment("穿戴护甲时，魔像之心提供的护甲值。")
                .defineInRange("GolemHeartDefaultArmorBonus", 4.0D, 0.0D, 20.0D);

        superArmorBonus = builder
                .comment("未穿戴任何护甲时，魔像之心提供的护甲值。")
                .defineInRange("GolemHeartSuperArmorBonus", 16.0D, 0.0D, 100.0D);

        superArmorToughnessBonus = builder
                .comment("未穿戴任何护甲时，魔像之心提供的护甲韧性。")
                .defineInRange("GolemHeartSuperArmorToughnessBonus", 4.0D, 0.0D, 20.0D);

        knockbackResistance = builder
                .comment("魔像之心提供的击退抗性。")
                .defineInRange("GolemHeartKnockbackResistance", 0.9D, 0.0D, 1.0D);

        meleeResistance = builder
                .comment("魔像之心提供的近战伤害减免百分比。")
                .defineInRange("GolemHeartMeleeResistance", 25, 0, 100);

        explosionResistance = builder
                .comment("未穿戴任何护甲时，魔像之心提供的爆炸伤害减免百分比。")
                .defineInRange("GolemHeartExplosionResistance", 40, 0, 100);

        magicVulnerability = builder
                .comment("魔像之心受到魔法伤害时的伤害倍率。2.0 表示最终伤害变为 200%，即额外受到 100% 魔法伤害。铁魔法法术伤害也会使用该倍率。")
                .defineInRange("GolemHeartMagicVulnerability", 2.0D, 1.0D, 20.0D);

        builder.pop();
    }
}