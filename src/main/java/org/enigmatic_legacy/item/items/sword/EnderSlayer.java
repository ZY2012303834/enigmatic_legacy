package org.enigmatic_legacy.item.items.sword;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.material.EnderSlayerToolMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 末影之屠 / The Ender Slayer
 * 按原项目复刻：
 * - 只有承受七咒之人才能真正使用；
 * - 基础攻击伤害配置值：4；
 * - 攻击速度：-2.6；
 * - 耐久：2000；
 * - 史诗品质；
 * - 防火；
 * - 对末地生物造成额外 +150% 伤害；
 * - 可以短暂压制末影人、潜影贝、玩家的传送能力。
 */
public class EnderSlayer extends SwordItem {

    /**
     * 原项目默认攻击伤害配置值。
     * 原项目说明：
     * 实际显示攻击伤害约为 4 + 此值。
     */
    public static final float DEFAULT_ATTACK_DAMAGE = 4.0F;

    /**
     * 原项目默认攻击速度。
     */
    public static final float ATTACK_SPEED = -2.6F;

    /**
     * 对末地生物的伤害加成。
     * 150% = 1.5
     * 最终伤害 = 原伤害 * 2.5
     */
    public static final float END_DAMAGE_BONUS = 1.50F;

    /**
     * 与原版 SwordItem 相同的属性 ID。
     * 物品栈刷新属性时使用相同 ID 覆盖默认攻击伤害/攻击速度，避免重复叠加。
     */
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            "minecraft",
            "base_attack_damage"
    );

    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            "minecraft",
            "base_attack_speed"
    );

    /**
     * 传送压制时间。
     * 原项目使用 400 ticks。
     * 400 ticks = 20 秒。
     */
    public static final int TELEPORT_BLOCK_TICKS = 400;

    /**
     * 额外末地生物 ID 列表。
     * 原项目允许通过配置额外添加“末地生物”。
     * 当前先保留这个集合，后续你要做配置时可以直接接入。
     */
    private static final Set<ResourceLocation> EXTRA_END_DWELLERS = new HashSet<>();

    public EnderSlayer() {
        super(
                EnderSlayerToolMaterial.INSTANCE,
                new Properties()
                        .stacksTo(1)
                        .rarity(Rarity.EPIC)
                        .fireResistant()
                        .durability(2000)
                        .attributes(SwordItem.createAttributes(
                                EnderSlayerToolMaterial.INSTANCE,
                                DEFAULT_ATTACK_DAMAGE,
                                ATTACK_SPEED
                        ))
        );
    }

    /**
     * 读取末影之屠的基础攻击伤害配置。
     * <p>
     * 物品注册阶段不能读取 COMMON 配置，因此构造器只写入默认属性。
     * 游戏运行后由 inventoryTick 将物品栈属性刷新为当前配置值。
     */
    private static float getConfiguredAttackDamage() {
        try {
            return ConfigCommon.ENDER_SLAYER_ATTACK_DAMAGE.get().floatValue();
        } catch (IllegalStateException exception) {
            return DEFAULT_ATTACK_DAMAGE;
        }
    }

    /**
     * 刷新末影之屠物品栈上的属性组件。
     * <p>
     * 构造器阶段只能写入默认属性，因为 COMMON 配置当时可能还没有加载完成。
     * 游戏运行后配置已经可读，再把当前配置值写入物品栈自己的 ATTRIBUTE_MODIFIERS 组件，
     * 让实际面板和攻击结算使用配置后的基础攻击伤害。
     */
    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slotId,
            boolean isSelected
    ) {
        ItemAttributeModifiers configuredAttributes = createConfiguredAttributes();

        if (!configuredAttributes.equals(stack.get(DataComponents.ATTRIBUTE_MODIFIERS))) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, configuredAttributes);
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    /**
     * 根据当前配置创建末影之屠的主手属性。
     * <p>
     * 使用与原版 SwordItem 相同的两个属性 ID，确保覆盖默认攻击伤害/攻击速度组件，
     * 而不是在旧组件之外叠加一组额外属性。
     */
    private static ItemAttributeModifiers createConfiguredAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                ATTACK_DAMAGE_ID,
                                getConfiguredAttackDamage(),
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
     * 末影之屠的附魔能力。
     * 1.21.1 没有 Item.Properties#enchantable(...)，
     * 所以这里继续使用 1.21.1 的 getEnchantmentValue 方式。
     */
    @Override
    public int getEnchantmentValue() {
        return EnderSlayerToolMaterial.INSTANCE.getEnchantmentValue();
    }

    /**
     * 判断目标是否属于“末地生物”。
     * 原项目默认包含：
     * - 末影人
     * - 末影龙
     * - 潜影贝
     * - 末影螨
     */
    public static boolean isEndDweller(LivingEntity entity) {
        if (entity instanceof EnderMan) {
            return true;
        }

        if (entity instanceof EnderDragon) {
            return true;
        }

        if (entity instanceof Shulker) {
            return true;
        }

        if (entity instanceof Endermite) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return id != null && EXTRA_END_DWELLERS.contains(id);
    }

    /**
     * 物品提示文本。
     *
     * 原项目风格：
     * - 默认只显示概述；
     * - 按住 Shift 显示完整说明；
     * - 最后显示“仅承受七咒之人可用”。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.1")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.2")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.empty());

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.ender_slayer.3",
                    Component.literal("+150%").withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.5")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.6")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.7")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.empty());

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ender_slayer.8")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }

        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}
