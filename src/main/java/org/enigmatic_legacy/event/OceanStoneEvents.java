package org.enigmatic_legacy.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.OceanStoneHelper;

public final class OceanStoneEvents {
    private OceanStoneEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!OceanStoneHelper.hasOceanStone(target)) {
            return;
        }

        if (event.getSource().is(DamageTypes.DROWN)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!OceanStoneHelper.hasOceanStone(target)) {
            return;
        }

        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        if (isAquaticAttacker(source)) {
            damage *= 1.0F - ConfigCommon.OCEAN_STONE_AQUATIC_DAMAGE_RESISTANCE.get() / 100.0F;
        }

        if (source.is(DamageTypeTags.IS_FIRE)) {
            damage *= ConfigCommon.OCEAN_STONE_FIRE_VULNERABILITY.get().floatValue();
        }

        event.setNewDamage(damage);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (OceanStoneHelper.hasOceanStone(player) && player.isEyeInFluid(FluidTags.WATER)) {
            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }

    private static boolean isAquaticAttacker(DamageSource source) {
        return isAquaticEntity(source.getEntity()) || isAquaticEntity(source.getDirectEntity());
    }

    private static boolean isAquaticEntity(Entity entity) {
        if (entity == null) {
            return false;
        }

        return entity instanceof Drowned
                || entity instanceof Guardian
                || entity instanceof ElderGuardian
                || entity.getType().is(EntityTypeTags.AQUATIC);
    }
}