package org.enigmatic_legacy.config.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 七咒之戒配置。
 */
public class CursedRingConfig {
    public final ModConfigSpec.IntValue painModifier;
    public final ModConfigSpec.IntValue monsterDamageDebuff;
    public final ModConfigSpec.IntValue armorDebuff;
    public final ModConfigSpec.IntValue experienceBonus;
    public final ModConfigSpec.IntValue knockbackDebuff;
    public final ModConfigSpec.IntValue fortuneBonus;
    public final ModConfigSpec.IntValue lootingBonus;
    public final ModConfigSpec.IntValue enchantingBonus;

    public final ModConfigSpec.DoubleValue neutralAngerRange;
    public final ModConfigSpec.DoubleValue neutralXrayRange;
    public final ModConfigSpec.DoubleValue endermanRandomTeleportRange;
    public final ModConfigSpec.DoubleValue endermanRandomTeleportFrequency;
    public final ModConfigSpec.DoubleValue superCursedTime;

    public final ModConfigSpec.BooleanValue saveTheBees;
    public final ModConfigSpec.BooleanValue ultraHardcore;
    public final ModConfigSpec.BooleanValue specialDropsEnabled;
    public final ModConfigSpec.BooleanValue disableInsomnia;
    public final ModConfigSpec.ConfigValue<List<String>> neutralAngerBlacklist;
    public final ModConfigSpec.ConfigValue<List<String>> animalGuideAnimalExclusionList;

