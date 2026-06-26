package org.enigmatic_legacy.item.items.sword;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 行刑者之斧 / Axe of Executioner
 * 功能说明：
 * 1. 作为主手武器时，攻击力为 10 点，攻击速度为 1.6。
 * 2. 玩家使用该斧头击杀指定怪物时，有概率掉落对应头颅。
 * 3. 基础斩首概率为 15%。
 * 4. 每一级抢夺附魔额外增加 5% 斩首概率。
 * 概率示例：
 * 无抢夺：15%
 * 抢夺 I：20%
 * 抢夺 II：25%
 * 抢夺 III：30%
 */
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class AxeOfExecutioner extends AxeItem {

    /**
     * 基础斩首概率。
     * 0.15F = 15%
     */
    private static final float BASE_BEHEADING_CHANCE = 0.15F;

    /**
     * 每一级抢夺附魔增加的斩首概率。
     * 0.05F = 5%
     */
    private static final float LOOTING_BONUS_PER_LEVEL = 0.05F;

    /**
     * 行刑者之斧的构造方法。
     * 这里继承 AxeItem，所以它本身就是斧头类型武器。
     * 属性解释：
     * Tiers.NETHERITE：
     * - 使用下界合金工具等级，保证耐久、挖掘等级等属性足够高。
     * stacksTo(1)：
     * - 只能堆叠 1 个。
     * fireResistant()：
     * - 掉进火里或岩浆里不会被烧毁。
     * rarity(Rarity.EPIC)：
     * - 物品名字显示为史诗品质颜色。
     * AxeItem.createAttributes(Tiers.NETHERITE, 5.0F, -2.4F)：
     * - 在 1.21.1 中，最终显示攻击力 = 工具基础攻击力 + 这里传入的攻击伤害加成。
     * - 下界合金工具基础攻击力为 5。
     * - 这里额外给 5。
     * - 所以最终攻击力显示为 10。
     * 攻击速度说明：
     * - 原版属性内部显示方式是 4.0 + attackSpeed。
     * - 这里传入 -2.4F。
     * - 4.0 - 2.4 = 1.6。
     * - 所以最终攻击速度显示为 1.6。
     */
    public AxeOfExecutioner() {
        super(
                Tiers.NETHERITE,
                new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .attributes(AxeItem.createAttributes(
                                Tiers.NETHERITE,
                                5.0F,
                                -2.4F
                        ))
        );
    }

    /**
     * 添加物品提示文本。
     * 也就是鼠标放到物品上时显示的说明。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        // 空行，用来让提示看起来更整齐。
        tooltip.add(SpellstoneTooltip.empty());

        // 第一行：说明基础 15% 斩首概率。
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.axe_of_executioner.1",
                Component.literal("15%").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));

        // 第二行：说明每级抢夺 +5%。
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.axe_of_executioner.2",
                Component.literal("+5%").withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY));
    }

    /**
     * 生物掉落事件。
     * 当 LivingEntity 死亡并生成掉落物时，NeoForge 会触发 LivingDropsEvent。
     * 我们在这里判断：
     * 1. 击杀者是不是玩家；
     * 2. 玩家主手拿的是不是行刑者之斧；
     * 3. 被击杀的生物能不能被斩首；
     * 4. 概率是否触发；
     * 5. 如果触发，就额外添加对应头颅掉落。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // 获取被击杀的生物。
        LivingEntity target = event.getEntity();

        // 只在服务端执行。
        // 掉落物必须由服务端生成，客户端执行会导致逻辑重复或无效。
        if (target.level().isClientSide()) {
            return;
        }

        // 判断伤害来源实体是不是玩家。
        // 如果是岩浆、摔落、怪物、环境伤害等造成死亡，则不触发。
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        // 获取玩家主手物品。
        ItemStack weapon = player.getMainHandItem();

        // 必须是主手拿着行刑者之斧击杀才触发。
        // 副手拿着不触发。
        // 背包里有也不触发。
        if (!(weapon.getItem() instanceof AxeOfExecutioner)) {
            return;
        }

        // 根据被击杀的生物类型，获取应该掉落的头颅。
        ItemStack head = getHeadFor(target);

        // 如果这个生物没有对应头颅，则不触发。
        if (head.isEmpty()) {
            return;
        }

        // 获取玩家主手武器上的抢夺附魔等级。
        // 注意：NeoForge 1.21.1 的 LivingDropsEvent 没有 getLootingLevel()。
        // 1.21+ 附魔是数据驱动注册对象，所以需要先从注册表中拿到 Looting 的 Holder，
        // 再从 ItemStack 上读取该附魔等级。
        int lootingLevel = getLootingLevel(player, weapon);

        // 计算最终斩首概率。
        // 公式：
        // 最终概率 = 基础概率 15% + 抢夺等级 × 5%
        float chance = BASE_BEHEADING_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;

        // 防止概率超过 100%。
        chance = Math.min(1.0F, chance);

        // 随机判定是否触发斩首。
        // nextFloat() 会生成 0.0 到 1.0 之间的随机数。
        // 如果随机数大于斩首概率，则本次不掉落头颅。
        if (target.getRandom().nextFloat() > chance) {
            return;
        }

        // 概率触发成功，向原掉落列表里额外添加一个头颅掉落物。
        event.getDrops().add(new ItemEntity(
                target.level(),
                target.getX(),
                target.getY(),
                target.getZ(),
                head
        ));
    }

    /**
     * 获取武器上的抢夺附魔等级。
     * 为什么不直接用 event.getLootingLevel()：
     * - NeoForge 1.21.1 的 LivingDropsEvent 没有 getLootingLevel() 方法。
     * - Minecraft 1.21+ 的附魔系统改成了数据驱动注册对象。
     * - 所以要先从注册表拿到 Enchantments.LOOTING 对应的 Holder，
     *   然后再从 ItemStack 读取该附魔等级。
     * 返回值：
     * - 没有抢夺：0
     * - 抢夺 I：1
     * - 抢夺 II：2
     * - 抢夺 III：3
     */
    private static int getLootingLevel(Player player, ItemStack weapon) {
        // 从当前世界的注册表中获取“抢夺”附魔 Holder。
        Holder<Enchantment> looting = player.level()
                .registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.LOOTING);

        // 从当前主手武器上读取抢夺等级。
        // 如果武器没有抢夺附魔，这里会返回 0。
        return Math.max(0, weapon.getEnchantmentLevel(looting));
    }

    /**
     * 根据生物类型返回对应的头颅物品。
     * 支持：
     * 僵尸 -> 僵尸头颅
     * 骷髅 -> 骷髅头颅
     * 凋灵骷髅 -> 凋灵骷髅头颅
     * 苦力怕 -> 苦力怕头颅
     * 末影龙 -> 龙首
     * 猪灵 -> 猪灵头颅
     * 如果不想支持猪灵头颅，可以删除 PIGLIN 那一段。
     */
    private static ItemStack getHeadFor(LivingEntity entity) {
        EntityType<?> type = entity.getType();

        // 僵尸头颅
        if (type == EntityType.ZOMBIE) {
            return new ItemStack(Items.ZOMBIE_HEAD);
        }

        // 骷髅头颅
        if (type == EntityType.SKELETON) {
            return new ItemStack(Items.SKELETON_SKULL);
        }

        // 凋灵骷髅头颅
        if (type == EntityType.WITHER_SKELETON) {
            return new ItemStack(Items.WITHER_SKELETON_SKULL);
        }

        // 苦力怕头颅
        if (type == EntityType.CREEPER) {
            return new ItemStack(Items.CREEPER_HEAD);
        }

        // 末影龙龙首
        if (type == EntityType.ENDER_DRAGON) {
            return new ItemStack(Items.DRAGON_HEAD);
        }

        // 猪灵头颅
        // 如果你想严格复刻旧版，可以删除这一段。
        if (type == EntityType.PIGLIN) {
            return new ItemStack(Items.PIGLIN_HEAD);
        }

        // 其它生物不掉落头颅。
        return ItemStack.EMPTY;
    }
}