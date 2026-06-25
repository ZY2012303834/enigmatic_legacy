package org.enigmatic_legacy.recipe;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * 求知之书特殊合成配方。
 * 合成方式：
 * 任意带附魔的物品 + 求知之书
 * 输出：
 * 带有原物品全部附魔的附魔书。
 * 注意：
 * 这是特殊合成配方，不是普通 shaped/shapeless recipe。
 * 因为输出附魔书需要动态读取输入物品上的附魔数据。
 */
public class EnchantmentTransposingRecipe extends CustomRecipe {
    public EnchantmentTransposingRecipe(CraftingBookCategory category) {
        super(category);
    }

    /**
     * 判断当前工作台输入是否符合配方。
     * 条件：
     * 1. 必须恰好有 1 本求知之书；
     * 2. 必须恰好有 1 个带附魔的物品；
     * 3. 不能有其它额外物品。
     */
    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        int tomeCount = 0;
        int enchantedItemCount = 0;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.ENCHANTMENT_TRANSPOSER.get())) {
                tomeCount++;
                continue;
            }

            if (hasRealEnchantments(stack)) {
                enchantedItemCount++;
                continue;
            }

            return false;
        }

        return tomeCount == 1 && enchantedItemCount == 1;
    }

    /**
     * 生成合成结果。
     * 把输入物品上的 ENCHANTMENTS / STORED_ENCHANTMENTS
     * 写入输出附魔书的 STORED_ENCHANTMENTS。
     */
    @Override
    public @NotNull ItemStack assemble(
            @NotNull CraftingInput input,
            @NotNull HolderLookup.Provider registries
    ) {
        ItemStack enchantedItem = ItemStack.EMPTY;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.ENCHANTMENT_TRANSPOSER.get())) {
                continue;
            }

            if (hasRealEnchantments(stack)) {
                enchantedItem = stack;
                break;
            }
        }

        if (enchantedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemEnchantments enchantments = collectEnchantments(enchantedItem);

        if (enchantments.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.STORED_ENCHANTMENTS, enchantments);

        return book;
    }

    /**
     * 合成表显示用结果。
     * 实际结果会在 assemble(...) 中根据输入动态生成。
     */
    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return new ItemStack(Items.ENCHANTED_BOOK);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ENCHANTMENT_TRANSPOSING.get();
    }

    /**
     * 判断物品是否真的携带附魔。
     * 普通工具、装备：
     * DataComponents.ENCHANTMENTS
     * 附魔书：
     * DataComponents.STORED_ENCHANTMENTS
     */
    private static boolean hasRealEnchantments(ItemStack stack) {
        return !stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()
                || !stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
    }

    /**
     * 收集输入物品上的全部附魔。
     * 同时兼容：
     * 1. 普通物品上的 ENCHANTMENTS；
     * 2. 附魔书上的 STORED_ENCHANTMENTS。
     */
    private static ItemEnchantments collectEnchantments(ItemStack stack) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        copyEnchantments(
                stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY),
                mutable
        );

        copyEnchantments(
                stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY),
                mutable
        );

        return mutable.toImmutable();
    }

    /**
     * 把一组附魔复制到 Mutable ItemEnchantments 中。
     */
    private static void copyEnchantments(ItemEnchantments source, ItemEnchantments.Mutable target) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : source.entrySet()) {
            target.set(entry.getKey(), entry.getIntValue());
        }
    }
}