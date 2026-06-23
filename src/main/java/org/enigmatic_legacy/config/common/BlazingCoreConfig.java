package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 烈焰核心服务器配置。

 * 这里的 comment 会写入 enigmatic_legacy-server.toml，
 * 所以全部使用中文注释。
 */
public class BlazingCoreConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.DoubleValue damageFeedback;
    public final ModConfigSpec.IntValue ignitionFeedback;
    public final ModConfigSpec.IntValue lavaImmunityTicks;
    public final ModConfigSpec.IntValue lavaCooldownPerTick;
    public final ModConfigSpec.IntValue effectDurationModifier;
    public final ModConfigSpec.IntValue moltenEffectDurationModifier;
    public final ModConfigSpec.DoubleValue aquaticDamageVulnerability;

    public BlazingCoreConfig(ModConfigSpec.Builder builder) {
        builder.push("Blazing Core");

        cooldown = builder
                .comment("烈焰核心主动能力的冷却时间，单位为刻。原物品没有主动能力，因此默认值为 0。20 刻 = 1 秒。")
                .defineInRange("cooldown", 0, 0, Integer.MAX_VALUE);

        damageFeedback = builder
                .comment("佩戴烈焰核心时，近战攻击者会立刻受到的火焰反馈伤害。")
                .defineInRange("damageFeedback", 4.0D, 0.0D, 512.0D);

        ignitionFeedback = builder
                .comment("佩戴烈焰核心时，近战攻击者会被点燃的秒数。")
                .defineInRange("ignitionFeedback", 4, 0, 512);

        lavaImmunityTicks = builder
                .comment("烈焰核心提供的连续岩浆临时免疫时间，单位为刻。热量达到该值后会开始受到岩浆伤害。默认 200 表示约 10 秒。")
                .defineInRange("lavaImmunityTicks", 200, 0, 72000);

        lavaCooldownPerTick = builder
                .comment("离开岩浆后每刻降低的过热值。数值越大，冷却越快。")
                .defineInRange("lavaCooldownPerTick", 2, 1, 72000);

        effectDurationModifier = builder
                .comment("佩戴烈焰核心时，大多数状态效果持续时间倍率，单位为百分比。默认 50 表示持续时间减半。")
                .defineInRange("effectDurationModifier", 50, 0, 10000);

        moltenEffectDurationModifier = builder
                .comment("佩戴烈焰核心时，熔火之心/抗火类状态效果持续时间倍率，单位为百分比。默认 200 表示持续时间翻倍。")
                .defineInRange("moltenEffectDurationModifier", 200, 0, 10000);

        aquaticDamageVulnerability = builder
                .comment("佩戴烈焰核心时，来自水生生物的伤害倍率。默认 2.0 表示受到 2 倍伤害。")
                .defineInRange("aquaticDamageVulnerability", 2.0D, 0.0D, 100.0D);

        builder.pop();
    }
}