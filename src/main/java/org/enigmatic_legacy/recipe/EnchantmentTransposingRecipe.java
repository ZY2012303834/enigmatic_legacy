package org.enigmatic_legacy.recipe;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * 附魔转移特殊合成配方。
 * 求知之书：
 * 任意附魔物品 / 附魔书 + 求知之书
 * -> 输出带有全部附魔的附魔书
 * -> 原物品保留，并移除全部已转移附魔
 * 噬咒之书：
 * 任意除附魔书以外的附魔物品 + 噬咒之书
 * -> 输出带有全部诅咒附魔的附魔书
 * -> 原物品保留，并只移除诅咒附魔
 */
public class EnchantmentTransposingRecipe extends CustomRecipe {
    public EnchantmentTransposingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        RecipeInputs inputs = findInputs(input);

        if (!inputs.valid()) {
            return false;
        }

        boolean cursesOnly = isCurseTransposer(inputs.transposer());

        // 噬咒之书不能和附魔书合成
        if (cursesOnly && inputs.enchantedItem().is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        return !collectEnchantments(inputs.enchantedItem(), cursesOnly).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull CraftingInput input,
            @NotNull HolderLookup.Provider registries
    ) {
        RecipeInputs inputs = findInputs(input);

        if (!inputs.valid()) {
            return ItemStack.EMPTY;
        }

        boolean cursesOnly = isCurseTransposer(inputs.transposer());

        if (cursesOnly && inputs.enchantedItem().is(Items.ENCHANTED_BOOK)) {
            return ItemStack.EMPTY;
        }

        ItemEnchantments enchantments = collectEnchantments(inputs.enchantedItem(), cursesOnly);

        if (enchantments.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.STORED_ENCHANTMENTS, enchantments);
        return book;
    }

    /**
     * 修复原物品被吞掉的问题。
     * 合成时：
     * - 求知之书被消耗，变成输出附魔书；
     * - 噬咒之书被消耗，变成输出附魔书；
     * - 原附魔物品作为剩余物品返还，但移除对应附魔。
     */
    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        RecipeInputs inputs = findInputs(input);

        if (!inputs.valid()) {
            return remainingItems;
        }

        boolean cursesOnly = isCurseTransposer(inputs.transposer());

        if (cursesOnly && inputs.enchantedItem().is(Items.ENCHANTED_BOOK)) {
            return remainingItems;
        }

        ItemStack strippedItem = removeTransferredEnchantments(inputs.enchantedItem(), cursesOnly);

        if (!strippedItem.isEmpty()) {
            remainingItems.set(inputs.enchantedItemSlot(), strippedItem);
        }

        return remainingItems;
    }

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

    private static RecipeInputs findInputs(CraftingInput input) {
        int transposerSlot = -1;
        int enchantedItemSlot = -1;

        ItemStack transposer = ItemStack.EMPTY;
        ItemStack enchantedItem = ItemStack.EMPTY;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (isTransposer(stack)) {
                if (transposerSlot != -1) {
                    return RecipeInputs.INVALID;
                }

                transposerSlot = slot;
                transposer = stack;
                continue;
            }

            if (enchantedItemSlot != -1) {
                return RecipeInputs.INVALID;
            }

            enchantedItemSlot = slot;
            enchantedItem = stack;
        }

        if (transposerSlot == -1 || enchantedItemSlot == -1) {
            return RecipeInputs.INVALID;
        }

        return new RecipeInputs(transposerSlot, transposer, enchantedItemSlot, enchantedItem);
    }

    private static boolean isTransposer(ItemStack stack) {
        return stack.is(ModItems.ENCHANTMENT_TRANSPOSER.get())
                || stack.is(ModItems.CURSE_TRANSPOSER.get());
    }

    private static boolean isCurseTransposer(ItemStack stack) {
        return stack.is(ModItems.CURSE_TRANSPOSER.get());
    }

    private static ItemEnchantments collectEnchantments(ItemStack stack, boolean cursesOnly) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        copyMatchingEnchantments(
                stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY),
                mutable,
                cursesOnly
        );

        copyMatchingEnchantments(
                stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY),
                mutable,
                cursesOnly
        );

        return mutable.toImmutable();
    }

    private static void copyMatchingEnchantments(
            ItemEnchantments source,
            ItemEnchantments.Mutable target,
            boolean cursesOnly
    ) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : source.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();

            if (!shouldTransfer(enchantment, cursesOnly)) {
                continue;
            }

            target.set(enchantment, entry.getIntValue());
        }
    }

    private static ItemStack removeTransferredEnchantments(ItemStack stack, boolean cursesOnly) {
        ItemStack result = stack.copyWithCount(1);

        ItemEnchantments remainingEnchantments = removeMatchingEnchantments(
                result.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY),
                cursesOnly
        );

        if (remainingEnchantments.isEmpty()) {
            result.remove(DataComponents.ENCHANTMENTS);
        } else {
            result.set(DataComponents.ENCHANTMENTS, remainingEnchantments);
        }

        ItemEnchantments remainingStoredEnchantments = removeMatchingEnchantments(
                result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY),
                cursesOnly
        );

        if (remainingStoredEnchantments.isEmpty()) {
            result.remove(DataComponents.STORED_ENCHANTMENTS);
        } else {
            result.set(DataComponents.STORED_ENCHANTMENTS, remainingStoredEnchantments);
        }

        return result;
    }

    private static ItemEnchantments removeMatchingEnchantments(ItemEnchantments source, boolean cursesOnly) {
        if (source.isEmpty()) {
            return ItemEnchantments.EMPTY;
        }

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(source);
        mutable.removeIf(enchantment -> shouldTransfer(enchantment, cursesOnly));
        return mutable.toImmutable();
    }

    private static boolean shouldTransfer(Holder<Enchantment> enchantment, boolean cursesOnly) {
        return !cursesOnly || enchantment.is(EnchantmentTags.CURSE);
    }

    private record RecipeInputs(
            int transposerSlot,
            ItemStack transposer,
            int enchantedItemSlot,
            ItemStack enchantedItem
    ) {
        private static final RecipeInputs INVALID = new RecipeInputs(
                -1,
                ItemStack.EMPTY,
                -1,
                ItemStack.EMPTY
        );

        private boolean valid() {
            return transposerSlot >= 0
                    && enchantedItemSlot >= 0
                    && !transposer.isEmpty()
                    && !enchantedItem.isEmpty();
        }
    }
}