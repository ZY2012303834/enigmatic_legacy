package org.enigmatic_legacy.mixin;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import org.enigmatic_legacy.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinEnchantmentCompatibility {
    /**
     * 恶意图腾只需要耐久附魔提高恶意能量上限，不应该获得经验修补。
     * <p>
     * 1.21.1 中耐久和经验修补都使用 minecraft:enchantable/durability 作为 supportedItems。
     * 为了让恶意图腾可以获得耐久，物品必须加入这个标签；但这样也会让
     * Enchantment#canEnchant 在经验修补上返回 true。
     * 所以这里在通用可附魔判断末端额外排除“恶意图腾 + 经验修补”的组合，
     * 同时保留同一标签带来的耐久附魔支持。
     */
    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void enigmaticLegacy$denyMendingOnTotemOfMalice(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!callback.getReturnValue() || !stack.is(ModItems.TOTEM_OF_MALICE.get())) {
            return;
        }

        Enchantment enchantment = (Enchantment) (Object) this;

        if (enchantment.description().getContents() instanceof TranslatableContents contents
                && "enchantment.minecraft.mending".equals(contents.getKey())) {
            callback.setReturnValue(false);
        }
    }

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
