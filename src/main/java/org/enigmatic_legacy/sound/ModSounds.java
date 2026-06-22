package org.enigmatic_legacy.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 模组音效注册。
 */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, EnigmaticLegacy.MODID);

    /**
     * 原 Enigmatic Legacy 的 CHARGED_ON。
     * 原注册名：misc.hhon
     */
    public static final DeferredHolder<SoundEvent, SoundEvent> CHARGED_ON =
            SOUND_EVENTS.register(
                    "misc.hhon",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "misc.hhon")
                    )
            );

    private ModSounds() {
    }
}