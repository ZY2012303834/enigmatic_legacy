package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AntiqueBookBagHelper;
import org.enigmatic_legacy.util.PlayerInventoryHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 生灵颂词 / Ode to Living Beings。
 *
 * <p>复刻自 Enigmatic Addons 的 {@code ode_to_living}。它不是 Curios 饰品，
 * 而是放在玩家背包中即可生效的书类遗物。</p>
 *
 * <p>核心规则：
 * 1. 背包内存在时，玩家不会误伤大多数动物；
 * 2. 被保护的中立动物不会因为七咒之戒主动攻击玩家；
 * 3. 用颂词攻击动物会把该动物类型加入这本颂词的黑名单；
 * 4. 再次对该类动物右键使用颂词，可以移除黑名单并安抚目标。</p>
 */
public class OdeToLiving extends Item {
    public static final int COOLDOWN_TICKS = 1200;

    private static final String BANNED_MOB_LIST_TAG = "BannedMobList";

    public OdeToLiving() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 查找玩家背包中的第一本生灵颂词。
     *
     * <p>原项目支持从古董包内查找；当前项目还没有对应容器系统，
     * 因此这里先覆盖主手、副手和普通背包。</p>
     */
    public static Optional<ItemStack> findOde(Player player) {
        return PlayerInventoryHelper.findInHotbar(player, stack -> stack.is(ModItems.ODE_TO_LIVING.get()))
                .or(() -> AntiqueBookBagHelper.findBook(player, ModItems.ODE_TO_LIVING.get()));
    }

    public static boolean hasOde(Player player) {
        return findOde(player).isPresent();
    }

    public static boolean isHeldOde(ItemStack stack) {
        return stack.is(ModItems.ODE_TO_LIVING.get());
    }

    /**
     * 判断该生物是否属于颂词可保护的目标。
     *
     * <p>当前项目的兽友指南已经定义了基础保护范围；颂词在此基础上补充
     * 中立动物，使其能真正修正七咒之戒第二诅咒。</p>
     */
    public static boolean isOdeAnimal(LivingEntity entity) {
        return AnimalGuidebook.isProtectedAnimal(entity)
                || entity instanceof Animal
                || entity instanceof Hoglin;
    }

    /**
     * 判断目标是否正在被玩家背包中的生灵颂词保护。
     */
    public static boolean isProtectedByOde(Player player, LivingEntity entity) {
        if (!isOdeAnimal(entity)) {
            return false;
        }

        return findOde(player)
                .map(stack -> !containsInBannedList(stack, entity))
                .orElse(false);
    }

    public static boolean containsInBannedList(ItemStack stack, LivingEntity entity) {
        String entityId = getEntityId(entity);
        ListTag list = getBannedList(stack);
        return list.contains(StringTag.valueOf(entityId));
    }

    public static void addToBannedList(ItemStack stack, LivingEntity entity) {
        CompoundTag tag = copyTag(stack);
        ListTag list = tag.getList(BANNED_MOB_LIST_TAG, Tag.TAG_STRING);
        StringTag entityId = StringTag.valueOf(getEntityId(entity));

        if (!list.contains(entityId)) {
            list.add(entityId);
            tag.put(BANNED_MOB_LIST_TAG, list);
            setTag(stack, tag);
        }
    }

    public static boolean removeFromBannedList(ItemStack stack, LivingEntity entity) {
        CompoundTag tag = copyTag(stack);
        ListTag list = tag.getList(BANNED_MOB_LIST_TAG, Tag.TAG_STRING);
        StringTag entityId = StringTag.valueOf(getEntityId(entity));

        if (!list.remove(entityId)) {
            return false;
        }

        tag.put(BANNED_MOB_LIST_TAG, list);
        setTag(stack, tag);
        return true;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack stack,
            @NotNull Player player,
            @NotNull LivingEntity target,
            @NotNull InteractionHand hand
    ) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }

        boolean changed = removeFromBannedList(stack, target);
        boolean appeased = appeaseTarget(player, target);

        if (!changed && !appeased) {
            return InteractionResult.PASS;
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    target.getX(),
                    target.getEyeY(),
                    target.getZ(),
                    5,
                    target.getBbWidth(),
                    0.1D,
                    target.getBbWidth(),
                    0.1D
            );
        }

        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.GOLD));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.3")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.4")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.5")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.6")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.7")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static boolean appeaseTarget(Player player, LivingEntity target) {
        boolean appeased = false;

        if (target instanceof NeutralMob neutralMob && target instanceof Mob mob && mob.getTarget() == player) {
            neutralMob.stopBeingAngry();
            mob.setTarget(null);
            appeased = true;
        } else if (target instanceof Mob mob && mob.getTarget() == player) {
            mob.setTarget(null);
            appeased = true;
        }

        return appeased;
    }

    private static ListTag getBannedList(ItemStack stack) {
        return copyTag(stack).getList(BANNED_MOB_LIST_TAG, Tag.TAG_STRING);
    }

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return id.toString();
    }

    private static CompoundTag copyTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
