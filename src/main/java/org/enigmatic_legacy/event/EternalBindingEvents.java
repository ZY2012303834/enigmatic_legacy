package org.enigmatic_legacy.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EternalBindingEvents {
    private static final String STACK_TAG = "Stack";
    private static final String SLOT_TAG = "Slot";
    private static final Map<UUID, ListTag> PENDING_ARMOR = new ConcurrentHashMap<>();

    private EternalBindingEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ListTag keptArmor = new ListTag();
        keepArmorSlot(player, keptArmor, EquipmentSlot.HEAD);
        keepArmorSlot(player, keptArmor, EquipmentSlot.CHEST);
        keepArmorSlot(player, keptArmor, EquipmentSlot.LEGS);
        keepArmorSlot(player, keptArmor, EquipmentSlot.FEET);

        if (!keptArmor.isEmpty()) {
            PENDING_ARMOR.put(player.getUUID(), keptArmor);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer newPlayer)) {
            return;
        }

        ListTag keptArmor = PENDING_ARMOR.remove(event.getOriginal().getUUID());

        if (keptArmor == null) {
            return;
        }

        for (Tag tag : keptArmor) {
            if (!(tag instanceof CompoundTag entry)) {
                continue;
            }

            EquipmentSlot slot = EquipmentSlot.byName(entry.getString(SLOT_TAG));

            if (slot == null || slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }

            ItemStack stack = ItemStack.parseOptional(
                    newPlayer.registryAccess(),
                    entry.getCompound(STACK_TAG)
            );

            if (!stack.isEmpty()) {
                newPlayer.setItemSlot(slot, stack);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioDropRules(DropRulesEvent event) {
        event.addOverride(
                EternalBindingEvents::hasEternalBinding,
                ICurio.DropRule.ALWAYS_KEEP
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioCanUnequip(CurioCanUnequipEvent event) {
        if (!hasEternalBinding(event.getStack())) {
            return;
        }

        if (event.getEntity() instanceof Player player && player.isCreative()) {
            event.setUnequipResult(TriState.TRUE);
            return;
        }

        event.setUnequipResult(TriState.FALSE);
    }

    public static boolean hasEternalBinding(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(ModEnchantments.ETERNAL_BINDING) && entry.getIntValue() > 0) {
                return true;
            }
        }

        return false;
    }

    private static void keepArmorSlot(ServerPlayer player, ListTag keptArmor, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);

        if (stack.isEmpty() || !hasEternalBinding(stack)) {
            return;
        }

        CompoundTag entry = new CompoundTag();
        entry.putString(SLOT_TAG, slot.getName());
        entry.put(STACK_TAG, stack.save(player.registryAccess()));
        keptArmor.add(entry);

        player.setItemSlot(slot, ItemStack.EMPTY);
    }
}
