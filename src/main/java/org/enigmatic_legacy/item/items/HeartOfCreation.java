package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 创造之心 / Heart of Creation。

 * 类型：术石 spellstone。

 * 主动效果：
 * 对配置范围内的所有敌人降下闪电，造成配置伤害，并附加凋零效果。

 * 注意：
 * 该物品不添加合成配方，不加入生存获取流程。
 */
public class HeartOfCreation extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * 主动技能凋零持续时间。
     * 100 tick = 5 秒。
     */
    public static final int ACTIVE_WITHER_DURATION = 100;

    /**
     * 主动技能凋零等级。

     * 注意：
     * Minecraft 药水等级从 0 开始。
     * 0 = I
     * 1 = II
     * 2 = III

     * 所以这里 2 代表凋零 III。
     */
    public static final int ACTIVE_WITHER_AMPLIFIER = 2;

    public HeartOfCreation() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许右键从手中直接装备到 Curios 术石槽。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 只能放进 spellstone 槽。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * 主动技能入口。

     * SpellstoneUsePayload 会在玩家按下术石快捷键时调用它。
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        double range = ConfigCommon.HEART_OF_CREATION_LIGHTNING_RANGE.get();
        float damage = ConfigCommon.HEART_OF_CREATION_LIGHTNING_DAMAGE.get().floatValue();

        AABB area = player.getBoundingBox().inflate(range);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> isValidEnemy(player, target)
        );

        if (targets.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.heart_of_creation.no_targets")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            player.getCooldowns().addCooldown(this, ConfigCommon.HEART_OF_CREATION_COOLDOWN.get());
            return;
        }

        for (LivingEntity target : targets) {
            strikeTarget(level, player, target, damage);
        }

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                1.5F,
                1.0F
        );

        player.getCooldowns().addCooldown(this, ConfigCommon.HEART_OF_CREATION_COOLDOWN.get());
    }

    /**
     * 判断目标是否算敌人。

     * 这里优先使用原版 Enemy 标记：
     * 僵尸、骷髅、苦力怕、幻翼等怪物都属于 Enemy。

     * 同时兼容正在攻击玩家 / 被玩家攻击过的生物。
     */
    private static boolean isValidEnemy(ServerPlayer player, LivingEntity target) {
        if (target == player) {
            return false;
        }

        if (!target.isAlive()) {
            return false;
        }

        if (player.isAlliedTo(target)) {
            return false;
        }

        return target instanceof Enemy
                || target.getLastHurtByMob() == player
                || player.getLastHurtByMob() == target;
    }

    /**
     * 对单个目标降下闪电并造成伤害。

     * 主动效果：
     * 对范围内所有敌人降下视觉闪电；
     * 造成配置伤害；
     * 附加凋零 III。
     */
    private static void strikeTarget(ServerLevel level, ServerPlayer player, LivingEntity target, float damage) {
        // 原作者创造之心主动技能音效：凋灵发射音效。
        level.levelEvent(null, 1024, target.blockPosition(), 0);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);

        if (lightning != null) {
            lightning.moveTo(target.getX(), target.getY(), target.getZ());
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }

        // 配置伤害，默认 10。
        target.hurt(player.damageSources().lightningBolt(), damage);

        // 附加凋零 III。
        target.addEffect(new MobEffectInstance(
                MobEffects.WITHER,
                ACTIVE_WITHER_DURATION,
                ACTIVE_WITHER_AMPLIFIER
        ), player);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.active")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.heart_of_creation.active",
                ConfigCommon.HEART_OF_CREATION_LIGHTNING_RANGE.get(),
                ConfigCommon.HEART_OF_CREATION_LIGHTNING_DAMAGE.get()
        ).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.spellstone.cooldown",
                String.format("%.1f", ConfigCommon.HEART_OF_CREATION_COOLDOWN.get() / 20.0F)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.heart_of_creation.passive.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.heart_of_creation.passive.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.heart_of_creation.passive.3")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.heart_of_creation.passive.4")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.heart_of_creation.passive.5")
                .withStyle(ChatFormatting.GOLD));
    }
}