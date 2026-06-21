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