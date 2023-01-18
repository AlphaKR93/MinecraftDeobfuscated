/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ghast
extends FlyingMob
implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
    private int explosionPower = 1;

    public Ghast(EntityType<? extends Ghast> $$0, Level $$1) {
        super((EntityType<? extends FlyingMob>)$$0, $$1);
        this.xpReward = 5;
        this.moveControl = new GhastMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new GhastLookGoal(this));
        this.goalSelector.addGoal(7, new GhastShootFireballGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)$$0 -> Math.abs((double)($$0.getY() - this.getY())) <= 4.0)));
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean $$0) {
        this.entityData.set(DATA_IS_CHARGING, $$0);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    private static boolean isReflectedFireball(DamageSource $$0) {
        return $$0.getDirectEntity() instanceof LargeFireball && $$0.getEntity() instanceof Player;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource $$0) {
        return !Ghast.isReflectedFireball($$0) && super.isInvulnerableTo($$0);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (Ghast.isReflectedFireball($$0)) {
            super.hurt($$0, 1000.0f);
            return true;
        }
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        return super.hurt($$0, $$1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 100.0);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.GHAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    public static boolean checkGhastSpawnRules(EntityType<Ghast> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getDifficulty() != Difficulty.PEACEFUL && $$4.nextInt(20) == 0 && Ghast.checkMobSpawnRules($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("ExplosionPower", 99)) {
            this.explosionPower = $$0.getByte("ExplosionPower");
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 2.6f;
    }

    static class GhastMoveControl
    extends MoveControl {
        private final Ghast ghast;
        private int floatDuration;

        public GhastMoveControl(Ghast $$0) {
            super($$0);
            this.ghast = $$0;
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            if (this.floatDuration-- <= 0) {
                this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                Vec3 $$0 = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
                double $$1 = $$0.length();
                if (this.canReach($$0 = $$0.normalize(), Mth.ceil($$1))) {
                    this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add($$0.scale(0.1)));
                } else {
                    this.operation = MoveControl.Operation.WAIT;
                }
            }
        }

        private boolean canReach(Vec3 $$0, int $$1) {
            AABB $$2 = this.ghast.getBoundingBox();
            for (int $$3 = 1; $$3 < $$1; ++$$3) {
                if (this.ghast.level.noCollision(this.ghast, $$2 = $$2.move($$0))) continue;
                return false;
            }
            return true;
        }
    }

    static class RandomFloatAroundGoal
    extends Goal {
        private final Ghast ghast;

        public RandomFloatAroundGoal(Ghast $$0) {
            this.ghast = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            double $$3;
            double $$2;
            MoveControl $$0 = this.ghast.getMoveControl();
            if (!$$0.hasWanted()) {
                return true;
            }
            double $$1 = $$0.getWantedX() - this.ghast.getX();
            double $$4 = $$1 * $$1 + ($$2 = $$0.getWantedY() - this.ghast.getY()) * $$2 + ($$3 = $$0.getWantedZ() - this.ghast.getZ()) * $$3;
            return $$4 < 1.0 || $$4 > 3600.0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource $$0 = this.ghast.getRandom();
            double $$1 = this.ghast.getX() + (double)(($$0.nextFloat() * 2.0f - 1.0f) * 16.0f);
            double $$2 = this.ghast.getY() + (double)(($$0.nextFloat() * 2.0f - 1.0f) * 16.0f);
            double $$3 = this.ghast.getZ() + (double)(($$0.nextFloat() * 2.0f - 1.0f) * 16.0f);
            this.ghast.getMoveControl().setWantedPosition($$1, $$2, $$3, 1.0);
        }
    }

    static class GhastLookGoal
    extends Goal {
        private final Ghast ghast;

        public GhastLookGoal(Ghast $$0) {
            this.ghast = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3 $$0 = this.ghast.getDeltaMovement();
                this.ghast.setYRot(-((float)Mth.atan2($$0.x, $$0.z)) * 57.295776f);
                this.ghast.yBodyRot = this.ghast.getYRot();
            } else {
                LivingEntity $$1 = this.ghast.getTarget();
                double $$2 = 64.0;
                if ($$1.distanceToSqr(this.ghast) < 4096.0) {
                    double $$3 = $$1.getX() - this.ghast.getX();
                    double $$4 = $$1.getZ() - this.ghast.getZ();
                    this.ghast.setYRot(-((float)Mth.atan2($$3, $$4)) * 57.295776f);
                    this.ghast.yBodyRot = this.ghast.getYRot();
                }
            }
        }
    }

    static class GhastShootFireballGoal
    extends Goal {
        private final Ghast ghast;
        public int chargeTime;

        public GhastShootFireballGoal(Ghast $$0) {
            this.ghast = $$0;
        }

        @Override
        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void start() {
            this.chargeTime = 0;
        }

        @Override
        public void stop() {
            this.ghast.setCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = this.ghast.getTarget();
            if ($$0 == null) {
                return;
            }
            double $$1 = 64.0;
            if ($$0.distanceToSqr(this.ghast) < 4096.0 && this.ghast.hasLineOfSight($$0)) {
                Level $$2 = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.ghast.isSilent()) {
                    $$2.levelEvent(null, 1015, this.ghast.blockPosition(), 0);
                }
                if (this.chargeTime == 20) {
                    double $$3 = 4.0;
                    Vec3 $$4 = this.ghast.getViewVector(1.0f);
                    double $$5 = $$0.getX() - (this.ghast.getX() + $$4.x * 4.0);
                    double $$6 = $$0.getY(0.5) - (0.5 + this.ghast.getY(0.5));
                    double $$7 = $$0.getZ() - (this.ghast.getZ() + $$4.z * 4.0);
                    if (!this.ghast.isSilent()) {
                        $$2.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                    }
                    LargeFireball $$8 = new LargeFireball($$2, (LivingEntity)this.ghast, $$5, $$6, $$7, this.ghast.getExplosionPower());
                    $$8.setPos(this.ghast.getX() + $$4.x * 4.0, this.ghast.getY(0.5) + 0.5, $$8.getZ() + $$4.z * 4.0);
                    $$2.addFreshEntity($$8);
                    this.chargeTime = -40;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }
            this.ghast.setCharging(this.chargeTime > 10);
        }
    }
}