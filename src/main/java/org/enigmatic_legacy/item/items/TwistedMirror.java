package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 扭曲魔镜 / Twisted Mirror。
 *
 * <p>功能：
 * <ul>
 *     <li>只有七咒之戒佩戴者可以使用；</li>
 *     <li>长按蓄力后传送回重生点；</li>
 *     <li>蓄力时显示末影粒子；</li>
 *     <li>如果没有床/重生锚，则回到主世界出生点；</li>
 *     <li>使用后进入 10 秒冷却；</li>
 *     <li>只能在原版三维度使用。</li>
 * </ul>
 */
public class TwistedMirror extends Item {

    /**
     * 使用完成后冷却 10 秒。
     */
    public static final int COOLDOWN_TICKS = 200;

    /**
     * 使用前需要蓄力 2 秒。
     *
     * <p>20 tick = 1 秒，所以 40 tick = 2 秒。
     */
    public static final int USE_DURATION_TICKS = 40;

    public TwistedMirror() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 右键时只开始使用，不立即传送。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isVanillaDimension(level)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 使用动画。
     *
     * <p>使用 BOW 动画，让扭曲魔镜表现得像弓一样需要拉起/蓄力。
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    /**
     * 最大使用时长。
     *
     * <p>达到这个时间后会调用 finishUsingItem。
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return USE_DURATION_TICKS;
    }

    /**
     * 蓄力期间每 tick 调用。
     *
     * <p>这里生成末影粒子，让玩家能看到魔镜正在启动。
     */
    @Override
    public void onUseTick(@NotNull Level level,
                          @NotNull LivingEntity entity,
                          @NotNull ItemStack stack,
                          int remainingUseDuration) {
        super.onUseTick(level, entity, stack, remainingUseDuration);

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 每 2 tick 刷一次粒子，避免太密。
        if (remainingUseDuration % 2 != 0) {
            return;
        }

        double progress = 1.0D - ((double) remainingUseDuration / USE_DURATION_TICKS);

        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                entity.getX(),
                entity.getY() + 1.0D,
                entity.getZ(),
                8 + (int) (progress * 12.0D),
                0.45D,
                0.65D,
                0.45D,
                0.08D
        );
    }

    /**
     * 使用完成后真正执行传送。
     */
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack,
                                              @NotNull Level level,
                                              @NotNull LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player)) {
            return stack;
        }

        if (isVanillaDimension(level)) {
            return stack;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return stack;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return stack;
        }

        spawnDepartureParticles(player);
        teleportBackToSpawn(player);
        spawnArrivalParticlesDelayed(player);

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return stack;
    }

    /**
     * 延迟 1 tick 后在目的地生成末影粒子。
     *
     * <p>传送刚完成的同一 tick 内，客户端可能还没完成位置/维度同步，
     * 所以目标点粒子容易看不到。延迟 1 tick 后再发给该玩家会稳定很多。
     */
    private static void spawnArrivalParticlesDelayed(ServerPlayer player) {
        player.server.tell(new TickTask(player.server.getTickCount() + 1, () -> {
            if (player.isRemoved()) {
                return;
            }

            spawnArrivalParticles(player);
        }));
    }

    /**
     * 传送前生成末影粒子。
     */
    private static void spawnDepartureParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        level.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                48,
                0.7D,
                0.9D,
                0.7D,
                0.15D
        );
    }

    /**
     * 在玩家当前位置生成到达粒子。
     */
    private static void spawnArrivalParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        level.sendParticles(
                player,
                ParticleTypes.PORTAL,
                true,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                64,
                0.8D,
                1.0D,
                0.8D,
                0.18D
        );
    }

    /**
     * 判断是否为原版三维度。
     */
    private static boolean isVanillaDimension(Level level) {
        return level.dimension() != Level.OVERWORLD
                && level.dimension() != Level.NETHER
                && level.dimension() != Level.END;
    }

    /**
     * 将玩家传送回重生点。
     *
     * <p>优先使用床/重生锚位置。
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