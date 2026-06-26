package org.enigmatic_legacy.generator.language;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class LanguageGenerator {

    private LanguageGenerator() {
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(EnglishLanguageGenerator::new);
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(ChineseLanguageGenerator::new);
    }
}
