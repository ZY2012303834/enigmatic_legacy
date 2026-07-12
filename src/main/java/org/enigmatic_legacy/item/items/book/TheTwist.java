package org.enigmatic_legacy.item.items.book;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

/**
 * 倒转之启 / The Twist
 * 原项目类名：
 * - TheTwist
 * 原项目定位：
 * - 七咒专属的启示之证变体；
 * - 既是 Patchouli 手册入口，也是武器；
 * - 命中目标时点燃目标；
 * - 修正七咒第四诅咒，使自身攻击始终造成全额伤害；
 * - 对 Boss 和玩家造成额外 +300% 伤害；
 * - 造成额外 +300% 击退。
 */
public class TheTwist extends AbstractBookItem {

    /**
     * 原项目 ID。
     */
    public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_acknowledgment"
    );

    /**
     * 原项目配置：
     * AttackDamage = 8
     * Minecraft 属性显示时会加上玩家基础 1 点，
     * 所以游戏内显示约为 9 点攻击伤害。
     */
    public static final double ATTACK_DAMAGE = 8.0D;

    /**
     * 原项目配置：
     * AttackSpeed = -1.8
     * 游戏内显示约为 2.2 攻击速度。
     */
    public static final double ATTACK_SPEED = -1.8D;

    /**
     * 原项目配置：
     * BossDamageBonus = 300%
     */
    public static final float BOSS_DAMAGE_BONUS_PERCENT = 300.0F;

    /**
     * 原项目配置：
     * KnockbackPowerBonus = 300%
     */
    public static final float KNOCKBACK_BONUS_PERCENT = 300.0F;

    /**
     * 属性 ID。
     */
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_twist_attack_damage"
    );

    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "the_twist_attack_speed"
    );

    public TheTwist() {
        super(new Properties()
                .rarity(Rarity.EPIC)
                .fireResistant()
                .attributes(createAttributes())
        );
    }

    /**
     * 判断玩家是否拥有一份可以提供被动效果的倒转之启。
     * <p>
     * 倒转之启继承自 AbstractBookItem，按当前项目的书类规则，
     * 放在快捷栏或放入玩家可访问的古旧书袋中都视为“携带”。
     * 这里故意不检查普通背包，避免普通背包里的备用书也自动生效。
     * <p>
     * 注意：
     * 这个方法只表示书类被动机制可以生效。
     * 主手攻击属性、命中点燃等物品自身攻击行为，
     * 仍然需要玩家实际用倒转之启攻击才会由物品方法触发。
     */
    public static boolean hasTheTwist(Player player) {
        return hasInHotbarOrBookBag(player, ModItems.THE_TWIST.get());
    }

    /**
     * 创建主手属性。
     */
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

    /**
     * 右键使用。
     * 原项目逻辑：
     * - 没有佩戴七咒之戒时，不允许打开；
     * - 主手使用时，如果副手已经是盾牌类格挡物，则不打开，避免阻止副手盾牌格挡；
     * - 佩戴七咒后右键打开 The Acknowledgment 手册。
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

        /*
         * 原项目修复过：
         * The Twist 在主手时，不应该阻止副手盾牌类物品格挡。
         */
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

    /**
     * 命中目标。
     * 复刻启示之证逻辑：
     * - 命中时点燃目标 4 秒。
     * 注意：
     * - 未佩戴七咒时的“造成 0 伤害”逻辑放在 TheTwistEvents 中；
     * - 这里仍然点燃目标，以复刻原项目“无七咒攻击仍可附火但无伤害”的表现。
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
     * 原项目附魔能力来自 The Acknowledgment：24。
     */
    @Override
    public int getEnchantmentValue() {
        return 24;
    }

    /**
     * 原项目限制：
     * - 虽然是书，但只能附节肢杀手；
     * - 不允许锋利、亡灵杀手等其它伤害类附魔。
     */
    @Override
    public boolean supportsEnchantment(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return enchantment.is(Enchantments.BANE_OF_ARTHROPODS);
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

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.the_twist.1")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.the_twist.2")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.the_twist.4")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.the_twist.5")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.the_twist.6",
                    Component.literal("+300%").withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.LIGHT_PURPLE));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.the_twist.7",
                    Component.literal("+300%").withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}
