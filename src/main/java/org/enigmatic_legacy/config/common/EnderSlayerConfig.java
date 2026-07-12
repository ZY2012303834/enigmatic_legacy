package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 末影之屠配置。
 * <p>
 * 末影之屠的基础伤害属于具体物品数值，不再放在 GenericConfig 中。
 * 物品注册阶段仍会使用默认属性，实际物品栈会在游戏运行时刷新为这里的配置值。
 */
public class EnderSlayerConfig {
    public final ModConfigSpec.DoubleValue attackDamage;

    public EnderSlayerConfig(ModConfigSpec.Builder builder) {
        builder.comment("末影之屠配置。").push("Ender Slayer");

        attackDamage = builder
                .comment(
                        "末影之屠的基础攻击伤害加值。",
                        "该数值传入原版 SwordItem 属性创建逻辑；实际面板伤害还会叠加玩家自身的 1 点基础攻击伤害。",
                        "默认 4.0 与旧实现保持一致；物品栈会在游戏运行时刷新为当前配置值。"
                )
                .defineInRange("EnderSlayerAttackDamage", 4.0D, 0.0D, 1000.0D);

        builder.pop();
    }
}
