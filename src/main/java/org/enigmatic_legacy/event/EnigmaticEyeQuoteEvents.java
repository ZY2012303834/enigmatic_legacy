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
    /**
     * 玩家死亡原因标记。
     * 用途：
     * - 玩家死亡时记录这次死亡是否来自实体攻击；
     * - 玩家复活后根据这个标记播放不同死亡语音。
     */
    private static final String DEATH_FROM_ENTITY = "DeathFromEntity";

    /**
     * 七咒之戒被摧毁标记。
     * 用途：
     * - 如果死亡或特殊逻辑导致七咒之戒被摧毁；
     * - 复活后优先播放戒指毁灭语音。
     */
    private static final String DESTROYED_CURSED_RING = "DestroyedCursedRing";

    /**
     * 击杀凋灵次数。
     * 用途：
     * - 控制击杀凋灵后的分阶段语音。
     */
    private static final String TIMES_KILLED_WITHER = "TimesKilledWither";

    /**
     * 莫测之眼 / 全知之眼语音解锁标记。
     * 注意：
     * - Quote.isNarratorUnlocked(...) 里使用的就是这个 key。
     * - 玩家死亡后必须复制这个标记，否则复活后语音系统会以为玩家没有唤醒过莫测之眼。
     */
    private static final String NARRATOR_UNLOCKED = "ELUnlockedNarrator";

    /**
     * 已经听过的一次性语音前缀。
     * Quote.playOnceIfUnlocked(...) 会写入：
     * ELHeardQuote_xxx = true
     * 玩家死亡后也需要复制这些标记，
     * 否则一次性语音可能会在死亡后重复播放。
     */
    private static final String HEARD_QUOTE_PREFIX = "ELHeardQuote_";

    /**
     * 玩家克隆事件。
     * 为什么需要这个事件：
     * - 玩家死亡重生时，Minecraft 会创建一个新的 Player 实体；
     * - 旧玩家身上的 PersistentData 不会自动完整复制到新玩家；
     * - 莫测之眼 / 全知之眼的语音解锁状态、死亡原因、已听过语音等数据都存在 PersistentData 中；
     * - 如果不复制，玩家死亡后复活时，语音系统会认为玩家没有解锁旁白者，从而导致死亡语音不播放。
     * 修复内容：
     * 1. 复制语音解锁标记 ELUnlockedNarrator；
     * 2. 复制死亡原因标记 DeathFromEntity；
     * 3. 复制七咒之戒毁灭标记 DestroyedCursedRing；
     * 4. 复制击杀凋灵次数 TimesKilledWither；
     * 5. 复制所有 ELHeardQuote_ 开头的一次性语音记录。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // 旧玩家数据。
        CompoundTag oldData = event.getOriginal().getPersistentData();

        // 新玩家数据。
        CompoundTag newData = event.getEntity().getPersistentData();

        // 复制莫测之眼 / 全知之眼语音解锁状态。
        copyBoolean(oldData, newData, NARRATOR_UNLOCKED);

        // 复制死亡原因。
        // 这个标记由 onLivingDeath(...) 写入，
        // 复活后的 onPlayerRespawn(...) 会读取它来决定播放哪组死亡语音。
        copyBoolean(oldData, newData, DEATH_FROM_ENTITY);

        // 复制七咒之戒毁灭标记。
        // 如果这个标记丢失，复活后不会播放戒指毁灭语音。
        copyBoolean(oldData, newData, DESTROYED_CURSED_RING);

        // 复制击杀凋灵次数。
        // 防止死亡后凋灵语音阶段被重置。
        if (oldData.contains(TIMES_KILLED_WITHER)) {
            newData.putInt(TIMES_KILLED_WITHER, oldData.getInt(TIMES_KILLED_WITHER));
        }

        // 复制已经听过的一次性语音。
        // 例如 ELHeardQuote_sulfur_air。
        // 否则玩家死亡后，这些一次性语音可能会重新播放。
        for (String key : oldData.getAllKeys()) {
            if (key.startsWith(HEARD_QUOTE_PREFIX)) {
                newData.putBoolean(key, oldData.getBoolean(key));
            }
        }
    }

    /**
     * 复制 boolean 类型的玩家持久数据。
     * 用途：
     * - 玩家死亡重生后，把旧玩家身上的语音相关标记复制到新玩家身上；
     * - 避免复活后莫测之眼 / 全知之眼语音系统失效。
     */
    private static void copyBoolean(CompoundTag oldData, CompoundTag newData, String key) {
        if (oldData.contains(key)) {
            newData.putBoolean(key, oldData.getBoolean(key));
        }
    }


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

        // 死亡语音判定结束后，清理本次死亡原因标记。
        // 这样可以避免该标记长期留在玩家数据里，影响后续判断。
        data.remove(DEATH_FROM_ENTITY);
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