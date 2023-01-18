/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet
extends Projectile {
    private static final double SPEED = 0.15;
    @Nullable
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;

    public ShulkerBullet(EntityType<? extends ShulkerBullet> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
        this.noPhysics = true;
    }

    public ShulkerBullet(Level $$0, LivingEntity $$1, Entity $$2, Direction.Axis $$3) {
        this((EntityType<? extends ShulkerBullet>)EntityType.SHULKER_BULLET, $$0);
        this.setOwner($$1);
        BlockPos $$4 = $$1.blockPosition();
        double $$5 = (double)$$4.getX() + 0.5;
        double $$6 = (double)$$4.getY() + 0.5;
        double $$7 = (double)$$4.getZ() + 0.5;
        this.moveTo($$5, $$6, $$7, this.getYRot(), this.getXRot());
        this.finalTarget = $$2;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection($$3);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.finalTarget != null) {
            $$0.putUUID("Target", this.finalTarget.getUUID());
        }
        if (this.currentMoveDirection != null) {
            $$0.putInt("Dir", this.currentMoveDirection.get3DDataValue());
        }
        $$0.putInt("Steps", this.flightSteps);
        $$0.putDouble("TXD", this.targetDeltaX);
        $$0.putDouble("TYD", this.targetDeltaY);
        $$0.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.flightSteps = $$0.getInt("Steps");
        this.targetDeltaX = $$0.getDouble("TXD");
        this.targetDeltaY = $$0.getDouble("TYD");
        this.targetDeltaZ = $$0.getDouble("TZD");
        if ($$0.contains("Dir", 99)) {
            this.currentMoveDirection = Direction.from3DDataValue($$0.getInt("Dir"));
        }
        if ($$0.hasUUID("Target")) {
            this.targetId = $$0.getUUID("Target");
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Nullable
    private Direction getMoveDirection() {
        return this.currentMoveDirection;
    }

    private void setMoveDirection(@Nullable Direction $$0) {
        this.currentMoveDirection = $$0;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis $$0) {
        BlockPos $$3;
        double $$1 = 0.5;
        if (this.finalTarget == null) {
            Vec3i $$2 = this.blockPosition().below();
        } else {
            $$1 = (double)this.finalTarget.getBbHeight() * 0.5;
            $$3 = new BlockPos(this.finalTarget.getX(), this.finalTarget.getY() + $$1, this.finalTarget.getZ());
        }
        double $$4 = (double)$$3.getX() + 0.5;
        double $$5 = (double)$$3.getY() + $$1;
        double $$6 = (double)$$3.getZ() + 0.5;
        Direction $$7 = null;
        if (!$$3.closerToCenterThan(this.position(), 2.0)) {
            BlockPos $$8 = this.blockPosition();
            ArrayList $$9 = Lists.newArrayList();
            if ($$0 != Direction.Axis.X) {
                if ($$8.getX() < $$3.getX() && this.level.isEmptyBlock((BlockPos)$$8.east())) {
                    $$9.add((Object)Direction.EAST);
                } else if ($$8.getX() > $$3.getX() && this.level.isEmptyBlock((BlockPos)$$8.west())) {
                    $$9.add((Object)Direction.WEST);
                }
            }
            if ($$0 != Direction.Axis.Y) {
                if ($$8.getY() < $$3.getY() && this.level.isEmptyBlock((BlockPos)$$8.above())) {
                    $$9.add((Object)Direction.UP);
                } else if ($$8.getY() > $$3.getY() && this.level.isEmptyBlock((BlockPos)$$8.below())) {
                    $$9.add((Object)Direction.DOWN);
                }
            }
            if ($$0 != Direction.Axis.Z) {
                if ($$8.getZ() < $$3.getZ() && this.level.isEmptyBlock((BlockPos)$$8.south())) {
                    $$9.add((Object)Direction.SOUTH);
                } else if ($$8.getZ() > $$3.getZ() && this.level.isEmptyBlock((BlockPos)$$8.north())) {
                    $$9.add((Object)Direction.NORTH);
                }
            }
            $$7 = Direction.getRandom(this.random);
            if ($$9.isEmpty()) {
                for (int $$10 = 5; !this.level.isEmptyBlock((BlockPos)$$8.relative($$7)) && $$10 > 0; --$$10) {
                    $$7 = Direction.getRandom(this.random);
                }
            } else {
                $$7 = (Direction)$$9.get(this.random.nextInt($$9.size()));
            }
            $$4 = this.getX() + (double)$$7.getStepX();
            $$5 = this.getY() + (double)$$7.getStepY();
            $$6 = this.getZ() + (double)$$7.getStepZ();
        }
        this.setMoveDirection($$7);
        double $$11 = $$4 - this.getX();
        double $$12 = $$5 - this.getY();
        double $$13 = $$6 - this.getZ();
        double $$14 = Math.sqrt((double)($$11 * $$11 + $$12 * $$12 + $$13 * $$13));
        if ($$14 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = $$11 / $$14 * 0.15;
            this.targetDeltaY = $$12 / $$14 * 0.15;
            this.targetDeltaZ = $$13 / $$14 * 0.15;
        }
        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((ServerLevel)this.level).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }
            if (!(this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && this.finalTarget.isSpectator())) {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                Vec3 $$0 = this.getDeltaMovement();
                this.setDeltaMovement($$0.add((this.targetDeltaX - $$0.x) * 0.2, (this.targetDeltaY - $$0.y) * 0.2, (this.targetDeltaZ - $$0.z) * 0.2));
            } else if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
            HitResult $$1 = ProjectileUtil.getHitResult(this, (Predicate<Entity>)((Predicate)this::canHitEntity));
            if ($$1.getType() != HitResult.Type.MISS) {
                this.onHit($$1);
            }
        }
        this.checkInsideBlocks();
        Vec3 $$2 = this.getDeltaMovement();
        this.setPos(this.getX() + $$2.x, this.getY() + $$2.y, this.getZ() + $$2.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.getX() - $$2.x, this.getY() - $$2.y + 0.15, this.getZ() - $$2.z, 0.0, 0.0, 0.0);
        } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
                }
            }
            if (this.currentMoveDirection != null) {
                BlockPos $$3 = this.blockPosition();
                Direction.Axis $$4 = this.currentMoveDirection.getAxis();
                if (this.level.loadedAndEntityCanStandOn((BlockPos)$$3.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection($$4);
                } else {
                    BlockPos $$5 = this.finalTarget.blockPosition();
                    if ($$4 == Direction.Axis.X && $$3.getX() == $$5.getX() || $$4 == Direction.Axis.Z && $$3.getZ() == $$5.getZ() || $$4 == Direction.Axis.Y && $$3.getY() == $$5.getY()) {
                        this.selectNextMoveDirection($$4);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity $$0) {
        return super.canHitEntity($$0) && !$$0.noPhysics;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return $$0 < 16384.0;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        Entity $$1 = $$0.getEntity();
        Entity $$2 = this.getOwner();
        LivingEntity $$3 = $$2 instanceof LivingEntity ? (LivingEntity)$$2 : null;
        boolean $$4 = $$1.hurt(DamageSource.indirectMobAttack(this, $$3).setProjectile(), 4.0f);
        if ($$4) {
            this.doEnchantDamageEffects($$3, $$1);
            if ($$1 instanceof LivingEntity) {
                ((LivingEntity)$$1).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200), (Entity)MoreObjects.firstNonNull((Object)$$2, (Object)this));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        super.onHitBlock($$0);
        ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.discard();
        }
        return true;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        double $$1 = $$0.getXa();
        double $$2 = $$0.getYa();
        double $$3 = $$0.getZa();
        this.setDeltaMovement($$1, $$2, $$3);
    }
}