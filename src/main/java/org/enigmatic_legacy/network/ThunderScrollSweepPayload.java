package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ScrollOfThunderEmbraceHelper;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端空挥左键时发给服务端的万钧之护卷轴横扫包。
 *
 * <p>旧版扩展在客户端监听“左键空点”，再由服务端补一次横扫判定。
 * 当前项目保持同样交互：客户端只负责告诉服务端玩家尝试空挥，
 * 服务端重新校验七咒资格、卷轴佩戴、冷却、站地状态和主手武器能力，避免客户端伪造效果。</p>
 */
public record ThunderScrollSweepPayload() implements CustomPacketPayload {
    public static final Type<ThunderScrollSweepPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "thunder_scroll_sweep")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ThunderScrollSweepPayload> STREAM_CODEC =
            StreamCodec.unit(new ThunderScrollSweepPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ThunderScrollSweepPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            performSweep(player);
        });
    }

    /**
     * 服务端空挥横扫逻辑。
     *
     * <p>这里不直接信任客户端：所有影响战斗的条件都在服务端重新判断。
     * 伤害沿用扩展项目思路：基础值为玩家攻击伤害的一半，再乘横扫伤害比例。
     * 当前版本横扫比例由 Attributes.SWEEPING_DAMAGE_RATIO 提供，万钧之护卷轴会把它提高到 1。</p>
     */
    private static void performSweep(ServerPlayer player) {
        if (!ScrollOfThunderEmbraceHelper.hasScroll(player)) {
            return;
        }

        if (!player.onGround()) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(ModItems.THUNDER_SCROLL.get())) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();

        if (!mainHand.canPerformAction(ItemAbilities.SWORD_SWEEP)) {
            return;
        }

        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F;
        float sweepingRatio = Math.max(1.0F, (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO));
        float damage = baseDamage * sweepingRatio;

        if (damage <= 0.0F) {
            return;
        }

        DamageSource damageSource = player.damageSources().playerAttack(player);
        AABB sweepBox = mainHand.getSweepHitBox(player, player);
        double reachSq = player.entityInteractionRange() * player.entityInteractionRange();
        boolean hitAny = false;

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, sweepBox)) {
            if (!canSweepHit(player, target, reachSq)) {
                continue;
            }

            target.knockback(
                    0.4F,
                    Math.sin(player.getYRot() * (float) Math.PI / 180.0F),
                    -Math.cos(player.getYRot() * (float) Math.PI / 180.0F)
            );
            target.hurt(damageSource, damage);
            hitAny = true;
        }

        if (!hitAny) {
            return;
        }

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                player.getSoundSource(),
                1.0F,
                1.0F
        );

        if (player.level() instanceof ServerLevel) {
            player.sweepAttack();
        }

        player.causeFoodExhaustion(0.1F);
        player.getCooldowns().addCooldown(ModItems.THUNDER_SCROLL.get(), getSweepCooldownTicks(player));
    }

    /**
     * 空挥横扫的目标过滤。
     * 排除自己、友方、标记盔甲架和交互距离外目标，与原版横扫判定保持一致。
     */
    private static boolean canSweepHit(ServerPlayer player, LivingEntity target, double reachSq) {
        return target != player
                && !player.isAlliedTo(target)
                && (!(target instanceof ArmorStand armorStand) || !armorStand.isMarker())
                && player.distanceToSqr(target) < reachSq;
    }

    /**
     * 根据攻击速度计算空挥横扫冷却。
     * 扩展项目使用 16 / attackSpeed；这里保留该公式，并至少冷却 1 tick。
     */
    private static int getSweepCooldownTicks(ServerPlayer player) {
        double attackSpeed = Math.max(0.1D, player.getAttributeValue(Attributes.ATTACK_SPEED));
        return Math.max(1, (int) Math.ceil(16.0D / attackSpeed));
    }
}
