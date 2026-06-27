package org.enigmatic_legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.WayfinderOfTheDamned;

/**
 * 被诅咒者的寻路指针客户端模型属性
 * 功能：
 * - 注册 item property：enigmatic_legacy:angle；
 * - 有灵魂水晶目标时，像指南针一样指向目标；
 * - 没有灵魂水晶目标时，像原项目一样随机旋转。
 * 注意：
 * - 这是客户端专用类；
 * - 不能在服务端加载 Minecraft / ItemProperties 这类客户端类；
 * - 所以使用 Dist.CLIENT 限制只在客户端注册。
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class WayfinderClientEvents {

    /**
     * 随机旋转用随机数。
     */
    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * 有目标时的平滑旋转值。
     */
    private static float targetRotation = 0.0F;

    /**
     * 有目标时的旋转速度。
     */
    private static double targetVelocity = 0.0D;

    /**
     * 上一次更新有目标旋转的游戏刻。
     */
    private static long lastTargetUpdateTick = Long.MIN_VALUE;

    /**
     * 没有目标时的随机旋转值。
     */
    private static float randomRotation = 0.0F;

    /**
     * 没有目标时的随机旋转速度。
     */
    private static double randomVelocity = 0.0D;

    /**
     * 上一次更新随机旋转的游戏刻。
     */
    private static long lastRandomUpdateTick = Long.MIN_VALUE;

    private WayfinderClientEvents() {
    }

    /**
     * 客户端初始化时注册物品模型属性。
     * 这个属性会被 item model override 读取：
     * - angle = 0.00 ~ 0.99；
     * - 根据 angle 切换 compass_00 ~ compass_31 这 32 帧贴图。
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.WAYFINDER_OF_THE_DAMNED.get(),
                ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "angle"),
                WayfinderClientEvents::getWayfinderAngle
        ));
    }

    /**
     * 计算寻路指针当前应该显示的角度。
     * 返回值：
     * - 0.0F ~ 1.0F；
     * - 模型 override 会根据这个值选择 32 帧贴图。
     * 逻辑：
     * - 如果物品没有保存目标，则随机旋转；
     * - 如果有目标，则根据玩家朝向和目标坐标计算角度；
     * - 最后加一点平滑处理，让旋转不要太生硬。
     */
    private static float getWayfinderAngle(
            ItemStack stack,
            ClientLevel level,
            LivingEntity entity,
            int seed
    ) {
        ClientLevel actualLevel = level != null ? level : Minecraft.getInstance().level;
        LivingEntity actualEntity = entity != null ? entity : Minecraft.getInstance().player;

        // 没有客户端世界时，无法计算，返回 0。
        if (actualLevel == null) {
            return 0.0F;
        }

        /*
         * 没有保存目标：
         * - 没有找到灵魂水晶；
         * - 没佩戴七咒之戒；
         * - 目标被清除；
         *
         * 此时像原项目一样随机旋转。
         */
        if (!WayfinderOfTheDamned.hasStoredTarget(stack)) {
            return getRandomSpinningAngle(actualLevel, seed);
        }

        /*
         * 如果没有实体视角，也无法根据玩家朝向计算。
         * 这种情况多见于某些 GUI 或特殊渲染场景。
         */
        if (actualEntity == null) {
            return getRandomSpinningAngle(actualLevel, seed);
        }

        BlockPos targetPos = WayfinderOfTheDamned.getStoredTargetPosition(stack);

        /*
         * 计算目标相对玩家的角度。
         *
         * atan2 参数：
         * - Z 差值；
         * - X 差值；
         *
         * 这和原版指南针的方向计算方式一致。
         */
        double targetAngle = Math.atan2(
                (double) targetPos.getZ() - actualEntity.getZ(),
                (double) targetPos.getX() - actualEntity.getX()
        ) / (Math.PI * 2.0D);

        /*
         * 玩家当前水平朝向。
         *
         * getYRot() 是玩家朝向角度；
         * 除以 360 后转为 0~1 区间。
         */
        double playerYaw = Mth.positiveModulo(actualEntity.getYRot() / 360.0D, 1.0D);

        /*
         * 转换为模型 override 需要的 0~1 角度。
         *
         * 0.25D 是指南针模型方向修正值。
         */
        float rawAngle = (float) (0.5D - (playerYaw - 0.25D - targetAngle));
        rawAngle = Mth.positiveModulo(rawAngle, 1.0F);

        return wobbleTowardTarget(actualLevel, rawAngle);
    }

    /**
     * 没有目标时的随机旋转。
     * 这个效果用于复刻：
     * - 没有找到灵魂水晶时；
     * - 指针随机乱转；
     * - 类似原版指南针没有有效目标时的失控表现。
     */
    private static float getRandomSpinningAngle(ClientLevel level, int seed) {
        long gameTime = level.getGameTime();

        if (gameTime != lastRandomUpdateTick) {
            lastRandomUpdateTick = gameTime;

            /*
             * 每 tick 给旋转速度加入一个随机扰动。
             * 数值越大，随机晃动越明显。
             */
            randomVelocity += (RANDOM.nextDouble() - 0.5D) * 0.18D;

            /*
             * 阻尼。
             * 防止速度无限增大。
             */
            randomVelocity *= 0.80D;

            randomRotation = Mth.positiveModulo(
                    (float) (randomRotation + randomVelocity),
                    1.0F
            );
        }

        /*
         * seed 用于让不同渲染实例不要完全同步。
         */
        return Mth.positiveModulo(randomRotation + seed * 0.0007F, 1.0F);
    }

    /**
     * 有目标时的平滑旋转。
     * 如果直接返回 rawAngle，指针会瞬间跳帧。
     * 这里模拟原版指南针的缓动效果。
     */
    private static float wobbleTowardTarget(ClientLevel level, float targetAngle) {
        long gameTime = level.getGameTime();

        if (gameTime != lastTargetUpdateTick) {
            lastTargetUpdateTick = gameTime;

            double delta = targetAngle - targetRotation;

            /*
             * 把角度差限制在 -0.5 ~ 0.5 之间。
             * 这样指针会选择最近方向旋转，而不是绕远路。
             */
            delta = Mth.positiveModulo((float) (delta + 0.5D), 1.0F) - 0.5D;

            /*
             * 加速度 + 阻尼，形成指南针式平滑晃动。
             */
            targetVelocity += delta * 0.10D;
            targetVelocity *= 0.80D;

            targetRotation = Mth.positiveModulo(
                    (float) (targetRotation + targetVelocity),
                    1.0F
            );
        }

        return targetRotation;
    }
}