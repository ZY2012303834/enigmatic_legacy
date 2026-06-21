package org.enigmatic_legacy.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.items.MagnetRing;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 磁力之戒工具类。
 * 这里集中处理：
 * 1. 玩家是否佩戴磁力之戒；
 * 2. 磁力之戒是否启用；
 * 3. 切换磁力之戒开关状态。
 * 开关状态保存在磁力之戒 ItemStack 自己的 CustomData 中，
 * 而不是保存在玩家 PersistentData 中。
 * 好处：
 * 1. 每一枚戒指都能保留自己的开关状态；
 * 2. 服务器修改 ItemStack 后，Curios/容器同步时客户端也能拿到状态；
 * 3. 不会出现玩家换戒指后状态混乱的问题。
 */
public final class MagnetRingHelper {
    /**
     * 服务器隐藏命令。
     * 客户端 UI 按钮点击后发送这个命令；
     * 真正的权限检查和开关切换都在服务端完成。
     */
    public static final String TOGGLE_COMMAND = "enigmatic_legacy_toggle_magnet_ring";

    /**
     * 写入 ItemStack CustomData 的键。
     * true  = 磁力开启；
     * false = 磁力关闭。
     * 如果没有这个键，则默认视为 true，避免老存档里的戒指突然失效。
     */
    private static final String ENABLED_TAG = "MagnetEnabled";

    private MagnetRingHelper() {
    }

    /**
     * 查找当前佩戴的“可以控制磁力开关”的戒指。
     *
     * 目前包括：
     * 1. 磁力之戒 magnet_ring；
     * 2. 转位之戒 dislocation_ring；
     * 3. 原项目命名兼容 super_magnet_ring。
     *
     * 这样做的好处：
     * 即使转位之戒类还没写出来，这里也不会因为 import DislocationRing 而编译失败。
     */
    public static Optional<ItemStack> findEquippedMagnetControlRing(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity).ifPresent(handler ->
                handler.findFirstCurio(MagnetRingHelper::isMagnetControlRing)
                        .ifPresent(slotResult -> result.set(slotResult.stack()))
        );

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断某个 ItemStack 是否属于“能显示磁力开关 UI 的戒指”。
     */
    public static boolean isMagnetControlRing(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        // 已实现的磁力之戒，直接用 instanceof 判断。
        if (stack.getItem() instanceof MagnetRing) {
            return true;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (!EnigmaticLegacy.MODID.equals(itemId.getNamespace())) {
            return false;
        }

        String path = itemId.getPath();

        // dislocation_ring 是更直观的项目内命名；
        // super_magnet_ring 是原 Enigmatic Legacy 的旧/原始 ID 风格。
        return path.equals("dislocation_ring")
                || path.equals("super_magnet_ring");
    }

    /**
     * 判断实体是否佩戴了磁力之戒或转位之戒。
     */
    public static boolean hasMagnetControlRing(LivingEntity entity) {
        return findEquippedMagnetControlRing(entity).isPresent();
    }

    /**
     * 查找实体当前佩戴的磁力之戒。
     * 返回 Optional.empty() 表示没有佩戴。
     */
    public static Optional<ItemStack> findEquippedMagnetRing(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof MagnetRing)).ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断实体是否佩戴磁力之戒。
     */
    public static boolean hasMagnetRing(LivingEntity entity) {
        return findEquippedMagnetRing(entity).isPresent();
    }

    /**
     * 判断某个磁力之戒 ItemStack 是否启用。
     * 没有写入状态时默认启用，兼容已有物品。
     */
    public static boolean isMagnetEnabled(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return true;
        }

        CompoundTag tag = data.copyTag();

        if (!tag.contains(ENABLED_TAG)) {
            return true;
        }

        return tag.getBoolean(ENABLED_TAG);
    }

    /**
     * 设置某个磁力之戒 ItemStack 的启用状态。
     */
    public static void setMagnetEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = new CompoundTag();

        CustomData oldData = stack.get(DataComponents.CUSTOM_DATA);

        if (oldData != null) {
            // 保留旧 CustomData，避免以后你给戒指加其它数据时被覆盖。
            tag = oldData.copyTag();
        }

        tag.putBoolean(ENABLED_TAG, enabled);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 切换磁力之戒状态。
     * 返回切换后的新状态。
     */
    public static boolean toggleMagnet(ItemStack stack) {
        boolean next = !isMagnetEnabled(stack);
        setMagnetEnabled(stack, next);
        return next;
    }
}