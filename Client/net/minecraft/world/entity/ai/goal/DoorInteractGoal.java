/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class DoorInteractGoal
extends Goal {
    protected Mob mob;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public DoorInteractGoal(Mob $$0) {
        this.mob = $$0;
        if (!GoalUtils.hasGroundPathNavigation($$0)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        }
        BlockState $$0 = this.mob.level.getBlockState(this.doorPos);
        if (!($$0.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
        }
        return $$0.getValue(DoorBlock.OPEN);
    }

    protected void setOpen(boolean $$0) {
        BlockState $$1;
        if (this.hasDoor && ($$1 = this.mob.level.getBlockState(this.doorPos)).getBlock() instanceof DoorBlock) {
            ((DoorBlock)$$1.getBlock()).setOpen(this.mob, this.mob.level, $$1, this.doorPos, $$0);
        }
    }

    @Override
    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
            return false;
        }
        if (!this.mob.horizontalCollision) {
            return false;
        }
        GroundPathNavigation $$0 = (GroundPathNavigation)this.mob.getNavigation();
        Path $$1 = $$0.getPath();
        if ($$1 == null || $$1.isDone() || !$$0.canOpenDoors()) {
            return false;
        }
        for (int $$2 = 0; $$2 < Math.min((int)($$1.getNextNodeIndex() + 2), (int)$$1.getNodeCount()); ++$$2) {
            Node $$3 = $$1.getNode($$2);
            this.doorPos = new BlockPos($$3.x, $$3.y + 1, $$3.z);
            if (this.mob.distanceToSqr(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25) continue;
            this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level, this.doorPos);
            if (!this.hasDoor) continue;
            return true;
        }
        this.doorPos = this.mob.blockPosition().above();
        this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level, this.doorPos);
        return this.hasDoor;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        this.doorOpenDirZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        float $$1;
        float $$0 = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        float $$2 = this.doorOpenDirX * $$0 + this.doorOpenDirZ * ($$1 = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ()));
        if ($$2 < 0.0f) {
            this.passed = true;
        }
    }
}