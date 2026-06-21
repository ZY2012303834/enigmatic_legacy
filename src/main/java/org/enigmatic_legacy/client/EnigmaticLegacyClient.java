package org.enigmatic_legacy.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 客户端专用入口类。
 *
 * <p>这个类只会在客户端环境加载，用来注册客户端独有的内容。
 * 例如：
 * <ul>
 *     <li>物品模型属性，例如扭曲之心的激活状态材质切换；</li>
 *     <li>特殊渲染器；</li>
 *     <li>屏幕、颜色、粒子等只存在于客户端的内容。</li>
 * </ul>
 *
 * <p>不要把通用注册、物品注册、方块注册、配置注册放在这里。
 * 这些内容应该继续放在 {@link EnigmaticLegacy} 主类中。
 *
 * <p>这里使用 {@code dist = Dist.CLIENT}，是为了避免服务端加载
 * {@code net.minecraft.client.*} 相关类导致专用服务器崩溃。
 */
@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
public final class EnigmaticLegacyClient {

    /**
     * 客户端构造函数。
     *
     * <p>NeoForge 会在客户端启动时调用它，并注入当前模组的事件总线。
     * 之后我们把所有客户端 setup 注册到这个 mod event bus 上。
     *
     * @param modEventBus 当前模组的事件总线，只用于注册 mod 生命周期事件
     */
    public EnigmaticLegacyClient(IEventBus modEventBus) {
        // 注册客户端物品属性。
        // 当前用于扭曲之心：根据 ItemStack 里的激活状态切换模型材质。
        modEventBus.addListener(ClientItemProperties::onClientSetup);
    }
}