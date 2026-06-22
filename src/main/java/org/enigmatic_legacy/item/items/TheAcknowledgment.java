package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

/**
 * 启示之证 / The Acknowledgment。
 * Plus 版行为：
 * 1. 作为 Patchouli 自定义手册物品；
 * 2. 右键打开 the_acknowledgment 手册；
 * 3. 可作为武器使用，攻击目标时点燃目标 4 秒；
 * 4. 可附魔，附魔值 24。
 */
public class TheAcknowledgment extends Item {
    public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_acknowledgment"
    );

    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_acknowledgment_attack_damage"
    );

    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_acknowledgment_attack_speed"
    );

    public TheAcknowledgment() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .attributes(createAttributes()));
    }

    /**
     * Plus 版数值：攻击伤害 3.5，攻击速度 -2.1。
     */
    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                ATTACK_DAMAGE_ID,
                                3.5D,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ATTACK_SPEED_ID,
                                -2.1D,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    /**
     * 当前是否打开的是启示之证手册。
     */
    public static boolean isOpen() {
        return BOOK_ID.equals(PatchouliAPI.get().getOpenBookGui());
    }

    /**
     * 获取 Patchouli 书本副标题。
     */
    public static @NotNull Component getSubtitle() {
        return PatchouliAPI.get().getSubtitle(BOOK_ID);
    }

    /**
     * 右键打开 Patchouli 手册。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            PatchouliAPI.get().openBookGUI(serverPlayer, BOOK_ID);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 命中敌人时点燃 4 秒。
     */
    @Override
    public boolean hurtEnemy(
            @NotNull ItemStack stack,
            @NotNull LivingEntity target,
            @NotNull LivingEntity attacker
    ) {
        target.igniteForSeconds(4);
        return super.hurtEnemy(stack, target, attacker);
    }

    /**
     * 可附魔。
     */
    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    /**
     * Plus 版附魔值：24。
     */
    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 24;
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
            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.the_acknowledgment.1"
            ).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.the_acknowledgment.2"
            ).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.the_acknowledgment.shift.1"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.the_acknowledgment.shift.2",
                "20%"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
    }
}