package org.enigmatic_legacy.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
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

public class EnigmaticAmuletEvents {
    private static final String STARTER_KEY = EnigmaticLegacy.MODID + "_received_unwitnessed_amulet";

    private static final double ATTACK_DAMAGE = 2.0D;
    private static final double SPRINTING_SPEED = 0.15D;
    private static final float PROJECTILE_DEFLECT_CHANCE = 0.15F;
    private static final double GRAVITY_REDUCTION = -0.20D;
    private static final double MINING_EFFICIENCY = 2.0D;
    private static final float LIFESTEAL = 0.10F;
    private static final double SWIM_SPEED = 0.25D;

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        if (data.getBoolean(STARTER_KEY)) {
            return;
        }

        data.putBoolean(STARTER_KEY, true);
        player.addItem(new ItemStack(ModItems.UNWITNESSED_AMULET.get()));
    }

    @SubscribeEvent
    public static void onCurioCanEquip(CurioCanEquipEvent event) {
        if (event.getEntity() instanceof Player player && player.isCreative()) {
            return;
        }

        ItemStack stack = event.getStack();

        if (stack.getItem() instanceof UnwitnessedAmulet) {
            event.setEquipResult(TriState.FALSE);
            return;
        }

        if (stack.getItem() instanceof EnigmaticAmulet && getEquippedVariant(event.getEntity()) != null) {
            event.setEquipResult(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        player.getAttributes().removeAttributeModifiers(allAttributeModifiers(player));

        AmuletVariant variant = getEquippedVariant(player);

        if (variant != null) {
            player.getAttributes().addTransientAttributeModifiers(attributeModifiersFor(variant, player));
        }
    }

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

    private static boolean hasVariant(Player player, AmuletVariant variant) {
        return getEquippedVariant(player) == variant;
    }

    private static AmuletVariant getEquippedVariant(net.minecraft.world.entity.LivingEntity entity) {
        AtomicReference<AmuletVariant> variant = new AtomicReference<>();

        CuriosApi.getCuriosInventory(entity).ifPresent(handler ->
                handler.findFirstCurio(stack -> stack.getItem() instanceof EnigmaticAmulet)
                        .ifPresent(slotResult -> {
                            if (slotResult.stack().getItem() instanceof EnigmaticAmulet amulet) {
                                variant.set(amulet.variant());
                            }
                        })
        );

        return variant.get();
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> allAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();

        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                id("enigmatic_amulet_red"),
                ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_VALUE
        ));

        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                id("enigmatic_amulet_aqua"),
                player.isSprinting() ? SPRINTING_SPEED : 0.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));

        map.put(Attributes.GRAVITY, new AttributeModifier(
                id("enigmatic_amulet_magenta"),
                GRAVITY_REDUCTION,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));

        map.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(
                id("enigmatic_amulet_green"),
                MINING_EFFICIENCY,
                AttributeModifier.Operation.ADD_VALUE
        ));

        map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(
                id("enigmatic_amulet_blue"),
                SWIM_SPEED,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));

        return map;
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> attributeModifiersFor(AmuletVariant variant, Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();

        switch (variant) {
            case RED -> map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    id("enigmatic_amulet_red"),
                    ATTACK_DAMAGE,
                    AttributeModifier.Operation.ADD_VALUE
            ));

            case AQUA -> map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                    id("enigmatic_amulet_aqua"),
                    player.isSprinting() ? SPRINTING_SPEED : 0.0D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));

            case MAGENTA -> map.put(Attributes.GRAVITY, new AttributeModifier(
                    id("enigmatic_amulet_magenta"),
                    GRAVITY_REDUCTION,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));

            case GREEN -> map.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(
                    id("enigmatic_amulet_green"),
                    MINING_EFFICIENCY,
                    AttributeModifier.Operation.ADD_VALUE
            ));

            case BLUE -> map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(
                    id("enigmatic_amulet_blue"),
                    SWIM_SPEED,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));

            case VIOLET, BLACK -> {
                // 紫色弹射物偏转、黑色生命偷取在事件里处理，不走 Attribute。
            }
        }

        return map;
    }
}