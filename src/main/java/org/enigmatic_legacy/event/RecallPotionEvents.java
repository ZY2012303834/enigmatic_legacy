package org.enigmatic_legacy.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import org.enigmatic_legacy.potion.ModPotions;

/**
 * 召回药水事件。
 * 召回药水是瞬时传送药水，不应该存在：
 * - 喷溅型召回药水
 * - 滞留型召回药水
 */
public final class RecallPotionEvents {
    private RecallPotionEvents() {
    }

    @SubscribeEvent
    public static void onPotionBrewPre(PotionBrewEvent.Pre event) {
        ItemStack ingredient = event.getItem(3);

        // 阻止：召回药水 + 火药 -> 喷溅型召回药水
        if (ingredient.is(Items.GUNPOWDER)) {
            for (int slot = 0; slot < 3; slot++) {
                if (isRecallPotion(event.getItem(slot), Items.POTION)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // 阻止：喷溅型召回药水 + 龙息 -> 滞留型召回药水
        if (ingredient.is(Items.DRAGON_BREATH)) {
            for (int slot = 0; slot < 3; slot++) {
                if (isRecallPotion(event.getItem(slot), Items.SPLASH_POTION)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private static boolean isRecallPotion(ItemStack stack, Item expectedItem) {
        if (!stack.is(expectedItem)) {
            return false;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(ModPotions.RECALL);
    }
}