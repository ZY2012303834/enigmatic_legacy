package org.enigmatic_legacy.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * 修补混合物特殊修复配方。
 * 配方逻辑：
 * 任意受损的可损坏物品 + 修补混合物
 * =
 * 满耐久的同一件物品 + 空玻璃瓶返还
 * 为什么必须使用特殊配方：
 * - 普通 JSON 配方只能固定输出一个物品。
 * - 修补混合物需要读取输入物品，然后输出“同一个物品的满耐久副本”。
 * - 还必须保留原物品的附魔、命名、组件、NBT 等数据。
 * 所以这里使用 CustomRecipe。
 */
public class MendingMixtureRepairRecipe extends CustomRecipe {

    public MendingMixtureRepairRecipe(CraftingBookCategory category) {
        super(category);
    }

    /**
     * 判断当前工作台输入是否匹配。
     * 合法输入必须满足：
     * 1. 只有 1 个修补混合物；
     * 2. 只有 1 个受损的可损坏物品；
     * 3. 不能有其它额外物品；
     * 4. 物品必须真的受损，否则不允许浪费修补混合物。
     */
    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        int mixtureCount = 0;
        int damagedItemCount = 0;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            // 空槽跳过。
            if (stack.isEmpty()) {
                continue;
            }

            // 统计修补混合物数量。
            if (stack.is(ModItems.MENDING_MIXTURE.get())) {
                mixtureCount++;
                continue;
            }

            // 判断是否为“受损的可损坏物品”。
            if (isRepairableDamagedItem(stack)) {
                damagedItemCount++;
                continue;
            }

            // 其它任何物品都会让配方无效。
            return false;
        }

        return mixtureCount == 1 && damagedItemCount == 1;
    }

    /**
     * 生成合成结果。
     * 这里会：
     * 1. 找到输入格里的受损物品；
     * 2. 复制一份；
     * 3. 把复制品耐久修满；
     * 4. 返回修复后的物品。
     * 注意：
     * - copy() 会保留物品组件、附魔、名称、NBT 等数据。
     * - setDamageValue(0) 只清空耐久损耗。
     */
    @Override
    public @NotNull ItemStack assemble(
            @NotNull CraftingInput input,
            @NotNull HolderLookup.Provider registries
    ) {
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (isRepairableDamagedItem(stack)) {
                ItemStack repaired = stack.copy();

                // 合成结果只能是 1 个。
                repaired.setCount(1);

                // 0 表示没有耐久损耗，即满耐久。
                repaired.setDamageValue(0);

                return repaired;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * 合成后留在工作台里的物品。
     * 复刻原版说明：
     * - 修补混合物用于修复后，会留下空玻璃瓶。
     * 所以：
     * - 修补混合物所在格子返回 glass_bottle；
     * - 被修复物品所在格子被消耗；
     * - 其它格子为空。
     */
    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (stack.is(ModItems.MENDING_MIXTURE.get())) {
                remainingItems.set(slot, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remainingItems;
    }

    /**
     * 允许任意至少 2 格的合成网格。
     * 背包 2x2 和工作台 3x3 都可以使用。
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    /**
     * 返回该特殊配方对应的序列化器。
     * JSON 类型：
     * enigmatic_legacy:mending_mixture_repair
     */
    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MENDING_MIXTURE_REPAIR.get();
    }

    /**
     * 判断物品是否可以被修补混合物修复。
     * 条件：
     * - 必须是可损坏物品；
     * - 必须已经受损。
     * 这样可以避免：
     * - 修复不可损坏物品；
     * - 对满耐久物品浪费修补混合物。
     */
    private static boolean isRepairableDamagedItem(ItemStack stack) {
        return stack.isDamageableItem() && stack.isDamaged();
    }
}