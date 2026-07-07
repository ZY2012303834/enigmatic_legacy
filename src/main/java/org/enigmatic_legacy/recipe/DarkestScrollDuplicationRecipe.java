package org.enigmatic_legacy.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * 至暗卷轴增殖配方。
 * <p>
 * 3x3 摆放：
 * 黑色染料 / 凋零玫瑰 / 黑色染料
 * 空卷轴 / 至暗卷轴 / 空卷轴
 * 黑色染料 / 凋零玫瑰 / 黑色染料
 * <p>
 * 至暗卷轴最大堆叠为 1，普通 JSON 配方不能输出 2 个，因此这里使用特殊配方。
 */
public class DarkestScrollDuplicationRecipe extends CustomRecipe {
    public DarkestScrollDuplicationRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return matchesPattern(input);
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull CraftingInput input,
            @NotNull HolderLookup.Provider registries
    ) {
        return matchesPattern(input)
                ? new ItemStack(ModItems.DARKEST_SCROLL.get(), 2)
                : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return new ItemStack(ModItems.DARKEST_SCROLL.get(), 2);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        ingredients.add(Ingredient.of(Items.BLACK_DYE));
        ingredients.add(Ingredient.of(Items.WITHER_ROSE));
        ingredients.add(Ingredient.of(Items.BLACK_DYE));
        ingredients.add(Ingredient.of(ModItems.THICC_SCROLL.get()));
        ingredients.add(Ingredient.of(ModItems.DARKEST_SCROLL.get()));
        ingredients.add(Ingredient.of(ModItems.THICC_SCROLL.get()));
        ingredients.add(Ingredient.of(Items.BLACK_DYE));
        ingredients.add(Ingredient.of(Items.WITHER_ROSE));
        ingredients.add(Ingredient.of(Items.BLACK_DYE));

        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.DARKEST_SCROLL_DUPLICATION.get();
    }

    private static boolean matchesPattern(CraftingInput input) {
        if (input.width() != 3 || input.height() != 3 || input.size() != 9) {
            return false;
        }

        return input.getItem(0).is(Items.BLACK_DYE)
                && input.getItem(1).is(Items.WITHER_ROSE)
                && input.getItem(2).is(Items.BLACK_DYE)
                && input.getItem(3).is(ModItems.THICC_SCROLL.get())
                && input.getItem(4).is(ModItems.DARKEST_SCROLL.get())
                && input.getItem(5).is(ModItems.THICC_SCROLL.get())
                && input.getItem(6).is(Items.BLACK_DYE)
                && input.getItem(7).is(Items.WITHER_ROSE)
                && input.getItem(8).is(Items.BLACK_DYE);
    }
}
