/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.OptionalInt
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketEntity
extends Projectile
implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
    private int life;
    private int lifetime;
    @Nullable
    private LivingEntity attachedToEntity;

    public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
    }

    public FireworkRocketEntity(Level $$0, double $$1, double $$2, double $$3, ItemStack $$4) {
        super((EntityType<? extends Projectile>)EntityType.FIREWORK_ROCKET, $$0);
        this.life = 0;
        this.setPos($$1, $$2, $$3);
        int $$5 = 1;
        if (!$$4.isEmpty() && $$4.hasTag()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, $$4.copy());
            $$5 += $$4.getOrCreateTagElement("Fireworks").getByte("Flight");
        }
        this.setDeltaMovement(this.random.triangle(0.0, 0.002297), 0.05, this.random.triangle(0.0, 0.002297));
        this.lifetime = 10 * $$5 + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public FireworkRocketEntity(Level $$0, @Nullable Entity $$1, double $$2, double $$3, double $$4, ItemStack $$5) {
        this($$0, $$2, $$3, $$4, $$5);
        this.setOwner($$1);
    }

    public FireworkRocketEntity(Level $$0, ItemStack $$1, LivingEntity $$2) {
        this($$0, $$2, $$2.getX(), $$2.getY(), $$2.getZ(), $$1);
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of((int)$$2.getId()));
        this.attachedToEntity = $$2;
    }

    public FireworkRocketEntity(Level $$0, ItemStack $$1, double $$2, double $$3, double $$4, boolean $$5) {
        this($$0, $$2, $$3, $$4, $$1);
        this.entityData.set(DATA_SHOT_AT_ANGLE, $$5);
    }

    public FireworkRocketEntity(Level $$0, ItemStack $$1, Entity $$2, double $$3, double $$4, double $$5, boolean $$6) {
        this($$0, $$1, $$3, $$4, $$5, $$6);
        this.setOwner($$2);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
        this.entityData.define(DATA_SHOT_AT_ANGLE, false);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return $$0 < 4096.0 && !this.isAttachedToEntity();
    }

    @Override
    public boolean shouldRender(double $$0, double $$1, double $$2) {
        return super.shouldRender($$0, $$1, $$2) && !this.isAttachedToEntity();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAttachedToEntity()) {
            if (this.attachedToEntity == null) {
                this.entityData.get(DATA_ATTACHED_TO_TARGET).ifPresent($$0 -> {
                    Entity $$1 = this.level.getEntity($$0);
                    if ($$1 instanceof LivingEntity) {
                        this.attachedToEntity = (LivingEntity)$$1;
                    }
                });
            }
            if (this.attachedToEntity != null) {
                Vec3 $$5;
                if (this.attachedToEntity.isFallFlying()) {
                    Vec3 $$02 = this.attachedToEntity.getLookAngle();
                    double $$1 = 1.5;
                    double $$2 = 0.1;
                    Vec3 $$3 = this.attachedToEntity.getDeltaMovement();
                    this.attachedToEntity.setDeltaMovement($$3.add($$02.x * 0.1 + ($$02.x * 1.5 - $$3.x) * 0.5, $$02.y * 0.1 + ($$02.y * 1.5 - $$3.y) * 0.5, $$02.z * 0.1 + ($$02.z * 1.5 - $$3.z) * 0.5));
                    Vec3 $$4 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);
                } else {
                    $$5 = Vec3.ZERO;
                }
                this.setPos(this.attachedToEntity.getX() + $$5.x, this.attachedToEntity.getY() + $$5.y, this.attachedToEntity.getZ() + $$5.z);
                this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
            }
        } else {
            if (!this.isShotAtAngle()) {
                double $$6 = this.horizontalCollision ? 1.0 : 1.15;
                this.setDeltaMovement(this.getDeltaMovement().multiply($$6, 1.0, $$6).add(0.0, 0.04, 0.0));
            }
            Vec3 $$7 = this.getDeltaMovement();
            this.move(MoverType.SELF, $$7);
            this.setDeltaMovement($$7);
        }
        HitResult $$8 = ProjectileUtil.getHitResult(this, (Predicate<Entity>)((Predicate)this::canHitEntity));
        if (!this.noPhysics) {
            this.onHit($$8);
            this.hasImpulse = true;
        }
        this.updateRotation();
        if (this.life == 0 && !this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0f, 1.0f);
        }
        ++this.life;
        if (this.level.isClientSide && this.life % 2 < 2) {
            this.level.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, -this.getDeltaMovement().y * 0.5, this.random.nextGaussian() * 0.05);
        }
        if (!this.level.isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    private void explode() {
        this.level.broadcastEntityEvent(this, (byte)17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.dealExplosionDamage();
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        if (this.level.isClientSide) {
            return;
        }
        this.explode();
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        BlockPos $$1 = new BlockPos($$0.getBlockPos());
        this.level.getBlockState($$1).entityInside(this.level, $$1, this);
        if (!this.level.isClientSide() && this.hasExplosion()) {
            this.explode();
        }
        super.onHitBlock($$0);
    }

    private boolean hasExplosion() {
        ItemStack $$0 = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundTag $$1 = $$0.isEmpty() ? null : $$0.getTagElement("Fireworks");
        ListTag $$2 = $$1 != null ? $$1.getList("Explosions", 10) : null;
        return $$2 != null && !$$2.isEmpty();
    }

    private void dealExplosionDamage() {
        ListTag $$3;
        float $$0 = 0.0f;
        ItemStack $$1 = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundTag $$2 = $$1.isEmpty() ? null : $$1.getTagElement("Fireworks");
        ListTag listTag = $$3 = $$2 != null ? $$2.getList("Explosions", 10) : null;
        if ($$3 != null && !$$3.isEmpty()) {
            $$0 = 5.0f + (float)($$3.size() * 2);
        }
        if ($$0 > 0.0f) {
            if (this.attachedToEntity != null) {
                this.attachedToEntity.hurt(DamageSource.fireworks(this, this.getOwner()), 5.0f + (float)($$3.size() * 2));
            }
            double $$4 = 5.0;
            Vec3 $$5 = this.position();
            List $$6 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0));
            for (LivingEntity $$7 : $$6) {
                if ($$7 == this.attachedToEntity || this.distanceToSqr($$7) > 25.0) continue;
                boolean $$8 = false;
                for (int $$9 = 0; $$9 < 2; ++$$9) {
                    Vec3 $$10 = new Vec3($$7.getX(), $$7.getY(0.5 * (double)$$9), $$7.getZ());
                    BlockHitResult $$11 = this.level.clip(new ClipContext($$5, $$10, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (((HitResult)$$11).getType() != HitResult.Type.MISS) continue;
                    $$8 = true;
                    break;
                }
                if (!$$8) continue;
                float $$12 = $$0 * (float)Math.sqrt((double)((5.0 - (double)this.distanceTo($$7)) / 5.0));
                $$7.hurt(DamageSource.fireworks(this, this.getOwner()), $$12);
            }
        }
    }

    private boolean isAttachedToEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent();
    }

    public boolean isShotAtAngle() {
        return this.entityData.get(DATA_SHOT_AT_ANGLE);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 17 && this.level.isClientSide) {
            if (!this.hasExplosion()) {
                for (int $$1 = 0; $$1 < this.random.nextInt(3) + 2; ++$$1) {
                    this.level.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
                }
            } else {
                ItemStack $$2 = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
                CompoundTag $$3 = $$2.isEmpty() ? null : $$2.getTagElement("Fireworks");
                Vec3 $$4 = this.getDeltaMovement();
                this.level.createFireworks(this.getX(), this.getY(), this.getZ(), $$4.x, $$4.y, $$4.z, $$3);
            }
        }
        super.handleEntityEvent($$0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Life", this.life);
        $$0.putInt("LifeTime", this.lifetime);
        ItemStack $$1 = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (!$$1.isEmpty()) {
            $$0.put("FireworksItem", $$1.save(new CompoundTag()));
        }
        $$0.putBoolean("ShotAtAngle", this.entityData.get(DATA_SHOT_AT_ANGLE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.life = $$0.getInt("Life");
        this.lifetime = $$0.getInt("LifeTime");
        ItemStack $$1 = ItemStack.of($$0.getCompound("FireworksItem"));
        if (!$$1.isEmpty()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, $$1);
        }
        if ($$0.contains("ShotAtAngle")) {
            this.entityData.set(DATA_SHOT_AT_ANGLE, $$0.getBoolean("ShotAtAngle"));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack $$0 = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        return $$0.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : $$0;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}