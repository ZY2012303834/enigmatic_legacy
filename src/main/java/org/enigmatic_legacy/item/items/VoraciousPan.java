package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 饕餮之锅 / The Voracious Pan
 * 原项目类名：
 * - EldritchPan
 * 按原项目复刻：
 * 1. 只有承受七咒之人才能真正使用；
 * 2. 基础攻击伤害：31；
 * 3. 基础攻击速度：-3.2；
 * 4. 基础护甲：4；
 * 5. 每击杀一种新的生物，攻击伤害 +0.5，护甲 +0.5；
 * 6. 最多统计 100 种不同生物；
 * 7. 命中造成伤害时吸血 15%；
 * 8. 命中造成伤害时夺取饥饿值 2；
 * 9. 主手持有时会逐渐施加饥饿效果；
 * 10. 可以像盾牌一样右键格挡；
 * 11. 可附魔，附魔能力 24；
 * 12. 可用多种食物修复。
 * 说明：
 * - 原项目在 1.20 使用 NBT 存储 PanUniqueKills。
 * - 这里适配 1.21.1，使用 DataComponents.CUSTOM_DATA 存储。
 * - 原项目通过动态 AttributeModifier 返回属性。
 * - 这里适配 1.21.1，直接刷新 DataComponents.ATTRIBUTE_MODIFIERS。
 */
public class VoraciousPan extends ShieldItem {

    /**
     * 原项目基础攻击伤害。
     */
    public static final double BASE_ATTACK_DAMAGE = 31.0D;

    /**
     * 原项目基础攻击速度。
     */
    public static final double BASE_ATTACK_SPEED = -3.2D;

    /**
     * 原项目基础护甲。
     */
    public static final double BASE_ARMOR = 4.0D;

    /**
     * 原项目吸血比例。
     * 0.15 = 15%
     */
    public static final double LIFE_STEAL = 0.15D;

    /**
     * 原项目夺饥数值。
     */
    public static final double HUNGER_STEAL = 2.0D;

    /**
     * 每种独特生物击杀提供的攻击伤害加成。
     */
    public static final double UNIQUE_DAMAGE_GAIN = 0.5D;

    /**
     * 每种独特生物击杀提供的护甲加成。
     */
    public static final double UNIQUE_ARMOR_GAIN = 0.5D;

    /**
     * 原项目实际最多只统计 100 种独特生物。
     */
    public static final int UNIQUE_GAIN_LIMIT = 100;

    /**
     * 原项目耐久。
     */
    public static final int DURABILITY = 4000;

    /**
     * 原项目附魔能力。
     */
    public static final int ENCHANTMENT_VALUE = 24;

    /**
     * 独特击杀列表 tag。
     */
    private static final String PAN_UNIQUE_KILLS_TAG = "PanUniqueKills";

