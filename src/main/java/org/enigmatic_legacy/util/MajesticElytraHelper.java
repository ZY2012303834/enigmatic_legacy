package org.enigmatic_legacy.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

/**
 * 壮丽鞘翅装备查询工具。
 * <p>
 * 原版鞘翅逻辑只会读取胸甲槽；Curios 的 back 背饰槽不会自动参与
 * {@code tryToStartFallFlying} 和 {@code updateFallFlying}。
 * 因此本类统一封装“胸甲槽优先，背饰槽兜底”的查询逻辑，避免客户端助推、
 * 服务端助推和 mixin 各自写一份槽位判断。
 */
public final class MajesticElytraHelper {
    /**
     * Curios 背饰槽标识。
     * <p>
     * 这个名字必须和 CuriosGenerator 里生成的 back 槽，以及
     * {@code data/curios/tags/item/back.json} 的标签对应。
     */
    public static final String BACK_SLOT = "back";

    private MajesticElytraHelper() {
    }

    /**
     * 获取玩家当前真正用于壮丽鞘翅功能的物品栈。
     * <p>
     * 胸甲槽优先：如果玩家把壮丽鞘翅穿在胸甲槽，所有旧逻辑保持不变。
     * 只有胸甲槽没有可用的壮丽鞘翅时，才尝试读取 Curios back 槽。
     */
    public static ItemStack getEquippedStack(LivingEntity entity) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (isUsableMajesticElytra(chestStack)) {
            return chestStack;
        }

        if (canElytraFly(chestStack, entity)) {
            return ItemStack.EMPTY;
        }

        return getBackSlotStack(entity);
    }

    /**
     * 给原版鞘翅判定使用的兜底查询。
     * <p>
     * 如果胸甲槽本来就有可用鞘翅，返回胸甲槽物品，保持原版行为。
     * 如果胸甲槽没有可用鞘翅，但背饰槽里有可用壮丽鞘翅，则返回背饰槽物品。
     * 这样 mixin 只需要把原版对胸甲槽的读取替换成这个方法即可。
     */
    public static ItemStack getChestOrBackElytraStack(LivingEntity entity, EquipmentSlot slot) {
        ItemStack originalStack = entity.getItemBySlot(slot);

        if (slot != EquipmentSlot.CHEST || canElytraFly(originalStack, entity)) {
            return originalStack;
        }

        ItemStack backStack = getBackSlotStack(entity);
        return backStack.isEmpty() ? originalStack : backStack;
    }

    /**
     * 读取 Curios back 槽中的壮丽鞘翅。
     * <p>
     * Curios 槽位数量可能被其他数据包修改，所以这里遍历整个 back 槽处理器，
     * 而不是假定一定只有第 0 格。
     */
    public static ItemStack getBackSlotStack(LivingEntity entity) {
        ItemStack backSlotStack = CuriosLookupApi.getStacksHandler(entity, BACK_SLOT)
                .map(stacksHandler -> {
                    var stacks = stacksHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack stack = stacks.getStackInSlot(slot);

                        if (isUsableMajesticElytra(stack)) {
                            return stack;
                        }
                    }

                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);

        if (!backSlotStack.isEmpty()) {
            return backSlotStack;
        }

        /*
         * 兼容兜底：
         * 有些整合包或数据包会复用其它 Curios 槽位承载“背饰”概念，
         * 或者在登录恢复阶段暂时无法通过固定 back 标识拿到栈处理器。
         * 精确 back 查询失败时，扫描所有 Curios 槽，找到第一件可用的壮丽鞘翅作为功能来源。
         */
        return CuriosLookupApi.findFirstStack(entity, MajesticElytraHelper::isUsableMajesticElytra)
                .orElse(ItemStack.EMPTY);
    }

    /**
     * 判断给定物品栈是否是可用的壮丽鞘翅。
     * <p>
     * 这里复用原版 {@link ElytraItem#isFlyEnabled(ItemStack)}，保证耐久只剩 1 点时
     * 和原版鞘翅一样不能继续起飞或助推。
     */
    public static boolean isUsableMajesticElytra(ItemStack stack) {
        return stack.is(ModItems.MAJESTIC_ELYTRA.get())
                && ElytraItem.isFlyEnabled(stack);
    }

    private static boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return !stack.isEmpty() && stack.canElytraFly(entity);
    }
}
