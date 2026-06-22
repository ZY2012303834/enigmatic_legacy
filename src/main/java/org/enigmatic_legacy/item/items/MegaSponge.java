package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * 超级海绵 / Extrapolated Megasponge。
 * 佩戴后，当玩家接触水体时，会从接触到的水方块开始连锁吸收附近水体。
 */
public class MegaSponge extends Item implements ICurioItem {
    private static final int COOLDOWN_TICKS = 20;

    public MegaSponge() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return;
        }

        Level level = player.level();

        if (level.isClientSide) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        BlockPos initialWater = getCollidedWater(FluidTags.WATER, player);

        if (initialWater == null) {
            return;
        }

        int absorbed = absorbConnectedWater(level, initialWater, ConfigCommon.MEGA_SPONGE_RADIUS.get());

        if (absorbed <= 0) {
            return;
        }

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BUCKET_FILL,
                SoundSource.PLAYERS,
                1.0F,
                (float) (0.8F + Math.random() * 0.2F)
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.PORTAL,
                    player.getX(),
                    player.getY() + player.getBbHeight() / 2.0D,
                    player.getZ(),
                    40,
                    0.6D,
                    0.6D,
                    0.6D,
                    0.02D
            );
        }

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }

    /**
     * 寻找玩家碰撞箱内接触到的水。
     */
    private BlockPos getCollidedWater(TagKey<Fluid> fluidTag, Player player) {
        AABB box = player.getBoundingBox().deflate(0.001D);

        int minX = Mth.floor(box.minX);
        int maxX = Mth.ceil(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.ceil(box.maxY);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.ceil(box.maxZ);

        Level level = player.level();

        if (!level.hasChunksAt(minX, minY, minZ, maxX, maxY, maxZ)) {
            return null;
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    mutable.set(x, y, z);

                    FluidState fluidState = level.getFluidState(mutable);

                    if (fluidState.is(fluidTag)) {
                        return mutable.immutable();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 从初始水方块开始，按六方向连锁吸水。
     */
    private int absorbConnectedWater(Level level, BlockPos start, int radius) {
        int safeRadius = Math.max(0, radius);

        Set<BlockPos> visited = new HashSet<>();
        Queue<WaterNode> queue = new ArrayDeque<>();

        visited.add(start);
        queue.add(new WaterNode(start, 0));

        int absorbed = 0;

        while (!queue.isEmpty()) {
            WaterNode node = queue.poll();
            BlockPos pos = node.pos();

            if (!level.getFluidState(pos).is(FluidTags.WATER)) {
                continue;
            }

            if (absorbWaterBlock(pos, level.getBlockState(pos), level)) {
                absorbed++;
            }

            if (node.depth() >= safeRadius) {
                continue;
            }

            for (BlockPos next : getNearbyPositions(pos)) {
                if (visited.contains(next)) {
                    continue;
                }

                if (!level.getFluidState(next).is(FluidTags.WATER)) {
                    continue;
                }

                visited.add(next);
                queue.add(new WaterNode(next, node.depth() + 1));
            }
        }

        return absorbed;
    }

    /**
     * 实际移除单个水方块。
     * 逻辑参考原项目：
     * 1. BucketPickup 优先尝试 pickup；
     * 2. 流体方块直接置空；
     * 3. 其他含水方块会掉落资源并置空；
     * 4. 海草、海带类方块保留。
     */
    private boolean absorbWaterBlock(BlockPos pos, BlockState state, Level level) {
        Block block = state.getBlock();

        if (block instanceof BucketPickup pickup) {
            ItemStack picked = pickup.pickupBlock(null, level, pos, state);

            return !picked.isEmpty();
        }

        if (block instanceof LiquidBlock) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return true;
        }

        if (state.is(Blocks.KELP)
                || state.is(Blocks.KELP_PLANT)
                || state.is(Blocks.SEAGRASS)
                || state.is(Blocks.TALL_SEAGRASS)) {
            return false;
        }

        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

        Block.dropResources(state, level, pos, blockEntity);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        return true;
    }

    private List<BlockPos> getNearbyPositions(BlockPos pos) {
        return List.of(
                pos.east(),
                pos.west(),
                pos.above(),
                pos.below(),
                pos.south(),
                pos.north()
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.mega_sponge.1"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.mega_sponge.2",
                ConfigCommon.MEGA_SPONGE_RADIUS.get()
        ).withStyle(ChatFormatting.GOLD));
    }

    private record WaterNode(BlockPos pos, int depth) {
    }
}
