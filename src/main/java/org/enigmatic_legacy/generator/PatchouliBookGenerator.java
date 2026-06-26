package org.enigmatic_legacy.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * Patchouli 手册数据生成器。

 * 生成：
 * 1. data/enigmatic_legacy/patchouli_books/the_acknowledgment/book.json
 * 2. assets/enigmatic_legacy/patchouli_books/the_acknowledgment/<语言>/categories/*.json
 * 3. assets/enigmatic_legacy/patchouli_books/the_acknowledgment/<语言>/entries/../*.json
 */
public class PatchouliBookGenerator implements DataProvider {
    private static final String BOOK = "the_acknowledgment";

    private final PackOutput.PathProvider dataPathProvider;
    private final PackOutput.PathProvider assetPathProvider;

    public PatchouliBookGenerator(PackOutput output) {
        this.dataPathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                "patchouli_books"
        );

        this.assetPathProvider = output.createPathProvider(
                PackOutput.Target.RESOURCE_PACK,
                "patchouli_books"
        );
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator()
                .getVanillaPack(event.includeServer())
                .addProvider(PatchouliBookGenerator::new);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(DataProvider.saveStable(
                output,
                createBookJson(),
                getDataBookPath()
        ));

        addEnglishContent(output, futures);
        addChineseContent(output, futures);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Books";
    }

    private Path getDataBookPath() {
        return this.dataPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                BOOK + "/book"
        ));
    }

    private Path getAssetPath(String language, String relativePath) {
        return this.assetPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                BOOK + "/" + language + "/" + relativePath
        ));
    }

    private CompletableFuture<?> save(
            CachedOutput output,
            String language,
            String relativePath,
            JsonObject json
    ) {
        return DataProvider.saveStable(
                output,
                json,
                getAssetPath(language, relativePath)
        );
    }

    private void addEnglishContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        /*
         * 基础分类：
         * - World：世界机制与隐藏设定
         * - Materials：材料
         * - Relics：普通遗物、戒指、护符
         */
        addCategories(output, futures, "en_us",
                "World", "Laws, consequences, and hidden truths of this world.",
                "Materials", "Unusual materials used to create enigmatic relics.",
                "Relics", "Ancient tools, rings, charms, and artifacts."
        );

        /*
         * 新增分类：
         * - Spellstones：术石
         * - Scrolls：奥秘卷轴
         * - Equipment：武器、工具与护甲
         *
         * 这里直接使用已有 category(...) 和 save(...) 方法生成 JSON。
         * 不需要手写 generated 里的 JSON。
         */
        futures.add(save(output, "en_us", "categories/spellstones", category(
                "Spellstones",
                "Powerful stones that grant active and passive abilities when equipped.",
                "enigmatic_legacy:golem_heart",
                30
        )));

        futures.add(save(output, "en_us", "categories/scrolls", category(
                "Arcane Scrolls",
                "Scrolls and pacts that alter flight, experience, curses, and rewards.",
                "enigmatic_legacy:xp_scroll",
                40
        )));

        futures.add(save(output, "en_us", "categories/equipment", category(
                "Equipment",
                "Weapons, tools, and armor forged from unusual materials.",
                "enigmatic_legacy:etherium_sword",
                50
        )));

        addWorldEntriesEn(output, futures);
        addMaterialEntriesEn(output, futures);
        addRelicEntriesEn(output, futures);

        /*
         * 新增完整物品说明。
         */
        addSpellstoneEntriesEn(output, futures);
        addScrollEntriesEn(output, futures);
        addEquipmentEntriesEn(output, futures);
    }

    /**
     * 英文装备条目。
     */
    private void addEquipmentEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/equipment/etherium_sword", recipeEntry(
                "Etherium Broadsword",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                0,
                "A heavy etherium blade forged for direct combat.",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_pickaxe", recipeEntry(
                "Etherium Pickaxe",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                10,
                "A durable etherium tool for mining hard materials.",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_shovel", recipeEntry(
                "Etherium Shovel",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                20,
                "A shovel forged from etherium for fast excavation.",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_axe", recipeEntry(
                "Etherium Waraxe",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                30,
                "An etherium axe made as both a tool and a weapon.",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_armor", entry(
                "Etherium Armor",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                40,
                spotlightPage(
                        "enigmatic_legacy:etherium_chestplate",
                        "Etherium Armor",
                        "A full armor set forged from etherium.$(br2)" +
                                "It is intended for late-game protection and pairs naturally with other advanced relics."
                ),
                craftingPage("enigmatic_legacy:etherium_helmet", "Helmet recipe."),
                craftingPage("enigmatic_legacy:etherium_chestplate", "Chestplate recipe."),
                craftingPage("enigmatic_legacy:etherium_leggings", "Leggings recipe."),
                craftingPage("enigmatic_legacy:etherium_boots", "Boots recipe.")
        )));
    }

    /**
     * 中文装备条目。
     */
    private void addEquipmentEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/equipment/etherium_sword", recipeEntry(
                "以太阔剑",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                0,
                "一把沉重的以太剑刃，为正面战斗而锻造。",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_pickaxe", recipeEntry(
                "以太镐",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                10,
                "由以太锻造的耐用矿镐，适合开采坚硬材料。",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_shovel", recipeEntry(
                "以太锹",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                20,
                "由以太锻造的铲具，用于快速挖掘泥土、沙砾与类似方块。",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_axe", recipeEntry(
                "以太斧",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                30,
                "既是工具，也是武器的以太战斧。",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "zh_cn", "entries/equipment/etherium_armor", entry(
                "以太套装",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                40,
                spotlightPage(
                        "enigmatic_legacy:etherium_chestplate",
                        "以太套装",
                        "由以太锻造的完整护甲套装。$(br2)" +
                                "它适合后期防护，也能自然搭配其它高阶遗物。"
                ),
                craftingPage("enigmatic_legacy:etherium_helmet", "头盔配方。"),
                craftingPage("enigmatic_legacy:etherium_chestplate", "胸甲配方。"),
                craftingPage("enigmatic_legacy:etherium_leggings", "护腿配方。"),
                craftingPage("enigmatic_legacy:etherium_boots", "靴子配方。")
        )));
    }

    private void addChineseContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        /*
         * 基础分类：
         * - 世界
         * - 材料
         * - 遗物
         */
        addCategories(output, futures, "zh_cn",
                "世界", "关于此世法则、代价与隐秘真相的记录。",
                "材料", "用于制作神秘遗物的异常材料。",
                "遗物", "古老工具、戒指、护符与神秘造物。"
        );

        /*
         * 新增分类：
         * - 术石
         * - 奥秘卷轴
         * - 装备
         */
        futures.add(save(output, "zh_cn", "categories/spellstones", category(
                "术石",
                "佩戴后提供主动与被动能力的强大遗物。",
                "enigmatic_legacy:golem_heart",
                30
        )));

        futures.add(save(output, "zh_cn", "categories/scrolls", category(
                "奥秘卷轴",
                "改变飞行、经验、诅咒与收益规则的卷轴和契约。",
                "enigmatic_legacy:xp_scroll",
                40
        )));

        futures.add(save(output, "zh_cn", "categories/equipment", category(
                "装备",
                "由异常材料锻造而成的武器、工具与护甲。",
                "enigmatic_legacy:etherium_sword",
                50
        )));

        addWorldEntriesZh(output, futures);
        addMaterialEntriesZh(output, futures);
        addRelicEntriesZh(output, futures);

        /*
         * 新增完整物品说明。
         */
        addSpellstoneEntriesZh(output, futures);
        addScrollEntriesZh(output, futures);
        addEquipmentEntriesZh(output, futures);
    }

    private void addCategories(
            CachedOutput output,
            List<CompletableFuture<?>> futures,
            String lang,
            String worldName,
            String worldDesc,
            String materialsName,
            String materialsDesc,
            String relicsName,
            String relicsDesc
    ) {
        futures.add(save(output, lang, "categories/world", category(
                worldName,
                worldDesc,
                "minecraft:grass_block",
                0
        )));

        futures.add(save(output, lang, "categories/materials", category(
                materialsName,
                materialsDesc,
                "enigmatic_legacy:astral_dust",
                10
        )));

        futures.add(save(output, lang, "categories/relics", category(
                relicsName,
                relicsDesc,
                "enigmatic_legacy:the_acknowledgment",
                20
        )));
    }

    private void addWorldEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/world/soul_loss", entry(
                "Soul Loss",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "Soul Loss",
                        "Death is rarely without consequence. Some forces return what was lost, but never without reminding you that the world keeps count."
                )
        )));

        futures.add(save(output, "en_us", "entries/world/the_architects_favor", entry(
                "The Architect's Favor",
                "world",
                "minecraft:structure_block",
                10,
                textPage(
                        "The Architect's Favor",
                        "Somewhere beyond ordinary craft and ordinary reward lies a favor not meant to be seen by everyone."
                ),
                textPage(
                        "Not Hidden Here",
                        "In the original design, this knowledge was deliberately hidden. In this book, the hint is visible, but the item itself is not generated until it is registered in the mod."
                )
        )));
    }

    private void addWorldEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/world/soul_loss", entry(
                "灵魂损耗",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "灵魂损耗",
                        "死亡从来不是毫无代价的。有些力量会归还失去之物，但世界始终记得你付出过什么。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/world/the_architects_favor", entry(
                "建筑师的青睐",
                "world",
                "minecraft:structure_block",
                10,
                textPage(
                        "建筑师的青睐",
                        "在寻常合成与寻常奖赏之外，存在着某种并非所有人都该看见的青睐。"
                ),
                textPage(
                        "此处不再隐藏",
                        "原设计中，这份知识被刻意隐藏。现在手册会显示这条提示；但在物品真正注册前，不会生成对应物品页与配方页。"
                )
        )));
    }

    private void addMaterialEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/materials/astral_dust", simpleSpotlight(
                "Astral Dust",
                "materials",
                "enigmatic_legacy:astral_dust",
                0,
                "A faintly shimmering substance found at the edge of the unknown. Many early relics begin with this dust."
        )));

        futures.add(save(output, "en_us", "entries/materials/ender_rod", recipeEntry(
                "Ender Rod",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "A rod infused with ender resonance. It serves as a stabilizing component for stranger constructions.",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "en_us", "entries/materials/thicc_scroll", recipeEntry(
                "Thicc Scroll",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "A blank but unusually sturdy scroll. It is prepared to contain knowledge that ordinary paper would fail to hold.",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "en_us", "entries/materials/etherium_ingot", entry(
                "Etherium Ingot",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                30,
                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "Etherium",
                        "Etherium is a refined material used in advanced relic construction."
                ),
                craftingPage(
                        "enigmatic_legacy:etherium_block_uncrafting",
                        "Etherium blocks can be broken back down into ingots."
                )
        )));

        futures.add(save(output, "en_us", "entries/materials/cosmic_heart", recipeEntry(
                "Cosmic Heart",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                40,
                "A heart-shaped concentration of cosmic potential. It is a crucial ingredient for artifacts tied to space and distance.",
                "enigmatic_legacy:cosmic_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/earth_heart", recipeEntry(
                "Heart of the Earth",
                "materials",
                "enigmatic_legacy:earth_heart",
                50,
                "Fragments of the earth can be gathered into a single heart, pulsing with buried vitality.",
                "enigmatic_legacy:earth_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/twisted_heart", recipeEntry(
                "Twisted Heart",
                "materials",
                "enigmatic_legacy:twisted_heart",
                60,
                "A heart distorted by hostile energies. It is dangerous, but many relics demand dangerous components.",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/evil_essence", simpleSpotlight(
                "Evil Essence",
                "materials",
                "enigmatic_legacy:evil_essence",
                70,
                "Condensed malice given form. It is unstable, corruptive, and useful."
        )));

        futures.add(save(output, "en_us", "entries/materials/evil_ingot", recipeEntry(
                "Evil Ingot",
                "materials",
                "enigmatic_legacy:evil_ingot",
                80,
                "An ingot forged from essence too malicious to be left uncontained.",
                "enigmatic_legacy:evil_ingot"
        )));
    }

    private void addMaterialEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/materials/astral_dust", simpleSpotlight(
                "星尘",
                "materials",
                "enigmatic_legacy:astral_dust",
                0,
                "一种微微闪烁的物质，仿佛来自未知的边缘。许多早期遗物都从这份尘埃开始。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/ender_rod", recipeEntry(
                "末影棒",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "注入末影共鸣的细棒，可作为更奇异构造的稳定部件。",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/thicc_scroll", recipeEntry(
                "空卷轴",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "一张空白却异常厚实的卷轴。它能承载普通纸张无法容纳的知识。",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/etherium_ingot", entry(
                "以太锭",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                30,
                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "以太",
                        "以太是一种经过精炼的材料，用于更高阶遗物的构造。"
                ),
                craftingPage(
                        "enigmatic_legacy:etherium_block_uncrafting",
                        "以太块可以重新拆回以太锭。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/materials/cosmic_heart", recipeEntry(
                "寰宇之心",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                40,
                "凝聚成心形的宇宙潜能，是与空间和距离相关遗物的重要材料。",
                "enigmatic_legacy:cosmic_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/earth_heart", recipeEntry(
                "大地之心",
                "materials",
                "enigmatic_legacy:earth_heart",
                50,
                "大地的碎片被重新聚合为一颗心，其中脉动着深埋地下的生命力。",
                "enigmatic_legacy:earth_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/twisted_heart", recipeEntry(
                "扭曲之心",
                "materials",
                "enigmatic_legacy:twisted_heart",
                60,
                "被敌意能量扭曲的心脏。它很危险，但许多遗物恰恰需要危险的材料。",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_essence", simpleSpotlight(
                "极恶精华",
                "materials",
                "enigmatic_legacy:evil_essence",
                70,
                "凝成实质的恶意。它不稳定，带有腐化性，也因此十分有用。"
        )));

        futures.add(save(output, "zh_cn", "entries/materials/evil_ingot", recipeEntry(
                "极恶锭",
                "materials",
                "enigmatic_legacy:evil_ingot",
                80,
                "由过于恶毒、不宜散置的精华锻成的锭。",
                "enigmatic_legacy:evil_ingot"
        )));
    }

    private void addRelicEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/relics/the_acknowledgment", entry(
                "The Acknowledgment",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "The Acknowledgment",
                        "The Acknowledgment is both a mysterious guidebook and an ancient relic.$(br2)" +
                                "Right-clicking it opens a manual that records relics, stones, scrolls, curses, and hidden knowledge.$(br2)" +
                                "When you do not know what to seek next, this book is the safest place to begin."
                ),
                textPage(
                        "As a Guide",
                        "The Acknowledgment is not consumed and does not need to be equipped in a Curios slot. Hold it and right-click to read the knowledge currently recorded within.$(br2)" +
                                "It contains material sources, relic uses, exploration goals, and important hints related to the Ring of the Seven Curses."
                ),
                textPage(
                        "As a Weapon",
                        "The Acknowledgment is not only a book. It can be swung like a weapon, setting struck enemies aflame.$(br2)" +
                                "Current values:$(br)" +
                                "$(li)Attack Damage: 3.5" +
                                "$(li)Attack Speed: -2.1" +
                                "$(li)Ignites target: 4 seconds$(br2)" +
                                "It can also be enchanted, with an enchantability value of 24."
                ),
                textPage(
                        "Curse Resonance",
                        "When the bearer suffers under the Ring of the Seven Curses, The Acknowledgment weakens the pain of the Fourth Curse.$(br2)" +
                                "Current effect:$(br)" +
                                "$(li)Reduces the Fourth Curse's extra incoming damage penalty by 20%$(br2)" +
                                "It does not remove the curse completely, but it makes survival slightly more forgiving."
                ),
                textPage(
                        "Usage Advice",
                        "It is recommended to craft The Acknowledgment early. It lets you check relic functions at any time and can serve as an emergency weapon.$(br2)" +
                                "If you are wearing the Ring of the Seven Curses, keeping it in your inventory is strongly advised."
                ),
                craftingPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "Craft The Acknowledgment, then right-click it to open this manual."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/iron_ring", recipeEntry(
                "Iron Ring",
                "relics",
                "enigmatic_legacy:iron_ring",
                10,
                "A simple ring base. Many stronger rings begin as something this plain.",
                "enigmatic_legacy:iron_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/exquisite_ring", recipeEntry(
                "Exquisite Ring",
                "relics",
                "enigmatic_legacy:golden_ring",
                20,
                "A refined ring with a more precious frame, suitable for enchantment and further transformation.",
                "enigmatic_legacy:golden_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/ender_ring", recipeEntry(
                "Ender Ring",
                "relics",
                "enigmatic_legacy:ender_ring",
                30,
                "A ring linked to ender storage. It grants convenient access to an Ender Chest through its own interface.",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/magnet_ring", recipeEntry(
                "Magnet Ring",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "This ring pulls nearby items toward its bearer. It can be toggled from the inventory screen.",
                "enigmatic_legacy:magnet_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/dislocation_ring", recipeEntry(
                "Dislocation Ring",
                "relics",
                "enigmatic_legacy:dislocation_ring",
                50,
                "A stronger magnetic ring. Instead of slowly dragging items, it dislocates nearby drops directly into reach.",
                "enigmatic_legacy:dislocation_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/twisted_mirror", recipeEntry(
                "Twisted Mirror",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                60,
                "A mirror that bends return and remembrance. It is crafted around a recall potion and a Twisted Heart.",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "en_us", "entries/relics/monster_charm", recipeEntry(
                "Emblem of Monster Slayer",
                "relics",
                "enigmatic_legacy:monster_charm",
                70,
                "An emblem devoted to hunting monsters. It increases damage against hostile creatures and grants additional rewards.",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/treasure_hunter_charm", recipeEntry(
                "Charm of Treasure Hunter",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                80,
                "A charm for miners and explorers. It improves mining, grants fortune, and can maintain night vision.",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "Emblem of Bloodstained Valor",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                90,
                "An emblem for cursed bearers. The closer its wearer is to death, the more violently it answers.",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "en_us", "entries/relics/mega_sponge", recipeEntry(
                "Extrapolated Megasponge",
                "relics",
                "enigmatic_legacy:mega_sponge",
                100,
                "A charm-like sponge that consumes nearby water when its bearer touches it.",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "en_us", "entries/relics/enchanter_pearl", recipeEntry(
                "Enchanter's Pearl",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                110,
                "A pearl useful only to cursed bearers. While equipped, it grants an additional charm slot.",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "en_us", "entries/relics/enigmatic_eye", entry(
                "Inscrutable Eye",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                120,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "Dormant Eye",
                        "The Eye sleeps until awakened. Once awakened, it may be worn as a charm."
                ),
                textPage(
                        "Awakened Sight",
                        "When equipped, the awakened Eye grants an additional charm slot and increases block interaction range. It may also give voice to the Watcher."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/golem_heart", entry(
                "Heart of the Golem",
                "relics",
                "enigmatic_legacy:golem_heart",
                2,
                spotlightPage(
                        "enigmatic_legacy:golem_heart",
                        "Heart of the Golem",
                        "A spellstone that makes its bearer as sturdy as an iron golem, at the cost of greater vulnerability to magic."
                ),
                textPage(
                        "Stone Skin",
                        "It grants armor and knockback resistance. If worn without armor, it grants stronger armor, armor toughness, and explosion resistance."
                ),
                textPage(
                        "Magical Weakness",
                        "Magic damage is amplified. Effects such as Poison, which normally cannot kill, can become lethal while this spellstone is worn."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/unwitnessed_amulet", entry(
                "Unwitnessed Amulet",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                130,
                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "Unwitnessed Amulet",
                        "An amulet whose nature is not yet witnessed. Use it to reveal one of its enigmatic variants."
                ),
                textPage(
                        "Revealed Forms",
                        "Each revealed amulet bears a different color and power: damage, speed, deflection, gravity, mining, lifesteal, or swimming."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/storage_crystal", simpleSpotlight(
                "Extradimensional Vessel",
                "relics",
                "enigmatic_legacy:storage_crystal",
                140,
                "A vessel for a life interrupted. It stores what would otherwise be lost when saved by the Seven Curses."
        )));

        futures.add(save(output, "en_us", "entries/relics/soul_crystal", simpleSpotlight(
                "Soul Crystal",
                "relics",
                "enigmatic_legacy:soul_crystal",
                150,
                "A crystallized remnant of a rescued soul, bound to the place and moment of death."
        )));

        futures.add(save(output, "en_us", "entries/relics/forbidden_fruit", simpleSpotlight(
                "Forbidden Fruit",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                160,
                "A fruit whose sweetness promises more than it should."
        )));
    }

    /**
     * 英文术石条目。
     * 这里补全当前项目已经注册的术石：
     * - Heart of the Golem
     * - Angel's Blessing
     * - Will of the Ocean
     * - Blazing Core
     * - Eye of the Nebula
     * - Pearl of the Void
     * - Non-Euclidean Cube
     * - Heart of Creation
     */
    private void addSpellstoneEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/spellstones/golem_heart", entry(
                "Heart of the Golem",
                "spellstones",
                "enigmatic_legacy:golem_heart",
                0,
                spotlightPage(
                        "enigmatic_legacy:golem_heart",
                        "Heart of the Golem",
                        "A defensive spellstone that turns the bearer into something closer to an iron golem.$(br2)" +
                                "It grants armor, toughness, knockback resistance, and special protection when the bearer wears no armor."
                ),
                textPage(
                        "Weakness",
                        "Its strength has a price.$(br2)" +
                                "Magic, poison, wither, and similar effects become more dangerous while this stone is worn."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/angel_blessing", entry(
                "Angel's Blessing",
                "spellstones",
                "enigmatic_legacy:angel_blessing",
                10,
                spotlightPage(
                        "enigmatic_legacy:angel_blessing",
                        "Angel's Blessing",
                        "A mobility spellstone tied to air and grace.$(br2)" +
                                "It helps the bearer move, fall, and recover more safely during exploration."
                ),
                textPage(
                        "Use",
                        "Best used when traveling through cliffs, mountains, floating islands, or dangerous vertical spaces."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/ocean_stone", entry(
                "Will of the Ocean",
                "spellstones",
                "enigmatic_legacy:ocean_stone",
                20,
                spotlightPage(
                        "enigmatic_legacy:ocean_stone",
                        "Will of the Ocean",
                        "A water-aspected spellstone for underwater exploration.$(br2)" +
                                "It improves movement and survival in water, but its power is opposed by flame."
                ),
                textPage(
                        "Warning",
                        "Fire and heat become far more threatening while relying on this stone."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/blazing_core", entry(
                "Blazing Core",
                "spellstones",
                "enigmatic_legacy:blazing_core",
                30,
                spotlightPage(
                        "enigmatic_legacy:blazing_core",
                        "Blazing Core",
                        "A fire-aspected spellstone connected to flame, lava, and the Nether.$(br2)" +
                                "It grants strong protection against heat, but its power is not without limits."
                ),
                textPage(
                        "Overheat",
                        "Do not mistake resistance for invulnerability.$(br2)" +
                                "If the core is pushed too far, the bearer may still suffer the consequences of fire and lava."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/eye_of_nebula", entry(
                "Eye of the Nebula",
                "spellstones",
                "enigmatic_legacy:eye_of_nebula",
                40,
                spotlightPage(
                        "enigmatic_legacy:eye_of_nebula",
                        "Eye of the Nebula",
                        "A teleportation spellstone linked to distant space.$(br2)" +
                                "Its active power moves the bearer behind the creature they are looking at."
                ),
                textPage(
                        "Combat Use",
                        "After teleporting, the next attack becomes far more dangerous.$(br2)" +
                                "It also improves magical offense and resistance, but water makes incoming damage much harsher."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/void_pearl", entry(
                "Pearl of the Void",
                "spellstones",
                "enigmatic_legacy:void_pearl",
                50,
                spotlightPage(
                        "enigmatic_legacy:void_pearl",
                        "Pearl of the Void",
                        "A forbidden spellstone that makes the bearer closer to the void.$(br2)" +
                                "It removes the need to breathe and protects against most status effects."
                ),
                textPage(
                        "Darkness",
                        "Enemies that remain too close in darkness may suffer void damage and crippling effects.$(br2)" +
                                "It can also help the bearer survive otherwise fatal damage."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/the_cube", entry(
                "Non-Euclidean Cube",
                "spellstones",
                "enigmatic_legacy:the_cube",
                60,
                spotlightPage(
                        "enigmatic_legacy:the_cube",
                        "Non-Euclidean Cube",
                        "A geometry-breaking spellstone.$(br2)" +
                                "It improves movement, mining, attack speed, luck, and fortune while bending the rules of incoming damage."
                ),
                textPage(
                        "Active Power",
                        "Its active ability sends the bearer toward a random structure within the current dimension.$(br2)" +
                                "Use it carefully. Non-Euclidean travel rarely cares about comfort."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/heart_of_creation", entry(
                "Heart of Creation",
                "spellstones",
                "enigmatic_legacy:heart_of_creation",
                70,
                spotlightPage(
                        "enigmatic_legacy:heart_of_creation",
                        "Heart of Creation",
                        "A supreme spellstone bound to creation and survival.$(br2)" +
                                "It grants flight, protects from many damage sources, and can call lightning upon nearby enemies."
                ),
                textPage(
                        "Immortality",
                        "When carried or equipped, it may prevent death by leaving the bearer at the edge of life.$(br2)" +
                                "This protection is powerful, but it should not be mistaken for carelessness."
                )
        )));
    }

    /**
     * 英文卷轴条目。
     */
    private void addScrollEntriesEn(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/scrolls/xp_scroll", entry(
                "Scroll of Ageless Wisdom",
                "scrolls",
                "enigmatic_legacy:xp_scroll",
                0,
                spotlightPage(
                        "enigmatic_legacy:xp_scroll",
                        "Scroll of Ageless Wisdom",
                        "An arcane scroll that stores experience.$(br2)" +
                                "When active, it can absorb nearby experience or return stored experience to the bearer."
                ),
                textPage(
                        "Use",
                        "Use Shift + Right Click to toggle its mode.$(br2)" +
                                "It is best carried when mining, enchanting, or preparing for dangerous fights."
                )
        )));

        futures.add(save(output, "en_us", "entries/scrolls/heaven_scroll", entry(
                "Gift of the Heaven",
                "scrolls",
                "enigmatic_legacy:heaven_scroll",
                10,
                spotlightPage(
                        "enigmatic_legacy:heaven_scroll",
                        "Gift of the Heaven",
                        "A scroll that grants flight within beacon influence.$(br2)" +
                                "It consumes experience while flying and protects against fall damage under proper conditions."
                ),
                textPage(
                        "Beacon Bound",
                        "Leaving beacon range removes flight and grants slow falling for a short time.$(br2)" +
                                "If you still have not landed afterward, the ground will remind you of gravity."
                )
        )));

        futures.add(save(output, "en_us", "entries/scrolls/cursed_scroll", entry(
                "Scroll of a Thousand Curses",
                "scrolls",
                "enigmatic_legacy:cursed_scroll",
                20,
                spotlightPage(
                        "enigmatic_legacy:cursed_scroll",
                        "Scroll of a Thousand Curses",
                        "A scroll meant only for those who endure the Seven Curses.$(br2)" +
                                "Its bonuses scale with the number of curse enchantments on your equipment."
                ),
                textPage(
                        "Scaling",
                        "It improves attack, mining speed, and regeneration.$(br2)" +
                                "The more cursed your equipment becomes, the stronger the scroll answers."
                )
        )));

        futures.add(save(output, "en_us", "entries/scrolls/fabulous_scroll", entry(
                "Grace of the Creator",
                "scrolls",
                "enigmatic_legacy:fabulous_scroll",
                30,
                spotlightPage(
                        "enigmatic_legacy:fabulous_scroll",
                        "Grace of the Creator",
                        "A superior flight scroll.$(br2)" +
                                "It allows flight anywhere, consuming experience unless the bearer remains within beacon range."
                ),
                textPage(
                        "Limit",
                        "It cannot be worn together with Gift of the Heaven.$(br2)" +
                                "Both grant flight, but their blessings do not stack."
                )
        )));

        futures.add(save(output, "en_us", "entries/scrolls/avarice_scroll", entry(
                "Pact of Infinite Avarice",
                "scrolls",
                "enigmatic_legacy:avarice_scroll",
                40,
                spotlightPage(
                        "enigmatic_legacy:avarice_scroll",
                        "Pact of Infinite Avarice",
                        "A pact for cursed bearers who value gain above restraint.$(br2)" +
                                "It improves fortune, affects piglins, increases rewards, and helps with trading."
                ),
                textPage(
                        "Greed",
                        "With sufficient curses, material gains can be doubled.$(br2)" +
                                "Killed creatures may drop emeralds, and villagers may offer better prices."
                )
        )));
    }

    private void addRelicEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/relics/the_acknowledgment", entry(
                "启示之证",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "启示之证",
                        "启示之证既是一本神秘指南，也是一件古老遗物。$(br2)" +
                                "右键使用时，它会打开这本记录遗物、术石、卷轴与诅咒知识的手册。$(br2)" +
                                "若你不知道下一步该寻找什么，它就是最可靠的起点。"
                ),
                textPage(
                        "作为指南",
                        "启示之证不会被消耗，也不需要放在饰品栏。只要手持右键，就能随时翻阅当前已记录的知识。$(br2)" +
                                "其中包含材料来源、遗物用途、世界探索目标，以及一些和七咒之戒相关的重要提示。"
                ),
                textPage(
                        "作为武器",
                        "启示之证并不只是书。它可以像武器一样挥动，命中敌人时会点燃目标。$(br2)" +
                                "当前数值：$(br)" +
                                "$(li)攻击伤害：3.5" +
                                "$(li)攻击速度：-2.1" +
                                "$(li)命中点燃：4 秒$(br2)" +
                                "它也可以被附魔，附魔能力为 24。"
                ),
                textPage(
                        "七咒共鸣",
                        "当持有者承受七咒之戒时，启示之证会削弱第四诅咒带来的痛苦。$(br2)" +
                                "当前效果：$(br)" +
                                "$(li)削弱第四诅咒的额外受伤惩罚 20%$(br2)" +
                                "它不会完全消除诅咒，只会让佩戴七咒之戒时的生存压力稍微降低。"
                ),
                textPage(
                        "使用建议",
                        "建议你在前期尽快制作启示之证。它能让你随时确认遗物用途，也能在没有合适武器时临时防身。$(br2)" +
                                "如果你正在佩戴七咒之戒，最好把它长期留在背包中，以获得第四诅咒削弱效果。"
                ),
                craftingPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "制作启示之证后，右键即可打开本手册。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/iron_ring", recipeEntry(
                "铁指环",
                "relics",
                "enigmatic_legacy:iron_ring",
                10,
                "最朴素的戒指基底。许多更强大的戒指都始于这样平凡的形态。",
                "enigmatic_legacy:iron_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/exquisite_ring", recipeEntry(
                "精美戒指",
                "relics",
                "enigmatic_legacy:golden_ring",
                20,
                "以贵重材料精制而成的戒指，适合作为附魔与进一步转化的载体。",
                "enigmatic_legacy:golden_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/ender_ring", recipeEntry(
                "末影之戒",
                "relics",
                "enigmatic_legacy:ender_ring",
                30,
                "与末影储物相连的戒指，可通过专属界面便捷访问末影箱。",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/magnet_ring", recipeEntry(
                "磁力之戒",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "这枚戒指会将附近掉落物吸向佩戴者。可在物品栏界面切换启用状态。",
                "enigmatic_legacy:magnet_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/dislocation_ring", recipeEntry(
                "转位之戒",
                "relics",
                "enigmatic_legacy:dislocation_ring",
                50,
                "更强大的磁性戒指。它并非缓慢拖动物品，而是让附近掉落物直接进入可拾取范围。",
                "enigmatic_legacy:dislocation_ring"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/twisted_mirror", recipeEntry(
                "扭曲魔镜",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                60,
                "一面扭曲归返与记忆的镜子，以回忆药水和扭曲之心为核心制成。",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/monster_charm", recipeEntry(
                "怪物猎人勋章",
                "relics",
                "enigmatic_legacy:monster_charm",
                70,
                "献给怪物猎人的勋章。它提升对敌对生物的伤害，并带来额外战利品。",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/treasure_hunter_charm", recipeEntry(
                "猎宝者护符",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                80,
                "属于矿工与探险者的护符。它强化挖掘，提供时运，并能维持夜视。",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "血战沙场之证",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                90,
                "只有受诅者才能承受的勋章。佩戴者越接近死亡，它回应得越猛烈。",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/mega_sponge", recipeEntry(
                "推演型超级海绵",
                "relics",
                "enigmatic_legacy:mega_sponge",
                100,
                "类似护符的海绵。佩戴者触碰水体时，它会吞噬附近的水。",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enchanter_pearl", recipeEntry(
                "附魔师的珍珠",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                110,
                "只对受诅者有用的珍珠。佩戴时提供额外护符栏位。",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enigmatic_eye", entry(
                "全知之眼",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                120,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "休眠之眼",
                        "此眼沉睡着，直到被唤醒。苏醒后，它可以作为护符佩戴。"
                ),
                textPage(
                        "苏醒之视",
                        "佩戴已唤醒的全知之眼时，会额外提供一个护符栏位，并提高方块交互距离。它也可能让观察者发声。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/golem_heart", entry(
                "魔像之心",
                "relics",
                "enigmatic_legacy:golem_heart",
                2,
                spotlightPage(
                        "enigmatic_legacy:golem_heart",
                        "魔像之心",
                        "一种术石。它让佩戴者变得如铁魔像般坚硬，但也让魔法伤害变得更加危险。"
                ),
                textPage(
                        "石肤",
                        "佩戴时提供护甲和击退抗性；未穿护甲时会获得更强的护甲、护甲韧性与爆炸抗性。"
                ),
                textPage(
                        "魔法易伤",
                        "魔法伤害会被放大。中毒等原本不能杀死玩家的效果，在魔像之心的影响下可以继续造成致命伤害。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/unwitnessed_amulet", entry(
                "未见之护符",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                130,
                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "未见之护符",
                        "尚未被见证其本质的护符。使用后会揭示其神秘变体之一。"
                ),
                textPage(
                        "显现形态",
                        "每种显现出的护符都拥有不同颜色与能力：伤害、速度、弹射物偏转、重力、挖掘、吸血或游泳。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/storage_crystal", simpleSpotlight(
                "超维容器",
                "relics",
                "enigmatic_legacy:storage_crystal",
                140,
                "承载被中断生命的容器。当七咒之戒救回佩戴者时，它保存本该失去之物。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/soul_crystal", simpleSpotlight(
                "灵魂水晶",
                "relics",
                "enigmatic_legacy:soul_crystal",
                150,
                "被拯救灵魂的结晶残片，绑定于死亡发生的地点与时刻。"
        )));

        futures.add(save(output, "zh_cn", "entries/relics/forbidden_fruit", simpleSpotlight(
                "禁果",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                160,
                "甜美得过于危险的果实。它许诺了不应轻易触碰的东西。"
        )));
    }

    /**
     * 中文卷轴条目。
     */
    private void addScrollEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/scrolls/xp_scroll", entry(
                "永恒智慧卷轴",
                "scrolls",
                "enigmatic_legacy:xp_scroll",
                0,
                spotlightPage(
                        "enigmatic_legacy:xp_scroll",
                        "永恒智慧卷轴",
                        "一种用于储存经验的奥秘卷轴。$(br2)" +
                                "启用后，它可以吸收附近经验，也能将储存经验返还给持有者。"
                ),
                textPage(
                        "使用",
                        "使用 Shift + 右键切换模式。$(br2)" +
                                "适合采矿、附魔或进入危险战斗前携带。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/heaven_scroll", entry(
                "天堂之礼",
                "scrolls",
                "enigmatic_legacy:heaven_scroll",
                10,
                spotlightPage(
                        "enigmatic_legacy:heaven_scroll",
                        "天堂之礼",
                        "一种在信标范围内赋予飞行能力的卷轴。$(br2)" +
                                "飞行会消耗经验，并在满足条件时免疫摔落伤害。"
                ),
                textPage(
                        "信标限制",
                        "离开信标范围会失去飞行，并短暂获得缓降。$(br2)" +
                                "如果缓降结束后仍未落地，重力会重新索取代价。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/cursed_scroll", entry(
                "千咒卷轴",
                "scrolls",
                "enigmatic_legacy:cursed_scroll",
                20,
                spotlightPage(
                        "enigmatic_legacy:cursed_scroll",
                        "千咒卷轴",
                        "只有承受七咒之人才能使用的卷轴。$(br2)" +
                                "它的加成会随装备上的诅咒附魔数量提升。"
                ),
                textPage(
                        "成长",
                        "它提升攻击、挖掘速度与生命恢复。$(br2)" +
                                "装备越受诅咒，卷轴回应得越强。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/fabulous_scroll", entry(
                "创造者的恩赐",
                "scrolls",
                "enigmatic_legacy:fabulous_scroll",
                30,
                spotlightPage(
                        "enigmatic_legacy:fabulous_scroll",
                        "创造者的恩赐",
                        "更高阶的飞行卷轴。$(br2)" +
                                "它允许在任何地方飞行；若不在信标范围内，会持续消耗经验。"
                ),
                textPage(
                        "限制",
                        "它不能与天堂之礼同时佩戴。$(br2)" +
                                "二者都赐予飞行，但恩赐不会彼此叠加。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/scrolls/avarice_scroll", entry(
                "无尽贪婪契约",
                "scrolls",
                "enigmatic_legacy:avarice_scroll",
                40,
                spotlightPage(
                        "enigmatic_legacy:avarice_scroll",
                        "无尽贪婪契约",
                        "献给受诅者的贪婪契约。$(br2)" +
                                "它提升时运，影响猪灵，增加收益，并让交易更加有利。"
                ),
                textPage(
                        "贪婪",
                        "当诅咒足够深时，物资收益可以翻倍。$(br2)" +
                                "击杀生物可能掉落绿宝石，村民也可能给出更低价格。"
                )
        )));
    }

    /**
     * 中文术石条目。
     * 这里补全当前项目已经注册的所有术石。
     */
    private void addSpellstoneEntriesZh(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "entries/spellstones/golem_heart", entry(
                "魔像之心",
                "spellstones",
                "enigmatic_legacy:golem_heart",
                0,
                spotlightPage(
                        "enigmatic_legacy:golem_heart",
                        "魔像之心",
                        "一种防御型术石，会让佩戴者变得更接近铁魔像。$(br2)" +
                                "它提供护甲、护甲韧性、击退抗性，并在未穿护甲时给予更强保护。"
                ),
                textPage(
                        "代价",
                        "强大的防护并非没有代价。$(br2)" +
                                "魔法、中毒、凋零等效果会变得更加危险。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/angel_blessing", entry(
                "天使之祝",
                "spellstones",
                "enigmatic_legacy:angel_blessing",
                10,
                spotlightPage(
                        "enigmatic_legacy:angel_blessing",
                        "天使之祝",
                        "与空气和轻盈相关的机动型术石。$(br2)" +
                                "它能帮助佩戴者更安全地移动、坠落与探索。"
                ),
                textPage(
                        "用途",
                        "适合探索峭壁、高山、浮岛和各种垂直空间。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/ocean_stone", entry(
                "海洋意志",
                "spellstones",
                "enigmatic_legacy:ocean_stone",
                20,
                spotlightPage(
                        "enigmatic_legacy:ocean_stone",
                        "海洋意志",
                        "水域属性的术石，适合水下探索。$(br2)" +
                                "它强化水中的移动与生存能力，但其力量与火焰相冲。"
                ),
                textPage(
                        "警告",
                        "佩戴时火焰与高温会变得更加危险。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/blazing_core", entry(
                "烈焰核心",
                "spellstones",
                "enigmatic_legacy:blazing_core",
                30,
                spotlightPage(
                        "enigmatic_legacy:blazing_core",
                        "烈焰核心",
                        "与火焰、岩浆和下界相关的术石。$(br2)" +
                                "它提供强大的高温防护，但这种防护并非没有极限。"
                ),
                textPage(
                        "过热",
                        "不要把抗性误认为无敌。$(br2)" +
                                "如果烈焰核心被推到极限，佩戴者仍可能承受火焰和岩浆的后果。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/eye_of_nebula", entry(
                "星云之眼",
                "spellstones",
                "enigmatic_legacy:eye_of_nebula",
                40,
                spotlightPage(
                        "enigmatic_legacy:eye_of_nebula",
                        "星云之眼",
                        "与遥远空间相连的传送术石。$(br2)" +
                                "主动使用时，会将佩戴者传送到其注视生物的身后。"
                ),
                textPage(
                        "战斗用途",
                        "传送之后，下一次攻击会变得更危险。$(br2)" +
                                "它也强化魔法伤害与魔法抗性，但在水中受到的伤害会更加严重。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/void_pearl", entry(
                "虚空珍珠",
                "spellstones",
                "enigmatic_legacy:void_pearl",
                50,
                spotlightPage(
                        "enigmatic_legacy:void_pearl",
                        "虚空珍珠",
                        "禁忌的术石，会让佩戴者更接近虚空。$(br2)" +
                                "它移除呼吸需求，并抵抗大多数状态效果。"
                ),
                textPage(
                        "黑暗",
                        "黑暗中靠近佩戴者的敌人可能受到虚空伤害和多种削弱效果。$(br2)" +
                                "它也可能帮助佩戴者抵挡一次致命伤害。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/the_cube", entry(
                "非欧立方",
                "spellstones",
                "enigmatic_legacy:the_cube",
                60,
                spotlightPage(
                        "enigmatic_legacy:the_cube",
                        "非欧立方",
                        "打破几何规则的术石。$(br2)" +
                                "它提升移动、挖掘、攻击速度、幸运与时运，并扭曲部分伤害规则。"
                ),
                textPage(
                        "主动能力",
                        "主动使用时，会将佩戴者送往当前维度中的随机结构。$(br2)" +
                                "谨慎使用。非欧旅行从不保证舒适。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/spellstones/heart_of_creation", entry(
                "创造之心",
                "spellstones",
                "enigmatic_legacy:heart_of_creation",
                70,
                spotlightPage(
                        "enigmatic_legacy:heart_of_creation",
                        "创造之心",
                        "与创造和生存相连的至高术石。$(br2)" +
                                "它提供飞行，抵御多种伤害，并能向附近敌人召下雷霆。"
                ),
                textPage(
                        "不朽",
                        "携带或佩戴时，它可以在死亡边缘保住佩戴者的生命。$(br2)" +
                                "这份保护很强大，但并不意味着可以鲁莽行事。"
                )
        )));
    }

    private static JsonObject createBookJson() {
        JsonObject book = new JsonObject();

        book.addProperty("name", "item.enigmatic_legacy.the_acknowledgment");
        book.addProperty("landing_text", "book.enigmatic_legacy.landing_text");
        book.addProperty("version", "20");

        book.addProperty("use_blocky_font", false);
        book.addProperty("filler_texture", "enigmatic_legacy:textures/gui/page_filler.png");
        book.addProperty("book_texture", "enigmatic_legacy:textures/gui/the_acknowledgment.png");
        book.addProperty("model", "enigmatic_legacy:item/the_acknowledgment");

        book.addProperty("dont_generate_book", true);
        book.addProperty("custom_book_item", "enigmatic_legacy:the_acknowledgment");
        book.addProperty("i18n", true);

        book.addProperty("nameplate_color", "FFAA00");
        book.addProperty("link_color", "00AAAA");
        book.addProperty("link_hover_color", "AA00AA");
        book.addProperty("progress_bar_color", "AA00AA");
        book.addProperty("progress_bar_background", "555555");
        book.addProperty("show_progress", false);
        book.addProperty("show_toasts", true);
        book.addProperty("pause_game", false);
        book.addProperty("text_overflow_mode", "overflow");

        // Patchouli 1.20+：book.json 在 data，实际书页内容在 assets。
        book.addProperty("use_resource_pack", true);

        return book;
    }

    private static JsonObject category(String name, String description, String icon, int sortnum) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("icon", icon);
        json.addProperty("sortnum", sortnum);
        json.addProperty("secret", false);
        return json;
    }

    private static JsonObject simpleSpotlight(
            String name,
            String category,
            String icon,
            int sortnum,
            String text
    ) {
        return entry(
                name,
                category,
                icon,
                sortnum,
                spotlightPage(icon, name, text)
        );
    }

    private static JsonObject recipeEntry(
            String name,
            String category,
            String icon,
            int sortnum,
            String introText,
            String recipe
    ) {
        return entry(
                name,
                category,
                icon,
                sortnum,
                spotlightPage(icon, name, introText),
                craftingPage(recipe, "合成配方 / Crafting Recipe")
        );
    }

    private static JsonObject entry(
            String name,
            String category,
            String icon,
            int sortnum,
            JsonObject... pages
    ) {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("category", EnigmaticLegacy.MODID + ":" + category);
        json.addProperty("icon", icon);
        json.addProperty("sortnum", sortnum);
        json.addProperty("secret", false);
        json.addProperty("read_by_default", true);

        JsonArray pageArray = new JsonArray();

        for (JsonObject page : pages) {
            pageArray.add(page);
        }

        json.add("pages", pageArray);

        return json;
    }

    private static JsonObject textPage(String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }

    private static JsonObject spotlightPage(String item, String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:spotlight");
        page.addProperty("item", item);
        page.addProperty("link_recipe", true);
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }

    private static JsonObject craftingPage(String recipe, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:crafting");
        page.addProperty("recipe", recipe);
        page.addProperty("title", " ");
        page.addProperty("text", text);
        return page;
    }
}