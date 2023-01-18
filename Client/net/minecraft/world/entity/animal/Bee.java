/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Byte
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Bee
extends Animal
implements NeutralMob,
FlyingAnimal {
    public static final float FLAP_DEGREES_PER_TICK = 120.32113f;
    public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966f);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.INT);
    private static final int FLAG_ROLL = 2;
    private static final int FLAG_HAS_STUNG = 4;
    private static final int FLAG_HAS_NECTAR = 8;
    private static final int STING_DEATH_COUNTDOWN = 1200;
    private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 2400;
    private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
    private static final int MIN_ATTACK_DIST = 4;
    private static final int MAX_CROPS_GROWABLE = 10;
    private static final int POISON_SECONDS_NORMAL = 10;
    private static final int POISON_SECONDS_HARD = 18;
    private static final int TOO_FAR_DISTANCE = 32;
    private static final int HIVE_CLOSE_ENOUGH_DISTANCE = 2;
    private static final int PATHFIND_TO_HIVE_WHEN_CLOSER_THAN = 16;
    private static final int HIVE_SEARCH_DISTANCE = 20;
    public static final String TAG_CROPS_GROWN_SINCE_POLLINATION = "CropsGrownSincePollination";
    public static final String TAG_CANNOT_ENTER_HIVE_TICKS = "CannotEnterHiveTicks";
    public static final String TAG_TICKS_SINCE_POLLINATION = "TicksSincePollination";
    public static final String TAG_HAS_STUNG = "HasStung";
    public static final String TAG_HAS_NECTAR = "HasNectar";
    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String TAG_HIVE_POS = "HivePos";
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    private float rollAmount;
    private float rollAmountO;
    private int timeSinceSting;
    int ticksWithoutNectarSinceExitingHive;
    private int stayOutOfHiveCountdown;
    private int numCropsGrownSincePollination;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
    int remainingCooldownBeforeLocatingNewHive;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
    int remainingCooldownBeforeLocatingNewFlower;
    @Nullable
    BlockPos savedFlowerPos;
    @Nullable
    BlockPos hivePos;
    BeePollinateGoal beePollinateGoal;
    BeeGoToHiveGoal goToHiveGoal;
    private BeeGoToKnownFlowerGoal goToKnownFlowerGoal;
    private int underWaterTicks;

    public Bee(EntityType<? extends Bee> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.lookControl = new BeeLookControl(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0f);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if ($$1.getBlockState($$0).isAir()) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeAttackGoal(this, 1.4f, true));
        this.goalSelector.addGoal(1, new BeeEnterHiveGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(ItemTags.FLOWERS), false));
        this.beePollinateGoal = new BeePollinateGoal();
        this.goalSelector.addGoal(4, this.beePollinateGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new BeeLocateHiveGoal());
        this.goToHiveGoal = new BeeGoToHiveGoal();
        this.goalSelector.addGoal(5, this.goToHiveGoal);
        this.goToKnownFlowerGoal = new BeeGoToKnownFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
        this.goalSelector.addGoal(7, new BeeGrowCropGoal());
        this.goalSelector.addGoal(8, new BeeWanderGoal());
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.targetSelector.addGoal(1, new BeeHurtByOtherGoal(this).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new BeeBecomeAngryTargetGoal(this));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<Bee>(this, true));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.hasHive()) {
            $$0.put(TAG_HIVE_POS, NbtUtils.writeBlockPos(this.getHivePos()));
        }
        if (this.hasSavedFlowerPos()) {
            $$0.put(TAG_FLOWER_POS, NbtUtils.writeBlockPos(this.getSavedFlowerPos()));
        }
        $$0.putBoolean(TAG_HAS_NECTAR, this.hasNectar());
        $$0.putBoolean(TAG_HAS_STUNG, this.hasStung());
        $$0.putInt(TAG_TICKS_SINCE_POLLINATION, this.ticksWithoutNectarSinceExitingHive);
        $$0.putInt(TAG_CANNOT_ENTER_HIVE_TICKS, this.stayOutOfHiveCountdown);
        $$0.putInt(TAG_CROPS_GROWN_SINCE_POLLINATION, this.numCropsGrownSincePollination);
        this.addPersistentAngerSaveData($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        this.hivePos = null;
        if ($$0.contains(TAG_HIVE_POS)) {
            this.hivePos = NbtUtils.readBlockPos($$0.getCompound(TAG_HIVE_POS));
        }
        this.savedFlowerPos = null;
        if ($$0.contains(TAG_FLOWER_POS)) {
            this.savedFlowerPos = NbtUtils.readBlockPos($$0.getCompound(TAG_FLOWER_POS));
        }
        super.readAdditionalSaveData($$0);
        this.setHasNectar($$0.getBoolean(TAG_HAS_NECTAR));
        this.setHasStung($$0.getBoolean(TAG_HAS_STUNG));
        this.ticksWithoutNectarSinceExitingHive = $$0.getInt(TAG_TICKS_SINCE_POLLINATION);
        this.stayOutOfHiveCountdown = $$0.getInt(TAG_CANNOT_ENTER_HIVE_TICKS);
        this.numCropsGrownSincePollination = $$0.getInt(TAG_CROPS_GROWN_SINCE_POLLINATION);
        this.readPersistentAngerSaveData(this.level, $$0);
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        boolean $$1 = $$0.hurt(DamageSource.sting(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if ($$1) {
            this.doEnchantDamageEffects(this, $$0);
            if ($$0 instanceof LivingEntity) {
                ((LivingEntity)$$0).setStingerCount(((LivingEntity)$$0).getStingerCount() + 1);
                int $$2 = 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    $$2 = 10;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    $$2 = 18;
                }
                if ($$2 > 0) {
                    ((LivingEntity)$$0).addEffect(new MobEffectInstance(MobEffects.POISON, $$2 * 20, 0), this);
                }
            }
            this.setHasStung(true);
            this.stopBeingAngry();
            this.playSound(SoundEvents.BEE_STING, 1.0f, 1.0f);
        }
        return $$1;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05f) {
            for (int $$0 = 0; $$0 < this.random.nextInt(2) + 1; ++$$0) {
                this.spawnFluidParticle(this.level, this.getX() - (double)0.3f, this.getX() + (double)0.3f, this.getZ() - (double)0.3f, this.getZ() + (double)0.3f, this.getY(0.5), ParticleTypes.FALLING_NECTAR);
            }
        }
        this.updateRollAmount();
    }

    private void spawnFluidParticle(Level $$0, double $$1, double $$2, double $$3, double $$4, double $$5, ParticleOptions $$6) {
        $$0.addParticle($$6, Mth.lerp($$0.random.nextDouble(), $$1, $$2), $$5, Mth.lerp($$0.random.nextDouble(), $$3, $$4), 0.0, 0.0, 0.0);
    }

    void pathfindRandomlyTowards(BlockPos $$0) {
        Vec3 $$8;
        Vec3 $$1 = Vec3.atBottomCenterOf($$0);
        int $$2 = 0;
        BlockPos $$3 = this.blockPosition();
        int $$4 = (int)$$1.y - $$3.getY();
        if ($$4 > 2) {
            $$2 = 4;
        } else if ($$4 < -2) {
            $$2 = -4;
        }
        int $$5 = 6;
        int $$6 = 8;
        int $$7 = $$3.distManhattan($$0);
        if ($$7 < 15) {
            $$5 = $$7 / 2;
            $$6 = $$7 / 2;
        }
        if (($$8 = AirRandomPos.getPosTowards(this, $$5, $$6, $$2, $$1, 0.3141592741012573)) == null) {
            return;
        }
        this.navigation.setMaxVisitedNodesMultiplier(0.5f);
        this.navigation.moveTo($$8.x, $$8.y, $$8.z, 1.0);
    }

    @Nullable
    public BlockPos getSavedFlowerPos() {
        return this.savedFlowerPos;
    }

    public boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    public void setSavedFlowerPos(BlockPos $$0) {
        this.savedFlowerPos = $$0;
    }

    @VisibleForDebug
    public int getTravellingTicks() {
        return Math.max((int)this.goToHiveGoal.travellingTicks, (int)this.goToKnownFlowerGoal.travellingTicks);
    }

    @VisibleForDebug
    public List<BlockPos> getBlacklistedHives() {
        return this.goToHiveGoal.blacklistedTargets;
    }

    private boolean isTiredOfLookingForNectar() {
        return this.ticksWithoutNectarSinceExitingHive > 3600;
    }

    boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown > 0 || this.beePollinateGoal.isPollinating() || this.hasStung() || this.getTarget() != null) {
            return false;
        }
        boolean $$0 = this.isTiredOfLookingForNectar() || this.level.isRaining() || this.level.isNight() || this.hasNectar();
        return $$0 && !this.isHiveNearFire();
    }

    public void setStayOutOfHiveCountdown(int $$0) {
        this.stayOutOfHiveCountdown = $$0;
    }

    public float getRollAmount(float $$0) {
        return Mth.lerp($$0, this.rollAmountO, this.rollAmount);
    }

    private void updateRollAmount() {
        this.rollAmountO = this.rollAmount;
        this.rollAmount = this.isRolling() ? Math.min((float)1.0f, (float)(this.rollAmount + 0.2f)) : Math.max((float)0.0f, (float)(this.rollAmount - 0.24f));
    }

    @Override
    protected void customServerAiStep() {
        boolean $$0 = this.hasStung();
        this.underWaterTicks = this.isInWaterOrBubble() ? ++this.underWaterTicks : 0;
        if (this.underWaterTicks > 20) {
            this.hurt(DamageSource.DROWN, 1.0f);
        }
        if ($$0) {
            ++this.timeSinceSting;
            if (this.timeSinceSting % 5 == 0 && this.random.nextInt(Mth.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
                this.hurt(DamageSource.GENERIC, this.getHealth());
            }
        }
        if (!this.hasNectar()) {
            ++this.ticksWithoutNectarSinceExitingHive;
        }
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, false);
        }
    }

    public void resetTicksWithoutNectarSinceExitingHive() {
        this.ticksWithoutNectarSinceExitingHive = 0;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        }
        BlockEntity $$0 = this.level.getBlockEntity(this.hivePos);
        return $$0 instanceof BeehiveBlockEntity && ((BeehiveBlockEntity)$$0).isFireNearby();
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int $$0) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, $$0);
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID $$0) {
        this.persistentAngerTarget = $$0;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    private boolean doesHiveHaveSpace(BlockPos $$0) {
        BlockEntity $$1 = this.level.getBlockEntity($$0);
        if ($$1 instanceof BeehiveBlockEntity) {
            return !((BeehiveBlockEntity)$$1).isFull();
        }
        return false;
    }

    @VisibleForDebug
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPos getHivePos() {
        return this.hivePos;
    }

    @VisibleForDebug
    public GoalSelector getGoalSelector() {
        return this.goalSelector;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendBeeInfo(this);
    }

    int getCropsGrownSincePollination() {
        return this.numCropsGrownSincePollination;
    }

    private void resetNumCropsGrownSincePollination() {
        this.numCropsGrownSincePollination = 0;
    }

    void incrementNumCropsGrownSincePollination() {
        ++this.numCropsGrownSincePollination;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }
            if (this.remainingCooldownBeforeLocatingNewHive > 0) {
                --this.remainingCooldownBeforeLocatingNewHive;
            }
            if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
                --this.remainingCooldownBeforeLocatingNewFlower;
            }
            boolean $$0 = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0;
            this.setRolling($$0);
            if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
        }
    }

    boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        }
        if (this.isTooFarAway(this.hivePos)) {
            return false;
        }
        BlockEntity $$0 = this.level.getBlockEntity(this.hivePos);
        return $$0 != null && $$0.getType() == BlockEntityType.BEEHIVE;
    }

    public boolean hasNectar() {
        return this.getFlag(8);
    }

    void setHasNectar(boolean $$0) {
        if ($$0) {
            this.resetTicksWithoutNectarSinceExitingHive();
        }
        this.setFlag(8, $$0);
    }

    public boolean hasStung() {
        return this.getFlag(4);
    }

    private void setHasStung(boolean $$0) {
        this.setFlag(4, $$0);
    }

    private boolean isRolling() {
        return this.getFlag(2);
    }

    private void setRolling(boolean $$0) {
        this.setFlag(2, $$0);
    }

    boolean isTooFarAway(BlockPos $$0) {
        return !this.closerThan($$0, 32);
    }

    private void setFlag(int $$0, boolean $$1) {
        if ($$1) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | $$0));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~$$0));
        }
    }

    private boolean getFlag(int $$0) {
        return (this.entityData.get(DATA_FLAGS_ID) & $$0) != 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FLYING_SPEED, 0.6f).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        FlyingPathNavigation $$1 = new FlyingPathNavigation(this, $$0){

            @Override
            public boolean isStableDestination(BlockPos $$0) {
                return !this.level.getBlockState((BlockPos)$$0.below()).isAir();
            }

            @Override
            public void tick() {
                if (Bee.this.beePollinateGoal.isPollinating()) {
                    return;
                }
                super.tick();
            }
        };
        $$1.setCanOpenDoors(false);
        $$1.setCanFloat(false);
        $$1.setCanPassDoors(true);
        return $$1;
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(ItemTags.FLOWERS);
    }

    boolean isFlowerValid(BlockPos $$0) {
        return this.level.isLoaded($$0) && this.level.getBlockState($$0).is(BlockTags.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    @Nullable
    public Bee getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.BEE.create($$0);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        if (this.isBaby()) {
            return $$1.height * 0.5f;
        }
        return $$1.height * 0.5f;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }

    public void dropOffNectar() {
        this.setHasNectar(false);
        this.resetNumCropsGrownSincePollination();
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!this.level.isClientSide) {
            this.beePollinateGoal.stopPollinating();
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected void jumpInLiquid(TagKey<Fluid> $$0) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5f * this.getEyeHeight(), this.getBbWidth() * 0.2f);
    }

    boolean closerThan(BlockPos $$0, int $$1) {
        return $$0.closerThan(this.blockPosition(), $$1);
    }

    class BeePollinateGoal
    extends BaseBeeGoal {
        private static final int MIN_POLLINATION_TICKS = 400;
        private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
        private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
        private final Predicate<BlockState> VALID_POLLINATION_BLOCKS;
        private static final double ARRIVAL_THRESHOLD = 0.1;
        private static final int POSITION_CHANGE_CHANCE = 25;
        private static final float SPEED_MODIFIER = 0.35f;
        private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6f;
        private static final float HOVER_POS_OFFSET = 0.33333334f;
        private int successfulPollinatingTicks;
        private int lastSoundPlayedTick;
        private boolean pollinating;
        @Nullable
        private Vec3 hoverPos;
        private int pollinatingTicks;
        private static final int MAX_POLLINATING_TICKS = 600;

        BeePollinateGoal() {
            this.VALID_POLLINATION_BLOCKS = $$0 -> {
                if ($$0.hasProperty(BlockStateProperties.WATERLOGGED) && $$0.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
                    return false;
                }
                if ($$0.is(BlockTags.FLOWERS)) {
                    if ($$0.is(Blocks.SUNFLOWER)) {
                        return $$0.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
                    }
                    return true;
                }
                return false;
            };
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            if (Bee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            }
            if (Bee.this.hasNectar()) {
                return false;
            }
            if (Bee.this.level.isRaining()) {
                return false;
            }
            Optional<BlockPos> $$0 = this.findNearbyFlower();
            if ($$0.isPresent()) {
                Bee.this.savedFlowerPos = (BlockPos)$$0.get();
                Bee.this.navigation.moveTo((double)Bee.this.savedFlowerPos.getX() + 0.5, (double)Bee.this.savedFlowerPos.getY() + 0.5, (double)Bee.this.savedFlowerPos.getZ() + 0.5, 1.2f);
                return true;
            }
            Bee.this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(Bee.this.random, 20, 60);
            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            if (!this.pollinating) {
                return false;
            }
            if (!Bee.this.hasSavedFlowerPos()) {
                return false;
            }
            if (Bee.this.level.isRaining()) {
                return false;
            }
            if (this.hasPollinatedLongEnough()) {
                return Bee.this.random.nextFloat() < 0.2f;
            }
            if (Bee.this.tickCount % 20 == 0 && !Bee.this.isFlowerValid(Bee.this.savedFlowerPos)) {
                Bee.this.savedFlowerPos = null;
                return false;
            }
            return true;
        }

        private boolean hasPollinatedLongEnough() {
            return this.successfulPollinatingTicks > 400;
        }

        boolean isPollinating() {
            return this.pollinating;
        }

        void stopPollinating() {
            this.pollinating = false;
        }

        @Override
        public void start() {
            this.successfulPollinatingTicks = 0;
            this.pollinatingTicks = 0;
            this.lastSoundPlayedTick = 0;
            this.pollinating = true;
            Bee.this.resetTicksWithoutNectarSinceExitingHive();
        }

        @Override
        public void stop() {
            if (this.hasPollinatedLongEnough()) {
                Bee.this.setHasNectar(true);
            }
            this.pollinating = false;
            Bee.this.navigation.stop();
            Bee.this.remainingCooldownBeforeLocatingNewFlower = 200;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.pollinatingTicks;
            if (this.pollinatingTicks > 600) {
                Bee.this.savedFlowerPos = null;
                return;
            }
            Vec3 $$0 = Vec3.atBottomCenterOf(Bee.this.savedFlowerPos).add(0.0, 0.6f, 0.0);
            if ($$0.distanceTo(Bee.this.position()) > 1.0) {
                this.hoverPos = $$0;
                this.setWantedPos();
                return;
            }
            if (this.hoverPos == null) {
                this.hoverPos = $$0;
            }
            boolean $$1 = Bee.this.position().distanceTo(this.hoverPos) <= 0.1;
            boolean $$2 = true;
            if (!$$1 && this.pollinatingTicks > 600) {
                Bee.this.savedFlowerPos = null;
                return;
            }
            if ($$1) {
                boolean $$3;
                boolean bl = $$3 = Bee.this.random.nextInt(25) == 0;
                if ($$3) {
                    this.hoverPos = new Vec3($$0.x() + (double)this.getOffset(), $$0.y(), $$0.z() + (double)this.getOffset());
                    Bee.this.navigation.stop();
                } else {
                    $$2 = false;
                }
                Bee.this.getLookControl().setLookAt($$0.x(), $$0.y(), $$0.z());
            }
            if ($$2) {
                this.setWantedPos();
            }
            ++this.successfulPollinatingTicks;
            if (Bee.this.random.nextFloat() < 0.05f && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                Bee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0f, 1.0f);
            }
        }

        private void setWantedPos() {
            Bee.this.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.35f);
        }

        private float getOffset() {
            return (Bee.this.random.nextFloat() * 2.0f - 1.0f) * 0.33333334f;
        }

        private Optional<BlockPos> findNearbyFlower() {
            return this.findNearestBlock(this.VALID_POLLINATION_BLOCKS, 5.0);
        }

        private Optional<BlockPos> findNearestBlock(Predicate<BlockState> $$0, double $$1) {
            BlockPos $$2 = Bee.this.blockPosition();
            BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
            int $$4 = 0;
            while ((double)$$4 <= $$1) {
                int $$5 = 0;
                while ((double)$$5 < $$1) {
                    int $$6 = 0;
                    while ($$6 <= $$5) {
                        int $$7;
                        int n = $$7 = $$6 < $$5 && $$6 > -$$5 ? $$5 : 0;
                        while ($$7 <= $$5) {
                            $$3.setWithOffset($$2, $$6, $$4 - 1, $$7);
                            if ($$2.closerThan($$3, $$1) && $$0.test((Object)Bee.this.level.getBlockState($$3))) {
                                return Optional.of((Object)$$3);
                            }
                            $$7 = $$7 > 0 ? -$$7 : 1 - $$7;
                        }
                        $$6 = $$6 > 0 ? -$$6 : 1 - $$6;
                    }
                    ++$$5;
                }
                $$4 = $$4 > 0 ? -$$4 : 1 - $$4;
            }
            return Optional.empty();
        }
    }

    class BeeLookControl
    extends LookControl {
        BeeLookControl(Mob $$0) {
            super($$0);
        }

        @Override
        public void tick() {
            if (Bee.this.isAngry()) {
                return;
            }
            super.tick();
        }

        @Override
        protected boolean resetXRotOnTick() {
            return !Bee.this.beePollinateGoal.isPollinating();
        }
    }

    class BeeAttackGoal
    extends MeleeAttackGoal {
        BeeAttackGoal(PathfinderMob $$0, double $$1, boolean $$2) {
            super($$0, $$1, $$2);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Bee.this.isAngry() && !Bee.this.hasStung();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && Bee.this.isAngry() && !Bee.this.hasStung();
        }
    }

    class BeeEnterHiveGoal
    extends BaseBeeGoal {
        BeeEnterHiveGoal() {
        }

        @Override
        public boolean canBeeUse() {
            BlockEntity $$0;
            if (Bee.this.hasHive() && Bee.this.wantsToEnterHive() && Bee.this.hivePos.closerToCenterThan(Bee.this.position(), 2.0) && ($$0 = Bee.this.level.getBlockEntity(Bee.this.hivePos)) instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity $$1 = (BeehiveBlockEntity)$$0;
                if ($$1.isFull()) {
                    Bee.this.hivePos = null;
                } else {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            BlockEntity $$0 = Bee.this.level.getBlockEntity(Bee.this.hivePos);
            if ($$0 instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity $$1 = (BeehiveBlockEntity)$$0;
                $$1.addOccupant(Bee.this, Bee.this.hasNectar());
            }
        }
    }

    class BeeLocateHiveGoal
    extends BaseBeeGoal {
        BeeLocateHiveGoal() {
        }

        @Override
        public boolean canBeeUse() {
            return Bee.this.remainingCooldownBeforeLocatingNewHive == 0 && !Bee.this.hasHive() && Bee.this.wantsToEnterHive();
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Bee.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPos> $$0 = this.findNearbyHivesWithSpace();
            if ($$0.isEmpty()) {
                return;
            }
            for (BlockPos $$1 : $$0) {
                if (Bee.this.goToHiveGoal.isTargetBlacklisted($$1)) continue;
                Bee.this.hivePos = $$1;
                return;
            }
            Bee.this.goToHiveGoal.clearBlacklist();
            Bee.this.hivePos = (BlockPos)$$0.get(0);
        }

        private List<BlockPos> findNearbyHivesWithSpace() {
            BlockPos $$02 = Bee.this.blockPosition();
            PoiManager $$12 = ((ServerLevel)Bee.this.level).getPoiManager();
            Stream<PoiRecord> $$2 = $$12.getInRange((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypeTags.BEE_HOME)), $$02, 20, PoiManager.Occupancy.ANY);
            return (List)$$2.map(PoiRecord::getPos).filter(Bee.this::doesHiveHaveSpace).sorted(Comparator.comparingDouble($$1 -> $$1.distSqr($$02))).collect(Collectors.toList());
        }
    }

    @VisibleForDebug
    public class BeeGoToHiveGoal
    extends BaseBeeGoal {
        public static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;
        private static final int MAX_BLACKLISTED_TARGETS = 3;
        final List<BlockPos> blacklistedTargets;
        @Nullable
        private Path lastPath;
        private static final int TICKS_BEFORE_HIVE_DROP = 60;
        private int ticksStuck;

        BeeGoToHiveGoal() {
            this.travellingTicks = Bee.this.level.random.nextInt(10);
            this.blacklistedTargets = Lists.newArrayList();
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            return Bee.this.hivePos != null && !Bee.this.hasRestriction() && Bee.this.wantsToEnterHive() && !this.hasReachedTarget(Bee.this.hivePos) && Bee.this.level.getBlockState(Bee.this.hivePos).is(BlockTags.BEEHIVES);
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            Bee.this.navigation.stop();
            Bee.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (Bee.this.hivePos == null) {
                return;
            }
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
                this.dropAndBlacklistHive();
                return;
            }
            if (Bee.this.navigation.isInProgress()) {
                return;
            }
            if (Bee.this.closerThan(Bee.this.hivePos, 16)) {
                boolean $$0 = this.pathfindDirectlyTowards(Bee.this.hivePos);
                if (!$$0) {
                    this.dropAndBlacklistHive();
                } else if (this.lastPath != null && Bee.this.navigation.getPath().sameAs(this.lastPath)) {
                    ++this.ticksStuck;
                    if (this.ticksStuck > 60) {
                        this.dropHive();
                        this.ticksStuck = 0;
                    }
                } else {
                    this.lastPath = Bee.this.navigation.getPath();
                }
                return;
            }
            if (Bee.this.isTooFarAway(Bee.this.hivePos)) {
                this.dropHive();
                return;
            }
            Bee.this.pathfindRandomlyTowards(Bee.this.hivePos);
        }

        private boolean pathfindDirectlyTowards(BlockPos $$0) {
            Bee.this.navigation.setMaxVisitedNodesMultiplier(10.0f);
            Bee.this.navigation.moveTo($$0.getX(), $$0.getY(), $$0.getZ(), 1.0);
            return Bee.this.navigation.getPath() != null && Bee.this.navigation.getPath().canReach();
        }

        boolean isTargetBlacklisted(BlockPos $$0) {
            return this.blacklistedTargets.contains((Object)$$0);
        }

        private void blacklistTarget(BlockPos $$0) {
            this.blacklistedTargets.add((Object)$$0);
            while (this.blacklistedTargets.size() > 3) {
                this.blacklistedTargets.remove(0);
            }
        }

        void clearBlacklist() {
            this.blacklistedTargets.clear();
        }

        private void dropAndBlacklistHive() {
            if (Bee.this.hivePos != null) {
                this.blacklistTarget(Bee.this.hivePos);
            }
            this.dropHive();
        }

        private void dropHive() {
            Bee.this.hivePos = null;
            Bee.this.remainingCooldownBeforeLocatingNewHive = 200;
        }

        private boolean hasReachedTarget(BlockPos $$0) {
            if (Bee.this.closerThan($$0, 2)) {
                return true;
            }
            Path $$1 = Bee.this.navigation.getPath();
            return $$1 != null && $$1.getTarget().equals($$0) && $$1.canReach() && $$1.isDone();
        }
    }

    public class BeeGoToKnownFlowerGoal
    extends BaseBeeGoal {
        private static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;

        BeeGoToKnownFlowerGoal() {
            this.travellingTicks = Bee.this.level.random.nextInt(10);
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            return Bee.this.savedFlowerPos != null && !Bee.this.hasRestriction() && this.wantsToGoToKnownFlower() && Bee.this.isFlowerValid(Bee.this.savedFlowerPos) && !Bee.this.closerThan(Bee.this.savedFlowerPos, 2);
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.travellingTicks = 0;
            Bee.this.navigation.stop();
            Bee.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (Bee.this.savedFlowerPos == null) {
                return;
            }
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
                Bee.this.savedFlowerPos = null;
                return;
            }
            if (Bee.this.navigation.isInProgress()) {
                return;
            }
            if (Bee.this.isTooFarAway(Bee.this.savedFlowerPos)) {
                Bee.this.savedFlowerPos = null;
                return;
            }
            Bee.this.pathfindRandomlyTowards(Bee.this.savedFlowerPos);
        }

        private boolean wantsToGoToKnownFlower() {
            return Bee.this.ticksWithoutNectarSinceExitingHive > 2400;
        }
    }

    class BeeGrowCropGoal
    extends BaseBeeGoal {
        static final int GROW_CHANCE = 30;

        BeeGrowCropGoal() {
        }

        @Override
        public boolean canBeeUse() {
            if (Bee.this.getCropsGrownSincePollination() >= 10) {
                return false;
            }
            if (Bee.this.random.nextFloat() < 0.3f) {
                return false;
            }
            return Bee.this.hasNectar() && Bee.this.isHiveValid();
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void tick() {
            if (Bee.this.random.nextInt(this.adjustedTickDelay(30)) != 0) {
                return;
            }
            for (int $$0 = 1; $$0 <= 2; ++$$0) {
                Vec3i $$1 = Bee.this.blockPosition().below($$0);
                BlockState $$2 = Bee.this.level.getBlockState((BlockPos)$$1);
                Block $$3 = $$2.getBlock();
                boolean $$4 = false;
                IntegerProperty $$5 = null;
                if (!$$2.is(BlockTags.BEE_GROWABLES)) continue;
                if ($$3 instanceof CropBlock) {
                    CropBlock $$6 = (CropBlock)$$3;
                    if (!$$6.isMaxAge($$2)) {
                        $$4 = true;
                        $$5 = $$6.getAgeProperty();
                    }
                } else if ($$3 instanceof StemBlock) {
                    int $$7 = $$2.getValue(StemBlock.AGE);
                    if ($$7 < 7) {
                        $$4 = true;
                        $$5 = StemBlock.AGE;
                    }
                } else if ($$2.is(Blocks.SWEET_BERRY_BUSH)) {
                    int $$8 = $$2.getValue(SweetBerryBushBlock.AGE);
                    if ($$8 < 3) {
                        $$4 = true;
                        $$5 = SweetBerryBushBlock.AGE;
                    }
                } else if ($$2.is(Blocks.CAVE_VINES) || $$2.is(Blocks.CAVE_VINES_PLANT)) {
                    ((BonemealableBlock)((Object)$$2.getBlock())).performBonemeal((ServerLevel)Bee.this.level, Bee.this.random, (BlockPos)$$1, $$2);
                }
                if (!$$4) continue;
                Bee.this.level.levelEvent(2005, (BlockPos)$$1, 0);
                Bee.this.level.setBlockAndUpdate((BlockPos)$$1, (BlockState)$$2.setValue($$5, $$2.getValue($$5) + 1));
                Bee.this.incrementNumCropsGrownSincePollination();
            }
        }
    }

    class BeeWanderGoal
    extends Goal {
        private static final int WANDER_THRESHOLD = 22;

        BeeWanderGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Bee.this.navigation.isDone() && Bee.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Bee.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 $$0 = this.findPos();
            if ($$0 != null) {
                Bee.this.navigation.moveTo(Bee.this.navigation.createPath(new BlockPos($$0), 1), 1.0);
            }
        }

        @Nullable
        private Vec3 findPos() {
            Vec3 $$2;
            if (Bee.this.isHiveValid() && !Bee.this.closerThan(Bee.this.hivePos, 22)) {
                Vec3 $$0 = Vec3.atCenterOf(Bee.this.hivePos);
                Vec3 $$1 = $$0.subtract(Bee.this.position()).normalize();
            } else {
                $$2 = Bee.this.getViewVector(0.0f);
            }
            int $$3 = 8;
            Vec3 $$4 = HoverRandomPos.getPos(Bee.this, 8, 7, $$2.x, $$2.z, 1.5707964f, 3, 1);
            if ($$4 != null) {
                return $$4;
            }
            return AirAndWaterRandomPos.getPos(Bee.this, 8, 4, -2, $$2.x, $$2.z, 1.5707963705062866);
        }
    }

    class BeeHurtByOtherGoal
    extends HurtByTargetGoal {
        BeeHurtByOtherGoal(Bee $$0) {
            super($$0, new Class[0]);
        }

        @Override
        public boolean canContinueToUse() {
            return Bee.this.isAngry() && super.canContinueToUse();
        }

        @Override
        protected void alertOther(Mob $$0, LivingEntity $$1) {
            if ($$0 instanceof Bee && this.mob.hasLineOfSight($$1)) {
                $$0.setTarget($$1);
            }
        }
    }

    static class BeeBecomeAngryTargetGoal
    extends NearestAttackableTargetGoal<Player> {
        BeeBecomeAngryTargetGoal(Bee $$0) {
            super($$0, Player.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)$$0::isAngryAt));
        }

        @Override
        public boolean canUse() {
            return this.beeCanTarget() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            boolean $$0 = this.beeCanTarget();
            if (!$$0 || this.mob.getTarget() == null) {
                this.targetMob = null;
                return false;
            }
            return super.canContinueToUse();
        }

        private boolean beeCanTarget() {
            Bee $$0 = (Bee)this.mob;
            return $$0.isAngry() && !$$0.hasStung();
        }
    }

    abstract class BaseBeeGoal
    extends Goal {
        BaseBeeGoal() {
        }

        public abstract boolean canBeeUse();

        public abstract boolean canBeeContinueToUse();

        @Override
        public boolean canUse() {
            return this.canBeeUse() && !Bee.this.isAngry();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canBeeContinueToUse() && !Bee.this.isAngry();
        }
    }
}