package org.enigmatic_legacy.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BigLamp extends LanternBlock {

    protected static final VoxelShape SITTING_LANTERN = Block.box(
            1.0D, 0.0D, 1.0D,
            15.0D, 14.0D, 15.0D
    );

    protected static final VoxelShape HANGING_LANTERN = Block.box(
            1.0D, 1.0D, 1.0D,
            15.0D, 15.0D, 15.0D
    );

    public BigLamp() {
        super(Properties.ofFullCopy(Blocks.LANTERN));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return state.getValue(HANGING) ? HANGING_LANTERN : SITTING_LANTERN;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(
            @NotNull BlockState state,
            LootParams.@NotNull Builder builder
    ) {
        return List.of(new ItemStack(this));
    }
}