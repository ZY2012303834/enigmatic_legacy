package org.enigmatic_legacy.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.scroll.CurseOfViolence;
import org.enigmatic_legacy.util.CurseOfViolenceHelper;

import java.util.UUID;

/**
 * 暴戾之咒事件逻辑。
 *
 * <p>物品类只负责 Curios 限制、吸收诅咒和 tooltip；
 * 所有会影响实体伤害、治疗和被标记目标的逻辑都放在这里，避免物品类承担全局事件职责。</p>
 */
public final class CurseOfViolenceEvents {
    private static final ResourceLocation VIOLENCE_ARMOR_MODIFIER = modLoc("violence_scroll_armor_debuff");
    private static final ResourceLocation VIOLENCE_ARMOR_TOUGHNESS_MODIFIER = modLoc("violence_scroll_armor_toughness_debuff");

    private CurseOfViolenceEvents() {
    }

    /**
     * 被暴戾之咒标记的实体会持续承受弱化版七咒第三诅咒：
     * 护甲与盔甲韧性降低。
     *
     * <p>原扩展还会让中立生物仇恨被标记者。当前项目的中立仇恨逻辑已经与七咒之戒、
     * 动物保护书、猪灵 AI 等系统耦合，这里先只复刻直接影响战斗数值的第一、第三、
     * 第四诅咒，避免给所有被标记生物引入不可控的全局仇恨副作用。</p>
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }

        removeWeakenedArmorModifiers(entity);

        if (!isMarkedByViolence(entity) || hasCursedRingCurio(entity)) {
            return;
        }

        double armorDebuff = ConfigCommon.CURSED_RING_ARMOR_DEBUFF.get() / 100.0D / 2.0D;

        addModifier(
                entity,
                Attributes.ARMOR,
                VIOLENCE_ARMOR_MODIFIER,
                -armorDebuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        addModifier(
                entity,
                Attributes.ARMOR_TOUGHNESS,
                VIOLENCE_ARMOR_TOUGHNESS_MODIFIER,
                -armorDebuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    /**
     * 暴戾之咒的核心战斗逻辑。
     *
     * <p>使用 LivingDamageEvent.Pre 是为了修改最终伤害。
     * 该阶段已经经过护甲、抗性、附魔等大部分原版减伤，最接近扩展项目旧版
     * LivingHurtEvent + LivingDamageEvent 组合中的“实际伤害”语义。</p>
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        float damage = event.getNewDamage();

        if (damage <= 0.0F) {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            damage = applyAttackerScroll(event, attacker, victim, damage);
            damage = applyMarkedAttackerPenalty(attacker, victim, damage);
        }

        damage = applyMarkedVictimPenalty(victim, damage);
        event.setNewDamage(Math.max(0.0F, damage));
    }

    /**
     * 攻击者佩戴暴戾之咒时：
     * 1. 连续攻击同一目标会基于上一击伤害增加额外伤害；
     * 2. 如果攻击发生在攻击者受击无敌时间内，会治疗攻击者并提高本次伤害；
     * 3. 目标会被打上暴戾七咒标记；
     * 4. 本次最终伤害写回卷轴，供下一次连续攻击使用。
     */
    private static float applyAttackerScroll(
            LivingDamageEvent.Pre event,
            LivingEntity attacker,
            LivingEntity victim,
            float damage
    ) {
        if (!CurseOfViolenceHelper.hasUsableScroll(attacker)) {
            return damage;
        }

        ItemStack scroll = CurseOfViolenceHelper.findScroll(attacker).orElse(ItemStack.EMPTY);

        if (scroll.isEmpty()) {
            return damage;
        }

        UUID victimId = victim.getUUID();
        UUID storedTarget = CurseOfViolence.getStoredTarget(scroll);

        if (victimId.equals(storedTarget)) {
            float extraDamage = (float) (CurseOfViolence.getStoredDamage(scroll) * CurseOfViolence.getStoredDamageModifier(scroll) / 100.0D);
            damage += extraDamage;
            consumeAngerEnergy(attacker, scroll);
        } else {
            CurseOfViolence.setStoredTarget(scroll, victimId);
        }

        if (attacker.invulnerableTime > 0) {
            float heal = (float) (damage * ConfigCommon.CURSE_OF_VIOLENCE_INVULNERABLE_HEAL_MULTIPLIER.get() / 100.0D);

            if (heal > 0.0F) {
                attacker.heal(heal);
            }

            damage *= (float) (1.0D + ConfigCommon.CURSE_OF_VIOLENCE_INVULNERABLE_ATTACK_MODIFIER.get() / 100.0D);
            attacker.invulnerableTime = 0;
        }

        victim.getPersistentData().putBoolean(CurseOfViolence.VIOLENCE_SEVEN_CURSES_TAG, true);
        CurseOfViolence.setStoredDamage(scroll, Math.max(0.0F, damage));
        return damage;
    }

