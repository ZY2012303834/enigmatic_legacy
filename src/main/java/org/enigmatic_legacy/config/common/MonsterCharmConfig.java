package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 怪物猎人勋章配置。
 */
public class MonsterCharmConfig {
    public final ModConfigSpec.IntValue undeadDamage;
    public final ModConfigSpec.IntValue hostileDamage;
    public final ModConfigSpec.BooleanValue bonusLootingEnabled;
    public final ModConfigSpec.BooleanValue doubleXpEnabled;

    public MonsterCharmConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "怪物猎人勋章配置"
        ).push("Monster Charm");

        undeadDamage = builder
                .comment("怪物猎人勋章对亡灵生物提供的额外伤害百分比。", "原项目默认值为 25。")
                .defineInRange("MonsterCharmUndeadDamage", 25, 0, 1000);

        hostileDamage = builder
                .comment("怪物猎人勋章对敌对生物提供的额外伤害百分比。", "原项目默认值为 10。")
                .defineInRange("MonsterCharmHostileDamage", 10, 0, 1000);

        bonusLootingEnabled = builder
                .comment("怪物猎人勋章是否提供 +1 Looting。")
                .define("MonsterCharmBonusLooting", true);

        doubleXpEnabled = builder
                .comment("怪物猎人勋章是否让怪物掉落双倍经验。")
                .define("MonsterCharmDoubleXP", true);

        builder.pop();
    }
}