package org.enigmatic_legacy.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.enigmatic_legacy.EnigmaticLegacy;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(EnigmaticLegacy.MODID).versioned("1");

        registrar.playToClient(
                PlayQuotePayload.TYPE,
                PlayQuotePayload.STREAM_CODEC,
                PlayQuotePayload::handle
        );
    }
}