package org.enigmatic_legacy.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.ring.MagicQuartzRing;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 魔法石英戒指工具类。
 * 用途：
 * 1. 判断玩家是否佩戴魔法石英戒指；
 * 2. 限制同一玩家只能佩戴一个魔法石英戒指；
 * 3. 判断哪些伤害属于魔法石英戒指的减伤范围。
 */
public final class MagicQuartzRingHelper {
    private static final String IRONS_SPELLBOOKS_MODID = "irons_spellbooks";

    private MagicQuartzRingHelper() {
    }

    /**
     * 判断玩家是否佩戴魔法石英戒指。
     */
    public static boolean hasMagicQuartzRing(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findFirstCurio(ModItems.MAGIC_QUARTZ_RING.get()).isPresent())
                .orElse(false);
    }

    /**
     * 判断指定戒指栏位是否允许放入魔法石英戒指。
     * 说明：
     * Curios 的 ring 槽可以有多个，但魔法石英戒指本身只能佩戴一个。
     * 如果检查的是当前已经放着魔法石英戒指的同一个槽位，返回 true，避免 Curios 刷新装备状态时误判。
     */
    public static boolean canEquipMagicQuartzRing(LivingEntity entity, String slotIdentifier, int slotIndex) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.getStacksHandler(slotIdentifier))
                .map(ringHandler -> {
                    for (int slot = 0; slot < ringHandler.getStacks().getSlots(); slot++) {
                        ItemStack stack = ringHandler.getStacks().getStackInSlot(slot);

                        if (!stack.is(ModItems.MAGIC_QUARTZ_RING.get())) {
                            continue;
                        }

                        if (slot == slotIndex) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                })
                .orElse(true);
    }

    /**
     * 判断是否为魔法石英戒指可以减免的伤害。
     * 对应参考项目 QuartzRing 的伤害类型：
     * - MAGIC
     * - WITHER
     * - DRAGON_BREATH
     * - INDIRECT_MAGIC
     */
    public static boolean isMagicQuartzRingDamage(DamageSource source) {
        return source.is(Tags.DamageTypes.IS_MAGIC)
                || source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || isIronsSpellbooksMagicDamage(source);
    }

    public static boolean isIronsSpellbooksMagicDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> isIronsSpellbooksMagicDamage(key.location()))
                .orElse(false);
    }

    public static boolean isIronsSpellbooksFireDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> isIronsSpellbooksFireDamage(key.location()))
                .orElse(false);
    }

    public static boolean isIronsSpellbooksMagicDamage(ResourceLocation damageType) {
        if (!IRONS_SPELLBOOKS_MODID.equals(damageType.getNamespace())) {
            return false;
        }

        String path = damageType.getPath();

        return path.contains("magic")
                || path.contains("spell")
                || path.contains("fire")
                || path.contains("ice")
                || path.contains("lightning")
                || path.contains("blood")
                || path.contains("holy")
                || path.contains("ender")
                || path.contains("evocation")
                || path.contains("poison")
                || path.contains("wither");
    }

    public static boolean isIronsSpellbooksFireDamage(ResourceLocation damageType) {
        if (!IRONS_SPELLBOOKS_MODID.equals(damageType.getNamespace())) {
            return false;
        }

        String path = damageType.getPath();

        return path.contains("fire")
                || path.contains("flame")
                || path.contains("burn")
                || path.contains("pyro");
    }

    /**
     * 应用 20% 魔法伤害减免。
     */
    public static float reduceMagicDamage(float amount) {
        return (float) (amount * (1.0D - MagicQuartzRing.MAGIC_RESISTANCE));
    }
}
