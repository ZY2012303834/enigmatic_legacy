package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 物品与方块启用开关。
 */
public class ItemOptionsConfig {
    public final ModConfigSpec.BooleanValue cursedRingEnabled;
    public final ModConfigSpec.BooleanValue cosmicHeartEnabled;
    public final ModConfigSpec.BooleanValue earthHeartFragmentEnabled;
    public final ModConfigSpec.BooleanValue bigLampEnabled;
    public final ModConfigSpec.BooleanValue bigShroomlampEnabled;
    public final ModConfigSpec.BooleanValue astralDustEnabled;
    public final ModConfigSpec.BooleanValue enderRodEnabled;
    public final ModConfigSpec.BooleanValue etheriumIngotEnabled;
    public final ModConfigSpec.BooleanValue etheriumOreEnabled;
    public final ModConfigSpec.BooleanValue thiccScrollEnabled;
    public final ModConfigSpec.BooleanValue darkestScrollEnabled;

    public ItemOptionsConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "物品与方块启用开关",
                "后续实现功能时，建议在功能触发、配方生成、战利品注入等位置读取这些开关。"
        ).push("Item Options");

        cursedRingEnabled = builder
                .comment("是否启用七咒之戒。后续可控制初始给予、Curios 佩戴、诅咒功能触发等。")
                .define("CursedRingEnabled", true);

        cosmicHeartEnabled = builder
                .comment("是否启用寰宇之心。后续可控制配方、战利品或特殊效果。")
                .define("CosmicHeartEnabled", true);

        earthHeartFragmentEnabled = builder
                .comment("是否启用大地之心碎片。后续可控制战利品、酿造或相关功能。")
                .define("EarthHeartFragmentEnabled", true);

        bigLampEnabled = builder
                .comment("是否启用大灯笼。后续可控制配方、创造栏显示或其他获取方式。")
                .define("BigLampEnabled", true);

        bigShroomlampEnabled = builder
                .comment("是否启用菌光体灯笼。后续可控制配方、创造栏显示或其他获取方式。")
                .define("BigShroomlampEnabled", true);

        astralDustEnabled = builder
                .comment("是否启用星尘。")
                .define("AstralDustEnabled", true);

        enderRodEnabled = builder
                .comment("是否启用末影棒。")
                .define("EnderRodEnabled", true);

        etheriumIngotEnabled = builder
                .comment("是否启用以太锭。")
                .define("EtheriumIngotEnabled", true);

        etheriumOreEnabled = builder
                .comment("是否启用以太矿石。")
                .define("EtheriumOreEnabled", true);

        thiccScrollEnabled = builder
                .comment("是否启用空卷轴。")
                .define("ThiccScrollEnabled", true);

        darkestScrollEnabled = builder
                .comment("是否启用至暗卷轴。")
                .define("DarkestScrollEnabled", true);

        builder.pop();
    }
}