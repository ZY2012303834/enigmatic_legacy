package org.enigmatic_legacy.config.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 通用机制配置。
 */
public class GenericConfig {
    public final ModConfigSpec.IntValue soulCrystalsMode;
    public final ModConfigSpec.IntValue maxSoulCrystalLoss;
    public final ModConfigSpec.ConfigValue<List<? extends String>> completeBossList;

    public GenericConfig(ModConfigSpec.Builder builder) {
        builder.comment("通用机制配置").push("Generic Config");

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
                        "每损失 1 个灵魂水晶，后续可用于减少 10% 最大生命上限。",
                        "如果设置为 10，代表玩家最多可能损失全部灵魂水晶。"
                )
                .defineInRange("MaxSoulCrystalLoss", 9, 1, 10);

        completeBossList = builder
                .comment(
                        "完整 Boss 列表。",
                        "后续实现 Boss 判断、特殊掉落或饰品效果时，可使用该列表。",
                        "格式示例：minecraft:wither",
                        "允许该列表为空；为空时表示不额外识别任何 Boss。"
                )
                .defineListAllowEmpty(
                        List.of("CompleteBossList"),
                        List.of(
                                "minecraft:ender_dragon",
                                "minecraft:wither",
                                "minecraft:elder_guardian"
                        ),
                        () -> "minecraft:wither",
                        value -> value instanceof String string
                                && ResourceLocation.tryParse(string) != null
                );

        builder.pop();
    }
}