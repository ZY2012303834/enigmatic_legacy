package org.enigmatic_legacy.generator.patchouli;
import net.neoforged.neoforge.data.event.GatherDataEvent;
/**
 * Patchouli ?????????
 */
public final class PatchouliBookGenerator {
    private PatchouliBookGenerator() {
    }
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator()
                .getVanillaPack(event.includeServer())
                .addProvider(PatchouliBookDataGenerator::new);
        event.getGenerator()
                .getVanillaPack(event.includeClient())
                .addProvider(EnglishPatchouliBookGenerator::new);
        event.getGenerator()
                .getVanillaPack(event.includeClient())
                .addProvider(ChinesePatchouliBookGenerator::new);
    }
}
