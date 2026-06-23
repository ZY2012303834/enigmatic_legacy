package org.enigmatic_legacy.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 模组自定义伤害类型。

 * 注意：
 * 1. 这里不是注册表 DeferredRegister；
 * 2. 1.20+ 的 DamageType 是数据驱动；
 * 3. 真正的 JSON 由 DamageTypeGenerator 生成。
 */
public final class ModDamageTypes {
    /**
     * 虚空珍珠黑暗光环伤害。

     * 生成后的数据路径：
     * data/enigmatic_legacy/damage_type/darkness.json
     */
    public static final ResourceKey<DamageType> DARKNESS = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "darkness")
    );

    private ModDamageTypes() {
    }

    /**
     * 创建 darkness 伤害源。
     *
     * @param level  服务端世界
     * @param source 伤害来源实体，这里通常是佩戴虚空珍珠的玩家
     */
    public static DamageSource darkness(ServerLevel level, Entity source) {
        Holder<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DARKNESS);

        return new DamageSource(holder, source);
    }
}