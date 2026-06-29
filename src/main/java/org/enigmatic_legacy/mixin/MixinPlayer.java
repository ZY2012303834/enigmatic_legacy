package org.enigmatic_legacy.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Redirect(
            method = "blockUsingShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canDisableShield()Z")
    )
    private boolean enigmaticLegacy$bulwarkCannotBeDisabledByAxes(LivingEntity attacker) {
        Player player = (Player) (Object) this;

        if (player.getUseItem().is(ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                && CursedRingHelper.hasCursedRing(player)) {
            return false;
        }

        return attacker.canDisableShield();
    }
}
