package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 永恒智慧卷轴 / Scroll of Ageless Wisdom。

 * 类型：秘术卷轴 scroll。

 * 效果：
 * 1. 可以存储大量经验值；
 * 2. Shift + 右键切换吸收 / 提取模式；
 * 3. 按卷轴快捷键启用 / 停用；
 * 4. 装备在 scroll 卷轴栏时，启用后自动执行当前模式；
 * 5. 启用时收集 16 格范围内的经验球。
 */
public class ScrollOfAgelessWisdom extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    private static final String STORED_XP_TAG = "storedXP";
    private static final String ACTIVE_TAG = "active";
    private static final String MODE_TAG = "mode";

    public static final int MODE_ABSORPTION = 0;
    public static final int MODE_EXTRACTION = 1;

    public static final double COLLECTION_RANGE = 16.0D;
    private static final int MIN_XP_PORTION = 5;

    public ScrollOfAgelessWisdom() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    /**
     * 普通右键可以直接装备到奥秘卷轴栏。
     * Shift + 右键时禁止 Curios 接管装备，
     * 因为 Shift + 右键用于切换吸收 / 提取模式。
     * 奥秘卷轴栏有 3 个，
     * 但永恒智慧卷轴最多只能装备 1 个。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        if (!isScrollSlot(context)) {
            return false;
        }

        LivingEntity entity = context.entity();

        if (entity instanceof Player player && player.isShiftKeyDown()) {
            return false;
        }

        return canEquipXpScroll(entity, stack);
    }

    /**
     * 只能放入 scroll 奥秘卷轴栏。
     * 奥秘卷轴栏可以有 3 个槽位，
     * 但永恒智慧卷轴最多只能装备 1 个。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!isScrollSlot(context)) {
            return false;
        }

        return canEquipXpScroll(context.entity(), stack);
    }

    /**
     * 判断当前 Curios 栏位是否为 scroll 奥秘卷轴栏。
     */
    private static boolean isScrollSlot(SlotContext context) {
        return context != null && SCROLL_SLOT.equals(context.identifier());
    }

    /**
     * 判断是否允许装备永恒智慧卷轴。
     * 规则：
     * 奥秘卷轴栏位可以有 3 个，
     * 但是永恒智慧卷轴本身最多只能装备 1 个。
     */
    private static boolean canEquipXpScroll(LivingEntity entity, ItemStack stack) {
        if (entity == null) {
            return true;
        }

        return CuriosLookupApi.findFirstSlot(entity,
                        equippedStack -> equippedStack.is(ModItems.XP_SCROLL.get()) && equippedStack != stack)
                .isEmpty();
    }

    /**
     * 卷轴装备在 Curios scroll 槽时，每 tick 执行。
     * 启用 + 吸收模式：
     * - 吸收玩家当前经验；
     * - 收集 16 格内经验球。
     * 启用 + 提取模式：
     * - 将全部存储经验返还给玩家；
     * - 自动停用。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!isActive(stack)) {
            return;
        }

        int experiencePortion = getExperiencePortion(player);

        if (getMode(stack) == MODE_EXTRACTION) {
            extractExperiencePortion(player, stack, experiencePortion);
            return;
        }

        absorbPlayerExperience(player, stack, experiencePortion);
        collectNearbyExperienceOrbs(player, stack);
    }

    /**
     * 手持右键逻辑。
     * 普通右键：
     * 返回 PASS，交给 Curios 处理，让卷轴直接装备到奥秘卷轴栏。
     * Shift + 右键：
     * 只切换吸收 / 提取模式，不进行装备。
     * 启用 / 停用：
     * 装备在奥秘卷轴栏后，使用绑定按键触发。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift + 右键：只切换模式，并阻止 Curios 继续装备。
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                toggleMode(player, stack);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        /*
         * 普通右键：
         * 必须返回 PASS。
         * 如果这里返回 sidedSuccess，右键事件会被物品自身消费，
         * Curios 就无法继续处理“右键装备”。
         */
        return InteractionResultHolder.pass(stack);
    }

    /**
     * 切换启用 / 停用。
     * 具体吸收或返还逻辑由 curioTick 按当前模式持续处理。
     */
    public static void toggleActive(Player player, ItemStack stack) {
        boolean newState = !isActive(stack);
        setActive(stack, newState);

        player.displayClientMessage(
                Component.translatable(
                        newState
                                ? "message.enigmatic_legacy.xp_scroll.enabled"
                                : "message.enigmatic_legacy.xp_scroll.disabled"
                ).withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED),
                true
        );

        playUseSound(player, newState ? 1.1F : 0.8F);
    }

    /**
     * 切换吸收 / 提取模式。
     */
    public static void toggleMode(Player player, ItemStack stack) {
        int nextMode = getMode(stack) == MODE_ABSORPTION ? MODE_EXTRACTION : MODE_ABSORPTION;
        setMode(stack, nextMode);

        player.displayClientMessage(
                Component.translatable(
                        nextMode == MODE_ABSORPTION
                                ? "message.enigmatic_legacy.xp_scroll.mode_absorption"
                                : "message.enigmatic_legacy.xp_scroll.mode_extraction"
                ).withStyle(ChatFormatting.GOLD),
                true
        );

        playUseSound(player, nextMode == MODE_ABSORPTION ? 1.0F : 1.25F);
    }

    /**
     * 按原项目逻辑，每 tick 只吸收当前等级经验条的一小份。
     */
    private static void absorbPlayerExperience(Player player, ItemStack stack, int experiencePortion) {
        int experience = ExperienceHelper.getPlayerXP(player);

        if (experience <= 0) {
            return;
        }

        int amount = Math.min(experiencePortion, experience);
        ExperienceHelper.drainPlayerXP(player, amount);
        addStoredExperience(stack, amount);
    }

    /**
     * 收集 16 格范围内经验球，并直接写入卷轴。
     */
    private static void collectNearbyExperienceOrbs(Player player, ItemStack stack) {
        Level level = player.level();
        AABB area = player.getBoundingBox().inflate(COLLECTION_RANGE);

        List<ExperienceOrb> orbs = level.getEntitiesOfClass(
                ExperienceOrb.class,
                area,
                orb -> orb.isAlive() && !orb.isRemoved()
        );

        if (orbs.isEmpty()) {
            return;
        }

        long collected = 0L;

        for (ExperienceOrb orb : orbs) {
            collected += orb.getValue();
            orb.discard();
        }

        if (collected > 0L) {
            addStoredExperience(stack, collected);
            playUseSound(player, 1.35F);
        }
    }

    /**
     * 按原项目逻辑，每 tick 只返还当前等级经验条的一小份。
     */
    private static void extractExperiencePortion(Player player, ItemStack stack, int experiencePortion) {
        long stored = getStoredExperience(stack);

        if (stored <= 0L) {
            return;
        }

        int amount = (int) Math.min(experiencePortion, stored);
        setStoredExperience(stack, stored - amount);
        ExperienceHelper.addPlayerXP(player, amount);
    }

    private static int getExperiencePortion(Player player) {
        int level = Math.max(0, player.experienceLevel);
        int levelExperience = getExperienceForLevel(level + 1) - getExperienceForLevel(level);
        int portion = levelExperience / 5;

        if (level > 100) {
            portion *= 1 + level / 100;
        }

        return Math.max(MIN_XP_PORTION, portion);
    }

    private static int getExperienceForLevel(int level) {
        if (level <= 0) {
            return 0;
        }

        if (level < 17) {
            return level * level + 6 * level;
        }

        if (level < 32) {
            return (int) (2.5D * level * level - 40.5D * level + 360.0D);
        }

        return (int) (4.5D * level * level - 162.5D * level + 2220.0D);
    }

    private static void playUseSound(Player player, float pitch) {
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                0.6F,
                pitch
        );
    }

    public static boolean isActive(ItemStack stack) {
        return getTag(stack).getBoolean(ACTIVE_TAG);
    }

    public static void setActive(ItemStack stack, boolean active) {
        CompoundTag tag = getTag(stack);
        tag.putBoolean(ACTIVE_TAG, active);
        setTag(stack, tag);
    }

    public static int getMode(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.contains(MODE_TAG) ? tag.getInt(MODE_TAG) : MODE_ABSORPTION;
    }

    public static void setMode(ItemStack stack, int mode) {
        CompoundTag tag = getTag(stack);
        tag.putInt(MODE_TAG, mode);
        setTag(stack, tag);
    }

    public static long getStoredExperience(ItemStack stack) {
        return Math.max(0L, getTag(stack).getLong(STORED_XP_TAG));
    }

    public static void setStoredExperience(ItemStack stack, long experience) {
        CompoundTag tag = getTag(stack);
        tag.putLong(STORED_XP_TAG, Math.max(0L, experience));
        setTag(stack, tag);
    }

    public static void addStoredExperience(ItemStack stack, long amount) {
        if (amount <= 0L) {
            return;
        }

        long current = getStoredExperience(stack);
        long next;

        /*
         * 防止 long 溢出。
         * 实际游戏里基本不可能存到这么大，但这里保持安全。
         */
        if (Long.MAX_VALUE - current < amount) {
            next = Long.MAX_VALUE;
        } else {
            next = current + amount;
        }

        setStoredExperience(stack, next);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        long stored = getStoredExperience(stack);
        boolean active = isActive(stack);
        int mode = getMode(stack);

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.xp_scroll.stored",
                SpellstoneTooltip.number(stored)
        ).withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable(
                active
                        ? "tooltip.enigmatic_legacy.xp_scroll.active"
                        : "tooltip.enigmatic_legacy.xp_scroll.inactive"
        ).withStyle(active ? ChatFormatting.DARK_PURPLE : ChatFormatting.RED));

        tooltip.add(Component.translatable(
                mode == MODE_ABSORPTION
                        ? "tooltip.enigmatic_legacy.xp_scroll.mode_absorption"
                        : "tooltip.enigmatic_legacy.xp_scroll.mode_extraction"
        ).withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.xp_scroll.usage.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.xp_scroll.usage.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.xp_scroll.usage.3", SpellstoneTooltip.number("16")));
    }
}
