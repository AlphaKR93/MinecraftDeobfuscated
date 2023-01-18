/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator
extends WalkNodeEvaluator {
    private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap();
    private static final float SMALL_MOB_INFLATED_START_NODE_BOUNDING_BOX = 1.5f;
    private static final int MAX_START_NODE_CANDIDATES = 10;

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        this.pathTypeByPosCache.clear();
        this.oldWaterCost = $$1.getPathfindingMalus(BlockPathTypes.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        this.pathTypeByPosCache.clear();
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos $$4;
        int $$3;
        if (this.canFloat() && this.mob.isInWater()) {
            int $$0 = this.mob.getBlockY();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)$$0, this.mob.getZ());
            BlockState $$2 = this.level.getBlockState($$1);
            while ($$2.is(Blocks.WATER)) {
                $$1.set(this.mob.getX(), (double)(++$$0), this.mob.getZ());
                $$2 = this.level.getBlockState($$1);
            }
        } else {
            $$3 = Mth.floor(this.mob.getY() + 0.5);
        }
        if (!this.canStartAt($$4 = new BlockPos(this.mob.getX(), (double)$$3, this.mob.getZ()))) {
            for (BlockPos $$5 : this.iteratePathfindingStartNodeCandidatePositions(this.mob)) {
                if (!this.canStartAt($$5)) continue;
                return super.getStartNode($$5);
            }
        }
        return super.getStartNode($$4);
    }

    @Override
    protected boolean canStartAt(BlockPos $$0) {
        BlockPathTypes $$1 = this.getBlockPathType(this.mob, $$0);
        return this.mob.getPathfindingMalus($$1) >= 0.0f;
    }

    @Override
    public Target getGoal(double $$0, double $$1, double $$2) {
        return this.getTargetFromNode(this.getNode(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2)));
    }

    @Override
    public int getNeighbors(Node[] $$0, Node $$1) {
        Node $$28;
        Node $$27;
        Node $$26;
        Node $$25;
        Node $$24;
        Node $$23;
        Node $$22;
        Node $$21;
        Node $$20;
        Node $$19;
        Node $$18;
        Node $$17;
        Node $$16;
        Node $$15;
        Node $$14;
        Node $$13;
        Node $$12;
        Node $$11;
        Node $$10;
        Node $$9;
        Node $$8;
        Node $$7;
        Node $$6;
        Node $$5;
        Node $$4;
        int $$2 = 0;
        Node $$3 = this.findAcceptedNode($$1.x, $$1.y, $$1.z + 1);
        if (this.isOpen($$3)) {
            $$0[$$2++] = $$3;
        }
        if (this.isOpen($$4 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z))) {
            $$0[$$2++] = $$4;
        }
        if (this.isOpen($$5 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z))) {
            $$0[$$2++] = $$5;
        }
        if (this.isOpen($$6 = this.findAcceptedNode($$1.x, $$1.y, $$1.z - 1))) {
            $$0[$$2++] = $$6;
        }
        if (this.isOpen($$7 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z))) {
            $$0[$$2++] = $$7;
        }
        if (this.isOpen($$8 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z))) {
            $$0[$$2++] = $$8;
        }
        if (this.isOpen($$9 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$7)) {
            $$0[$$2++] = $$9;
        }
        if (this.isOpen($$10 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z)) && this.hasMalus($$4) && this.hasMalus($$7)) {
            $$0[$$2++] = $$10;
        }
        if (this.isOpen($$11 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z)) && this.hasMalus($$5) && this.hasMalus($$7)) {
            $$0[$$2++] = $$11;
        }
        if (this.isOpen($$12 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$7)) {
            $$0[$$2++] = $$12;
        }
        if (this.isOpen($$13 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$8)) {
            $$0[$$2++] = $$13;
        }
        if (this.isOpen($$14 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z)) && this.hasMalus($$4) && this.hasMalus($$8)) {
            $$0[$$2++] = $$14;
        }
        if (this.isOpen($$15 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z)) && this.hasMalus($$5) && this.hasMalus($$8)) {
            $$0[$$2++] = $$15;
        }
        if (this.isOpen($$16 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$8)) {
            $$0[$$2++] = $$16;
        }
        if (this.isOpen($$17 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$5)) {
            $$0[$$2++] = $$17;
        }
        if (this.isOpen($$18 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$5)) {
            $$0[$$2++] = $$18;
        }
        if (this.isOpen($$19 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$4)) {
            $$0[$$2++] = $$19;
        }
        if (this.isOpen($$20 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$4)) {
            $$0[$$2++] = $$20;
        }
        if (this.isOpen($$21 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$11)) {
            $$0[$$2++] = $$21;
        }
        if (this.isOpen($$22 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$11)) {
            $$0[$$2++] = $$22;
        }
        if (this.isOpen($$23 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$10)) {
            $$0[$$2++] = $$23;
        }
        if (this.isOpen($$24 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$10)) {
            $$0[$$2++] = $$24;
        }
        if (this.isOpen($$25 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$15)) {
            $$0[$$2++] = $$25;
        }
        if (this.isOpen($$26 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$15)) {
            $$0[$$2++] = $$26;
        }
        if (this.isOpen($$27 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$14)) {
            $$0[$$2++] = $$27;
        }
        if (this.isOpen($$28 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$14)) {
            $$0[$$2++] = $$28;
        }
        return $$2;
    }

    private boolean hasMalus(@Nullable Node $$0) {
        return $$0 != null && $$0.costMalus >= 0.0f;
    }

    private boolean isOpen(@Nullable Node $$0) {
        return $$0 != null && !$$0.closed;
    }

    @Nullable
    protected Node findAcceptedNode(int $$0, int $$1, int $$2) {
        Node $$3 = null;
        BlockPathTypes $$4 = this.getCachedBlockPathType($$0, $$1, $$2);
        float $$5 = this.mob.getPathfindingMalus($$4);
        if ($$5 >= 0.0f) {
            $$3 = this.getNode($$0, $$1, $$2);
            $$3.type = $$4;
            $$3.costMalus = Math.max((float)$$3.costMalus, (float)$$5);
            if ($$4 == BlockPathTypes.WALKABLE) {
                $$3.costMalus += 1.0f;
            }
        }
        return $$3;
    }

    private BlockPathTypes getCachedBlockPathType(int $$0, int $$1, int $$2) {
        return (BlockPathTypes)((Object)this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong($$0, $$1, $$2), $$3 -> this.getBlockPathType(this.level, $$0, $$1, $$2, this.mob)));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3, Mob $$4) {
        EnumSet $$5 = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes $$6 = BlockPathTypes.BLOCKED;
        BlockPos $$7 = $$4.blockPosition();
        $$6 = super.getBlockPathTypes($$0, $$1, $$2, $$3, (EnumSet<BlockPathTypes>)$$5, $$6, $$7);
        if ($$5.contains((Object)BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        }
        BlockPathTypes $$8 = BlockPathTypes.BLOCKED;
        for (BlockPathTypes $$9 : $$5) {
            if ($$4.getPathfindingMalus($$9) < 0.0f) {
                return $$9;
            }
            if (!($$4.getPathfindingMalus($$9) >= $$4.getPathfindingMalus($$8))) continue;
            $$8 = $$9;
        }
        if ($$6 == BlockPathTypes.OPEN && $$4.getPathfindingMalus($$8) == 0.0f) {
            return BlockPathTypes.OPEN;
        }
        return $$8;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        BlockPathTypes $$5 = FlyNodeEvaluator.getBlockPathTypeRaw($$0, $$4.set($$1, $$2, $$3));
        if ($$5 == BlockPathTypes.OPEN && $$2 >= $$0.getMinBuildHeight() + 1) {
            BlockPathTypes $$6 = FlyNodeEvaluator.getBlockPathTypeRaw($$0, $$4.set($$1, $$2 - 1, $$3));
            if ($$6 == BlockPathTypes.DAMAGE_FIRE || $$6 == BlockPathTypes.LAVA) {
                $$5 = BlockPathTypes.DAMAGE_FIRE;
            } else if ($$6 == BlockPathTypes.DAMAGE_OTHER) {
                $$5 = BlockPathTypes.DAMAGE_OTHER;
            } else if ($$6 == BlockPathTypes.COCOA) {
                $$5 = BlockPathTypes.COCOA;
            } else if ($$6 == BlockPathTypes.FENCE) {
                if (!$$4.equals(this.mob.blockPosition())) {
                    $$5 = BlockPathTypes.FENCE;
                }
            } else {
                BlockPathTypes blockPathTypes = $$5 = $$6 == BlockPathTypes.WALKABLE || $$6 == BlockPathTypes.OPEN || $$6 == BlockPathTypes.WATER ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE;
            }
        }
        if ($$5 == BlockPathTypes.WALKABLE || $$5 == BlockPathTypes.OPEN) {
            $$5 = FlyNodeEvaluator.checkNeighbourBlocks($$0, $$4.set($$1, $$2, $$3), $$5);
        }
        return $$5;
    }

    private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob $$0) {
        boolean $$3;
        float $$1 = 1.0f;
        AABB $$2 = $$0.getBoundingBox();
        boolean bl = $$3 = $$2.getSize() < 1.0;
        if (!$$3) {
            return List.of((Object)new BlockPos($$2.minX, (double)$$0.getBlockY(), $$2.minZ), (Object)new BlockPos($$2.minX, (double)$$0.getBlockY(), $$2.maxZ), (Object)new BlockPos($$2.maxX, (double)$$0.getBlockY(), $$2.minZ), (Object)new BlockPos($$2.maxX, (double)$$0.getBlockY(), $$2.maxZ));
        }
        double $$4 = Math.max((double)0.0, (double)((1.5 - $$2.getZsize()) / 2.0));
        double $$5 = Math.max((double)0.0, (double)((1.5 - $$2.getXsize()) / 2.0));
        double $$6 = Math.max((double)0.0, (double)((1.5 - $$2.getYsize()) / 2.0));
        AABB $$7 = $$2.inflate($$5, $$6, $$4);
        return BlockPos.randomBetweenClosed($$0.getRandom(), 10, Mth.floor($$7.minX), Mth.floor($$7.minY), Mth.floor($$7.minZ), Mth.floor($$7.maxX), Mth.floor($$7.maxY), Mth.floor($$7.maxZ));
    }
}