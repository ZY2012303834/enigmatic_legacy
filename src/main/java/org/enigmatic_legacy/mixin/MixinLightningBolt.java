package org.enigmatic_legacy.mixin;

import net.minecraft.world.entity.LightningBolt;
import org.enigmatic_legacy.item.items.scroll.ScrollOfThunderEmbrace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 阻止万钧之护卷轴生成的闪电点燃方块。
 *
 * <p>LightningBolt#spawnFire 是私有方法，普通事件无法可靠阻止它放火。
 * 因此给卷轴生成的闪电打上 HarmlessThunder tag，并在这里拦截 spawnFire。</p>
 */
@Mixin(LightningBolt.class)
public class MixinLightningBolt {
    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$cancelHarmlessThunderFire(int extraIgnitions, CallbackInfo callback) {
        LightningBolt lightning = (LightningBolt) (Object) this;

        if (lightning.getTags().contains(ScrollOfThunderEmbrace.HARMLESS_THUNDER_TAG)) {
            callback.cancel();
        }
    }
}
