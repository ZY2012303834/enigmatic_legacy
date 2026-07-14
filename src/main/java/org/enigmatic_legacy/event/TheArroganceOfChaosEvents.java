package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.TheArroganceOfChaos;
import org.enigmatic_legacy.util.MajesticElytraHelper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 混沌之傲事件逻辑。
 *
 * <p>原扩展把大部分逻辑直接写在物品类里。当前项目更倾向于把物品静态数据、
 * tooltip 与事件行为拆开，因此这里集中处理运行时效果：</p>
 *
 * <p>1. 飞行期间记录持续飞行时间和最近速度，用于俯冲落地伤害计算；
 * 2. 减免背后伤害、摔落伤害和撞墙伤害；
 * 3. 滑翔落地时造成范围伤害。</p>
 */
public final class TheArroganceOfChaosEvents {
    private static final int DESCENDING_MIN_FLIGHT_TICKS = 20;
    private static final int DESCENDING_LANDING_GRACE_TICKS = 20;
    private static final double BASE_DESCENDING_RANGE = 3.5D;
    private static final double BACK_DAMAGE_DOT_THRESHOLD = -0.5D;
    private static final double KNOCKBACK_STRENGTH = 0.5D;
    private static final int MIN_ENDER_PARTICLES = 48;

    private static final ConcurrentMap<UUID, Vec3> LAST_MOVEMENTS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, Integer> FLYING_TICKS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, Long> LAST_FLIGHT_GAME_TICKS = new ConcurrentHashMap<>();

    private TheArroganceOfChaosEvents() {
    }

    /**
     * 每 tick 刷新混沌之傲的服务端飞行状态。
     *
     * <p>俯冲落地伤害依赖落地前速度。这里每 3 tick 记录一次玩家滑翔速度，
     * 对齐原扩展的节奏，避免最后一个落地 tick 的碰撞修正把速度压低到接近 0。</p>
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        /*
         * 这里必须读取“当前实际承担鞘翅飞行的物品栈”，不能单独扫描玩家是否佩戴了混沌之傲。
         * 当 Curios 背饰栏存在多个鞘翅时，MajesticElytraEvents 只会消耗当前生效鞘翅的耐久；
         * 如果这里额外查到另一个未生效的混沌之傲，就会出现“没有消耗混沌之傲耐久却触发落地伤害”的问题。
         */
        ItemStack stack = MajesticElytraHelper.getEquippedStack(player);

        if (stack.isEmpty() || !stack.is(ModItems.CHAOS_ELYTRA.get()) || !TheArroganceOfChaos.canUse(player)) {
            LAST_MOVEMENTS.remove(player.getUUID());
            FLYING_TICKS.remove(player.getUUID());
            LAST_FLIGHT_GAME_TICKS.remove(player.getUUID());
            return;
        }

        if (player.isFallFlying()) {
            FLYING_TICKS.merge(player.getUUID(), 1, Integer::sum);
            LAST_FLIGHT_GAME_TICKS.put(player.getUUID(), player.level().getGameTime());

            if (player.tickCount % 3 == 0) {
                LAST_MOVEMENTS.put(player.getUUID(), player.getDeltaMovement());
            }
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                if (player.getAbilities().flying) {
                    clearFlightRecord(player);
                    return;
                }

                if (isLanding(player) && tryTriggerDescending(serverPlayer, stack)) {
                    return;
                }
            }

