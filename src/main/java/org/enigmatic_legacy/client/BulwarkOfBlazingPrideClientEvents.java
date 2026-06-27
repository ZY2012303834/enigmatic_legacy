package org.enigmatic_legacy.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

/**
 * 烈焰之傲壁垒客户端模型属性。
 * 作用：
 * - 给自定义盾牌注册 minecraft:blocking 属性；
 * - 玩家正在使用烈焰之傲壁垒时返回 1.0；
 * - 模型 JSON 根据这个值切换到 infernal_shield_blocking；
 * - 修复“举盾时仍然只是普通物品拿在手上”的问题。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class BulwarkOfBlazingPrideClientEvents {

    private BulwarkOfBlazingPrideClientEvents() {
    }

    /**
     * 注册 blocking 属性。
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.BULWARK_OF_BLAZING_PRIDE.get(),

                // 必须使用 minecraft:blocking，因为模型 override 检查的就是这个 key。
                ResourceLocation.withDefaultNamespace("blocking"),

                // 正在使用本物品时返回 1.0，否则返回 0.0。
                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }

                    return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                }
        ));
    }
}