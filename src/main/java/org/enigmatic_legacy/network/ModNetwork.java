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

        registrar.playToServer(
                SpellstoneUsePayload.TYPE,
                SpellstoneUsePayload.STREAM_CODEC,
                SpellstoneUsePayload::handle
        );

        registrar.playToClient(
                PlayerMotionPayload.TYPE,
                PlayerMotionPayload.STREAM_CODEC,
                PlayerMotionPayload::handle
        );

        registrar.playToClient(
                ForceProjectileRotationPayload.TYPE,
                ForceProjectileRotationPayload.STREAM_CODEC,
                ForceProjectileRotationPayload::handle
        );

        registrar.playToClient(
                HeartOfCreationGuardPayload.TYPE,
                HeartOfCreationGuardPayload.STREAM_CODEC,
                HeartOfCreationGuardPayload::handle
        );

        registrar.playToClient(
                CursedRingTimerPayload.TYPE,
                CursedRingTimerPayload.STREAM_CODEC,
                CursedRingTimerPayload::handle
        );

        registrar.playToClient(
                TotemOfMaliceActivationPayload.TYPE,
                TotemOfMaliceActivationPayload.STREAM_CODEC,
                TotemOfMaliceActivationPayload::handle
        );

        registrar.playToServer(
                ScrollUsePayload.TYPE,
                ScrollUsePayload.STREAM_CODEC,
                ScrollUsePayload::handle
        );

        registrar.playToServer(
                MajesticElytraBoostPayload.TYPE,
                MajesticElytraBoostPayload.STREAM_CODEC,
                MajesticElytraBoostPayload::handle
        );
    }
}
