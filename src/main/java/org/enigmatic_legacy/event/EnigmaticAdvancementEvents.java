package org.enigmatic_legacy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.CursedRingHelper;

import java.util.Optional;

/**
 * 本项目进度事件。
 * 功能：
 * 1. “前途黑暗”必须玩家真正佩戴七咒之戒才触发；
 * 2. 本项目进度聊天提示使用自定义样式；
 * 3. 聊天提示里的 [ 和 ] 改为紫色。
 */
public final class EnigmaticAdvancementEvents {

    /**
     * “前途黑暗”进度 ID。
     */
    private static final ResourceLocation CURSED_RING_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "main/cursed_ring");

    private EnigmaticAdvancementEvents() {
    }

    /**
     * 每秒检查一次玩家是否佩戴七咒之戒。
     * 说明：
     * advancement JSON 已经改为 minecraft:impossible，
     * 所以这个进度只能通过这里手动授予。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        /*
         * 每秒检查一次，避免每 tick 查询进度。
         */
        if (player.tickCount % 20 != 0) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        grantAdvancement(player, CURSED_RING_ADVANCEMENT);
    }

    /**
     * 本项目进度完成后，发送自定义聊天提示。
     * 注意：
     * AdvancementGenerator 已经把 announce_to_chat 改为 false，
     * 所以不会再出现原版聊天提示。
     */
    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        AdvancementHolder advancement = event.getAdvancement();
        ResourceLocation id = advancement.id();

        /*
         * 只处理本项目 main 分类下的进度。
         * recipes 等其它内部进度不发聊天提示。
         */
        if (!EnigmaticLegacy.MODID.equals(id.getNamespace())) {
            return;
        }

        if (!id.getPath().startsWith("main/")) {
            return;
        }

        /*
         * 根进度不发送聊天提示。
         */
        if ("main/root".equals(id.getPath())) {
            return;
        }

        Optional<DisplayInfo> displayOptional = advancement.value().display();

        if (displayOptional.isEmpty()) {
            return;
        }

        DisplayInfo display = displayOptional.get();
        AdvancementType frame = display.getType();

        /*
         * 标题颜色仍然跟随进度类型：
         * task / goal / challenge 使用原版对应颜色。
         *
         * 但外侧 [ 和 ] 强制改为紫色。
         */
        MutableComponent title = display.getTitle().copy()
                .withStyle(frame.getChatColor());

        MutableComponent bracketedTitle = Component.literal("[")
                .withStyle(ChatFormatting.DARK_PURPLE)
                .append(title)
                .append(Component.literal("]").withStyle(ChatFormatting.DARK_PURPLE));

        /*
         * 使用原版聊天翻译 key：
         * chat.type.advancement.task
         * chat.type.advancement.goal
         * chat.type.advancement.challenge
         *
         * 这样中文/英文提示文本仍然跟随游戏语言。
         */
        MutableComponent message = Component.translatable(
                "chat.type.advancement." + frame.getSerializedName(),
                player.getDisplayName(),
                bracketedTitle
        );

        player.server.getPlayerList().broadcastSystemMessage(message, false);
    }

    /**
     * 手动授予指定进度的所有剩余条件。
     */
    private static void grantAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder advancement = player.server.getAdvancements().get(advancementId);

        if (advancement == null) {
            return;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

        if (progress.isDone()) {
            return;
        }

        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }
}