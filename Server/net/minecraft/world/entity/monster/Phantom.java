/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Comparator
 *  java.util.EnumSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Phantom
extends FlyingMob
implements Enemy {
    public static final float FLAP_DEGREES_PER_TICK = 7.448451f;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098f);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Phantom.class, EntityDataSerializers.INT);
    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    AttackPhase attackPhase = AttackPhase.CIRCLE;

    public Phantom(EntityType<? extends Phantom> $$0, Level $$1) {
        super((EntityType<? extends FlyingMob>)$$0, $$1);
        this.xpReward = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    @Override
    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new PhantomBodyRotationControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new PhantomCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new PhantomAttackPlayerTargetGoal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public void setPhantomSize(int $$0) {
        this.entityData.set(ID_SIZE, Mth.clamp($$0, 0, 64));
    }

    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.entityData.get(ID_SIZE);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.35f;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (ID_SIZE.equals($$0)) {
            this.updatePhantomSizeInfo();
        }
        super.onSyncedDataUpdated($$0);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float $$0 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            float $$1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            if ($$0 > 0.0f && $$1 <= 0.0f) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            int $$2 = this.getPhantomSize();
            float $$3 = Mth.cos(this.getYRot() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)$$2);
            float $$4 = Mth.sin(this.getYRot() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)$$2);
            float $$5 = (0.3f + $$0 * 0.45f) * ((float)$$2 * 0.2f + 1.0f);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)$$3, this.getY() + (double)$$5, this.getZ() + (double)$$4, 0.0, 0.0, 0.0);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)$$3, this.getY() + (double)$$5, this.getZ() - (double)$$4, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("AX")) {
            this.anchorPoint = new BlockPos($$0.getInt("AX"), $$0.getInt("AY"), $$0.getInt("AZ"));
        }
        this.setPhantomSize($$0.getInt("Size"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("AX", this.anchorPoint.getX());
        $$0.putInt("AY", this.anchorPoint.getY());
        $$0.putInt("AZ", this.anchorPoint.getZ());
        $$0.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return true;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public boolean canAttackType(EntityType<?> $$0) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        int $$1 = this.getPhantomSize();
        EntityDimensions $$2 = super.getDimensions($$0);
        float $$3 = ($$2.width + 0.2f * (float)$$1) / $$2.width;
        return $$2.scale($$3);
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP;

    }

    class PhantomMoveControl
    extends MoveControl {
        private float speed;

        public PhantomMoveControl(Mob $$0) {
            super($$0);
            this.speed = 0.1f;
        }

        @Override
        public void tick() {
            if (Phantom.this.horizontalCollision) {
                Phantom.this.setYRot(Phantom.this.getYRot() + 180.0f);
                this.speed = 0.1f;
            }
            double $$0 = Phantom.this.moveTargetPoint.x - Phantom.this.getX();
            double $$1 = Phantom.this.moveTargetPoint.y - Phantom.this.getY();
            double $$2 = Phantom.this.moveTargetPoint.z - Phantom.this.getZ();
            double $$3 = Math.sqrt((double)($$0 * $$0 + $$2 * $$2));
            if (Math.abs((double)$$3) > (double)1.0E-5f) {
                double $$4 = 1.0 - Math.abs((double)($$1 * (double)0.7f)) / $$3;
                $$3 = Math.sqrt((double)(($$0 *= $$4) * $$0 + ($$2 *= $$4) * $$2));
                double $$5 = Math.sqrt((double)($$0 * $$0 + $$2 * $$2 + $$1 * $$1));
                float $$6 = Phantom.this.getYRot();
                float $$7 = (float)Mth.atan2($$2, $$0);
                float $$8 = Mth.wrapDegrees(Phantom.this.getYRot() + 90.0f);
                float $$9 = Mth.wrapDegrees($$7 * 57.295776f);
                Phantom.this.setYRot(Mth.approachDegrees($$8, $$9, 4.0f) - 90.0f);
                Phantom.this.yBodyRot = Phantom.this.getYRot();
                this.speed = Mth.degreesDifferenceAbs($$6, Phantom.this.getYRot()) < 3.0f ? Mth.approach(this.speed, 1.8f, 0.005f * (1.8f / this.speed)) : Mth.approach(this.speed, 0.2f, 0.025f);
                float $$10 = (float)(-(Mth.atan2(-$$1, $$3) * 57.2957763671875));
                Phantom.this.setXRot($$10);
                float $$11 = Phantom.this.getYRot() + 90.0f;
                double $$12 = (double)(this.speed * Mth.cos($$11 * ((float)Math.PI / 180))) * Math.abs((double)($$0 / $$5));
                double $$13 = (double)(this.speed * Mth.sin($$11 * ((float)Math.PI / 180))) * Math.abs((double)($$2 / $$5));
                double $$14 = (double)(this.speed * Mth.sin($$10 * ((float)Math.PI / 180))) * Math.abs((double)($$1 / $$5));
                Vec3 $$15 = Phantom.this.getDeltaMovement();
                Phantom.this.setDeltaMovement($$15.add(new Vec3($$12, $$14, $$13).subtract($$15).scale(0.2)));
            }
        }
    }

    class PhantomLookControl
    extends LookControl {
        public PhantomLookControl(Mob $$0) {
            super($$0);
        }

        @Override
        public void tick() {
        }
    }

    class PhantomBodyRotationControl
    extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob $$0) {
            super($$0);
        }

        @Override
        public void clientTick() {
            Phantom.this.yHeadRot = Phantom.this.yBodyRot;
            Phantom.this.yBodyRot = Phantom.this.getYRot();
        }
    }

    class PhantomAttackStrategyGoal
    extends Goal {
        private int nextSweepTick;

        PhantomAttackStrategyGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = Phantom.this.getTarget();
            if ($$0 != null) {
                return Phantom.this.canAttack($$0, TargetingConditions.DEFAULT);
            }
            return false;
        }

        @Override
        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            Phantom.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop() {
            Phantom.this.anchorPoint = Phantom.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint).above(10 + Phantom.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (Phantom.this.attackPhase == AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    Phantom.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + Phantom.this.random.nextInt(4)) * 20);
                    Phantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0f, 0.95f + Phantom.this.random.nextFloat() * 0.1f);
                }
            }
        }

        private void setAnchorAboveTarget() {
            Phantom.this.anchorPoint = Phantom.this.getTarget().blockPosition().above(20 + Phantom.this.random.nextInt(20));
            if (Phantom.this.anchorPoint.getY() < Phantom.this.level.getSeaLevel()) {
                Phantom.this.anchorPoint = new BlockPos(Phantom.this.anchorPoint.getX(), Phantom.this.level.getSeaLevel() + 1, Phantom.this.anchorPoint.getZ());
            }
        }
    }

    class PhantomSweepAttackGoal
    extends PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        PhantomSweepAttackGoal() {
        }

        @Override
        public boolean canUse() {
            return Phantom.this.getTarget() != null && Phantom.this.attackPhase == AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity $$0 = Phantom.this.getTarget();
            if ($$0 == null) {
                return false;
            }
            if (!$$0.isAlive()) {
                return false;
            }
            if ($$0 instanceof Player) {
                Player $$1 = (Player)$$0;
                if ($$0.isSpectator() || $$1.isCreative()) {
                    return false;
                }
            }
            if (!this.canUse()) {
                return false;
            }
            if (Phantom.this.tickCount > this.catSearchTick) {
                this.catSearchTick = Phantom.this.tickCount + 20;
                List $$2 = Phantom.this.level.getEntitiesOfClass(Cat.class, Phantom.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);
                for (Cat $$3 : $$2) {
                    $$3.hiss();
                }
                this.isScaredOfCat = !$$2.isEmpty();
            }
            return !this.isScaredOfCat;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            Phantom.this.setTarget(null);
            Phantom.this.attackPhase = AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = Phantom.this.getTarget();
            if ($$0 == null) {
                return;
            }
            Phantom.this.moveTargetPoint = new Vec3($$0.getX(), $$0.getY(0.5), $$0.getZ());
            if (Phantom.this.getBoundingBox().inflate(0.2f).intersects($$0.getBoundingBox())) {
                Phantom.this.doHurtTarget($$0);
                Phantom.this.attackPhase = AttackPhase.CIRCLE;
                if (!Phantom.this.isSilent()) {
                    Phantom.this.level.levelEvent(1039, Phantom.this.blockPosition(), 0);
                }
            } else if (Phantom.this.horizontalCollision || Phantom.this.hurtTime > 0) {
                Phantom.this.attackPhase = AttackPhase.CIRCLE;
            }
        }
    }

    class PhantomCircleAroundAnchorGoal
    extends PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        PhantomCircleAroundAnchorGoal() {
        }

        @Override
        public boolean canUse() {
            return Phantom.this.getTarget() == null || Phantom.this.attackPhase == AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.distance = 5.0f + Phantom.this.random.nextFloat() * 10.0f;
            this.height = -4.0f + Phantom.this.random.nextFloat() * 9.0f;
            this.clockwise = Phantom.this.random.nextBoolean() ? 1.0f : -1.0f;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (Phantom.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0f + Phantom.this.random.nextFloat() * 9.0f;
            }
            if (Phantom.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                this.distance += 1.0f;
                if (this.distance > 15.0f) {
                    this.distance = 5.0f;
                    this.clockwise = -this.clockwise;
                }
            }
            if (Phantom.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = Phantom.this.random.nextFloat() * 2.0f * (float)Math.PI;
                this.selectNext();
            }
            if (this.touchingTarget()) {
                this.selectNext();
            }
            if (Phantom.this.moveTargetPoint.y < Phantom.this.getY() && !Phantom.this.level.isEmptyBlock((BlockPos)Phantom.this.blockPosition().below(1))) {
                this.height = Math.max((float)1.0f, (float)this.height);
                this.selectNext();
            }
            if (Phantom.this.moveTargetPoint.y > Phantom.this.getY() && !Phantom.this.level.isEmptyBlock((BlockPos)Phantom.this.blockPosition().above(1))) {
                this.height = Math.min((float)-1.0f, (float)this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(Phantom.this.anchorPoint)) {
                Phantom.this.anchorPoint = Phantom.this.blockPosition();
            }
            this.angle += this.clockwise * 15.0f * ((float)Math.PI / 180);
            Phantom.this.moveTargetPoint = Vec3.atLowerCornerOf(Phantom.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0f + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class PhantomAttackPlayerTargetGoal
    extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
        private int nextScanTick = PhantomAttackPlayerTargetGoal.reducedTickDelay(20);

        PhantomAttackPlayerTargetGoal() {
        }

        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            }
            this.nextScanTick = PhantomAttackPlayerTargetGoal.reducedTickDelay(60);
            List $$0 = Phantom.this.level.getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!$$0.isEmpty()) {
                $$0.sort(Comparator.comparing(Entity::getY).reversed());
                for (Player $$1 : $$0) {
                    if (!Phantom.this.canAttack($$1, TargetingConditions.DEFAULT)) continue;
                    Phantom.this.setTarget($$1);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity $$0 = Phantom.this.getTarget();
            if ($$0 != null) {
                return Phantom.this.canAttack($$0, TargetingConditions.DEFAULT);
            }
            return false;
        }
    }

    abstract class PhantomMoveTargetGoal
    extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return Phantom.this.moveTargetPoint.distanceToSqr(Phantom.this.getX(), Phantom.this.getY(), Phantom.this.getZ()) < 4.0;
        }
    }
}