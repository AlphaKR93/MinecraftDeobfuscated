/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Drowned
extends Zombie
implements RangedAttackMob {
    public static final float NAUTILUS_SHELL_CHANCE = 0.03f;
    boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public Drowned(EntityType<? extends Drowned> $$0, Level $$1) {
        super((EntityType<? extends Zombie>)$$0, $$1);
        this.maxUpStep = 1.0f;
        this.moveControl = new DrownedMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.waterNavigation = new WaterBoundPathNavigation(this, $$1);
        this.groundNavigation = new GroundPathNavigation(this, $$1);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new DrownedGoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(2, new DrownedTridentAttackGoal(this, 1.0, 40, 10.0f));
        this.goalSelector.addGoal(2, new DrownedAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new DrownedGoToBeachGoal(this, 1.0));
        this.goalSelector.addGoal(6, new DrownedSwimUpGoal(this, 1.0, this.level.getSeaLevel()));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Drowned.class).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)this::okTarget)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>((Mob)this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>((Mob)this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Axolotl>((Mob)this, Axolotl.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        $$3 = super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && $$0.getRandom().nextFloat() < 0.03f) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        }
        return $$3;
    }

    public static boolean checkDrownedSpawnRules(EntityType<Drowned> $$0, ServerLevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        boolean $$6;
        if (!$$1.getFluidState((BlockPos)$$3.below()).is(FluidTags.WATER)) {
            return false;
        }
        Holder $$5 = $$1.getBiome($$3);
        boolean bl = $$6 = $$1.getDifficulty() != Difficulty.PEACEFUL && Drowned.isDarkEnoughToSpawn($$1, $$3, $$4) && ($$2 == MobSpawnType.SPAWNER || $$1.getFluidState($$3).is(FluidTags.WATER));
        if ($$5.is(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
            return $$4.nextInt(15) == 0 && $$6;
        }
        return $$4.nextInt(40) == 0 && Drowned.isDeepEnoughToSpawn($$1, $$3) && $$6;
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor $$0, BlockPos $$1) {
        return $$1.getY() < $$0.getSeaLevel() - 5;
    }

    @Override
    protected boolean supportsBreakDoorGoal() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_AMBIENT_WATER;
        }
        return SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_HURT_WATER;
        }
        return SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_DEATH_WATER;
        }
        return SoundEvents.DROWNED_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
        if ((double)$$0.nextFloat() > 0.9) {
            int $$2 = $$0.nextInt(16);
            if ($$2 < 10) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack $$0, ItemStack $$1) {
        if ($$1.is(Items.NAUTILUS_SHELL)) {
            return false;
        }
        if ($$1.is(Items.TRIDENT)) {
            if ($$0.is(Items.TRIDENT)) {
                return $$0.getDamageValue() < $$1.getDamageValue();
            }
            return false;
        }
        if ($$0.is(Items.TRIDENT)) {
            return true;
        }
        return super.canReplaceCurrentItem($$0, $$1);
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }

    public boolean okTarget(@Nullable LivingEntity $$0) {
        if ($$0 != null) {
            return !this.level.isDay() || $$0.isInWater();
        }
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        }
        LivingEntity $$0 = this.getTarget();
        return $$0 != null && $$0.isInWater();
    }

    @Override
    public void travel(Vec3 $$0) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01f, $$0);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel($$0);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    protected boolean closeToNextPos() {
        double $$2;
        BlockPos $$1;
        Path $$0 = this.getNavigation().getPath();
        return $$0 != null && ($$1 = $$0.getTarget()) != null && ($$2 = this.distanceToSqr($$1.getX(), $$1.getY(), $$1.getZ())) < 4.0;
    }

    @Override
    public void performRangedAttack(LivingEntity $$0, float $$1) {
        ThrownTrident $$2 = new ThrownTrident(this.level, (LivingEntity)this, new ItemStack(Items.TRIDENT));
        double $$3 = $$0.getX() - this.getX();
        double $$4 = $$0.getY(0.3333333333333333) - $$2.getY();
        double $$5 = $$0.getZ() - this.getZ();
        double $$6 = Math.sqrt((double)($$3 * $$3 + $$5 * $$5));
        $$2.shoot($$3, $$4 + $$6 * (double)0.2f, $$5, 1.6f, 14 - this.level.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity($$2);
    }

    public void setSearchingForLand(boolean $$0) {
        this.searchingForLand = $$0;
    }

    static class DrownedMoveControl
    extends MoveControl {
        private final Drowned drowned;

        public DrownedMoveControl(Drowned $$0) {
            super($$0);
            this.drowned = $$0;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = this.drowned.getTarget();
            if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
                if ($$0 != null && $$0.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0, 0.002, 0.0));
                }
                if (this.operation != MoveControl.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
                    this.drowned.setSpeed(0.0f);
                    return;
                }
                double $$1 = this.wantedX - this.drowned.getX();
                double $$2 = this.wantedY - this.drowned.getY();
                double $$3 = this.wantedZ - this.drowned.getZ();
                double $$4 = Math.sqrt((double)($$1 * $$1 + $$2 * $$2 + $$3 * $$3));
                $$2 /= $$4;
                float $$5 = (float)(Mth.atan2($$3, $$1) * 57.2957763671875) - 90.0f;
                this.drowned.setYRot(this.rotlerp(this.drowned.getYRot(), $$5, 90.0f));
                this.drowned.yBodyRot = this.drowned.getYRot();
                float $$6 = (float)(this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float $$7 = Mth.lerp(0.125f, this.drowned.getSpeed(), $$6);
                this.drowned.setSpeed($$7);
                this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double)$$7 * $$1 * 0.005, (double)$$7 * $$2 * 0.1, (double)$$7 * $$3 * 0.005));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0, -0.008, 0.0));
                }
                super.tick();
            }
        }
    }

    static class DrownedGoToWaterGoal
    extends Goal {
        private final PathfinderMob mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public DrownedGoToWaterGoal(PathfinderMob $$0, double $$1) {
            this.mob = $$0;
            this.speedModifier = $$1;
            this.level = $$0.level;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.level.isDay()) {
                return false;
            }
            if (this.mob.isInWater()) {
                return false;
            }
            Vec3 $$0 = this.getWaterPos();
            if ($$0 == null) {
                return false;
            }
            this.wantedX = $$0.x;
            this.wantedY = $$0.y;
            this.wantedZ = $$0.z;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            RandomSource $$0 = this.mob.getRandom();
            BlockPos $$1 = this.mob.blockPosition();
            for (int $$2 = 0; $$2 < 10; ++$$2) {
                BlockPos $$3 = $$1.offset($$0.nextInt(20) - 10, 2 - $$0.nextInt(8), $$0.nextInt(20) - 10);
                if (!this.level.getBlockState($$3).is(Blocks.WATER)) continue;
                return Vec3.atBottomCenterOf($$3);
            }
            return null;
        }
    }

    static class DrownedTridentAttackGoal
    extends RangedAttackGoal {
        private final Drowned drowned;

        public DrownedTridentAttackGoal(RangedAttackMob $$0, double $$1, int $$2, float $$3) {
            super($$0, $$1, $$2, $$3);
            this.drowned = (Drowned)$$0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.getMainHandItem().is(Items.TRIDENT);
        }

        @Override
        public void start() {
            super.start();
            this.drowned.setAggressive(true);
            this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.drowned.stopUsingItem();
            this.drowned.setAggressive(false);
        }
    }

    static class DrownedAttackGoal
    extends ZombieAttackGoal {
        private final Drowned drowned;

        public DrownedAttackGoal(Drowned $$0, double $$1, boolean $$2) {
            super($$0, $$1, $$2);
            this.drowned = $$0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
        }
    }

    static class DrownedGoToBeachGoal
    extends MoveToBlockGoal {
        private final Drowned drowned;

        public DrownedGoToBeachGoal(Drowned $$0, double $$1) {
            super($$0, $$1, 8, 2);
            this.drowned = $$0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level.getSeaLevel() - 3);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader $$0, BlockPos $$1) {
            Vec3i $$2 = $$1.above();
            if (!$$0.isEmptyBlock((BlockPos)$$2) || !$$0.isEmptyBlock((BlockPos)((BlockPos)$$2).above())) {
                return false;
            }
            return $$0.getBlockState($$1).entityCanStandOn($$0, $$1, this.drowned);
        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    static class DrownedSwimUpGoal
    extends Goal {
        private final Drowned drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public DrownedSwimUpGoal(Drowned $$0, double $$1, int $$2) {
            this.drowned = $$0;
            this.speedModifier = $$1;
            this.seaLevel = $$2;
        }

        @Override
        public boolean canUse() {
            return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        @Override
        public void tick() {
            if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
                Vec3 $$0 = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), this.seaLevel - 1, this.drowned.getZ()), 1.5707963705062866);
                if ($$0 == null) {
                    this.stuck = true;
                    return;
                }
                this.drowned.getNavigation().moveTo($$0.x, $$0.y, $$0.z, this.speedModifier);
            }
        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(true);
            this.stuck = false;
        }

        @Override
        public void stop() {
            this.drowned.setSearchingForLand(false);
        }
    }
}