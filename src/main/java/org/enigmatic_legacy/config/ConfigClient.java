package org.enigmatic_legacy.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 神秘遗物客户端配置。
 *
 * 说明：
 * 这里存放只影响本地显示、客户端操作、视觉效果、提示信息等内容的配置。
 * 使用 ModConfig.Type.CLIENT 注册后，只会在物理客户端加载，不会同步到服务器。
 */
public class ConfigClient {

    public static final ModConfigSpec SPEC;

    // ==============================
    // 客户端显示 / 操作配置
    // ==============================

    public static final ModConfigSpec.ConfigValue<String> ACKNOWLEDGMENT_OVERFLOW_MODE;
    public static final ModConfigSpec.BooleanValue ANGEL_BLESSING_DOUBLE_JUMP;
    public static final ModConfigSpec.BooleanValue DISABLE_QUOTE_SUBTITLES;

    // ==============================
    // 你项目当前阶段可用的客户端配置
    // ==============================

    public static final ModConfigSpec.BooleanValue SHOW_CURIO_SLOT_HINT;
    public static final ModConfigSpec.BooleanValue ENABLE_ITEM_ANIMATED_TEXTURES_HINT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
                "客户端选项",
                "这里的配置只影响本地客户端，不影响服务器逻辑。"
        ).push("Client Options");

        ACKNOWLEDGMENT_OVERFLOW_MODE = builder
                .comment(
                        "The Acknowledgment 文本溢出模式。",
                        "原项目该项来自 Patchouli 文本显示设置。",
                        "当前项目暂未实现手册系统，此项先保留。"
                )
                .define("AcknowledgmentOverflowMode", "OVERFLOW");

        ANGEL_BLESSING_DOUBLE_JUMP = builder
                .comment(
                        "是否允许天使祝福通过空中再次按跳跃键触发主动能力。",
                        "当前项目暂未实现天使祝福，此项先保留。"
                )
                .define("AngelBlessingDoubleJump", true);

        DISABLE_QUOTE_SUBTITLES = builder
                .comment(
                        "是否禁用建筑师旁白字幕。",
                        "当前项目暂未实现旁白系统，此项先保留。"
                )
                .define("DisableQuoteSubtitles", false);

        SHOW_CURIO_SLOT_HINT = builder
                .comment(
                        "是否在相关物品提示中显示 Curios 栏位提示。",
                        "例如后续可以在七咒之戒提示中显示：可放入戒指栏位。"
                )
                .define("ShowCurioSlotHint", true);

        ENABLE_ITEM_ANIMATED_TEXTURES_HINT = builder
                .comment(
                        "是否在日志或调试提示中提醒动画材质需要 .png.mcmeta。",
                        "仅用于开发阶段提示，不影响动画贴图本身。"
                )
                .define("EnableItemAnimatedTexturesHint", true);

        builder.pop();

        SPEC = builder.build();
    }

    /**
     * 判断是否显示 Curios 栏位提示。
     *
     * 后续实现 Tooltip 时可以读取该配置。
     */
    public static boolean shouldShowCurioSlotHint() {
        return SHOW_CURIO_SLOT_HINT.get();
    }
}