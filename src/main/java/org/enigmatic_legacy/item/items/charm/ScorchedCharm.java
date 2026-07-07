package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 阳灼护符 / Charm of Scorched Sun。
 * 复刻自 Enigmatic Addons 的 ScorchedCharm。
 * 定位：
 * 1. Curios 护符栏物品；
 * 2. 提供火焰、岩浆、生存、生命汲取相关能力；
 * 3. 可以在岩浆上行走；
 * 4. 下蹲时允许潜入岩浆。
 */
public class ScorchedCharm extends Item implements ICurioItem {

    public ScorchedCharm() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
        );
    }

    /**
     * 只允许通过右键装备到 charm 护符栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 只允许放入 charm 护符栏。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        if (entity.level().isClientSide()) {
            entity.getPersistentData().putInt(CLIENT_TICK_TAG, entity.tickCount);
        }

        if (entity.isOnFire()) {
            entity.clearFire();
        }

        if (!entity.isInLava()) {
            return;
        }

        if (!entity.level().isClientSide() && entity.tickCount % 20 == 0) {
            entity.heal(LAVA_HEAL_AMOUNT);
        }

        if (entity instanceof Player player && !player.isAffectedByFluids()) {
            return;
        }

        CollisionContext collisionContext = CollisionContext.of(entity);
        if (collisionContext.isAbove(LiquidBlock.STABLE_SHAPE, entity.blockPosition(), true)
                && !entity.level().getFluidState(entity.blockPosition().above()).is(FluidTags.LAVA)) {
            entity.setOnGround(true);
        } else {
            if (isLavaSwimming(entity)) {
                return;
            }

            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, entity.isCrouching() ? -0.01D : 0.07D, 0.0D));
        }
    }

    public static final String CLIENT_TICK_TAG = "enigmatic_legacy.scorched_charm_client_tick";
    private static final float LAVA_HEAL_AMOUNT = 2.0F;

    private static boolean isLavaSwimming(LivingEntity entity) {
        return entity.isSprinting()
                && entity.isEyeInFluid(FluidTags.LAVA)
                && !entity.isPassenger();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.scorched_charm.1"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.scorched_charm.2",
                SpellstoneTooltip.number("2")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.scorched_charm.3",
                SpellstoneTooltip.percent("20%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.scorched_charm.4",
                SpellstoneTooltip.percent("10%"),
                SpellstoneTooltip.percent("20%")
        ));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.scorched_charm.5"));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.scorched_charm.6"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.scorched_charm.7",
                SpellstoneTooltip.percent("30%")
        ));

        tooltip.add(SpellstoneTooltip.empty());
    }
}
