package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 混沌之傲 / The Arrogance of Chaos 配置。
 *
 * <p>该物品是壮丽鞘翅的七咒高阶变体，包含飞行助推、背后减伤、
 * 俯冲落地范围伤害，以及深渊之心资格带来的额外强化。</p>
 */
public class TheArroganceOfChaosConfig {
    public final ModConfigSpec.DoubleValue flyingSpeedModifier;
    public final ModConfigSpec.DoubleValue descendingPowerModifier;
    public final ModConfigSpec.IntValue descendingCooldown;
    public final ModConfigSpec.DoubleValue damageResistance;

    public TheArroganceOfChaosConfig(ModConfigSpec.Builder builder) {
        builder.comment("混沌之傲配置。").push("The Arrogance of Chaos");

        flyingSpeedModifier = builder
                .comment(
                        "混沌之傲滑翔助推倍率。",
                        "原扩展默认值为 1.6。数值越高，按住跳跃键助推时速度越强。"
                )
                .defineInRange("FlyingSpeedModifier", 1.6D, 1.0D, 10.0D);

        descendingPowerModifier = builder
                .comment(
                        "混沌之傲俯冲落地范围伤害倍率基数。",
                        "落地前向下速度越高，最终伤害越高；该值作为指数计算的基数。",
                        "原扩展默认值为 1.6。"
                )
                .defineInRange("DescendingPowerModifier", 1.6D, 1.0D, 10.0D);

        descendingCooldown = builder
                .comment(
                        "混沌之傲俯冲落地技能冷却，单位为 tick。",
                        "20 tick = 1 秒。原扩展默认值为 500 tick。"
                )
                .defineInRange("DescendingCooldown", 500, 200, 2400);

        damageResistance = builder
                .comment(
                        "混沌之傲对背后伤害、摔落伤害和撞墙伤害的减免百分比。",
                        "深渊强化生效时，实际减免会额外提高 25%，最高不超过 100%。",
                        "原扩展默认值为 80。"
                )
                .defineInRange("DamageResistance", 80.0D, 0.0D, 100.0D);

        builder.pop();
    }
}
