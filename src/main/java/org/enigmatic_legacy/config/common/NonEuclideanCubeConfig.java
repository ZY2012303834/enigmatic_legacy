package org.enigmatic_legacy.config.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 非欧立方配置。
 */
public class NonEuclideanCubeConfig {
    public final ModConfigSpec.ConfigValue<List<? extends String>> randomBuffs;
    public final ModConfigSpec.ConfigValue<List<? extends String>> randomDebuffs;
    public final ModConfigSpec.IntValue damageLimit;
    public final ModConfigSpec.BooleanValue autoSkillTriggering;

    public NonEuclideanCubeConfig(ModConfigSpec.Builder builder) {
        builder.comment("非欧立方配置").push("Non-Euclidean Cube");

        randomBuffs = builder
                .comment(
                        "非欧立方击杀生物后可能给予玩家的随机正面效果列表。",
                        "格式示例：minecraft:strength。",
                        "修改该列表需要重启游戏后生效。"
                )
                .defineList(
                        "TheCubeRandomBuffs",
                        List.of(
                                "minecraft:regeneration",
                                "minecraft:speed",
                                "minecraft:haste",
                                "minecraft:strength",
                                "minecraft:resistance",
                                "minecraft:fire_resistance",
                                "minecraft:water_breathing",
                                "minecraft:night_vision",
                                "minecraft:absorption",
                                "minecraft:health_boost",
                                "minecraft:jump_boost",
                                "minecraft:luck",
                                "minecraft:saturation"
                        ),
                        () -> "minecraft:strength",
                        NonEuclideanCubeConfig::isValidResourceLocation
                );

        randomDebuffs = builder
                .comment(
                        "非欧立方受到非投射物伤害时可能给予攻击者的随机负面效果列表。",
                        "格式示例：minecraft:blindness。",
                        "修改该列表需要重启游戏后生效。"
                )
                .defineList(
                        "TheCubeRandomDebuffs",
                        List.of(
                                "minecraft:weakness",
                                "minecraft:slowness",
                                "minecraft:mining_fatigue",
                                "minecraft:blindness",
                                "minecraft:hunger",
                                "minecraft:poison",
                                "minecraft:wither",
                                "minecraft:nausea"
                        ),
                        () -> "minecraft:weakness",
                        NonEuclideanCubeConfig::isValidResourceLocation
                );

        damageLimit = builder
                .comment("非欧立方无视的高额伤害阈值。佩戴七咒之戒时实际阈值为该值的 1.5 倍。")
                .defineInRange("CubeDamageLimit", 100, 50, 10000);

        autoSkillTriggering = builder
                .comment("是否允许非欧立方在濒死时自动触发主动技能。")
                .define("CubeAutoSkillTriggering", true);

        builder.pop();
    }

    private static boolean isValidResourceLocation(Object value) {
        return value instanceof String string
                && ResourceLocation.tryParse(string.trim()) != null;
    }
}
