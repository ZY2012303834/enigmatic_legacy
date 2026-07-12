package org.enigmatic_legacy.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import org.enigmatic_legacy.util.MajesticElytraHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 让客户端也能用 Curios back 槽中的壮丽鞘翅发起滑翔。
 * <p>
 * 原版 LocalPlayer#aiStep 在发送 START_FALL_FLYING 包之前，会先硬编码检查胸甲槽：
 * 只有胸甲槽物品是 minecraft:elytra 并且可用时，客户端才会把起飞请求发给服务端。
 * 壮丽鞘翅放在 Curios back 槽时，服务端 mixin 已经能接受起飞请求，但客户端根本不会发送该请求。
 * 因此这里在原版逻辑之后补一次 back 槽检查，并发送同一个原版起飞包。
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Unique
    private boolean enigmaticLegacy$wasOnGroundBeforeAiStep;

    @Unique
    private boolean enigmaticLegacy$wasJumpingBeforeAiStep;

    /**
     * 记录客户端本 tick 输入处理前的状态。
     * <p>
     * 原版鞘翅起飞要求“本 tick 新按下跳跃键”，并且不能是刚从地面跳起的同一轮输入。
     * 如果只在 TAIL 检查 {@code input.jumping}，玩家按一次空格正常跳跃后也会被误判为背饰栏鞘翅起飞。
     */
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void enigmaticLegacy$captureJumpStateBeforeAiStep(CallbackInfo callback) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        this.enigmaticLegacy$wasOnGroundBeforeAiStep = player.onGround();
        this.enigmaticLegacy$wasJumpingBeforeAiStep = player.input.jumping;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void enigmaticLegacy$startFlyingWithBackSlotMajesticElytra(CallbackInfo callback) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (!player.input.jumping
                || this.enigmaticLegacy$wasJumpingBeforeAiStep
                || this.enigmaticLegacy$wasOnGroundBeforeAiStep
                || player.getAbilities().flying
                || player.isPassenger()
                || player.onClimbable()
                || MajesticElytraHelper.getEquippedStack(player).isEmpty()) {
            return;
        }

        /*
         * tryToStartFallFlying 本身会检查是否离地、是否已经在滑翔、是否在水中和是否漂浮。
         * MixinPlayer 已经把 back 槽壮丽鞘翅接入这个服务端/客户端共用的起飞校验，
         * 所以这里复用它，避免客户端自己复制一份不完整的条件。
         */
        if (player.tryToStartFallFlying()) {
            player.connection.send(new ServerboundPlayerCommandPacket(
                    player,
                    ServerboundPlayerCommandPacket.Action.START_FALL_FLYING
            ));
        }
    }
}
