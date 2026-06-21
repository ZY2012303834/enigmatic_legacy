package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 七咒之戒。
 * 当前实现：
 * 1. Curios 戒指物品
 * 2. 死亡保留
 * 3. 普通玩家无法摘下
 * 4. 基础 Tooltip
 * 具体数值效果由 CursedRingEvents 处理。
 */
public class CursedRing extends Item implements ICurioItem {

    public CursedRing() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.empty());

        if (ConfigClient.SHOW_CURIO_SLOT_HINT.get()) {
            tooltip.add(Component.literal("可放入：戒指栏位").withStyle(ChatFormatting.DARK_PURPLE));
        }

        tooltip.add(Component.literal("永恒绑定").withStyle(ChatFormatting.RED));

        tooltip.add(Component.empty());

        tooltip.add(Component.literal("按住 Shift 查看七咒效果").withStyle(ChatFormatting.DARK_GRAY));

        // 这里使用 hasShiftDown 需要客户端 Screen 类。
        // 为了避免服务端加载客户端类，这里先不直接调用 Screen。
        // 后续可以做 ClientTooltipEvent 版本。
        tooltip.add(Component.literal("受到伤害：")
                .append(Component.literal(ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() + "%").withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("对怪物伤害降低：")
                .append(Component.literal(ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() + "%").withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("护甲减免降低：")
                .append(Component.literal(ConfigCommon.CURSED_RING_ARMOR_DEBUFF.get() + "%").withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("击杀经验倍率：")
                .append(Component.literal(ConfigCommon.CURSED_RING_EXPERIENCE_BONUS.get() + "%").withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * 普通玩家不能摘下七咒之戒。
     * 创造模式玩家允许摘下，方便测试和调试。
     */
    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player) {
            return player.isCreative();
        }

        return false;
    }

    /**
     * 禁止右键直接装备。
     * 后续如需自动装备，由事件系统处理。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    /**
     * 死亡时永远保留。
     */
    @Override
    public ICurio.@NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        return ConfigCommon.CURSED_RING_FORTUNE_BONUS.get();
    }

    @Override
    public int getLootingLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        return ConfigCommon.CURSED_RING_LOOTING_BONUS.get();
    }
}
