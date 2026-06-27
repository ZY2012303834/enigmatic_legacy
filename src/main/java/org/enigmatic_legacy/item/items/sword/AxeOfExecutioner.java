package org.enigmatic_legacy.item.items.sword;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
public class AxeOfExecutioner extends SwordItem {

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
     * 注意：
     * 1. 虽然名字仍然叫“斧”，但这里按用户要求改为剑类属性；
     * 2. 继承 SwordItem；
     * 3. 使用 SwordItem.createAttributes；
     * 4. 下界合金 Tier 的基础攻击伤害为 4；
     * 5. SwordItem.createAttributes 第二个参数传 6.0F；
     * 6. 最终攻击伤害显示约为 4 + 6 = 10；
     * 7. 攻击速度传 -2.4F，最终显示为 1.6；
     * 8. 附魔也会通过 ItemTagGenerator 按剑类标签处理。
     */
    public AxeOfExecutioner() {
        super(
                Tiers.NETHERITE,
                new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .attributes(SwordItem.createAttributes(
                                Tiers.NETHERITE,
                                6.0F,
                                -2.4F
                        ))
        );
    }

    /**
     * 行刑者之斧的附魔能力。
     * 1.21.1 没有 Item.Properties#enchantable(...)，
     * 所以这里继续使用 1.21.1 的 getEnchantmentValue 方式。
     */
    @Override
    public int getEnchantmentValue() {
        return Tiers.NETHERITE.getEnchantmentValue();
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
     * 生物死亡事件。
     * 本方法负责实现行刑者之斧的斩首效果。
     * 新逻辑：
     * 1. 玩家主手拿着行刑者之斧杀死生物，可以掉落头颅。
     * 2. 非玩家生物主手拿着行刑者之斧杀死生物，也可以掉落头颅。
     * 例如：僵尸、猪灵、骷髅等，只要主手装备了这把斧头并完成击杀，就能触发。
     * 3. 如果被杀死的是玩家，则掉落该玩家自己的玩家头颅。
     * 注意：
     * - 必须是“主手”拿着行刑者之斧。
     * - 背包里有、副手拿着都不会触发。
     * - 只在服务端生成掉落物，避免客户端重复生成或无效生成。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // 获取被杀死的目标。
        LivingEntity target = event.getEntity();

        // 只在服务端执行。
        // 客户端不负责真正生成掉落物。
        if (target.level().isClientSide()) {
            return;
        }

        // 获取造成死亡的实体。
        // 玩家近战击杀时，这里通常是 Player。
        // 生物近战击杀时，这里通常是 Mob / LivingEntity。
        Entity sourceEntity = event.getSource().getEntity();

        // 如果伤害来源不是生物实体，就不触发。
        // 例如：岩浆、火焰、摔落、仙人掌、虚空等环境伤害。
        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }

        // 获取攻击者主手物品。
        // 这里 attacker 可以是玩家，也可以是僵尸、猪灵、骷髅等非玩家生物。
        ItemStack weapon = attacker.getMainHandItem();

        // 必须是主手拿着行刑者之斧才触发。
        // 这样就支持：
        // - 玩家主手拿斧头击杀；
        // - 非玩家生物主手拿斧头击杀。
        if (!(weapon.getItem() instanceof AxeOfExecutioner)) {
            return;
        }

        // 根据被击杀的目标获取对应头颅。
        // 如果目标是玩家，这里会生成玩家头颅。
        // 如果目标是僵尸、骷髅、苦力怕等，则生成对应原版怪物头颅。
        ItemStack head = getHeadFor(target);

        // 如果目标没有对应头颅，则不触发。
        if (head.isEmpty()) {
            return;
        }

        // 获取攻击者主手武器上的抢夺等级。
        // 这里不再限制攻击者必须是玩家，
        // 因为非玩家生物手里的武器也可能带抢夺附魔。
        int lootingLevel = getLootingLevel(attacker, weapon);

        // 计算最终斩首概率。
        // 公式：
        // 基础 15% + 每级抢夺 5%
        float chance = BASE_BEHEADING_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;

        // 防止概率超过 100%。
        chance = Math.min(1.0F, chance);

        // 随机判定是否触发。
        // nextFloat() 会生成 0.0 到 1.0 之间的小数。
        // 如果随机数大于概率，则本次不掉落头颅。
        if (target.getRandom().nextFloat() > chance) {
            return;
        }

        // 创建头颅掉落物实体。
        ItemEntity headEntity = new ItemEntity(
                target.level(),
                target.getX(),
                target.getY(),
                target.getZ(),
                head
        );

        // 设置默认拾取延迟。
        // 这样头颅不会在死亡瞬间被立刻吸走，方便玩家看到掉落。
        headEntity.setDefaultPickUpDelay();

