package org.enigmatic_legacy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.item.items.EnigmaticEye;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 全知之眼旁白事件。
 * 玩家佩戴已唤醒的全知之眼后，
 * 在特定条件下，全知之眼会向玩家发送旁白文本。
 */
public class EnigmaticEyeQuoteEvents {
    private static final String ROOT_TAG = "EnigmaticEyeQuotes";
    private static final String LAST_QUOTE_TIME_TAG = "LastQuoteGameTime";

    private static final long QUOTE_COOLDOWN_TICKS = 20L * 20L; // 20 秒

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 每秒检查一次即可。
        if (player.tickCount % 20 != 0) {
            return;
        }

        if (!hasActiveEnigmaticEye(player)) {
            return;
        }

        CompoundTag quoteData = getQuoteData(player);

        long gameTime = player.level().getGameTime();

        if (gameTime - quoteData.getLong(LAST_QUOTE_TIME_TAG) < QUOTE_COOLDOWN_TICKS) {
            return;
        }

        // 第一次佩戴已唤醒的全知之眼。
        if (trySayOnce(player, quoteData, "first_wear", "quote.enigmatic_legacy.enigmatic_eye.first_wear")) {
            return;
        }

        // 第一次进入下界。
        if (player.level().dimension() == Level.NETHER
                && trySayOnce(player, quoteData, "first_nether", "quote.enigmatic_legacy.enigmatic_eye.first_nether")) {
            return;
        }

        // 第一次进入末地。
        if (player.level().dimension() == Level.END
                && trySayOnce(player, quoteData, "first_end", "quote.enigmatic_legacy.enigmatic_eye.first_end")) {
            return;
        }

        // 主世界深层区域。
        if (player.level().dimension() == Level.OVERWORLD
                && player.getY() <= -32.0D
                && trySayOnce(player, quoteData, "deep_underground", "quote.enigmatic_legacy.enigmatic_eye.deep_underground")) {
            return;
        }

        // 低生命值。
        if (player.getHealth() <= player.getMaxHealth() * 0.3F
                && trySayOnce(player, quoteData, "low_health", "quote.enigmatic_legacy.enigmatic_eye.low_health")) {
            return;
        }

        // 午夜。
        long dayTime = player.level().getDayTime() % 24000L;

        if (dayTime >= 18000L
                && dayTime <= 19000L
                && trySayOnce(player, quoteData, "midnight", "quote.enigmatic_legacy.enigmatic_eye.midnight")) {
            return;
        }
    }

    /**
     * 判断玩家是否佩戴已唤醒的全知之眼。
     */
    private static boolean hasActiveEnigmaticEye(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.getItem() instanceof EnigmaticEye
                                && !EnigmaticEye.isDormant(stack)
                ))
                .isPresent();
    }

    /**
     * 只播报一次。
     */
    private static boolean trySayOnce(
            ServerPlayer player,
            CompoundTag quoteData,
            String flag,
            String translationKey
    ) {
        if (quoteData.getBoolean(flag)) {
            return false;
        }

        quoteData.putBoolean(flag, true);
        quoteData.putLong(LAST_QUOTE_TIME_TAG, player.level().getGameTime());

        say(player, translationKey);

        return true;
    }

    /**
     * 发送全知之眼旁白。
     */
    private static void say(ServerPlayer player, String translationKey) {
        player.sendSystemMessage(
                Component.translatable("quote.enigmatic_legacy.enigmatic_eye.prefix")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal(" "))
                        .append(Component.translatable(translationKey)
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC))
        );
    }

    /**
     * 获取玩家个人持久数据。
     */
    private static CompoundTag getQuoteData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        if (!persistentData.contains(ROOT_TAG)) {
            persistentData.put(ROOT_TAG, new CompoundTag());
        }

        return persistentData.getCompound(ROOT_TAG);
    }
}