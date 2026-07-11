package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Locale;

/**
 * 无知诅咒卷轴 / Scroll of Ignorance Curse。
 *
 * <p>该物品复刻自 Enigmatic Addons 的 cursed_xp_scroll，并按照当前项目的
 * 1.21.1 + NeoForge + Curios 写法重写。它本质上是“永恒智慧卷轴”的诅咒变体：
 * 装备在 Curios 的 scroll 槽位后，可以吸收玩家经验和附近经验球，也可以把储存经验返还给玩家。</p>
 *
 * <p>和普通经验卷轴不同的是，该卷轴属于七咒限制物品：
 * 只有当前真正佩戴七咒之戒的玩家才能装备并生效。卷轴会把储存经验折算为等级比例，
 * 再根据配置提供攻击伤害、治疗效果和击退抗性加成。玩家死亡时，事件类会清空储存经验，
 * 对应手册文案中的“诅咒会在脆弱时带走一切”。</p>
 */
public class ScrollOfIgnoranceCurse extends Item implements ICurioItem {
    /**
     * 本项目为卷轴类物品定义的 Curios 槽位名。
     * 只有放入 scroll 槽位时，curioTick、装备限制和快捷键逻辑才有意义。
     */
    private static final String SCROLL_SLOT = "scroll";

    /**
     * 物品自定义数据中的键名。
     *
     * <p>当前项目已经迁移到 1.21 DataComponents，因此不直接使用旧版
     * ItemStack#getOrCreateTag，而是把 CompoundTag 包在 DataComponents.CUSTOM_DATA 中保存。</p>
     */
    private static final String STORED_XP_TAG = "storedXP";
    private static final String ACTIVE_TAG = "active";
    private static final String MODE_TAG = "mode";

    /**
     * 吸收模式：启用时持续抽取玩家经验，并收集附近经验球写入卷轴。
     */
    public static final int MODE_ABSORPTION = 0;

    /**
     * 提取模式：启用时持续把卷轴中储存的经验返还给玩家。
     */
    public static final int MODE_EXTRACTION = 1;

    /**
     * 原扩展项目中无知诅咒卷轴的经验球收集范围是普通经验卷轴的 1.5 倍。
     * 当前普通经验卷轴范围为 16，因此这里实际为 24 格。
     */
    private static final double COLLECTION_RANGE = ScrollOfAgelessWisdom.COLLECTION_RANGE * 1.5D;

    /**
     * 每 tick 经验吞吐量的下限。
     * 低等级玩家的“当前等级升级所需经验 / 5”可能过小，因此至少处理 5 点经验。
     */
    private static final int MIN_XP_PORTION = 5;

