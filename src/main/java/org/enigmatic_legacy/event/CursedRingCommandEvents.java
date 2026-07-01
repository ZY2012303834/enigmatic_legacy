package org.enigmatic_legacy.event;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.enigmatic_legacy.entity.PermanentItemEntity;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.StorageCrystal;
import org.enigmatic_legacy.util.AbyssalHeartHelper;

public final class CursedRingCommandEvents {
    private static final String SET_CURSED_TIME_COMMAND = "enigmatic_legacy_set_cursed_time";
    private static final String CLEAR_SOUL_CRYSTAL_LOSS_COMMAND = "enigmatic_legacy_clear_soul_crystal_loss";

    private CursedRingCommandEvents() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(SET_CURSED_TIME_COMMAND)
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("percentage", DoubleArgumentType.doubleArg(0.0D, 100.0D))
                                .executes(context -> setCursedTime(
                                        context.getSource(),
                                        context.getSource().getPlayerOrException(),
                                        DoubleArgumentType.getDouble(context, "percentage")
                                )))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("percentage", DoubleArgumentType.doubleArg(0.0D, 100.0D))
                                        .executes(context -> setCursedTime(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                DoubleArgumentType.getDouble(context, "percentage")
                                        ))))
        );

        event.getDispatcher().register(
                Commands.literal(CLEAR_SOUL_CRYSTAL_LOSS_COMMAND)
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> clearSoulCrystalLoss(
                                context.getSource(),
                                context.getSource().getPlayerOrException()
                        ))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> clearSoulCrystalLoss(
                                        context.getSource(),
                                        EntityArgument.getPlayer(context, "player")
                                )))
        );
    }

    private static int setCursedTime(CommandSourceStack source, ServerPlayer player, double percentage) throws CommandSyntaxException {
        long cursedTicks = AbyssalHeartHelper.setCursedPlayTimeFraction(player, percentage / 100.0D);

        if (cursedTicks < 0L) {
            source.sendFailure(Component.literal("Cannot set cursed ring time: target player has no recorded play time."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal("Set " + player.getGameProfile().getName()
                        + "'s cursed ring time to "
                        + String.format(java.util.Locale.ROOT, "%.2f%%", percentage)
                        + " (" + AbyssalHeartHelper.formatPlayTime(cursedTicks)
                        + " / " + AbyssalHeartHelper.formatPlayTime(AbyssalHeartHelper.getTotalPlayTime(player)) + ")."),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int clearSoulCrystalLoss(CommandSourceStack source, ServerPlayer player) {
        int lostBefore = ModItems.SOUL_CRYSTAL.get().getLostCrystals(player);
        SoulCrystalCleanup cleanup = destroyOwnedSoulCrystalEntities(player);

        ModItems.SOUL_CRYSTAL.get().setLostCrystals(player, 0);

        source.sendSuccess(
                () -> Component.literal("Cleared " + player.getGameProfile().getName()
                        + "'s soul crystal loss: " + lostBefore + " -> 0. Destroyed "
                        + cleanup.soulCrystals + " soul crystal(s) and cleared "
                        + cleanup.storageCrystals + " extradimensional vessel(s)."),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static SoulCrystalCleanup destroyOwnedSoulCrystalEntities(ServerPlayer player) {
        int soulCrystals = 0;
        int storageCrystals = 0;

        for (ServerLevel level : player.server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!entity.isAlive()) {
                    continue;
                }

                if (entity instanceof PermanentItemEntity permanentItem) {
                    CleanupResult result = clearSoulTargetStack(permanentItem.getItem(), player);

                    if (result.shouldDiscardEntity) {
                        permanentItem.discard();
                    } else if (result.changedStack) {
                        permanentItem.setItem(permanentItem.getItem());
                    }

                    soulCrystals += result.soulCrystals;
                    storageCrystals += result.storageCrystals;
                    continue;
                }

                if (entity instanceof ItemEntity itemEntity) {
                    CleanupResult result = clearSoulTargetStack(itemEntity.getItem(), player);

                    if (result.shouldDiscardEntity) {
                        itemEntity.discard();
                    } else if (result.changedStack) {
                        itemEntity.setItem(itemEntity.getItem());
                    }

                    soulCrystals += result.soulCrystals;
                    storageCrystals += result.storageCrystals;
                }
            }
        }

        return new SoulCrystalCleanup(soulCrystals, storageCrystals);
    }

    private static CleanupResult clearSoulTargetStack(ItemStack stack, ServerPlayer player) {
        if (stack.isEmpty()) {
            return CleanupResult.NONE;
        }

        if (stack.is(ModItems.SOUL_CRYSTAL.get())
                && player.getUUID().equals(org.enigmatic_legacy.item.items.SoulCrystal.getOwnerId(stack))) {
            stack.setCount(0);
            return new CleanupResult(1, 0, true, true);
        }

        if (stack.is(ModItems.STORAGE_CRYSTAL.get())
                && StorageCrystal.clearStoredContentsIfOwned(stack, player)) {
            stack.setCount(0);
            return new CleanupResult(0, 1, true, true);
        }

        return CleanupResult.NONE;
    }

    private record SoulCrystalCleanup(int soulCrystals, int storageCrystals) {
    }

    private record CleanupResult(int soulCrystals, int storageCrystals, boolean changedStack, boolean shouldDiscardEntity) {
        private static final CleanupResult NONE = new CleanupResult(0, 0, false, false);
    }
}
