package org.enigmatic_legacy.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.item.items.scroll.ScrollOfThunderEmbrace;
import org.enigmatic_legacy.util.ScrollOfThunderEmbraceHelper;

import java.util.List;

/**
 * 万钧之护卷轴的全局事件逻辑。
 *
 * <p>物品类负责 Curios 限制和 tooltip；这里负责必须从事件管线切入的效果：
 * 攻击时改写最终伤害、给目标积累电荷、电荷过高时召唤闪电，
 * 以及佩戴者的闪电伤害减免。</p>
 */
public final class ScrollOfThunderEmbraceEvents {
    private ScrollOfThunderEmbraceEvents() {
    }

    /**
     * 处理伤害结算阶段的两个效果：
     *
     * <p>1. 玩家佩戴万钧之护卷轴攻击时，根据目标护甲提高最终伤害；</p>
     * <p>2. 玩家用支持横扫的武器攻击时，为目标增加电荷。</p>
     *
     * <p>使用 LivingDamageEvent.Pre 是因为该事件发生在护甲、抗性等减伤完成之后，
     * 修改的是“最终要扣血的伤害”。这与扩展项目中对 LivingDamageEvent 的使用更接近。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!(event.getSource().getEntity() instanceof Player player)) {
            handleLightningFinalDamage(event, target);
            return;
        }

        if (!ScrollOfThunderEmbraceHelper.hasScroll(player)) {
            handleLightningFinalDamage(event, target);
            return;
        }

        float damage = event.getNewDamage();

        if (player.getMainHandItem().canPerformAction(ItemAbilities.SWORD_SWEEP)) {
            addElectricCharge(target, damage, player);
        }

        event.setNewDamage(ScrollOfThunderEmbrace.modifyArmorPiercingDamage(target, damage));
        handleLightningFinalDamage(event, target);
    }

    /**
     * 在原始伤害进入减伤流程前，完全拦截卷轴生成的无害闪电对佩戴者的伤害。
     *
     * <p>普通闪电只做最终伤害减半，不在这里处理；
     * 但卷轴生成的 HarmlessThunder 对佩戴者应完全无效，越早取消越不容易触发其它连锁影响。</p>
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
            return;
        }

        if (!ScrollOfThunderEmbraceHelper.hasScroll(event.getEntity())) {
            return;
        }

        Entity directEntity = event.getSource().getDirectEntity();

        if (directEntity != null && directEntity.getTags().contains(ScrollOfThunderEmbrace.HARMLESS_THUNDER_TAG)) {
            event.setCanceled(true);
        }
    }

    /**
     * 每 tick 处理所有 LivingEntity 身上的电荷。
     *
     * <p>电荷小于等于阈值时缓慢衰减；超过阈值时尝试召唤闪电。
     * 如果目标周围极近距离存在万钧之护卷轴佩戴者，则跳过这次落雷，
     * 避免扩展项目中同样规避的“雷劈到主人”问题。</p>
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();
        int electric = data.getInt(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG);

        if (electric <= 0) {
            data.remove(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG);
            return;
        }

        if (electric <= ScrollOfThunderEmbrace.ELECTRIC_THRESHOLD) {
            writeElectricCharge(data, electric - 1);
            return;
        }

        if (hasNearbyThunderBearer(entity)) {
            return;
        }

        spawnHarmlessThunder(entity, electric);

        int remaining = (electric - ScrollOfThunderEmbrace.ELECTRIC_THRESHOLD) / 2
                + ScrollOfThunderEmbrace.ELECTRIC_REMAINING_BONUS;
        writeElectricCharge(data, remaining);
    }

    /**
     * 闪电即将击中实体时的额外保护。
     *
     * <p>卷轴生成的 HarmlessThunder 不应烧毁掉落物，也不应真正击中佩戴者。
     * 普通闪电不在这里取消，让原版雷击事件仍能正常发生，只在最终伤害阶段减半。</p>
     */
    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (!event.getLightning().getTags().contains(ScrollOfThunderEmbrace.HARMLESS_THUNDER_TAG)) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof ItemEntity
                || (entity instanceof LivingEntity livingEntity && ScrollOfThunderEmbraceHelper.hasScroll(livingEntity))) {
            event.setCanceled(true);
        }
    }

    /**
     * 佩戴者受到普通闪电伤害时减半。
     * 卷轴生成的无害闪电已经在 LivingIncomingDamageEvent 被取消，这里只兜底处理未被取消的闪电伤害。
     */
    private static void handleLightningFinalDamage(LivingDamageEvent.Pre event, LivingEntity target) {
        if (!event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
            return;
        }

        if (!ScrollOfThunderEmbraceHelper.hasScroll(target)) {
            return;
        }

        Entity directEntity = event.getSource().getDirectEntity();

        if (directEntity != null && directEntity.getTags().contains(ScrollOfThunderEmbrace.HARMLESS_THUNDER_TAG)) {
            event.setNewDamage(0.0F);
            return;
        }

        event.setNewDamage(event.getNewDamage() * 0.5F);
    }

    /**
     * 为目标增加电荷。
     *
     * <p>增长量 = 固定 60 + 随机 0~79 + 最终伤害 * 10。
     * 该公式直接来自扩展项目，使高伤害攻击更容易快速触发落雷。</p>
     */
    private static void addElectricCharge(LivingEntity target, float damage, Player attacker) {
        int gain = ScrollOfThunderEmbrace.ELECTRIC_BASE_GAIN
                + attacker.getRandom().nextInt(ScrollOfThunderEmbrace.ELECTRIC_RANDOM_GAIN_BOUND)
                + (int) (damage * ScrollOfThunderEmbrace.ELECTRIC_DAMAGE_GAIN);

        CompoundTag data = target.getPersistentData();
        int current = data.getInt(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG);
        data.putInt(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG, Math.max(0, current + gain));
    }

    private static void writeElectricCharge(CompoundTag data, int electric) {
        if (electric > 0) {
            data.putInt(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG, electric);
        } else {
            data.remove(ScrollOfThunderEmbrace.ELECTRIC_CHARGE_TAG);
        }
    }

    /**
     * 检查目标附近是否有万钧之护卷轴佩戴者。
     * 只检查玩家即可，因为卷轴装备限制本身只允许玩家使用。
     */
    private static boolean hasNearbyThunderBearer(LivingEntity target) {
        AABB area = target.getBoundingBox().inflate(ScrollOfThunderEmbrace.LIGHTNING_OWNER_SAFE_RADIUS);
        List<Player> players = target.level().getEntitiesOfClass(
                Player.class,
                area,
                ScrollOfThunderEmbraceHelper::hasScroll
        );

        return !players.isEmpty();
    }

    /**
     * 生成不会点火的闪电。
     *
     * <p>闪电被打上 HarmlessThunder tag 后，MixinLightningBolt 会阻止 spawnFire；
     * 伤害值按扩展项目公式放大为 lightning.getDamage() * electric / 600。</p>
     */
    private static void spawnHarmlessThunder(LivingEntity target, int electric) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);

        if (lightning == null) {
            return;
        }

        Vec3 position = Vec3.atBottomCenterOf(target.blockPosition());
        lightning.moveTo(position);
        lightning.setSilent(target.getRandom().nextBoolean());
        lightning.addTag(ScrollOfThunderEmbrace.HARMLESS_THUNDER_TAG);
        lightning.setDamage(lightning.getDamage() * electric / 600.0F);

        level.addFreshEntity(lightning);
    }
}
