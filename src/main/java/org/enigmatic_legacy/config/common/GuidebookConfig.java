package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 指南类物品配置。
 */
public class GuidebookConfig {
    public final ModConfigSpec.IntValue hunterGuideEffectiveDistance;
    public final ModConfigSpec.IntValue hunterGuideSynergyDamageReduction;
    public final ModConfigSpec.DoubleValue sanguinaryHandbookPetDamageMultiplier;

    public GuidebookConfig(ModConfigSpec.Builder builder) {
        builder.comment("指南类物品配置").push("Guidebooks");

        hunterGuideEffectiveDistance = builder
                .comment("野猎指南会将多少格范围内宠物受到的伤害转移给主人。")
                .defineInRange("HunterGuideEffectiveDistance", 24, 0, 256);

        hunterGuideSynergyDamageReduction = builder
                .comment("同时持有兽友指南时，野猎指南转移伤害降低的百分比。")
                .defineInRange("HunterGuideSynergyDamageReduction", 50, 0, 100);

        sanguinaryHandbookPetDamageMultiplier = builder
                .comment("血腥狩猎手册给予宠物的基础伤害倍率。0.25 表示 +25%。")
                .defineInRange("SanguinaryHandbookPetDamageMultiplier", 0.25D, 0.0D, 1024.0D);

        builder.pop();
    }
}
