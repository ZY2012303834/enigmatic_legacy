package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.book.SanguinaryHandbook;
import org.enigmatic_legacy.util.BloodstainedValorHelper;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.ScrollOfThousandCursesHelper;

/**
 * 血腥狩猎手册事件。
 */
public final class SanguinaryHandbookEvents {
    private SanguinaryHandbookEvents() {
    }

    @SubscribeEvent
    public static void onPetTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity) || !(entity instanceof OwnableEntity ownable)) {
            return;
        }

        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }

        movementSpeed.removeModifier(SanguinaryHandbook.PET_MOVEMENT_SPEED_ID);

        if (!(ownable.getOwner() instanceof Player owner) || !canAffectPet(owner, ownable, entity)) {
            return;
        }

        if (!BloodstainedValorHelper.canUseBloodstainedValor(owner)) {
            return;
        }

        double missingHealthRatio = BloodstainedValorHelper.getMissingHealthRatio(owner);
        if (missingHealthRatio <= 0.0D) {
            return;
        }

        movementSpeed.addTransientModifier(SanguinaryHandbook.createPetMovementSpeedModifier(missingHealthRatio));
    }

    @SubscribeEvent
    public static void onPetDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Entity attacker)) {
            return;
        }

        if (!(attacker instanceof OwnableEntity ownable)) {
            return;
        }

        if (!(ownable.getOwner() instanceof Player owner) || !canAffectPet(owner, ownable, attacker)) {
            return;
        }

        double damageMultiplier = ConfigCommon.SANGUINARY_HANDBOOK_PET_DAMAGE_MULTIPLIER.get();

        if (BloodstainedValorHelper.canUseBloodstainedValor(owner)) {
            damageMultiplier += 0.5D
                    * BloodstainedValorHelper.getMissingHealthRatio(owner)
                    * ConfigCommon.BLOODSTAINED_VALOR_ATTACK_DAMAGE.get();
        }

        if (ScrollOfThousandCursesHelper.hasScroll(owner)) {
            damageMultiplier += 0.75D * ScrollOfThousandCursesHelper.getAttackDamageBonus(owner);
        }

        if (damageMultiplier <= 0.0D) {
            return;
        }

        event.setNewDamage((float) (event.getNewDamage() * (1.0D + damageMultiplier)));
    }

    private static boolean canAffectPet(Player owner, OwnableEntity ownable, Entity petEntity) {
        if (!(owner instanceof ServerPlayer)) {
            return false;
        }

        if (!CursedRingHelper.hasCursedRing(owner) || !SanguinaryHandbook.hasHandbook(owner)) {
            return false;
        }

        if (!SanguinaryHandbook.isOwnedPet(ownable)) {
            return false;
        }

        if (owner.level() != petEntity.level()) {
            return false;
        }

        double range = ConfigCommon.HUNTER_GUIDE_EFFECTIVE_DISTANCE.get();
        return range > 0.0D && owner.distanceToSqr(petEntity) <= range * range;
    }
}
