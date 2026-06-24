package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;
import org.enigmatic_legacy.item.ModItems;

public class EnglishLanguageGenerator extends LanguageProvider {

    public EnglishLanguageGenerator(PackOutput output) {
        super(output, EnigmaticLegacy.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.enigmatic_legacy", "Enigmatic Legacy");      // 神秘遗物
        addItem(ModItems.ASTRAL_DUST, "Astral Dust");   // 星尘
        addItem(ModItems.ENDER_ROD, "Ender Rod");   // 末影棒
        addItem(ModItems.ETHERIUM_ORE, "Etherium Ore"); // 以太矿石
        addItem(ModItems.ETHERIUM_INGOT, "Etherium Ingot"); // 以太锭
        addItem(ModItems.THICC_SCROLL, "Blank Scroll"); // 空卷轴
        addItem(ModItems.DARKEST_SCROLL, "Darkest Scroll"); // 至暗卷轴
        addBlock(ModBlocks.ASTRAL_DUST_SACK, "Astral Block"); // 袋装星尘
        addBlock(ModBlocks.ETHERIUM_BLOCK, "Block of Etherium"); // 以太块
        addItem(ModItems.COSMIC_HEART, "Heart of the Cosmos"); // 寰宇之心
        addBlock(ModBlocks.BIG_LAMP, "Lamp");   // 大灯笼
        addBlock(ModBlocks.BIG_SHROOMLAMP, "Shroomlamp");   //菌光体灯笼
        addItem(ModItems.EARTH_HEART_FRAGMENT, "Fragment of the Earth"); // 大地之心碎片
        addItem(ModItems.EARTH_HEART, "Heart of the Earth"); // 大地之心
        // 扭曲之心
        addItem(ModItems.TWISTED_HEART, "Twisted Heart");
        add("tooltip.enigmatic_legacy.cursed_ones_only", "Only those bearing the curse may comprehend its purpose.");
        add("tooltip.enigmatic_legacy.twisted_heart.active", "Activated by the Ring of the Seven Curses.");
        add("tooltip.enigmatic_legacy.twisted_heart.inactive", "Dormant.");
        // end
        addItem(ModItems.CURSED_RING, "Ring of the Seven Curses"); // 七咒之戒
        addEnglishCursedRingTooltips();

        addItem(ModItems.EVIL_ESSENCE, "Nefarious Essence");
        add("tooltip.enigmatic_legacy.evilEssence1", "Embodies raw, unrefined energy torn out");
        add("tooltip.enigmatic_legacy.evilEssence2", "from the Wither's soul.");
        addItem(ModItems.IRON_RING, "Iron Ring");

        addItem(ModItems.EXQUISITE_RING, "Exquisite Ring");
        add("tooltip.enigmatic_legacy.exquisite_ring.luck", "Grants +1 Luck while equipped.");
        add("tooltip.enigmatic_legacy.exquisite_ring.piglin", "Piglins regard the wearer as one bearing gold.");
        add("tooltip.enigmatic_legacy.exquisite_ring.cursed_warning", "This piglin-neutralizing effect fails for bearers of the Seven Curses.");

        addItem(ModItems.EVIL_INGOT, "Nefarious Ingot");
        addItem(ModItems.STORAGE_CRYSTAL, "Extradimensional Vessel");
        add("tooltip.enigmatic_legacy.storageCrystal1", "Contains items and experience lost on death.");
        add("tooltip.enigmatic_legacy.storageCrystal2", "Stored stacks: %1$s");
        add("tooltip.enigmatic_legacy.storageCrystal3", "Stored experience: %1$s");

        addItem(ModItems.SOUL_CRYSTAL, "Soul Crystal");
        add("tooltip.enigmatic_legacy.soulCrystal1", "Right-Click to absorb the crystal and");
        add("tooltip.enigmatic_legacy.soulCrystal2", "restore one of your lost Soul Crystals.");

        addItem(ModItems.TWISTED_MIRROR, "Twisted Mirror");
        add("tooltip.enigmatic_legacy.twisted_mirror1", "Returns you to your spawn point.");
        add("tooltip.enigmatic_legacy.twisted_mirror2", "Only works in vanilla dimensions.");
        add("tooltip.enigmatic_legacy.twisted_mirror3", "Requires the Ring of the Seven Curses.");

        add("effect.enigmatic_legacy.recall", "Recall");
        add("effect.enigmatic_legacy.forbidden_fruit", "§5The Forbidden Fruit");

        add("item.minecraft.potion.effect.recall", "Potion of Recall");
        add("item.minecraft.splash_potion.effect.recall", "Splash Potion of Recall");
        add("item.minecraft.lingering_potion.effect.recall", "Lingering Potion of Recall");
        add("item.minecraft.tipped_arrow.effect.recall", "Arrow of Recall");

        addItem(ModItems.UNHOLY_GRAIL, "Unholy Grail");
        add("tooltip.enigmatic_legacy.unholy_grail1", "Drink from it to draw upon forbidden power.");
        add("tooltip.enigmatic_legacy.unholy_grail2", "The unworthy will suffer for their arrogance.");

        addItem(ModItems.GUARDIAN_HEART, "Heart of the Guardian");
        add("tooltip.enigmatic_legacy.guardian_heart1", "While in your hotbar, looking at a monster within %s blocks turns it against nearby monsters.");
        add("tooltip.enigmatic_legacy.guardian_heart2", "Nearby monsters within %s blocks will retaliate against the marked creature.");
        add("tooltip.enigmatic_legacy.guardian_heart3", "Ability cooldown: %s seconds.");

        addItem(ModItems.FORBIDDEN_FRUIT, "The Forbidden Fruit");
        add("tooltip.enigmatic_legacy.forbidden_fruit_lore", "Knowledge is the highest gift.");
        add("tooltip.enigmatic_legacy.forbidden_fruit1", "After eating it, you will no longer");
        add("tooltip.enigmatic_legacy.forbidden_fruit2", "feel hunger, but any form of");
        add("tooltip.enigmatic_legacy.forbidden_fruit3", "regeneration is reduced by %1$s.");

        addItem(ModItems.ENDER_RING, "Ring of Ender");
        add("tooltip.enigmatic_legacy.ender_ring1", "Allows access to your Ender Chest while equipped.");
        add("tooltip.enigmatic_legacy.ender_ring2", "Press the keybind or use the inventory button.");
        add("key.categories.enigmatic_legacy", "Enigmatic Legacy");
        add("key.enigmatic_legacy.ender_ring", "Open Ender Chest");
        add("button.enigmatic_legacy.open_ender_chest", "Open Ender Chest");
        add("message.enigmatic_legacy.ender_ring.no_access", "You need the Ring of Ender to do that.");

        addItem(ModItems.UNWITNESSED_AMULET, "Unwitnessed Amulet");

        addItem(ModItems.ENIGMATIC_AMULET_RED, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_AQUA, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_VIOLET, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_MAGENTA, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_GREEN, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_BLACK, "Enigmatic Amulet");
        addItem(ModItems.ENIGMATIC_AMULET_BLUE, "Enigmatic Amulet");

        add("tooltip.enigmatic_legacy.unwitnessed_amulet.1", "An amulet without witness, name, or fate.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.2", "It waits to be claimed by a mortal soul.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.3", "Right-click to reveal its true color.");
        add("tooltip.enigmatic_legacy.unwitnessed_amulet.use", "Right-click to witness it.");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.1", "A relic marked by an unknown force.");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.2", "Only one amulet may answer your call.");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.owner", "Witnessed by: %s");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.red", "Color: Crimson");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.aqua", "Color: Aqua");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.violet", "Color: Violet");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.magenta", "Color: Magenta");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.green", "Color: Green");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.black", "Color: Black");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.variant.blue", "Color: Blue");

        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red", "+2 Attack Damage");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua", "+15% Movement Speed while sprinting");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet", "15% chance to deflect incoming projectiles");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta", "-20% Gravity");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green", "+2 Mining Efficiency");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black", "10% Lifesteal");
        add("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue", "+25% Swim Speed");

        addItem(ModItems.MAGNET_RING, "Magnetic Ring");
        add("tooltip.enigmatic_legacy.magnet_ring.1", "Attracts nearby items within %s blocks.");

        add("tooltip.enigmatic_legacy.magnet_ring.enabled", "Magnetic field: Enabled");
        add("tooltip.enigmatic_legacy.magnet_ring.disabled", "Magnetic field: Disabled");

        addItem(ModItems.DISLOCATION_RING, "Dislocation Ring");

        add("tooltip.enigmatic_legacy.dislocation_ring.1", "Instantly collects nearby items within %s blocks.");
        add("tooltip.enigmatic_legacy.dislocation_ring.3", "Cannot be worn together with a Magnetic Ring.");

        add("tooltip.enigmatic_legacy.magnet_ring.2", "Inactive while sneaking.");
        add("tooltip.enigmatic_legacy.dislocation_ring.2", "Inactive while sneaking.");

        add("message.enigmatic_legacy.magnet_control.enabled", "%s enabled.");
        add("message.enigmatic_legacy.magnet_control.disabled", "%s disabled.");
        add("message.enigmatic_legacy.magnet_ring.no_ring", "You are not wearing a Magnetic Ring or Dislocation Ring.");

        add("gui.enigmatic_legacy.magnet_control.tooltip.enabled", "%s is enabled. Click to disable.");
        add("gui.enigmatic_legacy.magnet_control.tooltip.disabled", "%s is disabled. Click to enable.");

        addItem(ModItems.MONSTER_CHARM, "Emblem of Monster Slayer");

        add("tooltip.enigmatic_legacy.monster_charm.1", "Increases damage dealt to undead enemies by %s.");
        add("tooltip.enigmatic_legacy.monster_charm.2", "Increases damage dealt to hostile creatures by %s.");
        add("tooltip.enigmatic_legacy.monster_charm.3", "Provides +1 Looting.");
        add("tooltip.enigmatic_legacy.monster_charm.4", "Doubles experience dropped by monsters.");

        addItem(ModItems.TREASURE_HUNTER_CHARM, "Charm of Treasure Hunter");

        add("tooltip.enigmatic_legacy.treasure_hunter_charm.1", "Grants Night Vision while equipped.");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.2", "Provides +1 Fortune.");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.3", "Increases mining speed by %s.");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.4", "Right-click to toggle Night Vision.");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled", "Night Vision is enabled.");
        add("tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled", "Night Vision is disabled.");

        add("message.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled", "Night Vision enabled.");
        add("message.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled", "Night Vision disabled.");

        addItem(ModItems.BLOODSTAINED_VALOR_EMBLEM, "Emblem of Bloodstained Valor");

        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.1", "+%s Attack Damage per missing health percent.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.2", "+%s Attack Speed per missing health percent.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.3", "+%s Movement Speed per missing health percent.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.4", "+%s Damage Resistance per missing health percent.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.5", "These traits scale with how much health you are missing.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.6", "The closer you are to death, the stronger it becomes.");
        add("tooltip.enigmatic_legacy.bloodstained_valor_emblem.cursed_only", "Only bearers of the Seven Curses can use this item.");


        // Mega Sponge
        addItem(ModItems.MEGA_SPONGE, "Extrapolated Megasponge");
        add("tooltip.enigmatic_legacy.mega_sponge.1", "Automatically absorbs nearby water while equipped.");
        add("tooltip.enigmatic_legacy.mega_sponge.2", "Absorption radius: %s blocks.");
        // end

        // 附魔师的珍珠
        addItem(ModItems.ENCHANTER_PEARL, "Enchanter's Pearl");

        add("tooltip.enigmatic_legacy.enchanter_pearl.1", "Provides +1 Charm slot while equipped.");
        add("tooltip.enigmatic_legacy.enchanter_pearl.2", "Allows the bearer to carry more magical trinkets.");
        add("tooltip.enigmatic_legacy.enchanter_pearl.3", "The extra slot exists only while this pearl is equipped.");
        add("tooltip.enigmatic_legacy.enchanter_pearl.cursed_only", "Only bearers of the Seven Curses can use this item.");
        // end

        // 莫测之眼
        add("item.enigmatic_legacy.enigmatic_eye_dormant", "Dormant Eye");
        add("item.enigmatic_legacy.enigmatic_eye_active", "Inscrutable Eye");

        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.1", "It watches, yet it sleeps.");
        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.2", "Right-click to awaken the Eye.");
        add("tooltip.enigmatic_legacy.enigmatic_eye.dormant.3", "Some things are better left unseen.");

        add("tooltip.enigmatic_legacy.enigmatic_eye.active.1", "Provides +1 Charm slot while equipped.");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.2", "Increases block interaction range by 3 blocks.");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.3", "The Eye has awakened, and now it sees.");
        add("tooltip.enigmatic_legacy.enigmatic_eye.active.4", "Only the awakened Eye can be equipped.");

        add("message.enigmatic_legacy.enigmatic_eye.awakening", "The Eye begins to awaken...");
        add("message.enigmatic_legacy.enigmatic_eye.awakened", "The Eye awakens.");
        // end

        // 全知之眼
        add("quote.enigmatic_legacy.enigmatic_eye.prefix", "[The Eye]");
        add("quote.enigmatic_legacy.enigmatic_eye.first_wear", "At last, sight returns to the blind.");
        add("quote.enigmatic_legacy.enigmatic_eye.first_nether", "This place burns, yet it is not the deepest wound.");
        add("quote.enigmatic_legacy.enigmatic_eye.first_end", "An empty sky. A dead kingdom. How familiar.");
        add("quote.enigmatic_legacy.enigmatic_eye.deep_underground", "Beneath the stone, old things still remember your steps.");
        add("quote.enigmatic_legacy.enigmatic_eye.low_health", "You are closer to silence than you think.");
        add("quote.enigmatic_legacy.enigmatic_eye.midnight", "The world is quiet now. Listen carefully.");

        add("quote.no_peril_1", "Where there is no peril in the task - there can be no glory in its accomplishment.");
        add("quote.end_doorstep_1", "The doorstep of The End...\\nsingle step now separates you from it.");
        add("quote.only_because_1", "Only because you have fallen does not yet mean you backed down.");
        add("quote.demise_is_1", "Demise is but a temporary inconvenience for a demigod such as you.");
        add("quote.we_fall_1", "We fall, so that we may learn how to pick ourselves back up.");
        add("quote.you_will_endure_1", "You will endure this loss, and learn from it.");
        add("quote.oblivion_rejects_1", "Oblivion rejects you...\\nmake use of such opportunity, while you still can.");
        add("quote.setback_1", "A setback, but not a defeat.");
        add("quote.death_may_1", "Death may impede your conquest, but only broken will can halt it.");
        add("quote.eternity_to_keep_1", "You have eternity to keep trying. Do what it takes.");
        add("quote.violence_calls_1", "Violence calls for vengeance.\\nReturn, and dispense it!");
        add("quote.immortal_1", "You are immortal! Return and give that pitiful creature a demonstration of this fact.");
        add("quote.appaling_presence_1", "The shell is gone... but appalling presence will forever remain imprinted upon this realm.");
        add("quote.its_destruction_1", "Its destruction is a small consolation...\\ngiven the implications of its terrible existence.");

        add("quote.i_wandered_1", "Yes, I wandered those lands many times...");
        add("quote.i_wandered_2", "A desolate reminder of everything that was, and all that could have been.");

        add("quote.another_demigod_1", "Another demigod wanders this forsaken land...");
        add("quote.another_demigod_2", "Let me accompany you in your journey. Perhaps, you will manage that which others could not.");

        add("quote.another_eon_1", "Another eon, another wanderer.\\nAnother story to be told...");
        add("quote.another_eon_2", "I will observe yours with great curiosity.");

        add("quote.perhaps_you_1", "Perhaps, you will be the one to uncover the long-forgotten history of this land...");
        add("quote.perhaps_you_2", "If so, I must bear witness to your accomplishments.");

        add("quote.sulfur_air_1", "Sulfur in the air, sharp scent of burnt rock and flesh...");
        add("quote.sulfur_air_2", "This place is a Hell indeed.");

        add("quote.tortured_rocks_1", "Tortured rocks, pale as bones, hanged up above infinite, all-consuming blackness.");
        add("quote.tortured_rocks_2", "The End...\\nis almost like a fever dream.");

        add("quote.breathes_relieved_1", "The world breathes relieved without the burdensome presence of that horror...");
        add("quote.breathes_relieved_2", "So long at least.");

        add("quote.whether_it_is_1", "Whether it is power, or redemption that you seek in repeatedly returning Wither from and to its grave...");
        add("quote.whether_it_is_2", "...at least, you give that damnable creature a purpose.");

        add("quote.poor_creature_1", "Poor creature...\\nThe last of her kind, bound to guard the godforsaken island...");
        add("quote.poor_creature_2", "...in the middle of nothingness. Lay her to the deserved rest.");

        add("quote.horrible_existence_1", "What a horrible existence...\\nBrought back from the dead, only to be slain once more.");
        add("quote.horrible_existence_2", "You must truly be devoid of compassion.");

        add("quote.countless_dead_1", "Countless dead souls, merged into abomination of such terrible form that it defies description.");
        add("quote.countless_dead_2", "The earth is stained by the very presence of this foul creature.");

        add("quote.with_dragons_1", "With dragon's imminent demise, this place seems even more empty and purposeless than before.");
        add("quote.with_dragons_2", "Will you wander those fractured, deserted lands...");
        add("quote.with_dragons_3", "...or return to the facade of sanity and liveliness the Overworld provides?");
        add("quote.with_dragons_4", "Of course, we both know the answer...\\nyou cannot resist exploring the unknown, be it now or later.");

        add("quote.terrifying_form_1", "...its terrifying form is destructible, but not its soul.");
        add("quote.terrifying_form_2", "It bears curse not dissimilar to yours...\\nCurse of immortality.");

        add("quote.toll_paid_1", "With the toll paid, you are free from the burdensome swell of hatred and sorrow...");
        add("quote.toll_paid_2", "...imprisoned in that ring.");
        // end

        // 启示之证
        addItem(ModItems.THE_ACKNOWLEDGMENT, "The Acknowledgment");

        add("book.enigmatic_legacy.landing_text", "The vast lands lie ahead of you, full of secrets and mysteries to uncover.");

        add("tooltip.enigmatic_legacy.the_acknowledgment.1", "A guidebook for forgotten relics.");
        add("tooltip.enigmatic_legacy.the_acknowledgment.2", "Right-click to open The Acknowledgment.");
        add("tooltip.enigmatic_legacy.the_acknowledgment.shift.1", "Alters the Fourth Curse.");
        add("tooltip.enigmatic_legacy.the_acknowledgment.shift.2", "The Fourth Curse is weakened by %s.");
        // end

        // 术石
        add("curios.identifier.spellstone", "Spellstone");

        // 魔像之心
        addItem(ModItems.GOLEM_HEART, "Heart of the Golem");

        add("tooltip.enigmatic_legacy.spellstone.passive", "Passive Ability:");
        add("tooltip.enigmatic_legacy.golem_heart.1", "+%s Armor.");
        add("tooltip.enigmatic_legacy.golem_heart.2", "If you wear no armor:");
        add("tooltip.enigmatic_legacy.golem_heart.3", "+%s Armor and +%s Armor Toughness.");
        add("tooltip.enigmatic_legacy.golem_heart.4", "%s Explosion Damage Resistance.");
        add("tooltip.enigmatic_legacy.golem_heart.5", "%s Melee Damage Resistance.");
        add("tooltip.enigmatic_legacy.golem_heart.6", "%s Knockback Resistance.");
        add("tooltip.enigmatic_legacy.golem_heart.7", "Immune to crushing and piercing environmental damage.");
        add("tooltip.enigmatic_legacy.golem_heart.8", "Magic damage taken is multiplied by %s.");
        // end

        // 天使之祝
        addItem(ModItems.ANGEL_BLESSING, "Angel's Blessing");

        add("key.enigmatic_legacy.use_spellstone", "Use Spellstone");

        add("tooltip.enigmatic_legacy.spellstone.active", "Active Ability:");
        add("tooltip.enigmatic_legacy.spellstone.cooldown", "Cooldown: %s seconds.");
        add("tooltip.enigmatic_legacy.angel_blessing.active", "Accelerates you toward your line of sight.");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.1", "Immune to fall and collision damage.");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.2", "%s chance to reflect nearby projectiles.");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.3", "Your own projectiles are accelerated.");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.4", "Press the spellstone key to activate the dash.");
        add("tooltip.enigmatic_legacy.angel_blessing.passive.5", "Wither and void damage taken is increased.");
        // end

        // 海洋意志
        addItem(ModItems.OCEAN_STONE, "Will of the Ocean");

        add("tooltip.enigmatic_legacy.ocean_stone.active",
                "Summons a thunderstorm in the Overworld at the cost of experience.");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.1",
                "Grants %s damage resistance against aquatic creatures.");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.2",
                "Grants water breathing, underwater night vision and greatly improves underwater mining.");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.3",
                "Increases swimming speed by %s and negates underwater gravity.");
        add("tooltip.enigmatic_legacy.ocean_stone.passive.4",
                "Makes you more vulnerable to fire damage.");

        add("message.enigmatic_legacy.ocean_stone.wrong_dimension",
                "The will of the ocean cannot reach this dimension.");
        add("message.enigmatic_legacy.ocean_stone.already_thundering",
                "The storm is already raging.");
        add("message.enigmatic_legacy.ocean_stone.not_enough_xp",
                "You need more experience to call the storm.");
        add("message.enigmatic_legacy.ocean_stone.summoned",
                "The ocean answers your will.");
        // end

        // 烈焰核心
        addItem(ModItems.BLAZING_CORE, "Blazing Core");

        add("tooltip.enigmatic_legacy.blazing_core.active",
                "No active ability.");
        add("tooltip.enigmatic_legacy.blazing_core.passive.1",
                "Immunizes you against fire damage and automatically extinguishes you.");
        add("tooltip.enigmatic_legacy.blazing_core.passive.2",
                "Temporarily protects you from lava damage; staying in lava too long makes you overheat.");
        add("tooltip.enigmatic_legacy.blazing_core.passive.3",
                "When hit in melee, deals %s fire feedback damage to the attacker and ignites them.");
        add("tooltip.enigmatic_legacy.blazing_core.passive.4",
                "Most status effects last %s as long; fire resistance-like effects last twice as long.");
        add("tooltip.enigmatic_legacy.blazing_core.passive.5",
                "Damage from aquatic creatures is multiplied by %s.");
        // end

        // 星云之眼
        addItem(ModItems.EYE_OF_NEBULA, "Eye of the Nebula");

        add("message.enigmatic_legacy.eye_of_nebula.no_target", "The Eye of the Nebula found no target.");

        add("tooltip.enigmatic_legacy.eye_of_nebula.active", "Teleports you behind the creature you are looking at.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.1", "Increases magic damage by %s.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.2", "Grants %s magic resistance.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.3", "Has a %s chance to teleport away when attacked, avoiding that damage.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.4", "After using its active ability, your next attack deals %s additional damage.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.5", "Prevents fall-type teleportation damage after its own teleportation.");
        add("tooltip.enigmatic_legacy.eye_of_nebula.passive.6", "While in water, all damage taken is doubled.");
        // end

        // 虚空珍珠
        addItem(ModItems.VOID_PEARL, "Pearl of the Void");

        add("tooltip.enigmatic_legacy.void_pearl.passive.1", "You no longer need to breathe air and become immune to drowning damage.");
        add("tooltip.enigmatic_legacy.void_pearl.passive.2", "Removes almost all status effects, except special effects such as Forbidden Fruit.");
        add("tooltip.enigmatic_legacy.void_pearl.passive.3", "Your attacks inflict Wither.");
        add("tooltip.enigmatic_legacy.void_pearl.passive.4", "Every 0.5 seconds, creatures exposed to darkness within %s blocks take %s void damage and suffer severe debuffs.");
        add("tooltip.enigmatic_legacy.void_pearl.passive.5", "Has a %s chance to block fatal damage.");
        add("tooltip.enigmatic_legacy.void_pearl.passive.6", "Extinguishes you every tick and prevents suffocation damage inside blocks.");

        add("death.attack.enigmatic_legacy.darkness", "%1$s was devoured by darkness");
        add("death.attack.enigmatic_legacy.darkness.player", "%1$s was devoured by the void darkness around %2$s");
        // end

        // 非欧立方
        addItem(ModItems.THE_CUBE, "Non-Euclidean Cube");

        add("message.enigmatic_legacy.non_euclidean_cube.no_structure", "The Non-Euclidean Cube found no structure to fold space toward.");
        add("message.enigmatic_legacy.non_euclidean_cube.teleported", "Space folds, carrying you toward an unknown structure.");

        add("tooltip.enigmatic_legacy.non_euclidean_cube.active", "Teleports you near a random structure in the current dimension.");

        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.1", "%s sprint speed, %s swim speed, %s mining speed, and %s attack speed.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.2", "%s Fortune level and %s Luck.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.3", "Ignores damage above %s. With the Ring of Seven Curses, ignores damage above %s instead.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.4", "Has a %s chance to reflect projectiles or return damage to the attacker.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.5", "When damaged by non-projectile attacks, progressively afflicts the attacker with negative effects.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.6", "Grants a random positive effect when you defeat a creature, excluding Slow Falling.");
        add("tooltip.enigmatic_legacy.non_euclidean_cube.passive.7", "Prevents negative effects, cramming, fall, collision, thorns, teleportation, fire, and lava damage, and folds space to save you near death.");
        // end

        // 创造之心
        addItem(ModItems.HEART_OF_CREATION, "Heart of Creation");

        add("message.enigmatic_legacy.heart_of_creation.no_targets", "The Heart of Creation found no enemies to judge.");

        add("tooltip.enigmatic_legacy.heart_of_creation.active", "Calls lightning upon all enemies within %s blocks, dealing %s damage and inflicting Wither.");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.1", "Prevents suffocation, fall, collision, cramming, starvation, void, thorns, fire, and lava damage.");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.2", "Removes most negative status effects.");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.3", "Prevents knockback and grants flight.");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.4", "Compensates for mining speed loss while flying.");
        add("tooltip.enigmatic_legacy.heart_of_creation.passive.5", "While equipped or carried in your inventory, you become immortal;");
        // end

        // 永恒智慧卷轴
        addItem(ModItems.XP_SCROLL, "Scroll of Ageless Wisdom");

        add("curios.identifier.scroll", "Arcane Scroll");
        add("curios.modifiers.scroll", "When worn as scroll:");

        add("key.enigmatic_legacy.scroll", "Use Mystic Scroll");

        add("message.enigmatic_legacy.xp_scroll.enabled", "Scroll of Ageless Wisdom enabled.");
        add("message.enigmatic_legacy.xp_scroll.disabled", "Scroll of Ageless Wisdom disabled.");
        add("message.enigmatic_legacy.xp_scroll.mode_absorption", "Scroll of Ageless Wisdom switched to Absorption Mode.");
        add("message.enigmatic_legacy.xp_scroll.mode_extraction", "Scroll of Ageless Wisdom switched to Extraction Mode.");
        add("message.enigmatic_legacy.xp_scroll.extracted", "Scroll of Ageless Wisdom returned all stored experience.");

        add("tooltip.enigmatic_legacy.xp_scroll.stored", "Stored Experience: %s");
        add("tooltip.enigmatic_legacy.xp_scroll.active", "State: Active");
        add("tooltip.enigmatic_legacy.xp_scroll.inactive", "State: Inactive");
        add("tooltip.enigmatic_legacy.xp_scroll.mode_absorption", "Mode: Absorption");
        add("tooltip.enigmatic_legacy.xp_scroll.mode_extraction", "Mode: Extraction");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.1", "Shift + right-click to switch between Absorption and Extraction Mode.");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.2", "Press the scroll key to enable or disable the scroll.");
        add("tooltip.enigmatic_legacy.xp_scroll.usage.3", "While active, collects experience orbs within %s blocks.");
        // end

        // 天堂之礼
        addItem(ModItems.HEAVEN_SCROLL, "Gift of the Heaven");

        add("tooltip.enigmatic_legacy.heaven_scroll.1", "Grants free flight while within range of an active beacon.");
        add("tooltip.enigmatic_legacy.heaven_scroll.2", "Flying slowly consumes experience and increases mining speed.");
        add("tooltip.enigmatic_legacy.heaven_scroll.3", "Leaving beacon range removes flight and grants Slow Falling for 8 seconds.");
        add("tooltip.enigmatic_legacy.heaven_scroll.4", "While beacon-powered flight is available, negates fall damage.");
        // end

        // 千咒卷轴
        addItem(ModItems.CURSED_SCROLL, "Scroll of a Thousand Curses");

        add("tooltip.enigmatic_legacy.cursed_scroll.1", "Grants bonuses based on the number of curse enchantments on your equipped items.");
        add("tooltip.enigmatic_legacy.cursed_scroll.2", "Main hand, off hand, armor, and Curios equipment are counted.");
        add("tooltip.enigmatic_legacy.cursed_scroll.3", "Each curse enchantment counts as one entry, regardless of its level.");
        add("tooltip.enigmatic_legacy.cursed_scroll.4", "The Ring of the Seven Curses counts as 7 additional curses.");

        add("tooltip.enigmatic_legacy.cursed_scroll.attack", "Per curse: +%s Attack Damage");
        add("tooltip.enigmatic_legacy.cursed_scroll.mining", "Per curse: +%s Mining Speed");
        add("tooltip.enigmatic_legacy.cursed_scroll.healing", "Per curse: +%s Healing Received");

        add("tooltip.enigmatic_legacy.cursed_scroll.cursed_only", "Only bearers of the Seven Curses may use this item.");

        add("tooltip.enigmatic_legacy.cursed_scroll.current.factor", "Current Curse Count: %s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.attack", "Current Attack Damage: +%s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.mining", "Current Mining Speed: +%s");
        add("tooltip.enigmatic_legacy.cursed_scroll.current.healing", "Current Healing: +%s");
        // end

        // 创造者的恩赐
        addItem(ModItems.FABULOUS_SCROLL, "Grace of the Creator");

        add("tooltip.enigmatic_legacy.fabulous_scroll.1", "Grants you the ability of free flight.");
        add("tooltip.enigmatic_legacy.fabulous_scroll.2", "Flying rapidly consumes experience.");
        add("tooltip.enigmatic_legacy.fabulous_scroll.3", "Flight does not consume experience within range of an active beacon.");
        add("tooltip.enigmatic_legacy.fabulous_scroll.4", "Compensates mining speed loss while flying and negates fall damage while flight remains available.");
        // end

        // 按住 Shift 查看详情
        add("tooltip.enigmatic_legacy.hold_shift", "§5Hold §6Shift§5 to see details.");
    }

    private void addEnglishCursedRingTooltips() {
        add("tooltip.enigmatic_legacy.void", " ");
        add("tooltip.enigmatic_legacy.eternallyBound1", "§5Once worn, it becomes a part of you.");
        add("tooltip.enigmatic_legacy.eternallyBound2", "§4This ring will persist with you forever.");
        add("tooltip.enigmatic_legacy.eternallyBound2_creative", "§6With the power of god, you can unequip it.");
        add("tooltip.enigmatic_legacy.cursedRing3", "§dSeven curses will befall whoever bears it:");
        add("tooltip.enigmatic_legacy.cursedRing4", "§5- You receive double damage from §6ANY§5 source.");
        add("tooltip.enigmatic_legacy.cursedRing4_alt", "§5- You receive §6%1$s§5 damage from §6ANY§5 source.");
        add("tooltip.enigmatic_legacy.cursedRing5", "§5- Neutral creatures are aggressive towards you.");
        add("tooltip.enigmatic_legacy.cursedRing6", "§5- Armor is §6%1$s§5 less effective.");
        add("tooltip.enigmatic_legacy.cursedRing7", "§5- Monsters receive §6%1$s§5 less damage from you.");
        add("tooltip.enigmatic_legacy.cursedRing8", "§5- When on fire, you burn forever.");
        add("tooltip.enigmatic_legacy.cursedRing9", "§5- Every death tears your soul apart.");
        add("tooltip.enigmatic_legacy.cursedRing10", "§5- You suffer from incurable insomnia.");
        add("tooltip.enigmatic_legacy.cursedRing11", "§dSeven blessings will reward those who withstand:");
        add("tooltip.enigmatic_legacy.cursedRing12", "§5- §6+%1$s§d Looting Level");
        add("tooltip.enigmatic_legacy.cursedRing13", "§5- §6+%1$s§d Fortune Level");
        add("tooltip.enigmatic_legacy.cursedRing14", "§5- §6+%1$s§5 §dExperience§5 dropped.");
        add("tooltip.enigmatic_legacy.cursedRing15", "§5- §6+%1$s§d Enchanting Power§5 in §6Enchanting Table§5.");
        add("tooltip.enigmatic_legacy.cursedRing16", "§5- Unique drops from some creatures.");
        add("tooltip.enigmatic_legacy.cursedRing17", "§5- Functionality of §6Ring of Ender§5.");
        add("tooltip.enigmatic_legacy.cursedRing18", "§5- You can create and use unique relics.");
        add("tooltip.enigmatic_legacy.cursedRingLore1", "§5Once forged by antediluvian god, it beckons");
        add("tooltip.enigmatic_legacy.cursedRingLore2", "§5mortals and higher beings alike with promise");
        add("tooltip.enigmatic_legacy.cursedRingLore3", "§5of untold riches and immeasurable might...");
        add("tooltip.enigmatic_legacy.cursedRingLore4", "§5Be it arrogance or ignorance that leads them");
        add("tooltip.enigmatic_legacy.cursedRingLore5", "§5to believe they can harness ring's power,");
        add("tooltip.enigmatic_legacy.cursedRingLore6", "§5both are paid for in suffering the extent of");
        add("tooltip.enigmatic_legacy.cursedRingLore7", "§5which defies description.");
    }
}
