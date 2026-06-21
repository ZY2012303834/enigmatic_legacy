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
import org.enigmatic_legacy.item.items.AmuletVariant;
import org.enigmatic_legacy.item.items.EnigmaticAmulet;
import org.enigmatic_legacy.item.items.UnwitnessedAmulet;
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
     * 绿色护符：+2 挖掘效率。
     * Minecraft 1.21 原版已有 MINING_EFFICIENCY 属性。
     */
    private static final double MINING_EFFICIENCY = 2.0D;

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

    private static final ResourceLocation GREEN_MODIFIER =
            id("enigmatic_amulet_green");

    private static final ResourceLocation BLUE_MODIFIER =
            id("enigmatic_amulet_blue");

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

        data.putBoolean(STARTER_KEY, true);
        player.addItem(new ItemStack(ModItems.UNWITNESSED_AMULET.get()));
    }

    /**
     * Curios 装备限制。
     * 规则：
     * 1. 无主护身符不能直接装备，只能右键激活。
     * 2. 神秘护身符同一时间只能装备一个。
     * 3. 创造模式玩家放宽限制，方便调试。
     */
    @SubscribeEvent
    public static void onCurioCanEquip(CurioCanEquipEvent event) {
        if (event.getEntity() instanceof Player player && player.isCreative()) {
            return;
        }

        ItemStack stack = event.getStack();

        // 无主护身符是“未激活状态”，不允许放进 charm 槽。
        if (stack.getItem() instanceof UnwitnessedAmulet) {
            event.setEquipResult(TriState.FALSE);
            return;
        }

        // 如果玩家已经佩戴了任意颜色的神秘护身符，则不允许再装备第二个。
        if (stack.getItem() instanceof EnigmaticAmulet && getEquippedVariant(event.getEntity()) != null) {
            event.setEquipResult(TriState.FALSE);
        }
    }

    /**
     * 每 tick 刷新属性类护符效果。
     * 为什么不用永久属性？
     * 因为青色护符只在疾跑时生效，玩家状态随时变化；
     * 使用瞬时属性修饰器，每 tick 清理再添加，逻辑最直接、也最不容易残留。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // 属性只在服务端处理，客户端会由服务器同步。
        if (player.level().isClientSide) {
            return;
        }

        // 先清理所有可能来自神秘护身符的属性修饰器，防止切换护符或取消装备后残留。
        clearAttributeModifiers(player);

        AmuletVariant variant = getEquippedVariant(player);

        // 没有佩戴神秘护身符时，不添加任何效果。
        if (variant == null) {
            return;
        }

        // 根据当前佩戴的颜色添加对应效果。
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
     * 紫色护符：弹射物命中玩家时，有概率直接取消命中并移除弹射物。
     * 这里处理的是“事件类效果”，不依赖 Attribute。
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

        if (hasVariant(player, AmuletVariant.VIOLET)) {
            return;
        }

        if (player.getRandom().nextFloat() < PROJECTILE_DEFLECT_CHANCE) {
            event.setCanceled(true);
            event.getProjectile().discard();
        }
    }

    /**
     * 黑色护符：玩家造成伤害后，按实际伤害量回血。
     * 使用 LivingDamageEvent.Post 是为了拿到最终伤害，
     * 避免按被护甲、抗性、附魔减免前的伤害回血过多。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (hasVariant(player, AmuletVariant.BLACK)) {
            return;
        }

        float damage = event.getNewDamage();

        if (damage > 0.0F) {
            player.heal(damage * LIFESTEAL);
        }
    }

    private static boolean hasVariant(Player player, AmuletVariant variant) {
        return getEquippedVariant(player) != variant;
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
     * 清理所有护符可能添加过的属性修饰器。
     * 这里按固定 ResourceLocation ID 移除，
     * 比直接移除整个 Multimap 更稳，尤其适合青色护符这种数值会变化的效果。
     */
    private static void clearAttributeModifiers(Player player) {
        removeModifier(player, Attributes.ATTACK_DAMAGE, RED_MODIFIER);
        removeModifier(player, Attributes.MOVEMENT_SPEED, AQUA_MODIFIER);
        removeModifier(player, Attributes.GRAVITY, MAGENTA_MODIFIER);
        removeModifier(player, Attributes.MINING_EFFICIENCY, GREEN_MODIFIER);
        removeModifier(player, NeoForgeMod.SWIM_SPEED, BLUE_MODIFIER);
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

            case MAGENTA -> {
                // 品红：降低重力，让玩家下落更慢、跳跃/滞空感更轻。
                addModifier(
                        player,
                        Attributes.GRAVITY,
                        MAGENTA_MODIFIER,
                        GRAVITY_REDUCTION,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
            }

            case GREEN -> {
                // 绿色：增加挖掘效率属性。
                addModifier(
                        player,
                        Attributes.MINING_EFFICIENCY,
                        GREEN_MODIFIER,
                        MINING_EFFICIENCY,
                        AttributeModifier.Operation.ADD_VALUE
                );
            }

            case BLUE -> {
                // 蓝色：增加游泳速度。
                addModifier(
                        player,
                        NeoForgeMod.SWIM_SPEED,
                        BLUE_MODIFIER,
                        SWIM_SPEED,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
            }

            case VIOLET, BLACK -> {
                // 紫色和黑色不走属性：
                // 紫色在 ProjectileImpactEvent 中处理；
                // 黑色在 LivingDamageEvent.Post 中处理。
            }
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