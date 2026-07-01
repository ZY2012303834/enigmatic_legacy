package org.enigmatic_legacy.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.AntiqueBookBag;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.AntiqueBookBagHelper;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 古旧书袋中的特殊书本效果。
 *
 * <p>普通书类效果直接由各自的 has... 方法读取书袋。
 * 这里只处理原拓展项目中“放入书袋后额外获得”的特殊效果。
 * AntiqueBookBagHelper 只返回是否存在，因此同一本书放多本也不会叠加。</p>
 */
public final class AntiqueBookBagEvents {
    private AntiqueBookBagEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        LivingEntity target = event.getEntity();

        if (AntiqueBookBagHelper.hasBook(player, ModItems.THE_ACKNOWLEDGMENT.get())) {
            target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 60));
        }

        float damage = event.getAmount();

        if (isBossOrPlayer(target)
                && CursedRingHelper.hasCursedRing(player)
                && AntiqueBookBagHelper.hasBook(player, ModItems.THE_TWIST.get())) {
            damage += damage * 0.1F;
        }

        if (isBossOrPlayer(target)
                && AbyssalHeartHelper.isWorthy(player)
                && AntiqueBookBagHelper.hasBook(player, ModItems.THE_INFINITUM.get())) {
            damage += damage * 0.2F;
        }

        event.setAmount(damage);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!AbyssalHeartHelper.isWorthy(player)
                || !AntiqueBookBagHelper.hasBook(player, ModItems.THE_INFINITUM.get())) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage > 0.0F) {
            player.heal(damage * 0.1F);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal().getPersistentData().contains(AntiqueBookBag.ROOT_TAG)) {
            event.getEntity().getPersistentData().put(
                    AntiqueBookBag.ROOT_TAG,
                    event.getOriginal().getPersistentData().getCompound(AntiqueBookBag.ROOT_TAG).copy()
            );
        }
    }

    private static boolean isBossOrPlayer(LivingEntity entity) {
        return entity instanceof Player
                || entity instanceof WitherBoss
                || entity instanceof EnderDragon
                || entity instanceof ElderGuardian;
    }
}
