package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 禁果配置。
 */
public class ForbiddenFruitConfig {
    public final ModConfigSpec.IntValue regenerationSubtraction;
    public final ModConfigSpec.DoubleValue debuffDurationMultiplier;

    public ForbiddenFruitConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "禁果配置",
                "食用禁果后获得的永久效果相关设置。"
        ).push("Forbidden Fruit");

        regenerationSubtraction = builder
                .comment(
                        "食用禁果后，小额治疗脉冲被削减的百分比。",
                        "原项目默认值为 80，表示自然恢复只保留 20% 的治疗量。"
                )
                .defineInRange("ForbiddenFruitRegenerationSubtraction", 80, 0, 100);

        debuffDurationMultiplier = builder
                .comment("食用禁果后，初始负面效果持续时间的倍率。")
                .defineInRange("ForbiddenFruitDebuffDurationMultiplier", 1.0D, 0.0D, 100.0D);

        builder.pop();
    }
}
