package org.enigmatic_legacy.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 玩家友方实体判断工具。
 *
 * <p>虚空珍珠、轻蔑之约这类范围压制效果不能只排除玩家本人；
 * 被玩家驯服或制造出来的实体也应被视为玩家阵营的一部分，否则会出现
 * 玩家自己的宠物、女仆、傀儡被持续扣血或反复套负面效果的问题。</p>
 *
 * <p>这里不直接依赖具体第三方模组类，原因有两点：
 * 1. 这些模组不是本项目的编译期依赖，直接引用会导致未安装时崩溃；
 * 2. 不同模组对“主人”的 API 命名不完全一致。
 * 因此本类优先使用原版接口，再用反射兜底识别常见 owner/master/summoner 方法。</p>
 */
public final class OwnedEntityHelper {
    /**
     * 递归追溯主人链的最大深度。
     *
     * <p>部分傀儡模组允许“傀儡装配傀儡”，新傀儡的直接主人可能不是玩家，
     * 而是另一个已经属于玩家的傀儡。限制深度可以兼容这种链式归属，
     * 同时避免异常模组数据形成循环后造成无限递归。</p>
     */
    private static final int MAX_OWNER_LOOKUP_DEPTH = 4;

    /**
     * 常见的“返回主人实体”的公开方法名。
     *
     * <p>反射只在原版 OwnableEntity / TamableAnimal / IronGolem 判断失败后使用，
     * 用来兼容女仆、召唤物、傀儡类模组常见的公开 API。</p>
     */
    private static final Set<String> ENTITY_OWNER_METHODS = Set.of(
            "getOwner",
            "getTrueOwner",
            "getOwnerEntity",
            "getOwningEntity",
            "getMaster",
            "getSummoner",
            "getCaster"
    );

    /**
     * 常见的“返回主人 UUID”的公开方法名。
     *
     * <p>UUID 只能可靠判断是否属于当前效果来源玩家；如果主人实体未加载，
     * 无法仅凭 UUID 判断它一定是玩家。因此这里不会把任意非空 UUID 都当作玩家友方。</p>
     */
    private static final Set<String> UUID_OWNER_METHODS = Set.of(
            "getOwnerUUID",
            "getOwnerId",
            "getOwnerUniqueId",
            "getTameOwnerUUID",
            "getMasterUUID",
            "getSummonerUUID",
            "getCasterUUID"
    );

    private OwnedEntityHelper() {
    }

    /**
     * 判断目标是否应被当前玩家的敌意范围效果跳过。
     *
     * <p>覆盖范围：
     * 1. 原版及继承 TamableAnimal 的已驯服生物，包括东百女仆这类女仆实体；
     * 2. 玩家制造的原版铁傀儡；
     * 3. 实现 OwnableEntity 的模组实体；
     * 4. 通过常见 owner/master/summoner 方法公开主人的模组实体；
     * 5. 主人链最终指向玩家的装配型傀儡。</p>
     */
    public static boolean isProtectedPlayerOwnedAlly(Player effectSource, LivingEntity target) {
        return isProtectedPlayerOwnedAlly(effectSource, target, 0);
    }

    private static boolean isProtectedPlayerOwnedAlly(Player effectSource, LivingEntity target, int depth) {
        if (effectSource == null || target == null || target == effectSource) {
            return false;
        }

        /*
         * 原版已驯服生物直接跳过。
         * 东百女仆的 EntityMaid 继承 TamableAnimal，所以女仆也会被这条覆盖。
         */
        if (target instanceof TamableAnimal tamable && tamable.isTame()) {
            return true;
        }

        /*
         * 原版铁傀儡没有具体主人 UUID，但 isPlayerCreated 能区分玩家制造与村庄自然生成。
         * 玩家制造的铁傀儡按“玩家的傀儡”处理，避免被范围压制误伤。
         */
        if (target instanceof IronGolem golem && golem.isPlayerCreated()) {
            return true;
        }

        /*
         * OwnableEntity 是原版“可拥有实体”接口。
         * 如果直接主人是玩家，说明它属于玩家阵营；如果直接主人是另一个生物，
         * 则继续向上追溯，兼容“傀儡装配的傀儡”这类链式归属。
         */
        if (target instanceof OwnableEntity ownable
                && isProtectedOwnerEntity(effectSource, ownable.getOwner(), depth)) {
            return true;
        }

        /*
         * 兼容未实现原版 OwnableEntity、但提供 getOwner/getMaster/getSummoner 等公开方法的模组实体。
         */
        if (isProtectedReflectiveOwnerEntity(effectSource, target, depth)) {
            return true;
        }

        /*
         * 部分实体只暴露主人 UUID。这里仅在 UUID 等于当前效果来源玩家时跳过，
         * 避免把无法确认身份的任意 UUID 所有者都误判为玩家友方。
         */
        UUID ownerId = findReflectiveOwnerUuid(target);
        return effectSource.getUUID().equals(ownerId);
    }

    private static boolean isProtectedOwnerEntity(Player effectSource, Entity owner, int depth) {
        if (owner == null || owner == effectSource) {
            return owner == effectSource;
        }

        if (owner instanceof Player) {
            return true;
        }

        if (depth >= MAX_OWNER_LOOKUP_DEPTH || !(owner instanceof LivingEntity livingOwner)) {
            return false;
        }

        return isProtectedPlayerOwnedAlly(effectSource, livingOwner, depth + 1);
    }

    private static boolean isProtectedReflectiveOwnerEntity(Player effectSource, LivingEntity target, int depth) {
        for (Method method : target.getClass().getMethods()) {
            if (!isUsableOwnerMethod(method, ENTITY_OWNER_METHODS)) {
                continue;
            }

            Object value = invokeOwnerMethod(target, method);

            if (value instanceof Optional<?> optional) {
                value = optional.orElse(null);
            }

            if (value instanceof Entity owner && isProtectedOwnerEntity(effectSource, owner, depth)) {
                return true;
            }
        }

        return false;
    }

    private static UUID findReflectiveOwnerUuid(LivingEntity target) {
        for (Method method : target.getClass().getMethods()) {
            if (!isUsableOwnerMethod(method, UUID_OWNER_METHODS)) {
                continue;
            }

            Object value = invokeOwnerMethod(target, method);

            if (value instanceof Optional<?> optional) {
                value = optional.orElse(null);
            }

            UUID ownerId = asUuid(value);
            if (ownerId != null) {
                return ownerId;
            }
        }

        return null;
    }

    private static boolean isUsableOwnerMethod(Method method, Set<String> allowedNames) {
        return allowedNames.contains(method.getName())
                && method.getParameterCount() == 0
                && !Modifier.isStatic(method.getModifiers());
    }

    private static Object invokeOwnerMethod(LivingEntity target, Method method) {
        try {
            return method.invoke(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            /*
             * 第三方实体的 owner 方法可能依赖特定状态，反射失败不应影响主效果运行。
             * 失败时只跳过该方法，继续尝试其它兼容入口。
             */
            return null;
        }
    }

    private static UUID asUuid(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }

        if (value instanceof String string) {
            try {
                return UUID.fromString(string);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return null;
    }
}
