package org.enigmatic_legacy.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模组音效注册。
 */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, EnigmaticLegacy.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CHARGED_ON = register("misc.hhon");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHIELD_TRIGGER = register("misc.shield_trigger");
    // 饕餮之锅普通攻击音效。
    // 对应原项目：misc.pan_clang
    public static final DeferredHolder<SoundEvent, SoundEvent> PAN_CLANG =
            register("misc.pan_clang");

    // 饕餮之锅极低概率彩蛋攻击音效。
    // 对应原项目：misc.pan_clang_fr
    public static final DeferredHolder<SoundEvent, SoundEvent> PAN_CLANG_FR =
            register("misc.pan_clang_fr");



    public static final Map<String, DeferredHolder<SoundEvent, SoundEvent>> QUOTES = new LinkedHashMap<>();

    static {
        registerQuote("no_peril");
        registerQuote("end_doorstep");
        registerQuote("only_because");
        registerQuote("demise_is");
        registerQuote("we_fall");
        registerQuote("you_will_endure");
        registerQuote("oblivion_rejects");
        registerQuote("setback");
        registerQuote("death_may");
        registerQuote("eternity_to_keep");
        registerQuote("violence_calls");
        registerQuote("immortal");
        registerQuote("appaling_presence");
        registerQuote("its_destruction");

        registerQuote("i_wandered");
        registerQuote("another_demigod");
        registerQuote("another_eon");
        registerQuote("perhaps_you");
        registerQuote("sulfur_air");
        registerQuote("tortured_rocks");
        registerQuote("breathes_relieved");
        registerQuote("whether_it_is");
        registerQuote("poor_creature");
        registerQuote("horrible_existence");
        registerQuote("countless_dead");
        registerQuote("with_dragons");
        registerQuote("terrifying_form");
        registerQuote("toll_paid");
    }

    private static void registerQuote(String name) {
        QUOTES.put(name, register("quote." + name));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, name)
                )
        );
    }

    private ModSounds() {
    }
}