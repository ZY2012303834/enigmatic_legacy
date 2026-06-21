package org.enigmatic_legacy.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 神秘遗物服务器配置。
 * <p>
 * 说明：
 * 这里存放会影响玩法、平衡、物品功能、战利品、配方等内容的配置。
 * 使用 ModConfig.Type.SERVER 注册后，该配置可以按世界保存，并且会同步到客户端。
 */
public class ConfigCommon {

    public static final ModConfigSpec SPEC;

    // ==============================
    // 可访问性 / 基础机制配置
    // ==============================

    public static final ModConfigSpec.BooleanValue CUSTOM_DUNGEON_LOOT_ENABLED;
    public static final ModConfigSpec.BooleanValue BONUS_WOOL_RECIPES_ENABLED;
    public static final ModConfigSpec.BooleanValue DISABLE_AOE_SHIFT_SUPPRESSION;
    public static final ModConfigSpec.BooleanValue RETRIGGER_RECIPE_UNLOCKS;
    public static final ModConfigSpec.BooleanValue CRASH_ON_UNNAMED_POOL;

    // ==============================
    // 灵魂水晶机制配置
    // ==============================

    public static final ModConfigSpec.IntValue SOUL_CRYSTALS_MODE;
    public static final ModConfigSpec.IntValue MAX_SOUL_CRYSTAL_LOSS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> COMPLETE_BOSS_LIST;

    // ==============================
    // 已复制物品 / 方块启用开关
    // ==============================

    public static final ModConfigSpec.BooleanValue CURSED_RING_ENABLED;
    public static final ModConfigSpec.BooleanValue COSMIC_HEART_ENABLED;
    public static final ModConfigSpec.BooleanValue EARTH_HEART_FRAGMENT_ENABLED;

    public static final ModConfigSpec.BooleanValue BIG_LAMP_ENABLED;
    public static final ModConfigSpec.BooleanValue BIG_SHROOMLAMP_ENABLED;

    public static final ModConfigSpec.BooleanValue ASTRAL_DUST_ENABLED;
    public static final ModConfigSpec.BooleanValue ENDER_ROD_ENABLED;
    public static final ModConfigSpec.BooleanValue ETHERIUM_INGOT_ENABLED;
    public static final ModConfigSpec.BooleanValue ETHERIUM_ORE_ENABLED;
    public static final ModConfigSpec.BooleanValue THICC_SCROLL_ENABLED;
    public static final ModConfigSpec.BooleanValue DARKEST_SCROLL_ENABLED;

    // ==============================
    // 七咒之戒功能配置
    // ==============================

    public static final ModConfigSpec.IntValue CURSED_RING_PAIN_MODIFIER;
    public static final ModConfigSpec.IntValue CURSED_RING_MONSTER_DAMAGE_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_ARMOR_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_EXPERIENCE_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_KNOCKBACK_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_FORTUNE_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_LOOTING_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_ENCHANTING_BONUS;

    public static final ModConfigSpec.DoubleValue CURSED_RING_NEUTRAL_ANGER_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_NEUTRAL_XRAY_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_ENDERMAN_RANDOM_TELEPORT_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_ENDERMAN_RANDOM_TELEPORT_FREQUENCY;
    public static final ModConfigSpec.DoubleValue CURSED_RING_SUPER_CURSED_TIME;

    public static final ModConfigSpec.BooleanValue CURSED_RING_SAVE_THE_BEES;
    public static final ModConfigSpec.BooleanValue CURSED_RING_ULTRA_HARDCORE;
    public static final ModConfigSpec.BooleanValue CURSED_RING_SPECIAL_DROPS_ENABLED;
    public static final ModConfigSpec.BooleanValue CURSED_RING_DISABLE_INSOMNIA;
    public static final ModConfigSpec.IntValue FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_FRUIT_DEBUFF_DURATION_MULTIPLIER;

    // ==============================
    // 磁力之戒配置
    // ==============================
    public static final ModConfigSpec.DoubleValue MAGNET_RING_RANGE;

    public static final ModConfigSpec.BooleanValue MAGNET_RING_BUTTON_ENABLED;
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_X;
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_Y;
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_X_CREATIVE;
    public static final ModConfigSpec.IntValue MAGNET_RING_BUTTON_OFFSET_Y_CREATIVE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("可访问性选项", "这里的配置用于控制部分机制是否启用。", "注意：这些配置不会在运行时删除已注册内容，只建议用于控制配方、战利品、初始给予和功能触发。").push("Accessibility Options");

