package org.enigmatic_legacy.config.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 通用机制配置。
 * <p>
 * 这里只保留跨多个系统共用的基础机制配置。
 * 具体物品自己的数值配置应拆到独立配置类中，避免 GenericConfig 继续膨胀。
 */
public class GenericConfig {
    public final ModConfigSpec.IntValue soulCrystalsMode;
    public final ModConfigSpec.IntValue maxSoulCrystalLoss;
    public final ModConfigSpec.ConfigValue<List<? extends String>> completeBossList;

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
                        "完整 Boss 列表。",
                        "后续实现 Boss 判断、特殊掉落或饰品效果时，可使用该列表。",
                        "格式示例：minecraft:wither"
                )
                .defineList(
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
                        () -> "minecraft:ender_dragon",
                        value -> value instanceof String string
                                && ResourceLocation.tryParse(string) != null
                );

        builder.pop();
    }
}
