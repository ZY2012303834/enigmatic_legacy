package org.enigmatic_legacy.generator;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.recipe.EnchantmentTransposingRecipe;
import org.enigmatic_legacy.recipe.MendingMixtureRepairRecipe;
import org.jetbrains.annotations.NotNull;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeServer()).addProvider(output ->
                new RecipeGenerator(output, event.getLookupProvider()));
    }

    private static final TagKey<Item> ENIGMATIC_AMULETS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "enigmatic_amulets")
    );

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

        // 永恒智慧卷轴 / Scroll of Ageless Wisdom。
        // 对齐原项目 xp_scroll 配方。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.XP_SCROLL.get())
                .pattern("BEB")
                .pattern("IXF")
                .pattern("BGB")
                .define('B', Items.EXPERIENCE_BOTTLE)
                .define('E', Items.ENDER_EYE)
                .define('I', Items.INK_SAC)
                .define('X', ModItems.THICC_SCROLL.get())
                .define('F', Items.FEATHER)
                .define('G', Items.EMERALD)
                .unlockedBy("has_thicc_scroll", has(ModItems.THICC_SCROLL.get()))
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

        // 纯净之心 / Pure Heart
// 复刻 Enigmatic Addons 配方：
//  L
// QXQ
// RER
//
// L = 恶魂之泪
// Q = 灵液滴
// X = 大地之心
// R = 荧石粉
// E = 末影之眼
//
// 原拓展项目使用 cursed shaped recipe。
// 当前项目暂未接入 cursed recipe serializer，
// 因此这里先使用普通 shaped recipe 生成 JSON。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURE_HEART.get())
                .pattern(" L ")
                .pattern("QXQ")
                .pattern("RER")
                .define('L', Items.GHAST_TEAR)
                .define('Q', ModItems.ICHOR_DROPLET.get())
                .define('X', ModItems.EARTH_HEART.get())
                .define('R', Items.GLOWSTONE_DUST)
                .define('E', Items.ENDER_EYE)
                .unlockedBy("has_ichor_droplet", has(ModItems.ICHOR_DROPLET.get()))
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

        // Promise of the Earth.
        // Original Enigmatic Addons pattern:
        // mem / tXa / nbn
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EARTH_PROMISE.get())
                .pattern("mem")
                .pattern("tXa")
                .pattern("nbn")
                .define('m', Items.GOLDEN_APPLE)
                .define('e', ModItems.COSMIC_HEART.get())
                .define('t', ModItems.TWISTED_HEART.get())
                .define('X', ModItems.EXQUISITE_RING.get())
                .define('a', ModItems.PURE_HEART.get())
                .define('n', Items.ENCHANTED_GOLDEN_APPLE)
                .define('b', ModItems.TREASURE_HUNTER_CHARM.get())
                .unlockedBy("has_pure_heart", has(ModItems.PURE_HEART.get()))
                .unlockedBy("has_treasure_hunter_charm", has(ModItems.TREASURE_HUNTER_CHARM.get()))
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

        // 扭曲之心
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TWISTED_MIRROR.get())
                .pattern("IGI")
                .pattern("PXP")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('P', ModItems.RECALL_POTION.get())
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

        // 启示之证 / The Acknowledgment。
        // Plus 版配方：书 + 灯笼。
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.THE_ACKNOWLEDGMENT.get())
                .requires(Items.BOOK)
                .requires(Items.LANTERN)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);

        // 兽友指南 / Guide to Animal Companionship。
        // 对齐原项目 animal_guidebook 配方。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANIMAL_GUIDEBOOK.get())
                .pattern(" n ")
                .pattern("fXf")
                .pattern(" a ")
                .define('n', Items.GOLD_NUGGET)
                .define('f', Items.DANDELION)
                .define('X', Items.BOOK)
                .define('a', Items.APPLE)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);

        // 野猎指南 / Guide to Feral Hunt。
        // 对齐原项目 hunter_guidebook 配方。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HUNTER_GUIDEBOOK.get())
                .pattern("bp ")
                .pattern("nXn")
                .pattern(" lb")
                .define('b', Items.BONE)
                .define('p', Items.ENDER_PEARL)
                .define('n', Items.GOLD_NUGGET)
                .define('X', Items.BOOK)
                .define('l', Items.LEATHER)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);

        // 非欧立方 / Non-Euclidean Cube。
        // 按原版材料逻辑：魔像之心、寰宇之心 x2、烈焰核心、天使之祝、黑曜石、星云之眼、海洋意志、虚空珍珠。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.THE_CUBE.get())
                .pattern("GCG")
                .pattern("AOB")
                .pattern("EPV")
                .define('G', ModItems.GOLEM_HEART.get())
                .define('C', ModItems.COSMIC_HEART.get())
                .define('A', ModItems.ANGEL_BLESSING.get())
                .define('O', Items.OBSIDIAN)
                .define('B', ModItems.BLAZING_CORE.get())
                .define('E', ModItems.EYE_OF_NEBULA.get())
                .define('P', ModItems.OCEAN_STONE.get())
                .define('V', ModItems.VOID_PEARL.get())
                .unlockedBy("has_eye_of_nebula", has(ModItems.EYE_OF_NEBULA.get()))
                .save(output);

        // 千咒卷轴 / Scroll of a Thousand Curses。
        // 原项目配方：幻翼膜 * 2、扭曲之心、墨囊、至暗卷轴、羽毛、红石粉 * 2、附魔书。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CURSED_SCROLL.get())
                .pattern("PTP")
                .pattern("ISF")
                .pattern("RBR")
                .define('P', Items.PHANTOM_MEMBRANE)
                .define('T', ModItems.TWISTED_HEART.get())
                .define('I', Items.INK_SAC)
                .define('S', ModItems.DARKEST_SCROLL.get())
                .define('F', Items.FEATHER)
                .define('R', Items.REDSTONE)
                .define('B', Items.ENCHANTED_BOOK)
                .unlockedBy("has_darkest_scroll", has(ModItems.DARKEST_SCROLL.get()))
                .save(output);

        // 创造者的恩赐 / Grace of the Creator。
        // 原项目配方：以太锭 * 2、星尘 * 3、天堂之礼、龙息 * 2、鞘翅。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FABULOUS_SCROLL.get())
                .pattern("EAE")
                .pattern("AHA")
                .pattern("DBD")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('A', ModItems.ASTRAL_DUST.get())
                .define('H', ModItems.HEAVEN_SCROLL.get())
                .define('D', Items.DRAGON_BREATH)
                .define('B', Items.ELYTRA)
                .unlockedBy("has_heaven_scroll", has(ModItems.HEAVEN_SCROLL.get()))
                .save(output);

        // 无尽贪婪契约 / Pact of Infinite Avarice。
        // 原项目配方：金锭 * 4、精美戒指、墨囊、至暗卷轴、羽毛、扭曲之心。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AVARICE_SCROLL.get())
                .pattern("GRG")
                .pattern("ISF")
                .pattern("GTG")
                .define('G', Items.GOLD_INGOT)
                .define('R', ModItems.EXQUISITE_RING.get())
                .define('I', Items.INK_SAC)
                .define('S', ModItems.DARKEST_SCROLL.get())
                .define('F', Items.FEATHER)
                .define('T', ModItems.TWISTED_HEART.get())
                .unlockedBy("has_darkest_scroll", has(ModItems.DARKEST_SCROLL.get()))
                .save(output);

        // 求知之书 / Tome of Hungering Knowledge。
        // 原项目 ID：enchantment_transposer。
        // 原项目配方：海晶砂粒 * 2、金粒、青金石 * 2、书、烈焰粉 * 2、红石粉。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENCHANTMENT_TRANSPOSER.get())
                .pattern("PNP")
                .pattern("LXL")
                .pattern("BRB")
                .define('P', Items.PRISMARINE_CRYSTALS)
                .define('N', Items.GOLD_NUGGET)
                .define('L', Items.LAPIS_LAZULI)
                .define('X', Items.BOOK)
                .define('B', Items.BLAZE_POWDER)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);

        // 求知之书 / 噬咒之书附魔转移特殊配方。
        // 该配方没有固定输入形状，逻辑由 EnchantmentTransposingRecipe 动态判断：
        // 求知之书 + 任意带附魔物品 -> 带有原物品全部附魔的附魔书。
        // 噬咒之书 + 任意非附魔书的带诅咒附魔物品 -> 带有原物品全部诅咒附魔的附魔书。
        SpecialRecipeBuilder.special(EnchantmentTransposingRecipe::new)
                .save(output, ResourceLocation.fromNamespaceAndPath(
                        EnigmaticLegacy.MODID,
                        "enchantment_transposing"
                ));

        // 噬咒之书 / Tome of Devoured Malignancy。
        // 原项目 ID：curse_transposer。
        // 原项目配方：红石 * 4、幻翼膜 * 2、恶魂之泪、极恶精华、求知之书。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CURSE_TRANSPOSER.get())
                .pattern("RTR")
                .pattern("PXP")
                .pattern("RER")
                .define('R', Items.REDSTONE)
                .define('T', Items.GHAST_TEAR)
                .define('P', Items.PHANTOM_MEMBRANE)
                .define('X', ModItems.ENCHANTMENT_TRANSPOSER.get())
                .define('E', ModItems.EVIL_ESSENCE.get())
                .unlockedBy("has_enchantment_transposer", has(ModItems.ENCHANTMENT_TRANSPOSER.get()))
                .save(output);

        // 以太阔剑 / Etherium Broadsword
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_SWORD.get())
                .pattern("EDE")
                .pattern(" D ")
                .pattern(" R ")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('D', Items.DIAMOND)
                .define('R', ModItems.ENDER_ROD.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太镐 / Etherium Pickaxe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ETHERIUM_PICKAXE.get())
                .pattern("EEE")
                .pattern(" R ")
                .pattern(" R ")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('R', ModItems.ENDER_ROD.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太锹 / Etherium Shovel
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ETHERIUM_SHOVEL.get())
                .pattern(" E ")
                .pattern(" R ")
                .pattern(" R ")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('R', ModItems.ENDER_ROD.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太斧 / Etherium Waraxe
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_AXE.get())
                .pattern("E E")
                .pattern("ERE")
                .pattern(" R ")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('R', ModItems.ENDER_ROD.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);
        // 以太头盔 / Etherium Helmet
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_HELMET.get())
                .pattern("EEE")
                .pattern("E E")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太胸甲 / Etherium Chestplate
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_CHESTPLATE.get())
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太护腿 / Etherium Leggings
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_LEGGINGS.get())
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);

        // 以太靴子 / Etherium Boots
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ETHERIUM_BOOTS.get())
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .unlockedBy("has_etherium_ingot", has(ModItems.ETHERIUM_INGOT.get()))
                .save(output);


        // 行刑者之斧 / Axe of Executioner
        // 说明：
        // - 这个配方对齐原作者 forbidden_axe 的合成逻辑。
        // - 原作者配方结构：
        //   I S I
        //   P X P
        //     R
        // 材料含义：
        // - X = 钻石斧，作为行刑者之斧的基础武器。
        // - S = 凋灵骷髅头颅，对应“斩首/处刑”的主题。
        // - I = 下界合金锭，用于提升武器强度和稀有度。
        // - P = 烈焰粉，用于强化地狱/邪术风格。
        // - R = 烈焰棒，作为斧柄核心材料。
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AXE_OF_EXECUTIONER.get())
                .pattern("ISI")
                .pattern("PXP")
                .pattern(" R ")
                .define('I', Items.NETHERITE_INGOT)
                .define('S', Items.WITHER_SKELETON_SKULL)
                .define('P', Items.BLAZE_POWDER)
                .define('X', Items.DIAMOND_AXE)
                .define('R', Items.BLAZE_ROD)
                .unlockedBy("has_wither_skeleton_skull", has(Items.WITHER_SKELETON_SKULL))
                .save(output);

        // 修补混合物 / Mending Mixture
        //
        // 复刻设定：
        // - 无序合成；
        // - 使用龙息、恶魂之泪、幻翼膜和玻璃瓶制作；
        // - 后续可与任意受损可损坏物品合成，完全修复耐久。
        //
        // 数据生成后会生成：
        // data/enigmatic_legacy/recipe/mending_mixture.json
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MENDING_MIXTURE.get())
                .requires(Items.DRAGON_BREATH)
                .requires(Items.GHAST_TEAR)
                .requires(Items.PHANTOM_MEMBRANE)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_dragon_breath", has(Items.DRAGON_BREATH))
                .save(output);

        // 修补混合物特殊修复配方。
        //
        // 这个配方没有固定输入 JSON。
        // 它由 MendingMixtureRepairRecipe 动态判断：
        // - 1 个修补混合物；
        // - 1 个任意受损可损坏物品；
        // - 输出同一物品的满耐久副本；
        // - 修补混合物留下空玻璃瓶。
        //
        // 数据生成后会生成：
        // data/enigmatic_legacy/recipe/mending_mixture_repair.json
        SpecialRecipeBuilder.special(MendingMixtureRepairRecipe::new)
                .save(output, ResourceLocation.fromNamespaceAndPath(
                        EnigmaticLegacy.MODID,
                        "mending_mixture_repair"
                ));

        // 被诅咒者的寻路指针 / Wayfinder of the Damned
        //
        // 说明：
        // - 使用指南针作为“寻路”基础；
        // - 使用灵魂水晶作为定位目标核心；
        // - 使用七咒之戒相关材料强化诅咒主题。
        //
        // 数据生成后会生成：
        // data/enigmatic_legacy/recipe/wayfinder_of_the_damned.json
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WAYFINDER_OF_THE_DAMNED.get())
                .pattern(" E ")
                .pattern("CSC")
                .pattern(" R ")
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('C', ModItems.EVIL_INGOT.get())
                .define('S', ModItems.SOUL_CRYSTAL.get())
                .define('R', Items.COMPASS)
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        // 末影之屠 / The Ender Slayer
        // 邪恶精髓 / 黑曜石 / 邪恶精髓
        // 末影之眼 / 黑曜石 / 末影之眼
        // 恶魂之泪 / 木棍   / 恶魂之泪
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ENDER_SLAYER.get())
                .pattern("EOE")
                .pattern("IOI")
                .pattern("TST")
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('O', Items.OBSIDIAN)
                .define('I', Items.ENDER_EYE)
                .define('T', Items.GHAST_TEAR)
                .define('S', Items.STICK)
                .unlockedBy("has_evil_essence", has(ModItems.EVIL_ESSENCE.get()))
                .save(output);

