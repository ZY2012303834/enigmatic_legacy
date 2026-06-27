package org.enigmatic_legacy.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.enigmatic_legacy.config.common.*;

import java.util.List;

/**
 * 神秘遗物服务器配置总入口。
 *
 * 注意：
 * 1. 只能创建一个 ModConfigSpec.Builder；
 * 2. 所有子配置类都必须使用同一个 builder；
 * 3. 最后只能调用一次 SPEC = builder.build()。
 */
public final class ConfigCommon {
    public static final ModConfigSpec SPEC;

    public static final AccessibilityConfig ACCESSIBILITY;      // 基础配置
    public static final GenericConfig GENERIC;      // 通用机制配置
    public static final ItemOptionsConfig ITEM_OPTIONS;
    public static final CursedRingConfig CURSED_RING;
    public static final ForbiddenFruitConfig FORBIDDEN_FRUIT;
    public static final MagnetConfig MAGNET;
    public static final MonsterCharmConfig MONSTER_CHARM;
    public static final TreasureHunterCharmConfig TREASURE_HUNTER_CHARM;
    public static final BloodstainedValorConfig BLOODSTAINED_VALOR;
    public static final MegaSpongeConfig MEGA_SPONGE;
    public static final GolemHeartConfig GOLEM_HEART;
    public static final GuidebookConfig GUIDEBOOKS;

    // ==============================
    // 旧字段兼容别名：可访问性 / 基础机制配置
    // ==============================
    public static final ModConfigSpec.BooleanValue CUSTOM_DUNGEON_LOOT_ENABLED;
    public static final ModConfigSpec.BooleanValue BONUS_WOOL_RECIPES_ENABLED;
    public static final ModConfigSpec.BooleanValue DISABLE_AOE_SHIFT_SUPPRESSION;
    public static final ModConfigSpec.BooleanValue RETRIGGER_RECIPE_UNLOCKS;
    public static final ModConfigSpec.BooleanValue CRASH_ON_UNNAMED_POOL;

    // ==============================
    // 旧字段兼容别名：通用机制配置
    // ==============================
    public static final ModConfigSpec.IntValue SOUL_CRYSTALS_MODE;
    public static final ModConfigSpec.IntValue MAX_SOUL_CRYSTAL_LOSS;
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> COMPLETE_BOSS_LIST;

    // ==============================
    // 旧字段兼容别名：物品与方块启用开关
    // ==============================
    public static final ModConfigSpec.BooleanValue CURSED_RING_ENABLED;
    public static final ModConfigSpec.BooleanValue COSMIC_HEART_ENABLED;
    public static final ModConfigSpec.BooleanValue EARTH_HEART_FRAGMENT_ENABLED;
    public static final ModConfigSpec.BooleanValue BIG_LAMP_ENABLED;
    public static final ModConfigSpec.BooleanValue BIG_SHROOMLAMP_ENABLED;
    public static final ModConfigSpec.BooleanValue ASTRAL_DUST_ENABLED;
    public static final ModConfigSpec.BooleanValue ENDER_ROD_ENABLED;
    public static final ModConfigSpec.BooleanValue ETHERIUM_INGOT_ENABLED;
    public static final ModConfigSpec.BooleanValue ETHERIUM_ORE_ENABLED;
    public static final ModConfigSpec.BooleanValue THICC_SCROLL_ENABLED;
    public static final ModConfigSpec.BooleanValue DARKEST_SCROLL_ENABLED;

    // ==============================
    // 旧字段兼容别名：七咒之戒
    // ==============================
    public static final ModConfigSpec.IntValue CURSED_RING_PAIN_MODIFIER;
    public static final ModConfigSpec.IntValue CURSED_RING_MONSTER_DAMAGE_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_ARMOR_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_EXPERIENCE_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_KNOCKBACK_DEBUFF;
    public static final ModConfigSpec.IntValue CURSED_RING_FORTUNE_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_LOOTING_BONUS;
    public static final ModConfigSpec.IntValue CURSED_RING_ENCHANTING_BONUS;
    public static final ModConfigSpec.DoubleValue CURSED_RING_NEUTRAL_ANGER_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_NEUTRAL_XRAY_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_ENDERMAN_RANDOM_TELEPORT_RANGE;
    public static final ModConfigSpec.DoubleValue CURSED_RING_ENDERMAN_RANDOM_TELEPORT_FREQUENCY;
    public static final ModConfigSpec.DoubleValue CURSED_RING_SUPER_CURSED_TIME;
    public static final ModConfigSpec.BooleanValue CURSED_RING_SAVE_THE_BEES;
    public static final ModConfigSpec.BooleanValue CURSED_RING_ULTRA_HARDCORE;
    public static final ModConfigSpec.BooleanValue CURSED_RING_SPECIAL_DROPS_ENABLED;
    public static final ModConfigSpec.BooleanValue CURSED_RING_DISABLE_INSOMNIA;
    public static final ModConfigSpec.ConfigValue<List<String>> CURSED_RING_NEUTRAL_ANGER_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<String>> ANIMAL_GUIDE_ANIMAL_EXCLUSION_LIST;
    public static final ModConfigSpec.IntValue HUNTER_GUIDE_EFFECTIVE_DISTANCE;
    public static final ModConfigSpec.IntValue HUNTER_GUIDE_SYNERGY_DAMAGE_REDUCTION;

