package org.enigmatic_legacy.util;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Tooltip 客户端状态安全访问工具。
 *
 * <p>物品类属于 common 代码，专用服务端启动时也会加载这些类。
 * 如果 common 物品类直接引用 {@code Minecraft}、{@code LocalPlayer} 或 {@code Screen}，
 * 服务端类加载阶段就可能触发 NeoForge 的 DistCleaner 并崩溃。
 * 因此这里用字符串反射访问客户端类，并且在服务端直接返回默认值。</p>
 */
public final class ClientTooltipState {
    private ClientTooltipState() {
    }

    /**
     * 当前客户端是否按住 Shift。
     *
     * <p>服务端永远返回 false；实际 tooltip 只在客户端渲染，
     * 但服务端仍可能为了注册物品而加载方法所在类，所以不能静态引用 Screen。</p>
     */
    public static boolean isShiftDown() {
        if (!FMLEnvironment.dist.isClient()) {
            return false;
        }

        try {
            Class<?> screenClass = Class.forName("net.minecraft.client.gui.screens.Screen");
            return (Boolean) screenClass.getMethod("hasShiftDown").invoke(null);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    /**
     * 获取当前客户端玩家。
     *
     * <p>这里同样避免直接引用 Minecraft / LocalPlayer。
     * 暗夜契约的 tooltip 只需要用玩家计算当前暗度；如果拿不到客户端玩家，
     * 调用方按最低倍率显示即可。</p>
     */
    public static Player getClientPlayer() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }

        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object minecraft = minecraftClass.getMethod("getInstance").invoke(null);
            Object player = minecraftClass.getField("player").get(minecraft);
            return player instanceof Player typedPlayer ? typedPlayer : null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }
}
