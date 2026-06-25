package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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

    private static final String TOTAL_PLAY_TIME_TAG = "enigmatic_legacy_total_play_time";
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
     * 每 tick 记录一次玩家游戏时间。
     * totalPlayTime：
     * 玩家在线总时间。
     * cursedPlayTime：
     * 玩家佩戴七咒之戒时的在线时间。
     */
    public static void tickPlaytime(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        long totalPlayTime = data.getLong(TOTAL_PLAY_TIME_TAG);
        long cursedPlayTime = data.getLong(CURSED_PLAY_TIME_TAG);

        totalPlayTime++;

        if (CursedRingHelper.hasCursedRing(player)) {
            cursedPlayTime++;
        }

        data.putLong(TOTAL_PLAY_TIME_TAG, totalPlayTime);
        data.putLong(CURSED_PLAY_TIME_TAG, cursedPlayTime);
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
        CompoundTag data = player.getPersistentData();

        long totalPlayTime = data.getLong(TOTAL_PLAY_TIME_TAG);
        long cursedPlayTime = data.getLong(CURSED_PLAY_TIME_TAG);

        if (totalPlayTime <= 0L || cursedPlayTime <= 0L) {
            return 0.0D;
        }

        return Math.min(1.0D, (double) cursedPlayTime / (double) totalPlayTime);
    }

    public static String getSufferingPercentage(Player player) {
        return String.format(Locale.ROOT, "%.1f%%", getSufferingFraction(player) * 100.0D);
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

        newData.putLong(TOTAL_PLAY_TIME_TAG, oldData.getLong(TOTAL_PLAY_TIME_TAG));
        newData.putLong(CURSED_PLAY_TIME_TAG, oldData.getLong(CURSED_PLAY_TIME_TAG));
        newData.putInt(ABYSSAL_HEARTS_GAINED_TAG, oldData.getInt(ABYSSAL_HEARTS_GAINED_TAG));
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