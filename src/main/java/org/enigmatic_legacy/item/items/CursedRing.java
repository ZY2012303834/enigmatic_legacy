package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        addTooltip(tooltip, "void");

        if (ConfigClient.SHOW_CURIO_SLOT_HINT.get()) {
            tooltip.add(Component.literal("可放入：戒指栏位").withStyle(ChatFormatting.DARK_PURPLE));
        }

        if (Screen.hasShiftDown()) {
            addTooltip(tooltip, "cursedRing3");

            if (ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() == 200) {
                addTooltip(tooltip, "cursedRing4");
            } else {
                addTooltip(tooltip, "cursedRing4_alt", ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() + "%");
            }

            addTooltip(tooltip, "cursedRing5");
            addTooltip(tooltip, "cursedRing6", ConfigCommon.CURSED_RING_ARMOR_DEBUFF.get() + "%");
            addTooltip(tooltip, "cursedRing7", ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() + "%");
            addTooltip(tooltip, "cursedRing8");
            addTooltip(tooltip, "cursedRing9");
            addTooltip(tooltip, "cursedRing10");
            addTooltip(tooltip, "void");
            addTooltip(tooltip, "cursedRing11");
            addTooltip(tooltip, "cursedRing12", ConfigCommon.CURSED_RING_LOOTING_BONUS.get());
            addTooltip(tooltip, "cursedRing13", ConfigCommon.CURSED_RING_FORTUNE_BONUS.get());
            addTooltip(tooltip, "cursedRing14", ConfigCommon.CURSED_RING_EXPERIENCE_BONUS.get() + "%");
            addTooltip(tooltip, "cursedRing15", ConfigCommon.CURSED_RING_ENCHANTING_BONUS.get());
            addTooltip(tooltip, "cursedRing16");
            addTooltip(tooltip, "cursedRing17");
            addTooltip(tooltip, "cursedRing18");
            return;
        }

        addTooltip(tooltip, "cursedRingLore1");
        addTooltip(tooltip, "cursedRingLore2");
        addTooltip(tooltip, "cursedRingLore3");
        addTooltip(tooltip, "cursedRingLore4");
        addTooltip(tooltip, "cursedRingLore5");
        addTooltip(tooltip, "cursedRingLore6");
        addTooltip(tooltip, "cursedRingLore7");
        addTooltip(tooltip, "void");
        addTooltip(tooltip, "eternallyBound1");

        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) {
            addTooltip(tooltip, "eternallyBound2_creative");
        } else {
            addTooltip(tooltip, "eternallyBound2");
        }

        addTooltip(tooltip, "void");
        addTooltip(tooltip, "holdShift");
    }

    private static void addTooltip(List<Component> tooltip, String key, Object... args) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy." + key, args));
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
