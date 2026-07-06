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
 * 血腥狩猎手册。
 *
 * <p>这是七咒体系下强化宠物进攻能力的手册。玩家将它放在快捷栏或古旧书袋中时，
 * 附近属于自己的宠物会获得额外伤害，并且可以从血战沙场之证、千咒卷轴等效果中继承部分增益。</p>
 *
 * <p>具体事件逻辑位于 {@code SanguinaryHandbookEvents}，本类只负责物品基础属性、
 * 持有检测、宠物判定和 Tooltip 展示。</p>
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
            tooltip.add(SpellstoneTooltip.holdShift());
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative(
                    "tooltip.enigmatic_legacy.cursed_ones_only"
            ));
            return;
        }

        String petDamageBonus = "+"
                + Math.round(ConfigCommon.GUIDEBOOKS.sanguinaryHandbookPetDamageMultiplier.get() * 100.0D)
                + "%";

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.1"
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.2",
                SpellstoneTooltip.percent(petDamageBonus)
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.3"
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.4"
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.sanguinary_handbook.5"
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.cursed_ones_only"
        ));
    }
}
