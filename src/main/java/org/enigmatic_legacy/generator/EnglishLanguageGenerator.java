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
