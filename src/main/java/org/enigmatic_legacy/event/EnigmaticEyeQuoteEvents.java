package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.client.quote.Quote;

import java.util.Objects;

public class EnigmaticEyeQuoteEvents {
    private static final String DEATH_FROM_ENTITY = "DeathFromEntity";
    private static final String DESTROYED_CURSED_RING = "DestroyedCursedRing";
    private static final String TIMES_KILLED_WITHER = "TimesKilledWither";

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTo() == Level.NETHER) {
            Quote.SULFUR_AIR.playOnceIfUnlocked(player, 240);
        } else if (event.getTo() == Level.END) {
            Quote.TORTURED_ROCKS.playOnceIfUnlocked(player, 240);
        }
    }

    @SubscribeEvent
    public static void onEndPortalFrameUse(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.getItemInHand(event.getHand()).is(Items.ENDER_EYE)) {
            return;
        }

        if (!player.level().getBlockState(event.getPos()).is(Blocks.END_PORTAL_FRAME)) {
            return;
        }

        BlockPos clicked = event.getPos();

        Objects.requireNonNull(player.getServer()).execute(() -> {
            if (hasEndPortalAround(player.serverLevel(), clicked)) {
                Quote.END_DOORSTEP.playOnceIfUnlocked(player, 40);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().getBlockState(player.blockPosition()).is(Blocks.END_GATEWAY)
                || player.level().getBlockState(player.blockPosition().above()).is(Blocks.END_GATEWAY)) {
            Quote.I_WANDERED.playOnceIfUnlocked(player, 160);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon
                && event.getSource().getEntity() instanceof ServerPlayer player) {
            Quote.POOR_CREATURE.playOnceIfUnlocked(player, 60);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getPersistentData().putBoolean(
                    DEATH_FROM_ENTITY,
                    event.getSource().getEntity() instanceof LivingEntity
            );
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getEntity() instanceof WitherBoss) {
            handleWitherKilled(player);
        }

        if (event.getEntity() instanceof EnderDragon) {
            Quote.WITH_DRAGONS.playOnceIfUnlocked(player, 140);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.isEndConquered()) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        if (data.getBoolean(DESTROYED_CURSED_RING)) {
            data.remove(DESTROYED_CURSED_RING);
            Quote.random(Quote.RING_DESTRUCTION).play(player, 10);
            return;
        }

        if (player.getRandom().nextFloat() <= 0.2F) {
            return;
        }

        if (data.getBoolean(DEATH_FROM_ENTITY)) {
            Quote.random(Quote.DEATH_QUOTES_ENTITY).playIfUnlocked(player, 10);
        } else {
            Quote.random(Quote.DEATH_QUOTES).playIfUnlocked(player, 10);
        }
    }

    private static void handleWitherKilled(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        int killedWither = data.getInt(TIMES_KILLED_WITHER);

        if (killedWither <= 0) {
            Quote.BREATHES_RELIEVED.play(player, 140);
            data.putInt(TIMES_KILLED_WITHER, killedWither + 1);
        } else if (killedWither == 1) {
            Quote.APPALING_PRESENCE.play(player, 140);
            data.putInt(TIMES_KILLED_WITHER, killedWither + 1);
        } else if (killedWither == 2) {
            Quote.TERRIFYING_FORM.play(player, 140);
            data.putInt(TIMES_KILLED_WITHER, killedWither + 1);
        } else if (killedWither > 2 && killedWither < 5) {
            data.putInt(TIMES_KILLED_WITHER, killedWither + 1);
        } else if (killedWither == 4) {
            Quote.WHETHER_IT_IS.play(player, 140);
            data.putInt(TIMES_KILLED_WITHER, killedWither + 1);
        }
    }

    private static boolean hasEndPortalAround(ServerLevel level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-4, -1, -4), center.offset(4, 1, 4))) {
            if (level.getBlockState(pos).is(Blocks.END_PORTAL)) {
                return true;
            }
        }

        return false;
    }
}