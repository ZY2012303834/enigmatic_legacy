package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.quote.Quote;

/**
 * 音效数据生成器。
 * 生成路径：
 * src/generated/resources/assets/enigmatic_legacy/sounds.json
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
        // 原 Enigmatic Legacy 的 CHARGED_ON 音效。
        // sounds.json key: "misc.hhon"
        // 实际音频路径: assets/enigmatic_legacy/sounds/misc/hhon.ogg
        add(
                "misc.hhon",
                definition()
                        .with(sound(ResourceLocation.fromNamespaceAndPath(
                                EnigmaticLegacy.MODID,
                                "misc/hhon"
                        )))
        );

        for (Quote quote : Quote.values()) {
            ResourceLocation sound = ResourceLocation.fromNamespaceAndPath(
                    EnigmaticLegacy.MODID,
                    "quote/" + quote.name()
            );
            if (!helper.exists(sound, PackType.CLIENT_RESOURCES, ".ogg", "sounds")) {
                continue;
            }

            add(
                    "quote." + quote.name(),
                    definition().with(sound(sound))
            );
        }
    }
}
