package org.enigmatic_legacy.item.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.CursedSufferingTooltip;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

/**
 * 无止之言 / The Infinitum。
 * <p>
 * 原项目定位：启示之证的深渊终极变体，既是手册入口，也是高阶武器。
 */
public class TheInfinitum extends Item {
    public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_acknowledgment"
    );

    public static final double ATTACK_DAMAGE = 15.0D;
    public static final double ATTACK_SPEED = -2.0D;
    public static final float BOSS_DAMAGE_BONUS_PERCENT = 200.0F;
    public static final float KNOCKBACK_BONUS_PERCENT = 200.0F;
    public static final float LIFESTEAL_PERCENT = 10.0F;
    public static final int UNDEAD_PROBABILITY_PERCENT = 85;

    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_infinitum_attack_damage"
    );

    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_infinitum_attack_speed"
    );

    public TheInfinitum() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
                .attributes(createAttributes()));
    }

    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                ATTACK_DAMAGE_ID,
                                ATTACK_DAMAGE,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ATTACK_SPEED_ID,
                                ATTACK_SPEED,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public static boolean isHeld(Player player) {
        return player.getMainHandItem().is(ModItems.THE_INFINITUM.get())
                || player.getOffhandItem().is(ModItems.THE_INFINITUM.get());
    }

    public static float getModelProperty(Player player) {
        if (AbyssalHeartHelper.isWorthy(player)) {
            return 1.0F;
        }

        if (CursedRingHelper.hasCursedRing(player)) {
            return 0.5F;
        }

        return 0.0F;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!AbyssalHeartHelper.isWorthy(player)) {
            return InteractionResultHolder.pass(stack);
        }

        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();

            if (!offhandStack.isEmpty()
                    && offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK) {
                return InteractionResultHolder.pass(stack);
            }
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PatchouliAPI.get().openBookGUI(serverPlayer, BOOK_ID);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean hurtEnemy(
            @NotNull ItemStack stack,
            @NotNull LivingEntity target,
            @NotNull LivingEntity attacker
    ) {
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 24;
    }

    @Override
    public boolean supportsEnchantment(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return true;
    }

    /**
     * 无止之言提示文本。
     * 修改内容：
     * 1. 普通介绍只提示按住 Shift；
     * 2. Shift 介绍最底部统一显示七咒折磨 99.5% 要求；
     * 3. 删除原本分散的 worthy_ones_only 提示。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.1"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null && CursedRingHelper.hasCursedRing(player)) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.3"));
            tooltip.add(SpellstoneTooltip.empty());
        }

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.the_infinitum.4",
                SpellstoneTooltip.percent("+200%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.the_infinitum.5",
                SpellstoneTooltip.percent("+200%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.the_infinitum.6",
                SpellstoneTooltip.percent("+10%")
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.7"));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.8"));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.the_infinitum.9",
                SpellstoneTooltip.percent("85%")
        ));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.the_infinitum.10"));

        // 最底部统一显示七咒折磨 99.5% 要求和当前百分比。
        CursedSufferingTooltip.appendTooltip(tooltip);
    }
}
