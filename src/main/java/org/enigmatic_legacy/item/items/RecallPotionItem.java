package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.event.TeleportParticleEvents;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 召回药水。
 *
 * 注意：
 * 这是独立物品，不是原版 PotionContents 药水。
 * 这样 JEI 不会再自动生成喷溅型 / 滞留型召回药水配方。
 */
public class RecallPotionItem extends Item {
    private static final int DRINK_DURATION = 32;

    public RecallPotionItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return DRINK_DURATION;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull LivingEntity livingEntity
    ) {
        if (!level.isClientSide && livingEntity instanceof ServerPlayer player) {
            teleportPlayer(player);
        }

        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);

            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

            if (stack.isEmpty()) {
                return bottle;
            }

            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }

        return stack;
    }

    private static void teleportPlayer(ServerPlayer player) {
        ServerLevel currentLevel = player.serverLevel();

        // 在末地使用时，传送到末地主岛黑曜石平台附近。
        if (currentLevel.dimension() == Level.END) {
            teleportToEndPlatform(player, currentLevel);
            return;
        }

        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos targetPos = player.getRespawnPosition();
        float yaw = player.getRespawnAngle();

        if (targetLevel == null || targetPos == null) {
            targetLevel = player.server.overworld();
            targetPos = targetLevel.getSharedSpawnPos();
            yaw = targetLevel.getSharedSpawnAngle();
        }

        TeleportParticleEvents.spawnDepartureParticles(player);

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

        TeleportParticleEvents.scheduleArrivalParticles(player, 3);
    }

    private static void teleportToEndPlatform(ServerPlayer player, ServerLevel endLevel) {
        TeleportParticleEvents.spawnDepartureParticles(player);

        player.stopRiding();
        player.teleportTo(
                endLevel,
                100.5D,
                50.1D,
                0.5D,
                player.getYRot(),
                player.getXRot()
        );
        player.resetFallDistance();

        TeleportParticleEvents.scheduleArrivalParticles(player, 3);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.recall_potion.1")
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.recall_potion.2")
                .withStyle(ChatFormatting.GRAY));
    }
}