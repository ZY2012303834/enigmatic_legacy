package org.enigmatic_legacy.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Iron's Spells 'n Spellbooks 兼容逻辑。
 *
 * <p>本项目不直接调用铁魔法 API，只根据伤害类型 ID 做弱兼容判断。
 * 这样服务端未安装铁魔法时不会尝试加载铁魔法类。</p>
 */
public final class IronsSpellbooksCompat {
    public static final String MODID = "irons_spellbooks";

    private IronsSpellbooksCompat() {
    }

    /**
     * 判断伤害是否属于铁魔法的法术伤害。
     */
    public static boolean isMagicDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> isMagicDamage(key.location()))
                .orElse(false);
    }

    /**
     * 判断伤害是否属于铁魔法的火焰法术伤害。
     */
    public static boolean isFireSpellDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> isFireSpellDamage(key.location()))
                .orElse(false);
    }

    private static boolean isMagicDamage(ResourceLocation damageType) {
        if (!MODID.equals(damageType.getNamespace())) {
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

    private static boolean isFireSpellDamage(ResourceLocation damageType) {
        if (!MODID.equals(damageType.getNamespace())) {
            return false;
        }

        String path = damageType.getPath();

        return path.contains("fire")
                || path.contains("flame")
                || path.contains("burn")
                || path.contains("pyro");
    }
}
