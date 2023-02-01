/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Boolean
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.LinkedHashSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Strider
extends Animal
implements ItemSteerable,
Saddleable {
    private static final float SUFFOCATE_STEERING_MODIFIER = 0.23f;
    private static final float SUFFOCATE_SPEED_MODIFIER = 0.66f;
    private static final float STEERING_MODIFIER = 0.55f;
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
    private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
    private final ItemBasedSteering steering;
    @Nullable
    private TemptGoal temptGoal;
    @Nullable
    private PanicGoal panicGoal;

    public Strider(EntityType<? extends Strider> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
        this.blocksBuilding = true;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0f);
    }

    public static boolean checkStriderSpawnRules(EntityType<Strider> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        BlockPos.MutableBlockPos $$5 = $$3.mutable();
        do {
            $$5.move(Direction.UP);
        } while ($$1.getFluidState($$5).is(FluidTags.LAVA));
        return $$1.getBlockState($$5).isAir();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_BOOST_TIME.equals($$0) && this.level.isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BOOST_TIME, 0);
        this.entityData.define(DATA_SUFFOCATING, false);
        this.entityData.define(DATA_SADDLE_ID, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        this.steering.addAdditionalSaveData($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.steering.readAdditionalSaveData($$0);
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource $$0) {
        this.steering.setSaddle(true);
        if ($$0 != null) {
            this.level.playSound(null, this, SoundEvents.STRIDER_SADDLE, $$0, 0.5f, 1.0f);
        }
    }

    @Override
    protected void registerGoals() {
        this.panicGoal = new PanicGoal(this, 1.65);
        this.goalSelector.addGoal(1, this.panicGoal);
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.temptGoal = new TemptGoal(this, 1.4, TEMPT_ITEMS, false);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new StriderGoToLavaGoal(this, 1.5));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0f));
    }

    public void setSuffocating(boolean $$0) {
        this.entityData.set(DATA_SUFFOCATING, $$0);
    }

    public boolean isSuffocating() {
        if (this.getVehicle() instanceof Strider) {
            return ((Strider)this.getVehicle()).isSuffocating();
        }
        return this.entityData.get(DATA_SUFFOCATING);
    }

    @Override
    public boolean canStandOnFluid(FluidState $$0) {
        return $$0.is(FluidTags.LAVA);
    }

    @Override
    public double getPassengersRidingOffset() {
        float $$0 = Math.min((float)0.25f, (float)this.walkAnimation.speed());
        float $$1 = this.walkAnimation.position();
        return (double)this.getBbHeight() - 0.19 + (double)(0.12f * Mth.cos($$1 * 1.5f) * 2.0f * $$0);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        Entity $$0 = this.getFirstPassenger();
        return $$0 != null && this.canBeControlledBy($$0) ? $$0 : null;
    }

    private boolean canBeControlledBy(Entity $$0) {
        if ($$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            return $$1.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || $$1.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK);
        }
        return false;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity $$0) {
        Iterator $$1 = new Iterator[]{Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), $$0.getYRot()), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), $$0.getYRot() - 22.5f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), $$0.getYRot() + 22.5f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), $$0.getYRot() - 45.0f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), $$0.getYRot() + 45.0f)};
        LinkedHashSet $$2 = Sets.newLinkedHashSet();
        double $$3 = this.getBoundingBox().maxY;
        double $$4 = this.getBoundingBox().minY - 0.5;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (Vec3 $$6 : $$1) {
            $$5.set(this.getX() + $$6.x, $$3, this.getZ() + $$6.z);
            for (double $$7 = $$3; $$7 > $$4; $$7 -= 1.0) {
                $$2.add((Object)$$5.immutable());
                $$5.move(Direction.DOWN);
            }
        }
        for (BlockPos $$8 : $$2) {
            double $$9;
            if (this.level.getFluidState($$8).is(FluidTags.LAVA) || !DismountHelper.isBlockFloorValid($$9 = this.level.getBlockFloorHeight($$8))) continue;
            Vec3 $$10 = Vec3.upFromBottomCenterOf($$8, $$9);
            for (Pose $$11 : $$0.getDismountPoses()) {
                AABB $$12 = $$0.getLocalBoundsForPose($$11);
                if (!DismountHelper.canDismountTo(this.level, $$0, $$12.move($$10))) continue;
                $$0.setPose($$11);
                return $$10;
            }
        }
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public void travel(Vec3 $$0) {
        this.setSpeed(this.getMoveSpeed());
        this.travel(this, this.steering, $$0);
    }

    public float getMoveSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66f : 1.0f);
    }

    @Override
    public float getSteeringSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23f : 0.55f);
    }

    @Override
    public void travelWithInput(Vec3 $$0) {
        super.travel($$0);
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.6f;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0f, 1.0f);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
        this.checkInsideBlocks();
        if (this.isInLava()) {
            this.resetFallDistance();
            return;
        }
        super.checkFallDamage($$0, $$1, $$2, $$3);
    }

    @Override
    public void tick() {
        if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEvents.STRIDER_HAPPY, 1.0f, this.getVoicePitch());
        } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEvents.STRIDER_RETREAT, 1.0f, this.getVoicePitch());
        }
        if (!this.isNoAi()) {
            BlockState $$0 = this.level.getBlockState(this.blockPosition());
            BlockState $$1 = this.getBlockStateOnLegacy();
            boolean $$2 = $$0.is(BlockTags.STRIDER_WARM_BLOCKS) || $$1.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
            this.setSuffocating(!$$2);
        }
        super.tick();
        this.floatStrider();
        this.checkInsideBlocks();
    }

    private boolean isPanicking() {
        return this.panicGoal != null && this.panicGoal.isRunning();
    }

    private boolean isBeingTempted() {
        return this.temptGoal != null && this.temptGoal.isRunning();
    }

    @Override
    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    private void floatStrider() {
        if (this.isInLava()) {
            CollisionContext $$0 = CollisionContext.of(this);
            if (!$$0.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) || this.level.getFluidState((BlockPos)this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            } else {
                this.onGround = true;
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.175f).add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isPanicking() || this.isBeingTempted()) {
            return null;
        }
        return SoundEvents.STRIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.STRIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRIDER_DEATH;
    }

    @Override
    protected boolean canAddPassenger(Entity $$0) {
        return !this.isVehicle() && !this.isEyeInFluid(FluidTags.LAVA);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new StriderPathNavigation(this, $$0);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if ($$1.getBlockState($$0).getFluidState().is(FluidTags.LAVA)) {
            return 10.0f;
        }
        return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0f;
    }

    @Override
    @Nullable
    public Strider getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.STRIDER.create($$0);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return FOOD_ITEMS.test($$0);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        boolean $$2 = this.isFood($$0.getItemInHand($$1));
        if (!$$2 && this.isSaddled() && !this.isVehicle() && !$$0.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                $$0.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult $$3 = super.mobInteract($$0, $$1);
        if (!$$3.consumesAction()) {
            ItemStack $$4 = $$0.getItemInHand($$1);
            if ($$4.is(Items.SADDLE)) {
                return $$4.interactLivingEntity($$0, this, $$1);
            }
            return InteractionResult.PASS;
        }
        if ($$2 && !this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return $$3;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.6f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        if (this.isBaby()) {
            return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
        }
        RandomSource $$5 = $$0.getRandom();
        if ($$5.nextInt(30) == 0) {
            Mob $$6 = EntityType.ZOMBIFIED_PIGLIN.create($$0.getLevel());
            if ($$6 != null) {
                $$3 = this.spawnJockey($$0, $$1, $$6, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds($$5), false));
                $$6.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
                this.equipSaddle(null);
            }
        } else if ($$5.nextInt(10) == 0) {
            AgeableMob $$7 = EntityType.STRIDER.create($$0.getLevel());
            if ($$7 != null) {
                $$7.setAge(-24000);
                $$3 = this.spawnJockey($$0, $$1, $$7, null);
            }
        } else {
            $$3 = new AgeableMob.AgeableMobGroupData(0.5f);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    private SpawnGroupData spawnJockey(ServerLevelAccessor $$0, DifficultyInstance $$1, Mob $$2, @Nullable SpawnGroupData $$3) {
        $$2.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
        $$2.finalizeSpawn($$0, $$1, MobSpawnType.JOCKEY, $$3, null);
        $$2.startRiding(this, true);
        return new AgeableMob.AgeableMobGroupData(0.0f);
    }

    static class StriderGoToLavaGoal
    extends MoveToBlockGoal {
        private final Strider strider;

        StriderGoToLavaGoal(Strider $$0, double $$1) {
            super($$0, $$1, 8, 2);
            this.strider = $$0;
        }

        @Override
        public BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            return !this.strider.isInLava() && super.canUse();
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        @Override
        protected boolean isValidTarget(LevelReader $$0, BlockPos $$1) {
            return $$0.getBlockState($$1).is(Blocks.LAVA) && $$0.getBlockState((BlockPos)$$1.above()).isPathfindable($$0, $$1, PathComputationType.LAND);
        }
    }

    static class StriderPathNavigation
    extends GroundPathNavigation {
        StriderPathNavigation(Strider $$0, Level $$1) {
            super($$0, $$1);
        }

        @Override
        protected PathFinder createPathFinder(int $$0) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, $$0);
        }

        @Override
        protected boolean hasValidPathType(BlockPathTypes $$0) {
            if ($$0 == BlockPathTypes.LAVA || $$0 == BlockPathTypes.DAMAGE_FIRE || $$0 == BlockPathTypes.DANGER_FIRE) {
                return true;
            }
            return super.hasValidPathType($$0);
        }

        @Override
        public boolean isStableDestination(BlockPos $$0) {
            return this.level.getBlockState($$0).is(Blocks.LAVA) || super.isStableDestination($$0);
        }
    }
}