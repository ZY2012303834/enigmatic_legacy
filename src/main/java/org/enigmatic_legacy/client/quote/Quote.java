package org.enigmatic_legacy.client.quote;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.network.PlayQuotePayload;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class Quote {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final List<Quote> VALUES = new ArrayList<>();

    public static final Quote NO_PERIL = quote("no_peril", new Subtitles(8.0D));
    public static final Quote END_DOORSTEP = quote("end_doorstep", new Subtitles(6.0D));
    public static final Quote ONLY_BECAUSE = quote("only_because", new Subtitles(6.0D));
    public static final Quote DEMISE_IS = quote("demise_is", new Subtitles(7.0D));
    public static final Quote WE_FALL = quote("we_fall", new Subtitles(6.5D));
    public static final Quote YOU_WILL_ENDURE = quote("you_will_endure", new Subtitles(5.5D));
    public static final Quote OBLIVION_REJECTS = quote("oblivion_rejects", new Subtitles(9.0D));
    public static final Quote SETBACK = quote("setback", new Subtitles(5.0D));
    public static final Quote DEATH_MAY = quote("death_may", new Subtitles(8.25D));
    public static final Quote ETERNITY_TO_KEEP = quote("eternity_to_keep", new Subtitles(7.0D));
    public static final Quote VIOLENCE_CALLS = quote("violence_calls", new Subtitles(7.0D));
    public static final Quote IMMORTAL = quote("immortal", new Subtitles(8.5D));
    public static final Quote APPALING_PRESENCE = quote("appaling_presence", new Subtitles(9.5D));
    public static final Quote ITS_DESTRUCTION = quote("its_destruction", new Subtitles(9.0D));

    public static final Quote I_WANDERED = quote("i_wandered", new Subtitles(11.5D).addLine(4.75D));
    public static final Quote ANOTHER_DEMIGOD = quote("another_demigod", new Subtitles(14.0D).addLine(5.25D));
    public static final Quote ANOTHER_EON = quote("another_eon", new Subtitles(12.0D).addLine(7.5D));
    public static final Quote PERHAPS_YOU = quote("perhaps_you", new Subtitles(13.0D).addLine(7.5D));
    public static final Quote SULFUR_AIR = quote("sulfur_air", new Subtitles(10.25D).addLine(5.6D));
    public static final Quote TORTURED_ROCKS = quote("tortured_rocks", new Subtitles(12.25D).addLine(7.5D));
    public static final Quote BREATHES_RELIEVED = quote("breathes_relieved", new Subtitles(11.5D).addLine(7.8D));
    public static final Quote WHETHER_IT_IS = quote("whether_it_is", new Subtitles(12.5D).addLine(7.6D));
    public static final Quote POOR_CREATURE = quote("poor_creature", new Subtitles(15.0D).addLine(7.9D));
    public static final Quote HORRIBLE_EXISTENCE = quote("horrible_existence", new Subtitles(12.0D).addLine(7.6D));
    public static final Quote COUNTLESS_DEAD = quote("countless_dead", new Subtitles(16.0D).addLine(8.8D));
    public static final Quote WITH_DRAGONS = quote("with_dragons", new Subtitles(32.0D).addLine(9.7D).addLine(14.0D).addLine(21.2D));
    public static final Quote TERRIFYING_FORM = quote("terrifying_form", new Subtitles(14.5D).addLine(6.2D));
    public static final Quote TOLL_PAID = quote("toll_paid", new Subtitles(11.5D).addLine(7.4D));

    public static final List<Quote> DEATH_QUOTES = List.of(
            NO_PERIL, ONLY_BECAUSE, DEATH_MAY, DEMISE_IS,
            WE_FALL, YOU_WILL_ENDURE, OBLIVION_REJECTS, SETBACK
    );

    public static final List<Quote> DEATH_QUOTES_ENTITY = List.of(
            NO_PERIL, ONLY_BECAUSE, DEATH_MAY, DEMISE_IS,
            WE_FALL, YOU_WILL_ENDURE, OBLIVION_REJECTS, SETBACK,
            ETERNITY_TO_KEEP, IMMORTAL, VIOLENCE_CALLS
    );

    public static final List<Quote> NARRATOR_INTROS = List.of(
            ANOTHER_DEMIGOD, ANOTHER_EON, PERHAPS_YOU
    );

    public static final List<Quote> RING_DESTRUCTION = List.of(
            TOLL_PAID, ITS_DESTRUCTION
    );

    private final int id;
    private final String name;
    private final Subtitles subtitles;

    private Quote(String name, Subtitles subtitles) {
        this.id = VALUES.size();
        this.name = name;
        this.subtitles = subtitles;
        VALUES.add(this);
    }

    private static Quote quote(String name, Subtitles subtitles) {
        return new Quote(name, subtitles.bind(name));
    }

    public static Quote byId(int id) {
        return VALUES.get(id);
    }

    public static List<Quote> values() {
        return VALUES;
    }

    public static Quote random(List<Quote> quotes) {
        return quotes.get(RANDOM.nextInt(quotes.size()));
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Subtitles subtitles() {
        return subtitles;
    }

    public ResourceLocation soundLocation() {
        return ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "quote." + name);
    }

    public void play(ServerPlayer player, int delayTicks) {
        PacketDistributor.sendToPlayer(player, new PlayQuotePayload(this.id, delayTicks));
    }

    public void playIfUnlocked(ServerPlayer player, int delayTicks) {
        if (isNarratorUnlocked(player)) {
            play(player, delayTicks);
        }
    }

    public void playOnceIfUnlocked(ServerPlayer player, int delayTicks) {
        if (!isNarratorUnlocked(player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        String key = "ELHeardQuote_" + name;

        if (data.getBoolean(key)) {
            return;
        }

        data.putBoolean(key, true);
        play(player, delayTicks);
    }

    public static boolean isNarratorUnlocked(ServerPlayer player) {
        return player.getPersistentData().getBoolean("ELUnlockedNarrator");
    }

    public static void unlockNarrator(ServerPlayer player) {
        player.getPersistentData().putBoolean("ELUnlockedNarrator", true);
    }
}