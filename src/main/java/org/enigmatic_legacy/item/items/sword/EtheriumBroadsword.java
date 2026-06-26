package org.enigmatic_legacy.item.items.sword;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.items.material.EtheriumToolMaterial;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 以太阔剑 / Etherium Broadsword。
 * 原项目 ID：
 * enigmaticlegacy:etherium_sword
 * 当前项目 ID：
 * enigmatic_legacy:etherium_sword
 * 主动能力：
 * 右键向后跃退。
 * 原项目默认冷却：
 * EtheriumSwordCooldown = 40 ticks。
 * 原项目行为：
 * 副手持盾时不触发主动能力。
 */
public class EtheriumBroadsword extends SwordItem {
    private static final int ACTIVE_COOLDOWN_TICKS = 40;

    private static final double BACKSTEP_STRENGTH = 1.15D;
    private static final double BACKSTEP_UPWARD = 0.35D;

    public EtheriumBroadsword() {
        super(
                EtheriumToolMaterial.INSTANCE,
                new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .attributes(SwordItem.createAttributes(
                                EtheriumToolMaterial.INSTANCE,
                                6.0F,
                                -2.8F
                        ))
        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        /*
         * 原项目更新日志说明：
         * 副手拿盾时，以太阔剑主动能力不应触发。
         */
        if (player.getOffhandItem().is(Items.SHIELD)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            dashBackwards(player);
            player.getCooldowns().addCooldown(this, ACTIVE_COOLDOWN_TICKS);
        }

        player.playSound(SoundEvents.ENDER_DRAGON_FLAP, 0.75F, 1.35F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void dashBackwards(Player player) {
        var look = player.getLookAngle();

        double horizontalLength = Math.sqrt(look.x * look.x + look.z * look.z);

        if (horizontalLength < 1.0E-7D) {
            return;
        }

        double x = -look.x / horizontalLength * BACKSTEP_STRENGTH;
        double z = -look.z / horizontalLength * BACKSTEP_STRENGTH;

        player.setDeltaMovement(
                player.getDeltaMovement().add(x, BACKSTEP_UPWARD, z)
        );

        player.hurtMarked = true;
        player.resetFallDistance();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_sword.1"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_sword.2",
                SpellstoneTooltip.number(ACTIVE_COOLDOWN_TICKS / 20)
        ));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.etherium_sword.3")
                .withStyle(ChatFormatting.RED));
    }
}