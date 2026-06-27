package org.enigmatic_legacy.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.AmuletVariant;
import org.enigmatic_legacy.item.items.charm.EldritchAmulet;
import org.enigmatic_legacy.item.items.charm.EnigmaticAmulet;
import org.enigmatic_legacy.item.items.charm.UnwitnessedAmulet;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 神秘护身符事件处理。
 * 注意：
 * 1. 本类只负责护符本身的装备限制和七色效果。
 * 2. 超维容器 / 灵魂水晶的死亡生成逻辑已经移动到 SoulCrystalEvents，
 * 并且现在只由七咒之戒触发，护符不再参与死亡掉落保存。
 */
public final class EnigmaticAmuletEvents {
    /**
     * 记录玩家是否已经领取过无主护身符。
     * 这个数据写进 Player PersistentData，避免每次登录都重复发放。
     */
    private static final String STARTER_KEY =
            EnigmaticLegacy.MODID + "_received_unwitnessed_amulet";

    /**
     * 红色护符：+2 攻击伤害。
     */
    private static final double ATTACK_DAMAGE = 2.0D;

    private static final double ELDRITCH_ATTACK_DAMAGE = EldritchAmulet.ATTACK_DAMAGE;

    /**
     * 青色护符：疾跑时 +15% 移动速度。
     */
    private static final double SPRINTING_SPEED = 0.15D;

    /**
     * 紫色护符：15% 概率偏转来袭弹射物。
     */
    private static final float PROJECTILE_DEFLECT_CHANCE = 0.15F;

    /**
     * 品红护符：-20% 重力。
     * 这部分只负责“下落更慢 / 滞空更久”。
     * 注意：降低 GRAVITY 本身不一定会让玩家跳得更高，
     * 所以跳高效果另外在 LivingJumpEvent 里处理。
     */
    private static final double GRAVITY_REDUCTION = -0.20D;
    /**
     * 品红护符：额外跳跃高度。
     * 原版玩家跳跃初速度大约是 0.42。
     * 这里额外增加 0.12，体感是明显跳高，但不会夸张到像跳跃提升 II。
     */
    private static final double JUMP_HEIGHT_BOOST = 0.12D;

    /**
     * 黑色护符：造成伤害后回复实际伤害的 10%。
     */
    private static final float LIFESTEAL = 0.10F;

    /**
     * 蓝色护符：+25% 游泳速度。
     * NeoForge 提供 SWIM_SPEED 属性。
     */
    private static final double SWIM_SPEED = 0.25D;

    /**
     * 绿色神秘护身符 / 飞升护符：
     * +25% 挖掘速度。
     * 原项目是挖掘速度乘 1.25。
     * 这里写成额外增加原始速度的 25%。
     */
    private static final float MINING_SPEED_MULTIPLIER = 0.25F;

    /**
     * 每个属性修饰器都必须使用固定 ID。
     * 每 tick 先按 ID 移除旧修饰器，再按当前护符状态添加新修饰器，
     * 可以避免重复叠加，也能正确处理青色护符“疾跑/不疾跑”的切换。
     */
    private static final ResourceLocation RED_MODIFIER =
            id("enigmatic_amulet_red");

    private static final ResourceLocation AQUA_MODIFIER =
            id("enigmatic_amulet_aqua");

    private static final ResourceLocation MAGENTA_MODIFIER =
            id("enigmatic_amulet_magenta");

    private static final ResourceLocation BLUE_MODIFIER =
            id("enigmatic_amulet_blue");

    private static final ResourceLocation ELDRITCH_MODIFIER =
            id("eldritch_amulet");

