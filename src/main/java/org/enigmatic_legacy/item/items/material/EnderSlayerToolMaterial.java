package org.enigmatic_legacy.item.items.material;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * 末影之屠工具材料。
 * 按原项目 EnigmaticMaterials.ENDER_SLAYER 复刻：
 * - 耐久：2000
 * - 挖掘速度：6.0
 * - 攻击伤害基础加成：3.0
 * - 附魔能力：16
 * - 修复材料：黑曜石
 * 注意：
 * 末影之屠本质是剑，挖掘等级对它影响很小。
 */
public enum EnderSlayerToolMaterial implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 2000;
    }

    @Override
    public float getSpeed() {
        return 6.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 3.0F;
    }

    @Override
    public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
    }

    @Override
    public int getEnchantmentValue() {
        return 16;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(Items.OBSIDIAN);
    }
}