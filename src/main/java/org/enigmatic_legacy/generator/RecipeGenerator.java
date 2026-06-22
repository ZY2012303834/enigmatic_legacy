package org.enigmatic_legacy.generator;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModPotions;
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

        // 大地之心
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EARTH_HEART.get())
                .requires(ModItems.EARTH_HEART_FRAGMENT.get(), 8)
                .unlockedBy("has_earth_heart_fragment", has(ModItems.EARTH_HEART_FRAGMENT.get()))
                .save(output);

        // 扭曲之心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TWISTED_HEART.get())
                .pattern(" T ")
                .pattern("BXB")
                .pattern("RER")
                .define('T', Items.GHAST_TEAR)
                .define('B', Items.BLAZE_POWDER)
                .define('X', ModItems.EARTH_HEART.get())
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_EYE)
                .unlockedBy("has_earth_heart", has(ModItems.EARTH_HEART.get()))
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

        // 铁指环
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_RING.get())
                .pattern("NIN")
                .pattern("I I")
                .pattern("NIN")
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        // 精美戒指
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EXQUISITE_RING.get())
                .pattern("NIN")
                .pattern("IXI")
                .pattern("NIN")
                .define('I', Items.GOLD_INGOT)
                .define('N', Items.GOLD_NUGGET)
                .define('X', ModItems.IRON_RING.get())
                .unlockedBy("has_iron_ring", has(ModItems.IRON_RING.get()))
                .save(output);

        // 极恶锭
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EVIL_INGOT.get())
                .pattern("TET")
                .pattern("EXE")
                .pattern("TET")
                .define('T', Items.GHAST_TEAR)
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('X', Items.NETHERITE_INGOT)
                .unlockedBy("has_evil_essence", has(ModItems.EVIL_ESSENCE.get()))
                .save(output);

        // 扭曲魔镜
        ItemStack recallPotion = PotionContents.createItemStack(
                Items.POTION,
                ModPotions.RECALL
        );
        recallPotion.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        Ingredient recallPotionIngredient = DataComponentIngredient.of(
                false,
                recallPotion
        );

        // 扭曲之心
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TWISTED_MIRROR.get())
                .pattern("IGI")
                .pattern("PXP")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('P', recallPotionIngredient)
                .define('X', ModItems.TWISTED_HEART.get())
                .unlockedBy("has_twisted_heart", has(ModItems.TWISTED_HEART.get()))
                .save(output);

        // 末影之戒
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_RING.get())
                .pattern(" C ")
                .pattern("IXI")
                .pattern("NPN")
                .define('C', Items.ENDER_CHEST)
                .define('I', Items.GOLD_INGOT)
                .define('X', ModItems.IRON_RING.get())
                .define('N', Items.GOLD_NUGGET)
                .define('P', Items.ENDER_PEARL)
                .unlockedBy("has_ender_chest", has(Items.ENDER_CHEST))
                .save(output);

        // 磁力之戒配方。
        // 对齐原项目：钻石 + 铁锭 + 铁指环 + 金锭 + 红石。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGNET_RING.get())
                .pattern(" D ")
                .pattern("IXG")
                .pattern(" R ")
                .define('D', Items.DIAMOND)
                .define('I', Items.IRON_INGOT)
                .define('X', ModItems.IRON_RING.get())
                .define('G', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_iron_ring", has(ModItems.IRON_RING.get()))
                .save(output);

        // 转位之戒配方。
        // 原项目 ID 是 super_magnet_ring，显示名是 Dislocation Ring；
        // 本项目使用更直观的 dislocation_ring 作为注册 ID。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DISLOCATION_RING.get())
                .pattern("LEL")
                .pattern("GXG")
                .pattern("LGL")
                .define('L', Items.LAPIS_LAZULI)
                .define('E', Items.ENDER_EYE)
                .define('G', Items.GOLD_INGOT)
                .define('X', ModItems.MAGNET_RING.get())
                .unlockedBy("has_magnet_ring", has(ModItems.MAGNET_RING.get()))
                .save(output);

        // 怪物猎人勋章。
        // 对齐原项目 monster_charm 配方。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MONSTER_CHARM.get())
                .pattern(" S ")
                .pattern("PXP")
                .pattern("EIE")
                .define('S', Items.SOUL_LANTERN)
                .define('P', Items.BLAZE_POWDER)
                .define('X', Items.SKELETON_SKULL)
                .define('E', Items.EXPERIENCE_BOTTLE)
                .define('I', Items.NETHERITE_INGOT)
                .unlockedBy("has_skeleton_skull", has(Items.SKELETON_SKULL))
                .save(output);

        // 猎宝者护符。
        // 原项目后续版本配方要求大地之心；这里先按当前项目材料体系接入。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TREASURE_HUNTER_CHARM.get())
                .pattern(" G ")
                .pattern("EHE")
                .pattern(" D ")
                .define('G', Items.GOLDEN_PICKAXE)
                .define('E', Items.EMERALD)
                .define('H', ModItems.EARTH_HEART.get())
                .define('D', Items.DIAMOND)
                .unlockedBy("has_earth_heart", has(ModItems.EARTH_HEART.get()))
                .save(output);

        // 血战沙场之证 / Emblem of Bloodstained Valor。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOODSTAINED_VALOR_EMBLEM.get())
                .pattern("MSM")
                .pattern("PXP")
                .pattern("TIT")
                .define('M', Items.CRIMSON_FUNGUS)
                .define('S', Items.GOLDEN_SWORD)
                .define('P', Items.BLAZE_POWDER)
                .define('T', Items.GHAST_TEAR)
                .define('I', Items.NETHERITE_INGOT)
                .define('X', ModItems.TWISTED_HEART.get())
                .unlockedBy("has_twisted_heart", has(ModItems.TWISTED_HEART.get()))
                .save(output);

        // 超级海绵 / Extrapolated Megasponge。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEGA_SPONGE.get())
                .pattern("SES")
                .pattern("XNX")
                .pattern("STS")
                .define('S', Items.SPONGE)
                .define('E', Items.ENDER_EYE)
                .define('X', Items.HEART_OF_THE_SEA)
                .define('N', Items.NAUTILUS_SHELL)
                .define('T', Items.GHAST_TEAR)
                .unlockedBy("has_sponge", has(Items.SPONGE))
                .save(output);

        // 附魔师的珍珠 / Enchanter's Pearl。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENCHANTER_PEARL.get())
                .pattern(" G ")
                .pattern("EXE")
                .pattern("POP")
                .define('G', Items.EMERALD)
                .define('X', Items.ENDER_PEARL)
                .define('P', Items.BLAZE_POWDER)
                .define('O', Items.CRYING_OBSIDIAN)
                .define('E', ModItems.EVIL_ESSENCE.get())
                .unlockedBy("has_evil_essence", has(ModItems.EVIL_ESSENCE.get()))
                .save(output);
    }
}