        CUSTOM_DUNGEON_LOOT_ENABLED = builder.comment("是否允许本模组向地牢箱子、遗迹箱子等战利品表中添加自定义战利品。").define("CustomDungeonLootEnabled", true);

        BONUS_WOOL_RECIPES_ENABLED = builder.comment("是否启用额外羊毛染色配方。").define("BonusWoolRecipesEnabled", true);

        DISABLE_AOE_SHIFT_SUPPRESSION = builder.comment("是否禁用 Shift 对范围效果的抑制。", "false：玩家按住 Shift 时，范围挖掘或范围效果会被抑制。", "true：玩家按住 Shift 时，范围效果仍然照常触发。").define("DisableAOEShiftSuppression", false);

        RETRIGGER_RECIPE_UNLOCKS = builder.comment("玩家进入世界时，是否重新触发已解锁配方的 recipe_unlocked 条件。", "原项目用于补发或修复部分配方解锁相关逻辑。").define("RetriggerRecipeUnlocks", true);

        CRASH_ON_UNNAMED_POOL = builder.comment("当其他模组向战利品表注入未命名 LootPool 时，是否主动崩溃游戏。", "true：直接崩溃，适合开发阶段排查问题。", "false：只记录日志，游戏继续运行。").define("CrashOnUnnamedPool", true);

        builder.pop();

        builder.comment("通用机制配置").push("Generic Config");

        SOUL_CRYSTALS_MODE = builder.comment("灵魂水晶机制模式。", "0：默认关闭，除非某些物品或机制强制启用。", "1：当 keepInventory 为 true 时也启用。", "2：始终启用。").defineInRange("SoulCrystalsMode", 0, 0, 2);

        MAX_SOUL_CRYSTAL_LOSS = builder.comment("玩家最多可以损失多少个灵魂水晶。", "每损失 1 个灵魂水晶，后续可用于减少 10% 最大生命上限。", "如果设置为 10，代表玩家最多可能损失全部灵魂水晶。").defineInRange("MaxSoulCrystalLoss", 9, 1, 10);

