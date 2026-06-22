package org.enigmatic_legacy.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.item.items.EnchanterPearl;
import org.enigmatic_legacy.util.EnchanterPearlHelper;
import top.theillusivec4.curios.api.CuriosApi;

public final class EnchanterPearlEvents {

    private EnchanterPearlEvents() {
    }

    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            Multimap<String, AttributeModifier> modifier = createSlotModifier();
            boolean hasModifier = handler.getModifiers().get(EnchanterPearl.EXTRA_CHARM_SLOT)
                    .stream()
                    .anyMatch(attributeModifier -> attributeModifier.id().equals(EnchanterPearl.EXTRA_CHARM_SLOT_ID));
            boolean shouldHaveModifier = EnchanterPearlHelper.canUseEnchanterPearl(player);

            if (shouldHaveModifier && !hasModifier) {
                handler.addTransientSlotModifiers(modifier);
                handler.processSlots();
            } else if (!shouldHaveModifier && hasModifier) {
                handler.removeSlotModifiers(modifier);
                handler.processSlots();
            }
        });
    }

    private static Multimap<String, AttributeModifier> createSlotModifier() {
        Multimap<String, AttributeModifier> modifier = HashMultimap.create();
        modifier.put(
                EnchanterPearl.EXTRA_CHARM_SLOT,
                new AttributeModifier(
                        EnchanterPearl.EXTRA_CHARM_SLOT_ID,
                        EnchanterPearl.EXTRA_CHARM_SLOT_AMOUNT,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        return modifier;
    }
}
