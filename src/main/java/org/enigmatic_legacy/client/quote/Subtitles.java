package org.enigmatic_legacy.client.quote;

import net.minecraft.client.resources.language.I18n;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Subtitles {
    private static final double EXTRA_DISPLAY_SECONDS = 1.0D;

    private final double duration;
    private final NavigableMap<Double, Integer> lines = new TreeMap<>();
    private String quoteName;

    public Subtitles(double duration) {
        this.duration = duration;
        this.lines.put(0.0D, 1);
    }

    public Subtitles addLine(double time) {
        this.lines.put(time, this.lines.size() + 1);
        return this;
    }

    public Subtitles bind(String quoteName) {
        this.quoteName = quoteName;
        return this;
    }

    public double duration() {
        return duration + EXTRA_DISPLAY_SECONDS;
    }

    public String getLine(double playTime) {
        var entry = this.lines.floorEntry(playTime);
        Integer line = entry.getValue();
        String text = I18n.get("quote." + quoteName + "_" + line);
        String[] parts = text.split("\\\\n|\n");

        if (parts.length <= 1) {
            return text;
        }

        double start = entry.getKey();
        Double nextStart = this.lines.higherKey(start);
        double end = nextStart == null ? duration() : nextStart;
        double partLength = Math.max((end - start) / parts.length, 0.1D);
        int partIndex = Math.min((int) ((playTime - start) / partLength), parts.length - 1);

        return parts[partIndex];
    }
}
