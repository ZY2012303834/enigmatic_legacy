package org.enigmatic_legacy.client.quote;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

public final class QuoteHandler {
    public static final QuoteHandler INSTANCE = new QuoteHandler();

    private static final RandomSource RANDOM = RandomSource.create();

    private Quote currentQuote;
    private int delayTicks = 0;
    private double playTime = 0.0D;

    private QuoteHandler() {
    }

    public void playQuote(Quote quote, int delayTicks) {
        if (this.currentQuote != null) {
            return;
        }

        this.currentQuote = quote;
        this.delayTicks = delayTicks;
        this.playTime = 0.0D;
    }

    @SubscribeEvent
    public void onClientTick(PlayerTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || event.getEntity() != minecraft.player) {
            return;
        }

        if (this.currentQuote == null) {
            return;
        }

        if (this.delayTicks > 0) {
            if (!(minecraft.screen instanceof LevelLoadingScreen)
                    && !(minecraft.screen instanceof ReceivingLevelScreen)) {
                this.delayTicks--;
            }

            if (this.delayTicks > 0) {
                return;
            }
        }

        if (this.playTime <= 0.0D) {
            minecraft.getSoundManager().play(new SimpleSoundInstance(
                    this.currentQuote.soundLocation(),
                    SoundSource.VOICE,
                    0.7F,
                    1.0F,
                    RANDOM,
                    false,
                    0,
                    net.minecraft.client.resources.sounds.SoundInstance.Attenuation.NONE,
                    0.0D,
                    0.0D,
                    0.0D,
                    true
            ));

            this.playTime = 0.001D;
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            drawQuote(event.getGuiGraphics());
        }
    }

    @SubscribeEvent
    public void onRenderScreen(ScreenEvent.Render.Post event) {
        drawQuote(event.getGuiGraphics());
    }

    private void drawQuote(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (this.currentQuote == null || this.delayTicks > 0) {
            return;
        }

        this.playTime += minecraft.getTimer().getGameTimeDeltaPartialTick(false) / 20.0D;

        double remaining = this.currentQuote.subtitles().duration() - this.playTime;

        if (remaining <= 0.1D) {
            this.currentQuote = null;
            this.playTime = 0.0D;
            return;
        }

        if (this.playTime < 0.05D) {
            return;
        }

        String line = this.currentQuote.subtitles().getLine(this.playTime);
        List<String> wrapped = wrap(line, 260, minecraft);
        Window window = minecraft.getWindow();

        int lineHeight = minecraft.font.lineHeight + 2;
        int y = window.getGuiScaledHeight() - 70 - lineHeight * (wrapped.size() - 1);

        double fadeIn = Math.min(1.0D, this.playTime / 0.5D);
        double fadeOut = Math.min(1.0D, remaining / 0.5D);
        double alpha = Math.min(fadeIn, fadeOut);

        int textAlpha = Mth.clamp((int) (alpha * 255.0D), 0, 255);
        int rectAlpha = Mth.clamp((int) (alpha * 68.0D), 0, 68);

        int maxWidth = 0;

        for (String s : wrapped) {
            maxWidth = Math.max(maxWidth, minecraft.font.width(s));
        }

        int centerX = window.getGuiScaledWidth() / 2;
        int rectColor = rectAlpha << 24;

        for (int i = 0; i < wrapped.size(); i++) {
            String s = wrapped.get(i);
            int width = minecraft.font.width(s);
            int x = centerX - width / 2;
            int lineY = y + i * lineHeight;

            graphics.fill(centerX - maxWidth / 2 - 6, lineY - 2, centerX + maxWidth / 2 + 6, lineY + minecraft.font.lineHeight + 2, rectColor);
            graphics.drawString(minecraft.font, s, x, lineY, (textAlpha << 24) | 0xFFFF55, true);
        }
    }

    private static List<String> wrap(String text, int maxWidth, Minecraft minecraft) {
        String[] hardLines = text.split("\\\\n|\n");
        List<String> result = new ArrayList<>();

        for (String hardLine : hardLines) {
            StringBuilder current = new StringBuilder();

            for (String word : hardLine.split(" ")) {
                String next = current.isEmpty() ? word : current + " " + word;

                if (minecraft.font.width(next) > maxWidth && !current.isEmpty()) {
                    result.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(next);
                }
            }

            if (!current.isEmpty()) {
                result.add(current.toString());
            }
        }

        return result;
    }
}