package org.enigmatic_legacy.generator.patchouli;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 启示之证 Patchouli 手册英文内容生成器。
 * 设计目标：
 * 1. 按原项目 The Acknowledgment 的定位，将启示之证作为整个模组的手册入口；
 * 2. 不是只介绍启示之证本身，而是记录世界规则、材料、遗物、术石、卷轴、装备和战利品来源；
 * 3. 覆盖当前项目中主要已注册、可获取或有玩法意义的物品；
 * 4. 内容尽量保持 Patchouli 手册风格：短段落、分类清晰、适合游戏内阅读；
 * 5. JSON 继续由 datagen 生成，不手写静态 JSON。
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
     * 英文手册内容入口。
     */
    @Override
    protected void addContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        addCategories(
                output,
                futures,
                "en_us",

                "World",
                "Records about worldly laws, hidden mechanisms, exploration, and consequences.",

                "Materials",
                "Unusual materials used to craft enigmatic relics, spellstones, scrolls, and equipment.",

                "Relics",
                "Ancient rings, charms, grails, mirrors, fruits, and other enigmatic creations."
        );

        addExtraCategories(output, futures);

        addWorldEntries(output, futures);
        addMaterialEntries(output, futures);
        addRelicEntries(output, futures);
        addSpellstoneEntries(output, futures);
        addScrollEntries(output, futures);
        addEquipmentEntries(output, futures);
    }

    /**
     * 生成额外分类。
     */
    private void addExtraCategories(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "categories/spellstones", category(
                "Spellstones",
                "Powerful relic stones that grant active and passive abilities when equipped. They often define a player's core combat or exploration style.",
                "enigmatic_legacy:golem_heart",
                30
        )));

        futures.add(save(output, "en_us", "categories/scrolls", category(
                "Arcane Scrolls",
                "Scrolls and pacts that alter flight, experience, curses, rewards, and other rules.",
                "enigmatic_legacy:xp_scroll",
                40
        )));

        futures.add(save(output, "en_us", "categories/equipment", category(
                "Equipment",
                "Weapons, tools, armor, shields, and special equipment forged from abnormal materials.",
                "enigmatic_legacy:etherium_sword",
                50
        )));
    }

    /**
     * 世界机制条目。
     */
    private void addWorldEntries(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "entries/world/getting_started", entry(
                "Getting Started",
                "world",
                "enigmatic_legacy:the_acknowledgment",
                0,

                textPage(
                        "Getting Started",
                        "The Acknowledgment is the core guidebook of this project.$(br2)" +
                                "It records materials, relics, spellstones, arcane scrolls, equipment, loot sources, and the mechanics tied to the Ring of the Seven Curses.$(br2)" +
                                "When you do not know what to seek next, begin here."
                ),

                textPage(
                        "Recommended Route",
                        "If this is your first time playing, read in this order:$(br2)" +
                                "$(li)Materials: learn where Astral Dust, Etherium, Hearts, and other components come from.$(br)" +
                                "$(li)Relics: learn about rings, charms, and early utility items.$(br)" +
                                "$(li)Spellstones: choose the core power that fits your style.$(br)" +
                                "$(li)Scrolls: learn about flight, experience, curses, and rewards.$(br)" +
                                "$(li)Equipment: plan mid and late game weapons, tools, and armor.$(br)" +
                                "$(li)Seven Curses: read carefully before accepting the burden."
                )
        )));

        futures.add(save(output, "en_us", "entries/world/soul_loss", entry(
                "Soul Loss",
                "world",
                "minecraft:soul_lantern",
                10,

                textPage(
                        "Soul Loss",
                        "Death is rarely without consequence.$(br2)" +
                                "Some powers may return what was lost, but the world always remembers what you owed.$(br2)" +
                                "Soul Crystals, Extradimensional Vessels, and some Seven Curses items are tied to death recovery and retrieval."
                ),

                textPage(
                        "Hint",
                        "If you lose important items after death, first check whether they were preserved in a Soul Crystal or an Extradimensional Vessel.$(br2)" +
                                "Certain wayfinding items can help cursed bearers locate these containers."
                )
        )));

        futures.add(save(output, "en_us", "entries/world/loot_and_exploration", entry(
                "Loot and Exploration",
                "world",
                "minecraft:chest",
                20,

                textPage(
                        "Loot and Exploration",
                        "Not every item comes from crafting.$(br2)" +
                                "Some materials, spellstones, fruits, scrolls, and high-tier relics appear in structure chests.$(br2)" +
                                "If you cannot find a recipe in the crafting table or JEI, the item may require exploration."
                ),

                textPage(
                        "Common Structures",
                        "You should pay special attention to these structures:$(br2)" +
                                "$(li)Ancient City chests$(br)" +
                                "$(li)End City treasure chests$(br)" +
                                "$(li)Bastion chests$(br)" +
                                "$(li)Ruined Portal chests$(br)" +
                                "$(li)Dungeons and Mineshafts$(br)" +
                                "$(li)Desert Temples and Jungle Temples$(br)" +
                                "$(li)Ocean Ruins, Shipwrecks, and Buried Treasure$(br)" +
                                "$(li)Stronghold corridors and crossings"
                ),

                textPage(
                        "Testing Note",
                        "Structure chests usually generate their contents when first opened.$(br2)" +
                                "If a chest has already been opened, changing loot tables later will not refresh its contents.$(br2)" +
                                "When testing loot, use a new world, newly generated chunks, or the /loot command."
                )
        )));

        futures.add(save(output, "en_us", "entries/world/the_architects_favor", entry(
                "The Architect's Favor",
                "world",
                "minecraft:structure_block",
                30,

                textPage(
                        "The Architect's Favor",
                        "Beyond ordinary crafting and ordinary rewards lies a favor not meant to be seen by everyone.$(br2)" +
                                "It is not part of the usual progression and should not be treated as a normal item route."
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
                "A faintly shimmering abnormal dust, as if it came from the edge of the unknown.$(br2)" +
                        "Many early relics, rings, and enigmatic creations begin with it."
        )));

        futures.add(save(output, "en_us", "entries/materials/astral_dust_sack", simpleSpotlight(
                "Astral Dust Sack",
                "materials",
                "enigmatic_legacy:astral_dust_sack",
                5,
                "A storage block made from compressed Astral Dust.$(br2)" +
                        "Useful for storing large amounts of dust and keeping materials ready for enigmatic crafting."
        )));

        futures.add(save(output, "en_us", "entries/materials/ender_rod", recipeEntry(
                "Ender Rod",
                "materials",
                "enigmatic_legacy:ender_rod",
                10,
                "A rod infused with ender resonance.$(br2)" +
                        "It often serves as a stabilizing component for space, teleportation, and End-related creations.",
                "enigmatic_legacy:ender_rod"
        )));

        futures.add(save(output, "en_us", "entries/materials/thicc_scroll", recipeEntry(
                "Thicc Scroll",
                "materials",
                "enigmatic_legacy:thicc_scroll",
                20,
                "A blank but unusually sturdy scroll.$(br2)" +
                        "It can carry knowledge that ordinary paper would fail to contain, making it an important base for Arcane Scrolls.",
                "enigmatic_legacy:thicc_scroll"
        )));

        futures.add(save(output, "en_us", "entries/materials/darkest_scroll", simpleSpotlight(
                "Darkest Scroll",
                "materials",
                "enigmatic_legacy:darkest_scroll",
                25,
                "A scroll soaked in deep darkness.$(br2)" +
                        "It is not an equipable Arcane Scroll by itself, but an important material for more dangerous crafting paths.$(br2)" +
                        "The Darkest Scroll can only be found in Ancient City chests."
        )));

        futures.add(save(output, "en_us", "entries/materials/ichor_droplet", entry(
                "Ichor Droplet",
                "materials",
                "enigmatic_legacy:ichor_droplet",
                26,

                spotlightPage(
                        "enigmatic_legacy:ichor_droplet",
                        "Ichor Droplet",
                        "A Nether material known as the “blood of gods.”$(br2)" +
                                "It can usually be found in most Nether Fortress and Bastion loot chests."
                ),

                textPage(
                        "Nether Loot",
                        "Ichor Droplets can be found in most Nether loot chests.$(br2)" +
                                "Search these places first:$(br2)" +
                                "$(li)Nether Fortress chests$(br)" +
                                "$(li)Bastion treasure chests$(br)" +
                                "$(li)Bastion common chests$(br)" +
                                "$(li)Bastion bridge chests$(br)" +
                                "$(li)Bastion hoglin stable chests"
                ),

                textPage(
                        "Seven Curses Drop",
                        "When a player wearing the Ring of the Seven Curses kills a Ghast, the Ghast drops additional Ichor Droplets.$(br2)" +
                                "The curse awakens the faint divinity remaining in its blood and tears, allowing Ichor to appear again.$(br2)" +
                                "This is one of the more stable ways to obtain it."
                ),

                textPage(
                        "Use",
                        "The Ichor Droplet is a material, not an equipable relic or scroll.$(br2)" +
                                "It will be used in future crafting paths related to purification, divinity, the Nether, special potions, and higher creations."
                )
        )));

        futures.add(save(output, "en_us", "entries/materials/pure_heart", entry(
                "Pure Heart",
                "materials",
                "enigmatic_legacy:pure_heart",
                27,

                spotlightPage(
                        "enigmatic_legacy:pure_heart",
                        "Pure Heart",
                        "A strange heart born from hidden dark pollution and faint divinity.$(br2)" +
                                "It appears bright and pure, yet still requires the power of curses to be truly understood."
                ),

                textPage(
                        "Materials",
                        "The Pure Heart is crafted from a Heart of the Earth, Ichor Droplets, a Ghast Tear, Glowstone Dust, and an Eye of Ender.$(br2)" +
                                "It can be seen as another direction from the Twisted Heart: not deeper malice, but an attempt to turn pollution toward purification."
                ),

                textPage(
                        "Purification Path",
                        "The Pure Heart is not an equipable relic or an Arcane Scroll by itself.$(br2)" +
                                "It is an important material for later purification, blessing, and redemption progression.$(br2)" +
                                "Although it is called pure, that purity still stands upon curses."
                ),

                craftingPage(
                        "enigmatic_legacy:pure_heart",
                        "The crafting recipe for the Pure Heart."
                )
        )));

        futures.add(save(output, "en_us", "entries/materials/etherium_ore", simpleSpotlight(
                "Etherium Ore",
                "materials",
                "enigmatic_legacy:etherium_ore",
                30,
                "A rare ore carrying etherium energy.$(br2)" +
                        "It usually appears as a high-tier exploration reward rather than a normal underground vein.$(br2)" +
                        "Smelting it yields Etherium Ingots."
        )));

        futures.add(save(output, "en_us", "entries/materials/etherium_ingot", entry(
                "Etherium Ingot",
                "materials",
                "enigmatic_legacy:etherium_ingot",
                40,

                spotlightPage(
                        "enigmatic_legacy:etherium_ingot",
                        "Etherium Ingot",
                        "A refined material used in advanced relic construction.$(br2)" +
                                "It is commonly used to craft Etherium tools, weapons, and armor."
                ),

                craftingPage(
                        "enigmatic_legacy:etherium_block_uncrafting",
                        "Etherium blocks can be broken back down into ingots."
                )
        )));

        futures.add(save(output, "en_us", "entries/materials/etherium_block", simpleSpotlight(
                "Block of Etherium",
                "materials",
                "enigmatic_legacy:etherium_block",
                45,
                "A high-tier material block made from Etherium Ingots.$(br2)" +
                        "It can be used for storage and broken back down into ingots."
        )));

        futures.add(save(output, "en_us", "entries/materials/cosmic_heart", recipeEntry(
                "Cosmic Heart",
                "materials",
                "enigmatic_legacy:cosmic_heart",
                50,
                "A heart-shaped concentration of cosmic potential.$(br2)" +
                        "It is an important ingredient for relics tied to space, distance, teleportation, and higher artifacts.",
                "enigmatic_legacy:cosmic_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/earth_heart_fragment", simpleSpotlight(
                "Heart of the Earth Fragment",
                "materials",
                "enigmatic_legacy:earth_heart_fragment",
                55,
                "A broken fragment of the Heart of the Earth.$(br2)" +
                        "Enough fragments can be gathered and formed back into a complete heart."
        )));

        futures.add(save(output, "en_us", "entries/materials/earth_heart", recipeEntry(
                "Heart of the Earth",
                "materials",
                "enigmatic_legacy:earth_heart",
                60,
                "Fragments of the earth gathered into a single heart, pulsing with buried vitality.$(br2)" +
                        "Many defensive relics, spellstones, and equipment pieces require it.",
                "enigmatic_legacy:earth_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/twisted_heart", recipeEntry(
                "Twisted Heart",
                "materials",
                "enigmatic_legacy:twisted_heart",
                70,
                "A heart distorted by hostile energies.$(br2)" +
                        "It is dangerous, but many relics demand dangerous components.",
                "enigmatic_legacy:twisted_heart"
        )));

        futures.add(save(output, "en_us", "entries/materials/abyssal_heart", simpleSpotlight(
                "Abyssal Heart",
                "materials",
                "enigmatic_legacy:abyssal_heart",
                75,
                "A heart-shaped material touched by abyssal power.$(br2)" +
                        "It is often tied to the void, malice, and higher relics."
        )));

        futures.add(save(output, "en_us", "entries/materials/evil_essence", simpleSpotlight(
                "Evil Essence",
                "materials",
                "enigmatic_legacy:evil_essence",
                80,
                "Condensed malice given form.$(br2)" +
                        "It is unstable, corruptive, and useful."
        )));

        futures.add(save(output, "en_us", "entries/materials/evil_ingot", recipeEntry(
                "Evil Ingot",
                "materials",
                "enigmatic_legacy:evil_ingot",
                90,
                "An ingot forged from essence too malicious to be left uncontained.$(br2)" +
                        "It is often used in curse, malice, and high-tier relic progression.",
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
                                "Right-clicking it opens this manual, which records relics, spellstones, scrolls, curses, equipment, and hidden knowledge."
                ),

                textPage(
                        "What Does It Record?",
                        "The Acknowledgment records most of the project's core content:$(br2)" +
                                "$(li)Basic and rare materials$(br)" +
                                "$(li)Rings, charms, grails, mirrors, and fruits$(br)" +
                                "$(li)Active and passive spellstone powers$(br)" +
                                "$(li)Arcane scrolls and pacts$(br)" +
                                "$(li)Etherium equipment, special weapons, and shields$(br)" +
                                "$(li)Restrictions and rewards related to the Ring of the Seven Curses$(br)" +
                                "$(li)Exploration tips and loot sources"
                ),

                textPage(
                        "As a Guide",
                        "The Acknowledgment is not consumed and does not need to be equipped in a Curios slot.$(br2)" +
                                "Hold it and right-click to read the knowledge currently recorded within it.$(br2)" +
                                "When you obtain an unfamiliar item, search this book for an entry with the same name."
                ),

                textPage(
                        "As a Weapon",
                        "The Acknowledgment can also be swung like a weapon.$(br2)" +
                                "It deals modest damage, ignites struck enemies, and can be enchanted.$(br2)" +
                                "It is not a strong combat weapon, but it can protect you during early exploration."
                ),

                textPage(
                        "Curse Resonance",
                        "When its bearer suffers under the Ring of the Seven Curses, The Acknowledgment resonates with that burden.$(br2)" +
                                "It cannot remove the curses, nor can it make them safe.$(br2)" +
                                "It merely gives the bearer a little more room to continue through the pain."
                ),

                craftingPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "Craft The Acknowledgment, then right-click it to open this manual."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/cursed_ring", entry(
                "Ring of the Seven Curses",
                "relics",
                "enigmatic_legacy:cursed_ring",
                5,

                spotlightPage(
                        "enigmatic_legacy:cursed_ring",
                        "Ring of the Seven Curses",
                        "A cursed ring that binds its bearer to seven layers of punishment.$(br2)" +
                                "It is not an ordinary accessory. It is a dangerous path."
                ),

                textPage(
                        "Seven Costs",
                        "The Ring of the Seven Curses continuously punishes its bearer.$(br2)" +
                                "These punishments make combat, survival, recovery, item preservation, and exploration far more dangerous.$(br2)" +
                                "Yet many powerful relics only answer to those capable of bearing its burden."
                ),

                textPage(
                        "Advice",
                        "Do not wear it without understanding the consequences.$(br2)" +
                                "When an item requires the Seven Curses, or a certain percentage of time under their torment, its power usually comes with severe risk."
                )
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
                "A refined ring with a more precious frame.$(br2)" +
                        "It is suitable for enchantment, transformation, or use as a material for other rings.",
                "enigmatic_legacy:golden_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/ender_ring", recipeEntry(
                "Ender Ring",
                "relics",
                "enigmatic_legacy:ender_ring",
                30,
                "A ring linked to ender storage.$(br2)" +
                        "When worn, it allows convenient access to an Ender Chest.",
                "enigmatic_legacy:ender_ring"
        )));

        futures.add(save(output, "en_us", "entries/relics/magnet_ring", recipeEntry(
                "Magnet Ring",
                "relics",
                "enigmatic_legacy:magnet_ring",
                40,
                "This ring pulls nearby item drops toward its bearer.$(br2)" +
                        "It is useful while mining, farming, fighting, or cleaning up large amounts of loot.",
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

        futures.add(save(output, "en_us", "entries/relics/magic_quartz_ring", entry(
                "Magic Quartz Ring",
                "relics",
                "enigmatic_legacy:magic_quartz_ring",
                60,

                spotlightPage(
                        "enigmatic_legacy:magic_quartz_ring",
                        "Magic Quartz Ring",
                        "A ring made from quartz and magical materials.$(br2)" +
                                "When worn, it improves armor and luck while weakening certain kinds of magical damage."
                ),

                textPage(
                        "Worn Effects",
                        "The Magic Quartz Ring grants:$(br2)" +
                                "$(li)+2 Armor$(br)" +
                                "$(li)+1.5 Luck$(br)" +
                                "$(li)+30% Magic Damage Resistance$(br2)" +
                                "It works against vanilla magic, wither, dragon breath, and compatible magic damage from some other mods."
                ),

                textPage(
                        "Limit",
                        "Only one Magic Quartz Ring can be worn at a time.$(br2)" +
                                "Even if you have multiple ring slots, you cannot stack its effect by equipping several copies."
                ),

                craftingPage(
                        "enigmatic_legacy:magic_quartz_ring",
                        "The crafting recipe for the Magic Quartz Ring."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/twisted_mirror", recipeEntry(
                "Twisted Mirror",
                "relics",
                "enigmatic_legacy:twisted_mirror",
                70,
                "A mirror that bends return and remembrance.$(br2)" +
                        "It is crafted around a recall potion and a Twisted Heart.",
                "enigmatic_legacy:twisted_mirror"
        )));

        futures.add(save(output, "en_us", "entries/relics/recall_potion", simpleSpotlight(
                "Potion of Recall",
                "relics",
                "enigmatic_legacy:recall_potion",
                80,
                "A potion that attempts to return the user to a safe or bound location.$(br2)" +
                        "It is often used in recipes related to returning, teleportation, and the Twisted Mirror."
        )));

        futures.add(save(output, "en_us", "entries/relics/redemption_potion", simpleSpotlight(
                "Potion of Redemption",
                "relics",
                "enigmatic_legacy:redemption_potion",
                90,
                "A potion carrying the force of redemption.$(br2)" +
                        "It is uncommon and is usually obtained through higher exploration rewards."
        )));

        futures.add(save(output, "en_us", "entries/relics/mending_mixture", simpleSpotlight(
                "Mending Mixture",
                "relics",
                "enigmatic_legacy:mending_mixture",
                100,
                "A special mixture used to repair equipment.$(br2)" +
                        "It can fully repair damaged damageable items in a crafting grid."
        )));

        futures.add(save(output, "en_us", "entries/relics/unholy_grail", simpleSpotlight(
                "Unholy Grail",
                "relics",
                "enigmatic_legacy:unholy_grail",
                110,
                "A relic that turns violence into recovery.$(br2)" +
                        "It rewards aggressive survival, but should not be mistaken for safety."
        )));

        futures.add(save(output, "en_us", "entries/relics/animal_guidebook", entry(
                "Guide to Animal Companionship",
                "relics",
                "enigmatic_legacy:animal_guidebook",
                115,

                spotlightPage(
                        "enigmatic_legacy:animal_guidebook",
                        "Guide to Animal Companionship",
                        "A guidebook recording the knowledge of animal companionship.$(br2)" +
                                "As long as it is carried in the inventory, main hand, or off hand, it helps protect certain animals."
                ),

                textPage(
                        "Animal Protection",
                        "The guidebook protects certain animals, especially ordinary animals and some tamable creatures.$(br2)" +
                                "It also weakens the impact of the second curse of the Ring of the Seven Curses on tamable animals."
                ),

                textPage(
                        "Creative Check",
                        "Creative mode players can right-click a creature with this book to check whether it is treated as a tamable animal.$(br2)" +
                                "This is mainly useful for debugging and compatibility checks."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/hunter_guidebook", entry(
                "Guide to Feral Hunt",
                "relics",
                "enigmatic_legacy:hunter_guidebook",
                116,

                spotlightPage(
                        "enigmatic_legacy:hunter_guidebook",
                        "Guide to Feral Hunt",
                        "A guidebook recording wild hunting and companion coordination.$(br2)" +
                                "As long as it is carried in the inventory, main hand, or off hand, it changes how nearby pets receive damage."
                ),

                textPage(
                        "Damage Transfer",
                        "When nearby pets take damage, the Guide to Feral Hunt attempts to transfer part of that danger to their owner.$(br2)" +
                                "This can protect pets, but it also means the owner must accept greater risk."
                ),

                textPage(
                        "Synergy",
                        "When used together with the Guide to Animal Companionship, the transferred damage receives additional reduction.$(br2)" +
                                "It is useful for players who often fight or explore with pets."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/sanguinary_handbook", entry(
                "Sanguinary Hunting Handbook",
                "relics",
                "enigmatic_legacy:sanguinary_handbook",
                117,

                spotlightPage(
                        "enigmatic_legacy:sanguinary_handbook",
                        "Sanguinary Hunting",
                        "After some time, you will certainly feel that your followers' strength has fallen far behind yours, while the protection provided by the Guide to Feral Hunt is no longer enough.$(br2)" +
                                "So, you found a new use for Twisted Hearts."
                ),

                textPage(
                        "Violent Doctrine",
                        "This handbook lets your followers master new abilities and receive part of the violent power carried by you.$(br2)" +
                                "Let them listen to the proverbs in the handbook and dedicate their lives without hesitation."
                ),

                craftingPage(
                        "enigmatic_legacy:sanguinary_handbook",
                        "The handbook is crafted by strengthening the Guide to Feral Hunt with Twisted Heart, netherite, ghast tears, dragon breath, and phantom membranes."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/guardian_heart", simpleSpotlight(
                "Guardian Heart",
                "relics",
                "enigmatic_legacy:guardian_heart",
                120,
                "A heart carrying the memory of ancient guardians.$(br2)" +
                        "It is linked to protection, endurance, and the depths."
        )));

        futures.add(save(output, "en_us", "entries/relics/monster_charm", recipeEntry(
                "Emblem of Monster Slayer",
                "relics",
                "enigmatic_legacy:monster_charm",
                130,
                "An emblem devoted to hunting monsters.$(br2)" +
                        "It improves combat against hostile creatures and can grant additional rewards.",
                "enigmatic_legacy:monster_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/treasure_hunter_charm", recipeEntry(
                "Charm of Treasure Hunter",
                "relics",
                "enigmatic_legacy:treasure_hunter_charm",
                140,
                "A charm for miners and explorers.$(br2)" +
                        "It improves mining, fortune, and exploration comfort.",
                "enigmatic_legacy:treasure_hunter_charm"
        )));

        futures.add(save(output, "en_us", "entries/relics/bloodstained_valor_emblem", recipeEntry(
                "Emblem of Bloodstained Valor",
                "relics",
                "enigmatic_legacy:bloodstained_valor_emblem",
                150,
                "An emblem for cursed bearers.$(br2)" +
                        "The closer its wearer is to death, the more violently it answers.",
                "enigmatic_legacy:bloodstained_valor_emblem"
        )));

        futures.add(save(output, "en_us", "entries/relics/scorched_charm", entry(
                "Charm of Scorched Sun",
                "relics",
                "enigmatic_legacy:scorched_charm",
                155,

                spotlightPage(
                        "enigmatic_legacy:scorched_charm",
                        "Charm of Scorched Sun",
                        "A high-tier charm scorched by sunfire and lava.$(br2)" +
                                "It turns the Blazing Core's fire adaptation into a steadier blessing of survival."
                ),

                textPage(
                        "Fire and Lava",
                        "The Charm of Scorched Sun grants immunity to most fire damage, including fire, burning, lava, and hot floors.$(br2)" +
                                "It allows you to walk on lava. If you want to dive into lava, crouch."
                ),

                textPage(
                        "Lava Movement",
                        "The Charm of Scorched Sun now allows its bearer to swim in lava.$(br2)" +
                                "It also grants the same improved lava visibility as the Blazing Core, making it useful for exploring Nether lava lakes."
                ),

                textPage(
                        "Healing and Lifesteal",
                        "While touching lava, the charm restores 2 health every second.$(br2)" +
                                "When you attack a burning target, it restores 20% of the damage dealt.$(br2)" +
                                "It rewards fighting within fire rather than fleeing from it."
                ),

                textPage(
                        "Damage Resistance",
                        "While worn, the charm has a 10% chance to completely resist incoming damage.$(br2)" +
                                "While touching lava, this chance doubles to 20%.$(br2)" +
                                "The protection is unreliable, but it may save your life at the worst moment."
                ),

                craftingPage(
                        "enigmatic_legacy:scorched_charm",
                        "The crafting recipe for the Charm of Scorched Sun."
                )
        )));
        futures.add(save(output, "en_us", "entries/relics/mega_sponge", recipeEntry(
                "Extrapolated Megasponge",
                "relics",
                "enigmatic_legacy:mega_sponge",
                160,
                "A charm-like sponge.$(br2)" +
                        "When its bearer touches water, it attempts to consume nearby water blocks.",
                "enigmatic_legacy:mega_sponge"
        )));

        futures.add(save(output, "en_us", "entries/relics/enchanter_pearl", recipeEntry(
                "Enchanter's Pearl",
                "relics",
                "enigmatic_legacy:enchanter_pearl",
                170,
                "A pearl useful only to cursed bearers.$(br2)" +
                        "While equipped, it grants an additional charm slot.",
                "enigmatic_legacy:enchanter_pearl"
        )));

        futures.add(save(output, "en_us", "entries/relics/enigmatic_eye", entry(
                "Dormant Eye / Inscrutable Eye",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                180,

                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "Dormant Eye",
                        "This eye sleeps until awakened.$(br2)" +
                                "It appears only once for a player, usually when they first discover the right kind of loot."
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
                190,
                "A higher eye that peers beyond ordinary space.$(br2)" +
                        "It is connected to additional reach, hidden containers, and stranger perception."
        )));

        futures.add(save(output, "en_us", "entries/relics/enchantment_transposer", simpleSpotlight(
                "Tome of Hungering Knowledge",
                "relics",
                "enigmatic_legacy:enchantment_transposer",
                200,
                "A tome that hungers for enchantments.$(br2)" +
                        "It can manipulate enchantment power in ways ordinary books cannot."
        )));

        futures.add(save(output, "en_us", "entries/relics/curse_transposer", simpleSpotlight(
                "Tome of Devoured Malignancy",
                "relics",
                "enigmatic_legacy:curse_transposer",
                210,
                "A tome that devours malignancy.$(br2)" +
                        "It is tied to curses and to those willing to handle cursed knowledge."
        )));

        futures.add(save(output, "en_us", "entries/relics/unwitnessed_amulet", entry(
                "Unwitnessed Amulet",
                "relics",
                "enigmatic_legacy:unwitnessed_amulet",
                220,

                spotlightPage(
                        "enigmatic_legacy:unwitnessed_amulet",
                        "Unwitnessed Amulet",
                        "An amulet whose nature has not yet been witnessed.$(br2)" +
                                "Using it reveals one of its enigmatic variants."
                ),

                textPage(
                        "Revealed Forms",
                        "Each revealed amulet has a different color and power:$(br2)" +
                                "$(li)Red: Attack Damage$(br)" +
                                "$(li)Aqua: Sprint Speed$(br)" +
                                "$(li)Violet: Projectile Deflection$(br)" +
                                "$(li)Magenta: Gravity Change$(br)" +
                                "$(li)Green: Mining Efficiency$(br)" +
                                "$(li)Black: Lifesteal$(br)" +
                                "$(li)Blue: Swim Speed"
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/enigmatic_amulets", withExtraRecipeMappings(entry(
                        "Seven Enigmatic Amulets",
                        "relics",
                        "enigmatic_legacy:enigmatic_amulet_red",
                        230,

                        spotlightPage(
                                "enigmatic_legacy:enigmatic_amulet_red,enigmatic_legacy:enigmatic_amulet_aqua,enigmatic_legacy:enigmatic_amulet_violet,enigmatic_legacy:enigmatic_amulet_magenta,enigmatic_legacy:enigmatic_amulet_green,enigmatic_legacy:enigmatic_amulet_black,enigmatic_legacy:enigmatic_amulet_blue",
                                "Seven Enigmatic Amulets",
                                "When the Unwitnessed Amulet is witnessed, it reveals one of seven colored Enigmatic Amulets.$(br2)" +
                                        "Each color represents a different kind of power."
                        ),

                        textPage(
                                "Seven Forms",
                                "The seven forms are:$(br2)" +
                                        "$(li)Red: Attack Damage$(br)" +
                                        "$(li)Aqua: Sprint Speed$(br)" +
                                        "$(li)Violet: Projectile Deflection$(br)" +
                                        "$(li)Magenta: Gravity Change$(br)" +
                                        "$(li)Green: Mining Efficiency$(br)" +
                                        "$(li)Black: Lifesteal$(br)" +
                                        "$(li)Blue: Swim Speed"
                        ),

                        textPage(
                                "Progression",
                                "All seven colored amulets can be gathered into the Amulet of Ascension.$(br2)" +
                                        "The Amulet of Ascension inherits all seven powers and leads toward the more dangerous Testament of Contempt."
                        )
                ), 0,
                "enigmatic_legacy:enigmatic_amulet_red",
                "enigmatic_legacy:enigmatic_amulet_aqua",
                "enigmatic_legacy:enigmatic_amulet_violet",
                "enigmatic_legacy:enigmatic_amulet_magenta",
                "enigmatic_legacy:enigmatic_amulet_green",
                "enigmatic_legacy:enigmatic_amulet_black",
                "enigmatic_legacy:enigmatic_amulet_blue"
        )));

        futures.add(save(output, "en_us", "entries/relics/ascension_amulet", entry(
                "Amulet of Ascension",
                "relics",
                "enigmatic_legacy:ascension_amulet",
                240,

                spotlightPage(
                        "enigmatic_legacy:ascension_amulet",
                        "Amulet of Ascension",
                        "A higher amulet formed from all seven Enigmatic Amulets.$(br2)" +
                                "It inherits the powers of all seven colors."
                ),

                textPage(
                        "Worn Effects",
                        "The Amulet of Ascension grants bonuses related to attack, speed, projectile deflection, gravity, mining, lifesteal, and swimming.$(br2)" +
                                "It is the completed form of the Enigmatic Amulet path and a key step toward the Testament of Contempt."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/eldritch_amulet", recipeEntry(
                "Testament of Contempt",
                "relics",
                "enigmatic_legacy:eldritch_amulet",
                250,
                "The Amulet of Ascension may be perfected, but perfection can still be made cruel.$(br2)" +
                        "The Testament of Contempt grants all seven amulet traits, adds raw attack power and lifesteal, weakens creatures caught in your gaze, and preserves your inventory on death. Items marked by Curse of Vanishing are still lost.",
                "enigmatic_legacy:eldritch_amulet"
        )));

        futures.add(save(output, "en_us", "entries/relics/storage_crystal", simpleSpotlight(
                "Extradimensional Vessel",
                "relics",
                "enigmatic_legacy:storage_crystal",
                260,
                "A vessel for a life interrupted.$(br2)" +
                        "It stores what would otherwise be lost when saved by the Seven Curses."
        )));

        futures.add(save(output, "en_us", "entries/relics/soul_crystal", simpleSpotlight(
                "Soul Crystal",
                "relics",
                "enigmatic_legacy:soul_crystal",
                270,
                "A crystallized remnant of a rescued soul.$(br2)" +
                        "It is bound to the place and moment of death."
        )));

        futures.add(save(output, "en_us", "entries/relics/wayfinder_of_the_damned", simpleSpotlight(
                "Wayfinder of the Damned",
                "relics",
                "enigmatic_legacy:wayfinder_of_the_damned",
                280,
                "A wayfinder that answers only to those suffering under the Seven Curses.$(br2)" +
                        "It can help locate the nearest Soul Crystal or Extradimensional Vessel containing death drops in the current dimension."
        )));

        futures.add(save(output, "en_us", "entries/relics/forbidden_fruit", simpleSpotlight(
                "Forbidden Fruit",
                "relics",
                "enigmatic_legacy:forbidden_fruit",
                290,
                "A fruit whose sweetness promises more than it should.$(br2)" +
                        "Its gift is powerful, but the price should never be ignored."
        )));

        futures.add(save(output, "en_us", "entries/relics/astral_fruit", entry(
                "Astral Fruit",
                "relics",
                "enigmatic_legacy:astral_fruit",
                300,

                spotlightPage(
                        "enigmatic_legacy:astral_fruit",
                        "Astral Fruit",
                        "A rare fruit carrying starlight power.$(br2)" +
                                "Eating it grants a set of powerful temporary effects."
                ),

                textPage(
                        "Base Effects",
                        "Eating Astral Fruit grants its base effects regardless of whether the player wears the Ring of the Seven Curses.$(br2)" +
                                "These effects are useful before dangerous combat, exploration, or escape."
                ),

                textPage(
                        "Seven Curses Resonance",
                        "If the player eats Astral Fruit while wearing the Ring of the Seven Curses, and has never absorbed its starlight before, it permanently grants 1 additional ring slot.$(br2)" +
                                "This slot increase can only happen once per player.$(br2)" +
                                "Without the Ring of the Seven Curses, or on later uses, the fruit only grants its base effects."
                )
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
                                "It helps the bearer move, fall, and explore more safely."
                ),

                textPage(
                        "Use",
                        "It is best used when traveling through cliffs, mountains, floating islands, or other dangerous vertical spaces.$(br2)" +
                                "If terrain and falling often put you in danger, this stone is extremely useful."
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
                        "A water-aspected spellstone made for underwater exploration.$(br2)" +
                                "It improves movement and survival in water, but its power is opposed by flame."
                ),

                textPage(
                        "Warning",
                        "Fire and heat become far more threatening while relying on this stone.$(br2)" +
                                "Be especially careful in the Nether, near lava lakes, or around blazes."
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
                        "A spellstone tied to fire, lava, and the Nether.$(br2)" +
                                "It grants strong adaptation to extreme heat and allows the bearer to move through lava."
                ),

                textPage(
                        "Lava Adaptation",
                        "While wearing the Blazing Core, you are immune to most ordinary fire damage and gain temporary protection in lava.$(br2)" +
                                "It also improves visibility in lava and allows you to swim through it."
                ),

                textPage(
                        "Overheat",
                        "Do not mistake resistance for invulnerability.$(br2)" +
                                "If the Blazing Core is pushed too far, the bearer may still suffer the consequences of fire and lava.$(br2)" +
                                "Damage from aquatic creatures also becomes more dangerous."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/forgotten_ice", entry(
                "Forgotten Ice Crystal",
                "spellstones",
                "enigmatic_legacy:forgotten_ice",
                34,

                spotlightPage(
                        "enigmatic_legacy:forgotten_ice",
                        "Forgotten Ice Crystal",
                        "A cold spellstone crystallized from forgotten frost.$(br2)" +
                                "It stores the memory of freezing, turning prolonged ice into harder imprisonment."
                ),

                textPage(
                        "Active Power",
                        "Activating the crystal releases a freezing wave around you.$(br2)" +
                                "Nearby creatures take freeze damage and receive additional frozen ticks. Fully frozen targets suffer even more damage."
                ),

                textPage(
                        "Frozen Memory",
                        "The crystal remembers creatures kept fully frozen near its bearer.$(br2)" +
                                "If a target remains fully frozen long enough, it becomes hard frozen and can barely move."
                ),

                textPage(
                        "Combat Effects",
                        "Your melee attacks freeze targets more quickly, and fully frozen enemies take increased damage from you.$(br2)" +
                                "When melee attackers strike you, the crystal answers with freezing backlash."
                ),

                textPage(
                        "Weakness",
                        "Forgotten frost resists freezing, projectiles, and sonic force.$(br2)" +
                                "However, fire and falling become far more dangerous while this spellstone is worn."
                )
        )));

        futures.add(save(output, "en_us", "entries/spellstones/revival_leaf", entry(
                "Revival Leaves",
                "spellstones",
                "enigmatic_legacy:revival_leaf",
                35,

                spotlightPage(
                        "enigmatic_legacy:revival_leaf",
                        "Revival Leaves",
                        "A nature-aspected spellstone overflowing with restorative life force.$(br2)" +
                                "It turns experience into regeneration, cleanses withering flowers, and answers nearby plant life."
                ),

                textPage(
                        "Active Power",
                        "Activating Revival Leaves consumes experience and grants regeneration to nearby creatures.$(br2)" +
                                "It also purifies nearby Wither Roses, turning them into Poppies."
                ),

                textPage(
                        "Passive Growth",
                        "While worn, it slowly restores health, removes Hunger, Poison, and Wither, and accelerates nearby crops.$(br2)" +
                                "Your attacks poison targets and reduce their healing while poisoned."
                ),

                textPage(
                        "Plant Flight",
                        "When plant blocks are nearby, Revival Leaves grants flight.$(br2)" +
                                "Leaving plant life behind will soon revoke this flight. It does not override stronger flight relics."
                ),

                textPage(
                        "Weakness",
                        "Its life force is vulnerable to burning and piercing force.$(br2)" +
                                "Fire and projectile damage against you are increased while this spellstone is worn."
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
                                "Use it carefully. Non-Euclidean travel does not promise comfort."
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
                        "A supreme spellstone tied to creation and survival.$(br2)" +
                                "It grants flight, resists many forms of damage, and calls lightning down upon nearby enemies."
                ),

                textPage(
                        "Immortality",
                        "When carried or worn, it can keep the bearer alive at the edge of death.$(br2)" +
                                "This protection is powerful, but it does not mean recklessness is safe."
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
                        "An arcane scroll used to store experience.$(br2)" +
                                "When enabled, it can absorb nearby experience and later return stored experience to its holder."
                ),

                textPage(
                        "Use",
                        "Use Shift + Right Click to switch modes.$(br2)" +
                                "It is useful while mining, enchanting, or preparing for dangerous combat."
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
                        "A scroll that grants flight while within beacon range.$(br2)" +
                                "Flying consumes experience and can prevent fall damage while its conditions are met."
                ),

                textPage(
                        "Beacon Limit",
                        "Leaving beacon range removes flight and briefly grants Slow Falling.$(br2)" +
                                "If Slow Falling ends before you land, gravity will reclaim its price."
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
                        "A scroll usable only by those who endure the Seven Curses.$(br2)" +
                                "Its bonuses scale with the number of curse enchantments on your equipment."
                ),

                textPage(
                        "Growth",
                        "It improves attack, mining speed, and life regeneration.$(br2)" +
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
                        "A higher flight scroll.$(br2)" +
                                "It allows flight anywhere, but continuously consumes experience outside beacon range."
                ),

                textPage(
                        "Limit",
                        "It cannot be worn together with Gift of the Heaven.$(br2)" +
                                "Both grant flight, but their gifts do not stack with each other."
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
                        "A pact of greed offered to the cursed.$(br2)" +
                                "It improves fortune, affects piglins, increases rewards, and makes trading more favorable."
                ),

                textPage(
                        "Greed",
                        "When the curse is deep enough, material rewards can be doubled.$(br2)" +
                                "Killed creatures may drop emeralds, and villagers may offer lower prices."
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
                "A cruel axe with a chance to behead slain enemies.$(br2)" +
                        "Its base beheading chance is 15%, and each level of Looting adds another 5%.",
                "enigmatic_legacy:axe_of_executioner"
        )));

        futures.add(save(output, "en_us", "entries/equipment/ender_slayer", simpleSpotlight(
                "The Ender Slayer",
                "equipment",
                "enigmatic_legacy:ender_slayer",
                10,
                "A Seven Curses relic weapon made to fight End creatures.$(br2)" +
                        "It is especially dangerous against End-related enemies and is worth preparing before exploring the End."
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_sword", recipeEntry(
                "Etherium Broadsword",
                "equipment",
                "enigmatic_legacy:etherium_sword",
                20,
                "A heavy Etherium blade forged for direct combat.$(br2)" +
                        "It has strong durability and stable melee performance.",
                "enigmatic_legacy:etherium_sword"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_pickaxe", recipeEntry(
                "Etherium Pickaxe",
                "equipment",
                "enigmatic_legacy:etherium_pickaxe",
                30,
                "A durable pickaxe forged from Etherium.$(br2)" +
                        "It is suitable for mining hard materials and can receive normal tool enchantments.",
                "enigmatic_legacy:etherium_pickaxe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_shovel", recipeEntry(
                "Etherium Shovel",
                "equipment",
                "enigmatic_legacy:etherium_shovel",
                40,
                "A shovel forged from Etherium.$(br2)" +
                        "It is used to quickly dig dirt, gravel, sand, and similar blocks.",
                "enigmatic_legacy:etherium_shovel"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_axe", recipeEntry(
                "Etherium Axe",
                "equipment",
                "enigmatic_legacy:etherium_axe",
                50,
                "An Etherium waraxe that serves as both tool and weapon.$(br2)" +
                        "It is useful for chopping wood and can also serve as reliable melee equipment.",
                "enigmatic_legacy:etherium_axe"
        )));

        futures.add(save(output, "en_us", "entries/equipment/etherium_armor", entry(
                "Etherium Armor",
                "equipment",
                "enigmatic_legacy:etherium_chestplate",
                60,

                spotlightPage(
                        "enigmatic_legacy:etherium_chestplate",
                        "Etherium Armor",
                        "A complete armor set forged from Etherium.$(br2)" +
                                "It is suitable for late-game protection and naturally pairs with other high-tier relics."
                ),

                craftingPage("enigmatic_legacy:etherium_helmet", "The recipe for the Etherium Helmet."),
                craftingPage("enigmatic_legacy:etherium_chestplate", "The recipe for the Etherium Chestplate."),
                craftingPage("enigmatic_legacy:etherium_leggings", "The recipe for the Etherium Leggings."),
                craftingPage("enigmatic_legacy:etherium_boots", "The recipe for the Etherium Boots.")
        )));

        futures.add(save(output, "en_us", "entries/equipment/the_twist", simpleSpotlight(
                "The Twist",
                "equipment",
                "enigmatic_legacy:the_twist",
                70,
                "A twisted form of The Acknowledgment shaped by the Seven Curses.$(br2)" +
                        "It is both a book and a weapon, and it answers only to those who endure the curses."
        )));

        futures.add(save(output, "en_us", "entries/equipment/the_infinitum", simpleSpotlight(
                "The Infinitum",
                "equipment",
                "enigmatic_legacy:the_infinitum",
                80,
                "A deeper, higher form beyond The Twist.$(br2)" +
                        "It carries knowledge and violence that seem almost endless."
        )));

        futures.add(save(output, "en_us", "entries/equipment/majestic_elytra", simpleSpotlight(
                "Majestic Elytra",
                "equipment",
                "enigmatic_legacy:majestic_elytra",
                90,
                "An elytra strengthened by enigmatic power.$(br2)" +
                        "It is useful for long-distance exploration and can be used alongside higher equipment progression."
        )));

        futures.add(save(output, "en_us", "entries/equipment/bulwark_of_blazing_pride", simpleSpotlight(
                "Bulwark of Blazing Pride",
                "equipment",
                "enigmatic_legacy:infernal_shield",
                100,
                "A fireproof shield that only those enduring the Seven Curses can truly use.$(br2)" +
                        "It is tied to flame, defense, and pride, forming a solid bulwark in dangerous battles."
        )));

        futures.add(save(output, "en_us", "entries/equipment/voracious_pan", simpleSpotlight(
                "The Voracious Pan",
                "equipment",
                "enigmatic_legacy:eldritch_pan",
                110,
                "A ridiculous-looking but highly dangerous Seven Curses relic.$(br2)" +
                        "It grows stronger through kills of different creatures and can be used as a weapon."
        )));
    }
}