    private EnigmaticAmuletEvents() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path);
    }

    /**
     * 玩家首次登录时给予无主护身符。
     * 现在无主护身符不会在登录时自动变成神秘护身符；
     * 玩家需要手持无主护身符右键，才会激活并随机生成一种颜色。
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        // 已经领取过就不再重复发放。
        if (data.getBoolean(STARTER_KEY)) {
            return;
        }

        if (hasAnyAmulet(player)) {
            data.putBoolean(STARTER_KEY, true);
            return;
        }

        ItemStack unwitnessedAmulet = new ItemStack(ModItems.UNWITNESSED_AMULET.get());

        if (!player.addItem(unwitnessedAmulet)) {
            player.drop(unwitnessedAmulet, false);
        }

        data.putBoolean(STARTER_KEY, true);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal().getPersistentData().getBoolean(STARTER_KEY)
                || hasAnyAmulet(event.getOriginal())) {
            event.getEntity().getPersistentData().putBoolean(STARTER_KEY, true);
        }
    }

    /**
     * Curios 装备限制。
     * 规则：
     * 1. 无主护身符不能直接装备，只能右键激活；
     * 2. 普通神秘护身符和飞升护符互斥；
     * 3. 同一时间只能装备一个普通神秘护身符或一个飞升护符；
     * 4. 创造模式玩家放宽限制，方便调试。
     */
    @SubscribeEvent
    public static void onCurioCanEquip(CurioCanEquipEvent event) {
        Player player = event.getEntity() instanceof Player checkedPlayer ? checkedPlayer : null;

        if (player != null && player.isCreative()) {
            return;
        }

        ItemStack stack = event.getStack();

        // 无主护身符是“未激活状态”，不允许放进 charm 槽。
        if (stack.getItem() instanceof UnwitnessedAmulet) {
            event.setEquipResult(TriState.FALSE);
            return;
        }

        boolean isRegularAmulet = stack.getItem() instanceof EnigmaticAmulet;
        boolean isAscensionAmulet = stack.is(ModItems.ASCENSION_AMULET.get());
        boolean isEldritchAmulet = stack.is(ModItems.ELDRITCH_AMULET.get());

        if (isEldritchAmulet && (player == null || !AbyssalHeartHelper.isWorthy(player))) {
            event.setEquipResult(TriState.FALSE);
            return;
        }

        if ((isRegularAmulet || isAscensionAmulet || isEldritchAmulet) && hasAnyEquippedAmulet(event.getEntity())) {
            event.setEquipResult(TriState.FALSE);
        }
    }

    /**
     * 每 tick 刷新属性类护符效果。
     * 飞升护符：
     * - 同时拥有红、青、品红、蓝四种属性类效果；
     * - 紫色、黑色、绿色分别在对应事件里处理。
     * 普通神秘护身符：
     * - 只拥有自身颜色效果。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        clearAttributeModifiers(player);

        boolean hasAscension = hasAscensionAmulet(player);
        boolean hasEldritch = hasEldritchAmulet(player);
        AmuletVariant variant = getEquippedVariant(player);

        if (!hasAscension && !hasEldritch && variant == null) {
            return;
        }

        if (hasAscension || hasEldritch) {
            for (AmuletVariant amuletVariant : AmuletVariant.values()) {
                applyAttributeModifier(player, amuletVariant);
            }

            if (hasEldritch && AbyssalHeartHelper.isWorthy(player)) {
                addModifier(
                        player,
                        Attributes.ATTACK_DAMAGE,
                        ELDRITCH_MODIFIER,
                        ELDRITCH_ATTACK_DAMAGE,
                        AttributeModifier.Operation.ADD_VALUE
                );
            }

            return;
        }

        applyAttributeModifier(player, variant);
    }

    /**
     * 品红护符：跳得更高。
     * 原理：
     * LivingJumpEvent 会在实体完成原版跳跃逻辑后触发。
     * 此时玩家已经获得了原版跳跃速度，我们再额外增加一点 Y 轴速度，
     * 就能实现“跳得更高”。
     * 为什么不用 Attributes.JUMP_STRENGTH？
     * 因为该属性在 1.21.x 中对玩家不稳定/不生效，
     * NeoForge 文档也提示 jump_strength 主要影响马，不影响玩家。
     */
    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 只在服务端修改运动，避免客户端和服务端速度不一致。
        if (player.level().isClientSide) {
            return;
        }

        // 只有佩戴品红神秘护符时才增加跳跃高度。
        if (!hasVariant(player, AmuletVariant.MAGENTA)) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();

        // 在原版跳跃速度基础上额外增加 Y 轴速度。
        player.setDeltaMovement(movement.x, movement.y + JUMP_HEIGHT_BOOST, movement.z);

        // 标记实体运动发生变化，帮助同步速度。
        player.hasImpulse = true;
    }

    /**
     * 紫色护符 / 飞升护符：
     * 弹射物命中玩家时，有 15% 概率偏转。
     */
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();

        if (!(result instanceof EntityHitResult entityHitResult)) {
            return;
        }

        if (!(entityHitResult.getEntity() instanceof Player player)) {
            return;
        }

        if (!hasVariant(player, AmuletVariant.VIOLET)) {
            return;
        }

        if (player.getRandom().nextFloat() < PROJECTILE_DEFLECT_CHANCE) {
            event.setCanceled(true);
            event.getProjectile().discard();
        }
    }

    /**
     * 黑色护符 / 飞升护符：
     * 玩家造成伤害后，按最终伤害量回复 10% 生命。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!hasVariant(player, AmuletVariant.BLACK)) {
            return;
        }

        float damage = event.getNewDamage();

        if (damage > 0.0F) {
            player.heal(damage * LIFESTEAL);
        }
    }

    /**
     * 绿色护符 / 飞升护符：
     * +25% 挖掘速度。
     * 原项目是在 BreakSpeed 事件里直接乘挖掘速度，
     * 这里按原项目方式实现。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (!hasVariant(player, AmuletVariant.GREEN)) {
            return;
        }

        float bonus = event.getOriginalSpeed() * MINING_SPEED_MULTIPLIER;
        event.setNewSpeed(event.getNewSpeed() + bonus);
    }

    /**
     * 判断玩家是否拥有某一种神秘护身符效果。
     * 普通神秘护身符：
     * - 只有对应颜色返回 true。
     * 飞升护符：
     * - 对所有颜色都返回 true。
     */
    private static boolean hasVariant(Player player, AmuletVariant variant) {
        return hasAscensionAmulet(player) || hasEldritchAmulet(player) || getEquippedVariant(player) == variant;
    }

    /**
     * 从 Curios 栏位中寻找当前佩戴的神秘护身符颜色。
     */
    private static AmuletVariant getEquippedVariant(LivingEntity entity) {
        AtomicReference<AmuletVariant> variant = new AtomicReference<>();

        CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof EnigmaticAmulet)).ifPresent(slotResult -> {
            if (slotResult.stack().getItem() instanceof EnigmaticAmulet amulet) {
                variant.set(amulet.variant());
            }
        });

        return variant.get();
    }

    /**
     * 判断玩家是否佩戴飞升护符。
     */
    private static boolean hasAscensionAmulet(LivingEntity entity) {
        AtomicReference<Boolean> hasAscension = new AtomicReference<>(false);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.is(ModItems.ASCENSION_AMULET.get())
                ))
                .ifPresent(slotResult -> hasAscension.set(true));

        return hasAscension.get();
    }

    public static boolean hasEldritchAmulet(LivingEntity entity) {
        AtomicReference<Boolean> hasEldritch = new AtomicReference<>(false);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.is(ModItems.ELDRITCH_AMULET.get())
                ))
                .ifPresent(slotResult -> hasEldritch.set(true));

        return hasEldritch.get();
    }

    /**
     * 判断玩家 Curios 栏里是否已经佩戴了普通神秘护身符或飞升护符。
     */
    private static boolean hasAnyEquippedAmulet(LivingEntity entity) {
        AtomicReference<Boolean> hasAmulet = new AtomicReference<>(false);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.getItem() instanceof EnigmaticAmulet
                                || stack.is(ModItems.ASCENSION_AMULET.get())
                                || stack.is(ModItems.ELDRITCH_AMULET.get())
                ))
                .ifPresent(slotResult -> hasAmulet.set(true));

        return hasAmulet.get();
    }

    private static boolean hasAnyAmulet(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (isAnyAmulet(stack)) {
                return true;
            }
        }

        AtomicReference<Boolean> hasAmulet = new AtomicReference<>(false);
        CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(EnigmaticAmuletEvents::isAnyAmulet))
                .ifPresent(slotResult -> hasAmulet.set(true));

        return hasAmulet.get();
    }

    private static boolean isAnyAmulet(ItemStack stack) {
        return stack.getItem() instanceof UnwitnessedAmulet
                || stack.getItem() instanceof EnigmaticAmulet
                || stack.is(ModItems.ASCENSION_AMULET.get())
                || stack.is(ModItems.ELDRITCH_AMULET.get());
    }
    /**
     * 清理所有护符可能添加过的属性修饰器。
     * 这里按固定 ResourceLocation ID 移除，
     * 比直接移除整个 Multimap 更稳，尤其适合青色护符这种数值会变化的效果。
     */
    private static void clearAttributeModifiers(Player player) {
        removeModifier(player, Attributes.ATTACK_DAMAGE, RED_MODIFIER);
        removeModifier(player, Attributes.MOVEMENT_SPEED, AQUA_MODIFIER);
        removeModifier(player, Attributes.GRAVITY, MAGENTA_MODIFIER);
        removeModifier(player, NeoForgeMod.SWIM_SPEED, BLUE_MODIFIER);
        removeModifier(player, Attributes.ATTACK_DAMAGE, ELDRITCH_MODIFIER);
    }

    /**
     * 根据护符颜色添加对应属性修饰器。
     */
    private static void applyAttributeModifier(Player player, AmuletVariant variant) {
        switch (variant) {
            case RED -> {
                // 红色：直接增加攻击伤害。
                addModifier(
                        player,
                        Attributes.ATTACK_DAMAGE,
                        RED_MODIFIER,
                        ATTACK_DAMAGE,
                        AttributeModifier.Operation.ADD_VALUE
                );
            }

            case AQUA -> {
                // 青色：只有玩家正在疾跑时才增加移动速度。
                if (player.isSprinting()) {
                    addModifier(
                            player,
                            Attributes.MOVEMENT_SPEED,
                            AQUA_MODIFIER,
                            SPRINTING_SPEED,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    );
                }
            }

            case MAGENTA -> // 品红：降低重力，让玩家下落更慢、跳跃/滞空感更轻。
                    addModifier(
                            player,
                            Attributes.GRAVITY,
                            MAGENTA_MODIFIER,
                            GRAVITY_REDUCTION,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    );

            case BLUE -> // 蓝色：增加游泳速度。
                    addModifier(
                            player,
                            NeoForgeMod.SWIM_SPEED,
                            BLUE_MODIFIER,
                            SWIM_SPEED,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    );
        }
    }

    /**
     * 给实体添加一个瞬时属性修饰器。
     * addTransientModifier 的效果不会写入存档，
     * 玩家退出、死亡或属性容器刷新后不会永久残留。
     */
    private static void addModifier(
            LivingEntity entity,
            net.minecraft.core.Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(modifierId, amount, operation));
    }

    /**
     * 按 ID 移除属性修饰器。
     * 如果该属性不存在，或者当前没有这个修饰器，直接跳过。
     */
    private static void removeModifier(
            LivingEntity entity,
            net.minecraft.core.Holder<Attribute> attribute,
            ResourceLocation modifierId
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(modifierId);
    }
}
