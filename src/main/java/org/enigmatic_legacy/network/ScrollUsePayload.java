package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.items.ScrollOfAgelessWisdom;
import org.enigmatic_legacy.util.ScrollOfAgelessWisdomHelper;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端按下“卷轴快捷键”后发给服务端。
 * 触发条件：
 * 客户端已经确认玩家按下 Shift + 绑定键。
 * 服务端效果：
 * 查找装备在奥秘卷轴栏的永恒智慧卷轴，
 * 然后启用 / 停用它。
 */
public record ScrollUsePayload() implements CustomPacketPayload {
    public static final Type<ScrollUsePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "trigger_scroll")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ScrollUsePayload> STREAM_CODEC =
            StreamCodec.unit(new ScrollUsePayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScrollUsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            ItemStack stack = ScrollOfAgelessWisdomHelper.findScroll(player)
                    .orElse(ItemStack.EMPTY);

            if (stack.getItem() instanceof ScrollOfAgelessWisdom) {
                ScrollOfAgelessWisdom.toggleActive(player, stack);
            }
        });
    }
}