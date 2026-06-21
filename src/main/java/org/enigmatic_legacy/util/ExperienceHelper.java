package org.enigmatic_legacy.util;

import net.minecraft.world.entity.player.Player;

/**
 * Small XP helpers mirroring the arithmetic used by vanilla player levels.
 */
public final class ExperienceHelper {

    private ExperienceHelper() {
    }

    public static int getPlayerXP(Player player) {
        return getExperienceForLevel(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static void drainPlayerXP(Player player, int amount) {
        if (amount > 0) {
            player.giveExperiencePoints(-amount);
        }
    }

    public static void addPlayerXP(Player player, int amount) {
        if (amount > 0) {
            player.giveExperiencePoints(amount);
        }
    }

    private static int getExperienceForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }

        if (level <= 31) {
            return (int) (2.5D * level * level - 40.5D * level + 360.0D);
        }

        return (int) (4.5D * level * level - 162.5D * level + 2220.0D);
    }
}
