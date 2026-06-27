package org.enigmatic_legacy.generator.patchouli;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 启示之证 Patchouli 手册英文内容生成器。
 * 作用：
 * - 生成英文分类；
 * - 生成英文物品介绍；
 * - 补全当前项目已有的术石、卷轴、装备和主要遗物说明。
 */
final class EnglishPatchouliBookGenerator extends AbstractPatchouliBookContentGenerator {
    EnglishPatchouliBookGenerator(PackOutput output) {
        super(output);
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Books: en_us";
    }

    /**
     * 英文内容入口。
     */
    @Override
    protected void addContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        // 基础分类。
        addCategories(output, futures, "en_us",
                "World", "Laws, consequences, and hidden truths of this world.",
                "Materials", "Unusual materials used to create enigmatic relics.",
                "Relics", "Ancient rings, charms, tomes, and unusual artifacts."
        );

        // 额外分类：术石、卷轴、装备。
        addExtraCategories(output, futures);

        // 各分类条目。
        addWorldEntries(output, futures);
        addMaterialEntries(output, futures);
        addRelicEntries(output, futures);
        addSpellstoneEntries(output, futures);
        addScrollEntries(output, futures);
        addEquipmentEntries(output, futures);
    }

    /**
     * 生成新增分类。
     */
    private void addExtraCategories(CachedOutput output, List<CompletableFuture<?>> futures) {
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
    }

    /**
     * 世界机制条目。
     */
    private void addWorldEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/world/soul_loss", entry(
                "Soul Loss",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "Soul Loss",
                        "Death is rarely without consequence.$(br2)" +
                                "Some forces return what was lost, but never without reminding you that the world keeps count."
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
                        "Hint",
                        "The Acknowledgment records only fragments of this truth.$(br2)" +
                                "Some knowledge must be earned through exploration, curses, and survival."
                )
        )));
    }

    /**
     * 材料条目。
     */
    private void addMaterialEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/materials/astral_dust", simpleSpotlight(
                "Astral Dust",
                "materials",
                "enigmatic_legacy:astral_dust",
                0,
                "A faintly shimmering substance found at the edge of the unknown.$(br2)" +
                        "Many early relics begin with this dust."
        )));

        futures.add(save(output, "en_us", "entries/materials/ender_rod", recipeEntry(
                "Ender Rod",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "A rod infused with ender resonance.$(br2)" +
                        "It serves as a stabilizing component for stranger constructions.",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "en_us", "entries/materials/thicc_scroll", recipeEntry(
                "Thicc Scroll",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "A blank but unusually sturdy scroll.$(br2)" +
                        "It is prepared to contain knowledge ordinary paper would fail to hold.",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "en_us", "entries/materials/etherium_ingot", entry(
                "Etherium Ingot",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                30,
                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "Etherium Ingot",
                        "A refined material used in advanced relic construction.$(br2)" +
                                "It is commonly used for etherium tools, weapons, and armor."
                ),
                craftingPage("enigmatic_legacy:etherium_block_uncrafting", "Etherium blocks can be broken back down into ingots.")
        )));

        futures.add(save(output, "en_us", "entries/materials/cosmic_heart", recipeEntry(
                "Cosmic Heart",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                40,
                "A heart-shaped concentration of cosmic potential.$(br2)" +
                        "It is a crucial ingredient for artifacts tied to space and distance.",
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
                "A heart distorted by hostile energies.$(br2)" +
                        "It is dangerous, but many relics demand dangerous components.",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/evil_essence", simpleSpotlight(
                "Evil Essence",
                "materials",
                "enigmatic_legacy:evil_essence",
                70,
                "Condensed malice given form.$(br2)" +
                        "It is unstable, corruptive, and useful."
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

    /**
     * 遗物条目。
     */
    private void addRelicEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/relics/the_acknowledgment", entry(
                "The Acknowledgment",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "The Acknowledgment",
                        "The Acknowledgment is both a mysterious guidebook and an ancient relic.$(br2)" +
                                "Right-clicking it opens this manual, which records relics, stones, scrolls, curses, and hidden knowledge."
                ),
                textPage(
                        "As a Guide",
                        "The Acknowledgment is not consumed and does not need to be equipped.$(br2)" +
                                "Hold it and right-click to read its current knowledge."
                ),
                textPage(
                        "As a Weapon",
                        "The Acknowledgment can also be swung like a weapon.$(br2)" +
                                "It deals modest damage, ignites struck enemies, and can be enchanted."
                ),
                textPage(
                        "Curse Resonance",
                        "When the bearer suffers under the Ring of the Seven Curses, The Acknowledgment softens part of the curse's pain.$(br2)" +
                                "It does not remove the curse, but it makes survival slightly more forgiving."
                ),
                craftingPage("enigmatic_legacy:the_acknowledgment", "Craft The Acknowledgment, then right-click it to open this manual.")
        )));

        futures.add(save(output, "en_us", "entries/relics/cursed_ring", simpleSpotlight(
                "Ring of the Seven Curses",
                "relics",
                "enigmatic_legacy:cursed_ring",
                5,
                "A cursed ring that binds the bearer to seven punishments.$(br2)" +
                        "Many powerful relics answer only to those who endure its burden."
        )));

        futures.add(save(output, "en_us", "entries/relics/iron_ring", recipeEntry(
                "Iron Ring",
                "relics",
                "enigmatic_legacy:iron_ring",
                10,
                "A simple ring base.$(br2)" +
                        "Many stronger rings begin as something this plain.",
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
                "A ring linked to ender storage.$(br2)" +
                        "It grants convenient access to an Ender Chest through its own interface.",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/magnet_ring", recipeEntry(
                "Magnet Ring",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "This ring pulls nearby items toward its bearer.$(br2)" +
                        "It is useful while mining, farming, or fighting around many drops.",
                "enigmatic_legacy:magnet_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/dislocation_ring", recipeEntry(
                "Dislocation Ring",
                "relics",
                "enigmatic_legacy:dislocation_ring",
                50,
                "A stronger magnetic ring.$(br2)" +
                        "Instead of slowly dragging drops, it dislocates nearby items directly into reach.",
                "enigmatic_legacy:dislocation_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/twisted_mirror", recipeEntry(
                "Twisted Mirror",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                60,
                "A mirror that bends return and remembrance.$(br2)" +
                        "It is crafted around a recall potion and a Twisted Heart.",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "en_us", "entries/relics/unholy_grail", simpleSpotlight(
                "Unholy Grail",
                "relics",
                "enigmatic_legacy:unholy_grail",
                70,
                "A relic that turns violence into recovery.$(br2)" +
                        "It rewards aggressive survival, but should not be mistaken for safety."
        )));

        futures.add(save(output, "en_us", "entries/relics/guardian_heart", simpleSpotlight(
                "Guardian Heart",
                "relics",
                "enigmatic_legacy:guardian_heart",
                80,
                "A heart carrying the memory of ancient guardians.$(br2)" +
                        "It is linked to protection, endurance, and the depths."
        )));

        futures.add(save(output, "en_us", "entries/relics/monster_charm", recipeEntry(
                "Emblem of Monster Slayer",
                "relics",
                "enigmatic_legacy:monster_charm",
                90,
                "An emblem devoted to hunting monsters.$(br2)" +
                        "It improves combat against hostile creatures and can grant additional rewards.",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/treasure_hunter_charm", recipeEntry(
                "Charm of Treasure Hunter",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                100,
                "A charm for miners and explorers.$(br2)" +
                        "It improves mining, fortune, and exploration comfort.",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "Emblem of Bloodstained Valor",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                110,
                "An emblem for cursed bearers.$(br2)" +
                        "The closer its wearer is to death, the more violently it answers.",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "en_us", "entries/relics/mega_sponge", recipeEntry(
                "Extrapolated Megasponge",
                "relics",
                "enigmatic_legacy:mega_sponge",
                120,
                "A charm-like sponge that consumes nearby water when its bearer touches it.",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "en_us", "entries/relics/enchanter_pearl", recipeEntry(
                "Enchanter's Pearl",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                130,
                "A pearl useful only to cursed bearers.$(br2)" +
                        "While equipped, it grants an additional charm slot.",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "en_us", "entries/relics/enigmatic_eye", entry(
                "Dormant Eye / Inscrutable Eye",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                140,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "Dormant Eye",
                        "The Eye sleeps until awakened.$(br2)" +
                                "It appears only once for a player, when they first discover the right kind of loot."
                ),
                textPage(
                        "Awakened Sight",
                        "Once awakened, the Eye may be worn as a charm.$(br2)" +
                                "It grants an additional charm slot, increases interaction range, and may give voice to the Watcher."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/extradimensional_eye", simpleSpotlight(
                "Extradimensional Eye",
                "relics",
                "enigmatic_legacy:extradimensional_eye",
                150,
                "A higher eye that peers beyond ordinary space.$(br2)" +
                        "It is connected to additional reach, hidden containers, and stranger perception."
        )));

        futures.add(save(output, "en_us", "entries/relics/enchantment_transposer", simpleSpotlight(
                "Tome of Hungering Knowledge",
                "relics",
                "enigmatic_legacy:enchantment_transposer",
                160,
                "A tome that hungers for enchantments.$(br2)" +
                        "It is used to manipulate enchantment power in ways ordinary books cannot."
        )));

        futures.add(save(output, "en_us", "entries/relics/curse_transposer", simpleSpotlight(
                "Tome of Devoured Malignancy",
                "relics",
                "enigmatic_legacy:curse_transposer",
                170,
                "A tome that devours malignancy.$(br2)" +
                        "It is tied to curses and to those willing to handle cursed knowledge."
        )));

        futures.add(save(output, "en_us", "entries/relics/unwitnessed_amulet", entry(
                "Unwitnessed Amulet",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                180,
                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "Unwitnessed Amulet",
                        "An amulet whose nature is not yet witnessed.$(br2)" +
                                "Use it to reveal one of its enigmatic variants."
                ),
                textPage(
                        "Revealed Forms",
                        "Each revealed amulet bears a different color and power: damage, speed, deflection, gravity, mining, lifesteal, or swimming."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/eldritch_amulet", recipeEntry(
                "Testament of Contempt",
                "relics",
                "enigmatic_legacy:eldritch_amulet",
                185,
                "The Amulet of Ascension may be perfected, but perfection can still be made cruel.$(br2)" +
                        "The Testament of Contempt grants all seven amulet traits, adds raw attack power and lifesteal, weakens creatures caught in your gaze, and preserves your inventory on death. Items marked by Curse of Vanishing are still lost.",
                "enigmatic_legacy:eldritch_amulet"
        )));

        futures.add(save(output, "en_us", "entries/relics/storage_crystal", simpleSpotlight(
                "Extradimensional Vessel",
                "relics",
                "enigmatic_legacy:storage_crystal",
                190,
                "A vessel for a life interrupted.$(br2)" +
                        "It stores what would otherwise be lost when saved by the Seven Curses."
        )));

        futures.add(save(output, "en_us", "entries/relics/soul_crystal", simpleSpotlight(
                "Soul Crystal",
                "relics",
                "enigmatic_legacy:soul_crystal",
                200,
                "A crystallized remnant of a rescued soul, bound to the place and moment of death."
        )));

        futures.add(save(output, "en_us", "entries/relics/forbidden_fruit", simpleSpotlight(
                "Forbidden Fruit",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                210,
                "A fruit whose sweetness promises more than it should.$(br2)" +
                        "Its gift is powerful, but the price should never be ignored."
        )));
    }

    /**
     * 术石条目。
     */
    private void addSpellstoneEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
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
     * 奥秘卷轴条目。
     */
    private void addScrollEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
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

    /**
     * 装备条目。
     */
    private void addEquipmentEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/equipment/axe_of_executioner", recipeEntry(
                "Axe of Executioner",
                "equipment",
                "enigmatic_legacy:axe_of_executioner",
                0,
                "A brutal axe with a chance to behead slain creatures.$(br2)" +
                        "Its base beheading chance is 15%, and each Looting level adds 5% more.",
                "enigmatic_legacy:axe_of_executioner"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_sword", recipeEntry(
                "Etherium Broadsword",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                10,
                "A heavy etherium blade forged for direct combat.",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_pickaxe", recipeEntry(
                "Etherium Pickaxe",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                20,
                "A durable etherium tool for mining hard materials.",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_shovel", recipeEntry(
                "Etherium Shovel",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                30,
                "A shovel forged from etherium for fast excavation.",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_axe", recipeEntry(
                "Etherium Waraxe",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                40,
                "An etherium axe made as both a tool and a weapon.",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_armor", entry(
                "Etherium Armor",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                50,
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
}
