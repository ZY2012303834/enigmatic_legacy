package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.OdeToLiving;

import java.util.List;

/**
 * 生灵颂词服务端事件。
 *
 * <p>原拓展项目把这些事件直接注册在物品实例上；当前项目习惯把行为放到
 * event 包中统一注册，因此这里独立成事件类。</p>
 */
public final class OdeToLivingEvents {
    private OdeToLivingEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            handlePlayerAttackingAnimal(event, attacker);
        }
    }

    /**
     * 背包内有颂词时，玩家攻击动物会被阻止。
     *
     * <p>如果玩家正用颂词本体攻击，则该次攻击用于“记录例外种类”，
     * 因此不会取消伤害。这一点对应原项目的黑名单机制。</p>
     */
    private static void handlePlayerAttackingAnimal(LivingIncomingDamageEvent event, Player attacker) {
        if (!OdeToLiving.isOdeAnimal(event.getEntity())) {
            return;
        }

        ItemStack mainHand = attacker.getMainHandItem();

        if (OdeToLiving.isHeldOde(mainHand)) {
            if (OdeToLiving.isProtectedByOde(attacker, event.getEntity())) {
                OdeToLiving.addToBannedList(mainHand, event.getEntity());
            }

            return;
        }

        if (OdeToLiving.isProtectedByOde(attacker, event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 != 0 || !OdeToLiving.hasOde(player)) {
            return;
        }

        calmProtectedAnimals(player);
    }

    /**
     * 每秒清理一次附近被颂词保护动物对玩家的仇恨。
     *
     * <p>这里不强行修改所有动物 AI，只处理“目标正是该玩家”的情况，
     * 避免干扰动物对其它实体的正常行为。</p>
     */
    private static void calmProtectedAnimals(ServerPlayer player) {
        double range = ConfigCommon.CURSED_RING_NEUTRAL_ANGER_RANGE.get();
        AABB box = player.getBoundingBox().inflate(range);

        List<Animal> animals = player.level().getEntitiesOfClass(
                Animal.class,
                box,
                animal -> animal.isAlive() && OdeToLiving.isProtectedByOde(player, animal)
        );

        for (Animal animal : animals) {
            if (!(animal instanceof Mob mob) || mob.getTarget() != player) {
                continue;
            }

            mob.setTarget(null);

            if (animal instanceof NeutralMob neutralMob) {
                neutralMob.stopBeingAngry();
            }
        }
    }
}
