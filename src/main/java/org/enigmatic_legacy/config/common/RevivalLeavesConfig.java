package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 复苏之叶服务器配置。
 *
 * <p>数值尽量沿用 Enigmatic Addons 原项目：
 * 主动冷却 320 tick，自然恢复每 40 tick 触发一次，主动范围 5 格。</p>
 */
public class RevivalLeavesConfig {
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.IntValue naturalRegenerationTick;
    public final ModConfigSpec.DoubleValue abilityRadius;
    public final ModConfigSpec.IntValue poisonTime;
    public final ModConfigSpec.IntValue poisonLevel;
    public final ModConfigSpec.IntValue regenerationTime;
    public final ModConfigSpec.IntValue regenerationLevel;
    public final ModConfigSpec.IntValue effectDurationModifier;
    public final ModConfigSpec.DoubleValue fireDamageVulnerability;
    public final ModConfigSpec.DoubleValue projectileDamageVulnerability;

    public RevivalLeavesConfig(ModConfigSpec.Builder builder) {
        builder.push("Revival Leaves");

        cooldown = builder
                .comment("复苏之叶主动能力冷却时间，单位为刻。20 刻 = 1 秒。")
                .defineInRange("cooldown", 320, 0, Integer.MAX_VALUE);

        naturalRegenerationTick = builder
                .comment("复苏之叶自然恢复的触发间隔，单位为刻。默认 40 表示每 2 秒恢复一次。")
                .defineInRange("naturalRegenerationTick", 40, 5, 72000);

        abilityRadius = builder
                .comment("复苏之叶主动能力影响半径。范围内生物会获得生命恢复，并在高等级时立刻回复部分最大生命值。")
                .defineInRange("abilityRadius", 5.0D, 0.0D, 128.0D);

        poisonTime = builder
                .comment("佩戴复苏之叶攻击目标时，施加中毒效果的持续时间，单位为刻。")
                .defineInRange("poisonTime", 160, 0, 72000);

        poisonLevel = builder
                .comment("佩戴复苏之叶攻击目标时，施加中毒效果的等级。0 表示中毒 I，1 表示中毒 II。")
                .defineInRange("poisonLevel", 1, 0, 3);

        regenerationTime = builder
                .comment("主动能力给予范围内生物生命恢复效果的基础持续时间，单位为刻。")
                .defineInRange("regenerationTime", 180, 0, 72000);

        regenerationLevel = builder
                .comment("主动能力给予范围内生物生命恢复效果的等级。0 表示生命恢复 I，1 表示生命恢复 II。")
                .defineInRange("regenerationLevel", 1, 0, 3);

        effectDurationModifier = builder
                .comment("佩戴复苏之叶时，新获得状态效果的持续时间倍率，单位为百分比。默认 125 表示延长 25%。")
                .defineInRange("effectDurationModifier", 125, 0, 10000);

        fireDamageVulnerability = builder
                .comment("佩戴复苏之叶时，受到火焰类伤害的倍率。默认 2.0 表示受到 2 倍伤害。")
                .defineInRange("fireDamageVulnerability", 2.0D, 0.0D, 100.0D);

        projectileDamageVulnerability = builder
                .comment("佩戴复苏之叶时，受到弹射物伤害的倍率。默认 1.5 表示受到 1.5 倍伤害。")
                .defineInRange("projectileDamageVulnerability", 1.5D, 0.0D, 100.0D);

        builder.pop();
    }
}
