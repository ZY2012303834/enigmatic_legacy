package org.enigmatic_legacy.item.items.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.item.items.material.EtheriumToolMaterial;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 以太镐 / Etherium Pickaxe。
 * 效果：
 * 挖掘 3x3x1 范围内的方块。
 * 潜行状态：
 * - 潜行挖掘时，区域挖掘不生效；
 * - 潜行右键可以切换是否启用区域挖掘。
 * 武器属性：
 * - 主手攻击力：7
 * - 攻击速度：1.2
 */
public class EtheriumPickaxe extends PickaxeItem {
    private static final String AREA_MINING_ENABLED_TAG = "EtheriumPickaxeAreaMiningEnabled";

    /*
     * 防止额外破坏方块时递归触发 3x3x1。
     */
    private static final ThreadLocal<Boolean> AREA_MINING_IN_PROGRESS =
            ThreadLocal.withInitial(() -> false);

    public EtheriumPickaxe() {
        super(
                EtheriumToolMaterial.INSTANCE,
                new Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .attributes(PickaxeItem.createAttributes(
                                EtheriumToolMaterial.INSTANCE,
                                1.0F,
                                -2.8F
                        ))
        );
    }

    @Override
    public boolean mineBlock(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull BlockState state,
            @NotNull BlockPos pos,
            @NotNull LivingEntity livingEntity
    ) {
        boolean result = super.mineBlock(stack, level, state, pos, livingEntity);

        if (level.isClientSide()) {
            return result;
        }

        if (AREA_MINING_IN_PROGRESS.get()) {
            return result;
        }

        if (!(livingEntity instanceof ServerPlayer player)) {
            return result;
        }

        if (!isAreaMiningEnabled(stack)) {
            return result;
        }

        /*
         * 要求：潜行状态下不生效。
         */
        if (player.isShiftKeyDown()) {
            return result;
        }

        if (!stack.isCorrectToolForDrops(state)) {
            return result;
        }

        Direction face = getMiningFace(level, player);
        mineArea(player, stack, pos, face);

        return result;
    }

    /**
     * 潜行右键切换是否启用区域挖掘。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            boolean enabled = !isAreaMiningEnabled(stack);
            setAreaMiningEnabled(stack, enabled);

            player.displayClientMessage(
                    Component.translatable(
                            enabled
                                    ? "message.enigmatic_legacy.etherium_pickaxe.area_enabled"
                                    : "message.enigmatic_legacy.etherium_pickaxe.area_disabled"
                    ).withStyle(enabled ? ChatFormatting.DARK_PURPLE : ChatFormatting.RED),
                    true
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * 对着方块潜行右键时，也切换区域挖掘，避免被普通方块交互吞掉。
     */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
        }

        return super.useOn(context);
    }

    private static void mineArea(ServerPlayer player, ItemStack stack, BlockPos origin, Direction face) {
        AREA_MINING_IN_PROGRESS.set(true);

        try {
            for (BlockPos targetPos : getAreaPositions(origin, face)) {
                if (targetPos.equals(origin)) {
                    continue;
                }

                BlockState targetState = player.level().getBlockState(targetPos);

                if (!canMineExtraBlock(player, stack, targetPos, targetState)) {
                    continue;
                }

                player.gameMode.destroyBlock(targetPos);
            }
        } finally {
            AREA_MINING_IN_PROGRESS.set(false);
        }
    }

    private static boolean canMineExtraBlock(
            ServerPlayer player,
            ItemStack stack,
            BlockPos pos,
            BlockState state
    ) {
        if (state.isAir()) {
            return false;
        }

        if (state.getDestroySpeed(player.level(), pos) < 0.0F) {
            return false;
        }

        if (!player.level().mayInteract(player, pos)) {
            return false;
        }

        return stack.isCorrectToolForDrops(state);
    }

    private static List<BlockPos> getAreaPositions(BlockPos origin, Direction face) {
        Direction.Axis axis = face.getAxis();

        java.util.ArrayList<BlockPos> positions = new java.util.ArrayList<>(9);

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                BlockPos target;

                if (axis == Direction.Axis.Y) {
                    target = origin.offset(a, 0, b);
                } else if (axis == Direction.Axis.X) {
                    target = origin.offset(0, a, b);
                } else {
                    target = origin.offset(a, b, 0);
                }

                positions.add(target);
            }
        }

        return positions;
    }

    private static Direction getMiningFace(Level level, ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(6.0D));

        BlockHitResult hitResult = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getDirection();
        }

        Vec3 look = player.getLookAngle();
        return Direction.getNearest(look.x, look.y, look.z);
    }

    private static boolean isAreaMiningEnabled(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (!tag.contains(AREA_MINING_ENABLED_TAG)) {
            return true;
        }

        return tag.getBoolean(AREA_MINING_ENABLED_TAG);
    }

    private static void setAreaMiningEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(AREA_MINING_ENABLED_TAG, enabled);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_pickaxe.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_pickaxe.2"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.etherium_pickaxe.3"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_pickaxe.4"));

        if (!isAreaMiningEnabled(stack)) {
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.etherium_pickaxe.disabled"));
        }
    }
}