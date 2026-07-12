package org.enigmatic_legacy.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.BlazingCoreHelper;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.MajesticElytraHelper;
import org.enigmatic_legacy.util.ScorchedCharmHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    private static final float LAVA_SWIM_INPUT_SPEED = 0.08F;

    @Shadow
    protected abstract boolean isAffectedByFluids();

    @Shadow
    protected boolean jumping;

    /**
     * 让背饰栏中的壮丽鞘翅参与原版“持续滑翔”检查。
     * <p>
     * 原版 LivingEntity#updateFallFlying 每 tick 都会读取胸甲槽：
     * 如果那里没有可用鞘翅，玩家会被立刻停止滑翔。
     * 这里只替换该方法内部读取胸甲槽的结果：
     * 胸甲槽有可用鞘翅时保持原版返回；
     * 胸甲槽没有可用鞘翅时，才把 Curios back 槽中的壮丽鞘翅交给原版继续检查。
     */
    @Redirect(
            method = "updateFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack enigmaticLegacy$useBackSlotMajesticElytraForFlightTick(LivingEntity entity, EquipmentSlot slot) {
        return MajesticElytraHelper.getChestOrBackElytraStack(entity, slot);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$travelInLavaLikeWater(Vec3 travelVector, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!enigmaticLegacy$hasLavaSwimming(entity) || !entity.isControlledByLocalInstance() || !this.isAffectedByFluids()) {
            return;
        }

        double gravity = entity.getGravity();
        boolean falling = entity.getDeltaMovement().y <= 0.0D;
        if (falling && entity.hasEffect(MobEffects.SLOW_FALLING)) {
            gravity = Math.min(gravity, 0.01D);
        }

        double startY = entity.getY();
        entity.setSwimming(true);

        entity.moveRelative(LAVA_SWIM_INPUT_SPEED, travelVector);

        if (this.jumping) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x, Math.max(movement.y + 0.04D, 0.04D), movement.z);
            falling = false;
        } else if (entity.isShiftKeyDown()) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x, Math.min(movement.y - 0.04D, -0.04D), movement.z);
            falling = true;
        }

        entity.move(MoverType.SELF, entity.getDeltaMovement());

        Vec3 movement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) {
            movement = new Vec3(movement.x, 0.2D, movement.z);
        }

        movement = movement.multiply(0.9D, 0.8D, 0.9D);
        movement = entity.getFluidFallingAdjustedMovement(gravity, falling, movement);
        entity.setDeltaMovement(movement);

        if (entity.horizontalCollision && entity.isFree(movement.x, movement.y + 0.6D - entity.getY() + startY, movement.z)) {
            entity.setDeltaMovement(movement.x, 0.3D, movement.z);
        }

        entity.setSwimming(true);

        entity.calculateEntityAnimation(entity instanceof FlyingAnimal);
        callback.cancel();
    }

    @Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$jumpInLavaLikeWater(TagKey<Fluid> fluidTag, CallbackInfo callback) {
        if (!fluidTag.equals(FluidTags.LAVA)) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        if (!enigmaticLegacy$hasLavaMovement(entity)) {
            return;
        }

        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
        callback.cancel();
    }

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void enigmaticLegacy$scorchedCharmCanStandOnLava(
            FluidState fluidState,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (callback.getReturnValue()) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isCrouching()) {
            return;
        }

        if (fluidState.is(FluidTags.LAVA) && ScorchedCharmHelper.hasScorchedCharm(entity)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$bulwarkBlocksImmediately(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof Player player
                && entity.isUsingItem()
                && entity.getUseItem().is(ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                && CursedRingHelper.hasCursedRing(player)) {
            callback.setReturnValue(true);
        }
    }

    private static boolean enigmaticLegacy$hasLavaSwimming(LivingEntity entity) {
        return entity.isSprinting()
                && entity.isEyeInFluid(FluidTags.LAVA)
                && !entity.isPassenger()
                && enigmaticLegacy$hasLavaMovement(entity);
    }

    private static boolean enigmaticLegacy$hasLavaMovement(LivingEntity entity) {
        return entity.isInLava()
                && (BlazingCoreHelper.hasBlazingCore(entity) || ScorchedCharmHelper.hasScorchedCharm(entity));
    }
}
