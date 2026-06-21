package org.enigmatic_legacy.generator;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeServer()).addProvider(output ->
                new RecipeGenerator(output, event.getLookupProvider()));
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        // 袋装星尘
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ASTRAL_DUST_SACK.get())
                .requires(ModItems.ASTRAL_DUST.get(), 9)
                .unlockedBy("has_astral_dust", has(ModItems.ASTRAL_DUST.get()))
                .save(output);

        // 末影棒
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_ROD.get(), 2)
                .pattern("  b")
                .pattern("aXa")
                .pattern("b  ")
                .define('X', Items.ENDER_PEARL)
                .define('b', Items.BLAZE_ROD)
                .define('a', ModItems.ASTRAL_DUST.get())
                .unlockedBy("has_astral_dust", has(ModItems.ASTRAL_DUST.get()))
                .save(output);

        // 空卷轴
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.THICC_SCROLL.get())
                .pattern("nX ")
                .pattern(" X ")
                .pattern(" Xn")
                .define('X', Items.PAPER)
                .define('n', Items.STICK)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);

        // 以太块
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ETHERIUM_BLOCK.get())
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ModItems.ETHERIUM_INGOT.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ETHERIUM_INGOT.get(), 9)
                .requires(ModBlocks.ETHERIUM_BLOCK.get())
                .group("enigmatic_legacy_etherium_ingot")
                .unlockedBy("has_etherium_block", has(ModBlocks.ETHERIUM_BLOCK.get()))
                .save(output, ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "etherium_block_uncrafting"));

        // 寰宇之心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COSMIC_HEART.get())
                .pattern("DSD")
                .pattern("PXP")
                .pattern("DED")
                .define('X', Items.HEART_OF_THE_SEA)
                .define('D', ModItems.ASTRAL_DUST.get())
                .define('E', Items.ENDER_EYE)
                .define('P', Items.BLAZE_POWDER)
                .define('S', Items.NETHER_STAR)
                .unlockedBy("has_heart_of_the_sea", has(Items.HEART_OF_THE_SEA))
                .save(output);

        // 大灯笼
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BIG_LAMP.get(), 2)
                .pattern("ici")
                .pattern("gXg")
                .pattern("igi")
                .define('X', Items.GLOWSTONE)
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GOLD_NUGGET)
                .define('c', Items.CHAIN)
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE))
                .save(output, ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "lamps/big_lamp"));

        // 菌光体灯笼
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BIG_SHROOMLAMP.get(), 2)
                .pattern("ici")
                .pattern("gXg")
                .pattern("igi")
                .define('X', Items.SHROOMLIGHT)
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GOLD_NUGGET)
                .define('c', Items.CHAIN)
                .unlockedBy("has_shroomlight", has(Items.SHROOMLIGHT))
                .save(output, ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "lamps/big_shroomlamp"));
    }
}
