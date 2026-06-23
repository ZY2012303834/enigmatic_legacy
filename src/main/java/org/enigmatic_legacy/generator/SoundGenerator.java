package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.sound.ModSounds;

/**
 * 音效数据生成器。
 */
public class SoundGenerator extends SoundDefinitionsProvider {

    public SoundGenerator(PackOutput output, ExistingFileHelper helper) {
        super(output, EnigmaticLegacy.MODID, helper);
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

            add(
                    soundEvent.get(),
                    definition().with(sound(sound))
            );
        });

        // 原作者 misc.shield_trigger 音效。
        // 注意：音效事件叫 misc.shield_trigger，
        // 但实际 OGG 文件不是 shield_trigger.ogg，
        // 而是 shield_hit_0.ogg 和 shield_hit_1.ogg。
        add(
                ModSounds.SHIELD_TRIGGER.getId(),
                definition()
                        .with(sound(ResourceLocation.fromNamespaceAndPath(
                                EnigmaticLegacy.MODID,
                                "misc/shield_hit_0"
                        )))
                        .with(sound(ResourceLocation.fromNamespaceAndPath(
                                EnigmaticLegacy.MODID,
                                "misc/shield_hit_1"
                        )))
        );
    }
}
