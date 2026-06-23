package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 磁力之戒 / 转位之戒配置。
 */
public class MagnetConfig {
    public final ModConfigSpec.DoubleValue magnetRingRange;
    public final ModConfigSpec.DoubleValue dislocationRingRange;

    public MagnetConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "磁力之戒配置",
                "控制 Magnetic Ring / 磁力之戒的吸取范围。"
        ).push("Magnet Ring");

        magnetRingRange = builder
                .comment("磁力之戒吸取掉落物的半径。", "原项目默认值为 8。")
                .defineInRange("MagnetRingRange", 8.0D, 1.0D, 256.0D);

        builder.pop();

        builder.comment(
                "转位之戒配置",
                "控制 Dislocation Ring / 转位之戒的远程拾取范围。"
        ).push("Dislocation Ring");

        dislocationRingRange = builder
                .comment("转位之戒远程拾取掉落物的半径。", "原项目默认值为 16。")
                .defineInRange("DislocationRingRange", 16.0D, 1.0D, 256.0D);

        builder.pop();
    }
}