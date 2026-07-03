package org.enigmatic_legacy.util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CuriosLookupApi;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 天体果实栏位工具类。
 * 用途：
 * 1. 给玩家永久增加 1 个 Curios 戒指栏位；
 * 2. 使用固定 ResourceLocation，避免同一个效果重复叠加；
 * 3. 配合 AstralFruit 的 ConsumedAstralFruit 标记，实现“只能生效一次”。
 */
public final class AstralFruitSlotHelper {

    /**
     * 天体果实永久戒指栏位修饰 ID。
     * 注意：
     * 这个 ID 必须固定。
     * 如果每次随机生成 ID，就会导致多次食用后重复增加栏位。
     */
    public static final ResourceLocation ASTRAL_FRUIT_RING_SLOT_MODIFIER = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "astral_fruit_ring_slot"
    );

    private AstralFruitSlotHelper() {
    }

    /**
     * 给玩家永久增加 1 个戒指栏位。
     * 返回 true：
     * - Curios 背包存在；
     * - 已成功添加永久 ring 栏位修饰。
     * 返回 false：
     * - 没拿到 Curios 背包；
     * - 不写入已生效标记，避免玩家损失永久效果。
     */
    public static boolean grantPermanentRingSlot(Player player) {
        AtomicBoolean success = new AtomicBoolean(false);

        CuriosLookupApi.getInventory(player).ifPresent(curiosInventory -> {
            /*
             * 先移除同 ID 修饰，再重新添加。
             *
             * 作用：
             * 1. 防止旧版本残留同 ID 修饰时出现异常；
             * 2. 保证最终只有一个 astral_fruit_ring_slot；
             * 3. 即使这个方法被误调用多次，也不会变成 +2、+3。
             */
            curiosInventory.removeSlotModifier("ring", ASTRAL_FRUIT_RING_SLOT_MODIFIER);

            Multimap<String, AttributeModifier> modifiers = LinkedHashMultimap.create();

            modifiers.put(
                    "ring",
                    new AttributeModifier(
                            ASTRAL_FRUIT_RING_SLOT_MODIFIER,
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );

            /*
             * permanent slot modifier 会保存到玩家数据里。
             * 这就是“永久增加栏位”的关键。
             */
            curiosInventory.addPermanentSlotModifiers(modifiers);

            success.set(true);
        });

        return success.get();
    }
}
