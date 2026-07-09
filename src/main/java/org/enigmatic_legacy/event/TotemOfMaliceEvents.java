package org.enigmatic_legacy.event;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.TotemOfMalice;
import org.enigmatic_legacy.network.TotemOfMaliceActivationPayload;

/**
 * 恶意图腾事件逻辑。
 *
 * <p>物品类负责能量、tooltip 和 Curios 佩戴限制。</p>
 * <p>这里集中处理战斗、保命爆发和铁砧修补，避免物品类直接监听世界事件。</p>
 */
public final class TotemOfMaliceEvents {
    private static final double REVIVAL_BLAST_RADIUS = 8.0D;
    private static final double REVIVAL_KNOCKBACK_STRENGTH = 0.5D;

    private TotemOfMaliceEvents() {
    }

    /**
     * 处理恶意图腾的袭击者伤害联动和保命效果。
     *
     * <p>优先级设为 LOWEST，确保其它伤害修正先完成。</p>
     * <p>最后再根据即将造成的最终伤害判断是否需要触发保命。</p>
     *
     * @param event 伤害预处理事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (event.getSource().getEntity() instanceof Player attacker
                && target instanceof Raider
                && TotemOfMalice.hasUsableTotem(attacker)) {
            event.setNewDamage((float) (event.getNewDamage() * (1.0D + TotemOfMalice.RAIDER_DAMAGE_BOOST)));
        }

        if (target instanceof Player player
                && event.getSource().getEntity() instanceof Raider
                && TotemOfMalice.hasUsableTotem(player)) {
            event.setNewDamage((float) (event.getNewDamage() * (1.0D - TotemOfMalice.RAIDER_DAMAGE_RESISTANCE)));
        }

        if (target instanceof Player player) {
            tryRevive(player, event);
        }
    }

    /**
     * 使用邪恶精髓在铁砧中恢复恶意图腾能量。
     *
     * <p>输入：恶意图腾 + 邪恶精髓。</p>
     * <p>输出：能量恢复到当前最大值的恶意图腾。</p>
     * <p>修补固定消耗 5 级经验，不再累积额外经验惩罚。</p>
     *
     * @param event 铁砧输出刷新事件
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!left.is(ModItems.TOTEM_OF_MALICE.get()) || !right.is(ModItems.EVIL_ESSENCE.get())) {
            return;
        }

        int maxPower = TotemOfMalice.getMaxPower(left);

        if (TotemOfMalice.getPower(left) >= maxPower) {
            return;
        }

        ItemStack repaired = left.copy();
        TotemOfMalice.setPower(repaired, maxPower);

        event.setOutput(repaired);
        event.setCost(5L);
        event.setMaterialCost(1);
    }

    /**
     * 判断并触发恶意图腾保命。
     *
     * <p>该逻辑只拦截非穿透无敌的致死伤害。</p>
     * <p>触发后玩家恢复满血、清除状态效果、获得短暂无敌，并对附近实体造成恶意爆发伤害。</p>
     *
     * @param player 受到致死伤害的玩家
     * @param event  伤害预处理事件
     */
    private static void tryRevive(Player player, LivingDamageEvent.Pre event) {
        if (player.level().isClientSide()) {
            return;
        }

        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        if (event.getNewDamage() < player.getHealth()) {
            return;
        }

        TotemOfMalice.findChargedTotem(player).ifPresent(totem -> {
            ItemStack usedTotem = totem.copy();
            TotemOfMalice.consumePower(totem);

            event.setNewDamage(0.0F);
            player.setHealth(player.getMaxHealth());
            player.removeAllEffects();
            player.invulnerableTime = 60;

            awardTotemUse(player, usedTotem);
            triggerRevivalFeedback(player);
            damageNearbyEntities(player);
        });
    }

    /**
     * 发放恶意图腾触发时应有的原版统计和图腾使用条件。
     *
     * <p>原拓展会触发 {@link CriteriaTriggers#USED_TOTEM}，这样依赖原版图腾触发条件的进度可以正常完成。</p>
     *
     * @param player    触发恶意图腾的玩家
     * @param usedTotem 触发前复制出的恶意图腾，用于进度条件匹配
     */
    private static void awardTotemUse(Player player, ItemStack usedTotem) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.awardStat(Stats.ITEM_USED.get(ModItems.TOTEM_OF_MALICE.get()));
        CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, usedTotem);
    }

    /**
     * 播放恶意图腾触发反馈。
     *
     * <p>对齐原拓展：服务端向附近客户端发送包，客户端播放女巫粒子、图腾音效和恶意图腾举起动画。</p>
     *
     * @param player 触发图腾的玩家
     */
    private static void triggerRevivalFeedback(Player player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        PacketDistributor.sendToPlayersNear(
                level,
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                64.0D,
                new TotemOfMaliceActivationPayload(player.getX(), player.getY(), player.getZ())
        );
    }

    /**
     * 对图腾触发点周围实体造成恶意爆发。
     *
     * <p>伤害基于玩家最大生命值，保留原拓展“触发时冲击周围敌人”的定位。</p>
     * <p>爆发不会伤害触发者本人。</p>
     *
     * @param player 触发图腾的玩家
     */
    private static void damageNearbyEntities(Player player) {
        float damage = player.getMaxHealth() * 1.5F;

        for (LivingEntity entity : player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(REVIVAL_BLAST_RADIUS)
        )) {
            if (entity == player || !entity.isAlive()) {
                continue;
            }

            applyKnockback(player, entity);
            entity.hurt(player.damageSources().source(DamageTypes.MAGIC, player), damage);
            entity.invulnerableTime = 0;
        }
    }

    /**
     * 给恶意爆发命中的实体施加轻微击退。
     *
     * @param player 爆发中心
     * @param entity 被命中的实体
     */
    private static void applyKnockback(Player player, LivingEntity entity) {
        var direction = entity.position().subtract(player.position());

        if (direction.lengthSqr() <= 0.0001D) {
            return;
        }

        double distance = Math.max(entity.distanceTo(player), 1.0F);
        double strength = Math.min(1.0D, REVIVAL_KNOCKBACK_STRENGTH / distance);
        var horizontal = direction.normalize().scale(strength);
        entity.push(horizontal.x, entity.onGround() ? 1.2D * strength : 0.0D, horizontal.z);
    }
}
