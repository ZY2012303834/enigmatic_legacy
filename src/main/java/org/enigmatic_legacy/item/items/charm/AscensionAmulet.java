package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 飞升护符 / Amulet of Ascension
 * 按原项目复刻：
 * - 原项目 ID：ascension_amulet
 * - 品质：EPIC
 * - 防火
 * - 最大堆叠 1
 * - 栏位：curios:charm
 * - 效果：同时拥有七种神秘护身符颜色效果
 * 注意：
 * - 原项目飞升护符曾经也参与死亡保存超维容器逻辑；
 * - 但你当前项目已经把灵魂水晶 / 超维容器事件绑定到七咒之戒；
 * - 所以这里不再实现任何死亡保存逻辑。
 */
public class AscensionAmulet extends Item {

    public AscensionAmulet() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
        );
    }

    /**
     * 显示见证者。
     *
     * 如果后续你做自定义合成并继承普通神秘护身符的 Owner，
     * 这里可以直接显示。
     */
    public static String getOwner(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return "";
        }

        CompoundTag tag = data.copyTag();
        return tag.contains(EnigmaticAmulet.OWNER_TAG)
                ? tag.getString(EnigmaticAmulet.OWNER_TAG)
                : "";
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ascension_amulet.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ascension_amulet.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ascension_amulet.3")
                .withStyle(ChatFormatting.GRAY));

        String owner = getOwner(stack);

        if (!owner.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.owner", owner)
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("curios.modifiers.charm")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ascension_amulet.no_vessel")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}