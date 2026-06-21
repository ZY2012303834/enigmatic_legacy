package org.enigmatic_legacy.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// 封装的大灯笼
public class MassiveLamp extends Block {

    public MassiveLamp() {
        super(Properties.ofFullCopy(Blocks.LANTERN)
                .sound(SoundType.GLASS)
                .noOcclusion());
    }

    @Override
    public boolean shouldDisplayFluidOverlay(
            @NotNull BlockState state,
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull FluidState fluidState
    ) {
        return super.shouldDisplayFluidOverlay(state, world, pos, fluidState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return List.of(new ItemStack(this));
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }
}