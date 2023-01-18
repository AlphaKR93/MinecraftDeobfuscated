/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;

public class Slime
extends Mob
implements Enemy {
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public Slime(EntityType<? extends Slime> $$0, Level $$1) {
        super((EntityType<? extends Mob>)$$0, $$1);
        this.fixupDimensions();
        this.moveControl = new SlimeMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)$$0 -> Math.abs((double)($$0.getY() - this.getY())) <= 4.0)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>((Mob)this, IronGolem.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 1);
    }

    @VisibleForTesting
    public void setSize(int $$0, boolean $$1) {
        int $$2 = Mth.clamp($$0, 1, 127);
        this.entityData.set(ID_SIZE, $$2);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue($$2 * $$2);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * (float)$$2);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue($$2);
        if ($$1) {
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = $$2;
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Size", this.getSize() - 1);
        $$0.putBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        this.setSize($$0.getInt("Size") + 1, false);
        super.readAdditionalSaveData($$0);
        this.wasOnGround = $$0.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5f;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            int $$0 = this.getSize();
            for (int $$1 = 0; $$1 < $$0 * 8; ++$$1) {
                float $$2 = this.random.nextFloat() * ((float)Math.PI * 2);
                float $$3 = this.random.nextFloat() * 0.5f + 0.5f;
                float $$4 = Mth.sin($$2) * (float)$$0 * 0.5f * $$3;
                float $$5 = Mth.cos($$2) * (float)$$0 * 0.5f * $$3;
                this.level.addParticle(this.getParticleType(), this.getX() + (double)$$4, this.getY(), this.getZ() + (double)$$5, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            this.targetSquish = -0.5f;
        } else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0f;
        }
        this.wasOnGround = this.onGround;
        this.decreaseSquish();
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6f;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void refreshDimensions() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.refreshDimensions();
        this.setPos($$0, $$1, $$2);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (ID_SIZE.equals($$0)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.onSyncedDataUpdated($$0);
    }

    public EntityType<? extends Slime> getType() {
        return super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason $$0) {
        int $$1 = this.getSize();
        if (!this.level.isClientSide && $$1 > 1 && this.isDeadOrDying()) {
            Component $$2 = this.getCustomName();
            boolean $$3 = this.isNoAi();
            float $$4 = (float)$$1 / 4.0f;
            int $$5 = $$1 / 2;
            int $$6 = 2 + this.random.nextInt(3);
            for (int $$7 = 0; $$7 < $$6; ++$$7) {
                float $$8 = ((float)($$7 % 2) - 0.5f) * $$4;
                float $$9 = ((float)($$7 / 2) - 0.5f) * $$4;
                Slime $$10 = this.getType().create(this.level);
                if ($$10 == null) continue;
                if (this.isPersistenceRequired()) {
                    $$10.setPersistenceRequired();
                }
                $$10.setCustomName($$2);
                $$10.setNoAi($$3);
                $$10.setInvulnerable(this.isInvulnerable());
                $$10.setSize($$5, true);
                $$10.moveTo(this.getX() + (double)$$8, this.getY() + 0.5, this.getZ() + (double)$$9, this.random.nextFloat() * 360.0f, 0.0f);
                this.level.addFreshEntity($$10);
            }
        }
        super.remove($$0);
    }

    @Override
    public void push(Entity $$0) {
        super.push($$0);
        if ($$0 instanceof IronGolem && this.isDealsDamage()) {
            this.dealDamage((LivingEntity)$$0);
        }
    }

    @Override
    public void playerTouch(Player $$0) {
        if (this.isDealsDamage()) {
            this.dealDamage($$0);
        }
    }

    protected void dealDamage(LivingEntity $$0) {
        if (this.isAlive()) {
            int $$1 = this.getSize();
            if (this.distanceToSqr($$0) < 0.6 * (double)$$1 * (0.6 * (double)$$1) && this.hasLineOfSight($$0) && $$0.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                this.doEnchantDamageEffects(this, $$0);
            }
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.625f * $$1.height;
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if (this.isTiny()) {
            return SoundEvents.SLIME_HURT_SMALL;
        }
        return SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isTiny()) {
            return SoundEvents.SLIME_DEATH_SMALL;
        }
        return SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        if (this.isTiny()) {
            return SoundEvents.SLIME_SQUISH_SMALL;
        }
        return SoundEvents.SLIME_SQUISH;
    }

    public static boolean checkSlimeSpawnRules(EntityType<Slime> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        if ($$1.getDifficulty() != Difficulty.PEACEFUL) {
            boolean $$6;
            if ($$1.getBiome($$3).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && $$3.getY() > 50 && $$3.getY() < 70 && $$4.nextFloat() < 0.5f && $$4.nextFloat() < $$1.getMoonBrightness() && $$1.getMaxLocalRawBrightness($$3) <= $$4.nextInt(8)) {
                return Slime.checkMobSpawnRules($$0, $$1, $$2, $$3, $$4);
            }
            if (!($$1 instanceof WorldGenLevel)) {
                return false;
            }
            ChunkPos $$5 = new ChunkPos($$3);
            boolean bl = $$6 = WorldgenRandom.seedSlimeChunk($$5.x, $$5.z, ((WorldGenLevel)$$1).getSeed(), 987234911L).nextInt(10) == 0;
            if ($$4.nextInt(10) == 0 && $$6 && $$3.getY() < 40) {
                return Slime.checkMobSpawnRules($$0, $$1, $$2, $$3, $$4);
            }
        }
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f * (float)this.getSize();
    }

    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    protected void jumpFromGround() {
        Vec3 $$0 = this.getDeltaMovement();
        this.setDeltaMovement($$0.x, this.getJumpPower(), $$0.z);
        this.hasImpulse = true;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        RandomSource $$5 = $$0.getRandom();
        int $$6 = $$5.nextInt(3);
        if ($$6 < 2 && $$5.nextFloat() < 0.5f * $$1.getSpecialMultiplier()) {
            ++$$6;
        }
        int $$7 = 1 << $$6;
        this.setSize($$7, true);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    float getSoundPitch() {
        float $$0 = this.isTiny() ? 1.4f : 0.8f;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * $$0;
    }

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return super.getDimensions($$0).scale(0.255f * (float)this.getSize());
    }

    static class SlimeMoveControl
    extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final Slime slime;
        private boolean isAggressive;

        public SlimeMoveControl(Slime $$0) {
            super($$0);
            this.slime = $$0;
            this.yRot = 180.0f * $$0.getYRot() / (float)Math.PI;
        }

        public void setDirection(float $$0, boolean $$1) {
            this.yRot = $$0;
            this.isAggressive = $$1;
        }

        public void setWantedMovement(double $$0) {
            this.speedModifier = $$0;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0f));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0f);
                return;
            }
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.isOnGround()) {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    if (this.isAggressive) {
                        this.jumpDelay /= 3;
                    }
                    this.slime.getJumpControl().jump();
                    if (this.slime.doPlayJumpSound()) {
                        this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                    }
                } else {
                    this.slime.xxa = 0.0f;
                    this.slime.zza = 0.0f;
                    this.mob.setSpeed(0.0f);
                }
            } else {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }

    static class SlimeFloatGoal
    extends Goal {
        private final Slime slime;

        public SlimeFloatGoal(Slime $$0) {
            this.slime = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.JUMP, (Enum)Goal.Flag.MOVE));
            $$0.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8f) {
                this.slime.getJumpControl().jump();
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.2);
        }
    }

    static class SlimeAttackGoal
    extends Goal {
        private final Slime slime;
        private int growTiredTimer;

        public SlimeAttackGoal(Slime $$0) {
            this.slime = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = this.slime.getTarget();
            if ($$0 == null) {
                return false;
            }
            if (!this.slime.canAttack($$0)) {
                return false;
            }
            return this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void start() {
            this.growTiredTimer = SlimeAttackGoal.reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity $$0 = this.slime.getTarget();
            if ($$0 == null) {
                return false;
            }
            if (!this.slime.canAttack($$0)) {
                return false;
            }
            return --this.growTiredTimer > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = this.slime.getTarget();
            if ($$0 != null) {
                this.slime.lookAt($$0, 10.0f, 10.0f);
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
        }
    }

    static class SlimeRandomDirectionGoal
    extends Goal {
        private final Slime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(Slime $$0) {
            this.slime = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = this.slime.getRandom().nextInt(360);
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    static class SlimeKeepOnJumpingGoal
    extends Goal {
        private final Slime slime;

        public SlimeKeepOnJumpingGoal(Slime $$0) {
            this.slime = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.JUMP, (Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            ((SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.0);
        }
    }
}