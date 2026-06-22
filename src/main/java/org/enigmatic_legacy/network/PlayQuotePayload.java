package org.enigmatic_legacy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.quote.Quote;
import org.enigmatic_legacy.client.quote.QuoteHandler;

public record PlayQuotePayload(int quoteId, int delayTicks) implements CustomPacketPayload {
    public static final Type<PlayQuotePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "play_quote")
    );

    public static final StreamCodec<ByteBuf, PlayQuotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PlayQuotePayload::quoteId,
            ByteBufCodecs.VAR_INT,
            PlayQuotePayload::delayTicks,
            PlayQuotePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PlayQuotePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> QuoteHandler.INSTANCE.playQuote(
                Quote.byId(payload.quoteId()),
                payload.delayTicks()
        ));
    }
}