/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.animal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Turtle
extends Animal {
    private static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    public static final Ingredient FOOD_ITEMS = Ingredient.of(Blocks.SEAGRASS.asItem());
    int layEggCounter;
    public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR = $$0 -> $$0.isBaby() && !$$0.isInWater();

    public Turtle(EntityType<? extends Turtle> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DOOR_IRON_CLOSED, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, -1.0f);
        this.moveControl = new TurtleMoveControl(this);
        this.maxUpStep = 1.0f;
    }

    public void setHomePos(BlockPos $$0) {
        this.entityData.set(HOME_POS, $$0);
    }

    BlockPos getHomePos() {
        return this.entityData.get(HOME_POS);
    }

    void setTravelPos(BlockPos $$0) {
        this.entityData.set(TRAVEL_POS, $$0);
    }

    BlockPos getTravelPos() {
        return this.entityData.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    void setHasEgg(boolean $$0) {
        this.entityData.set(HAS_EGG, $$0);
    }

    public boolean isLayingEgg() {
        return this.entityData.get(LAYING_EGG);
    }

    void setLayingEgg(boolean $$0) {
        this.layEggCounter = $$0 ? 1 : 0;
        this.entityData.set(LAYING_EGG, $$0);
    }

    boolean isGoingHome() {
        return this.entityData.get(GOING_HOME);
    }

    void setGoingHome(boolean $$0) {
        this.entityData.set(GOING_HOME, $$0);
    }

    boolean isTravelling() {
        return this.entityData.get(TRAVELLING);
    }

    void setTravelling(boolean $$0) {
        this.entityData.set(TRAVELLING, $$0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOME_POS, BlockPos.ZERO);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
        this.entityData.define(GOING_HOME, false);
        this.entityData.define(TRAVELLING, false);
        this.entityData.define(LAYING_EGG, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("HomePosX", this.getHomePos().getX());
        $$0.putInt("HomePosY", this.getHomePos().getY());
        $$0.putInt("HomePosZ", this.getHomePos().getZ());
        $$0.putBoolean("HasEgg", this.hasEgg());
        $$0.putInt("TravelPosX", this.getTravelPos().getX());
        $$0.putInt("TravelPosY", this.getTravelPos().getY());
        $$0.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        int $$1 = $$0.getInt("HomePosX");
        int $$2 = $$0.getInt("HomePosY");
        int $$3 = $$0.getInt("HomePosZ");
        this.setHomePos(new BlockPos($$1, $$2, $$3));
        super.readAdditionalSaveData($$0);
        this.setHasEgg($$0.getBoolean("HasEgg"));
        int $$4 = $$0.getInt("TravelPosX");
        int $$5 = $$0.getInt("TravelPosY");
        int $$6 = $$0.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos($$4, $$5, $$6));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setHomePos(this.blockPosition());
        this.setTravelPos(BlockPos.ZERO);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public static boolean checkTurtleSpawnRules(EntityType<Turtle> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$3.getY() < $$1.getSeaLevel() + 4 && TurtleEggBlock.onSand($$1, $$3) && Turtle.isBrightEnoughToSpawn($$1, $$3);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TurtlePanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new TurtleBreedGoal(this, 1.0));
        this.goalSelector.addGoal(1, new TurtleLayEggGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, FOOD_ITEMS, false));
        this.goalSelector.addGoal(3, new TurtleGoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TurtleGoHomeGoal(this, 1.0));
        this.goalSelector.addGoal(7, new TurtleTravelGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(9, new TurtleRandomStrollGoal(this, 1.0, 100));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (!this.isInWater() && this.onGround && !this.isBaby()) {
            return SoundEvents.TURTLE_AMBIENT_LAND;
        }
        return super.getAmbientSound();
    }

    protected void playSwimSound(float $$0) {
        super.playSwimSound($$0 * 1.5f);
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.TURTLE_SWIM;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_HURT_BABY;
        }
        return SoundEvents.TURTLE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_DEATH_BABY;
        }
        return SoundEvents.TURTLE_DEATH;
    }

    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        SoundEvent $$2 = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
        this.playSound($$2, 0.15f, 1.0f);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    protected float nextStep() {
        return this.moveDist + 0.15f;
    }

    public float getScale() {
        return this.isBaby() ? 0.3f : 1.0f;
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new TurtlePathNavigation(this, $$0);
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.TURTLE.create($$0);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(Blocks.SEAGRASS.asItem());
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if (!this.isGoingHome() && $$1.getFluidState($$0).is(FluidTags.WATER)) {
            return 10.0f;
        }
        if (TurtleEggBlock.onSand($$1, $$0)) {
            return 10.0f;
        }
        return $$1.getPathfindingCostFromLightLevels($$0);
    }

    @Override
    public void aiStep() {
        BlockPos $$0;
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0 && TurtleEggBlock.onSand(this.level, $$0 = this.blockPosition())) {
            this.level.levelEvent(2001, $$0, Block.getId(this.level.getBlockState((BlockPos)$$0.below())));
        }
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SCUTE, 1);
        }
    }

    public void travel(Vec3 $$0) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1f, $$0);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (!(this.getTarget() != null || this.isGoingHome() && this.getHomePos().closerToCenterThan(this.position(), 20.0))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel($$0);
        }
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return false;
    }

    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static class TurtleMoveControl
    extends MoveControl {
        private final Turtle turtle;

        TurtleMoveControl(Turtle $$0) {
            super($$0);
            this.turtle = $$0;
        }

        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 16.0)) {
                    this.turtle.setSpeed(Math.max((float)(this.turtle.getSpeed() / 2.0f), (float)0.08f));
                }
                if (this.turtle.isBaby()) {
                    this.turtle.setSpeed(Math.max((float)(this.turtle.getSpeed() / 3.0f), (float)0.06f));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setSpeed(Math.max((float)(this.turtle.getSpeed() / 2.0f), (float)0.06f));
            }
        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.operation != MoveControl.Operation.MOVE_TO || this.turtle.getNavigation().isDone()) {
                this.turtle.setSpeed(0.0f);
                return;
            }
            double $$0 = this.wantedX - this.turtle.getX();
            double $$1 = this.wantedY - this.turtle.getY();
            double $$2 = this.wantedZ - this.turtle.getZ();
            double $$3 = Math.sqrt((double)($$0 * $$0 + $$1 * $$1 + $$2 * $$2));
            $$1 /= $$3;
            float $$4 = (float)(Mth.atan2($$2, $$0) * 57.2957763671875) - 90.0f;
            this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), $$4, 90.0f));
            this.turtle.yBodyRot = this.turtle.getYRot();
            float $$5 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.turtle.setSpeed(Mth.lerp(0.125f, this.turtle.getSpeed(), $$5));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, (double)this.turtle.getSpeed() * $$1 * 0.1, 0.0));
        }
    }

    static class TurtlePanicGoal
    extends PanicGoal {
        TurtlePanicGoal(Turtle $$0, double $$1) {
            super($$0, $$1);
        }

        @Override
        public boolean canUse() {
            if (!this.shouldPanic()) {
                return false;
            }
            BlockPos $$0 = this.lookForWater(this.mob.level, (Entity)((Object)this.mob), 7);
            if ($$0 != null) {
                this.posX = $$0.getX();
                this.posY = $$0.getY();
                this.posZ = $$0.getZ();
                return true;
            }
            return this.findRandomPosition();
        }
    }

    static class TurtleBreedGoal
    extends BreedGoal {
        private final Turtle turtle;

        TurtleBreedGoal(Turtle $$0, double $$1) {
            super($$0, $$1);
            this.turtle = $$0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.turtle.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayer $$0 = this.animal.getLoveCause();
            if ($$0 == null && this.partner.getLoveCause() != null) {
                $$0 = this.partner.getLoveCause();
            }
            if ($$0 != null) {
                $$0.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger($$0, this.animal, this.partner, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            RandomSource $$1 = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), $$1.nextInt(7) + 1));
            }
        }
    }

    static class TurtleLayEggGoal
    extends MoveToBlockGoal {
        private final Turtle turtle;

        TurtleLayEggGoal(Turtle $$0, double $$1) {
            super($$0, $$1, 16);
            this.turtle = $$0;
        }

        @Override
        public boolean canUse() {
            if (this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0)) {
                return super.canUse();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos $$0 = this.turtle.blockPosition();
            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                if (this.turtle.layEggCounter < 1) {
                    this.turtle.setLayingEgg(true);
                } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
                    Level $$1 = this.turtle.level;
                    $$1.playSound((Player)null, $$0, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3f, 0.9f + $$1.random.nextFloat() * 0.2f);
                    $$1.setBlock((BlockPos)this.blockPos.above(), (BlockState)Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setLayingEgg(false);
                    this.turtle.setInLoveTime(600);
                }
                if (this.turtle.isLayingEgg()) {
                    ++this.turtle.layEggCounter;
                }
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader $$0, BlockPos $$1) {
            if (!$$0.isEmptyBlock((BlockPos)$$1.above())) {
                return false;
            }
            return TurtleEggBlock.isSand($$0, $$1);
        }
    }

    static class TurtleGoToWaterGoal
    extends MoveToBlockGoal {
        private static final int GIVE_UP_TICKS = 1200;
        private final Turtle turtle;

        TurtleGoToWaterGoal(Turtle $$0, double $$1) {
            super($$0, $$0.isBaby() ? 2.0 : $$1, 24);
            this.turtle = $$0;
            this.verticalSearchStart = -1;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            if (this.turtle.isBaby() && !this.turtle.isInWater()) {
                return super.canUse();
            }
            if (!(this.turtle.isGoingHome() || this.turtle.isInWater() || this.turtle.hasEgg())) {
                return super.canUse();
            }
            return false;
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 160 == 0;
        }

        @Override
        protected boolean isValidTarget(LevelReader $$0, BlockPos $$1) {
            return $$0.getBlockState($$1).is(Blocks.WATER);
        }
    }

    static class TurtleGoHomeGoal
    extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;
        private int closeToHomeTryTicks;
        private static final int GIVE_UP_TICKS = 600;

        TurtleGoHomeGoal(Turtle $$0, double $$1) {
            this.turtle = $$0;
            this.speedModifier = $$1;
        }

        @Override
        public boolean canUse() {
            if (this.turtle.isBaby()) {
                return false;
            }
            if (this.turtle.hasEgg()) {
                return true;
            }
            if (this.turtle.getRandom().nextInt(TurtleGoHomeGoal.reducedTickDelay(700)) != 0) {
                return false;
            }
            return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 64.0);
        }

        @Override
        public void start() {
            this.turtle.setGoingHome(true);
            this.stuck = false;
            this.closeToHomeTryTicks = 0;
        }

        @Override
        public void stop() {
            this.turtle.setGoingHome(false);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 7.0) && !this.stuck && this.closeToHomeTryTicks <= this.adjustedTickDelay(600);
        }

        @Override
        public void tick() {
            BlockPos $$0 = this.turtle.getHomePos();
            boolean $$1 = $$0.closerToCenterThan(this.turtle.position(), 16.0);
            if ($$1) {
                ++this.closeToHomeTryTicks;
            }
            if (this.turtle.getNavigation().isDone()) {
                Vec3 $$2 = Vec3.atBottomCenterOf($$0);
                Vec3 $$3 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, $$2, 0.3141592741012573);
                if ($$3 == null) {
                    $$3 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, $$2, 1.5707963705062866);
                }
                if ($$3 != null && !$$1 && !this.turtle.level.getBlockState(new BlockPos($$3)).is(Blocks.WATER)) {
                    $$3 = DefaultRandomPos.getPosTowards(this.turtle, 16, 5, $$2, 1.5707963705062866);
                }
                if ($$3 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo($$3.x, $$3.y, $$3.z, this.speedModifier);
            }
        }
    }

    static class TurtleTravelGoal
    extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;

        TurtleTravelGoal(Turtle $$0, double $$1) {
            this.turtle = $$0;
            this.speedModifier = $$1;
        }

        @Override
        public boolean canUse() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        @Override
        public void start() {
            int $$0 = 512;
            int $$1 = 4;
            RandomSource $$2 = this.turtle.random;
            int $$3 = $$2.nextInt(1025) - 512;
            int $$4 = $$2.nextInt(9) - 4;
            int $$5 = $$2.nextInt(1025) - 512;
            if ((double)$$4 + this.turtle.getY() > (double)(this.turtle.level.getSeaLevel() - 1)) {
                $$4 = 0;
            }
            BlockPos $$6 = new BlockPos((double)$$3 + this.turtle.getX(), (double)$$4 + this.turtle.getY(), (double)$$5 + this.turtle.getZ());
            this.turtle.setTravelPos($$6);
            this.turtle.setTravelling(true);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigation().isDone()) {
                Vec3 $$0 = Vec3.atBottomCenterOf(this.turtle.getTravelPos());
                Vec3 $$1 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, $$0, 0.3141592741012573);
                if ($$1 == null) {
                    $$1 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, $$0, 1.5707963705062866);
                }
                if ($$1 != null) {
                    int $$2 = Mth.floor($$1.x);
                    int $$3 = Mth.floor($$1.z);
                    int $$4 = 34;
                    if (!this.turtle.level.hasChunksAt($$2 - 34, $$3 - 34, $$2 + 34, $$3 + 34)) {
                        $$1 = null;
                    }
                }
                if ($$1 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo($$1.x, $$1.y, $$1.z, this.speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void stop() {
            this.turtle.setTravelling(false);
            super.stop();
        }
    }

    static class TurtleRandomStrollGoal
    extends RandomStrollGoal {
        private final Turtle turtle;

        TurtleRandomStrollGoal(Turtle $$0, double $$1, int $$2) {
            super($$0, $$1, $$2);
            this.turtle = $$0;
        }

        @Override
        public boolean canUse() {
            if (!(this.mob.isInWater() || this.turtle.isGoingHome() || this.turtle.hasEgg())) {
                return super.canUse();
            }
            return false;
        }
    }

    static class TurtlePathNavigation
    extends AmphibiousPathNavigation {
        TurtlePathNavigation(Turtle $$0, Level $$1) {
            super($$0, $$1);
        }

        @Override
        public boolean isStableDestination(BlockPos $$0) {
            Turtle $$1;
            Mob mob = this.mob;
            if (mob instanceof Turtle && ($$1 = (Turtle)mob).isTravelling()) {
                return this.level.getBlockState($$0).is(Blocks.WATER);
            }
            return !this.level.getBlockState((BlockPos)$$0.below()).isAir();
        }
    }
}