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
 * 饕餮之锅客户端模型属性注册。
 * 修复内容：
 * - 给自定义锅注册 minecraft:blocking 属性；
 * - 玩家正在使用饕餮之锅时返回 1.0；
 * - 没有使用时返回 0.0；
 * - 模型 JSON 根据这个属性切换 idle / blocking 模型。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class VoraciousPanClientEvents {

    private VoraciousPanClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.VORACIOUS_PAN.get(),

                /*
                 * 必须是 minecraft:blocking。
                 * ItemGenerator 里的 override 也要用 mcLoc("blocking")。
                 */
                ResourceLocation.withDefaultNamespace("blocking"),

                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }

                    /*
                     * 关键：
                     * 只有正在使用的物品就是这口锅时，才进入 blocking 模型。
                     */
                    return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                }
        ));
    }
}