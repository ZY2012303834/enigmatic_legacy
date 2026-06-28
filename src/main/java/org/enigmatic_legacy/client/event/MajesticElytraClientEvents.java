package org.enigmatic_legacy.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.network.MajesticElytraBoostPayload;

public final class MajesticElytraClientEvents {
    private static boolean boosting;

    private MajesticElytraClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            boosting = false;
            return;
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean shouldBoost = minecraft.options.keyJump.isDown()
                && player.isFallFlying()
                && chestStack.is(ModItems.MAJESTIC_ELYTRA.get())
                && ElytraItem.isFlyEnabled(chestStack);

        setBoosting(shouldBoost);
    }

    private static void setBoosting(boolean value) {
        if (boosting == value) {
            return;
        }

        boosting = value;
        PacketDistributor.sendToServer(new MajesticElytraBoostPayload(value));
    }
}
