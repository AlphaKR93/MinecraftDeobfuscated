/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class AmphibiousNodeEvaluator
extends WalkNodeEvaluator {
    private final boolean prefersShallowSwimming;
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    public AmphibiousNodeEvaluator(boolean $$0) {
        this.prefersShallowSwimming = $$0;
    }

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        $$1.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.oldWalkableCost = $$1.getPathfindingMalus(BlockPathTypes.WALKABLE);
        $$1.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0f);
        this.oldWaterBorderCost = $$1.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
        $$1.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0f);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public Node getStart() {
        if (!this.mob.isInWater()) {
            return super.getStart();
        }
        return this.getStartNode(new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ)));
    }

    @Override
    public Target getGoal(double $$0, double $$1, double $$2) {
        return this.getTargetFromNode(this.getNode(Mth.floor($$0), Mth.floor($$1 + 0.5), Mth.floor($$2)));
    }

    @Override
    public int getNeighbors(Node[] $$0, Node $$1) {
        int $$6;
        int $$2 = super.getNeighbors($$0, $$1);
        BlockPathTypes $$3 = this.getCachedBlockType(this.mob, $$1.x, $$1.y + 1, $$1.z);
        BlockPathTypes $$4 = this.getCachedBlockType(this.mob, $$1.x, $$1.y, $$1.z);
        if (this.mob.getPathfindingMalus($$3) >= 0.0f && $$4 != BlockPathTypes.STICKY_HONEY) {
            int $$5 = Mth.floor(Math.max((float)1.0f, (float)this.mob.maxUpStep));
        } else {
            $$6 = 0;
        }
        double $$7 = this.getFloorLevel(new BlockPos($$1.x, $$1.y, $$1.z));
        Node $$8 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z, Math.max((int)0, (int)($$6 - 1)), $$7, Direction.UP, $$4);
        Node $$9 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z, $$6, $$7, Direction.DOWN, $$4);
        if (this.isVerticalNeighborValid($$8, $$1)) {
            $$0[$$2++] = $$8;
        }
        if (this.isVerticalNeighborValid($$9, $$1) && $$4 != BlockPathTypes.TRAPDOOR) {
            $$0[$$2++] = $$9;
        }
        for (int $$10 = 0; $$10 < $$2; ++$$10) {
            Node $$11 = $$0[$$10];
            if ($$11.type != BlockPathTypes.WATER || !this.prefersShallowSwimming || $$11.y >= this.mob.level.getSeaLevel() - 10) continue;
            $$11.costMalus += 1.0f;
        }
        return $$2;
    }

    private boolean isVerticalNeighborValid(@Nullable Node $$0, Node $$1) {
        return this.isNeighborValid($$0, $$1) && $$0.type == BlockPathTypes.WATER;
    }

    @Override
    protected boolean isAmphibious() {
        return true;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        BlockPathTypes $$5 = AmphibiousNodeEvaluator.getBlockPathTypeRaw($$0, $$4.set($$1, $$2, $$3));
        if ($$5 == BlockPathTypes.WATER) {
            for (Direction $$6 : Direction.values()) {
                BlockPathTypes $$7 = AmphibiousNodeEvaluator.getBlockPathTypeRaw($$0, $$4.set($$1, $$2, $$3).move($$6));
                if ($$7 != BlockPathTypes.BLOCKED) continue;
                return BlockPathTypes.WATER_BORDER;
            }
            return BlockPathTypes.WATER;
        }
        return AmphibiousNodeEvaluator.getBlockPathTypeStatic($$0, $$4);
    }
}