package org.enigmatic_legacy.api;

import net.minecraft.world.item.Item;
import org.enigmatic_legacy.item.ModItems;

/**
 * 遗物物品集合统一入口。
 * <p>
 * 数据生成器、附魔规则、战利品规则等系统经常需要同一批“遗物”列表。
 * 这里集中维护，避免同一批物品在多个文件中重复书写导致漏项。
 */
public final class RelicItemApi {
    private RelicItemApi() {
    }

    /**
     * 能够在铁砧中敲入诅咒附魔的遗物。
     */
    public static Item[] curseEnchantableRelics() {
        return new Item[]{
                ModItems.CURSED_RING.get(),
                ModItems.IRON_RING.get(),
                ModItems.EXQUISITE_RING.get(),
                ModItems.ENDER_RING.get(),
                ModItems.MAGNET_RING.get(),
                ModItems.DISLOCATION_RING.get(),
                ModItems.MAGIC_QUARTZ_RING.get(),
                ModItems.EARTH_PROMISE.get(),
                ModItems.TWISTED_MIRROR.get(),
                ModItems.UNHOLY_GRAIL.get(),
                ModItems.GUARDIAN_HEART.get(),
                ModItems.ABYSSAL_HEART.get(),
                ModItems.EXTRADIMENSIONAL_EYE.get(),
                ModItems.WAYFINDER_OF_THE_DAMNED.get(),
                ModItems.ANIMAL_GUIDEBOOK.get(),
                ModItems.HUNTER_GUIDEBOOK.get(),
                ModItems.ODE_TO_LIVING.get(),
                ModItems.ANTIQUE_BOOK_BAG.get(),
                ModItems.UNWITNESSED_AMULET.get(),
                ModItems.ENIGMATIC_AMULET_RED.get(),
                ModItems.ENIGMATIC_AMULET_AQUA.get(),
                ModItems.ENIGMATIC_AMULET_VIOLET.get(),
                ModItems.ENIGMATIC_AMULET_MAGENTA.get(),
                ModItems.ENIGMATIC_AMULET_GREEN.get(),
                ModItems.ENIGMATIC_AMULET_BLACK.get(),
                ModItems.ENIGMATIC_AMULET_BLUE.get(),
                ModItems.ASCENSION_AMULET.get(),
                ModItems.ELDRITCH_AMULET.get(),
                ModItems.MONSTER_CHARM.get(),
                ModItems.TREASURE_HUNTER_CHARM.get(),
                ModItems.BLOODSTAINED_VALOR_EMBLEM.get(),
                ModItems.ENCHANTER_PEARL.get(),
                ModItems.ENIGMATIC_EYE.get(),
                ModItems.SCORCHED_CHARM.get(),
                ModItems.THE_ACKNOWLEDGMENT.get(),
                ModItems.THE_TWIST.get(),
                ModItems.THE_INFINITUM.get(),
                ModItems.MAJESTIC_ELYTRA.get(),
                ModItems.BULWARK_OF_BLAZING_PRIDE.get(),
                ModItems.VORACIOUS_PAN.get(),
                ModItems.GOLEM_HEART.get(),
                ModItems.ANGEL_BLESSING.get(),
                ModItems.OCEAN_STONE.get(),
                ModItems.BLAZING_CORE.get(),
                ModItems.EYE_OF_NEBULA.get(),
                ModItems.VOID_PEARL.get(),
                ModItems.THE_CUBE.get(),
                ModItems.HEART_OF_CREATION.get(),
                ModItems.XP_SCROLL.get(),
                ModItems.HEAVEN_SCROLL.get(),
                ModItems.CURSED_SCROLL.get(),
                ModItems.FABULOUS_SCROLL.get(),
                ModItems.AVARICE_SCROLL.get(),
                ModItems.STORAGE_CRYSTAL.get(),
                ModItems.ASTRAL_FRUIT.get(),
                ModItems.FORBIDDEN_FRUIT.get()
        };
    }
}
