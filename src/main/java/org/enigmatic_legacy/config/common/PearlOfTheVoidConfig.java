package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 虚空珍珠服务器配置。
 *
 * <p>虚空珍珠的黑暗光环会扫描附近生物，并只在目标暴露于黑暗时触发。
 * 本配置类用于集中管理这类可调参数，避免把阈值和特殊实体写死在事件逻辑中。</p>
 */
public class PearlOfTheVoidConfig {
    public final ModConfigSpec.IntValue darknessBrightnessThreshold;
    public final ModConfigSpec.DoubleValue darknessRange;
    public final ModConfigSpec.ConfigValue<List<String>> flyingCreatures;

    public PearlOfTheVoidConfig(ModConfigSpec.Builder builder) {
        builder.comment("虚空珍珠配置。").push("Pearl of the Void");

        darknessBrightnessThreshold = builder
                .comment(
                        "虚空珍珠黑暗光环的亮度触发阈值。",
                        "目标所在位置的综合亮度必须小于该值，才会被视为暴露在黑暗中。",
                        "默认 3 表示亮度 0、1、2 会触发，与此前写死逻辑保持一致。",
                        "设为 16 表示普通生物在任意亮度下都可触发；设为 0 表示普通亮度条件永不触发。",
                        "未着火的飞行生物仍会按下方 FlyingCreatures 列表直接视为暴露在黑暗中。"
                )
                .defineInRange("DarknessBrightnessThreshold", 3, 0, 16);

        darknessRange = builder
                .comment(
                        "虚空珍珠黑暗光环的伤害与负面效果扫描范围，单位为格。",
                        "默认 16.0"
                )
                .defineInRange("DarknessRange", 16.0D, 0.0D, 256.0D);

        flyingCreatures = builder
                .comment(
                        "虚空珍珠黑暗光环视为“飞行生物”的实体列表。",
                        "列表中的实体如果没有着火，会忽略亮度阈值，直接视为暴露在黑暗中。",
                        "默认只包含原版幻翼，与此前写死逻辑保持一致。",
                        "格式：TOML 字符串数组。",
                        "每一项必须是精确实体注册名，格式为 namespace:path，不支持通配符。",
                        "两个实体之间使用英文逗号 , 隔开，允许换行和缩进。",
                        "示例：",
                        "FlyingCreatures = [",
                        "    \"minecraft:phantom\",",
                        "    \"modid:some_flying_mob\",",
                        "]"
                )
                .define(
                        "FlyingCreatures",
                        List.of("minecraft:phantom"),
                        ConfigListValidators::isValidExactResourceIdList
                );

        builder.pop();
    }
}
