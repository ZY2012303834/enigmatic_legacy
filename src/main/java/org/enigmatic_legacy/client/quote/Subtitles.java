package org.enigmatic_legacy.client.quote;

import net.minecraft.client.resources.language.I18n;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Subtitles {
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
        return duration;
    }

    public String getLine(double playTime) {
        Integer line = this.lines.floorEntry(playTime).getValue();
        return I18n.get("quote." + quoteName + "_" + line);
    }
}