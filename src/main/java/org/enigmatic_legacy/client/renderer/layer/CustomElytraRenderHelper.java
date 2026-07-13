package org.enigmatic_legacy.client.renderer.layer;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 自定义鞘翅客户端渲染来源选择工具。
 *
 * <p>壮丽鞘翅和混沌之傲各自有独立渲染层，但玩家可能通过 Curios 扩展、
 * 数据包或兼容槽位同时装备多个背饰栏鞘翅。如果每个渲染层都只查找自己的物品，
 * 就会出现多个 ElytraModel 叠在背上的问题。</p>
 *
 * <p>这里把选择规则集中到一个地方：胸甲栏优先；胸甲栏已有任意可飞行鞘翅时，
 * 背饰栏不参与渲染；否则只取 Curios 中第一个可见的本模组自定义鞘翅。
 * 两个渲染层都读取这个唯一结果，从源头避免叠加显示。</p>
 */
final class CustomElytraRenderHelper {
    private CustomElytraRenderHelper() {
    }

    static ItemStack getRenderableCustomElytra(LivingEntity entity) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (isCustomElytra(chestStack)) {
            return chestStack;
        }

        if (!chestStack.isEmpty() && chestStack.canElytraFly(entity)) {
            return ItemStack.EMPTY;
        }

        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findCurios(CustomElytraRenderHelper::isCustomElytra).stream()
                        .filter(result -> result.slotContext().visible())
                        .map(result -> result.stack())
                        .findFirst()
                        .orElse(ItemStack.EMPTY))
                .orElse(ItemStack.EMPTY);
    }

    private static boolean isCustomElytra(ItemStack stack) {
        return stack.is(ModItems.MAJESTIC_ELYTRA.get()) || stack.is(ModItems.CHAOS_ELYTRA.get());
    }
}
