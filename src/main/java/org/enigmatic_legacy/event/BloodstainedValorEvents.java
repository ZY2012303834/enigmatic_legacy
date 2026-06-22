package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.BloodstainedValorHelper;

/**
 * 血战沙场之证事件。
 * 效果随缺失生命比例动态变化：
 * 1. 提高攻击伤害；
 * 2. 提高攻击速度；
 * 3. 提高移动速度；
 * 4. 降低受到的伤害。
 */
public class BloodstainedValorEvents {
    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "bloodstained_valor_attack_speed"
    );

    private static final ResourceLocation MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "bloodstained_valor_movement_speed"
    );

    /**
     * 每 tick 刷新动态属性。
     * 原项目中攻击速度和移动速度是 Curios 属性修饰器；
     * 这里用 PlayerTickEvent 动态刷新，避免缺失生命变化后数值不更新。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        // 先移除旧修饰器，防止卸下护符、回血或失去七咒之戒后属性残留。
        removeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_ID);
        removeModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_ID);

        if (!BloodstainedValorHelper.canUseBloodstainedValor(player)) {
            return;
        }

        double missingHealthRatio = BloodstainedValorHelper.getMissingHealthRatio(player);

        if (missingHealthRatio <= 0.0D) {
            return;
        }

        double attackSpeedBonus = missingHealthRatio * ConfigCommon.BLOODSTAINED_VALOR_ATTACK_SPEED.get();
        double movementSpeedBonus = missingHealthRatio * ConfigCommon.BLOODSTAINED_VALOR_MOVEMENT_SPEED.get();

        addMultipliedBaseModifier(
                player,
                Attributes.ATTACK_SPEED,
                ATTACK_SPEED_ID,
                attackSpeedBonus
        );

        addMultipliedBaseModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                MOVEMENT_SPEED_ID,
                movementSpeedBonus
        );
    }

    /**
     * 处理攻击伤害提升和受到伤害降低。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        // 攻击者佩戴血战沙场之证：提高造成的伤害。
        if (event.getSource().getEntity() instanceof Player attacker
                && BloodstainedValorHelper.canUseBloodstainedValor(attacker)) {
            double missingHealthRatio = BloodstainedValorHelper.getMissingHealthRatio(attacker);
            double attackDamageBonus = missingHealthRatio * ConfigCommon.BLOODSTAINED_VALOR_ATTACK_DAMAGE.get();

            if (attackDamageBonus > 0.0D) {
                event.setAmount((float) (event.getAmount() * (1.0D + attackDamageBonus)));
            }
        }

        // 受击者佩戴血战沙场之证：降低受到的伤害。
        if (event.getEntity() instanceof Player defender
                && BloodstainedValorHelper.canUseBloodstainedValor(defender)) {
            double missingHealthRatio = BloodstainedValorHelper.getMissingHealthRatio(defender);
            double resistance = missingHealthRatio * ConfigCommon.BLOODSTAINED_VALOR_DAMAGE_RESISTANCE.get();

            if (resistance > 0.0D) {
                double multiplier = Math.max(0.0D, 1.0D - resistance);
                event.setAmount((float) (event.getAmount() * multiplier));
            }
        }
    }

    private static void addMultipliedBaseModifier(
            Player player,
            Holder<Attribute> attribute,
            ResourceLocation id,
            double amount
    ) {
        if (amount <= 0.0D) {
            return;
        }

        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(
                id,
                amount,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
    }
}