// 烈焰之傲壁垒 / Bulwark of Blazing Pride
//
// 对齐原项目 infernal_shield 配方：
// P I P
// R S R
// O C O
//
// P = 烈焰粉
// I = 下界合金锭
// R = 烈焰棒
// S = 盾牌
// O = 黑曜石
// C = 扭曲之心
//
// 注意：当前项目该物品注册名仍是 infernal_shield，
// 所以数据生成后会输出：
// data/enigmatic_legacy/recipe/infernal_shield.json
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                .pattern("PIP")
                .pattern("RSR")
                .pattern("OCO")
                .define('P', Items.BLAZE_POWDER)
                .define('I', Items.NETHERITE_INGOT)
                .define('R', Items.BLAZE_ROD)
                .define('S', Items.SHIELD)
                .define('O', Items.OBSIDIAN)
                .define('C', ModItems.TWISTED_HEART.get())
                .unlockedBy("has_twisted_heart", has(ModItems.TWISTED_HEART.get()))
                .unlockedBy("has_shield", has(Items.SHIELD))
                .save(output);

        // 饕餮之锅 / The Voracious Pan
        // 原项目配方形状：
        // ESE
        // IHI
        // TRT
        // 原项目 S 使用 void_stone。
        // 当前项目没有 void_stone，所以这里暂时使用虚空珍珠替代。
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VORACIOUS_PAN.get())
                .pattern("ESE")
                .pattern("IHI")
                .pattern("TRT")
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('S', ModItems.VOID_PEARL.get())
                .define('I', ModItems.EVIL_INGOT.get())
                .define('H', ModItems.ABYSSAL_HEART.get())
                .define('T', Items.GHAST_TEAR)
                .define('R', ModItems.ENDER_ROD.get())
                .unlockedBy("has_abyssal_heart", has(ModItems.ABYSSAL_HEART.get()))
                .save(output);

        // 倒转之启 / The Twist
        // 原项目配方：
        // E R E
        // I X I
        // E C E
        // E = 邪恶精髓
        // R = 红石粉
        // I = 下界合金锭
        // X = 启示之证
        // C = 扭曲之心
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.THE_TWIST.get())
                .pattern("ERE")
                .pattern("IXI")
                .pattern("ECE")
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('R', Items.REDSTONE)
                .define('I', Items.NETHERITE_INGOT)
                .define('X', ModItems.THE_ACKNOWLEDGMENT.get())
                .define('C', ModItems.TWISTED_HEART.get())
                .unlockedBy("has_evil_essence", has(ModItems.EVIL_ESSENCE.get()))
                .save(output);

        // 无止之言 / The Infinitum
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.THE_INFINITUM.get())
                .pattern("HPH")
                .pattern("EXE")
                .pattern("NAN")
                .define('H', ModItems.COSMIC_HEART.get())
                .define('P', ModItems.ENCHANTER_PEARL.get())
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('X', ModItems.THE_TWIST.get())
                .define('N', Items.NETHERITE_INGOT)
                .define('A', ModItems.ABYSSAL_HEART.get())
                .unlockedBy("has_the_twist", has(ModItems.THE_TWIST.get()))
                .save(output);

        // 飞升护符 / Amulet of Ascension
        // 原项目配方：
        // A S A
        // E M E
        // D C D
        // A = 紫水晶碎片
        // S = 星尘
        // E = 以太锭
        // M = 任意颜色神秘护身符
        // D = 龙息
        // C = 寰宇之心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ASCENSION_AMULET.get())
                .pattern("ASA")
                .pattern("EME")
                .pattern("DCD")
                .define('A', Items.AMETHYST_SHARD)
                .define('S', ModItems.ASTRAL_DUST.get())
                .define('E', ModItems.ETHERIUM_INGOT.get())
                .define('M', ENIGMATIC_AMULETS)
                .define('D', Items.DRAGON_BREATH)
                .define('C', ModItems.COSMIC_HEART.get())
                .unlockedBy("has_cosmic_heart", has(ModItems.COSMIC_HEART.get()))
                .save(output);

        // 轻蔑之约 / The Testament of Contempt
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ELDRITCH_AMULET.get())
                .pattern("EAE")
                .pattern("NUN")
                .pattern("TST")
                .define('E', ModItems.EVIL_ESSENCE.get())
                .define('A', ModItems.ABYSSAL_HEART.get())
                .define('N', Items.NETHERITE_INGOT)
                .define('U', ModItems.ASCENSION_AMULET.get())
                .define('T', ModItems.TWISTED_HEART.get())
                .define('S', Items.NETHER_STAR)
                .unlockedBy("has_ascension_amulet", has(ModItems.ASCENSION_AMULET.get()))
                .save(output);

        /*
         * 魔法石英戒指。
         *
         * 参考 Enigmatic Addons：
         * - 使用地狱石英；
         * - 使用青金石作为附魔材料；
         * - 基底使用铁指环。
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGIC_QUARTZ_RING.get())
                .pattern("QLQ")
                .pattern("LRL")
                .pattern("QLQ")
                .define('Q', Items.QUARTZ)
                .define('L', Items.LAPIS_LAZULI)
                .define('R', ModItems.IRON_RING.get())
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .unlockedBy("has_iron_ring", has(ModItems.IRON_RING.get()))
                .save(output);

        // 阳灼护符 / Charm of Scorched Sun
// 复刻 Enigmatic Addons 配方：
// n n
// aXa
// qbq
//
// X = 烈焰核心
// n = 烈焰粉
// a = 远古残骸
// q = 玄武岩
// b = 纯净之心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SCORCHED_CHARM.get())
                .pattern("N N")
                .pattern("AXA")
                .pattern("QBQ")
                .define('X', ModItems.BLAZING_CORE.get())
                .define('N', Items.BLAZE_POWDER)
                .define('A', Items.NETHERITE_SCRAP)
                .define('Q', Items.BASALT)
                .define('B', ModItems.PURE_HEART.get())
                .unlockedBy("has_pure_heart", has(ModItems.PURE_HEART.get()))
                .unlockedBy("has_blazing_core", has(ModItems.BLAZING_CORE.get()))
                .save(output);
    }
}
