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
        addCategories(output, futures, "en_us",
                "World",
                "Laws, consequences, and hidden truths of this world.",
                "Materials",
                "Unusual materials used to create enigmatic relics.",
                "Relics",
                "Ancient tools, rings, charms, and artifacts."
        );

        addWorldEntriesEn(output, futures);
        addMaterialEntriesEn(output, futures);
        addRelicEntriesEn(output, futures);
    }

    private void addChineseContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        addCategories(output, futures, "zh_cn",
                "世界",
                "关于此世法则、代价与隐秘真相的记录。",
                "材料",
                "用于制作神秘遗物的异常材料。",
                "遗物",
                "古老工具、戒指、护符与神秘造物。"
        );

        addWorldEntriesZh(output, futures);
        addMaterialEntriesZh(output, futures);
        addRelicEntriesZh(output, futures);
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