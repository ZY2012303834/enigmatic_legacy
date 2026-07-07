package org.enigmatic_legacy.item.items.spellstone;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 忘却冰晶 / Forgotten Ice Crystal。
 *
 * <p>冰霜系术石，复刻 Enigmatic Addons 的 Forgotten Ice Crystal，并按本项目 1.21.1 NeoForge 结构重写。</p>
 *
 * <p>主动能力会释放寒潮，对附近生物造成冻结伤害并追加冻结时间。</p>
 *
 * <p>被动能力会强化冻结相关战斗：佩戴者免疫冻结，近战使目标更快冻结，
 * 完全冻结目标受到更高伤害，长期完全冻结的目标会进入硬冻结状态。</p>
 *
 * <p>代价是佩戴者更惧怕火焰与摔落，但会抵御部分弹射物与音波伤害。</p>
 */
public class ForgottenIceCrystal extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * 记录目标被忘却冰晶持续完全冻结的时间。
     */
    public static final String FROZEN_TICK_TAG = "enigmatic_legacy.forgotten_ice_crystal_frozen_tick";

    /**
     * 标记目标已经进入硬冻结状态。
     */
    public static final String HARD_FROZEN_TAG = "enigmatic_legacy.forgotten_ice_crystal_hard_frozen";

    /**
     * 硬冻结移动速度修正 ID。
     */
    public static final ResourceLocation HARD_FROZEN_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "forgotten_ice_crystal_hard_frozen_speed"
    );

    public ForgottenIceCrystal() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * Curios 每 tick 被动逻辑。
     *
     * <p>只有玩家佩戴时才扫描附近实体，用来累计“完全冻结”时间。</p>
     *
     * <p>累计时间超过配置阈值后，目标会被事件类处理为硬冻结状态。</p>
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        double radius = ConfigCommon.FORGOTTEN_ICE_CRYSTAL_ACTIVE_RADIUS.get();
        AABB area = player.getBoundingBox().inflate(radius);

        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (entity == player) {
                continue;
            }

            if (!entity.isFullyFrozen()) {
                entity.getPersistentData().remove(FROZEN_TICK_TAG);
                continue;
            }

            int ticksFrozen = Mth.clamp(
                    entity.getPersistentData().getInt(FROZEN_TICK_TAG) + 1,
                    0,
                    ConfigCommon.FORGOTTEN_ICE_CRYSTAL_MAX_STORED_FROZEN_TICKS.get()
            );

            entity.getPersistentData().putInt(FROZEN_TICK_TAG, ticksFrozen);

            if (ticksFrozen > ConfigCommon.FORGOTTEN_ICE_CRYSTAL_HARD_FROZEN_THRESHOLD.get()) {
                entity.getPersistentData().putBoolean(HARD_FROZEN_TAG, true);
            }
        }
    }

    /**
     * 主动能力入口。
     *
     * <p>这个方法由术石快捷键网络包调用，本身不会自动触发。</p>
     *
     * <p>主动能力会在范围内释放寒潮：造成冻结伤害、追加冻结时间，
     * 对已经完全冻结并被积累冻结时间的目标造成更高伤害。</p>
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        double radius = ConfigCommon.FORGOTTEN_ICE_CRYSTAL_ACTIVE_RADIUS.get();
        float baseDamage = ConfigCommon.FORGOTTEN_ICE_CRYSTAL_EXTRA_DAMAGE_BASE.get().floatValue()
                + (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        AABB area = player.getBoundingBox().inflate(radius);
        boolean hitAny = false;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (entity == player) {
                continue;
            }

            int storedFrozenTicks = entity.getPersistentData().getInt(FROZEN_TICK_TAG);
            float extraMultiplier = 0.0F;

            if (entity.isFullyFrozen()) {
                extraMultiplier = (float) (ConfigCommon.FORGOTTEN_ICE_CRYSTAL_EXTRA_DAMAGE_PER_FROZEN_TICK.get()
                        * Math.min(storedFrozenTicks, 400) / 2.0D);
            }

            if (entity.canFreeze()) {
                entity.setTicksFrozen(entity.getTicksFrozen() + ConfigCommon.FORGOTTEN_ICE_CRYSTAL_ACTIVE_FREEZE_TICKS.get());
            }

            float finalDamage = baseDamage * (1.0F + extraMultiplier);
            entity.hurt(player.damageSources().source(DamageTypes.FREEZE, player), finalDamage);
            shrinkStoredFrozenTicks(entity, storedFrozenTicks);
            sendFreezeParticles(level, entity);
            hitAny = true;
        }

        BlockPos origin = player.blockPosition();
        level.playSound(null, origin, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.8F, 0.7F);

        if (hitAny) {
            sendFreezeParticles(level, player);
        }

        player.getCooldowns().addCooldown(this, ConfigCommon.FORGOTTEN_ICE_CRYSTAL_COOLDOWN.get());
    }

    /**
     * 主动技能命中后收缩目标存储的完全冻结时间。
     *
     * <p>这一点沿用原拓展项目设计：主动能力会消耗一部分已经积累的冻结收益，
     * 避免同一个完全冻结目标无限滚雪球。</p>
     */
    private static void shrinkStoredFrozenTicks(LivingEntity entity, int storedFrozenTicks) {
        if (storedFrozenTicks <= 400) {
            entity.getPersistentData().remove(FROZEN_TICK_TAG);
            return;
        }

        entity.getPersistentData().putInt(FROZEN_TICK_TAG, (storedFrozenTicks - 400) / 2);
    }

    /**
     * 发送冻结粒子。
     */
    private static void sendFreezeParticles(ServerLevel level, LivingEntity entity) {
        Vec3 center = entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D);

        level.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Blocks.BLUE_ICE)),
                center.x,
                center.y,
                center.z,
                18,
                0.35D,
                0.35D,
                0.35D,
                0.02D
        );

        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                center.x,
                center.y,
                center.z,
                18,
                0.35D,
                0.35D,
                0.35D,
                0.02D
        );
    }

    /**
     * 硬冻结属性修正。
     *
     * <p>原项目使用总乘法大幅降低移动速度，这里集中封装给事件类调用。</p>
     */
    public static Multimap<Holder<Attribute>, AttributeModifier> getHardFrozenModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                        HARD_FROZEN_SPEED_ID,
                        -2.0D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        return modifiers;
    }

    /**
     * 物品 tooltip。
     *
     * <p>统一样式：</p>
     * <ul>
     *     <li>普通介绍：紫色；</li>
     *     <li>数字 / 百分比：金色；</li>
     *     <li>负面代价：红色。</li>
     * </ul>
     *
     * <p>这里在国际化 key 后方写中文注释，方便维护语言文件。</p>
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift()); // 按住 Shift 查看详情。
            return;
        }

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.active" // 主动能力：
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.active.1" // 释放冻结寒潮，伤害并冻结附近生物。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.cooldown", // 冷却：%s 秒。
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.FORGOTTEN_ICE_CRYSTAL_COOLDOWN.get() / 20.0F))
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.passive" // 被动能力：
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.1" // 免疫冻结伤害。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.2", // 受到的弹射物与音波伤害降低 %s。
                SpellstoneTooltip.percent(ConfigCommon.FORGOTTEN_ICE_CRYSTAL_PROJECTILE_AND_SONIC_RESISTANCE.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.3" // 近战攻击会使目标更快冻结。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.4", // 完全冻结的目标受到你造成的伤害提高 %s。
                SpellstoneTooltip.percent(ConfigCommon.FORGOTTEN_ICE_CRYSTAL_FROST_DAMAGE_BOOST.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.5" // 长时间保持完全冻结的生物会陷入硬冻结，几乎无法移动。
        ));
        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.forgotten_ice_crystal.passive.6" // 极寒会让火焰与摔落伤害变得更加危险。
        ));
    }
}
