package org.enigmatic_legacy.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.enigmatic_legacy.util.ScorchedCharmHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class MixinLiquidBlock {

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$scorchedCharmLavaCollision(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context,
            CallbackInfoReturnable<VoxelShape> callback
    ) {
        if (!state.getFluidState().is(FluidTags.LAVA) || state.getValue(LiquidBlock.LEVEL) != 0) {
            return;
        }

        if (!context.isAbove(LiquidBlock.STABLE_SHAPE, pos, true)) {
            return;
        }

        if (level.getFluidState(pos.above()).is(FluidTags.LAVA)) {
            return;
        }

        if (!(context instanceof EntityCollisionContext entityContext)) {
            return;
        }

        Entity entity = entityContext.getEntity();
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.isCrouching()) {
            return;
        }

        if (ScorchedCharmHelper.hasScorchedCharm(livingEntity)) {
            callback.setReturnValue(LiquidBlock.STABLE_SHAPE);
        }
    }
}
