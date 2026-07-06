package org.enigmatic_legacy.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.book.AnimalGuidebook;
import org.enigmatic_legacy.item.items.book.OdeToLiving;

import java.util.List;

/**
 * 七咒之戒工具类。
 * <p>
 * 这里集中处理：
 * 1. 判断玩家是否佩戴七咒之戒
 * 2. 激怒中立生物
 * 3. 让末影人随机传送
 */
public class CursedRingHelper {

    /**
     * 判断玩家是否在 Curios 栏位中佩戴七咒之戒。
     */
    public static boolean hasCursedRing(Player player) {
        if (!ConfigCommon.CURSED_RING_ENABLED.get()) {
            return false;
        }

        return CuriosLookupApi.hasCurio(player, ModItems.CURSED_RING.get());
    }

    /**
     * 判断指定戒指栏位是否允许放入七咒之戒。
     * <p>
     * Curios 的 ring 槽可以有多个，但七咒之戒本身只能佩戴一个。
     * 如果检查的是当前已经放着七咒之戒的同一个槽位，返回 true，避免 Curios 刷新装备状态时误判。
     */
    public static boolean canEquipCursedRing(Player player, String slotIdentifier, int slotIndex) {
        if (!ConfigCommon.CURSED_RING_ENABLED.get()) {
            return false;
        }

        return CuriosLookupApi.getStacksHandler(player, slotIdentifier)
                .map(ringHandler -> {
                    for (int slot = 0; slot < ringHandler.getStacks().getSlots(); slot++) {
                        ItemStack stack = ringHandler.getStacks().getStackInSlot(slot);

                        if (!stack.is(ModItems.CURSED_RING.get())) {
                            continue;
                        }

                        if (slot == slotIndex) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                })
                .orElse(true);
    }

    /**
     * 每秒处理一次七咒之戒的仇恨逻辑。
     */
    public static void tickCurses(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (!hasCursedRing(player)) {
            return;
        }

        angerNeutralMobs(player);
        teleportEndermen(player);
    }

    /**
     * 激怒周围中立生物。
     */
    private static void angerNeutralMobs(Player player) {
        double range = ConfigCommon.CURSED_RING_NEUTRAL_ANGER_RANGE.get();
        double xrayRange = ConfigCommon.CURSED_RING_NEUTRAL_XRAY_RANGE.get();

        AABB box = player.getBoundingBox().inflate(range);

        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> entity != player && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            double visibility = player.getVisibilityPercent(entity);
            double angerDistance = Math.max(range * visibility, xrayRange);

            if (entity.distanceToSqr(player) > angerDistance * angerDistance) {
                continue;
            }

            if (!player.hasLineOfSight(entity) && player.distanceTo(entity) > xrayRange) {
                continue;
            }

            // 猪灵使用原版仇恨 AI。
            // 但如果玩家佩戴无尽贪婪契约，猪灵必须保持中立。
            if (isNeutralAngerSuppressedByBooks(player, entity)) {
                continue;
            }

            if (entity instanceof Piglin piglin) {
                if (PactOfInfiniteAvariceHelper.hasPact(player)) {
                    continue;
                }

                if (piglin.getTarget() == null || !piglin.getTarget().isAlive()) {
                    angerPiglin(piglin, player);
                }

                continue;
            }

            if (!(entity instanceof NeutralMob)) {
                continue;
            }

            // 被驯服的生物不被七咒之戒激怒。
            switch (entity) {
                case TamableAnimal tamable when tamable.isTame() -> {
                    continue;
                }


                // 玩家制造的铁傀儡不被七咒之戒激怒。
                case IronGolem golem when golem.isPlayerCreated() -> {
                    continue;
                }


                // 可选：保护蜜蜂。
                case Bee bee when ConfigCommon.CURSED_RING_SAVE_THE_BEES.get() -> {
                    continue;
                }
                default -> {
                }
            }

            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());

            if (isNeutralAngerBlacklisted(entityId)) {
                continue;
            }

            if (entity instanceof Mob mob) {
                if (mob.getTarget() == null || !mob.getTarget().isAlive()) {
                    mob.setTarget(player);
                }
            }
        }
    }

    /**
     * 在七咒写入仇恨目标前先检查书本效果，避免先激怒再解除导致生物行为抽搐。
     */
    private static boolean isNeutralAngerSuppressedByBooks(Player player, LivingEntity entity) {
        if (isNeutralAngerSuppressedByAnimalGuide(player, entity)) {
            return true;
        }

        return OdeToLiving.isProtectedByOde(player, entity);
    }

    private static boolean isNeutralAngerSuppressedByAnimalGuide(Player player, LivingEntity entity) {
        return AnimalGuidebook.hasGuidebook(player)
                && (AnimalGuidebook.isProtectedAnimal(entity) || AnimalGuidebook.isTamableAnimal(entity));
    }

    /**
     * 通过猪灵的脑记忆写入七咒仇恨目标。
     */
    private static void angerPiglin(Piglin piglin, Player player) {
        if (!piglin.canAttack(player)) {
            return;
        }

        Brain<Piglin> brain = piglin.getBrain();
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        brain.eraseMemory(MemoryModuleType.CELEBRATE_LOCATION);
        brain.eraseMemory(MemoryModuleType.DANCING);
        brain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
        brain.setMemoryWithExpiry(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
        brain.setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, player.getUUID(), 600L);

        if (piglin.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            brain.setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }

    /**
     * 末影人随机传送到佩戴者附近。
     */
    private static void teleportEndermen(Player player) {
        double range = ConfigCommon.CURSED_RING_ENDERMAN_RANDOM_TELEPORT_RANGE.get();
        double frequency = ConfigCommon.CURSED_RING_ENDERMAN_RANDOM_TELEPORT_FREQUENCY.get();

        AABB box = player.getBoundingBox().inflate(range);

        List<EnderMan> endermen = player.level().getEntitiesOfClass(
                EnderMan.class,
                box,
                EnderMan::isAlive
        );

        for (EnderMan enderman : endermen) {
            if (player.getRandom().nextDouble() <= 0.002D * frequency) {
                if (teleportEndermanTowards(enderman, player) && player.hasLineOfSight(enderman)) {
                    enderman.setTarget(player);
                }
            }
        }
    }

    /**
     * 使用原版 teleportTowards 的坐标公式，但通过公开的 randomTeleport 执行传送。
     */
    private static boolean teleportEndermanTowards(EnderMan enderman, Player player) {
        Vec3 offset = new Vec3(
                enderman.getX() - player.getX(),
                enderman.getY(0.5D) - player.getEyeY(),
                enderman.getZ() - player.getZ()
        ).normalize();

        double x = enderman.getX() + (enderman.getRandom().nextDouble() - 0.5D) * 8.0D - offset.x * 16.0D;
        double y = enderman.getY() + enderman.getRandom().nextInt(16) - 8.0D - offset.y * 16.0D;
        double z = enderman.getZ() + (enderman.getRandom().nextDouble() - 0.5D) * 8.0D - offset.z * 16.0D;

        return enderman.randomTeleport(x, y, z, true);
    }

    /**
     * 中立生物仇恨黑名单。
     * 用途：
     * 七咒之戒第二诅咒会激怒周围中立生物。
     * 但部分生物不应该被激怒，例如：
     * - 其它模组的重要 NPC；
     * - Boss 型中立实体；
     * - the_bumblezone:bee_queen；
     * - 玩家希望保护的特定生物。
     * 配置项：
     * CursedRingNeutralAngerBlacklist
     * 格式：
     * namespace:entity_id
     */
    private static boolean isNeutralAngerBlacklisted(ResourceLocation entityId) {
        if (entityId == null) {
            return false;
        }

        for (String rawId : ConfigCommon.CURSED_RING_NEUTRAL_ANGER_BLACKLIST.get()) {
            ResourceLocation blacklistedId = ResourceLocation.tryParse(rawId.trim());

            if (blacklistedId == null) {
                continue;
            }

            if (entityId.equals(blacklistedId)) {
                return true;
            }
        }

        return false;
    }
}
