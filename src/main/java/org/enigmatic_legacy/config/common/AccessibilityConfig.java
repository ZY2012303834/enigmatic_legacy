package org.enigmatic_legacy.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 可访问性 / 基础机制配置。
 */
public class AccessibilityConfig {
    public final ModConfigSpec.BooleanValue customDungeonLootEnabled;
    public final ModConfigSpec.BooleanValue bonusWoolRecipesEnabled;
    public final ModConfigSpec.BooleanValue disableAoeShiftSuppression;
    public final ModConfigSpec.BooleanValue retriggerRecipeUnlocks;
    public final ModConfigSpec.BooleanValue crashOnUnnamedPool;

    public AccessibilityConfig(ModConfigSpec.Builder builder) {
        builder.comment(
                "可访问性选项",
                "这里的配置用于控制部分机制是否启用。",
                "注意：这些配置不会在运行时删除已注册内容，只建议用于控制配方、战利品、初始给予和功能触发。"
        ).push("Accessibility Options");

        customDungeonLootEnabled = builder
                .comment("是否允许本模组向地牢箱子、遗迹箱子等战利品表中添加自定义战利品。")
                .define("CustomDungeonLootEnabled", true);

        bonusWoolRecipesEnabled = builder
                .comment("是否启用额外羊毛染色配方。")
                .define("BonusWoolRecipesEnabled", true);

        disableAoeShiftSuppression = builder
                .comment(
                        "是否禁用 Shift 对范围效果的抑制。",
                        "false：玩家按住 Shift 时，范围挖掘或范围效果会被抑制。",
                        "true：玩家按住 Shift 时，范围效果仍然照常触发。"
                )
                .define("DisableAOEShiftSuppression", false);

        retriggerRecipeUnlocks = builder
                .comment(
                        "玩家进入世界时，是否重新触发已解锁配方的 recipe_unlocked 条件。",
                        "原项目用于补发或修复部分配方解锁相关逻辑。"
                )
                .define("RetriggerRecipeUnlocks", true);

        crashOnUnnamedPool = builder
                .comment(
                        "当其他模组向战利品表注入未命名 LootPool 时，是否主动崩溃游戏。",
                        "true：直接崩溃，适合开发阶段排查问题。",
                        "false：只记录日志，游戏继续运行。"
                )
                .define("CrashOnUnnamedPool", true);

        builder.pop();
    }
}