    /**
     * 连续攻击消耗怒意能量。
     *
     * <p>原扩展中普通状态约 1/3 概率消耗 1 点能量。
     * 当能量耗尽时，它还可能从装备中的其它诅咒附魔里“啃”掉一个诅咒来恢复能量。
     * 这里保留这两个行为，但用当前项目的 ItemEnchantments 数据组件实现。</p>
     */
    private static void consumeAngerEnergy(LivingEntity attacker, ItemStack scroll) {
        if (attacker instanceof Player player && player.isCreative()) {
            return;
        }

        if (attacker.getRandom().nextInt(3) == 0) {
            CurseOfViolence.addDurability(scroll, -1);
        }

        if (CurseOfViolence.getDurability(scroll) > 0 || attacker.getRandom().nextInt(8) != 0) {
            return;
        }

        if (removeOneCurseFromEquippedCurio(attacker)) {
            CurseOfViolence.addDurability(scroll, 30);
        }
    }

    private static boolean removeOneCurseFromEquippedCurio(LivingEntity entity) {
        return CuriosLookupApi.getInventory(entity)
                .map(handler -> {
                    for (var stacksHandler : handler.getCurios().values()) {
                        var stacks = stacksHandler.getStacks();

                        for (int slot = 0; slot < stacks.getSlots(); slot++) {
                            ItemStack stack = stacks.getStackInSlot(slot);

                            if (stack.isEmpty() || stack.is(ModItems.VIOLENCE_SCROLL.get())) {
                                continue;
                            }

                            if (entity.getRandom().nextInt(4) != 0) {
                                continue;
                            }

                            if (CurseOfViolence.removeOneCurse(stack)) {
                                stacksHandler.update();
                                return true;
                            }
                        }
                    }

                    return false;
                })
                .orElse(false);
    }

    /**
     * 被暴戾标记的攻击者会承受弱化版七咒第四诅咒：
     * 其造成的伤害降低，但只降低原七咒数值的一半。
     */
    private static float applyMarkedAttackerPenalty(LivingEntity attacker, LivingEntity victim, float damage) {
        if (!isMarkedByViolence(attacker) || hasCursedRingCurio(victim)) {
            return damage;
        }

        float debuff = ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() / 100.0F / 2.0F;
        return damage * Math.max(0.0F, 1.0F - debuff);
    }

    /**
     * 被暴戾标记的受害者会承受弱化版七咒第一诅咒：
     * 受到的最终伤害提高，但只取原七咒痛苦倍率超过 100% 部分的一半。
     */
    private static float applyMarkedVictimPenalty(LivingEntity victim, float damage) {
        if (!isMarkedByViolence(victim) || hasCursedRingCurio(victim)) {
            return damage;
        }

        float painMultiplier = ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() / 100.0F;
        float extraPain = Math.max(0.0F, painMultiplier - 1.0F) / 2.0F;
        return damage * (1.0F + extraPain);
    }

    private static boolean isMarkedByViolence(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(CurseOfViolence.VIOLENCE_SEVEN_CURSES_TAG);
    }

    private static boolean hasCursedRingCurio(LivingEntity entity) {
        return CuriosLookupApi.hasCurio(entity, ModItems.CURSED_RING.get());
    }

    private static void addModifier(
            LivingEntity entity,
            net.minecraft.core.Holder<Attribute> attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null || amount == 0.0D) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }

    private static void removeWeakenedArmorModifiers(LivingEntity entity) {
        removeModifier(entity, Attributes.ARMOR, VIOLENCE_ARMOR_MODIFIER);
        removeModifier(entity, Attributes.ARMOR_TOUGHNESS, VIOLENCE_ARMOR_TOUGHNESS_MODIFIER);
    }

    private static void removeModifier(LivingEntity entity, net.minecraft.core.Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = entity.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path);
    }
}
