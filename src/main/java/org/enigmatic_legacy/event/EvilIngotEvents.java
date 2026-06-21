package org.enigmatic_legacy.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 极恶锭相关事件。
 *
 * <p>用于实现原项目中“极恶锭不会被爆炸摧毁”的特性。
 */
public final class EvilIngotEvents {

    private EvilIngotEvents() {
    }

    /**
     * 当实体检查是否会受到某个伤害源影响时触发。
     *
     * <p>如果实体是掉落物，且掉落物内部的物品是极恶锭，
     * 并且当前伤害源属于爆炸伤害，则让该实体对此次伤害无敌。
     */
    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        if (!event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();

        if (!stack.is(ModItems.EVIL_INGOT.get())) {
            return;
        }

        event.setInvulnerable(true);
    }
}