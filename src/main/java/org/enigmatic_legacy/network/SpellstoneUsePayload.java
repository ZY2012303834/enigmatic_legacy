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
import org.enigmatic_legacy.item.items.*;
import org.enigmatic_legacy.util.*;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull Type<SpellstoneUsePayload> type() {
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

            // 按优先级查找当前佩戴的术石。
            // 你的项目目前是一个 spellstone 槽，所以理论上只会找到一个。
            // 这里保留顺序，方便以后扩展更多术石。
            ItemStack stack = AngelBlessingHelper.findAngelBlessing(player).orElse(ItemStack.EMPTY);

            if (stack.isEmpty()) {
                stack = OceanStoneHelper.findOceanStone(player).orElse(ItemStack.EMPTY);
            }

            if (stack.isEmpty()) {
                stack = EyeOfNebulaHelper.findEyeOfNebula(player).orElse(ItemStack.EMPTY);
            }

            if (stack.isEmpty()) {
                stack = NonEuclideanCubeHelper.findNonEuclideanCube(player).orElse(ItemStack.EMPTY);
            }

            if (stack.isEmpty()) {
                stack = HeartOfCreationHelper.findHeartOfCreation(player).orElse(ItemStack.EMPTY);
            }

            // 根据找到的物品类型触发对应主动技能。
            switch (stack.getItem()) {
                case AngelBlessing angelBlessing -> angelBlessing.triggerActiveAbility(level, player, stack);
                case OceanStone oceanStone -> oceanStone.triggerActiveAbility(level, player, stack);
                case EyeOfNebula eyeOfNebula -> eyeOfNebula.triggerActiveAbility(level, player, stack);
                case NonEuclideanCube cube -> cube.triggerActiveAbility(level, player, stack);
                case HeartOfCreation heartOfCreation -> heartOfCreation.triggerActiveAbility(level, player, stack);

                default -> {
                }
            }
        });
    }
}