    // ==============================
    // 旧字段兼容别名：禁果
    // ==============================
    public static final ModConfigSpec.IntValue FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_FRUIT_DEBUFF_DURATION_MULTIPLIER;

    // ==============================
    // 旧字段兼容别名：磁力之戒 / 转位之戒
    // ==============================
    public static final ModConfigSpec.DoubleValue MAGNET_RING_RANGE;
    public static final ModConfigSpec.DoubleValue DISLOCATION_RING_RANGE;

    // ==============================
    // 旧字段兼容别名：怪物猎人勋章
    // ==============================
    public static final ModConfigSpec.IntValue MONSTER_CHARM_UNDEAD_DAMAGE;
    public static final ModConfigSpec.IntValue MONSTER_CHARM_HOSTILE_DAMAGE;
    public static final ModConfigSpec.BooleanValue MONSTER_CHARM_BONUS_LOOTING_ENABLED;
    public static final ModConfigSpec.BooleanValue MONSTER_CHARM_DOUBLE_XP_ENABLED;

    // ==============================
    // 旧字段兼容别名：猎宝者护符
    // ==============================
    public static final ModConfigSpec.IntValue TREASURE_HUNTER_CHARM_MINING_SPEED_BONUS;
    public static final ModConfigSpec.BooleanValue TREASURE_HUNTER_CHARM_FORTUNE_ENABLED;
    public static final ModConfigSpec.BooleanValue TREASURE_HUNTER_CHARM_NIGHT_VISION_ENABLED;
    public static final ModConfigSpec.IntValue TREASURE_HUNTER_CHARM_NIGHT_VISION_DURATION;

    // ==============================
    // 旧字段兼容别名：血战沙场之证
    // ==============================
    public static final ModConfigSpec.DoubleValue BLOODSTAINED_VALOR_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue BLOODSTAINED_VALOR_ATTACK_SPEED;
    public static final ModConfigSpec.DoubleValue BLOODSTAINED_VALOR_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue BLOODSTAINED_VALOR_DAMAGE_RESISTANCE;

    // ==============================
    // 旧字段兼容别名：超级海绵
    // ==============================
    public static final ModConfigSpec.IntValue MEGA_SPONGE_RADIUS;

    // ==============================
    // 旧字段兼容别名：魔像之心
    // ==============================
    public static final ModConfigSpec.DoubleValue GOLEM_HEART_DEFAULT_ARMOR_BONUS;
    public static final ModConfigSpec.DoubleValue GOLEM_HEART_SUPER_ARMOR_BONUS;
    public static final ModConfigSpec.DoubleValue GOLEM_HEART_SUPER_ARMOR_TOUGHNESS_BONUS;
    public static final ModConfigSpec.DoubleValue GOLEM_HEART_KNOCKBACK_RESISTANCE;
    public static final ModConfigSpec.IntValue GOLEM_HEART_MELEE_RESISTANCE;
    public static final ModConfigSpec.IntValue GOLEM_HEART_EXPLOSION_RESISTANCE;
    public static final ModConfigSpec.DoubleValue GOLEM_HEART_MAGIC_VULNERABILITY;

    // 天使之祝
    public static final AngelBlessingConfig ANGEL_BLESSING;

    public static final ModConfigSpec.DoubleValue ANGEL_BLESSING_ACCELERATION_MODIFIER;
    public static final ModConfigSpec.DoubleValue ANGEL_BLESSING_ACCELERATION_MODIFIER_ELYTRA;
    public static final ModConfigSpec.IntValue ANGEL_BLESSING_DEFLECT_CHANCE;
    public static final ModConfigSpec.DoubleValue ANGEL_BLESSING_VULNERABILITY_MODIFIER;
    public static final ModConfigSpec.IntValue ANGEL_BLESSING_COOLDOWN;

