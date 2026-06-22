package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.sound.ModSounds;

/**
 * 音效数据生成器。
 */
public class SoundGenerator extends SoundDefinitionsProvider {
    private final ExistingFileHelper helper;

    public SoundGenerator(PackOutput output, ExistingFileHelper helper) {
        super(output, EnigmaticLegacy.MODID, helper);
        this.helper = helper;
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator()
                .getVanillaPack(event.includeClient())
                .addProvider(output -> new SoundGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    public void registerSounds() {
        add(
                ModSounds.CHARGED_ON.get(),
                definition().with(sound(ResourceLocation.fromNamespaceAndPath(
                        EnigmaticLegacy.MODID,
                        "misc/hhon"
                )))
        );

        ModSounds.QUOTES.forEach((name, soundEvent) -> {
            ResourceLocation sound = ResourceLocation.fromNamespaceAndPath(
                    EnigmaticLegacy.MODID,
                    "quote/" + name
            );
            if (!helper.exists(sound, PackType.CLIENT_RESOURCES, ".ogg", "sounds")) {
                return;
            }

            add(
                    soundEvent.get(),
                    definition().with(sound(ResourceLocation.fromNamespaceAndPath(
                        EnigmaticLegacy.MODID,
                        "quote/" + name
                    )))
            );
        });
    }
}
