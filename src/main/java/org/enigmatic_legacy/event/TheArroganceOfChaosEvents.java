package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
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
 * 2. 深渊强化生效时提供 +3 护甲；
 * 3. 减免背后伤害、摔落伤害和撞墙伤害；
 * 4. 被助推状态下垂直俯冲落地时造成范围伤害。</p>
 */
public final class TheArroganceOfChaosEvents {
    private static final ResourceLocation ABYSS_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "chaos_elytra_abyss_armor"
    );

    private static final double DESCENDING_MIN_LOOK_Y = -0.95D;
    private static final int DESCENDING_MIN_FLIGHT_TICKS = 36;
    private static final double BASE_DESCENDING_RANGE = 3.5D;
    private static final double ABYSS_RANGE_MULTIPLIER = 1.25D;
    private static final double ABYSS_DAMAGE_MULTIPLIER = 1.1D;
    private static final double KNOCKBACK_STRENGTH = 0.5D;

    private static final ConcurrentMap<UUID, Vec3> LAST_MOVEMENTS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, Integer> FLYING_TICKS = new ConcurrentHashMap<>();

    private TheArroganceOfChaosEvents() {
    }

    /**
     * 每 tick 刷新混沌之傲的服务端飞行状态与深渊强化护甲。
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

        removeModifier(player, Attributes.ARMOR, ABYSS_ARMOR_ID);

        ItemStack stack = MajesticElytraHelper.getEquippedChaosElytraStack(player);

        if (stack.isEmpty() || !TheArroganceOfChaos.canUse(player)) {
            LAST_MOVEMENTS.remove(player.getUUID());
            FLYING_TICKS.remove(player.getUUID());
            return;
        }

        if (TheArroganceOfChaos.hasAbyssBoost(player)) {
            addArmorModifier(player);
        }

        if (player.isFallFlying()) {
            FLYING_TICKS.merge(player.getUUID(), 1, Integer::sum);

            if (player.tickCount % 3 == 0) {
                LAST_MOVEMENTS.put(player.getUUID(), player.getDeltaMovement());
            }
        } else {
            FLYING_TICKS.remove(player.getUUID());
            LAST_MOVEMENTS.put(player.getUUID(), Vec3.ZERO);
        }
    }

    /**
     * 混沌之傲的防御效果。
     *
     * <p>原扩展会减免背后伤害、摔落伤害和飞行撞墙伤害。
     * 背后判定采用攻击直接实体相对玩家朝向的点积：直接实体位于玩家视线反方向时，
     * 视为“来自背后”。</p>
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

        double resistance = TheArroganceOfChaos.getDamageResistance(player);

        if (resistance >= 1.0D) {
            event.setNewDamage(0.0F);
            return;
        }

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
                || player.getLookAngle().y > DESCENDING_MIN_LOOK_Y
                || player.getCooldowns().isOnCooldown(ModItems.CHAOS_ELYTRA.get())
                || !hasEnoughFlightTime(player)
                || !hasEnoughImpactSpace(player.blockPosition(), player.level())) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(
                    ModItems.CHAOS_ELYTRA.get(),
                    ConfigCommon.CHAOS_ELYTRA_DESCENDING_COOLDOWN.get()
            );
        }

        Vec3 lastMovement = LAST_MOVEMENTS.getOrDefault(player.getUUID(), player.getDeltaMovement());
        boolean abyssBoost = TheArroganceOfChaos.hasAbyssBoost(player);
        double range = BASE_DESCENDING_RANGE + lastMovement.length() * (abyssBoost ? ABYSS_RANGE_MULTIPLIER : 1.0D);
        double damageMultiplier = Math.pow(
                ConfigCommon.CHAOS_ELYTRA_DESCENDING_POWER_MODIFIER.get(),
                Math.abs(lastMovement.y)
        ) * (abyssBoost ? ABYSS_DAMAGE_MULTIPLIER : 1.0D);
        float damage = (float) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);

        damageNearbyEntities(player, range, damage);
        spawnDescendingEffects(player, range);

        FLYING_TICKS.remove(player.getUUID());
        LAST_MOVEMENTS.put(player.getUUID(), Vec3.ZERO);
        return true;
    }

    private static void damageNearbyEntities(ServerPlayer player, double range, float damage) {
        AABB area = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> target != player && target.isAlive()
        );

        for (LivingEntity target : targets) {
            knockAwayFromImpact(player, target);
            target.hurt(player.damageSources().playerAttack(player), damage);
        }
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

        level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(
                ParticleTypes.DRAGON_BREATH,
                player.getX(),
                player.getY() + 0.2D,
                player.getZ(),
                Math.max(24, (int) (range * 8.0D)),
                range * 0.35D,
                0.2D,
                range * 0.35D,
                0.02D
        );
    }

    private static boolean isProtectedDamage(Player player, DamageSource source) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.FLY_INTO_WALL)) {
            return true;
        }

        Entity directEntity = source.getDirectEntity();
        return directEntity != null
                && directEntity.position().subtract(player.position()).dot(player.getLookAngle()) < 0.0D;
    }

    private static boolean hasEnoughFlightTime(ServerPlayer player) {
        int trackedTicks = FLYING_TICKS.getOrDefault(player.getUUID(), 0);
        return Math.max(trackedTicks, player.getFallFlyingTicks()) > DESCENDING_MIN_FLIGHT_TICKS;
    }

    private static boolean hasEnoughImpactSpace(BlockPos pos, net.minecraft.world.level.Level level) {
        int space = 0;

        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(2, 2, 2), pos.offset(-2, -2, -2))) {
            if (level.getBlockState(blockPos).isAir()) {
                space += 3;
            } else {
                space -= 1;
            }
        }

        return space > 0;
    }

    private static void addArmorModifier(Player player) {
        AttributeInstance instance = player.getAttribute(Attributes.ARMOR);

        if (instance == null) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(
                ABYSS_ARMOR_ID,
                3.0D,
                AttributeModifier.Operation.ADD_VALUE
        ));
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
    }
}
