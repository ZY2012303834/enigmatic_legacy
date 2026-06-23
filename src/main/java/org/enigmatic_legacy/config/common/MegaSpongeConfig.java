package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 超级海绵配置。
 */
public class MegaSpongeConfig {
    public final ModConfigSpec.IntValue radius;

    public MegaSpongeConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "超级海绵配置",
                "这些配置参考原 Enigmatic Legacy 的 Extrapolated Megasponge。"
        ).push("Mega Sponge");

        radius = builder
                .comment("超级海绵吸收水体的连锁半径。", "原项目默认值为 4，等同于原版海绵范围。")
                .defineInRange("MegaspongeRadius", 4, 0, 128);

        builder.pop();
    }
}