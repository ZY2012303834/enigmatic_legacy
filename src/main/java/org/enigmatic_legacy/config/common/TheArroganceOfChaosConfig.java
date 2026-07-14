package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 混沌之傲 / The Arrogance of Chaos 配置。
 *
 * <p>该物品是壮丽鞘翅的七咒高阶变体，包含飞行助推、背后减伤
 * 和俯冲落地范围伤害。</p>
 */
public class TheArroganceOfChaosConfig {
    public final ModConfigSpec.DoubleValue flyingSpeedModifier;
    public final ModConfigSpec.DoubleValue descendingMinimumSpeed;
    public final ModConfigSpec.DoubleValue descendingPowerModifier;
    public final ModConfigSpec.DoubleValue damageResistance;

    public TheArroganceOfChaosConfig(ModConfigSpec.Builder builder) {
        builder.comment("混沌之傲配置。").push("The Arrogance of Chaos");

        flyingSpeedModifier = builder
                .comment(
                        "混沌之傲滑翔助推倍率。",
                        "原扩展默认值为 1.6。数值越高，按住跳跃键助推时速度越强。"
                )
                .defineInRange("FlyingSpeedModifier", 1.6D, 1.0D, 10.0D);

        descendingMinimumSpeed = builder
                .comment(
                        "混沌之傲触发俯冲落地范围伤害所需的最低飞行速度。",
                        "该速度使用落地前记录的三维移动速度，单位约为方块/tick。",
                        "提高该值可以避免低速滑翔、短距离起飞或空中切换状态时误触发落地伤害。"
                )
                .defineInRange("DescendingMinimumSpeed", 0.75D, 0.0D, 10.0D);

        descendingPowerModifier = builder
                .comment(
                        "混沌之傲俯冲落地范围伤害倍率基数。",
                        "落地前向下速度越高，最终伤害越高；该值作为指数计算的基数。",
                        "原扩展默认值为 1.6。"
                )
                .defineInRange("DescendingPowerModifier", 1.6D, 1.0D, 10.0D);

        damageResistance = builder
                .comment(
                        "混沌之傲对背后伤害、摔落伤害和撞墙伤害的减免百分比。",
                        "最终至少保留 5% 伤害，避免完全免疫。",
                        "原扩展默认值为 80。"
                )
                .defineInRange("DamageResistance", 80.0D, 0.0D, 100.0D);

        builder.pop();
    }
}
