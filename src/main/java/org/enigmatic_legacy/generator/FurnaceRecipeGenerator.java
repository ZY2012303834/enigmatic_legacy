package org.enigmatic_legacy.generator;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class FurnaceRecipeGenerator implements DataProvider {

    private static final AdvancementHolder ROOT_RECIPE_ADVANCEMENT = new AdvancementHolder(
            RecipeBuilder.ROOT_RECIPE_ADVANCEMENT,
            new Advancement(
                    Optional.empty(),
                    Optional.empty(),
                    AdvancementRewards.EMPTY,
                    Map.of(),
                    AdvancementRequirements.EMPTY,
                    false));

    private final PackOutput.PathProvider recipePathProvider;
    private final PackOutput.PathProvider advancementPathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FurnaceRecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        this.advancementPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
        this.registries = registries;
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeServer()).addProvider(output ->
                new FurnaceRecipeGenerator(output, event.getLookupProvider()));
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return registries.thenCompose(registries -> run(output, registries));
    }

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
        Set<ResourceLocation> recipes = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        buildRecipes(new RecipeOutput() {
            @Override
            public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                accept(id, recipe, advancement, new ICondition[0]);
            }

            @Override
            public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                if (conditions.length > 0) {
                    throw new UnsupportedOperationException("Conditional furnace recipes are not supported yet: " + id);
                }

                if (!recipes.add(id)) {
                    throw new IllegalStateException("Duplicate furnace recipe " + id);
                }

                futures.add(DataProvider.saveStable(output, registries, Recipe.CODEC, recipe, recipePathProvider.json(id)));
                if (advancement != null) {
                    futures.add(DataProvider.saveStable(
                            output,
                            registries,
                            Advancement.CODEC,
                            advancement.value(),
                            advancementPathProvider.json(advancement.id())));
                }
            }

            @Override
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(ROOT_RECIPE_ADVANCEMENT);
            }
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    protected void buildRecipes(RecipeOutput output) {
        smelting(output, ModItems.ETHERIUM_ORE.get(), ModItems.ETHERIUM_INGOT.get(), 8.0F, 400);
        blasting(output, ModItems.ETHERIUM_ORE.get(), ModItems.ETHERIUM_INGOT.get(), 8.0F, 200);
    }

    protected void smelting(RecipeOutput output, ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, cookingTime)
                .unlockedBy("has_" + getItemName(ingredient), has(ingredient))
                .save(output, EnigmaticLegacy.MODID + ":" + getItemName(result) + "_from_smelting");
    }

    protected void blasting(RecipeOutput output, ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, cookingTime)
                .unlockedBy("has_" + getItemName(ingredient), has(ingredient))
                .save(output, EnigmaticLegacy.MODID + ":" + getItemName(result) + "_from_blasting");
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    private static String getItemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    @Override
    public @NotNull String getName() {
        return "Furnace Recipes: " + EnigmaticLegacy.MODID;
    }
}
