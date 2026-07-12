package org.enigmatic_legacy.item.items.scroll;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 万钧之护卷轴 / Scroll of Thunder Embrace。
 *
 * <p>该物品复刻自 Enigmatic Addons 的 thunder_scroll，并按当前项目的
 * NeoForge 1.21.1 + Curios 写法重写。它属于“七咒受限卷轴”：只有真正佩戴
 * 七咒之戒的玩家才能装备并触发效果。</p>
 *
 * <p>核心效果分为四部分：</p>
 * <p>1. 通过 Curios 属性让玩家横扫攻击至少拥有 100% 横扫伤害比例；</p>
 * <p>2. 玩家造成近战伤害时，根据目标护甲值提高最终伤害，相当于部分穿透护甲；</p>
 * <p>3. 用支持横扫的武器命中目标时积累“电荷”，电荷过高后召唤不会点火的闪电；</p>
 * <p>4. 佩戴者受到闪电伤害时减半，并完全免疫本卷轴生成的无害闪电。</p>
 *
 * <p>为了保持类职责清晰，本类只负责物品属性、装备限制、tooltip 和静态公式；
 * 真正的战斗、闪电、客户端空挥触发逻辑位于事件类与网络包中。</p>
 */
public class ScrollOfThunderEmbrace extends Item implements ICurioItem {
    /**
     * 当前项目为卷轴类物品定义的 Curios 槽位名。
     * 只有放入 scroll 槽时，Curios 才应把它当成奥秘卷轴处理。
     */
    private static final String SCROLL_SLOT = "scroll";

    /**
     * 写入实体 PersistentData 的电荷键。
     * 被万钧之护卷轴命中的目标会积累这个数值，数值超过阈值后触发闪电。
     */
    public static final String ELECTRIC_CHARGE_TAG = "enigmatic_legacy.thunder_scroll_electric";

    /**
     * 标记卷轴生成的闪电。
     * MixinLightningBolt 会读取该 tag，阻止这类闪电生成火焰；
     * 事件类也会用它判断佩戴者是否应该完全免疫本卷轴的闪电。
     */
    public static final String HARMLESS_THUNDER_TAG = "HarmlessThunder";

    /**
     * 电荷触发闪电的阈值。
     * 数值沿用扩展项目：超过 1200 后生成闪电，然后把剩余电荷折半并保留少量余量。
     */
    public static final int ELECTRIC_THRESHOLD = 1200;

    /**
     * 每次使用支持横扫的武器命中时，电荷的固定增长量。
     */
    public static final int ELECTRIC_BASE_GAIN = 60;

    /**
     * 每次命中时额外随机增长量的上界，实际为 [0, 79]。
     */
    public static final int ELECTRIC_RANDOM_GAIN_BOUND = 80;

    /**
     * 最终伤害转化为电荷的倍率。
     * 扩展项目使用 event.getAmount() * 10，这里保留同样手感。
     */
    public static final float ELECTRIC_DAMAGE_GAIN = 10.0F;

    /**
     * 触发闪电后保留的基础电荷。
     * 避免目标刚被雷劈后完全归零，让连续攻击有更明显的连锁感。
     */
    public static final int ELECTRIC_REMAINING_BONUS = 100;

    /**
     * 触发闪电前检测佩戴者的安全半径。
     * 若附近已经有万钧之护卷轴佩戴者，就不在目标身上直接落雷，避免反复劈到使用者。
     */
    public static final double LIGHTNING_OWNER_SAFE_RADIUS = 2.2D;

    /**
     * Curios 属性修饰符 ID。
     * 1.21 的横扫伤害由 Attributes.SWEEPING_DAMAGE_RATIO 控制；
     * 添加 +1.0 可让横扫比例达到扩展项目“至少 100%”的效果。
     */
    private static final ResourceLocation SWEEPING_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "thunder_scroll_sweeping_damage"
    );

    public ScrollOfThunderEmbrace() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许手持右键直接装备到 Curios 槽位，但实际仍复用 canEquip 的完整限制。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * Curios 装备资格判断。
     *
     * <p>限制条件：</p>
     * <p>1. 必须放入 scroll 槽；</p>
     * <p>2. 装备者必须是玩家；</p>
     * <p>3. 必须通过七咒受限饰品的统一校验；</p>
     * <p>4. 同一名玩家最多只能装备 1 个万钧之护卷轴。</p>
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context == null || !SCROLL_SLOT.equals(context.identifier())) {
            return false;
        }

        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        if (!CursedRingApi.canEquipRestrictedCurio(context, stack)) {
            return false;
        }

        return canEquipThunderScroll(player, context.index());
    }

    /**
     * 检查 scroll 槽位中是否已经存在另一个万钧之护卷轴。
     *
     * <p>Curios 刷新槽位时可能会重新校验当前槽里的同一个物品。
     * 因此如果扫描到的是 currentSlotIndex 对应槽位，需要跳过它，
     * 避免把“自己”误判为重复装备。</p>
     */
    private static boolean canEquipThunderScroll(Player player, int currentSlotIndex) {
        return CuriosLookupApi.getStacksHandler(player, SCROLL_SLOT)
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.THUNDER_SCROLL.get())) {
                            continue;
                        }

                        if (slot == currentSlotIndex) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                })
                .orElse(true);
    }

    /**
     * 给佩戴者提供 100% 横扫伤害比例。
     *
     * <p>旧版扩展通过 mixin 改写 EnchantmentHelper#getSweepingDamageRatio。
     * 1.21 已把横扫比例改成玩家属性 Attributes.SWEEPING_DAMAGE_RATIO，
     * 因此这里直接通过 Curios 属性修饰符实现，更贴合当前版本 API。</p>
     */
    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();

        if (slotContext.entity() instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.THUNDER_SCROLL.get())) {
            attributes.put(
                    Attributes.SWEEPING_DAMAGE_RATIO,
                    new AttributeModifier(
                            SWEEPING_DAMAGE_ID,
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        return attributes;
    }

    /**
     * 根据目标护甲提高伤害。
     *
     * <p>公式沿用扩展项目：
     * factor = 1 - min(0.0375 * armor, 0.75)，最终伤害除以 sqrt(factor)。
     * 护甲越高，factor 越低，伤害提升越明显；最多按 75% 护甲影响上限计算，
     * 防止极端护甲值把伤害放大到失控。</p>
     */
    public static float modifyArmorPiercingDamage(LivingEntity target, float damage) {
        double armor = target.getAttributeValue(Attributes.ARMOR);

        if (armor <= 0.0D || damage <= 0.0F) {
            return damage;
        }

        double factor = 1.0D - Math.min(0.0375D * armor, 0.75D);
        return (float) (damage / Math.sqrt(factor));
    }

    /**
     * Tooltip 文案。
     * Shift 展开时显示完整效果；未按 Shift 时沿用本项目卷轴类的“按住 Shift 查看”风格。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.thunder_scroll.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.thunder_scroll.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.thunder_scroll.3"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.thunder_scroll.4"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.thunder_scroll.cursed_only"));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }
}