    // 海洋意志
    public static final OceanStoneConfig OCEAN_STONE;

    public static final ModConfigSpec.IntValue OCEAN_STONE_COOLDOWN;
    public static final ModConfigSpec.IntValue OCEAN_STONE_SWIM_SPEED_BONUS;
    public static final ModConfigSpec.IntValue OCEAN_STONE_AQUATIC_DAMAGE_RESISTANCE;
    public static final ModConfigSpec.DoubleValue OCEAN_STONE_XP_COST_MODIFIER;
    public static final ModConfigSpec.IntValue OCEAN_STONE_NIGHT_VISION_DURATION;
    public static final ModConfigSpec.DoubleValue OCEAN_STONE_FIRE_VULNERABILITY;

    // 烈焰核心
    public static final BlazingCoreConfig BLAZING_CORE;
    public static final ModConfigSpec.IntValue BLAZING_CORE_COOLDOWN;
    public static final ModConfigSpec.DoubleValue BLAZING_CORE_DAMAGE_FEEDBACK;
    public static final ModConfigSpec.IntValue BLAZING_CORE_IGNITION_FEEDBACK;
    public static final ModConfigSpec.IntValue BLAZING_CORE_LAVA_IMMUNITY_TICKS;
    public static final ModConfigSpec.IntValue BLAZING_CORE_LAVA_COOLDOWN_PER_TICK;
    public static final ModConfigSpec.IntValue BLAZING_CORE_EFFECT_DURATION_MODIFIER;
    public static final ModConfigSpec.IntValue BLAZING_CORE_MOLTEN_EFFECT_DURATION_MODIFIER;
    public static final ModConfigSpec.DoubleValue BLAZING_CORE_AQUATIC_DAMAGE_VULNERABILITY;

    // 创造之心
    public static final HeartOfCreationConfig HEART_OF_CREATION;
    public static final ModConfigSpec.IntValue HEART_OF_CREATION_COOLDOWN;
    public static final ModConfigSpec.DoubleValue HEART_OF_CREATION_LIGHTNING_RANGE;
    public static final ModConfigSpec.DoubleValue HEART_OF_CREATION_LIGHTNING_DAMAGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ACCESSIBILITY = new AccessibilityConfig(builder);   // 基础配置
        GENERIC = new GenericConfig(builder);       // 通用机制配置
        ITEM_OPTIONS = new ItemOptionsConfig(builder);
        CURSED_RING = new CursedRingConfig(builder);
        FORBIDDEN_FRUIT = new ForbiddenFruitConfig(builder);
        MAGNET = new MagnetConfig(builder);
        MONSTER_CHARM = new MonsterCharmConfig(builder);
        TREASURE_HUNTER_CHARM = new TreasureHunterCharmConfig(builder);
        BLOODSTAINED_VALOR = new BloodstainedValorConfig(builder);
        MEGA_SPONGE = new MegaSpongeConfig(builder);
        GOLEM_HEART = new GolemHeartConfig(builder);
        GUIDEBOOKS = new GuidebookConfig(builder);

        CUSTOM_DUNGEON_LOOT_ENABLED = ACCESSIBILITY.customDungeonLootEnabled;
        BONUS_WOOL_RECIPES_ENABLED = ACCESSIBILITY.bonusWoolRecipesEnabled;
        DISABLE_AOE_SHIFT_SUPPRESSION = ACCESSIBILITY.disableAoeShiftSuppression;
        RETRIGGER_RECIPE_UNLOCKS = ACCESSIBILITY.retriggerRecipeUnlocks;
        CRASH_ON_UNNAMED_POOL = ACCESSIBILITY.crashOnUnnamedPool;

        SOUL_CRYSTALS_MODE = GENERIC.soulCrystalsMode;
        MAX_SOUL_CRYSTAL_LOSS = GENERIC.maxSoulCrystalLoss;
        COMPLETE_BOSS_LIST = GENERIC.completeBossList;

        CURSED_RING_ENABLED = ITEM_OPTIONS.cursedRingEnabled;
        COSMIC_HEART_ENABLED = ITEM_OPTIONS.cosmicHeartEnabled;
        EARTH_HEART_FRAGMENT_ENABLED = ITEM_OPTIONS.earthHeartFragmentEnabled;
        BIG_LAMP_ENABLED = ITEM_OPTIONS.bigLampEnabled;
        BIG_SHROOMLAMP_ENABLED = ITEM_OPTIONS.bigShroomlampEnabled;
        ASTRAL_DUST_ENABLED = ITEM_OPTIONS.astralDustEnabled;
        ENDER_ROD_ENABLED = ITEM_OPTIONS.enderRodEnabled;
        ETHERIUM_INGOT_ENABLED = ITEM_OPTIONS.etheriumIngotEnabled;
        ETHERIUM_ORE_ENABLED = ITEM_OPTIONS.etheriumOreEnabled;
        THICC_SCROLL_ENABLED = ITEM_OPTIONS.thiccScrollEnabled;
        DARKEST_SCROLL_ENABLED = ITEM_OPTIONS.darkestScrollEnabled;

