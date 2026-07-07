package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 以太核心服务器配置。
 *
 * <p>复刻 Enigmatic Addons 的 Etherium Core，并按本项目术石结构拆分。</p>
 */
public class EtheriumCoreConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.IntValue shieldDuration;
    public final ModConfigSpec.DoubleValue armorBonus;
    public final ModConfigSpec.DoubleValue toughnessBonus;
    public final ModConfigSpec.IntValue armorMultiplier;
    public final ModConfigSpec.IntValue toughnessMultiplier;
    public final ModConfigSpec.IntValue knockbackResistance;
    public final ModConfigSpec.IntValue damageConversion;
    public final ModConfigSpec.DoubleValue maxStoredDamage;
    public final ModConfigSpec.IntValue shieldDamageResistance;
    public final ModConfigSpec.DoubleValue shieldKnockbackStrength;
    public final ModConfigSpec.IntValue shieldThresholdBonus;

    public EtheriumCoreConfig(ModConfigSpec.Builder builder) {
        builder.push("Etherium Core");

        cooldown = builder
                .comment("以太核心主动能力冷却时间，单位为刻。20 刻 = 1 秒。")
                .defineInRange("cooldown", 800, 0, Integer.MAX_VALUE);

        shieldDuration = builder
                .comment("以太核心主动激活以太护盾的持续时间，单位为刻。")
                .defineInRange("shieldDuration", 400, 0, 72000);

        armorBonus = builder
                .comment("佩戴以太核心时提供的护甲值。")
                .defineInRange("armorBonus", 12.0D, 0.0D, 1000.0D);

        toughnessBonus = builder
                .comment("佩戴以太核心时提供的护甲韧性。")
                .defineInRange("toughnessBonus", 10.0D, 0.0D, 1000.0D);

        armorMultiplier = builder
                .comment("佩戴以太核心时提供的护甲值百分比加成。默认 20 表示 +20%。")
                .defineInRange("armorMultiplier", 20, 0, 10000);

        toughnessMultiplier = builder
                .comment("佩戴以太核心时提供的盔甲韧性百分比加成。默认 40 表示 +40%。")
                .defineInRange("toughnessMultiplier", 40, 0, 10000);

        knockbackResistance = builder
                .comment("佩戴以太核心时提供的击退抗性。默认 50 表示 +50%。")
                .defineInRange("knockbackResistance", 50, 0, 100);

        damageConversion = builder
                .comment("受到伤害时，有多少百分比会被记录并转化为下一次攻击的额外伤害。默认 40 表示 40%。")
                .defineInRange("damageConversion", 40, 0, 10000);

        maxStoredDamage = builder
                .comment("以太核心最多存储多少额外反击伤害，避免无限滚雪球。")
                .defineInRange("maxStoredDamage", 80.0D, 0.0D, 10000.0D);

        shieldDamageResistance = builder
                .comment("主动以太护盾激活期间受到非弹射物攻击时的伤害减免百分比。默认 50 表示减免 50%。")
                .defineInRange("shieldDamageResistance", 50, 0, 100);

        shieldKnockbackStrength = builder
                .comment("主动以太护盾激活期间，近战攻击者被击退的强度。")
                .defineInRange("shieldKnockbackStrength", 0.85D, 0.0D, 10.0D);

        shieldThresholdBonus = builder
                .comment("佩戴以太核心时，以太套装护盾生命阈值提高百分比。默认 50 表示原阈值提高 50%。")
                .defineInRange("shieldThresholdBonus", 50, 0, 10000);

        builder.pop();
    }
}