    /**
     * 击退抗性临时属性修饰符 ID。
     *
     * <p>由于该加成会随卷轴储存经验变化，不能只依赖 Curios 的静态属性回调。
     * 每 tick 先按该 ID 移除旧修饰符，再按当前储存等级添加新修饰符，保证数值实时刷新且不会重复叠加。</p>
     */
    private static final ResourceLocation KNOCKBACK_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "scroll_of_ignorance_curse_knockback_resistance"
    );

    /**
     * 创建不可堆叠、史诗稀有度、抗火的卷轴物品。
     */
    public ScrollOfIgnoranceCurse() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 控制“手持右键直接装备到 Curios 槽位”的行为。
     *
     * <p>普通右键交给 Curios 处理装备；Shift + 右键被本类用来切换吸收/提取模式，
     * 因此这里在玩家按住 Shift 时拒绝 Curios 自动装备，避免一次操作同时切模式和装备。</p>
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        if (player.isShiftKeyDown()) {
            return false;
        }

        return canEquip(context, stack);
    }

    /**
     * Curios 装备资格判断。
     *
     * <p>限制条件：
     * 1. 必须是 scroll 槽位；
     * 2. 必须是玩家；
     * 3. 必须通过七咒限制物品的统一校验；
     * 4. 同一名玩家最多只能装备 1 个无知诅咒卷轴。</p>
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

        return canEquipIgnoranceScroll(player, context.index());
    }

    /**
     * 检查 scroll 槽位中是否已经存在另一个无知诅咒卷轴。
     *
     * <p>Curios 在刷新槽位或同步物品时，可能会用当前槽位里的同一个 ItemStack 重新校验装备资格。
     * 因此如果发现的是 currentSlotIndex 对应的槽位，需要跳过它，避免把“自己”误判为重复装备。</p>
     */
    private static boolean canEquipIgnoranceScroll(Player player, int currentSlotIndex) {
        return CuriosLookupApi.getStacksHandler(player, SCROLL_SLOT)
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.CURSED_XP_SCROLL.get())) {
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
     * 卷轴装备在 Curios 槽位时每 tick 执行的核心逻辑。
     *
     * <p>服务端侧执行：
     * 1. 刷新击退抗性临时属性，使其跟随当前储存经验变化；
     * 2. 如果卷轴未启用，只保留属性刷新，不处理经验吞吐；
     * 3. 提取模式下返还经验；
     * 4. 吸收模式下抽取玩家经验并收集附近经验球。</p>
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        refreshKnockbackResistance(player, stack);

        if (!isActive(stack)) {
            return;
        }

        int experiencePortion = getExperiencePortion(player);

        if (getMode(stack) == MODE_EXTRACTION) {
            extractExperiencePortion(player, stack, experiencePortion);
            return;
        }

        absorbPlayerExperience(player, stack, experiencePortion);
        collectNearbyExperienceOrbs(player, stack);
    }

    /**
     * 卸下卷轴时移除动态击退抗性修饰符。
     *
     * <p>击退抗性不是通过 Curios 静态属性表返回的，而是在 curioTick 中动态刷新。
     * 如果卸下时不主动清理，玩家可能短时间保留最后一次刷新的临时属性。</p>
     */
    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof Player player) {
            removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_ID);
        }

        ICurioItem.super.onUnequip(context, newStack, stack);
    }

    /**
     * 手持右键逻辑。
     *
     * <p>Shift + 右键：切换吸收/提取模式，并返回成功，阻止 Curios 继续尝试装备。
     * 普通右键：返回 PASS，把操作留给 Curios 的“右键装备”流程。</p>
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                toggleMode(player, stack);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * 快捷键触发的启用/停用逻辑。
     *
     * <p>客户端按下卷轴快捷键后会发包到服务端，服务端找到装备中的卷轴并调用该方法。
     * 这里只负责切换 active 标记、显示动作栏提示和播放反馈音效；实际经验处理仍由 curioTick 完成。</p>
     */
    public static void toggleActive(Player player, ItemStack stack) {
        boolean newState = !isActive(stack);
        setActive(stack, newState);

        player.displayClientMessage(
                Component.translatable(
                        newState
                                ? "message.enigmatic_legacy.cursed_xp_scroll.enabled"
                                : "message.enigmatic_legacy.cursed_xp_scroll.disabled"
                ).withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED),
                true
        );

        playUseSound(player, newState ? 1.1F : 0.8F);
    }

    /**
     * Shift + 右键触发的模式切换逻辑。
     *
     * <p>吸收模式和提取模式只影响后续 curioTick 如何处理经验。
     * 切换时不立即转移经验，避免一次右键造成大量经验变化。</p>
     */
    public static void toggleMode(Player player, ItemStack stack) {
        int nextMode = getMode(stack) == MODE_ABSORPTION ? MODE_EXTRACTION : MODE_ABSORPTION;
        setMode(stack, nextMode);

        player.displayClientMessage(
                Component.translatable(
                        nextMode == MODE_ABSORPTION
                                ? "message.enigmatic_legacy.cursed_xp_scroll.mode_absorption"
                                : "message.enigmatic_legacy.cursed_xp_scroll.mode_extraction"
                ).withStyle(ChatFormatting.GOLD),
                true
        );

        playUseSound(player, nextMode == MODE_ABSORPTION ? 1.0F : 1.25F);
    }

    /**
     * 从玩家身上抽取一小份经验并写入卷轴。
     *
     * <p>每 tick 的抽取量由 getExperiencePortion 决定，会随玩家当前等级提高而提高。
     * 如果玩家经验不足，则只抽取当前剩余经验，避免经验值被扣成负数。</p>
     */
    private static void absorbPlayerExperience(Player player, ItemStack stack, int experiencePortion) {
        int experience = ExperienceHelper.getPlayerXP(player);

        if (experience <= 0) {
            return;
        }

        int amount = Math.min(experiencePortion, experience);
        ExperienceHelper.drainPlayerXP(player, amount);
        addStoredExperience(stack, amount);
    }

    /**
     * 收集附近经验球并直接写入卷轴储存经验。
     *
     * <p>这里没有调用 ExperienceOrb#playerTouch，因为原版拾取会把经验先给玩家，
     * 再由卷轴下一 tick 抽走；直接 discard 并累加数值能更稳定地表现“卷轴吞噬经验”的效果。</p>
     */
    private static void collectNearbyExperienceOrbs(Player player, ItemStack stack) {
        Level level = player.level();
        AABB area = player.getBoundingBox().inflate(COLLECTION_RANGE);

        List<ExperienceOrb> orbs = level.getEntitiesOfClass(
                ExperienceOrb.class,
                area,
                orb -> orb.isAlive() && !orb.isRemoved()
        );

        if (orbs.isEmpty()) {
            return;
        }

        long collected = 0L;

        for (ExperienceOrb orb : orbs) {
            collected += orb.getValue();
            orb.discard();
        }

        if (collected > 0L) {
            addStoredExperience(stack, collected);
            playUseSound(player, 1.35F);
        }
    }

    /**
     * 从卷轴中取出一小份经验返还给玩家。
     *
     * <p>提取模式不会一次性倒空卷轴，而是和吸收模式一样按当前等级经验条的一部分逐 tick 处理，
     * 这样手感与原扩展项目一致，也避免瞬间返还大量经验造成显示跳变。</p>
     */
    private static void extractExperiencePortion(Player player, ItemStack stack, int experiencePortion) {
        long stored = getStoredExperience(stack);

        if (stored <= 0L) {
            return;
        }

        int amount = (int) Math.min(experiencePortion, stored);
        setStoredExperience(stack, stored - amount);
        ExperienceHelper.addPlayerXP(player, amount);
    }

    /**
     * 计算每 tick 应处理多少经验。
     *
     * <p>公式复刻原扩展项目：当前等级升到下一等级所需经验的五分之一。
     * 玩家等级超过 100 后额外放大吞吐量，使高等级阶段不会过慢。</p>
     */
    private static int getExperiencePortion(Player player) {
        int level = Math.max(0, player.experienceLevel);
        long levelExperience = getExperienceForLevel(level + 1) - getExperienceForLevel(level);
        int portion = (int) Math.min(Integer.MAX_VALUE, levelExperience / 5);

        if (level > 100) {
            portion *= 1 + level / 100;
        }

        return Math.max(MIN_XP_PORTION, portion);
    }

    /**
     * 把卷轴储存的经验总量反推为等价等级。
     * 该等级只用于显示和加成比例计算，不会直接修改玩家等级。
     */
    public static int getStoredLevel(ItemStack stack) {
        return getLevelForExperience(getStoredExperience(stack));
    }

    /**
     * 计算当前储存等级相对配置上限的比例，范围固定为 0 到 1。
     *
     * <p>例如默认上限为 1000 级，卷轴储存经验等价于 500 级时，返回 0.5。
     * 后续攻击、治疗、击退抗性加成都基于这个比例乘以各自配置上限。</p>
     */
    public static double getLevelModifier(ItemStack stack) {
        int upperLimit = Math.max(1, ConfigCommon.IGNORANCE_SCROLL_XP_LEVEL_UPPER_LIMIT.get());
        return Math.clamp((double) getStoredLevel(stack) / upperLimit, 0.0D, 1.0D);
    }

    /**
     * 当前攻击伤害加成倍率。
     * 返回值是小数倍率，例如 1.0 表示 +100%。
     */
    public static double getAttackDamageBonus(ItemStack stack) {
        return getLevelModifier(stack) * ConfigCommon.IGNORANCE_SCROLL_DAMAGE_BOOST_LIMIT.get() / 100.0D;
    }

    /**
     * 当前治疗效果加成倍率。
     * 返回值是小数倍率，例如 0.5 表示 +50%。
     */
    public static double getHealingBonus(ItemStack stack) {
        return getLevelModifier(stack) * ConfigCommon.IGNORANCE_SCROLL_HEAL_BOOST_LIMIT.get() / 100.0D;
    }

    /**
     * 当前击退抗性加成值。
     *
     * <p>击退抗性在原版属性中通常按 0.0 到 1.0 表示 0% 到 100%。
     * 这里延续扩展项目配置，允许默认上限为 160%，最终以 ADD_VALUE 写入属性。</p>
     */
    public static double getKnockbackResistanceBonus(ItemStack stack) {
        return getLevelModifier(stack) * ConfigCommon.IGNORANCE_SCROLL_KNOCKBACK_RESISTANCE_LIMIT.get() / 100.0D;
    }

    /**
     * 清空储存经验。
     * 目前由死亡事件调用，表现为“死亡时诅咒吞掉卷轴中全部经验”。
     */
    public static void clearStoredExperience(ItemStack stack) {
        setStoredExperience(stack, 0L);
    }

    /**
     * 根据经验总量反推原版等级。
     *
     * <p>这里使用二分查找，而不是逐级递增，避免卷轴储存大量经验时 tooltip 或事件计算变慢。
     * high 上限设置为 1,000,000 级，已经远超正常游戏范围。</p>
     */
    private static int getLevelForExperience(long experience) {
        if (experience <= 0L) {
            return 0;
        }

        int low = 0;
        int high = 1;

        while (high < 1_000_000 && getExperienceForLevel(high) <= experience) {
            high *= 2;
        }

        while (low + 1 < high) {
            int middle = low + (high - low) / 2;

            if (getExperienceForLevel(middle) <= experience) {
                low = middle;
            } else {
                high = middle;
            }
        }

        return low;
    }

    /**
     * 原版玩家等级到累计经验的换算公式。
     *
     * <p>返回 long 是为了避免高等级反推时 int 溢出。
     * 正常游戏中经验量远低于 long 上限，但卷轴允许长期储存，因此这里保守处理。</p>
     */
    private static long getExperienceForLevel(int level) {
        if (level <= 0) {
            return 0L;
        }

        if (level < 17) {
            return (long) level * level + 6L * level;
        }

        if (level < 32) {
            return (long) (2.5D * level * level - 40.5D * level + 360.0D);
        }

        return (long) (4.5D * level * level - 162.5D * level + 2220.0D);
    }

    /**
     * 刷新玩家的击退抗性加成。
     *
     * <p>流程是先移除旧修饰符，再根据当前储存经验重新添加。
     * 这样即使卷轴每 tick 都在吸收或返还经验，属性面板和实际效果也能及时更新。</p>
     */
    private static void refreshKnockbackResistance(Player player, ItemStack stack) {
        removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_ID);

        if (!CursedRingApi.canUseRestrictedCurio(player, ModItems.CURSED_XP_SCROLL.get())) {
            return;
        }

        double amount = getKnockbackResistanceBonus(stack);

        if (amount <= 0.0D) {
            return;
        }

        AttributeInstance instance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (instance == null) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(
                KNOCKBACK_RESISTANCE_ID,
                amount,
                AttributeModifier.Operation.ADD_VALUE
        ));
    }

    /**
     * 按 ResourceLocation 移除指定属性修饰符。
     * NeoForge 1.21 的 AttributeInstance 支持用 ID 精确移除，适合动态刷新临时属性。
     */
    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
    }

    /**
     * 播放卷轴操作反馈音效。
     * 不同 pitch 用于区分启用、停用、切换模式和吸收经验球。
     */
    private static void playUseSound(Player player, float pitch) {
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                0.6F,
                pitch
        );
    }

    /**
     * 读取卷轴是否处于启用状态。
     * 未写入过数据的全新卷轴默认返回 false。
     */
    public static boolean isActive(ItemStack stack) {
        return getTag(stack).getBoolean(ACTIVE_TAG);
    }

    /**
     * 启用时显示附魔光效。
     * 这是原扩展项目的表现细节，也能让玩家在背包里快速识别当前状态。
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isActive(stack);
    }

    /**
     * 写入启用状态。
     */
    public static void setActive(ItemStack stack, boolean active) {
        CompoundTag tag = getTag(stack);
        tag.putBoolean(ACTIVE_TAG, active);
        setTag(stack, tag);
    }

    /**
     * 读取当前模式。
     * 如果物品没有写入模式数据，默认使用吸收模式，方便新制作出的卷轴直接开始储存经验。
     */
    public static int getMode(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.contains(MODE_TAG) ? tag.getInt(MODE_TAG) : MODE_ABSORPTION;
    }

    /**
     * 写入当前吸收/提取模式。
     */
    public static void setMode(ItemStack stack, int mode) {
        CompoundTag tag = getTag(stack);
        tag.putInt(MODE_TAG, mode);
        setTag(stack, tag);
    }

    /**
     * 读取储存经验。
     * 使用 long 是为了让卷轴可以长期积累，不受 int 上限限制。
     */
    public static long getStoredExperience(ItemStack stack) {
        return Math.max(0L, getTag(stack).getLong(STORED_XP_TAG));
    }

    /**
     * 写入储存经验，并把负数夹到 0。
     */
    public static void setStoredExperience(ItemStack stack, long experience) {
        CompoundTag tag = getTag(stack);
        tag.putLong(STORED_XP_TAG, Math.max(0L, experience));
        setTag(stack, tag);
    }

    /**
     * 增加储存经验。
     *
     * <p>这里额外处理 long 溢出：如果当前值已经接近 Long.MAX_VALUE，
     * 就直接钳制到 Long.MAX_VALUE，而不是让数值回绕成负数。</p>
     */
    public static void addStoredExperience(ItemStack stack, long amount) {
        if (amount <= 0L) {
            return;
        }

        long current = getStoredExperience(stack);
        long next = Long.MAX_VALUE - current < amount ? Long.MAX_VALUE : current + amount;
        setStoredExperience(stack, next);
    }

    /**
     * 从 DataComponents.CUSTOM_DATA 中复制一份 CompoundTag。
     *
     * <p>返回 copy 是为了避免直接修改组件内部数据后没有重新 set 回 ItemStack。
     * 所有写操作都应该通过 setTag 重新包装为 CustomData。</p>
     */
    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    /**
     * 把修改后的 CompoundTag 写回 ItemStack。
     */
    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * tooltip 中使用的百分比格式。
     * Locale.ROOT 避免部分系统区域设置把小数点格式化成逗号。
     */
    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.1f%%", value * 100.0D);
    }

    /**
     * 客户端 tooltip。
     *
     * <p>显示内容包括：
     * 1. 当前储存经验和等价等级；
     * 2. 启用状态；
     * 3. 吸收/提取模式；
     * 4. 当前三项加成；
     * 5. 操作说明和七咒限制说明。</p>
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        long stored = getStoredExperience(stack);
        boolean active = isActive(stack);
        int mode = getMode(stack);

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.cursed_xp_scroll.stored",
                SpellstoneTooltip.number(stored),
                SpellstoneTooltip.number(getStoredLevel(stack))
        ).withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable(
                active
                        ? "tooltip.enigmatic_legacy.cursed_xp_scroll.active"
                        : "tooltip.enigmatic_legacy.cursed_xp_scroll.inactive"
        ).withStyle(active ? ChatFormatting.DARK_PURPLE : ChatFormatting.RED));

        tooltip.add(Component.translatable(
                mode == MODE_ABSORPTION
                        ? "tooltip.enigmatic_legacy.cursed_xp_scroll.mode_absorption"
                        : "tooltip.enigmatic_legacy.cursed_xp_scroll.mode_extraction"
        ).withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_xp_scroll.current.damage",
                SpellstoneTooltip.percent("+" + formatPercent(getAttackDamageBonus(stack)))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_xp_scroll.current.healing",
                SpellstoneTooltip.percent("+" + formatPercent(getHealingBonus(stack)))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_xp_scroll.current.knockback",
                SpellstoneTooltip.percent("+" + formatPercent(getKnockbackResistanceBonus(stack)))
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_xp_scroll.usage.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_xp_scroll.usage.2"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_xp_scroll.usage.3",
                SpellstoneTooltip.number((int) COLLECTION_RANGE)
        ));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_xp_scroll.cursed_only"));
    }
}