        // 直接把头颅生成到世界中。
        target.level().addFreshEntity(headEntity);
    }

    /**
     * 获取攻击者主手武器上的抢夺附魔等级。
     * 为什么参数使用 LivingEntity：
     * - 玩家是 LivingEntity。
     * - 僵尸、猪灵、骷髅等怪物也是 LivingEntity。
     * - 这样非玩家生物主手拿着行刑者之斧击杀目标时，也能读取武器上的抢夺等级。
     * 为什么不用 event.getLootingLevel()：
     * - NeoForge 1.21.1 的 LivingDropsEvent 没有 getLootingLevel()。
     * - Minecraft 1.21+ 附魔系统改为数据驱动注册对象。
     * - 所以需要从注册表获取 Looting 的 Holder，再从 ItemStack 中读取等级。
     * 返回值：
     * - 没有抢夺：0
     * - 抢夺 I：1
     * - 抢夺 II：2
     * - 抢夺 III：3
     */
    private static int getLootingLevel(LivingEntity attacker, ItemStack weapon) {
        // 从当前世界的附魔注册表里获取“抢夺”附魔。
        Holder<Enchantment> looting = attacker.level()
                .registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.LOOTING);

        // 从武器上读取抢夺附魔等级。
        // 如果没有抢夺，返回 0。
        return Math.max(0, weapon.getEnchantmentLevel(looting));
    }

    /**
     * 根据被击杀目标返回对应头颅物品。
     * 支持内容：
     * 1. 玩家 -> 该玩家自己的玩家头颅。
     * 2. 僵尸类 -> 僵尸头颅。
     * 3. 骷髅类 -> 骷髅头颅。
     * 4. 凋灵骷髅 -> 凋灵骷髅头颅。
     * 5. 苦力怕 -> 苦力怕头颅。
     * 6. 末影龙 -> 龙首。
     * 7. 猪灵类 -> 猪灵头颅。
     * 注意：
     * - 蜘蛛、末影人、女巫、烈焰人等原版没有对应头颅。
     * - 如果后续你想让所有怪物都能掉“自定义头颅”，需要额外做自定义玩家头颅贴图或自定义物品。
     */
    private static ItemStack getHeadFor(LivingEntity entity) {
        // 玩家死亡时，掉落该玩家自己的玩家头颅。
        if (entity instanceof Player player) {
            return createPlayerHead(player);
        }

        // 获取被击杀目标的实体类型。
        EntityType<?> type = entity.getType();

        // 僵尸类怪物统一掉落僵尸头颅。
        if (type == EntityType.ZOMBIE
                || type == EntityType.HUSK
                || type == EntityType.DROWNED) {
            return new ItemStack(Items.ZOMBIE_HEAD);
        }

        // 骷髅类怪物统一掉落骷髅头颅。
        if (type == EntityType.SKELETON
                || type == EntityType.STRAY
                || type == EntityType.BOGGED) {
            return new ItemStack(Items.SKELETON_SKULL);
        }

        // 凋灵骷髅头颅。
        if (type == EntityType.WITHER_SKELETON) {
            return new ItemStack(Items.WITHER_SKELETON_SKULL);
        }

        // 苦力怕头颅。
        if (type == EntityType.CREEPER) {
            return new ItemStack(Items.CREEPER_HEAD);
        }

        // 末影龙龙首。
        if (type == EntityType.ENDER_DRAGON) {
            return new ItemStack(Items.DRAGON_HEAD);
        }

        // 猪灵类怪物统一掉落猪灵头颅。
        if (type == EntityType.PIGLIN
                || type == EntityType.PIGLIN_BRUTE
                || type == EntityType.ZOMBIFIED_PIGLIN) {
            return new ItemStack(Items.PIGLIN_HEAD);
        }

        // 其它生物没有对应原版头颅，不掉落。
        return ItemStack.EMPTY;
    }

    /**
     * 创建指定玩家的玩家头颅。
     * Minecraft 1.21+ 已经不推荐用旧 NBT 写玩家头颅数据。
     * 玩家头颅的拥有者信息现在放在 DataComponents.PROFILE 里。
     * 这里使用：
     * - Items.PLAYER_HEAD 创建玩家头颅物品；
     * - player.getGameProfile() 获取玩家的 UUID、名字和皮肤属性；
     * - new ResolvableProfile(...) 包装玩家资料；
     * - head.set(DataComponents.PROFILE, ...) 写入头颅组件。
     * 这样掉落出来的玩家头颅会对应被击杀玩家。
     */
    private static ItemStack createPlayerHead(Player player) {
        // 创建一个原版玩家头颅。
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        // 写入玩家资料组件。
        // 如果玩家资料里已经包含皮肤属性，头颅会显示对应皮肤。
        head.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));

        // 返回带玩家资料的头颅。
        return head;
    }
}