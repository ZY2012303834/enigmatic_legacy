package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.network.CursedRingTimerPayload;

import java.util.Locale;

/**
 * 深渊之心工具类。
 * 负责：
 * 1. 记录玩家总在线时间；
 * 2. 记录玩家佩戴七咒之戒的时间；
 * 3. 判断玩家是否达到 99.5% 七咒折磨比例；
 * 4. 记录玩家已经生成过多少个深渊之心。
 */
public final class AbyssalHeartHelper {
    public static final double REQUIRED_SUFFERING_FRACTION = 0.995D;

    private static final String CURSED_PLAY_TIME_TAG = "enigmatic_legacy_cursed_play_time";
    private static final String ABYSSAL_HEARTS_GAINED_TAG = "enigmatic_legacy_abyssal_hearts_gained";

    /**
     * 原项目 1.20.X 源码里限制为 5：
     * heartsGained < 5
     * 这里保留这个上限。
     */
    public static final int MAX_ABYSSAL_HEARTS_PER_PLAYER = 5;

    private AbyssalHeartHelper() {
    }

    /**
     * 七咒之戒每 tick 调用，记录玩家佩戴七咒之戒的时间。
     */
    public static void tickCursedRingWear(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        long cursedPlayTime = data.getLong(CURSED_PLAY_TIME_TAG) + 1L;
        data.putLong(CURSED_PLAY_TIME_TAG, cursedPlayTime);

        if (player.tickCount % 20 == 0) {
            syncTimer(serverPlayer);
        }
    }

    /**
     * 是否达到深渊之心资格。
     * 条件：
     * 1. 当前必须佩戴七咒之戒；
     * 2. 佩戴七咒时间 / 总游戏时间 >= 99.5%。
     */
    public static boolean isWorthy(Player player) {
        if (!CursedRingHelper.hasCursedRing(player)) {
            return false;
        }

        return getSufferingFraction(player) >= REQUIRED_SUFFERING_FRACTION;
    }

    /**
     * 获取七咒折磨比例。
     * 返回值：
     * 0.995 = 99.5%
     */
    public static double getSufferingFraction(Player player) {
        long totalPlayTime = getTotalPlayTime(player);
        long cursedPlayTime = getCursedPlayTime(player);

        if (totalPlayTime <= 0L || cursedPlayTime <= 0L) {
            return 0.0D;
        }

        return Math.min(1.0D, (double) cursedPlayTime / (double) totalPlayTime);
    }

    public static String getSufferingPercentage(Player player) {
        return String.format(Locale.ROOT, "%.1f%%", getSufferingFraction(player) * 100.0D);
    }

    /**
     * 获取玩家总在线时间 tick。
     *
     * 这个时间由 AbyssalHeartEvents.onPlayerTick(...) 每 tick 记录。
     */
    public static long getTotalPlayTime(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        }

        return player.getPersistentData().getLong(CursedRingTimerPayload.CLIENT_TOTAL_PLAY_TIME_TAG);
    }

    /**
     * 获取玩家佩戴七咒之戒的在线时间 tick。
     *
     * 只有玩家当前佩戴七咒之戒时，
     * tickPlaytime(...) 才会增加这个数值。
     */
    public static long getCursedPlayTime(Player player) {
        return player.getPersistentData().getLong(CURSED_PLAY_TIME_TAG);
    }

    /**
     * 获取深渊之心要求的七咒折磨比例文本。
     *
     * 默认要求：
     * 99.5%
     */
    public static String getRequiredSufferingPercentage() {
        return String.format(Locale.ROOT, "%.1f%%", REQUIRED_SUFFERING_FRACTION * 100.0D);
    }

    /**
     * 把 tick 时间格式化成更容易阅读的文本。
     * 20 tick = 1 秒。
     * 显示规则：
     * - 超过 1 天：显示 天 + 小时 + 分钟
     * - 超过 1 小时：显示 小时 + 分钟 + 秒
     * - 超过 1 分钟：显示 分钟 + 秒
     * - 否则：显示 秒
     */
    public static String formatPlayTime(long ticks) {
        long totalSeconds = Math.max(0L, ticks / 20L);

        long days = totalSeconds / 86400L;
        long hours = (totalSeconds % 86400L) / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        if (days > 0L) {
            return String.format(Locale.ROOT, "%dd %dh %dm", days, hours, minutes);
        }

        if (hours > 0L) {
            return String.format(Locale.ROOT, "%dh %dm %ds", hours, minutes, seconds);
        }

        if (minutes > 0L) {
            return String.format(Locale.ROOT, "%dm %ds", minutes, seconds);
        }

        return String.format(Locale.ROOT, "%ds", seconds);
    }

    public static boolean canGainAnotherAbyssalHeart(Player player) {
        return getAbyssalHeartsGained(player) < MAX_ABYSSAL_HEARTS_PER_PLAYER;
    }

    public static int getAbyssalHeartsGained(Player player) {
        return player.getPersistentData().getInt(ABYSSAL_HEARTS_GAINED_TAG);
    }

    public static void incrementAbyssalHeartsGained(Player player) {
        player.getPersistentData().putInt(
                ABYSSAL_HEARTS_GAINED_TAG,
                getAbyssalHeartsGained(player) + 1
        );
    }

    /**
     * 玩家死亡重生时复制统计数据。
     */
    public static void copyPersistentData(Player oldPlayer, Player newPlayer) {
        CompoundTag oldData = oldPlayer.getPersistentData();
        CompoundTag newData = newPlayer.getPersistentData();

        newData.putLong(CURSED_PLAY_TIME_TAG, oldData.getLong(CURSED_PLAY_TIME_TAG));
        newData.putInt(ABYSSAL_HEARTS_GAINED_TAG, oldData.getInt(ABYSSAL_HEARTS_GAINED_TAG));
    }

    public static void syncTimer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(
                player,
                new CursedRingTimerPayload(getCursedPlayTime(player), getTotalPlayTime(player))
        );
    }

    public static void sendUnworthyMessage(Player player) {
        player.displayClientMessage(
                Component.translatable(
                                "message.enigmatic_legacy.abyssal_heart.unworthy",
                                getSufferingPercentage(player)
                        )
                        .withStyle(ChatFormatting.DARK_PURPLE),
                true
        );
    }
}
