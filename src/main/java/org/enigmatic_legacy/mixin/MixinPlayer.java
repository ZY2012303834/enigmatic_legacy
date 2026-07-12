package org.enigmatic_legacy.mixin;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.MajesticElytraHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    /**
     * 让 Curios back 槽中的壮丽鞘翅参与双跳起飞。
     * <p>
     * 原版 Player#tryToStartFallFlying 只检查胸甲槽。
     * 当胸甲槽没有可用鞘翅时，原版会返回 false，服务端随即调用 stopFallFlying。
     * 这里在返回 false 后补一次背饰槽判断，并复用原版的起飞限制：
     * 不能在地面、水中、漂浮状态或已经滑翔时重复启动。
     */
    @Inject(method = "tryToStartFallFlying", at = @At("RETURN"), cancellable = true)
    private void enigmaticLegacy$tryStartFlyingWithBackSlotMajesticElytra(CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            return;
        }

        Player player = (Player) (Object) this;

        if (player.onGround()
                || player.isFallFlying()
                || player.isInWater()
                || player.hasEffect(MobEffects.LEVITATION)
                || MajesticElytraHelper.getBackSlotStack(player).isEmpty()) {
            return;
        }

        player.startFallFlying();
        callback.setReturnValue(true);
    }
}
