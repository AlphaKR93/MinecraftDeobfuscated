/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.Dynamic
 *  java.lang.Boolean
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.camel;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RiderShieldingMount;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Camel
extends AbstractHorse
implements PlayerRideableJumping,
RiderShieldingMount,
Saddleable {
    public static final Ingredient TEMPTATION_ITEM = Ingredient.of(Items.CACTUS);
    public static final int DASH_COOLDOWN_TICKS = 55;
    public static final int MAX_HEAD_Y_ROT = 30;
    private static final float RUNNING_SPEED_BONUS = 0.1f;
    private static final float DASH_VERTICAL_MOMENTUM = 1.4285f;
    private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222f;
    private static final int SITDOWN_DURATION_TICKS = 40;
    private static final int STANDUP_DURATION_TICKS = 52;
    private static final int IDLE_MINIMAL_DURATION_TICKS = 80;
    private static final float SITTING_HEIGHT_DIFFERENCE = 1.43f;
    public static final EntityDataAccessor<Boolean> DASH = SynchedEntityData.defineId(Camel.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Camel.class, EntityDataSerializers.LONG);
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitPoseAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
    private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.scalable(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43f);
    private int dashCooldown = 0;
    private int idleAnimationTimeout = 0;

    public Camel(EntityType<? extends Camel> $$0, Level $$1) {
        super((EntityType<? extends AbstractHorse>)$$0, $$1);
        this.maxUpStep = 1.5f;
        GroundPathNavigation $$2 = (GroundPathNavigation)this.getNavigation();
        $$2.setCanFloat(true);
        $$2.setCanWalkOverFences(true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        long $$1 = $$0.getLong("LastPoseTick");
        if ($$1 < 0L) {
            this.setPose(Pose.SITTING);
        }
        this.resetLastPoseChangeTick($$1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Camel.createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 32.0).add(Attributes.MOVEMENT_SPEED, 0.09f).add(Attributes.JUMP_STRENGTH, 0.42f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DASH, false);
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        CamelAi.initMemories(this, $$0.getRandom());
        this.resetLastPoseChangeTickToFullStand($$0.getLevel().getGameTime());
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    protected Brain.Provider<Camel> brainProvider() {
        return CamelAi.brainProvider();
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return CamelAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return $$0 == Pose.SITTING ? SITTING_DIMENSIONS.scale(this.getScale()) : super.getDimensions($$0);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height - 0.1f;
    }

    @Override
    public double getRiderShieldingHeight() {
        return 0.5;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("camelBrain");
        Brain<?> $$0 = this.getBrain();
        $$0.tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("camelActivityUpdate");
        CamelAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDashing() && this.dashCooldown < 55 && (this.onGround || this.isInWater())) {
            this.setDashing(false);
        }
        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.level.playSound(null, this.blockPosition(), SoundEvents.CAMEL_DASH_READY, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        if (this.level.isClientSide()) {
            this.setupAnimationStates();
        }
        if (this.refuseToMove()) {
            this.clampHeadRotationToBody(this, 30.0f);
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
        if (this.isCamelSitting()) {
            this.walkAnimationState.stop();
            this.sitUpAnimationState.stop();
            this.dashAnimationState.stop();
            if (this.isSittingDown()) {
                this.sitAnimationState.startIfStopped(this.tickCount);
                this.sitPoseAnimationState.stop();
            } else {
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.sitAnimationState.stop();
            this.sitPoseAnimationState.stop();
            this.dashAnimationState.animateWhen(this.isDashing(), this.tickCount);
            this.sitUpAnimationState.animateWhen(this.isInPoseTransition(), this.tickCount);
            this.walkAnimationState.animateWhen((this.onGround || this.hasControllingPassenger()) && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6, this.tickCount);
        }
    }

    @Override
    public void travel(Vec3 $$0) {
        if (!this.isAlive()) {
            return;
        }
        if (this.refuseToMove() && this.isOnGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            $$0 = $$0.multiply(0.0, 1.0, 0.0);
        }
        super.travel($$0);
    }

    public boolean refuseToMove() {
        return this.isCamelSitting() || this.isInPoseTransition();
    }

    @Override
    protected float getDrivenMovementSpeed(LivingEntity $$0) {
        float $$1 = $$0.isSprinting() && this.getJumpCooldown() == 0 ? 0.1f : 0.0f;
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) + $$1;
    }

    @Override
    protected boolean mountIgnoresControllerInput(LivingEntity $$0) {
        boolean $$1 = this.isInPoseTransition();
        if (this.isCamelSitting() && !$$1 && $$0.zza > 0.0f) {
            this.standUp();
        }
        return this.refuseToMove() || super.mountIgnoresControllerInput($$0);
    }

    @Override
    public boolean canJump(Player $$0) {
        return !this.refuseToMove() && this.getControllingPassenger() == $$0 && super.canJump($$0);
    }

    @Override
    public void onPlayerJump(int $$0) {
        if (!this.isSaddled() || this.dashCooldown > 0 || !this.isOnGround()) {
            return;
        }
        super.onPlayerJump($$0);
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected void executeRidersJump(float $$0, float $$1, float $$2) {
        double $$3 = this.getAttributeValue(Attributes.JUMP_STRENGTH) * (double)this.getBlockJumpFactor() + this.getJumpBoostPower();
        this.addDeltaMovement(this.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale((double)(22.2222f * $$0) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor()).add(0.0, (double)(1.4285f * $$0) * $$3, 0.0));
        this.dashCooldown = 55;
        this.setDashing(true);
        this.hasImpulse = true;
    }

    public boolean isDashing() {
        return this.entityData.get(DASH);
    }

    public void setDashing(boolean $$0) {
        this.entityData.set(DASH, $$0);
    }

    public boolean isPanicking() {
        return this.getBrain().checkMemory(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    public void handleStartJump(int $$0) {
        this.playSound(SoundEvents.CAMEL_DASH, 1.0f, 1.0f);
        this.setDashing(true);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAMEL_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CAMEL_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.CAMEL_HURT;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        if ($$1.getSoundType() == SoundType.SAND) {
            this.playSound(SoundEvents.CAMEL_STEP_SAND, 1.0f, 1.0f);
        } else {
            this.playSound(SoundEvents.CAMEL_STEP, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return TEMPTATION_ITEM.test($$0);
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if ($$0.isSecondaryUseActive()) {
            this.openCustomInventoryScreen($$0);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult $$3 = $$2.interactLivingEntity($$0, this, $$1);
        if ($$3.consumesAction()) {
            return $$3;
        }
        if (this.isFood($$2)) {
            return this.fedFood($$0, $$2);
        }
        if (this.getPassengers().size() < 2 && !this.isBaby()) {
            this.doPlayerRide($$0);
        }
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    protected void onLeashDistance(float $$0) {
        if ($$0 > 6.0f && this.isCamelSitting() && !this.isInPoseTransition()) {
            this.standUp();
        }
    }

    @Override
    protected boolean handleEating(Player $$0, ItemStack $$1) {
        boolean $$4;
        boolean $$3;
        boolean $$2;
        if (!this.isFood($$1)) {
            return false;
        }
        boolean bl = $$2 = this.getHealth() < this.getMaxHealth();
        if ($$2) {
            this.heal(2.0f);
        }
        boolean bl2 = $$3 = this.isTamed() && this.getAge() == 0 && this.canFallInLove();
        if ($$3) {
            this.setInLove($$0);
        }
        if ($$4 = this.isBaby()) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp(10);
            }
        }
        if ($$2 || $$3 || $$4) {
            SoundEvent $$5;
            if (!this.isSilent() && ($$5 = this.getEatingSound()) != null) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), $$5, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean canMate(Animal $$0) {
        if ($$0 == this) return false;
        if (!($$0 instanceof Camel)) return false;
        Camel $$1 = (Camel)$$0;
        if (!this.canParent()) return false;
        if (!$$1.canParent()) return false;
        return true;
    }

    @Override
    @Nullable
    public Camel getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.CAMEL.create($$0);
    }

    @Override
    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.CAMEL_EAT;
    }

    @Override
    protected void actuallyHurt(DamageSource $$0, float $$1) {
        this.standUpPanic();
        super.actuallyHurt($$0, $$1);
    }

    @Override
    public void positionRider(Entity $$0) {
        int $$1 = this.getPassengers().indexOf((Object)$$0);
        if ($$1 < 0) {
            return;
        }
        boolean $$2 = $$1 == 0;
        float $$3 = 0.5f;
        float $$4 = (float)(this.isRemoved() ? (double)0.01f : this.getBodyAnchorAnimationYOffset($$2, 0.0f) + $$0.getMyRidingOffset());
        if (this.getPassengers().size() > 1) {
            if (!$$2) {
                $$3 = -0.7f;
            }
            if ($$0 instanceof Animal) {
                $$3 += 0.2f;
            }
        }
        Vec3 $$5 = new Vec3(0.0, 0.0, $$3).yRot(-this.yBodyRot * ((float)Math.PI / 180));
        $$0.setPos(this.getX() + $$5.x, this.getY() + (double)$$4, this.getZ() + $$5.z);
        this.clampRotation($$0);
    }

    private double getBodyAnchorAnimationYOffset(boolean $$0, float $$1) {
        double $$2 = this.getPassengersRidingOffset();
        float $$3 = this.getScale() * 1.43f;
        float $$4 = $$3 - this.getScale() * 0.2f;
        float $$5 = $$3 - $$4;
        boolean $$6 = this.isInPoseTransition();
        boolean $$7 = this.isCamelSitting();
        if ($$6) {
            float $$12;
            int $$11;
            int $$8;
            int n = $$8 = $$7 ? 40 : 52;
            if ($$7) {
                int $$9 = 28;
                float $$10 = $$0 ? 0.5f : 0.1f;
            } else {
                $$11 = $$0 ? 24 : 32;
                $$12 = $$0 ? 0.6f : 0.35f;
            }
            float $$13 = Mth.clamp((float)this.getPoseTime() + $$1, 0.0f, (float)$$8);
            boolean $$14 = $$13 < (float)$$11;
            float $$15 = $$14 ? $$13 / (float)$$11 : ($$13 - (float)$$11) / (float)($$8 - $$11);
            float $$16 = $$3 - $$12 * $$4;
            $$2 += $$7 ? (double)Mth.lerp($$15, $$14 ? $$3 : $$16, $$14 ? $$16 : $$5) : (double)Mth.lerp($$15, $$14 ? $$5 - $$3 : $$5 - $$16, $$14 ? $$5 - $$16 : 0.0f);
        }
        if ($$7 && !$$6) {
            $$2 += (double)$$5;
        }
        return $$2;
    }

    @Override
    public Vec3 getLeashOffset(float $$0) {
        return new Vec3(0.0, this.getBodyAnchorAnimationYOffset(true, $$0) - (double)(0.2f * this.getScale()), this.getBbWidth() * 0.56f);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getDimensions((Pose)(this.isCamelSitting() ? Pose.SITTING : Pose.STANDING)).height - (this.isBaby() ? 0.35f : 0.6f);
    }

    @Override
    public void onPassengerTurned(Entity $$0) {
        if (this.getControllingPassenger() != $$0) {
            this.clampRotation($$0);
        }
    }

    private void clampRotation(Entity $$0) {
        $$0.setYBodyRot(this.getYRot());
        float $$1 = $$0.getYRot();
        float $$2 = Mth.wrapDegrees($$1 - this.getYRot());
        float $$3 = Mth.clamp($$2, -160.0f, 160.0f);
        $$0.yRotO += $$3 - $$2;
        float $$4 = $$1 + $$3 - $$2;
        $$0.setYRot($$4);
        $$0.setYHeadRot($$4);
    }

    private void clampHeadRotationToBody(Entity $$0, float $$1) {
        float $$2 = $$0.getYHeadRot();
        float $$3 = Mth.wrapDegrees(this.yBodyRot - $$2);
        float $$4 = Mth.clamp(Mth.wrapDegrees(this.yBodyRot - $$2), -$$1, $$1);
        float $$5 = $$2 + $$3 - $$4;
        $$0.setYHeadRot($$5);
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    protected boolean canAddPassenger(Entity $$0) {
        return this.getPassengers().size() <= 2;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity $$0;
        if (!this.getPassengers().isEmpty() && this.isSaddled() && ($$0 = (Entity)this.getPassengers().get(0)) instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)$$0;
            return $$1;
        }
        return null;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public boolean isCamelSitting() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }

    public boolean isInPoseTransition() {
        long $$0 = this.getPoseTime();
        return $$0 < (long)(this.isCamelSitting() ? 40 : 52);
    }

    private boolean isSittingDown() {
        return this.isCamelSitting() && this.getPoseTime() < 40L;
    }

    public void sitDown() {
        if (this.isCamelSitting()) {
            return;
        }
        this.playSound(SoundEvents.CAMEL_SIT, 1.0f, 1.0f);
        this.setPose(Pose.SITTING);
        this.resetLastPoseChangeTick(-this.level.getGameTime());
    }

    public void standUp() {
        if (!this.isCamelSitting()) {
            return;
        }
        this.playSound(SoundEvents.CAMEL_STAND, 1.0f, 1.0f);
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTick(this.level.getGameTime());
    }

    public void standUpPanic() {
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTickToFullStand(this.level.getGameTime());
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long $$0) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, $$0);
    }

    private void resetLastPoseChangeTickToFullStand(long $$0) {
        this.resetLastPoseChangeTick(Math.max((long)0L, (long)($$0 - 52L - 1L)));
    }

    public long getPoseTime() {
        return this.level.getGameTime() - Math.abs((long)this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    public SoundEvent getSaddleSoundEvent() {
        return SoundEvents.CAMEL_SADDLE;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (!this.firstTick && DASH.equals($$0)) {
            this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new CamelBodyRotationControl(this);
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    public void openCustomInventoryScreen(Player $$0) {
        if (!this.level.isClientSide) {
            $$0.openHorseInventory(this, this.inventory);
        }
    }

    class CamelBodyRotationControl
    extends BodyRotationControl {
        public CamelBodyRotationControl(Camel $$0) {
            super($$0);
        }

        @Override
        public void clientTick() {
            if (!Camel.this.refuseToMove()) {
                super.clientTick();
            }
        }
    }
}