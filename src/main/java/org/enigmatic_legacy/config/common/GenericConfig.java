package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 通用机制配置。
 *
 * <p>这里只保留跨多个系统共用的基础机制配置。
 * 具体物品自己的数值配置应拆到独立配置类中，避免 GenericConfig 继续膨胀。</p>
 */
public class GenericConfig {
    public final ModConfigSpec.IntValue soulCrystalsMode;
    public final ModConfigSpec.IntValue maxSoulCrystalLoss;
    public final ModConfigSpec.ConfigValue<List<String>> completeBossList;

    public GenericConfig(ModConfigSpec.Builder builder) {
        builder.comment("通用机制配置。").push("Generic Config");

        soulCrystalsMode = builder
                .comment(
                        "灵魂水晶机制模式。",
                        "0：默认关闭，除非某些物品或机制强制启用。",
                        "1：当 keepInventory 为 true 时也启用。",
                        "2：始终启用。"
                )
                .defineInRange("SoulCrystalsMode", 0, 0, 2);

        maxSoulCrystalLoss = builder
                .comment(
                        "玩家最多可以损失多少个灵魂水晶。",
                        "每损失 1 个灵魂水晶，后续可用于减少 10% 最大生命上限。"
                )
                .defineInRange("MaxSoulCrystalLoss", 9, 1, 9);

        completeBossList = builder
                .comment(
                        "完整 Boss 实体列表。",
                        "后续实现 Boss 判断、特殊掉落或饰品效果时，会使用该列表判断实体是否属于 Boss。",
                        "格式：TOML 字符串数组。",
                        "每一项必须是精确实体注册名，格式为 namespace:path，不支持通配符。",
                        "两个实体之间使用英文逗号 , 隔开，允许换行和缩进。",
                        "示例：",
                        "CompleteBossList = [",
                        "    \"minecraft:ender_dragon\",",
                        "    \"minecraft:wither\",",
                        "    \"cataclysm:ender_golem\",",
                        "]"
                )
                .define(
                        "CompleteBossList",
                        List.of(
                                "minecraft:ender_dragon",
                                "minecraft:wither",
                                "minecraft:elder_guardian",
                                "cataclysm:ender_golem",
                                "cataclysm:ender_guardian",
                                "cataclysm:netherite_monstrosity",
                                "cataclysm:ignis",
                                "cataclysm:ignited_revenant",
                                "cataclysm:the_harbinger",
                                "cataclysm:the_leviathan",
                                "cataclysm:coralssus",
                                "cataclysm:amethyst_crab",
                                "cataclysm:ancient_remnant",
                                "cataclysm:kobolediator",
                                "cataclysm:wadjet",
                                "cataclysm:maledictus",
                                "cataclysm:scylla"
                        ),
                        ConfigListValidators::isValidExactResourceIdList
                );

        builder.pop();
    }
}
