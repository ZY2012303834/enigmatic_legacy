package org.enigmatic_legacy.recipe;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
 * 支持两种转移书：
 * 1. 求知之书 / Tome of Hungering Knowledge
 *    任意附魔物品或附魔书 + 求知之书
 *    -> 转移全部附魔到附魔书。
 * 2. 噬咒之书 / Tome of Devoured Malignancy
 *    任意除附魔书以外的附魔物品 + 噬咒之书
 *    -> 只转移全部诅咒附魔到附魔书。
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
        return hasTransposableEnchantments(inputs.enchantedItem(), cursesOnly);
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
        ItemEnchantments enchantments = collectEnchantments(inputs.enchantedItem(), cursesOnly);

        if (enchantments.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.STORED_ENCHANTMENTS, enchantments);
        return book;
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
        ItemStack transposer = ItemStack.EMPTY;
        ItemStack enchantedItem = ItemStack.EMPTY;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (isTransposer(stack)) {
                if (!transposer.isEmpty()) {
                    return RecipeInputs.INVALID;
                }

                transposer = stack;
                continue;
            }

            if (!enchantedItem.isEmpty()) {
                return RecipeInputs.INVALID;
            }

            enchantedItem = stack;
        }

        if (transposer.isEmpty() || enchantedItem.isEmpty()) {
            return RecipeInputs.INVALID;
        }

        return new RecipeInputs(transposer, enchantedItem);
    }

    private static boolean isTransposer(ItemStack stack) {
        return stack.is(ModItems.ENCHANTMENT_TRANSPOSER.get())
                || stack.is(ModItems.CURSE_TRANSPOSER.get());
    }

    private static boolean isCurseTransposer(ItemStack stack) {
        return stack.is(ModItems.CURSE_TRANSPOSER.get());
    }

    private static boolean hasTransposableEnchantments(ItemStack stack, boolean cursesOnly) {
        if (cursesOnly && stack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        return !collectEnchantments(stack, cursesOnly).isEmpty();
    }

    private static ItemEnchantments collectEnchantments(ItemStack stack, boolean cursesOnly) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        copyEnchantments(
                stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY),
                mutable,
                cursesOnly
        );

        if (!cursesOnly) {
            copyEnchantments(
                    stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY),
                    mutable,
                    false
            );
        }

        return mutable.toImmutable();
    }

    private static void copyEnchantments(
            ItemEnchantments source,
            ItemEnchantments.Mutable target,
            boolean cursesOnly
    ) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : source.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();

            if (cursesOnly && !enchantment.is(EnchantmentTags.CURSE)) {
                continue;
            }

            target.set(enchantment, entry.getIntValue());
        }
    }

    private record RecipeInputs(ItemStack transposer, ItemStack enchantedItem) {
        private static final RecipeInputs INVALID = new RecipeInputs(ItemStack.EMPTY, ItemStack.EMPTY);

        private boolean valid() {
            return !transposer.isEmpty() && !enchantedItem.isEmpty();
        }
    }
}