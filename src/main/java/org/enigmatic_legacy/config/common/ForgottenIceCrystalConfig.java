package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 忘却冰晶服务器配置。
 *
 * <p>数值以 Enigmatic Addons 原版 Forgotten Ice Crystal 为基础，并按本项目术石结构拆分。</p>
 */
public class ForgottenIceCrystalConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.DoubleValue activeRadius;
    public final ModConfigSpec.IntValue activeFreezeTicks;
    public final ModConfigSpec.IntValue meleeFreezeTicks;
    public final ModConfigSpec.DoubleValue retaliationFreezeDamage;
    public final ModConfigSpec.DoubleValue extraDamageBase;
    public final ModConfigSpec.DoubleValue extraDamagePerFrozenTick;
    public final ModConfigSpec.IntValue frostDamageBoost;
    public final ModConfigSpec.IntValue projectileAndSonicResistance;
    public final ModConfigSpec.DoubleValue fireDamageVulnerability;
    public final ModConfigSpec.DoubleValue fallDamageVulnerability;
    public final ModConfigSpec.IntValue hardFrozenThreshold;
    public final ModConfigSpec.IntValue maxStoredFrozenTicks;

    public ForgottenIceCrystalConfig(ModConfigSpec.Builder builder) {
        builder.push("Forgotten Ice Crystal");

        cooldown = builder
                .comment("忘却冰晶主动能力冷却时间，单位为刻。20 刻 = 1 秒。")
                .defineInRange("cooldown", 240, 0, Integer.MAX_VALUE);

        activeRadius = builder
                .comment("忘却冰晶主动能力影响半径。范围内生物会受到冻结伤害并增加冻结时间。")
                .defineInRange("activeRadius", 5.0D, 0.0D, 128.0D);

        activeFreezeTicks = builder
                .comment("主动能力对可冻结目标追加的冻结时间，单位为刻。")
                .defineInRange("activeFreezeTicks", 500, 0, 72000);

        meleeFreezeTicks = builder
                .comment("佩戴忘却冰晶近战命中目标时，追加的冻结时间，单位为刻。")
                .defineInRange("meleeFreezeTicks", 70, 0, 72000);

        retaliationFreezeDamage = builder
                .comment("佩戴忘却冰晶受到近战类攻击时，对攻击者造成的冻结伤害。")
                .defineInRange("retaliationFreezeDamage", 3.0D, 0.0D, 10000.0D);

        extraDamageBase = builder
                .comment("主动能力的基础冻结伤害。最终基础伤害还会加上玩家攻击伤害。")
                .defineInRange("extraDamageBase", 2.0D, 0.0D, 10000.0D);

        extraDamagePerFrozenTick = builder
                .comment("主动能力命中完全冻结目标时，每个已存储冻结刻提供的额外伤害倍率。")
                .defineInRange("extraDamagePerFrozenTick", 0.01D, 0.0D, 100.0D);

        frostDamageBoost = builder
                .comment("攻击完全冻结目标时的额外伤害百分比。默认 30 表示提高 30%。")
                .defineInRange("frostDamageBoost", 30, 0, 10000);

        projectileAndSonicResistance = builder
                .comment("佩戴忘却冰晶时，受到弹射物和音波伤害的减免百分比。默认 30 表示降低 30%。")
                .defineInRange("projectileAndSonicResistance", 30, 0, 100);

        fireDamageVulnerability = builder
                .comment("佩戴忘却冰晶时，受到火焰类伤害的倍率。默认 2.5 表示受到 2.5 倍伤害。")
                .defineInRange("fireDamageVulnerability", 2.5D, 0.0D, 100.0D);

        fallDamageVulnerability = builder
                .comment("佩戴忘却冰晶时，受到摔落伤害的倍率。默认 1.6 表示受到 1.6 倍伤害。")
                .defineInRange("fallDamageVulnerability", 1.6D, 0.0D, 100.0D);

        hardFrozenThreshold = builder
                .comment("目标完全冻结并被忘却冰晶影响超过多少刻后进入硬冻结。")
                .defineInRange("hardFrozenThreshold", 340, 0, 72000);

        maxStoredFrozenTicks = builder
                .comment("忘却冰晶记录目标完全冻结时间的上限，用于限制主动能力额外伤害。")
                .defineInRange("maxStoredFrozenTicks", 1200, 0, 72000);

        builder.pop();
    }
}
