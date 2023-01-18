/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;

public class SwimNodeEvaluator
extends NodeEvaluator {
    private final boolean allowBreaching;
    private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();

    public SwimNodeEvaluator(boolean $$0) {
        this.allowBreaching = $$0;
    }

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        this.pathTypesByPosCache.clear();
    }

    @Override
    public void done() {
        super.done();
        this.pathTypesByPosCache.clear();
    }

    @Override
    public Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public Target getGoal(double $$0, double $$1, double $$2) {
        return this.getTargetFromNode(this.getNode(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2)));
    }

    @Override
    public int getNeighbors(Node[] $$0, Node $$1) {
        int $$2 = 0;
        EnumMap $$3 = Maps.newEnumMap(Direction.class);
        for (Direction $$4 : Direction.values()) {
            Node $$5 = this.findAcceptedNode($$1.x + $$4.getStepX(), $$1.y + $$4.getStepY(), $$1.z + $$4.getStepZ());
            $$3.put((Object)$$4, (Object)$$5);
            if (!this.isNodeValid($$5)) continue;
            $$0[$$2++] = $$5;
        }
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            Direction $$7 = $$6.getClockWise();
            Node $$8 = this.findAcceptedNode($$1.x + $$6.getStepX() + $$7.getStepX(), $$1.y, $$1.z + $$6.getStepZ() + $$7.getStepZ());
            if (!this.isDiagonalNodeValid($$8, (Node)$$3.get((Object)$$6), (Node)$$3.get((Object)$$7))) continue;
            $$0[$$2++] = $$8;
        }
        return $$2;
    }

    protected boolean isNodeValid(@Nullable Node $$0) {
        return $$0 != null && !$$0.closed;
    }

    protected boolean isDiagonalNodeValid(@Nullable Node $$0, @Nullable Node $$1, @Nullable Node $$2) {
        return this.isNodeValid($$0) && $$1 != null && $$1.costMalus >= 0.0f && $$2 != null && $$2.costMalus >= 0.0f;
    }

    @Nullable
    protected Node findAcceptedNode(int $$0, int $$1, int $$2) {
        float $$5;
        Node $$3 = null;
        BlockPathTypes $$4 = this.getCachedBlockType($$0, $$1, $$2);
        if ((this.allowBreaching && $$4 == BlockPathTypes.BREACH || $$4 == BlockPathTypes.WATER) && ($$5 = this.mob.getPathfindingMalus($$4)) >= 0.0f) {
            $$3 = this.getNode($$0, $$1, $$2);
            $$3.type = $$4;
            $$3.costMalus = Math.max((float)$$3.costMalus, (float)$$5);
            if (this.level.getFluidState(new BlockPos($$0, $$1, $$2)).isEmpty()) {
                $$3.costMalus += 8.0f;
            }
        }
        return $$3;
    }

    protected BlockPathTypes getCachedBlockType(int $$0, int $$1, int $$2) {
        return (BlockPathTypes)((Object)this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong($$0, $$1, $$2), $$3 -> this.getBlockPathType(this.level, $$0, $$1, $$2)));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3) {
        return this.getBlockPathType($$0, $$1, $$2, $$3, this.mob);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3, Mob $$4) {
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (int $$6 = $$1; $$6 < $$1 + this.entityWidth; ++$$6) {
            for (int $$7 = $$2; $$7 < $$2 + this.entityHeight; ++$$7) {
                for (int $$8 = $$3; $$8 < $$3 + this.entityDepth; ++$$8) {
                    FluidState $$9 = $$0.getFluidState($$5.set($$6, $$7, $$8));
                    BlockState $$10 = $$0.getBlockState($$5.set($$6, $$7, $$8));
                    if ($$9.isEmpty() && $$10.isPathfindable($$0, (BlockPos)$$5.below(), PathComputationType.WATER) && $$10.isAir()) {
                        return BlockPathTypes.BREACH;
                    }
                    if ($$9.is(FluidTags.WATER)) continue;
                    return BlockPathTypes.BLOCKED;
                }
            }
        }
        BlockState $$11 = $$0.getBlockState($$5);
        if ($$11.isPathfindable($$0, $$5, PathComputationType.WATER)) {
            return BlockPathTypes.WATER;
        }
        return BlockPathTypes.BLOCKED;
    }
}