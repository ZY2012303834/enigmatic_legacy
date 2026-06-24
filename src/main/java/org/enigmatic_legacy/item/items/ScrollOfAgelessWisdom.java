package org.enigmatic_legacy.item.items;

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
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 永恒智慧卷轴 / Scroll of Ageless Wisdom。

 * 类型：秘术卷轴 scroll。

 * 效果：
 * 1. 可以存储大量经验值；
 * 2. 右键切换吸收 / 提取模式；
 * 3. Shift + 右键启用 / 停用；
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

    public ScrollOfAgelessWisdom() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    /**
     * 普通右键可以直接装备到奥秘卷轴栏。
     * 但是 Shift + 右键时必须禁止装备，
     * 因为 Shift + 右键用于切换吸收 / 提取模式。
     * 同时，永恒智慧卷轴最多只能装备 1 个。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        if (!isScrollSlot(context)) {
            return false;
        }

        LivingEntity entity = context.entity();

        // Shift + 右键时，不允许 Curios 接管装备。
        if (entity instanceof Player player && player.isShiftKeyDown()) {
            return false;
        }

        // 奥秘卷轴栏有 3 个，但永恒智慧卷轴自身只能装备 1 个。
        return !hasAnotherXpScrollEquipped(entity);
    }

    /**
     * 限制只能放进 scroll 奥秘卷轴栏。
     * 奥秘卷轴栏可以有 3 个槽位，
     * 但永恒智慧卷轴这个物品最多只能装备 1 个。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!isScrollSlot(context)) {
            return false;
        }

        return !hasAnotherXpScrollEquipped(context.entity());
    }

    /**
     * 判断当前 Curios 栏位是否为 scroll 奥秘卷轴栏。
     */
    private static boolean isScrollSlot(SlotContext context) {
        return context != null && SCROLL_SLOT.equals(context.identifier());
    }

    /**
     * 检查玩家 Curios 里是否已经装备了永恒智慧卷轴。
     * 用途：
     * 奥秘卷轴栏位可以是 3 个，
     * 但永恒智慧卷轴最多只能占其中 1 个。
     */
    private static boolean hasAnotherXpScrollEquipped(LivingEntity entity) {
        if (entity == null) {
            return true;
        }

        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.XP_SCROLL.get())
                ))
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

        if (getMode(stack) == MODE_EXTRACTION) {
            extractAllExperience(player, stack);
            setActive(stack, false);
            playUseSound(player, 1.15F);
            return;
        }

        absorbPlayerExperience(player, stack);
        collectNearbyExperienceOrbs(player, stack);
    }

    /**
     * 手持右键逻辑。

     * 普通右键：
     * 返回 PASS，交给 Curios 处理，让卷轴直接装备到奥秘卷轴栏。

     * Shift + 右键：
     * 只切换吸收 / 提取模式，不进行装备。

     * 启用 / 停用：
     * 装备在奥秘卷轴栏后，使用 Shift + 绑定按键触发。
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
         * 返回 PASS，交给 Curios 的 canEquipFromUse(...) 处理右键装备。
         */
        return InteractionResultHolder.pass(stack);
    }

    /**
     * 切换启用 / 停用。

     * 如果当前是提取模式，启用时会立即返还全部经验并自动停用。
     */
    public static void toggleActive(Player player, ItemStack stack) {
        boolean newState = !isActive(stack);
        setActive(stack, newState);

        if (newState && getMode(stack) == MODE_EXTRACTION) {
            extractAllExperience(player, stack);
            setActive(stack, false);

            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.xp_scroll.extracted", getStoredExperience(stack))
                            .withStyle(ChatFormatting.AQUA),
                    true
            );

            playUseSound(player, 1.2F);
            return;
        }

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
     * 吸收玩家当前经验条内的全部经验。
     */
    private static void absorbPlayerExperience(Player player, ItemStack stack) {
        int experience = ExperienceHelper.getPlayerXP(player);

        if (experience <= 0) {
            return;
        }

        ExperienceHelper.drainPlayerXP(player, experience);
        addStoredExperience(stack, experience);
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
     * 将卷轴中的全部经验返还给玩家。
     */
    private static void extractAllExperience(Player player, ItemStack stack) {
        long stored = getStoredExperience(stack);

        if (stored <= 0L) {
            return;
        }

        long remaining = stored;

        /*
         * giveExperiencePoints 接收 int。
         * 这里分批返还，避免一次性转换溢出。
         */
        while (remaining > 0L) {
            int amount = (int) Math.min(1_000_000L, remaining);
            ExperienceHelper.addPlayerXP(player, amount);
            remaining -= amount;
        }

        setStoredExperience(stack, 0L);
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

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.xp_scroll.stored",
                stored
        ).withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable(
                active
                        ? "tooltip.enigmatic_legacy.xp_scroll.active"
                        : "tooltip.enigmatic_legacy.xp_scroll.inactive"
        ).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED));

        tooltip.add(Component.translatable(
                mode == MODE_ABSORPTION
                        ? "tooltip.enigmatic_legacy.xp_scroll.mode_absorption"
                        : "tooltip.enigmatic_legacy.xp_scroll.mode_extraction"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.xp_scroll.usage.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.xp_scroll.usage.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.xp_scroll.usage.3", "16")
                .withStyle(ChatFormatting.GRAY));
    }
}