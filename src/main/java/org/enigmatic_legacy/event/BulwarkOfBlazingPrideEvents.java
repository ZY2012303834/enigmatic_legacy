package org.enigmatic_legacy.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 烈焰之傲壁垒事件。
 * 原项目额外机制：
 * - 如果玩家正在使用烈焰之傲壁垒格挡；
 * - 且伤害来源来自背后或侧后方；
 * - 该伤害会提高 50%。
 * 这个机制对应原项目：
 * “Handler for increasing damage on users of Bulwark of Blazing Pride.”
 */
public final class BulwarkOfBlazingPrideEvents {

    /**
     * 背后受击伤害倍率。
     * 原项目：
     * event.setAmount(event.getAmount() * 1.5F)
     */
    private static final float BACKSTAB_DAMAGE_MULTIPLIER = 1.5F;

    private BulwarkOfBlazingPrideEvents() {
    }

    /**
     * 玩家举盾时，处理背后 / 侧后方攻击额外伤害。
     * 判断逻辑说明：
     * - sourcePos：伤害来源位置；
     * - lookVec：玩家视线方向；
     * - sourceToSelf：从伤害来源指向玩家自己的方向；
     * 如果攻击来自玩家正面：
     * - sourceToSelf 与 lookVec 点积通常小于 0；
     * - 不增加伤害。
     * 如果攻击来自玩家背后 / 侧后方：
     * - sourceToSelf 与 lookVec 点积大于等于 0；
     * - 增加 50% 伤害。
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        if (!player.isUsingItem()) {
            return;
        }

        ItemStack usedStack = player.getUseItem();

        if (!usedStack.is(ModItems.BULWARK_OF_BLAZING_PRIDE.get())) {
            return;
        }

        if (event.getSource().getEntity() == null) {
            return;
        }

        Vec3 sourcePos = event.getSource().getSourcePosition();

        if (sourcePos == null) {
            return;
        }

        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 sourceToSelf = sourcePos.vectorTo(player.position());

        /*
         * 只考虑水平面方向。
         * 这样高低差不会影响“正面 / 背面”的判断。
         */
        sourceToSelf = new Vec3(sourceToSelf.x, 0.0D, sourceToSelf.z);

        if (sourceToSelf.lengthSqr() <= 1.0E-6D) {
            return;
        }

        sourceToSelf = sourceToSelf.normalize();

        /*
         * 原项目判断：
         * if (!(sourceToSelf.dot(lookVec) < 0.0D)) {
         *     damage *= 1.5F;
         * }
         *
         * 这里保持同样逻辑。
         */
        if (!(sourceToSelf.dot(lookVec) < 0.0D)) {
            event.setNewDamage(event.getNewDamage() * BACKSTAB_DAMAGE_MULTIPLIER);
        }
    }
}