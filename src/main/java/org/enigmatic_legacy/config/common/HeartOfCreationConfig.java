package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 创造之心服务器配置。

 * 这里的 comment 会写入 enigmatic_legacy-server.toml。
 */
public class HeartOfCreationConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.DoubleValue lightningRange;
    public final ModConfigSpec.DoubleValue lightningDamage;

    public HeartOfCreationConfig(ModConfigSpec.Builder builder) {
        builder.push("Heart of Creation");

        cooldown = builder
                .comment("创造之心主动能力冷却时间，单位为刻。20 刻 = 1 秒。默认 60 = 3 秒。")
                .defineInRange("cooldown", 60, 0, Integer.MAX_VALUE);

        lightningRange = builder
                .comment("创造之心主动能力影响范围。范围内敌人会被闪电击中并获得凋零效果。")
                .defineInRange("lightningRange", 16.0D, 1.0D, 128.0D);

        lightningDamage = builder
                .comment("创造之心主动能力每次闪电造成的伤害。默认 10。")
                .defineInRange("lightningDamage", 10.0D, 0.0D, 10000.0D);

        builder.pop();
    }
}