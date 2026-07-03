package org.enigmatic_legacy.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinEnchantmentCompatibility {
    @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
    private static void enigmaticLegacy$denyBindingWithEternalBinding(
            Holder<Enchantment> first,
            Holder<Enchantment> second,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (enigmaticLegacy$isBindingAndEternalBindingPair(first, second)) {
            callback.setReturnValue(false);
        }
    }

    @Unique
    private static boolean enigmaticLegacy$isBindingAndEternalBindingPair(
            Holder<Enchantment> first,
            Holder<Enchantment> second
    ) {
        return first.is(Enchantments.BINDING_CURSE) && second.is(ModEnchantments.ETERNAL_BINDING)
                || first.is(ModEnchantments.ETERNAL_BINDING) && second.is(Enchantments.BINDING_CURSE);
    }
}
