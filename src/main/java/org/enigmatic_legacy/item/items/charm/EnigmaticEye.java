package org.enigmatic_legacy.item.items.charm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.quote.Quote;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 休眠之眼 / Enigmatic Eye。
 * 休眠时不能装备。
 * 右键唤醒后变为全知之眼。
 * 唤醒后佩戴效果：
 * 1. +1 charm 护符栏位；
 * 2. +3 方块交互距离。
 */
public class EnigmaticEye extends Item implements ICurioItem {
    private static final String DORMANT_TAG = "IsDormant";
    private static final String ACTIVATION_ANIMATION_TAG = "ActivationAnimation";

    public static final String EXTRA_CHARM_SLOT = "charm";

    public static final ResourceLocation EXTRA_CHARM_SLOT_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "enigmatic_eye_extra_charm_slot"
    );

    public static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "enigmatic_eye_block_reach"
    );

    public static final double EXTRA_CHARM_SLOT_AMOUNT = 1.0D;
    public static final double BLOCK_REACH_AMOUNT = 3.0D;

    public EnigmaticEye() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 休眠状态默认 true。
     */
    public static boolean isDormant(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return true;
        }

        CompoundTag tag = data.copyTag();

        if (!tag.contains(DORMANT_TAG)) {
            return true;
        }

        return tag.getBoolean(DORMANT_TAG);
    }

    public static void setDormant(ItemStack stack, boolean dormant) {
        CompoundTag tag = copyCustomTag(stack);
        tag.putBoolean(DORMANT_TAG, dormant);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getActivationAnimation(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return -1;
        }

        CompoundTag tag = data.copyTag();

        if (!tag.contains(ACTIVATION_ANIMATION_TAG)) {
            return -1;
        }

        return tag.getInt(ACTIVATION_ANIMATION_TAG);
    }

    public static boolean hasActivationAnimation(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return false;
        }

        return data.copyTag().contains(ACTIVATION_ANIMATION_TAG);
    }

    public static void setActivationAnimation(ItemStack stack, int ticks) {
        CompoundTag tag = copyCustomTag(stack);
        tag.putInt(ACTIVATION_ANIMATION_TAG, ticks);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 客户端模型属性。
     * 0.0 = 休眠；
     * 0.4 = 唤醒动画第一帧；
     * 0.8 = 唤醒动画第二帧；
     * 1.0 = 已唤醒。
     */
    public static float getModelProperty(ItemStack stack) {
        if (!isDormant(stack)) {
            return 1.0F;
        }

        int animationTicks = getActivationAnimation(stack);

        if (animationTicks > -1) {
            return animationTicks > 2 ? 0.4F : 0.8F;
        }

        return 0.0F;
    }

    private static CompoundTag copyCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    /**
     * 休眠状态不能右键直接装备。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return !isDormant(stack);
    }

    /**
     * 休眠状态不能装备。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (isDormant(stack)) {
            return false;
        }

        return CuriosApi.getCuriosInventory(context.entity())
                .map(handler -> handler.findFirstCurio(otherStack ->
                        otherStack != stack
                                && otherStack.getItem() instanceof EnigmaticEye
                                && !isDormant(otherStack)
                ).isEmpty())
                .orElse(true);
    }

    /**
     * 根据状态显示不同名称：
     * 休眠之眼 / 全知之眼。
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(isDormant(stack)
                ? "item.enigmatic_legacy.enigmatic_eye_dormant"
                : "item.enigmatic_legacy.enigmatic_eye_active");
    }

    /**
     * 右键唤醒。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);


        if (isDormant(stack) && !hasActivationAnimation(stack)) {
            if (!level.isClientSide) {
                setActivationAnimation(stack, 4);

                level.playSound(
                        null,
                        player.blockPosition(),
                        ModSounds.CHARGED_ON.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );

                if (player instanceof ServerPlayer serverPlayer && !Quote.isNarratorUnlocked(serverPlayer)) {
                    Quote.unlockNarrator(serverPlayer);
                    Quote.random(Quote.NARRATOR_INTROS).play(serverPlayer, 80);
                }
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * 唤醒动画倒计时。
     */
    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slotId,
            boolean isSelected
    ) {
        int animationTicks = getActivationAnimation(stack);

        if (animationTicks > 0) {
            setActivationAnimation(stack, animationTicks - 1);
        } else if (animationTicks == 0) {
            setActivationAnimation(stack, -1);
            setDormant(stack, false);

            if (!level.isClientSide
                    && entity instanceof ServerPlayer serverPlayer
                    && !isDormant(stack)
                    && !Quote.isNarratorUnlocked(serverPlayer)) {
                Quote.unlockNarrator(serverPlayer);
                Quote.random(Quote.NARRATOR_INTROS).play(serverPlayer, 60);
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    /**
     * 唤醒后佩戴时：
     * 1. +1 charm 栏位；
     * 2. +3 方块交互距离。
     */
    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();

        if (isDormant(stack)) {
            return attributes;
        }

        if (slotContext.entity() instanceof Player) {
            CuriosApi.addSlotModifier(
                    attributes,
                    EXTRA_CHARM_SLOT,
                    EXTRA_CHARM_SLOT_ID,
                    EXTRA_CHARM_SLOT_AMOUNT,
                    AttributeModifier.Operation.ADD_VALUE
            );

            attributes.put(
                    Attributes.BLOCK_INTERACTION_RANGE,
                    new AttributeModifier(
                            BLOCK_REACH_ID,
                            BLOCK_REACH_AMOUNT,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        return attributes;
    }

    /**
     * 休眠之眼 / 全知之眼 tooltip。
     * 统一规则：
     * 1. 普通介绍文字为紫色；
     * 2. +1、+3 这类数字为金色；
     * 3. 按住 Shift 提示统一深灰色；
     * 4. 使用 List<Component>，避免原始 List 警告。
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
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        if (isDormant(stack)) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_eye.dormant.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_eye.dormant.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_eye.dormant.3"));
        } else {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_eye.active.1",
                    SpellstoneTooltip.number("+1")
            ));

            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_eye.active.2",
                    SpellstoneTooltip.number("+3")
            ));

            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_eye.active.3"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_eye.active.4"));
        }
    }
}