            clearExpiredFlightRecord(player);
        }
    }

    /**
     * 混沌之傲的防御效果。
     *
     * <p>原扩展会减免背后伤害、摔落伤害和飞行撞墙伤害。
     * 背后判定只看水平面，并要求攻击来源明确位于玩家背后扇区，避免正面或侧前方攻击误触发减伤。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player) || !TheArroganceOfChaos.canUse(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (!isProtectedDamage(player, source)) {
            return;
        }

        double resistance = TheArroganceOfChaos.getDamageResistance();
        event.setNewDamage((float) (event.getNewDamage() * (1.0D - resistance)));
    }

    /**
     * 尝试触发混沌之傲俯冲落地技能。
     *
     * <p>该方法由通用鞘翅落地事件调用。原因是壮丽鞘翅和混沌之傲共享背饰槽飞行修复，
     * 通用落地免伤会先处理“刚结束滑翔”的 fall 事件；在那个时间点识别混沌之傲俯冲，
     * 可以避免俯冲事件被普通免摔逻辑吞掉。</p>
     */
    public static boolean tryTriggerDescending(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()
                || !stack.is(ModItems.CHAOS_ELYTRA.get())
                || !TheArroganceOfChaos.canUse(player)
                || !hasEnoughFlightTime(player)) {
            return false;
        }

        Vec3 lastMovement = LAST_MOVEMENTS.getOrDefault(player.getUUID(), player.getDeltaMovement());

        /*
         * 落地 tick 的当前速度经常已经被碰撞修正压到接近 0，因此这里使用滑翔期间缓存的最后速度。
         * 只有速度达到配置阈值才触发范围伤害，避免低速贴地滑翔、短距离起飞或状态切换误触发。
         */
        if (!hasEnoughDescendingSpeed(lastMovement)) {
            clearFlightRecord(player);
            return false;
        }

        double range = BASE_DESCENDING_RANGE + lastMovement.length();
        double damageMultiplier = Math.pow(
                ConfigCommon.CHAOS_ELYTRA_DESCENDING_POWER_MODIFIER.get(),
                Math.abs(lastMovement.y)
        );
        float damage = (float) Math.max(4.0D, player.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);

        damageNearbyEntities(player, range, damage);
        spawnDescendingEffects(player, range);

        FLYING_TICKS.remove(player.getUUID());
        LAST_FLIGHT_GAME_TICKS.remove(player.getUUID());
        LAST_MOVEMENTS.put(player.getUUID(), Vec3.ZERO);
        return true;
    }

    private static void damageNearbyEntities(ServerPlayer player, double range, float damage) {
        AABB area = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> target != player && target.isAlive() && isInsideSphericalRange(player, target, range)
        );

        for (LivingEntity target : targets) {
            knockAwayFromImpact(player, target);
            target.invulnerableTime = 0;
            target.hurt(player.damageSources().source(DamageTypes.MAGIC, player), damage);
        }
    }

    private static boolean isInsideSphericalRange(Player player, LivingEntity target, double range) {
        Vec3 center = player.getBoundingBox().getCenter();

        /*
         * AABB#distanceToSqr 计算点到目标碰撞箱的最近距离。
         * 这样大型实体只要碰撞箱进入球形半径就会被命中，
         * 小型实体则按它自身碰撞箱位置判断，不再吃到立方体角落里的超范围伤害。
         */
        return target.getBoundingBox().distanceToSqr(center) <= range * range;
    }

    private static void knockAwayFromImpact(Player player, LivingEntity target) {
        double distance = Math.max(0.1D, target.distanceTo(player));
        float modifier = (float) Math.min(1.0D, 1.2D / distance);
        Vec3 horizontal = target.position()
                .subtract(player.position())
                .multiply(1.0D, 0.0D, 1.0D);

        if (horizontal.lengthSqr() < 1.0E-4D) {
            return;
        }

        Vec3 push = horizontal.normalize().scale(KNOCKBACK_STRENGTH * modifier);
        target.push(push.x, target.onGround() ? 1.2D * modifier : 0.0D, push.z);
        target.hurtMarked = true;
    }

    private static void spawnDescendingEffects(ServerPlayer player, double range) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS,
                1.0F,
                0.8F
        );

        /*
         * 粒子范围直接使用本次实际伤害半径。
         * vanilla 的普通 PORTAL 粒子会在客户端按生命周期向生成点回收，看起来像向内收缩。
         * 因此这里使用 REVERSE_PORTAL，并让所有粒子都从冲击中心生成，再沿随机方向飞出，
         * 形成真正从中心向外扩散的末影冲击波。
         */
        int particles = Math.max(MIN_ENDER_PARTICLES, (int) (range * range * 6.0D));
        double centerX = player.getX();
        double centerY = player.getY() + 0.25D;
        double centerZ = player.getZ();

        for (int i = 0; i < particles; i++) {
            Vec3 direction = randomOutwardDirection(player);
            double speed = range * (0.08D + player.getRandom().nextDouble() * 0.06D);

            level.sendParticles(
                    ParticleTypes.REVERSE_PORTAL,
                    centerX,
                    centerY,
                    centerZ,
                    0,
                    direction.x * speed,
                    direction.y * speed,
                    direction.z * speed,
                    1.0D
            );
        }
    }

    private static Vec3 randomOutwardDirection(Player player) {
        double x = player.getRandom().nextDouble() * 2.0D - 1.0D;
        double y = player.getRandom().nextDouble() * 0.8D;
        double z = player.getRandom().nextDouble() * 2.0D - 1.0D;
        Vec3 direction = new Vec3(x, y, z);

        if (direction.lengthSqr() < 1.0E-4D) {
            return new Vec3(1.0D, 0.0D, 0.0D);
        }

        return direction.normalize();
    }

    private static boolean isProtectedDamage(Player player, DamageSource source) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.FLY_INTO_WALL)) {
            return true;
        }

        Entity attacker = source.getEntity() != null ? source.getEntity() : source.getDirectEntity();

        if (attacker == null || attacker == player) {
            return false;
        }

        Vec3 look = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        Vec3 sourceToPlayer = attacker.position()
                .subtract(player.position())
                .multiply(1.0D, 0.0D, 1.0D);

        if (look.lengthSqr() < 1.0E-4D || sourceToPlayer.lengthSqr() < 1.0E-4D) {
            return false;
        }

        /*
         * player -> attacker 与玩家水平朝向的点积越小，攻击来源越接近玩家背后。
         * -0.5 约等于背后 120 度扇区；正面、侧前方和正侧方都不会触发背后减伤。
         */
        return sourceToPlayer.normalize().dot(look.normalize()) <= BACK_DAMAGE_DOT_THRESHOLD;
    }

    private static boolean hasEnoughFlightTime(ServerPlayer player) {
        int trackedTicks = FLYING_TICKS.getOrDefault(player.getUUID(), 0);
        return Math.max(trackedTicks, player.getFallFlyingTicks()) >= DESCENDING_MIN_FLIGHT_TICKS;
    }

    private static boolean hasEnoughDescendingSpeed(Vec3 lastMovement) {
        double minimumSpeed = ConfigCommon.CHAOS_ELYTRA_DESCENDING_MINIMUM_SPEED.get();

        /*
         * 速度阈值为 0 时视为关闭该限制，方便整合包或测试环境恢复“只看飞行时间”的触发方式。
         * 使用平方比较可以避免每次触发都额外开方，同时结果与 lastMovement.length() 的阈值一致。
         */
        return minimumSpeed <= 0.0D || lastMovement.lengthSqr() >= minimumSpeed * minimumSpeed;
    }

    private static boolean isLanding(Player player) {
        return player.onGround() || player.verticalCollisionBelow;
    }

    private static void clearExpiredFlightRecord(Player player) {
        Long lastFlightTick = LAST_FLIGHT_GAME_TICKS.get(player.getUUID());

        /*
         * 俯冲落地触发点不一定在玩家仍处于 isFallFlying() 的同一 tick 内。
         * 背饰栏鞘翅的服务端状态恢复、LivingFallEvent 和 PlayerTick.Post 的先后顺序
         * 会让落地后的第一个 Post tick 先看到“已经不在滑翔”，因此不能立刻清掉速度和飞行时长。
         * 保留一个短窗口，让落地事件或兜底落地检测还能拿到落地前的真实速度。
         */
        if (lastFlightTick == null
                || player.level().getGameTime() - lastFlightTick > DESCENDING_LANDING_GRACE_TICKS) {
            clearFlightRecord(player);
        }
    }

    private static void clearFlightRecord(Player player) {
        LAST_MOVEMENTS.remove(player.getUUID());
        FLYING_TICKS.remove(player.getUUID());
        LAST_FLIGHT_GAME_TICKS.remove(player.getUUID());
    }

}
