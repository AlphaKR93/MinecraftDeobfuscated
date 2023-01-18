/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.time.LocalDate
 *  java.time.temporal.ChronoField
 *  java.time.temporal.TemporalField
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bat
extends AmbientCreature {
    public static final float FLAP_DEGREES_PER_TICK = 74.48451f;
    public static final int TICKS_PER_FLAP = Mth.ceil(2.4166098f);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
    private static final int FLAG_RESTING = 1;
    private static final TargetingConditions BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0);
    @Nullable
    private BlockPos targetPosition;

    public Bat(EntityType<? extends Bat> $$0, Level $$1) {
        super((EntityType<? extends AmbientCreature>)$$0, $$1);
        if (!$$1.isClientSide) {
            this.setResting(true);
        }
    }

    @Override
    public boolean isFlapping() {
        return !this.isResting() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1f;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95f;
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        if (this.isResting() && this.random.nextInt(4) != 0) {
            return null;
        }
        return SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity $$0) {
    }

    @Override
    protected void pushEntities() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
    }

    public boolean isResting() {
        return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_ID_FLAGS);
        if ($$0) {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$1 | 1));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$1 & 0xFFFFFFFE));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isResting()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0 - (double)this.getBbHeight(), this.getZ());
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPos $$0 = this.blockPosition();
        Vec3i $$1 = $$0.above();
        if (this.isResting()) {
            boolean $$2 = this.isSilent();
            if (this.level.getBlockState((BlockPos)$$1).isRedstoneConductor(this.level, $$0)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = this.random.nextInt(360);
                }
                if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
                    this.setResting(false);
                    if (!$$2) {
                        this.level.levelEvent(null, 1025, $$0, 0);
                    }
                }
            } else {
                this.setResting(false);
                if (!$$2) {
                    this.level.levelEvent(null, 1025, $$0, 0);
                }
            }
        } else {
            if (!(this.targetPosition == null || this.level.isEmptyBlock(this.targetPosition) && this.targetPosition.getY() > this.level.getMinBuildHeight())) {
                this.targetPosition = null;
            }
            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
                this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }
            double $$3 = (double)this.targetPosition.getX() + 0.5 - this.getX();
            double $$4 = (double)this.targetPosition.getY() + 0.1 - this.getY();
            double $$5 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
            Vec3 $$6 = this.getDeltaMovement();
            Vec3 $$7 = $$6.add((Math.signum((double)$$3) * 0.5 - $$6.x) * (double)0.1f, (Math.signum((double)$$4) * (double)0.7f - $$6.y) * (double)0.1f, (Math.signum((double)$$5) * 0.5 - $$6.z) * (double)0.1f);
            this.setDeltaMovement($$7);
            float $$8 = (float)(Mth.atan2($$7.z, $$7.x) * 57.2957763671875) - 90.0f;
            float $$9 = Mth.wrapDegrees($$8 - this.getYRot());
            this.zza = 0.5f;
            this.setYRot(this.getYRot() + $$9);
            if (this.random.nextInt(100) == 0 && this.level.getBlockState((BlockPos)$$1).isRedstoneConductor(this.level, (BlockPos)$$1)) {
                this.setResting(true);
            }
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.entityData.set(DATA_ID_FLAGS, $$0.getByte("BatFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
    }

    public static boolean checkBatSpawnRules(EntityType<Bat> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        if ($$3.getY() >= $$1.getSeaLevel()) {
            return false;
        }
        int $$5 = $$1.getMaxLocalRawBrightness($$3);
        int $$6 = 4;
        if (Bat.isHalloween()) {
            $$6 = 7;
        } else if ($$4.nextBoolean()) {
            return false;
        }
        if ($$5 > $$4.nextInt($$6)) {
            return false;
        }
        return Bat.checkMobSpawnRules($$0, $$1, $$2, $$3, $$4);
    }

    private static boolean isHalloween() {
        LocalDate $$0 = LocalDate.now();
        int $$1 = $$0.get((TemporalField)ChronoField.DAY_OF_MONTH);
        int $$2 = $$0.get((TemporalField)ChronoField.MONTH_OF_YEAR);
        return $$2 == 10 && $$1 >= 20 || $$2 == 11 && $$1 <= 3;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height / 2.0f;
    }
}