package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 修补混合物 / Mending Mixture
 * 复刻功能：
 * 1. 可以作为材料，在工作台中修复任意受损的可损坏物品。
 * 2. 修复合成由 MendingMixtureRepairRecipe 处理。
 * 3. 合成修复后会留下空玻璃瓶。
 * 4. 右键可以饮用，但会获得负面效果。
 * 注意：
 * - 它不是原版 PotionItem。
 * - 这样可以避免 JEI / 原版药水系统自动生成喷溅型、滞留型等派生药水。
 */
public class MendingMixture extends Item {

    /**
     * 饮用时长。
     * 原版药水通常也是 32 tick。
     */
    private static final int DRINK_DURATION = 32;

    public MendingMixture() {
        super(new Item.Properties()
                // 修补混合物是瓶装消耗品，允许堆叠。
                .stacksTo(16)
                // 稀有度设置为稀有。
                .rarity(Rarity.RARE)
        );
    }

    /**
     * 右键使用。
     * 这里让玩家开始饮用动画。
     * 真正效果在 finishUsingItem(...) 中处理。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 使用动画。
     * 使用 DRINK，让它像药水一样饮用。
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    /**
     * 使用时长。
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return DRINK_DURATION;
    }

    /**
     * 饮用结束后的效果。
     * 原作者说明：
     * - Don't try to drink it.
     * - You will not like the consequences.
     * 所以这里给一组负面效果。
     */
    @Override
    public @NotNull ItemStack finishUsingItem(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull LivingEntity livingEntity
    ) {
        // 只在服务端真正添加效果。
        if (!level.isClientSide()) {
            // 中毒：不会直接致死，但会持续扣血。
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));

            // 虚弱：降低攻击能力。
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 1));

            // 缓慢：降低移动速度。
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1));

            // 饥饿：玩家饮用时会快速消耗饥饿值。
            livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1));
        }

        /*
         * 消耗瓶装混合物，并返还空玻璃瓶。
         *
         * 创造模式玩家不消耗物品，也不返还空瓶。
         */
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);

            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

            // 如果这一瓶已经被喝完，直接把空瓶作为返回物。
            if (stack.isEmpty()) {
                return bottle;
            }

            // 如果当前堆叠还没空，就尝试把空瓶放进背包。
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }

        return stack;
    }

    /**
     * 物品提示文本。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.mending_mixture.1")
                .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.mending_mixture.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.mending_mixture.3")
                .withStyle(ChatFormatting.DARK_RED));
    }
}