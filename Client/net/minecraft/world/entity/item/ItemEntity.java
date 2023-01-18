/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.util.List
 *  java.util.Objects
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.item;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemEntity
extends Entity {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final int LIFETIME = 6000;
    private static final int INFINITE_PICKUP_DELAY = Short.MAX_VALUE;
    private static final int INFINITE_LIFETIME = Short.MIN_VALUE;
    private int age;
    private int pickupDelay;
    private int health = 5;
    @Nullable
    private UUID thrower;
    @Nullable
    private UUID owner;
    public final float bobOffs;

    public ItemEntity(EntityType<? extends ItemEntity> $$0, Level $$1) {
        super($$0, $$1);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0f;
        this.setYRot(this.random.nextFloat() * 360.0f);
    }

    public ItemEntity(Level $$0, double $$1, double $$2, double $$3, ItemStack $$4) {
        this($$0, $$1, $$2, $$3, $$4, $$0.random.nextDouble() * 0.2 - 0.1, 0.2, $$0.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(Level $$0, double $$1, double $$2, double $$3, ItemStack $$4, double $$5, double $$6, double $$7) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, $$0);
        this.setPos($$1, $$2, $$3);
        this.setDeltaMovement($$5, $$6, $$7);
        this.setItem($$4);
    }

    private ItemEntity(ItemEntity $$0) {
        super($$0.getType(), $$0.level);
        this.setItem($$0.getItem().copy());
        this.copyPosition($$0);
        this.age = $$0.age;
        this.bobOffs = $$0.bobOffs;
    }

    @Override
    public boolean dampensVibrations() {
        return this.getItem().is(ItemTags.DAMPENS_VIBRATIONS);
    }

    public Entity getThrowingEntity() {
        return (Entity)Util.mapNullable(this.getThrower(), this.level::getPlayerByUUID);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        double $$6;
        int $$5;
        if (this.getItem().isEmpty()) {
            this.discard();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != Short.MAX_VALUE) {
            --this.pickupDelay;
        }
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        Vec3 $$0 = this.getDeltaMovement();
        float $$1 = this.getEyeHeight() - 0.11111111f;
        if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)$$1) {
            this.setUnderwaterMovement();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)$$1) {
            this.setUnderLavaMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        if (this.level.isClientSide) {
            this.noPhysics = false;
        } else {
            boolean bl = this.noPhysics = !this.level.noCollision(this, this.getBoundingBox().deflate(1.0E-7));
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }
        if (!this.onGround || this.getDeltaMovement().horizontalDistanceSqr() > (double)1.0E-5f || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float $$2 = 0.98f;
            if (this.onGround) {
                $$2 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getFriction() * 0.98f;
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply($$2, 0.98, $$2));
            if (this.onGround) {
                Vec3 $$3 = this.getDeltaMovement();
                if ($$3.y < 0.0) {
                    this.setDeltaMovement($$3.multiply(1.0, -0.5, 1.0));
                }
            }
        }
        boolean $$4 = Mth.floor(this.xo) != Mth.floor(this.getX()) || Mth.floor(this.yo) != Mth.floor(this.getY()) || Mth.floor(this.zo) != Mth.floor(this.getZ());
        int n = $$5 = $$4 ? 2 : 40;
        if (this.tickCount % $$5 == 0 && !this.level.isClientSide && this.isMergable()) {
            this.mergeWithNeighbours();
        }
        if (this.age != Short.MIN_VALUE) {
            ++this.age;
        }
        this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
        if (!this.level.isClientSide && ($$6 = this.getDeltaMovement().subtract($$0).lengthSqr()) > 0.01) {
            this.hasImpulse = true;
        }
        if (!this.level.isClientSide && this.age >= 6000) {
            this.discard();
        }
    }

    private void setUnderwaterMovement() {
        Vec3 $$0 = this.getDeltaMovement();
        this.setDeltaMovement($$0.x * (double)0.99f, $$0.y + (double)($$0.y < (double)0.06f ? 5.0E-4f : 0.0f), $$0.z * (double)0.99f);
    }

    private void setUnderLavaMovement() {
        Vec3 $$0 = this.getDeltaMovement();
        this.setDeltaMovement($$0.x * (double)0.95f, $$0.y + (double)($$0.y < (double)0.06f ? 5.0E-4f : 0.0f), $$0.z * (double)0.95f);
    }

    private void mergeWithNeighbours() {
        if (!this.isMergable()) {
            return;
        }
        List $$02 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5, 0.0, 0.5), $$0 -> $$0 != this && $$0.isMergable());
        for (ItemEntity $$1 : $$02) {
            if (!$$1.isMergable()) continue;
            this.tryToMerge($$1);
            if (!this.isRemoved()) continue;
            break;
        }
    }

    private boolean isMergable() {
        ItemStack $$0 = this.getItem();
        return this.isAlive() && this.pickupDelay != Short.MAX_VALUE && this.age != Short.MIN_VALUE && this.age < 6000 && $$0.getCount() < $$0.getMaxStackSize();
    }

    private void tryToMerge(ItemEntity $$0) {
        ItemStack $$1 = this.getItem();
        ItemStack $$2 = $$0.getItem();
        if (!Objects.equals((Object)this.getOwner(), (Object)$$0.getOwner()) || !ItemEntity.areMergable($$1, $$2)) {
            return;
        }
        if ($$2.getCount() < $$1.getCount()) {
            ItemEntity.merge(this, $$1, $$0, $$2);
        } else {
            ItemEntity.merge($$0, $$2, this, $$1);
        }
    }

    public static boolean areMergable(ItemStack $$0, ItemStack $$1) {
        if (!$$1.is($$0.getItem())) {
            return false;
        }
        if ($$1.getCount() + $$0.getCount() > $$1.getMaxStackSize()) {
            return false;
        }
        if ($$1.hasTag() ^ $$0.hasTag()) {
            return false;
        }
        return !$$1.hasTag() || $$1.getTag().equals($$0.getTag());
    }

    public static ItemStack merge(ItemStack $$0, ItemStack $$1, int $$2) {
        int $$3 = Math.min((int)(Math.min((int)$$0.getMaxStackSize(), (int)$$2) - $$0.getCount()), (int)$$1.getCount());
        ItemStack $$4 = $$0.copy();
        $$4.grow($$3);
        $$1.shrink($$3);
        return $$4;
    }

    private static void merge(ItemEntity $$0, ItemStack $$1, ItemStack $$2) {
        ItemStack $$3 = ItemEntity.merge($$1, $$2, 64);
        $$0.setItem($$3);
    }

    private static void merge(ItemEntity $$0, ItemStack $$1, ItemEntity $$2, ItemStack $$3) {
        ItemEntity.merge($$0, $$1, $$3);
        $$0.pickupDelay = Math.max((int)$$0.pickupDelay, (int)$$2.pickupDelay);
        $$0.age = Math.min((int)$$0.age, (int)$$2.age);
        if ($$3.isEmpty()) {
            $$2.discard();
        }
    }

    @Override
    public boolean fireImmune() {
        return this.getItem().getItem().isFireResistant() || super.fireImmune();
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!this.getItem().isEmpty() && this.getItem().is(Items.NETHER_STAR) && $$0.isExplosion()) {
            return false;
        }
        if (!this.getItem().getItem().canBeHurtBy($$0)) {
            return false;
        }
        if (this.level.isClientSide) {
            return true;
        }
        this.markHurt();
        this.health = (int)((float)this.health - $$1);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, $$0.getEntity());
        if (this.health <= 0) {
            this.getItem().onDestroyed(this);
            this.discard();
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putShort("Health", (short)this.health);
        $$0.putShort("Age", (short)this.age);
        $$0.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            $$0.putUUID("Thrower", this.getThrower());
        }
        if (this.getOwner() != null) {
            $$0.putUUID("Owner", this.getOwner());
        }
        if (!this.getItem().isEmpty()) {
            $$0.put("Item", this.getItem().save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        this.health = $$0.getShort("Health");
        this.age = $$0.getShort("Age");
        if ($$0.contains("PickupDelay")) {
            this.pickupDelay = $$0.getShort("PickupDelay");
        }
        if ($$0.hasUUID("Owner")) {
            this.owner = $$0.getUUID("Owner");
        }
        if ($$0.hasUUID("Thrower")) {
            this.thrower = $$0.getUUID("Thrower");
        }
        CompoundTag $$1 = $$0.getCompound("Item");
        this.setItem(ItemStack.of($$1));
        if (this.getItem().isEmpty()) {
            this.discard();
        }
    }

    @Override
    public void playerTouch(Player $$0) {
        if (this.level.isClientSide) {
            return;
        }
        ItemStack $$1 = this.getItem();
        Item $$2 = $$1.getItem();
        int $$3 = $$1.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals((Object)$$0.getUUID())) && $$0.getInventory().add($$1)) {
            $$0.take(this, $$3);
            if ($$1.isEmpty()) {
                this.discard();
                $$1.setCount($$3);
            }
            $$0.awardStat(Stats.ITEM_PICKED_UP.get($$2), $$3);
            $$0.onItemPickup(this);
        }
    }

    @Override
    public Component getName() {
        Component $$0 = this.getCustomName();
        if ($$0 != null) {
            return $$0;
        }
        return Component.translatable(this.getItem().getDescriptionId());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerLevel $$0) {
        Entity $$1 = super.changeDimension($$0);
        if (!this.level.isClientSide && $$1 instanceof ItemEntity) {
            ((ItemEntity)$$1).mergeWithNeighbours();
        }
        return $$1;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack $$0) {
        this.getEntityData().set(DATA_ITEM, $$0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        super.onSyncedDataUpdated($$0);
        if (DATA_ITEM.equals($$0)) {
            this.getItem().setEntityRepresentation(this);
        }
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID $$0) {
        this.owner = $$0;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID $$0) {
        this.thrower = $$0;
    }

    public int getAge() {
        return this.age;
    }

    public void setDefaultPickUpDelay() {
        this.pickupDelay = 10;
    }

    public void setNoPickUpDelay() {
        this.pickupDelay = 0;
    }

    public void setNeverPickUp() {
        this.pickupDelay = Short.MAX_VALUE;
    }

    public void setPickUpDelay(int $$0) {
        this.pickupDelay = $$0;
    }

    public boolean hasPickUpDelay() {
        return this.pickupDelay > 0;
    }

    public void setUnlimitedLifetime() {
        this.age = Short.MIN_VALUE;
    }

    public void setExtendedLifetime() {
        this.age = -6000;
    }

    public void makeFakeItem() {
        this.setNeverPickUp();
        this.age = 5999;
    }

    public float getSpin(float $$0) {
        return ((float)this.getAge() + $$0) / 20.0f + this.bobOffs;
    }

    public ItemEntity copy() {
        return new ItemEntity(this);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 180.0f - this.getSpin(0.5f) / ((float)Math.PI * 2) * 360.0f;
    }
}