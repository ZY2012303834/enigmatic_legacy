package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 烈焰之傲壁垒 / Bulwark of Blazing Pride
 * 原项目类名：
 * - InfernalShield
 * 按原项目复刻：
 * 1. 只有承受七咒之人才能使用；
 * 2. 作为盾牌使用，右键举盾；
 * 3. 耐久 10000；
 * 4. 史诗品质；
 * 5. 防火；
 * 6. 可用黑曜石修复；
 * 7. 可附魔，附魔能力 16；
 * 8. 玩家手持该盾时，如果着火且不在岩浆中，会自动熄灭火焰；
 * 9. 额外的背后受击 1.5 倍伤害逻辑放在 BulwarkOfBlazingPrideEvents 中。
 */
public class BulwarkOfBlazingPride extends ShieldItem {

    /**
     * 原项目耐久。
     */
    public static final int DURABILITY = 10000;

    /**
     * 原项目举盾持续时间。
     */
    public static final int USE_DURATION = 72000;

    /**
     * 原项目附魔能力。
     */
    public static final int ENCHANTMENT_VALUE = 16;

    public BulwarkOfBlazingPride() {
        super(new Properties()
                .stacksTo(1)
                .durability(DURABILITY)
                .rarity(Rarity.EPIC)
                .fireResistant()
        );
    }

    /**
     * 右键使用。
     * 原项目逻辑：
     * - 如果玩家是承受七咒之人，才允许开始举盾；
     * - 如果没有佩戴七咒之戒，右键不生效。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!CursedRingHelper.hasCursedRing(player)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 使用动画。
     * BLOCK 会让玩家进入举盾姿态。
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    /**
     * 举盾持续时间。
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return USE_DURATION;
    }

    /**
     * 背包 / 手持 tick。
     * 原项目逻辑：
     * - 只处理服务端玩家；
     * - 玩家必须着火；
     * - 玩家必须佩戴七咒之戒；
     * - 盾牌必须在主手或副手；
     * - 如果玩家不在岩浆里，就自动灭火。
     * 注意：
     * - 不在岩浆中才灭火，是为了避免和烈焰核心的岩浆热量机制产生无限免疫问题。
     */
    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slotId,
            boolean isSelected
    ) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) {
            return;
        }

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (!player.isOnFire()) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        boolean inMainHand = player.getMainHandItem() == stack;
        boolean inOffHand = player.getOffhandItem() == stack;

        if (!inMainHand && !inOffHand) {
            return;
        }

        if (!player.isInLava()) {
            player.clearFire();
        }
    }

    /**
     * 修复材料。
     * 原项目：
     * - 可以使用黑曜石修复。
     */
    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return repairCandidate.is(Items.OBSIDIAN) || super.isValidRepairItem(stack, repairCandidate);
    }

    /**
     * 是否可附魔。
     */
    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    /**
     * 附魔能力。
     */
    @Override
    public int getEnchantmentValue() {
        return ENCHANTMENT_VALUE;
    }

    /**
     * Tooltip。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.1")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.2")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.3")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.4")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.bulwark_of_blazing_pride.5")
                    .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}
