/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation
extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected PathFinder createPathFinder(int $$0) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, $$0);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public Path createPath(BlockPos $$0, int $$1) {
        if (this.level.getBlockState((BlockPos)$$0).isAir()) {
            Vec3i $$2 = $$0.below();
            while ($$2.getY() > this.level.getMinBuildHeight() && this.level.getBlockState((BlockPos)$$2).isAir()) {
                $$2 = ((BlockPos)$$2).below();
            }
            if ($$2.getY() > this.level.getMinBuildHeight()) {
                return super.createPath((BlockPos)((BlockPos)$$2).above(), $$1);
            }
            while ($$2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState((BlockPos)$$2).isAir()) {
                $$2 = ((BlockPos)$$2).above();
            }
            $$0 = $$2;
        }
        if (this.level.getBlockState((BlockPos)$$0).getMaterial().isSolid()) {
            Vec3i $$3 = $$0.above();
            while ($$3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState((BlockPos)$$3).getMaterial().isSolid()) {
                $$3 = ((BlockPos)$$3).above();
            }
            return super.createPath((BlockPos)$$3, $$1);
        }
        return super.createPath((BlockPos)$$0, $$1);
    }

    @Override
    public Path createPath(Entity $$0, int $$1) {
        return this.createPath($$0.blockPosition(), $$1);
    }

    private int getSurfaceY() {
        if (!this.mob.isInWater() || !this.canFloat()) {
            return Mth.floor(this.mob.getY() + 0.5);
        }
        int $$0 = this.mob.getBlockY();
        BlockState $$1 = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)$$0, this.mob.getZ()));
        int $$2 = 0;
        while ($$1.is(Blocks.WATER)) {
            $$1 = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)(++$$0), this.mob.getZ()));
            if (++$$2 <= 16) continue;
            return this.mob.getBlockY();
        }
        return $$0;
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }
            for (int $$0 = 0; $$0 < this.path.getNodeCount(); ++$$0) {
                Node $$1 = this.path.getNode($$0);
                if (!this.level.canSeeSky(new BlockPos($$1.x, $$1.y, $$1.z))) continue;
                this.path.truncateNodes($$0);
                return;
            }
        }
    }

    protected boolean hasValidPathType(BlockPathTypes $$0) {
        if ($$0 == BlockPathTypes.WATER) {
            return false;
        }
        if ($$0 == BlockPathTypes.LAVA) {
            return false;
        }
        return $$0 != BlockPathTypes.OPEN;
    }

    public void setCanOpenDoors(boolean $$0) {
        this.nodeEvaluator.setCanOpenDoors($$0);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean $$0) {
        this.nodeEvaluator.setCanPassDoors($$0);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean $$0) {
        this.avoidSun = $$0;
    }

    public void setCanWalkOverFences(boolean $$0) {
        this.nodeEvaluator.setCanWalkOverFences($$0);
    }
}