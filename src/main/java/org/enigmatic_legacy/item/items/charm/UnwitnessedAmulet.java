package org.enigmatic_legacy.item.items.charm;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 无主护身符。
 * 这是神秘护身符的“未激活状态”。
 * 玩家右键后，它会随机转化为七种颜色之一的神秘护身符。
 */
public class UnwitnessedAmulet extends Item {
    public UnwitnessedAmulet() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    /**
     * 右键激活无主护身符。
     * 逻辑：
     * 1. 随机生成一种颜色的神秘护身符。
     * 2. 在服务端写入见证者，也就是玩家名。
     * 3. 返回新的 ItemStack，让手上的无主护身符直接被替换。
     * 注意：
     * 这里不生成超维容器。
     * 超维容器现在只由七咒之戒的死亡逻辑处理。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack result = EnigmaticAmulet.createRandom(player.getRandom());

        if (!level.isClientSide) {
            // 记录是哪名玩家激活了这枚护身符。
            EnigmaticAmulet.setOwner(result, player.getGameProfile().getName());

            // 使用原版音效给激活一个反馈，不额外注册自定义音效。
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.BEACON_ACTIVATE,
                    SoundSource.PLAYERS,
                    0.7F,
                    1.4F
            );
        }

        // sidedSuccess 会在服务端替换物品，在客户端播放右键动画。
        return InteractionResultHolder.sidedSuccess(result, level.isClientSide());
    }

    /**
     * Tooltip 文本。
     * 统一规则：
     * 1. 普通介绍文字为紫色；
     * 2. 不再使用 ChatFormatting.GRAY / GOLD 手写颜色；
     * 3. 使用 List<Component>，避免原始 List 警告。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.unwitnessed_amulet.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.unwitnessed_amulet.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.unwitnessed_amulet.3"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.unwitnessed_amulet.use"));
    }
}
