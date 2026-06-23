package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.network.PlayerMotionPayload;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 天使之祝 / Angel's Blessing。
 *
 * 术石主动技能：
 * 向视野方向加速。
 *
 * 被动：
 * 免疫摔落和碰撞伤害；
 * 反射接近弹射物；
 * 自己射出的弹射物加速；
 * 凋零和虚空伤害增加。
 */
public class AngelBlessing extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    public AngelBlessing() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    private static boolean isSpellstoneSlot(SlotContext context) {
        return SPELLSTONE_SLOT.equals(context.identifier());
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 主动技能。
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getAbilities().flying) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        Vec3 finalMotion = getAccelerationMotion(player);

        player.setDeltaMovement(finalMotion);
        player.hurtMarked = true;

        PacketDistributor.sendToPlayer(
                player,
                new PlayerMotionPayload(finalMotion.x, finalMotion.y, finalMotion.z)
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.TRIDENT_RIPTIDE_1.value(),
                SoundSource.PLAYERS,
                1.0F,
                0.6F + player.getRandom().nextFloat() * 0.1F
        );

        player.getCooldowns().addCooldown(stack.getItem(), ConfigCommon.ANGEL_BLESSING_COOLDOWN.get());
    }

    private static @NotNull Vec3 getAccelerationMotion(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 motion = player.getDeltaMovement();

        Vec3 acceleration;

        if (player.isFallFlying()) {
            acceleration = look.scale(ConfigCommon.ANGEL_BLESSING_ACCELERATION_MODIFIER_ELYTRA.get());
            acceleration = acceleration.scale(1.0D / (Math.max(0.15D, motion.length()) * 2.25D));
        } else {
            acceleration = look.scale(ConfigCommon.ANGEL_BLESSING_ACCELERATION_MODIFIER.get());
            acceleration = acceleration.add(
                    0.0D,
                    player.getJumpBoostPower() * 0.6D + player.getGravity() * 0.8D,
                    0.0D
            );
        }

        return motion.add(acceleration);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.active")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.angel_blessing.active")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.spellstone.cooldown",
                String.format("%.1f", ConfigCommon.ANGEL_BLESSING_COOLDOWN.get() / 20.0F)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.angel_blessing.passive.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.angel_blessing.passive.2",
                ConfigCommon.ANGEL_BLESSING_DEFLECT_CHANCE.get() + "%"
        ).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.angel_blessing.passive.3")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.angel_blessing.passive.4")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.angel_blessing.passive.5")
                .withStyle(ChatFormatting.RED));
    }
}