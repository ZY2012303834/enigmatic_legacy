package org.enigmatic_legacy.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.item.items.scroll.PactOfDarkNight;
import org.enigmatic_legacy.util.PactOfDarkNightHelper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 暗夜契约卷轴事件逻辑。
 *
 * <p>负责四类效果：
 * 1. 玩家造成伤害时按暗度提高伤害；
 * 2. 玩家受到伤害时按暗度降低伤害；
 * 3. 玩家造成直接伤害后按暗度吸血；
 * 4. 抑制佩戴者附近的幻翼生成，并削弱已经进入范围内的幻翼。</p>
 */
public final class PactOfDarkNightEvents {
    /**
     * 扩展项目维护的是 Player -> AABB 的 WeakHashMap。
     * 这里使用 UUID 做 key，避免玩家对象引用在维度切换/登出时滞留。
     */
    private static final Map<UUID, AABB> NIGHT_SCROLL_BOXES = new ConcurrentHashMap<>();

    /**
     * 幻翼抑制范围。
     * 原实现使用 X/Z 128、Y 360 的巨大盒子，这里保留数值。
     */
    private static final double PHANTOM_SUPPRESSION_XZ = 128.0D;
    private static final double PHANTOM_SUPPRESSION_Y = 360.0D;

    /**
     * 防止对同一只幻翼每 tick 都重复施加凋零和半血伤害。
     */
    private static final String PHANTOM_WEAKENED_TAG = "enigmatic_legacy_night_scroll_weakened";

    private PactOfDarkNightEvents() {
    }

    /**
     * 每 tick 刷新佩戴者周围的幻翼抑制范围。
     *
     * <p>只有真正佩戴暗夜契约并拥有七咒资格的玩家会写入范围；
     * 没有资格、死亡或客户端侧都不会参与抑制。</p>
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!player.isAlive()) {
            NIGHT_SCROLL_BOXES.remove(player.getUUID());
            return;
        }

        if (PactOfDarkNightHelper.hasPact(player)) {
            NIGHT_SCROLL_BOXES.put(player.getUUID(), player.getBoundingBox().inflate(PHANTOM_SUPPRESSION_XZ, PHANTOM_SUPPRESSION_Y, PHANTOM_SUPPRESSION_XZ));
        } else {
            NIGHT_SCROLL_BOXES.remove(player.getUUID());
        }
    }

    /**
     * 拒绝失眠机制准备为佩戴者或附近玩家生成幻翼。
     */
    @SubscribeEvent
    public static void onPlayerSpawnPhantoms(PlayerSpawnPhantomsEvent event) {
        Player player = event.getEntity();

        if (PactOfDarkNightHelper.hasPact(player) || intersectsNightScrollBox(player.getBoundingBox().inflate(6.0D))) {
            event.setResult(PlayerSpawnPhantomsEvent.Result.DENY);
            event.setPhantomsToSpawn(0);
        }
    }

    /**
     * 拦截自然生成的幻翼。
     *
     * <p>当前 NeoForge 版本没有旧 Forge 的 FinalizeSpawn 子类，
     * 因此在 PositionCheck 阶段把结果设为 FAIL。</p>
     */
    @SubscribeEvent
    public static void onMobPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }

        if (!(event.getEntity() instanceof Phantom phantom)) {
            return;
        }

        if (intersectsNightScrollBox(phantom.getBoundingBox())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    /**
     * 已经进入抑制范围的幻翼会被削弱。
     *
     * <p>原扩展会施加凋零 III 10 秒，并造成当前生命一半的伤害。
     * 这里用 persistent data 记录一次性触发，避免持续每 tick 重复半血。</p>
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Phantom phantom)) {
            return;
        }

        if (phantom.level().isClientSide()) {
            return;
        }

        if (!intersectsNightScrollBox(phantom.getBoundingBox())) {
            return;
        }

        if (phantom.getPersistentData().getBoolean(PHANTOM_WEAKENED_TAG)) {
            return;
        }

        phantom.getPersistentData().putBoolean(PHANTOM_WEAKENED_TAG, true);
        phantom.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 2));
        phantom.hurt(phantom.damageSources().mobAttack(phantom), phantom.getHealth() / 2.0F);
        phantom.invulnerableTime = 1;
    }

    /**
     * 攻击伤害提升、受击减伤和吸血都放在最终伤害阶段处理。
     *
     * <p>LivingDamageEvent.Pre 的数值已经经过护甲和大部分减伤计算，
     * 更接近扩展项目旧 LivingDamageEvent 对最终伤害的改写方式。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player attacker && PactOfDarkNightHelper.isDark(attacker)) {
            double modifier = PactOfDarkNightHelper.getDarkModifier(attacker);
            double bonus = PactOfDarkNight.getDamageBoost(modifier);
            event.setNewDamage((float) (event.getNewDamage() * (1.0D + bonus)));
        }

        if (event.getEntity() instanceof Player victim && PactOfDarkNightHelper.isDark(victim)) {
            double modifier = PactOfDarkNightHelper.getDarkModifier(victim);
            double resistance = Math.min(0.95D, PactOfDarkNight.getDamageResistance(modifier));
            event.setNewDamage((float) (event.getNewDamage() * (1.0D - resistance)));
        }

        if (event.getSource().getDirectEntity() instanceof Player attacker && PactOfDarkNightHelper.isDark(attacker)) {
            double modifier = PactOfDarkNightHelper.getDarkModifier(attacker);
            double lifeSteal = PactOfDarkNight.getLifeSteal(modifier);

            if (lifeSteal > 0.0D && event.getNewDamage() > 0.0F) {
                attacker.heal((float) (event.getNewDamage() * lifeSteal));
            }
        }
    }

    private static boolean intersectsNightScrollBox(AABB box) {
        for (AABB nightBox : NIGHT_SCROLL_BOXES.values()) {
            if (nightBox.intersects(box)) {
                return true;
            }
        }

        return false;
    }
}
