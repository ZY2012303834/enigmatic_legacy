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
    public static final ModConfigSpec.BooleanValue FORBIDDEN_FRUIT_RENDER_HUNGER_BAR;
    public static final ModConfigSpec.BooleanValue FORBIDDEN_FRUIT_REPLACE_HUNGER_BAR;


    public static final ModConfigSpec.BooleanValue ENDER_RING_BUTTON_ENABLED;
    public static final ModConfigSpec.IntValue ENDER_RING_BUTTON_OFFSET_X;
    public static final ModConfigSpec.IntValue ENDER_RING_BUTTON_OFFSET_Y;
    public static final ModConfigSpec.IntValue ENDER_RING_BUTTON_OFFSET_X_CREATIVE;
    public static final ModConfigSpec.IntValue ENDER_RING_BUTTON_OFFSET_Y_CREATIVE;

    // ==============================
// 磁力之戒按钮配置
// ==============================

    /**
     * 是否在玩家装备磁力之戒时，在背包界面显示磁力开关按钮。
     */
    public static final ModConfigSpec.BooleanValue MAGNET_RING_BUTTON_ENABLED;

    /**
     * 磁力之戒按钮在普通背包界面的 X 偏移。
     */
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_X;

    /**
     * 磁力之戒按钮在普通背包界面的 Y 偏移。
     */
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_Y;

    /**
     * 磁力之戒按钮在创造模式背包界面的 X 偏移。
     */
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_X_CREATIVE;

    /**
     * 磁力之戒按钮在创造模式背包界面的 Y 偏移。
     */
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_Y_CREATIVE;

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

        FORBIDDEN_FRUIT_RENDER_HUNGER_BAR = builder
                .comment("Whether to render a hunger bar after The Forbidden Fruit has been consumed.")
                .define("ForbiddenFruitRenderHungerBar", true);

        FORBIDDEN_FRUIT_REPLACE_HUNGER_BAR = builder
                .comment("Whether The Forbidden Fruit replaces vanilla hunger icons with its original custom icons.")
                .define("ForbiddenFruitReplaceHungerBar", true);

        ENDER_RING_BUTTON_ENABLED = builder
                .comment(
                        "是否在玩家装备末影之戒或七咒之戒时，在背包界面显示打开末影箱的按钮。",
                        "对应原项目 Ring of Ender 的背包 UI 按钮。"
                )
                .define("EnderRingButtonEnabled", true);

        ENDER_RING_BUTTON_OFFSET_X = builder
                .comment("末影之戒按钮在普通背包界面的 X 偏移。")
                .defineInRange("EnderRingButtonOffsetX", 0, -32768, 32768);

        ENDER_RING_BUTTON_OFFSET_Y = builder
                .comment("末影之戒按钮在普通背包界面的 Y 偏移。")
                .defineInRange("EnderRingButtonOffsetY", 0, -32768, 32768);

        ENDER_RING_BUTTON_OFFSET_X_CREATIVE = builder
                .comment("末影之戒按钮在创造模式背包界面的 X 偏移。")
                .defineInRange("EnderRingButtonOffsetXCreative", 0, -32768, 32768);

        ENDER_RING_BUTTON_OFFSET_Y_CREATIVE = builder
                .comment("末影之戒按钮在创造模式背包界面的 Y 偏移。")
                .defineInRange("EnderRingButtonOffsetYCreative", 0, -32768, 32768);

        // ==============================
// 磁力之戒按钮配置
// ==============================

        MAGNET_RING_BUTTON_ENABLED = builder
                .comment(
                        "是否在玩家装备磁力之戒时，在背包界面显示磁力开关按钮。",
                        "按钮只负责发送切换命令；真正的磁力吸取逻辑仍由服务端判断。"
                )
                .define("MagnetRingButtonEnabled", true);

        MAGNET_RING_BUTTON_OFFSET_X = builder
                .comment(
                        "磁力之戒按钮在普通背包界面的 X 偏移。",
                        "默认 0，按钮会显示在背包右侧。"
                )
                .defineInRange("MagnetRingButtonOffsetX", 0, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_Y = builder
                .comment(
                        "磁力之戒按钮在普通背包界面的 Y 偏移。",
                        "默认 24，避免和末影之戒按钮重叠。"
                )
                .defineInRange("MagnetRingButtonOffsetY", 24, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_X_CREATIVE = builder
                .comment(
                        "磁力之戒按钮在创造模式背包界面的 X 偏移。"
                )
                .defineInRange("MagnetRingButtonOffsetXCreative", 0, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_Y_CREATIVE = builder
                .comment(
                        "磁力之戒按钮在创造模式背包界面的 Y 偏移。",
                        "默认 24，避免和末影之戒按钮重叠。"
                )
                .defineInRange("MagnetRingButtonOffsetYCreative", 24, -32768, 32768);

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
