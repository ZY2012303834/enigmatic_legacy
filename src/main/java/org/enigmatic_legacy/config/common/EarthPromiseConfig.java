package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 地灵之诺配置。
 */
public class EarthPromiseConfig {
    public final ModConfigSpec.IntValue breakSpeedBonus;
    public final ModConfigSpec.DoubleValue armorBonus;
    public final ModConfigSpec.DoubleValue toughnessBonus;
    public final ModConfigSpec.IntValue cooldown;
    public final ModConfigSpec.IntValue totalResistance;
    public final ModConfigSpec.IntValue abilityTriggerPercent;

    public EarthPromiseConfig(ModConfigSpec.Builder builder) {
        builder.comment("地灵之诺配置").push("Promise of the Earth");

        breakSpeedBonus = builder
                .comment("地灵之诺提供的挖掘速度加成，单位为百分比。")
                .defineInRange("BreakSpeed", 20, 0, 1000);

        armorBonus = builder
                .comment("地灵之诺提供的护甲值。")
                .defineInRange("Armor", 5.0D, 0.0D, 256.0D);

        toughnessBonus = builder
                .comment("地灵之诺提供的盔甲韧性。")
                .defineInRange("Toughness", 2.0D, 0.0D, 256.0D);

        cooldown = builder
                .comment("地灵之诺被动能力的冷却时间，单位为 tick。20 tick = 1 秒。")
                .defineInRange("Cooldown", 1000, 0, 32768);

        totalResistance = builder
                .comment("对第一诅咒的修正系数，单位为百分比。")
                .defineInRange("TotalResistance", 25, 0, 100);

        abilityTriggerPercent = builder
                .comment("触发被动能力所需的伤害阈值，按当前生命值百分比计算。")
                .defineInRange("AbilityTriggerPercent", 80, 0, 100);

        builder.pop();
    }
}
