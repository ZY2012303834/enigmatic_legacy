package org.enigmatic_legacy.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 邪恶精髓相关事件。
 *
 * <p>用于实现原项目中“邪恶精髓不会被爆炸摧毁”的特性。
 */
public final class EvilEssenceEvents {

    private EvilEssenceEvents() {
    }

    /**
     * 当实体检查是否会受到某个伤害源影响时触发。
     *
     * <p>如果实体是掉落物，且掉落物内部的物品是邪恶精髓，
     * 并且当前伤害源属于爆炸伤害，则让该实体对此次伤害无敌。
     */
    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        if (!itemEntity.getItem().is(ModItems.EVIL_ESSENCE.get())) {
            return;
        }

        if (!event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            return;
        }

        event.setInvulnerable(true);
    }
}