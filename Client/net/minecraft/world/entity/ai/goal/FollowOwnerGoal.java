/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class FollowOwnerGoal
extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
    private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public FollowOwnerGoal(TamableAnimal $$0, double $$1, float $$2, float $$3, boolean $$4) {
        this.tamable = $$0;
        this.level = $$0.level;
        this.speedModifier = $$1;
        this.navigation = $$0.getNavigation();
        this.startDistance = $$2;
        this.stopDistance = $$3;
        this.canFly = $$4;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        if (!($$0.getNavigation() instanceof GroundPathNavigation) && !($$0.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity $$0 = this.tamable.getOwner();
        if ($$0 == null) {
            return false;
        }
        if ($$0.isSpectator()) {
            return false;
        }
        if (this.tamable.isOrderedToSit()) {
            return false;
        }
        if (this.tamable.distanceToSqr($$0) < (double)(this.startDistance * this.startDistance)) {
            return false;
        }
        this.owner = $$0;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        }
        if (this.tamable.isOrderedToSit()) {
            return false;
        }
        return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0f, this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = this.adjustedTickDelay(10);
        if (this.tamable.isLeashed() || this.tamable.isPassenger()) {
            return;
        }
        if (this.tamable.distanceToSqr(this.owner) >= 144.0) {
            this.teleportToOwner();
        } else {
            this.navigation.moveTo(this.owner, this.speedModifier);
        }
    }

    private void teleportToOwner() {
        BlockPos $$0 = this.owner.blockPosition();
        for (int $$1 = 0; $$1 < 10; ++$$1) {
            int $$2 = this.randomIntInclusive(-3, 3);
            int $$3 = this.randomIntInclusive(-1, 1);
            int $$4 = this.randomIntInclusive(-3, 3);
            boolean $$5 = this.maybeTeleportTo($$0.getX() + $$2, $$0.getY() + $$3, $$0.getZ() + $$4);
            if (!$$5) continue;
            return;
        }
    }

    private boolean maybeTeleportTo(int $$0, int $$1, int $$2) {
        if (Math.abs((double)((double)$$0 - this.owner.getX())) < 2.0 && Math.abs((double)((double)$$2 - this.owner.getZ())) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos($$0, $$1, $$2))) {
            return false;
        }
        this.tamable.moveTo((double)$$0 + 0.5, $$1, (double)$$2 + 0.5, this.tamable.getYRot(), this.tamable.getXRot());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos $$0) {
        BlockPathTypes $$1 = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, $$0.mutable());
        if ($$1 != BlockPathTypes.WALKABLE) {
            return false;
        }
        BlockState $$2 = this.level.getBlockState((BlockPos)$$0.below());
        if (!this.canFly && $$2.getBlock() instanceof LeavesBlock) {
            return false;
        }
        Vec3i $$3 = $$0.subtract(this.tamable.blockPosition());
        return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move((BlockPos)$$3));
    }

    private int randomIntInclusive(int $$0, int $$1) {
        return this.tamable.getRandom().nextInt($$1 - $$0 + 1) + $$0;
    }
}