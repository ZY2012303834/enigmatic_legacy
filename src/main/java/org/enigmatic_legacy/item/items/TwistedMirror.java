package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 扭曲魔镜 / Twisted Mirror。
 *
 * <p>原项目功能：
 * <ul>
 *     <li>只有七咒之戒佩戴者可以使用；</li>
 *     <li>右键后将玩家传送回重生点；</li>
 *     <li>如果没有床/重生锚，则回到主世界出生点；</li>
 *     <li>使用后进入 10 秒冷却；</li>
 *     <li>只能在原版三维度使用。</li>
 * </ul>
 */
public class TwistedMirror extends Item {

    /**
     * 10 秒冷却。
     *
     * <p>Minecraft 中 20 tick = 1 秒，所以 200 tick = 10 秒。
     */
    public static final int COOLDOWN_TICKS = 200;

    public TwistedMirror() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isVanillaDimension(level)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            teleportBackToSpawn(serverPlayer);
            serverPlayer.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResultHolder.success(stack);
    }

    /**
     * 判断是否为原版三维度。
     *
     * <p>原项目限制为 vanilla dimension。
     */
    private static boolean isVanillaDimension(Level level) {
        return level.dimension() == Level.OVERWORLD
                || level.dimension() == Level.NETHER
                || level.dimension() == Level.END;
    }

    /**
     * 将玩家传送回重生点。
     *
     * <p>优先使用玩家设置的床/重生锚位置；
     * 如果玩家没有设置重生点，则传送回主世界出生点。
     */
    private static void teleportBackToSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos targetPos = player.getRespawnPosition();
        float yaw = player.getRespawnAngle();

        if (targetLevel == null || targetPos == null) {
            targetLevel = player.server.overworld();
            targetPos = targetLevel.getSharedSpawnPos();
            yaw = targetLevel.getSharedSpawnAngle();
        }

        player.stopRiding();
        player.teleportTo(
                targetLevel,
                targetPos.getX() + 0.5D,
                targetPos.getY() + 0.1D,
                targetPos.getZ() + 0.5D,
                yaw,
                player.getXRot()
        );

        player.resetFallDistance();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.twisted_mirror1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.twisted_mirror2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.twisted_mirror3")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}