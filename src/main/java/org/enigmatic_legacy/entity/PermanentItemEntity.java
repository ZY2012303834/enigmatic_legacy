package org.enigmatic_legacy.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.SoulCrystal;
import org.enigmatic_legacy.item.items.StorageCrystal;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 原版 Enigmatic Legacy 的 PermanentItemEntity 适配版。
 *
 * <p>超维容器和灵魂水晶在物品栏里仍然是 ItemStack，但掉到世界里时使用这个实体承载。
 * 这样可以避免它们被爆炸、火焰、仙人掌等普通掉落物逻辑摧毁，也能在玩家接触时执行专用取回逻辑。
 */
public class PermanentItemEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(PermanentItemEntity.class, EntityDataSerializers.ITEM_STACK);

    private int age;
    private int pickupDelay;
    private UUID owner;
    private Vec3 boundPosition;
    public final float hoverStart = (float) (Math.random() * Math.PI * 2.0D);

    public PermanentItemEntity(EntityType<? extends PermanentItemEntity> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setNoGravity(true);
    }

    public PermanentItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        this(ModEntities.PERMANENT_ITEM.get(), level);
        double safeY = y <= level.getMinBuildHeight() ? level.getMinBuildHeight() + 8.0D : y;
        this.setPos(x, safeY, z);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.boundPosition = new Vec3(x, safeY, z);
        this.setItem(stack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        ItemStack stack = this.getItem();

        if (stack.isEmpty()) {
            this.discard();
            return;
        }

        // 原版实体会被固定在生成点，防止水流、碰撞或其他实体逻辑把容器挤走。
        if (!this.level().isClientSide && this.boundPosition != null && !this.position().equals(this.boundPosition)) {
            this.teleportTo(this.boundPosition.x, this.boundPosition.y, this.boundPosition.z);
        }

        super.tick();

        if (this.pickupDelay > 0 && this.pickupDelay != Short.MAX_VALUE) {
            this.pickupDelay--;
        }

        this.setDeltaMovement(Vec3.ZERO);
        this.setPortalCooldown();
        this.age++;

        if (this.level().isClientSide && this.age % 2 == 0) {
            this.level().addParticle(
                    ParticleTypes.PORTAL,
                    this.getX(),
                    this.getY() + this.getBbHeight() * 0.5D,
                    this.getZ(),
                    (this.random.nextDouble() - 0.5D) * 2.0D,
                    (this.random.nextDouble() - 0.5D) * 2.0D,
                    (this.random.nextDouble() - 0.5D) * 2.0D
            );
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Permanent item entities ignore normal damage sources.
        return false;
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public Entity changeDimension(ServerLevel destination) {
        return null;
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Age", (short) this.age);
        tag.putShort("PickupDelay", (short) this.pickupDelay);

        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
        }

        if (this.boundPosition != null) {
            tag.putDouble("BoundX", this.boundPosition.x);
            tag.putDouble("BoundY", this.boundPosition.y);
            tag.putDouble("BoundZ", this.boundPosition.z);
        }

        ItemStack stack = this.getItem();
        if (!stack.isEmpty()) {
            tag.put("Item", stack.save(this.registryAccess()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getShort("Age");
        this.pickupDelay = tag.getShort("PickupDelay");

        if (tag.hasUUID("Owner")) {
            this.owner = tag.getUUID("Owner");
        }

        if (tag.contains("BoundX") && tag.contains("BoundY") && tag.contains("BoundZ")) {
            this.boundPosition = new Vec3(tag.getDouble("BoundX"), tag.getDouble("BoundY"), tag.getDouble("BoundZ"));
        }

        if (tag.contains("Item")) {
            this.setItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Item")));
        }

        if (this.getItem().isEmpty()) {
            this.discard();
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (this.level().isClientSide || this.pickupDelay > 0) {
            return;
        }

        ItemStack stack = this.getItem();
        Item item = stack.getItem();
        boolean isOwner = this.owner == null || player.getUUID().equals(this.owner);

        if (item instanceof StorageCrystal storageCrystal) {
            // 超维容器只允许死亡者自己取回，避免其他玩家拿走其中保存的背包和经验。
            if (!isOwner) {
                return;
            }

            storageCrystal.retrieveDropsFromCrystal(stack, player);
            this.finishPickup(player, item, 1);
            return;
        }

        if (item instanceof SoulCrystal soulCrystal) {
            if (!SoulCrystal.hasOwner(stack)) {
                this.finishPickup(player, item, 1);
                return;
            }

            if (!isOwner || !soulCrystal.retrieveSoulFromCrystal(player, stack)) {
                return;
            }

            this.finishPickup(player, item, 1);
            return;
        }

        // 非专用物品保留接近普通掉落物的取回逻辑，便于后续复用这个实体。
        int count = stack.getCount();
        if (isOwner && player.getInventory().add(stack)) {
            this.finishPickup(player, item, count);
        }
    }

    private void finishPickup(Player player, Item item, int count) {
        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
        player.take(this, count);
        player.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
        this.discard();
        this.setItem(ItemStack.EMPTY);
    }

    public ItemStack getItem() {
        return this.getEntityData().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(ITEM, stack);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner;
    }

    public void setOwnerId(@Nullable UUID owner) {
        this.owner = owner;
    }

    public int getAge() {
        return this.age;
    }

    public void setDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    @Override
    public Component getName() {
        Component customName = this.getCustomName();
        return customName != null ? customName : Component.translatable(this.getItem().getDescriptionId());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return this.getItem().is(ModItems.STORAGE_CRYSTAL.get()) || this.getItem().is(ModItems.SOUL_CRYSTAL.get());
    }
}
