package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EldritchAmulet;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.OwnedEntityHelper;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EldritchAmuletEvents {
    private static final String INVENTORY_MAIN = "InventoryMain";
    private static final String INVENTORY_ARMOR = "InventoryArmor";
    private static final String INVENTORY_OFFHAND = "InventoryOffhand";
    // 轻蔑之约凝视减益持续 5 秒；Minecraft 状态效果时间单位为 tick，20 tick = 1 秒。
    private static final int GAZE_EFFECT_DURATION = 100;
    private static final int GAZE_EFFECT_AMPLIFIER = 1;

    private static final Map<UUID, CompoundTag> STORED_INVENTORIES = new HashMap<>();

    private EldritchAmuletEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)
                || player.tickCount % 5 != 0
                || !hasEldritchAndWorthy(player)) {
            return;
        }

        for (LivingEntity target : getObservedEntities(serverPlayer)) {
            if (target instanceof ServerPlayer otherPlayer && hasEldritchAndWorthy(otherPlayer)) {
                continue;
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, GAZE_EFFECT_DURATION, GAZE_EFFECT_AMPLIFIER));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, GAZE_EFFECT_DURATION, GAZE_EFFECT_AMPLIFIER));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, GAZE_EFFECT_DURATION, GAZE_EFFECT_AMPLIFIER));
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !hasEldritchAndWorthy(player)) {
            return;
        }

        float damage = event.getNewDamage();

        if (damage > 0.0F) {
            player.heal(damage * EldritchAmulet.EXTRA_LIFESTEAL);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !hasEldritchAndWorthy(player)) {
            return;
        }

        STORED_INVENTORIES.put(player.getUUID(), storeInventory(player));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer newPlayer)) {
            return;
        }

        CompoundTag inventoryTag = STORED_INVENTORIES.remove(event.getOriginal().getUUID());

        if (inventoryTag != null) {
            restoreInventory(newPlayer, inventoryTag);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioDropRules(DropRulesEvent event) {
        event.addOverride(
                stack -> stack.is(ModItems.ELDRITCH_AMULET.get()),
                ICurio.DropRule.ALWAYS_KEEP
        );
    }

    private static boolean hasEldritchAndWorthy(Player player) {
        return EnigmaticAmuletEvents.hasEldritchAmulet(player) && AbyssalHeartHelper.isWorthy(player);
    }

    private static Iterable<LivingEntity> getObservedEntities(ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 end = start.add(look.scale(EldritchAmulet.GAZE_RANGE));
        AABB searchBox = player.getBoundingBox()
                .expandTowards(look.scale(EldritchAmulet.GAZE_RANGE))
                .inflate(EldritchAmulet.GAZE_RADIUS);
        double maxProjection = EldritchAmulet.GAZE_RANGE;
        double maxDistanceSqr = EldritchAmulet.GAZE_RADIUS * EldritchAmulet.GAZE_RADIUS;

        return player.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player
                        && entity.isAlive()
                        && EntitySelector.NO_SPECTATORS.test(entity)
                        /*
                         * 轻蔑之约的凝视只负责给目标附加负面效果。
                         * 在目标枚举阶段排除玩家友方实体，避免已驯服生物、女仆、
                         * 玩家傀儡以及由傀儡继续装配出的傀儡被反复施加减益。
                         */
                        && !OwnedEntityHelper.isProtectedPlayerOwnedAlly(player, entity)
                        && isNearSightLine(start, look, end, entity, maxProjection, maxDistanceSqr)
                        && player.hasLineOfSight(entity)
        );
    }

    private static boolean isNearSightLine(
            Vec3 start,
            Vec3 look,
            Vec3 end,
            LivingEntity entity,
            double maxProjection,
            double maxDistanceSqr
    ) {
        Vec3 target = entity.getBoundingBox().getCenter();
        Vec3 startToTarget = target.subtract(start);
        double projection = startToTarget.dot(look);

        if (projection <= 0.0D || projection > maxProjection) {
            return false;
        }

        Vec3 closest = start.add(look.scale(projection));
        return target.distanceToSqr(closest) <= maxDistanceSqr
                || entity.getBoundingBox().clip(start, end).isPresent();
    }

    private static CompoundTag storeInventory(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();
        tag.put(INVENTORY_MAIN, storeInventoryList(player, player.getInventory().items));
        tag.put(INVENTORY_ARMOR, storeInventoryList(player, player.getInventory().armor));
        tag.put(INVENTORY_OFFHAND, storeInventoryList(player, player.getInventory().offhand));
        return tag;
    }

    private static ListTag storeInventoryList(ServerPlayer player, NonNullList<ItemStack> inventory) {
        ListTag list = new ListTag();

        for (int index = 0; index < inventory.size(); index++) {
            ItemStack stack = inventory.get(index);

            if (stack.isEmpty() || hasVanishingCurse(player, stack)) {
                list.add(new CompoundTag());
            } else {
                list.add(stack.save(player.registryAccess()));
            }

            inventory.set(index, ItemStack.EMPTY);
        }

        return list;
    }

    private static void restoreInventory(ServerPlayer player, CompoundTag tag) {
        restoreInventoryList(player, player.getInventory().items, tag.getList(INVENTORY_MAIN, Tag.TAG_COMPOUND));
        restoreInventoryList(player, player.getInventory().armor, tag.getList(INVENTORY_ARMOR, Tag.TAG_COMPOUND));
        restoreInventoryList(player, player.getInventory().offhand, tag.getList(INVENTORY_OFFHAND, Tag.TAG_COMPOUND));
    }

    private static void restoreInventoryList(ServerPlayer player, NonNullList<ItemStack> inventory, ListTag list) {
        int size = Math.min(inventory.size(), list.size());

        for (int index = 0; index < size; index++) {
            inventory.set(index, ItemStack.parseOptional(player.registryAccess(), list.getCompound(index)));
        }
    }

    private static boolean hasVanishingCurse(ServerPlayer player, ItemStack stack) {
        Holder<Enchantment> vanishingCurse = player.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.VANISHING_CURSE);
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        return enchantments.getLevel(vanishingCurse) > 0;
    }
}