        // 七咒之戒
        CURSED_RING_PAIN_MODIFIER = CURSED_RING.painModifier;
        CURSED_RING_MONSTER_DAMAGE_DEBUFF = CURSED_RING.monsterDamageDebuff;
        CURSED_RING_ARMOR_DEBUFF = CURSED_RING.armorDebuff;
        CURSED_RING_EXPERIENCE_BONUS = CURSED_RING.experienceBonus;
        CURSED_RING_KNOCKBACK_DEBUFF = CURSED_RING.knockbackDebuff;
        CURSED_RING_FORTUNE_BONUS = CURSED_RING.fortuneBonus;
        CURSED_RING_LOOTING_BONUS = CURSED_RING.lootingBonus;
        CURSED_RING_ENCHANTING_BONUS = CURSED_RING.enchantingBonus;
        CURSED_RING_NEUTRAL_ANGER_RANGE = CURSED_RING.neutralAngerRange;
        CURSED_RING_NEUTRAL_XRAY_RANGE = CURSED_RING.neutralXrayRange;
        CURSED_RING_ENDERMAN_RANDOM_TELEPORT_RANGE = CURSED_RING.endermanRandomTeleportRange;
        CURSED_RING_ENDERMAN_RANDOM_TELEPORT_FREQUENCY = CURSED_RING.endermanRandomTeleportFrequency;
        CURSED_RING_SUPER_CURSED_TIME = CURSED_RING.superCursedTime;
        CURSED_RING_SAVE_THE_BEES = CURSED_RING.saveTheBees;
        CURSED_RING_ULTRA_HARDCORE = CURSED_RING.ultraHardcore;
        CURSED_RING_SPECIAL_DROPS_ENABLED = CURSED_RING.specialDropsEnabled;
        CURSED_RING_DISABLE_INSOMNIA = CURSED_RING.disableInsomnia;
        CURSED_RING_NEUTRAL_ANGER_BLACKLIST = CURSED_RING.neutralAngerBlacklist;
        ANIMAL_GUIDE_ANIMAL_EXCLUSION_LIST = CURSED_RING.animalGuideAnimalExclusionList;
        HUNTER_GUIDE_EFFECTIVE_DISTANCE = GUIDEBOOKS.hunterGuideEffectiveDistance;
        HUNTER_GUIDE_SYNERGY_DAMAGE_REDUCTION = GUIDEBOOKS.hunterGuideSynergyDamageReduction;
        // end

        FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION = FORBIDDEN_FRUIT.regenerationSubtraction;
        FORBIDDEN_FRUIT_DEBUFF_DURATION_MULTIPLIER = FORBIDDEN_FRUIT.debuffDurationMultiplier;

        MAGNET_RING_RANGE = MAGNET.magnetRingRange;
        DISLOCATION_RING_RANGE = MAGNET.dislocationRingRange;

        MONSTER_CHARM_UNDEAD_DAMAGE = MONSTER_CHARM.undeadDamage;
        MONSTER_CHARM_HOSTILE_DAMAGE = MONSTER_CHARM.hostileDamage;
        MONSTER_CHARM_BONUS_LOOTING_ENABLED = MONSTER_CHARM.bonusLootingEnabled;
        MONSTER_CHARM_DOUBLE_XP_ENABLED = MONSTER_CHARM.doubleXpEnabled;

        TREASURE_HUNTER_CHARM_MINING_SPEED_BONUS = TREASURE_HUNTER_CHARM.miningSpeedBonus;
        TREASURE_HUNTER_CHARM_FORTUNE_ENABLED = TREASURE_HUNTER_CHARM.fortuneEnabled;
        TREASURE_HUNTER_CHARM_NIGHT_VISION_ENABLED = TREASURE_HUNTER_CHARM.nightVisionEnabled;
        TREASURE_HUNTER_CHARM_NIGHT_VISION_DURATION = TREASURE_HUNTER_CHARM.nightVisionDuration;

