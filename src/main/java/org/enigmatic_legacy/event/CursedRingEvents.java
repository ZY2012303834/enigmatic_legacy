package org.enigmatic_legacy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.HeartOfCreationHelper;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

/**
 * 七咒之戒事件处理类。
 * <p>
 * 说明：
 * 原项目部分逻辑写在 CursedRing 类和 SuperpositionHandler 中。
 * 当前项目拆分为事件类，方便适配 NeoForge 1.21.1。
 */
public class CursedRingEvents {
    private static final String STARTING_CURSED_RING_GIVEN = "enigmatic_legacy_received_cursed_ring";
    private static final String LEGACY_STARTING_CURSED_RING_GIVEN = "EnigmaticLegacyStartingCursedRingGiven";

    /**
     * 创造之心会给予飞行能力。
     * 为避免原版 PhantomSpawner 因飞行能力导致七咒幻翼压力失效，
     * 这里给七咒之戒补一个独立的幻翼刷新冷却。
     */
    private static final String HEART_CREATION_PHANTOM_COOLDOWN_TAG =
            "enigmatic_legacy_heart_creation_phantom_cooldown";

    /**
     * 玩家每秒处理一次七咒之戒的仇恨逻辑。
     * <p>
     * PlayerTickEvent.Post 会在逻辑客户端和逻辑服务端都触发，
     * 所以具体逻辑中必须检查 level().isClientSide()。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // 复刻原项目：七咒之戒佩戴者着火后，燃烧时间会不断延长。
        // 注意：这里只在“已经着火”时延长，不会主动点燃玩家。
        // 所以跳进水里、雨水灭火、其他原版灭火方式依然有效。
        if (!player.level().isClientSide()
                && player.isOnFire()
                && CursedRingHelper.hasCursedRing(player)) {
            player.setRemainingFireTicks(player.getRemainingFireTicks() + 2);
        }

        // 其他七咒逻辑仍然每秒执行一次，避免中立生物仇恨和末影人传送每 tick 检查。
        if (player.tickCount % 20 != 0) {
            return;
        }

        CursedRingHelper.tickCurses(player);

        if (player instanceof ServerPlayer serverPlayer) {
            tickHeartOfCreationPhantomSpawn(serverPlayer);
        }
    }

    /**
     * 七咒之戒调整伤害：
     * 1. 佩戴者受到更多伤害。
     * 2. 佩戴者攻击怪物时，伤害降低。
     * 3. 佩戴者的护甲减伤效果降低。
     * <p>
     * NeoForge 1.21.1 使用 LivingIncomingDamageEvent 替代旧版 LivingHurtEvent。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (target instanceof Player player && CursedRingHelper.hasCursedRing(player)) {
            float multiplier = ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() / 100.0F;
            event.setAmount(event.getAmount() * multiplier);

            float armorDebuff = ConfigCommon.CURSED_RING_ARMOR_DEBUFF.get() / 100.0F;
            event.addReductionModifier(
                    DamageContainer.Reduction.ARMOR,
                    (container, baseReduction) -> baseReduction * Math.max(0.0F, 1.0F - armorDebuff)
            );
        }

        if (event.getSource().getEntity() instanceof Player attacker
                && CursedRingHelper.hasCursedRing(attacker)
                && target instanceof Enemy) {
            float debuff = ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() / 100.0F;
            event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - debuff));
        }
    }

    /**
     * 七咒之戒佩戴者受到更多击退。
     */
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        float multiplier = ConfigCommon.CURSED_RING_KNOCKBACK_DEBUFF.get() / 100.0F;
        event.setStrength(event.getStrength() * multiplier);
    }

    /**
     * 七咒之戒佩戴者击杀生物时获得更多经验。
     */
    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        Player attacker = event.getAttackingPlayer();

        if (attacker == null) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(attacker)) {
            return;
        }

        int original = event.getDroppedExperience();
        double multiplier = ConfigCommon.CURSED_RING_EXPERIENCE_BONUS.get() / 100.0D;

        event.setDroppedExperience((int) Math.round(original * multiplier));
    }

    /**
     * 七咒之戒给附魔台提供额外附魔等级。
     */
    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        int bonus = ConfigCommon.CURSED_RING_ENCHANTING_BONUS.get();

        if (bonus <= 0 || !hasCursedPlayerNear(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 8.0D)) {
            return;
        }

        event.setEnchantLevel(event.getEnchantLevel() + bonus);
    }

    /**
     * 七咒之戒佩戴者击杀原版生物时获得额外特殊掉落。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!ConfigCommon.CURSED_RING_SPECIAL_DROPS_ENABLED.get() || !event.isRecentlyHit()) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player player) || !CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        LivingEntity killed = event.getEntity();

        if (killed.getClass() == Shulker.class) {
            addDropWithChance(event, new ItemStack(ModItems.ASTRAL_DUST.get()), 20);
        } else if (killed.getClass() == Skeleton.class || killed.getClass() == Stray.class) {
            addDrop(event, randomStack(killed, Items.ARROW, 3, 15));
        } else if (killed.getClass() == Zombie.class || killed.getClass() == Husk.class) {
            addDropWithChance(event, randomStack(killed, Items.SLIME_BALL, 1, 3), 25);
        } else if (killed.getClass() == Spider.class || killed.getClass() == CaveSpider.class) {
            addDrop(event, randomStack(killed, Items.STRING, 2, 12));
        } else if (killed.getClass() == Guardian.class) {
            addDropWithChance(event, new ItemStack(Items.NAUTILUS_SHELL), 15);
            addDrop(event, randomStack(killed, Items.PRISMARINE_CRYSTALS, 2, 5));
        } else if (killed instanceof ElderGuardian) {
            addDrop(event, randomStack(killed, Items.PRISMARINE_CRYSTALS, 4, 16));
            addDrop(event, randomStack(killed, Items.PRISMARINE_SHARD, 7, 28));

            addOneOf(event,
                    new ItemStack(ModItems.GUARDIAN_HEART.get()),
                    new ItemStack(ModItems.COSMIC_HEART.get()),
                    new ItemStack(Items.HEART_OF_THE_SEA),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
                    new ItemStack(Items.ENDER_EYE)
            );

        } else if (killed.getClass() == EnderMan.class) {
            addDropWithChance(event, randomStack(killed, Items.ENDER_EYE, 1, 2), 40);
        } else if (killed.getClass() == Blaze.class) {
            addDrop(event, randomStack(killed, Items.BLAZE_POWDER, 0, 5));
        } else if (killed.getClass() == ZombifiedPiglin.class) {
            addDropWithChance(event, randomStack(killed, Items.GOLD_INGOT, 1, 3), 40);
            addDropWithChance(event, randomStack(killed, Items.GLOWSTONE_DUST, 1, 7), 30);
        } else if (killed.getClass() == Witch.class) {
            addDropWithChance(event, new ItemStack(Items.GHAST_TEAR), 30);
            addDropWithChance(event, randomStack(killed, Items.PHANTOM_MEMBRANE, 1, 3), 50);
        } else if (killed.getClass() == Pillager.class || killed.getClass() == Vindicator.class) {
            addDrop(event, randomStack(killed, Items.EMERALD, 0, 4));
        } else if (killed.getClass() == Villager.class) {
            addDrop(event, randomStack(killed, Items.EMERALD, 2, 6));
        } else if (killed.getClass() == Creeper.class) {
            addDrop(event, randomStack(killed, Items.GUNPOWDER, 4, 12));
        } else if (killed.getClass() == PiglinBrute.class) {
            addDropWithChance(event, new ItemStack(Items.NETHERITE_SCRAP), 20);
        } else if (killed.getClass() == Evoker.class) {
            addDrop(event, new ItemStack(Items.TOTEM_OF_UNDYING));
            addDrop(event, randomStack(killed, Items.EMERALD, 5, 20));
            addDropWithChance(event, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 10);
            addDropWithChance(event, randomStack(killed, Items.ENDER_PEARL, 1, 3), 30);
            addDropWithChance(event, randomStack(killed, Items.BLAZE_ROD, 2, 4), 30);
            addDropWithChance(event, randomStack(killed, Items.EXPERIENCE_BOTTLE, 4, 10), 50);
        } else if (killed.getClass() == WitherSkeleton.class) {
            addDrop(event, randomStack(killed, Items.BLAZE_POWDER, 0, 3));
            addDropWithChance(event, new ItemStack(Items.GHAST_TEAR), 20);
            addDropWithChance(event, new ItemStack(Items.NETHERITE_SCRAP), 7);
        } else if (killed.getClass() == Ghast.class) {
            addDrop(event, randomStack(killed, Items.PHANTOM_MEMBRANE, 1, 4));
        } else if (killed.getClass() == Drowned.class) {
            addDropWithChance(event, randomStack(killed, Items.LAPIS_LAZULI, 1, 3), 30);
        } else if (killed.getClass() == Vex.class) {
            addDrop(event, randomStack(killed, Items.GLOWSTONE_DUST, 0, 2));
            addDropWithChance(event, new ItemStack(Items.PHANTOM_MEMBRANE), 30);
        } else if (killed.getClass() == Phantom.class || killed.getClass() == Silverfish.class) {
        } else if (killed.getClass() == Piglin.class) {
            addDropWithChance(event, randomStack(killed, Items.GOLD_INGOT, 2, 4), 50);
        } else if (killed.getClass() == Ravager.class) {
            addDrop(event, randomStack(killed, Items.EMERALD, 3, 10));
            addDrop(event, randomStack(killed, Items.LEATHER, 2, 7));
            addDropWithChance(event, randomStack(killed, Items.DIAMOND, 0, 4), 50);
        } else if (killed.getClass() == MagmaCube.class) {
            addDropWithChance(event, new ItemStack(Items.BLAZE_POWDER), 50);
        } else if (killed.getClass() == Chicken.class) {
            addDropWithChance(event, new ItemStack(Items.EGG), 50);
        } else if (killed instanceof WitherBoss) {
            // 凋零
            addDrop(event, randomStack(killed, ModItems.DARKEST_SCROLL.get(), 2, 6));
            addDrop(event, new ItemStack(ModItems.EVIL_ESSENCE.get()));
        } else if (killed instanceof EnderDragon) {
            addDrop(event, new ItemStack(ModItems.COSMIC_HEART.get()));
        }
    }

    /**
     * 七咒之戒的失眠诅咒：允许幻翼按原版 PhantomSpawner 流程额外生成。
     */
    @SubscribeEvent
    public static void onPlayerSpawnPhantoms(PlayerSpawnPhantomsEvent event) {
        if (ConfigCommon.CURSED_RING_DISABLE_INSOMNIA.get()) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(event.getEntity())) {
            return;
        }

        event.setResult(PlayerSpawnPhantomsEvent.Result.ALLOW);
    }

    /**
     * 七咒之戒的失眠诅咒：
     * 佩戴七咒之戒时，玩家不能开始睡觉。
     *
     * 原项目行为：
     * Trying to sleep with Ring of the Seven Curses now displays message indicating player can't sleep.
     */
    @SubscribeEvent
    public static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (ConfigCommon.CURSED_RING_DISABLE_INSOMNIA.get()) {
            return;
        }

        ServerPlayer player = event.getEntity();

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        /*
         * OTHER_PROBLEM 会阻止进入睡眠。
         * 同时主动给玩家发提示，复刻原项目“尝试睡觉时显示不能睡”的行为。
         */
        event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);

        player.displayClientMessage(
                Component.translatable("message.enigmatic_legacy.cursed_ring.no_sleep")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                true
        );
    }

    /**
     * 兜底：
     * 如果玩家已经处于睡眠状态后才装备/获得七咒之戒，
     * 或者被其他模组强制进入睡眠状态，这里会让他立刻不能继续睡。
     *
     * NeoForge 的 CanContinueSleepingEvent 用于覆盖“是否可以继续睡觉”的检查。
     */
    @SubscribeEvent
    public static void onCanContinueSleeping(CanContinueSleepingEvent event) {
        if (ConfigCommon.CURSED_RING_DISABLE_INSOMNIA.get()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        event.setContinueSleeping(false);

        player.displayClientMessage(
                Component.translatable("message.enigmatic_legacy.cursed_ring.no_sleep")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                true
        );
    }

    /**
     * 创造之心兼容用幻翼刷新。
     * 原逻辑只通过 PlayerSpawnPhantomsEvent 放行原版 PhantomSpawner。
     * 但创造之心会授予飞行能力，可能导致原版幻翼刷新链不再稳定触发。
     * 所以这里仅在：
     * 1. 玩家佩戴七咒之戒；
     * 2. 玩家装备创造之心；
     * 3. 失眠诅咒未被配置禁用；
     * 4. 当前为夜晚且头顶可见天空；
     * 时，额外生成少量幻翼。
     */
    private static void tickHeartOfCreationPhantomSpawn(ServerPlayer player) {
        if (ConfigCommon.CURSED_RING_DISABLE_INSOMNIA.get()) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        if (!HeartOfCreationHelper.hasHeartOfCreationEquipped(player)) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        if (level.isDay()) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        if (!level.canSeeSky(playerPos)) {
            return;
        }

        int cooldown = player.getPersistentData().getInt(HEART_CREATION_PHANTOM_COOLDOWN_TAG);

        if (cooldown > 0) {
            player.getPersistentData().putInt(
                    HEART_CREATION_PHANTOM_COOLDOWN_TAG,
                    Math.max(0, cooldown - 20)
            );
            return;
        }

        /*
         * 不每次冷却结束都生成，避免过于密集。
         * 每秒检查一次，25% 概率触发。
         */
        if (player.getRandom().nextDouble() > 0.25D) {
            player.getPersistentData().putInt(HEART_CREATION_PHANTOM_COOLDOWN_TAG, 20 * 15);
            return;
        }

        int count = 1 + player.getRandom().nextInt(2);

        for (int i = 0; i < count; i++) {
            spawnCursedPhantom(level, player);
        }

        /*
         * 下一轮额外刷新间隔：
         * 60 ~ 120 秒。
         */
        player.getPersistentData().putInt(
                HEART_CREATION_PHANTOM_COOLDOWN_TAG,
                20 * (60 + player.getRandom().nextInt(61))
        );
    }

    /**
     * 在玩家上方生成一只七咒幻翼。
     */
    private static void spawnCursedPhantom(ServerLevel level, ServerPlayer player) {
        Phantom phantom = EntityType.PHANTOM.create(level);

        if (phantom == null) {
            return;
        }

        double x = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 20.0D;
        double y = player.getY() + 20.0D + player.getRandom().nextInt(10);
        double z = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 20.0D;

        phantom.moveTo(
                x,
                y,
                z,
                player.getRandom().nextFloat() * 360.0F,
                0.0F
        );

        phantom.setTarget(player);

        level.addFreshEntity(phantom);
    }

    /**
     * 七咒之戒死亡时保留。
     * <p>
     * 旧版通过 ICurioItem#getDropRule 返回 ALWAYS_KEEP。当前 Curios 版本同时提供 DropRulesEvent，
     * 这里加一层兜底，确保戒指不会被 Curios 死亡掉落流程丢出。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioDropRules(DropRulesEvent event) {
        event.addOverride(
                stack -> stack.is(ModItems.CURSED_RING.get()),
                ICurio.DropRule.ALWAYS_KEEP
        );
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        /*
         * 七咒之戒首次进世界发放逻辑。
         *
         * 注意：
         * 不再使用 CURSED_RING_ULTRA_HARDCORE 控制初始发放。
         * 当前项目的 ItemOptionsConfig 里 CursedRingEnabled 默认就是 true，
         * 注释也说明它可用于控制初始给予、Curios 佩戴、诅咒功能触发。
         */
        if (!ConfigCommon.CURSED_RING_ENABLED.get()) {
            return;
        }

        /*
         * 如果玩家已经佩戴，或者背包里已经有七咒之戒，
         * 只记录已发放标记，不再重复给予。
         */
        if (CursedRingHelper.hasCursedRing(player) || hasCursedRingInInventory(player)) {
            markReceivedStartingCursedRing(player);
            return;
        }

        /*
         * 如果已经记录发放过，则不重复发放。
         */
        if (hasReceivedStartingCursedRing(player)) {
            return;
        }

        ItemStack cursedRing = new ItemStack(ModItems.CURSED_RING.get());

        /*
         * 先尝试放入背包。
         * 如果背包满了，就掉落到玩家脚下，避免因为 addItem 失败导致戒指丢失。
         */
        if (!player.addItem(cursedRing)) {
            player.drop(cursedRing, false);
        }

        /*
         * 只有真正执行过给予逻辑后，再写入已发放标记。
         */
        markReceivedStartingCursedRing(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (hasReceivedStartingCursedRing(event.getOriginal())) {
            markReceivedStartingCursedRing(event.getEntity());
        }
    }

    private static boolean hasReceivedStartingCursedRing(Player player) {
        return player.getPersistentData().getBoolean(STARTING_CURSED_RING_GIVEN)
                || player.getPersistentData().getBoolean(LEGACY_STARTING_CURSED_RING_GIVEN);
    }

    private static void markReceivedStartingCursedRing(Player player) {
        player.getPersistentData().putBoolean(STARTING_CURSED_RING_GIVEN, true);
        player.getPersistentData().putBoolean(LEGACY_STARTING_CURSED_RING_GIVEN, true);
    }

    private static boolean hasCursedRingInInventory(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.is(ModItems.CURSED_RING.get())) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasCursedPlayerNear(Level level, double x, double y, double z, double range) {
        AABB box = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);

        return !level.getEntitiesOfClass(Player.class, box, CursedRingHelper::hasCursedRing).isEmpty();
    }

    private static ItemStack randomStack(LivingEntity entity, net.minecraft.world.level.ItemLike item, int min, int max) {
        int count = min + entity.getRandom().nextInt(max - min + 1);
        return new ItemStack(item, count);
    }

    private static void addDropWithChance(LivingDropsEvent event, ItemStack stack, int chance) {
        if (event.getEntity().getRandom().nextInt(100) < chance) {
            addDrop(event, stack);
        }
    }

    private static void addOneOf(LivingDropsEvent event, ItemStack... stacks) {
        if (stacks.length == 0) {
            return;
        }

        addDrop(event, stacks[event.getEntity().getRandom().nextInt(stacks.length)]);
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        LivingEntity entity = event.getEntity();
        event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack));
    }
}
