package org.enigmatic_legacy.item.items.book;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 血腥狩猎手册 / Sanguinary Hunting Handbook。
 *
 * <p>七咒专属追随者手册。放在快捷栏或古旧书袋中时，强化主人附近宠物造成的伤害，
 * 并让宠物继承血战沙场之证与千咒卷轴的部分暴力增幅。</p>
 */
public class SanguinaryHandbook extends AbstractBookItem {
    public static final ResourceLocation PET_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "sanguinary_handbook_pet_movement_speed"
    );

    public SanguinaryHandbook() {
        super(Rarity.RARE);
    }

    public static boolean hasHandbook(Player player) {
        return hasInHotbarOrBookBag(player, ModItems.SANGUINARY_HANDBOOK.get());
    }

    public static boolean isOwnedPet(OwnableEntity ownable) {
        return !(ownable instanceof TamableAnimal tamable && !tamable.isTame());
    }

    public static AttributeModifier createPetMovementSpeedModifier(double ownerMissingHealthRatio) {
        double amount = ownerMissingHealthRatio
                * ConfigCommon.BLOODSTAINED_VALOR_MOVEMENT_SPEED.get()
                * 1.25D;

        return new AttributeModifier(
                PET_MOVEMENT_SPEED_ID,
                amount,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift()); // 按住 Shift 查看详情。
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative(
                    "tooltip.enigmatic_legacy.cursed_ones_only" // 唯有背负诅咒者方能理解它的用途。
            ));
            return;
        }

        String petDamageBonus = "+"
                + Math.round(ConfigCommon.GUIDEBOOKS.sanguinaryHandbookPetDamageMultiplier.get() * 100.0D)
                + "%";

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.1" // 一本属于七咒承受者的血染手册。
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.2", // 附近属于你的宠物造成的伤害提高 %s。
                SpellstoneTooltip.percent(petDamageBonus)
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.3" // 你的受伤状态会激发宠物更强的凶性。
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.4" // 它们会继承血战沙场之证与千咒卷轴的部分暴力增幅。
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.5" // 放在快捷栏或古旧书袋中时生效。
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.cursed_ones_only" // 唯有背负诅咒者方能理解它的用途。
        ));
    }
}