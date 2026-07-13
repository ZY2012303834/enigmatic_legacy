package org.enigmatic_legacy.config.common;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 配置列表校验工具。
 *
 * <p>多个配置项都会要求玩家填写实体 ID、效果 ID 等资源名列表。
 * 统一校验入口可以让这些配置的写法保持一致，也能避免每个配置类重复维护一套
 * ResourceLocation 校验逻辑。</p>
 */
public final class ConfigListValidators {
    private ConfigListValidators() {
    }

    /**
     * 校验精确资源 ID 列表。
     *
     * <p>适用于只接受 {@code namespace:path} 的配置，例如 Boss 列表、
     * 虚空珍珠飞行生物列表等。</p>
     */
    public static boolean isValidExactResourceIdList(Object value) {
        if (!(value instanceof List<?> list)) {
            return false;
        }

        for (Object element : list) {
            if (!(element instanceof String string)
                    || ResourceLocation.tryParse(string.trim()) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * 校验实体 ID 列表，并允许 {@code namespace:*} 形式的命名空间通配。
     *
     * <p>目前用于七咒之戒中立生物仇恨黑名单。</p>
     */
    public static boolean isValidEntityIdListWithNamespaceWildcard(Object value) {
        if (!(value instanceof List<?> list)) {
            return false;
        }

        for (Object element : list) {
            if (!(element instanceof String string)) {
                return false;
            }

            String trimmed = string.trim();

            if (trimmed.isEmpty()) {
                return false;
            }

            if (trimmed.endsWith(":*")) {
                String namespace = trimmed.substring(0, trimmed.length() - 2);

                if (!ResourceLocation.isValidNamespace(namespace)) {
                    return false;
                }

                continue;
            }

            if (ResourceLocation.tryParse(trimmed) == null) {
                return false;
            }
        }

        return true;
    }
}