        COMPLETE_BOSS_LIST = builder.comment("完整 Boss 列表。", "后续实现 Boss 判断、特殊掉落或饰品效果时，可使用该列表。", "格式示例：minecraft:wither").defineList("CompleteBossList", List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:elder_guardian"), value -> value instanceof String && ResourceLocation.tryParse((String) value) != null);

        builder.pop();

        builder.comment("物品与方块启用开关", "这些配置参考原 Enigmatic Legacy 的物品启用选项。", "后续实现功能时，建议在功能触发、配方生成、战利品注入等位置读取这些开关。").push("Item Options");

        CURSED_RING_ENABLED = builder.comment("是否启用七咒之戒。后续可控制初始给予、Curios 佩戴、诅咒功能触发等。").define("CursedRingEnabled", true);

        COSMIC_HEART_ENABLED = builder.comment("是否启用寰宇之心。后续可控制配方、战利品或特殊效果。").define("CosmicHeartEnabled", true);

        EARTH_HEART_FRAGMENT_ENABLED = builder.comment("是否启用大地之心碎片。后续可控制战利品、酿造或相关功能。").define("EarthHeartFragmentEnabled", true);

        BIG_LAMP_ENABLED = builder.comment("是否启用大灯笼。后续可控制配方、创造栏显示或其他获取方式。").define("BigLampEnabled", true);

        BIG_SHROOMLAMP_ENABLED = builder.comment("是否启用菌光体灯笼。后续可控制配方、创造栏显示或其他获取方式。").define("BigShroomlampEnabled", true);

        ASTRAL_DUST_ENABLED = builder.comment("是否启用星尘。").define("AstralDustEnabled", true);

        ENDER_ROD_ENABLED = builder.comment("是否启用末影棒。").define("EnderRodEnabled", true);

        ETHERIUM_INGOT_ENABLED = builder.comment("是否启用以太锭。").define("EtheriumIngotEnabled", true);

        ETHERIUM_ORE_ENABLED = builder.comment("是否启用以太矿石。").define("EtheriumOreEnabled", true);

        THICC_SCROLL_ENABLED = builder.comment("是否启用空卷轴。").define("ThiccScrollEnabled", true);

        DARKEST_SCROLL_ENABLED = builder.comment("是否启用至暗卷轴。").define("DarkestScrollEnabled", true);

        builder.pop();

        builder.comment(
                "七咒之戒配置",
                "这些配置参考原 Enigmatic Legacy 的 Ring of the Seven Curses。",
                "当前项目先实现核心可迁移功能，时运、抢夺、附魔台加成后续单独接入。"
        ).push("The Seven Curses");

        CURSED_RING_PAIN_MODIFIER = builder
                .comment(
                        "七咒之戒佩戴者受到的伤害倍率，单位为百分比。",
                        "原项目默认值为 200，表示受到 200% 伤害。"
                )
                .defineInRange("CursedRingPainModifier", 200, 0, 10000);

        CURSED_RING_MONSTER_DAMAGE_DEBUFF = builder
                .comment(
                        "七咒之戒佩戴者对怪物造成的伤害降低百分比。",
                        "原项目默认值为 50，表示对怪物伤害降低 50%。"
                )
                .defineInRange("CursedRingMonsterDamageDebuff", 50, 0, 100);

        CURSED_RING_ARMOR_DEBUFF = builder
                .comment(
                        "七咒之戒佩戴者护甲减免降低百分比。",
                        "原项目默认值为 30，表示护甲减伤效果降低 30%。"
                )
                .defineInRange("CursedRingArmorDebuff", 30, 0, 100);

        CURSED_RING_EXPERIENCE_BONUS = builder
                .comment(
                        "七咒之戒佩戴者击杀生物获得的经验倍率，单位为百分比。",
                        "原项目默认值为 400，表示获得 400% 经验。"
                )
                .defineInRange("CursedRingExperienceBonus", 400, 0, 10000);

        CURSED_RING_KNOCKBACK_DEBUFF = builder
                .comment(
                        "七咒之戒佩戴者受到的击退倍率，单位为百分比。",
                        "原项目默认值为 200，表示受到 200% 击退。"
                )
                .defineInRange("CursedRingKnockbackDebuff", 200, 0, 10000);

        CURSED_RING_FORTUNE_BONUS = builder
                .comment("七咒之戒提供的额外时运等级。")
                .defineInRange("CursedRingFortuneBonus", 1, 0, 100);

        CURSED_RING_LOOTING_BONUS = builder
                .comment("七咒之戒提供的额外抢夺等级。")
                .defineInRange("CursedRingLootingBonus", 1, 0, 100);

        CURSED_RING_ENCHANTING_BONUS = builder
                .comment("七咒之戒给附魔台提供的额外附魔能量。")
                .defineInRange("CursedRingEnchantingBonus", 10, 0, 1000);

        CURSED_RING_NEUTRAL_ANGER_RANGE = builder
                .comment("中立生物会被七咒之戒激怒的范围。")
                .defineInRange("CursedRingNeutralAngerRange", 24.0D, 4.0D, 256.0D);

        CURSED_RING_NEUTRAL_XRAY_RANGE = builder
                .comment(
                        "中立生物无视视线仇恨七咒之戒佩戴者的最小范围。",
                        "距离小于该值时，即使看不到玩家也会尝试仇恨。"
                )
                .defineInRange("CursedRingNeutralXRayRange", 4.0D, 0.0D, 256.0D);

        CURSED_RING_ENDERMAN_RANDOM_TELEPORT_RANGE = builder
                .comment("末影人尝试随机传送到七咒之戒佩戴者附近的范围。")
                .defineInRange("CursedRingEndermanRandomTeleportRange", 32.0D, 8.0D, 256.0D);

        CURSED_RING_ENDERMAN_RANDOM_TELEPORT_FREQUENCY = builder
                .comment(
                        "末影人随机传送频率倍率。",
                        "数值越低，触发概率越低。"
                )
                .defineInRange("CursedRingEndermanRandomTeleportFrequency", 1.0D, 0.01D, 100.0D);

        CURSED_RING_SUPER_CURSED_TIME = builder
                .comment("玩家需要佩戴七咒之戒多久才视为 Super Cursed，数值为总时间比例。")
                .defineInRange("CursedRingSuperCursedTime", 0.995D, 0.0D, 1.0D);

        CURSED_RING_SAVE_THE_BEES = builder
                .comment(
                        "是否保护蜜蜂不受七咒之戒第二诅咒影响。",
                        "true：蜜蜂不会因为戒指主动仇恨玩家。"
                )
                .define("CursedRingSaveTheBees", false);

        CURSED_RING_ULTRA_HARDCORE = builder
                .comment(
                        "是否在玩家首次进入世界时直接给予七咒之戒。",
                        "开启后会尝试直接装备到 Curios 戒指槽；无可用槽位时退回背包。"
                )
                .define("CursedRingUltraHardcore", false);

        CURSED_RING_SPECIAL_DROPS_ENABLED = builder
                .comment("是否启用七咒之戒佩戴者可从原版生物获得的特殊掉落。")
                .define("CursedRingEnableSpecialDrops", true);

        CURSED_RING_DISABLE_INSOMNIA = builder
                .comment("是否禁用七咒之戒的失眠诅咒效果。")
                .define("CursedRingDisableInsomnia", false);

        builder.pop();

        builder.comment(
                "Forbidden Fruit",
                "Settings for the permanent effects granted after eating The Forbidden Fruit."
        ).push("Forbidden Fruit");

        FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION = builder
                .comment(
                        "Percentage of small healing pulses removed after the fruit is consumed.",
                        "The original default is 80, meaning natural regeneration heals only 20% as much."
                )
                .defineInRange("ForbiddenFruitRegenerationSubtraction", 80, 0, 100);

        FORBIDDEN_FRUIT_DEBUFF_DURATION_MULTIPLIER = builder
                .comment("Multiplier applied to the initial debuff durations after eating the fruit.")
                .defineInRange("ForbiddenFruitDebuffDurationMultiplier", 1.0D, 0.0D, 100.0D);

        builder.pop();

        builder.comment(
                "磁力之戒配置",
                "控制 Magnetic Ring / 磁力之戒的吸取范围。"
        ).push("Magnet Ring");

        MAGNET_RING_RANGE = builder
                .comment(
                        "磁力之戒吸取掉落物的半径。",
                        "原项目默认值为 8。"
                )
                .defineInRange("MagnetRingRange", 8.0D, 1.0D, 256.0D);

        builder.pop();

        MAGNET_RING_BUTTON_ENABLED = builder
                .comment(
                        "是否在玩家装备磁力之戒时，在背包界面显示磁力开关按钮。",
                        "按钮只负责发送切换命令；真正的磁力效果仍由服务端判断。"
                )
                .define("MagnetRingButtonEnabled", true);

        MAGNET_RING_BUTTON_OFFSET_X = builder
                .comment("磁力之戒按钮在普通背包界面的 X 偏移。")
                .defineInRange("MagnetRingButtonOffsetX", 0, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_Y = builder
                .comment("磁力之戒按钮在普通背包界面的 Y 偏移。")
                .defineInRange("MagnetRingButtonOffsetY", 0, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_X_CREATIVE = builder
                .comment("磁力之戒按钮在创造模式背包界面的 X 偏移。")
                .defineInRange("MagnetRingButtonOffsetXCreative", 0, -32768, 32768);

        MAGNET_RING_BUTTON_OFFSET_Y_CREATIVE = builder
                .comment("磁力之戒按钮在创造模式背包界面的 Y 偏移。")
                .defineInRange("MagnetRingButtonOffsetYCreative", 0, -32768, 32768);

        SPEC = builder.build();
    }

    /**
     * 判断指定实体 ID 是否在 Boss 列表中。
     *
     * @param entityId 实体 ID，例如 minecraft:wither
     * @return 如果配置列表包含该实体 ID，则返回 true
     */
    public static boolean isBoss(ResourceLocation entityId) {
        return COMPLETE_BOSS_LIST.get().contains(entityId.toString());
    }

    /**
     * 判断七咒之戒是否启用。
     * <p>
     * 后续实现七咒之戒功能时，建议在佩戴、给予、效果触发前调用。
     */
    public static boolean isCursedRingEnabled() {
        return CURSED_RING_ENABLED.get();
    }
}