        BLOODSTAINED_VALOR_ATTACK_DAMAGE = BLOODSTAINED_VALOR.attackDamage;
        BLOODSTAINED_VALOR_ATTACK_SPEED = BLOODSTAINED_VALOR.attackSpeed;
        BLOODSTAINED_VALOR_MOVEMENT_SPEED = BLOODSTAINED_VALOR.movementSpeed;
        BLOODSTAINED_VALOR_DAMAGE_RESISTANCE = BLOODSTAINED_VALOR.damageResistance;


        MEGA_SPONGE_RADIUS = MEGA_SPONGE.radius;

        // 魔像之心
        GOLEM_HEART_DEFAULT_ARMOR_BONUS = GOLEM_HEART.defaultArmorBonus;
        GOLEM_HEART_SUPER_ARMOR_BONUS = GOLEM_HEART.superArmorBonus;
        GOLEM_HEART_SUPER_ARMOR_TOUGHNESS_BONUS = GOLEM_HEART.superArmorToughnessBonus;
        GOLEM_HEART_KNOCKBACK_RESISTANCE = GOLEM_HEART.knockbackResistance;
        GOLEM_HEART_MELEE_RESISTANCE = GOLEM_HEART.meleeResistance;
        GOLEM_HEART_EXPLOSION_RESISTANCE = GOLEM_HEART.explosionResistance;
        GOLEM_HEART_MAGIC_VULNERABILITY = GOLEM_HEART.magicVulnerability;

        // 天使之祝
        ANGEL_BLESSING = new AngelBlessingConfig(builder);

        ANGEL_BLESSING_ACCELERATION_MODIFIER = ANGEL_BLESSING.accelerationModifier;
        ANGEL_BLESSING_ACCELERATION_MODIFIER_ELYTRA = ANGEL_BLESSING.accelerationModifierElytra;
        ANGEL_BLESSING_DEFLECT_CHANCE = ANGEL_BLESSING.deflectChance;
        ANGEL_BLESSING_VULNERABILITY_MODIFIER = ANGEL_BLESSING.vulnerabilityModifier;
        ANGEL_BLESSING_COOLDOWN = ANGEL_BLESSING.cooldown;

        // 海洋意志
        OCEAN_STONE = new OceanStoneConfig(builder);

        OCEAN_STONE_COOLDOWN = OCEAN_STONE.cooldown;
        OCEAN_STONE_SWIM_SPEED_BONUS = OCEAN_STONE.swimSpeedBonus;
        OCEAN_STONE_AQUATIC_DAMAGE_RESISTANCE = OCEAN_STONE.aquaticDamageResistance;
        OCEAN_STONE_XP_COST_MODIFIER = OCEAN_STONE.xpCostModifier;
        OCEAN_STONE_NIGHT_VISION_DURATION = OCEAN_STONE.nightVisionDuration;
        OCEAN_STONE_FIRE_VULNERABILITY = OCEAN_STONE.fireVulnerability;

        // 烈焰核心
        BLAZING_CORE = new BlazingCoreConfig(builder);
        BLAZING_CORE_COOLDOWN = BLAZING_CORE.cooldown;
        BLAZING_CORE_DAMAGE_FEEDBACK = BLAZING_CORE.damageFeedback;
        BLAZING_CORE_IGNITION_FEEDBACK = BLAZING_CORE.ignitionFeedback;
        BLAZING_CORE_LAVA_IMMUNITY_TICKS = BLAZING_CORE.lavaImmunityTicks;
        BLAZING_CORE_LAVA_COOLDOWN_PER_TICK = BLAZING_CORE.lavaCooldownPerTick;
        BLAZING_CORE_EFFECT_DURATION_MODIFIER = BLAZING_CORE.effectDurationModifier;
        BLAZING_CORE_MOLTEN_EFFECT_DURATION_MODIFIER = BLAZING_CORE.moltenEffectDurationModifier;
        BLAZING_CORE_AQUATIC_DAMAGE_VULNERABILITY = BLAZING_CORE.aquaticDamageVulnerability;

        // 创造之心
        HEART_OF_CREATION = new HeartOfCreationConfig(builder);
        HEART_OF_CREATION_COOLDOWN = HEART_OF_CREATION.cooldown;
        HEART_OF_CREATION_LIGHTNING_RANGE = HEART_OF_CREATION.lightningRange;
        HEART_OF_CREATION_LIGHTNING_DAMAGE = HEART_OF_CREATION.lightningDamage;





        SPEC = builder.build();
    }

    private ConfigCommon() {
    }

    public static boolean isBoss(ResourceLocation entityId) {
        return COMPLETE_BOSS_LIST.get().contains(entityId.toString());
    }

    public static boolean isCursedRingEnabled() {
        return CURSED_RING_ENABLED.get();
    }
}
