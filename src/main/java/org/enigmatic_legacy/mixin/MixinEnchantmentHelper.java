package org.enigmatic_legacy.mixin;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.enigmatic_legacy.event.EternalBindingEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
    @Inject(method = "has", at = @At("RETURN"), cancellable = true)
    private static void enigmaticLegacy$hasEternalBindingArmorLock(
            ItemStack stack,
            DataComponentType<?> component,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!callback.getReturnValue()
                && component == EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE
                && EternalBindingEvents.hasEternalBinding(stack)) {
            callback.setReturnValue(true);
        }
    }
}