    public CursedRingConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "七咒之戒配置",
                "当前项目先实现核心可迁移功能，时运、抢夺、附魔台加成后续单独接入。"
        ).push("The Seven Curses");

        painModifier = builder
                .comment("七咒之戒佩戴者受到的伤害倍率，单位为百分比。", "原项目默认值为 200。")
                .defineInRange("CursedRingPainModifier", 200, 0, 10000);

        monsterDamageDebuff = builder
                .comment("七咒之戒佩戴者对怪物造成的伤害降低百分比。", "原项目默认值为 50。")
                .defineInRange("CursedRingMonsterDamageDebuff", 50, 0, 100);

        armorDebuff = builder
                .comment("七咒之戒佩戴者护甲减免降低百分比。", "原项目默认值为 30。")
                .defineInRange("CursedRingArmorDebuff", 30, 0, 100);

        experienceBonus = builder
                .comment("七咒之戒佩戴者击杀生物获得的经验倍率，单位为百分比。", "原项目默认值为 400。")
                .defineInRange("CursedRingExperienceBonus", 400, 0, 10000);

        knockbackDebuff = builder
                .comment("七咒之戒佩戴者受到的击退倍率，单位为百分比。", "原项目默认值为 200。")
                .defineInRange("CursedRingKnockbackDebuff", 200, 0, 10000);

        fortuneBonus = builder
                .comment("七咒之戒提供的额外时运等级。")
                .defineInRange("CursedRingFortuneBonus", 1, 0, 100);

        lootingBonus = builder
                .comment("七咒之戒提供的额外抢夺等级。")
                .defineInRange("CursedRingLootingBonus", 1, 0, 100);

        enchantingBonus = builder
                .comment("七咒之戒给附魔台提供的额外附魔能量。")
                .defineInRange("CursedRingEnchantingBonus", 10, 0, 1000);

        neutralAngerRange = builder
                .comment("中立生物会被七咒之戒激怒的范围。")
                .defineInRange("CursedRingNeutralAngerRange", 24.0D, 4.0D, 256.0D);

        neutralXrayRange = builder
                .comment("中立生物无视视线仇恨七咒之戒佩戴者的最小范围。")
                .defineInRange("CursedRingNeutralXRayRange", 4.0D, 0.0D, 256.0D);

        endermanRandomTeleportRange = builder
                .comment("末影人尝试随机传送到七咒之戒佩戴者附近的范围。")
                .defineInRange("CursedRingEndermanRandomTeleportRange", 32.0D, 8.0D, 256.0D);

        endermanRandomTeleportFrequency = builder
                .comment("末影人随机传送频率倍率。")
                .defineInRange("CursedRingEndermanRandomTeleportFrequency", 1.0D, 0.01D, 100.0D);

        superCursedTime = builder
                .comment("玩家需要佩戴七咒之戒多久才视为 Super Cursed，数值为总时间比例。")
                .defineInRange("CursedRingSuperCursedTime", 0.995D, 0.0D, 1.0D);

        saveTheBees = builder
                .comment("是否保护蜜蜂不受七咒之戒第二诅咒影响。")
                .define("CursedRingSaveTheBees", false);

        ultraHardcore = builder
                .comment("是否在玩家首次进入世界时直接给予七咒之戒。")
                .define("CursedRingUltraHardcore", false);

        specialDropsEnabled = builder
                .comment("是否启用七咒之戒佩戴者可从原版生物获得的特殊掉落。")
                .define("CursedRingEnableSpecialDrops", true);

        disableInsomnia = builder
                .comment("是否禁用七咒之戒的失眠诅咒效果。")
                .define("CursedRingDisableInsomnia", false);

        neutralAngerBlacklist = builder
                .comment(
                        "七咒之戒第二诅咒的中立生物仇恨黑名单。",
                        "列表中的实体不会被七咒之戒激怒。",
                        "格式：TOML 字符串数组。",
                        "两个实体之间使用英文逗号 , 隔开。",
                        "允许换行和空格。",
                        "支持两种写法：",
                        "1. 精确实体 ID，例如：minecraft:bee",
                        "2. 整个命名空间通配，例如：resourcefulbees:*",
                        "示例：",
                        "CursedRingNeutralAngerBlacklist = [",
                        "    \"minecraft:iron_golem\",",
                        "    \"guardvillagers:guard\",",
                        "]"
                )
                .define(
                        "CursedRingNeutralAngerBlacklist",
                        List.of(
                                "minecraft:iron_golem",
                                "guardvillagers:guard",
                                "irons_spellbooks:pyromancer",
                                "irons_spellbooks:cryomancer",
                                "irons_spellbooks:priest",
                                "irons_spellbooks:apothecarist",
                                "touhou_little_maid_spell:elf_templar"
                        ),
                        CursedRingConfig::isValidEntityIdList
                );

        animalGuideAnimalExclusionList = builder
                .comment(
                        "兽友指南额外保护实体列表。",
                        "列表中的实体会被兽友指南视为可驯服动物，用于削弱七咒之戒第二诅咒。",
                        "格式：TOML 字符串数组。",
                        "示例：",
                        "AnimalGuideAnimalExclusionList = [",
                        "    \"minecraft:iron_golem\",",
                        "    \"minecraft:zombified_piglin\"",
                        "]"
                )
                .define(
                        "AnimalGuideAnimalExclusionList",
                        List.of(),
                        CursedRingConfig::isValidExactEntityIdList
                );

        builder.pop();
    }

    private static boolean isValidEntityIdList(Object value) {
        if (!(value instanceof List<?> list)) {
            return false;
        }

        for (Object element : list) {
            if (!(element instanceof String string)) {
                return false;
            }

            String trimmed = string.trim();

            if (trimmed.isEmpty()) {
                return false;
            }

            if (trimmed.endsWith(":*")) {
                String namespace = trimmed.substring(0, trimmed.length() - 2);
                if (!ResourceLocation.isValidNamespace(namespace)) {
                    return false;
                }

                continue;
            }

            if (ResourceLocation.tryParse(trimmed) == null) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidExactEntityIdList(Object value) {
        if (!(value instanceof List<?> list)) {
            return false;
        }

        for (Object element : list) {
            if (!(element instanceof String string)) {
                return false;
            }

            if (ResourceLocation.tryParse(string.trim()) == null) {
                return false;
            }
        }

        return true;
    }

}
