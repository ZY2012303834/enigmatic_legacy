package org.enigmatic_legacy.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.items.MagnetRing;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 磁力之戒工具类。
 * 这里和 EnderRingHelper 一样，不缓存佩戴状态。
 * 每次调用都实时从 Curios 查询，保证背包打开时装备/摘下戒指后 UI 可以立即变化。
 */
public final class MagnetRingHelper {
    public static final String TOGGLE_COMMAND = "enigmatic_legacy_toggle_magnet_ring";

    private static final String ENABLED_TAG = "MagnetEnabled";

    private MagnetRingHelper() {
    }

    /**
     * 查找当前佩戴的“磁力控制戒指”。
     * 包括：
     * 1. 磁力之戒 magnet_ring；
     * 2. 转位之戒 dislocation_ring；
     * 3. 兼容 super_magnet_ring。
     */
    public static Optional<ItemStack> findEquippedMagnetControlRing(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.findFirstCurio(MagnetRingHelper::isMagnetControlRing)).ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断是否佩戴磁力之戒或转位之戒。
     * 这个方法要在 UI 每帧调用，不能只在 Screen 初始化时调用。
     */
    public static boolean hasMagnetControlRing(Player player) {
        return findEquippedMagnetControlRing(player).isPresent();
    }

    /**
     * 判断某个 ItemStack 是否是磁力按钮可控制的戒指。
     */
    public static boolean isMagnetControlRing(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof MagnetRing) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (!EnigmaticLegacy.MODID.equals(id.getNamespace())) {
            return false;
        }

        String path = id.getPath();

        return path.equals("dislocation_ring")
                || path.equals("super_magnet_ring");
    }

    /**
     * 判断这枚戒指的磁力是否开启。
     * 没有数据时默认开启，兼容旧存档。
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
     * 设置磁力开关状态。
     */
    public static void setMagnetEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = new CompoundTag();

        CustomData oldData = stack.get(DataComponents.CUSTOM_DATA);

        if (oldData != null) {
            tag = oldData.copyTag();
        }

        tag.putBoolean(ENABLED_TAG, enabled);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 切换磁力状态。
     */
    public static boolean toggleMagnet(ItemStack stack) {
        boolean next = !isMagnetEnabled(stack);
        setMagnetEnabled(stack, next);
        return next;
    }

    /**
     * 查找当前佩戴的磁力之戒。
     *
     * 兼容旧调用。
     * 如果后续只需要“磁力之戒或转位之戒”，请使用 findEquippedMagnetControlRing。
     */
    public static Optional<ItemStack> findEquippedMagnetRing(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof MagnetRing))
                .ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断是否佩戴磁力之戒。
     *
     * 兼容旧调用。
     */
    public static boolean hasMagnetRing(Player player) {
        return findEquippedMagnetRing(player).isPresent();
    }
}