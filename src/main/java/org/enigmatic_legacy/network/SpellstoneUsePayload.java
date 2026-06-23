package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.items.AngelBlessing;
import org.enigmatic_legacy.item.items.OceanStone;
import org.enigmatic_legacy.util.AngelBlessingHelper;
import org.enigmatic_legacy.util.OceanStoneHelper;

/**
 * 客户端请求触发当前术石主动技能。
 */
public record SpellstoneUsePayload() implements CustomPacketPayload {
    public static final Type<SpellstoneUsePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "trigger_spellstone")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellstoneUsePayload> STREAM_CODEC =
            StreamCodec.unit(new SpellstoneUsePayload());

    @Override
    public Type<SpellstoneUsePayload> type() {
        return TYPE;
    }

    public static void handle(SpellstoneUsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.level() instanceof ServerLevel level)) {
                return;
            }

            ItemStack stack = AngelBlessingHelper.findAngelBlessing(player).orElse(ItemStack.EMPTY);

            if (stack.isEmpty()) {
                stack = OceanStoneHelper.findOceanStone(player).orElse(ItemStack.EMPTY);
            }

            if (stack.getItem() instanceof AngelBlessing angelBlessing) {
                angelBlessing.triggerActiveAbility(level, player, stack);
            } else if (stack.getItem() instanceof OceanStone oceanStone) {
                oceanStone.triggerActiveAbility(level, player, stack);
            }
        });
    }
}