    /**
     * 攻击伤害属性 ID。
     */
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "voracious_pan_attack_damage"
    );

    /**
     * 攻击速度属性 ID。
     */
    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "voracious_pan_attack_speed"
    );

    /**
     * 护甲属性 ID。
     */
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "voracious_pan_armor"
    );

    /**
     * 玩家主手持有饕餮之锅的持续时间。
     * 原项目这个 Map 是 public static，
     * 由事件处理器负责增长和清零。
     */
    public static final Map<Player, Integer> HOLDING_DURATIONS = new WeakHashMap<>();

    public VoraciousPan() {
        super(new Properties()
                .stacksTo(1)
                .durability(DURABILITY)
                .rarity(Rarity.EPIC)
                .fireResistant()
                .attributes(createAttributeModifiers(0))
        );
    }

    /**
     * 右键使用。
     * 原项目逻辑：
     * - 只有承受七咒之人才能举起饕餮之锅；
     * - 如果主手使用，而副手已经有盾牌类格挡物，则不触发；
     * - 成功时进入 BLOCK 使用动画。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        /*
         * 如果玩家没有佩戴七咒之戒，不允许使用。
         */
        if (!CursedRingHelper.hasCursedRing(player)) {
            return InteractionResultHolder.pass(stack);
        }

        /*
         * 原项目逻辑：
         * 主手使用时，如果副手已经是格挡类物品，不触发锅的格挡。
         */
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();

            if (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK) {
                return InteractionResultHolder.pass(stack);
            }
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 使用动画。
     * BLOCK 会让它像盾牌一样格挡。
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    /**
     * 使用持续时间。
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    /**
     * 饕餮之锅命中目标。
     * 完整复刻原项目攻击音效：
     * - 99.99% 概率播放 misc.pan_clang；
     * - 0.01% 概率播放 misc.pan_clang_fr 彩蛋音效；
     * - 普通音效 pitch 为 0.9 ~ 1.0；
     * - 彩蛋音效 pitch 为 0.8 ~ 1.0；
     * - 命中后消耗 1 点耐久。
     */
    @Override
    public boolean hurtEnemy(
            @NotNull ItemStack stack,
            @NotNull LivingEntity target,
            @NotNull LivingEntity attacker
    ) {
        if (!attacker.level().isClientSide()) {
            if (attacker.getRandom().nextDouble() < 0.0001D) {
                attacker.level().playSound(
                        null,
                        attacker.getX(),
                        attacker.getY(),
                        attacker.getZ(),
                        ModSounds.PAN_CLANG_FR.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        attacker.level().random.nextFloat() * 0.2F + 0.8F
                );
            } else {
                attacker.level().playSound(
                        null,
                        attacker.getX(),
                        attacker.getY(),
                        attacker.getZ(),
                        ModSounds.PAN_CLANG.get(),
                        SoundSource.PLAYERS,
                        0.5F,
                        attacker.level().random.nextFloat() * 0.1F + 0.9F
                );
            }
        }

        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    /**
     * 挖方块时损耗耐久。
     * 原项目逻辑：
     * - 如果方块硬度不为 0，消耗 2 点耐久。
     */
    @Override
    public boolean mineBlock(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull BlockState state,
            @NotNull net.minecraft.core.BlockPos pos,
            @NotNull LivingEntity entity
    ) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(2, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        }

        return true;
    }

    /**
     * 背包 tick。
     * 原项目 EldritchPan 本身 inventoryTick 是 NO-OP。
     * 这里仅保留动态属性刷新：
     * - 独特击杀数量变化后；
     * - 需要刷新攻击伤害和护甲组件。
     * Growing Hunger / Bloodlust 放在 VoraciousPanEvents 里处理。
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

        refreshAttributeModifiers(stack);
    }

    /**
     * 修复材料。
     * 原项目修复材料：
     * - 牛肉
     * - 猪排
     * - 羊肉
     * - 腐肉
     * - 苹果
     * - 金苹果
     * - 附魔金苹果
     * - 毒马铃薯
     */
    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return repairCandidate.is(Items.BEEF)
                || repairCandidate.is(Items.PORKCHOP)
                || repairCandidate.is(Items.MUTTON)
                || repairCandidate.is(Items.ROTTEN_FLESH)
                || repairCandidate.is(Items.APPLE)
                || repairCandidate.is(Items.GOLDEN_APPLE)
                || repairCandidate.is(Items.ENCHANTED_GOLDEN_APPLE)
                || repairCandidate.is(Items.POISONOUS_POTATO)
                || super.isValidRepairItem(stack, repairCandidate);
    }

    /**
     * 可附魔。
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
        int kills = getKillCount(stack);

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.lore")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            writeKillCountTooltip(tooltip, kills);

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                    .withStyle(ChatFormatting.RED));

            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.1")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.voracious_pan.lifesteal",
                Component.literal("+15%").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.voracious_pan.hungersteal",
                Component.literal("+2").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.3")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.4")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.voracious_pan.damage_gain",
                Component.literal("+0.5").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.voracious_pan.armor_gain",
                Component.literal("+0.5").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        writeKillCountTooltip(tooltip, kills);

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }

    /**
     * 写入击杀统计 tooltip。
     */
    private static void writeKillCountTooltip(List<Component> tooltip, int kills) {
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.voracious_pan.kills",
                Component.literal(Integer.toString(kills)).withStyle(ChatFormatting.GOLD),
                Component.literal(Integer.toString(UNIQUE_GAIN_LIMIT)).withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        if (kills >= UNIQUE_GAIN_LIMIT) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.voracious_pan.kills_max")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    /**
     * 获取独特击杀数量。
     */
    public static int getKillCount(ItemStack pan) {
        return getUniqueKills(pan).size();
    }

    /**
     * 获取独特击杀列表。
     */
    public static List<ResourceLocation> getUniqueKills(ItemStack pan) {
        CustomData data = pan.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return List.of();
        }

        CompoundTag tag = data.copyTag();

        if (!tag.contains(PAN_UNIQUE_KILLS_TAG, Tag.TAG_LIST)) {
            return List.of();
        }

        ListTag list = tag.getList(PAN_UNIQUE_KILLS_TAG, Tag.TAG_STRING);
        List<ResourceLocation> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));

            if (id != null) {
                result.add(id);
            }
        }

        return result;
    }

    /**
     * 添加一个独特击杀。
     * 返回：
     * - true：成功添加新的生物类型；
     * - false：已经存在，或者达到 100 上限。
     */
    public static boolean addKillIfNotPresent(ItemStack pan, ResourceLocation mobId) {
        if (mobId == null) {
            return false;
        }

        List<ResourceLocation> kills = getUniqueKills(pan);

        if (kills.size() >= UNIQUE_GAIN_LIMIT) {
            return false;
        }

        if (kills.contains(mobId)) {
            return false;
        }

        CompoundTag tag = copyCustomTag(pan);
        ListTag list;

        if (tag.contains(PAN_UNIQUE_KILLS_TAG, Tag.TAG_LIST)) {
            list = tag.getList(PAN_UNIQUE_KILLS_TAG, Tag.TAG_STRING);
        } else {
            list = new ListTag();
        }

        list.add(StringTag.valueOf(mobId.toString()));
        tag.put(PAN_UNIQUE_KILLS_TAG, list);

        pan.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        /*
         * 新击杀会影响动态属性，所以立刻刷新。
         */
        refreshAttributeModifiers(pan);

        return true;
    }

    /**
     * 刷新饕餮之锅属性组件。
     * 1.21.1 中物品属性来自 DataComponents.ATTRIBUTE_MODIFIERS。
     * 因此击杀数量变化后，需要把新的攻击/护甲数值写回组件。
     */
    public static void refreshAttributeModifiers(ItemStack pan) {
        pan.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(getKillCount(pan)));
    }

    /**
     * 创建属性组件。
     * 主手：
     * - 攻击伤害
     * - 攻击速度
     * - 护甲
     * 副手：
     * - 护甲
     */
    private static ItemAttributeModifiers createAttributeModifiers(int killCount) {
        int cappedKills = Math.min(killCount, UNIQUE_GAIN_LIMIT);

        double damage = BASE_ATTACK_DAMAGE + UNIQUE_DAMAGE_GAIN * cappedKills;
        double armor = BASE_ARMOR + UNIQUE_ARMOR_GAIN * cappedKills;

        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                ATTACK_DAMAGE_ID,
                                damage,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ATTACK_SPEED_ID,
                                BASE_ATTACK_SPEED,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                ARMOR_ID,
                                armor,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                ARMOR_ID,
                                armor,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.OFFHAND
                )
                .build();
    }

    /**
     * 复制 CUSTOM_DATA。
     */
    private static CompoundTag copyCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    /**
     * 处理吸血。
     */
    public static void applyLifeSteal(Player attacker, float damage) {
        if (damage <= 0.0F) {
            return;
        }

        attacker.heal((float) (damage * LIFE_STEAL));
    }

    /**
     * 处理夺饥。
     * 原项目逻辑：
     * - 如果攻击者仍然会饥饿：
     *   - 从玩家目标身上偷饱食度；
     *   - 或者从非玩家目标处获得饱食度；
     * - 如果攻击者不会饥饿，例如吃过禁忌之果：
     *   - 夺饥改为回血；
     *   - 玩家目标：回血 = 偷到的食物值 / 2；
     *   - 非玩家目标：回血 = hungerSteal / 2。
     */
    public static void applyHungerSteal(Player attacker, LivingEntity target) {
        boolean noHunger = ForbiddenFruit.hasConsumedFruit(attacker);

        if (target instanceof Player victim) {
            FoodData victimFood = victim.getFoodData();

            int foodSteal = Math.min(
                    (int) Math.ceil(HUNGER_STEAL),
                    victimFood.getFoodLevel()
            );

            float saturationSteal = Math.min(
                    (float) (HUNGER_STEAL / 5.0D),
                    victimFood.getSaturationLevel()
            );

            victimFood.setFoodLevel(victimFood.getFoodLevel() - foodSteal);
            victimFood.setSaturation(victimFood.getSaturationLevel() - saturationSteal);

            if (noHunger) {
                attacker.heal(foodSteal / 2.0F);
            } else {
                attacker.getFoodData().eat(foodSteal, saturationSteal);
            }

            return;
        }

        if (noHunger) {
            attacker.heal((float) (HUNGER_STEAL / 2.0D));
        } else {
            attacker.getFoodData().eat(
                    (int) Math.ceil(HUNGER_STEAL),
                    (float) (HUNGER_STEAL / 5.0D)
            );
        }
    }

    /**
     * 判断一个物品是否是饕餮之锅。
     */
    public static boolean isPan(ItemStack stack) {
        return stack.getItem() instanceof VoraciousPan;
    }

    /**
     * 获取实体类型 ID。
     */
    public static ResourceLocation getEntityTypeId(LivingEntity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }
}
