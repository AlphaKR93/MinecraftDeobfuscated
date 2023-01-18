/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator
extends NodeEvaluator {
    public static final double SPACE_BETWEEN_WALL_POSTS = 0.5;
    private static final double DEFAULT_MOB_JUMP_HEIGHT = 1.125;
    protected float oldWaterCost;
    private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();
    private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap();

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        this.oldWaterCost = $$1.getPathfindingMalus(BlockPathTypes.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        this.pathTypesByPosCache.clear();
        this.collisionCache.clear();
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos.MutableBlockPos $$0 = new BlockPos.MutableBlockPos();
        int $$1 = this.mob.getBlockY();
        BlockState $$2 = this.level.getBlockState($$0.set(this.mob.getX(), (double)$$1, this.mob.getZ()));
        if (this.mob.canStandOnFluid($$2.getFluidState())) {
            while (this.mob.canStandOnFluid($$2.getFluidState())) {
                $$2 = this.level.getBlockState($$0.set(this.mob.getX(), (double)(++$$1), this.mob.getZ()));
            }
            --$$1;
        } else if (this.canFloat() && this.mob.isInWater()) {
            while ($$2.is(Blocks.WATER) || $$2.getFluidState() == Fluids.WATER.getSource(false)) {
                $$2 = this.level.getBlockState($$0.set(this.mob.getX(), (double)(++$$1), this.mob.getZ()));
            }
            --$$1;
        } else if (this.mob.isOnGround()) {
            $$1 = Mth.floor(this.mob.getY() + 0.5);
        } else {
            Vec3i $$3 = this.mob.blockPosition();
            while ((this.level.getBlockState((BlockPos)$$3).isAir() || this.level.getBlockState((BlockPos)$$3).isPathfindable(this.level, (BlockPos)$$3, PathComputationType.LAND)) && $$3.getY() > this.mob.level.getMinBuildHeight()) {
                $$3 = $$3.below();
            }
            $$1 = $$3.above().getY();
        }
        BlockPos $$4 = this.mob.blockPosition();
        if (!this.canStartAt($$0.set($$4.getX(), $$1, $$4.getZ()))) {
            AABB $$5 = this.mob.getBoundingBox();
            if (this.canStartAt($$0.set($$5.minX, (double)$$1, $$5.minZ)) || this.canStartAt($$0.set($$5.minX, (double)$$1, $$5.maxZ)) || this.canStartAt($$0.set($$5.maxX, (double)$$1, $$5.minZ)) || this.canStartAt($$0.set($$5.maxX, (double)$$1, $$5.maxZ))) {
                return this.getStartNode($$0);
            }
        }
        return this.getStartNode(new BlockPos($$4.getX(), $$1, $$4.getZ()));
    }

    protected Node getStartNode(BlockPos $$0) {
        Node $$1 = this.getNode($$0);
        $$1.type = this.getBlockPathType(this.mob, $$1.asBlockPos());
        $$1.costMalus = this.mob.getPathfindingMalus($$1.type);
        return $$1;
    }

    protected boolean canStartAt(BlockPos $$0) {
        BlockPathTypes $$1 = this.getBlockPathType(this.mob, $$0);
        return $$1 != BlockPathTypes.OPEN && this.mob.getPathfindingMalus($$1) >= 0.0f;
    }

    @Override
    public Target getGoal(double $$0, double $$1, double $$2) {
        return this.getTargetFromNode(this.getNode(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2)));
    }

    @Override
    public int getNeighbors(Node[] $$0, Node $$1) {
        Node $$14;
        Node $$13;
        Node $$12;
        Node $$11;
        Node $$10;
        Node $$9;
        Node $$8;
        double $$6;
        Node $$7;
        int $$2 = 0;
        int $$3 = 0;
        BlockPathTypes $$4 = this.getCachedBlockType(this.mob, $$1.x, $$1.y + 1, $$1.z);
        BlockPathTypes $$5 = this.getCachedBlockType(this.mob, $$1.x, $$1.y, $$1.z);
        if (this.mob.getPathfindingMalus($$4) >= 0.0f && $$5 != BlockPathTypes.STICKY_HONEY) {
            $$3 = Mth.floor(Math.max((float)1.0f, (float)this.mob.maxUpStep));
        }
        if (this.isNeighborValid($$7 = this.findAcceptedNode($$1.x, $$1.y, $$1.z + 1, $$3, $$6 = this.getFloorLevel(new BlockPos($$1.x, $$1.y, $$1.z)), Direction.SOUTH, $$5), $$1)) {
            $$0[$$2++] = $$7;
        }
        if (this.isNeighborValid($$8 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z, $$3, $$6, Direction.WEST, $$5), $$1)) {
            $$0[$$2++] = $$8;
        }
        if (this.isNeighborValid($$9 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z, $$3, $$6, Direction.EAST, $$5), $$1)) {
            $$0[$$2++] = $$9;
        }
        if (this.isNeighborValid($$10 = this.findAcceptedNode($$1.x, $$1.y, $$1.z - 1, $$3, $$6, Direction.NORTH, $$5), $$1)) {
            $$0[$$2++] = $$10;
        }
        if (this.isDiagonalValid($$1, $$8, $$10, $$11 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z - 1, $$3, $$6, Direction.NORTH, $$5))) {
            $$0[$$2++] = $$11;
        }
        if (this.isDiagonalValid($$1, $$9, $$10, $$12 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z - 1, $$3, $$6, Direction.NORTH, $$5))) {
            $$0[$$2++] = $$12;
        }
        if (this.isDiagonalValid($$1, $$8, $$7, $$13 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z + 1, $$3, $$6, Direction.SOUTH, $$5))) {
            $$0[$$2++] = $$13;
        }
        if (this.isDiagonalValid($$1, $$9, $$7, $$14 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z + 1, $$3, $$6, Direction.SOUTH, $$5))) {
            $$0[$$2++] = $$14;
        }
        return $$2;
    }

    protected boolean isNeighborValid(@Nullable Node $$0, Node $$1) {
        return $$0 != null && !$$0.closed && ($$0.costMalus >= 0.0f || $$1.costMalus < 0.0f);
    }

    protected boolean isDiagonalValid(Node $$0, @Nullable Node $$1, @Nullable Node $$2, @Nullable Node $$3) {
        if ($$3 == null || $$2 == null || $$1 == null) {
            return false;
        }
        if ($$3.closed) {
            return false;
        }
        if ($$2.y > $$0.y || $$1.y > $$0.y) {
            return false;
        }
        if ($$1.type == BlockPathTypes.WALKABLE_DOOR || $$2.type == BlockPathTypes.WALKABLE_DOOR || $$3.type == BlockPathTypes.WALKABLE_DOOR) {
            return false;
        }
        boolean $$4 = $$2.type == BlockPathTypes.FENCE && $$1.type == BlockPathTypes.FENCE && (double)this.mob.getBbWidth() < 0.5;
        return $$3.costMalus >= 0.0f && ($$2.y < $$0.y || $$2.costMalus >= 0.0f || $$4) && ($$1.y < $$0.y || $$1.costMalus >= 0.0f || $$4);
    }

    private static boolean doesBlockHavePartialCollision(BlockPathTypes $$0) {
        return $$0 == BlockPathTypes.FENCE || $$0 == BlockPathTypes.DOOR_WOOD_CLOSED || $$0 == BlockPathTypes.DOOR_IRON_CLOSED;
    }

    private boolean canReachWithoutCollision(Node $$0) {
        AABB $$1 = this.mob.getBoundingBox();
        Vec3 $$2 = new Vec3((double)$$0.x - this.mob.getX() + $$1.getXsize() / 2.0, (double)$$0.y - this.mob.getY() + $$1.getYsize() / 2.0, (double)$$0.z - this.mob.getZ() + $$1.getZsize() / 2.0);
        int $$3 = Mth.ceil($$2.length() / $$1.getSize());
        $$2 = $$2.scale(1.0f / (float)$$3);
        for (int $$4 = 1; $$4 <= $$3; ++$$4) {
            if (!this.hasCollisions($$1 = $$1.move($$2))) continue;
            return false;
        }
        return true;
    }

    protected double getFloorLevel(BlockPos $$0) {
        if ((this.canFloat() || this.isAmphibious()) && this.level.getFluidState($$0).is(FluidTags.WATER)) {
            return (double)$$0.getY() + 0.5;
        }
        return WalkNodeEvaluator.getFloorLevel(this.level, $$0);
    }

    public static double getFloorLevel(BlockGetter $$0, BlockPos $$1) {
        Vec3i $$2 = $$1.below();
        VoxelShape $$3 = $$0.getBlockState((BlockPos)$$2).getCollisionShape($$0, (BlockPos)$$2);
        return (double)$$2.getY() + ($$3.isEmpty() ? 0.0 : $$3.max(Direction.Axis.Y));
    }

    protected boolean isAmphibious() {
        return false;
    }

    @Nullable
    protected Node findAcceptedNode(int $$0, int $$1, int $$2, int $$3, double $$4, Direction $$5, BlockPathTypes $$6) {
        double $$14;
        double $$13;
        AABB $$15;
        Node $$7 = null;
        BlockPos.MutableBlockPos $$8 = new BlockPos.MutableBlockPos();
        double $$9 = this.getFloorLevel($$8.set($$0, $$1, $$2));
        if ($$9 - $$4 > this.getMobJumpHeight()) {
            return null;
        }
        BlockPathTypes $$10 = this.getCachedBlockType(this.mob, $$0, $$1, $$2);
        float $$11 = this.mob.getPathfindingMalus($$10);
        double $$12 = (double)this.mob.getBbWidth() / 2.0;
        if ($$11 >= 0.0f) {
            $$7 = this.getNodeAndUpdateCostToMax($$0, $$1, $$2, $$10, $$11);
        }
        if (WalkNodeEvaluator.doesBlockHavePartialCollision($$6) && $$7 != null && $$7.costMalus >= 0.0f && !this.canReachWithoutCollision($$7)) {
            $$7 = null;
        }
        if ($$10 == BlockPathTypes.WALKABLE || this.isAmphibious() && $$10 == BlockPathTypes.WATER) {
            return $$7;
        }
        if (($$7 == null || $$7.costMalus < 0.0f) && $$3 > 0 && ($$10 != BlockPathTypes.FENCE || this.canWalkOverFences()) && $$10 != BlockPathTypes.UNPASSABLE_RAIL && $$10 != BlockPathTypes.TRAPDOOR && $$10 != BlockPathTypes.POWDER_SNOW && ($$7 = this.findAcceptedNode($$0, $$1 + 1, $$2, $$3 - 1, $$4, $$5, $$6)) != null && ($$7.type == BlockPathTypes.OPEN || $$7.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0f && this.hasCollisions($$15 = new AABB(($$13 = (double)($$0 - $$5.getStepX()) + 0.5) - $$12, this.getFloorLevel($$8.set($$13, (double)($$1 + 1), $$14 = (double)($$2 - $$5.getStepZ()) + 0.5)) + 0.001, $$14 - $$12, $$13 + $$12, (double)this.mob.getBbHeight() + this.getFloorLevel($$8.set((double)$$7.x, (double)$$7.y, (double)$$7.z)) - 0.002, $$14 + $$12))) {
            $$7 = null;
        }
        if (!this.isAmphibious() && $$10 == BlockPathTypes.WATER && !this.canFloat()) {
            if (this.getCachedBlockType(this.mob, $$0, $$1 - 1, $$2) != BlockPathTypes.WATER) {
                return $$7;
            }
            while ($$1 > this.mob.level.getMinBuildHeight()) {
                if (($$10 = this.getCachedBlockType(this.mob, $$0, --$$1, $$2)) == BlockPathTypes.WATER) {
                    $$7 = this.getNodeAndUpdateCostToMax($$0, $$1, $$2, $$10, this.mob.getPathfindingMalus($$10));
                    continue;
                }
                return $$7;
            }
        }
        if ($$10 == BlockPathTypes.OPEN) {
            int $$16 = 0;
            int $$17 = $$1;
            while ($$10 == BlockPathTypes.OPEN) {
                if (--$$1 < this.mob.level.getMinBuildHeight()) {
                    return this.getBlockedNode($$0, $$17, $$2);
                }
                if ($$16++ >= this.mob.getMaxFallDistance()) {
                    return this.getBlockedNode($$0, $$1, $$2);
                }
                $$10 = this.getCachedBlockType(this.mob, $$0, $$1, $$2);
                $$11 = this.mob.getPathfindingMalus($$10);
                if ($$10 != BlockPathTypes.OPEN && $$11 >= 0.0f) {
                    $$7 = this.getNodeAndUpdateCostToMax($$0, $$1, $$2, $$10, $$11);
                    break;
                }
                if (!($$11 < 0.0f)) continue;
                return this.getBlockedNode($$0, $$1, $$2);
            }
        }
        if (WalkNodeEvaluator.doesBlockHavePartialCollision($$10) && $$7 == null) {
            $$7 = this.getNode($$0, $$1, $$2);
            $$7.closed = true;
            $$7.type = $$10;
            $$7.costMalus = $$10.getMalus();
        }
        return $$7;
    }

    private double getMobJumpHeight() {
        return Math.max((double)1.125, (double)this.mob.maxUpStep);
    }

    private Node getNodeAndUpdateCostToMax(int $$0, int $$1, int $$2, BlockPathTypes $$3, float $$4) {
        Node $$5 = this.getNode($$0, $$1, $$2);
        $$5.type = $$3;
        $$5.costMalus = Math.max((float)$$5.costMalus, (float)$$4);
        return $$5;
    }

    private Node getBlockedNode(int $$0, int $$1, int $$2) {
        Node $$3 = this.getNode($$0, $$1, $$2);
        $$3.type = BlockPathTypes.BLOCKED;
        $$3.costMalus = -1.0f;
        return $$3;
    }

    private boolean hasCollisions(AABB $$0) {
        return this.collisionCache.computeIfAbsent((Object)$$0, $$1 -> !this.level.noCollision(this.mob, $$0));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3, Mob $$4, int $$5, int $$6, int $$7, boolean $$8, boolean $$9) {
        EnumSet $$10 = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes $$11 = BlockPathTypes.BLOCKED;
        BlockPos $$12 = $$4.blockPosition();
        $$11 = this.getBlockPathTypes($$0, $$1, $$2, $$3, $$5, $$6, $$7, $$8, $$9, (EnumSet<BlockPathTypes>)$$10, $$11, $$12);
        if ($$10.contains((Object)BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        }
        if ($$10.contains((Object)BlockPathTypes.UNPASSABLE_RAIL)) {
            return BlockPathTypes.UNPASSABLE_RAIL;
        }
        BlockPathTypes $$13 = BlockPathTypes.BLOCKED;
        for (BlockPathTypes $$14 : $$10) {
            if ($$4.getPathfindingMalus($$14) < 0.0f) {
                return $$14;
            }
            if (!($$4.getPathfindingMalus($$14) >= $$4.getPathfindingMalus($$13))) continue;
            $$13 = $$14;
        }
        if ($$11 == BlockPathTypes.OPEN && $$4.getPathfindingMalus($$13) == 0.0f && $$5 <= 1) {
            return BlockPathTypes.OPEN;
        }
        return $$13;
    }

    public BlockPathTypes getBlockPathTypes(BlockGetter $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8, EnumSet<BlockPathTypes> $$9, BlockPathTypes $$10, BlockPos $$11) {
        for (int $$12 = 0; $$12 < $$4; ++$$12) {
            for (int $$13 = 0; $$13 < $$5; ++$$13) {
                for (int $$14 = 0; $$14 < $$6; ++$$14) {
                    int $$15 = $$12 + $$1;
                    int $$16 = $$13 + $$2;
                    int $$17 = $$14 + $$3;
                    BlockPathTypes $$18 = this.getBlockPathType($$0, $$15, $$16, $$17);
                    $$18 = this.evaluateBlockPathType($$0, $$7, $$8, $$11, $$18);
                    if ($$12 == 0 && $$13 == 0 && $$14 == 0) {
                        $$10 = $$18;
                    }
                    $$9.add((Object)$$18);
                }
            }
        }
        return $$10;
    }

    protected BlockPathTypes evaluateBlockPathType(BlockGetter $$0, boolean $$1, boolean $$2, BlockPos $$3, BlockPathTypes $$4) {
        if ($$4 == BlockPathTypes.DOOR_WOOD_CLOSED && $$1 && $$2) {
            $$4 = BlockPathTypes.WALKABLE_DOOR;
        }
        if ($$4 == BlockPathTypes.DOOR_OPEN && !$$2) {
            $$4 = BlockPathTypes.BLOCKED;
        }
        if ($$4 == BlockPathTypes.RAIL && !($$0.getBlockState($$3).getBlock() instanceof BaseRailBlock) && !($$0.getBlockState((BlockPos)$$3.below()).getBlock() instanceof BaseRailBlock)) {
            $$4 = BlockPathTypes.UNPASSABLE_RAIL;
        }
        if ($$4 == BlockPathTypes.LEAVES) {
            $$4 = BlockPathTypes.BLOCKED;
        }
        return $$4;
    }

    protected BlockPathTypes getBlockPathType(Mob $$0, BlockPos $$1) {
        return this.getCachedBlockType($$0, $$1.getX(), $$1.getY(), $$1.getZ());
    }

    protected BlockPathTypes getCachedBlockType(Mob $$0, int $$1, int $$2, int $$3) {
        return (BlockPathTypes)((Object)this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong($$1, $$2, $$3), $$4 -> this.getBlockPathType(this.level, $$1, $$2, $$3, $$0, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors())));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter $$0, int $$1, int $$2, int $$3) {
        return WalkNodeEvaluator.getBlockPathTypeStatic($$0, new BlockPos.MutableBlockPos($$1, $$2, $$3));
    }

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter $$0, BlockPos.MutableBlockPos $$1) {
        int $$2 = $$1.getX();
        int $$3 = $$1.getY();
        int $$4 = $$1.getZ();
        BlockPathTypes $$5 = WalkNodeEvaluator.getBlockPathTypeRaw($$0, $$1);
        if ($$5 == BlockPathTypes.OPEN && $$3 >= $$0.getMinBuildHeight() + 1) {
            BlockPathTypes $$6 = WalkNodeEvaluator.getBlockPathTypeRaw($$0, $$1.set($$2, $$3 - 1, $$4));
            BlockPathTypes blockPathTypes = $$5 = $$6 == BlockPathTypes.WALKABLE || $$6 == BlockPathTypes.OPEN || $$6 == BlockPathTypes.WATER || $$6 == BlockPathTypes.LAVA ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE;
            if ($$6 == BlockPathTypes.DAMAGE_FIRE) {
                $$5 = BlockPathTypes.DAMAGE_FIRE;
            }
            if ($$6 == BlockPathTypes.DAMAGE_CACTUS) {
                $$5 = BlockPathTypes.DAMAGE_CACTUS;
            }
            if ($$6 == BlockPathTypes.DAMAGE_OTHER) {
                $$5 = BlockPathTypes.DAMAGE_OTHER;
            }
            if ($$6 == BlockPathTypes.STICKY_HONEY) {
                $$5 = BlockPathTypes.STICKY_HONEY;
            }
            if ($$6 == BlockPathTypes.POWDER_SNOW) {
                $$5 = BlockPathTypes.DANGER_POWDER_SNOW;
            }
        }
        if ($$5 == BlockPathTypes.WALKABLE) {
            $$5 = WalkNodeEvaluator.checkNeighbourBlocks($$0, $$1.set($$2, $$3, $$4), $$5);
        }
        return $$5;
    }

    public static BlockPathTypes checkNeighbourBlocks(BlockGetter $$0, BlockPos.MutableBlockPos $$1, BlockPathTypes $$2) {
        int $$3 = $$1.getX();
        int $$4 = $$1.getY();
        int $$5 = $$1.getZ();
        for (int $$6 = -1; $$6 <= 1; ++$$6) {
            for (int $$7 = -1; $$7 <= 1; ++$$7) {
                for (int $$8 = -1; $$8 <= 1; ++$$8) {
                    if ($$6 == 0 && $$8 == 0) continue;
                    $$1.set($$3 + $$6, $$4 + $$7, $$5 + $$8);
                    BlockState $$9 = $$0.getBlockState($$1);
                    if ($$9.is(Blocks.CACTUS)) {
                        return BlockPathTypes.DANGER_CACTUS;
                    }
                    if ($$9.is(Blocks.SWEET_BERRY_BUSH)) {
                        return BlockPathTypes.DANGER_OTHER;
                    }
                    if (WalkNodeEvaluator.isBurningBlock($$9)) {
                        return BlockPathTypes.DANGER_FIRE;
                    }
                    if (!$$0.getFluidState($$1).is(FluidTags.WATER)) continue;
                    return BlockPathTypes.WATER_BORDER;
                }
            }
        }
        return $$2;
    }

    protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        Block $$3 = $$2.getBlock();
        Material $$4 = $$2.getMaterial();
        if ($$2.isAir()) {
            return BlockPathTypes.OPEN;
        }
        if ($$2.is(BlockTags.TRAPDOORS) || $$2.is(Blocks.LILY_PAD) || $$2.is(Blocks.BIG_DRIPLEAF)) {
            return BlockPathTypes.TRAPDOOR;
        }
        if ($$2.is(Blocks.POWDER_SNOW)) {
            return BlockPathTypes.POWDER_SNOW;
        }
        if ($$2.is(Blocks.CACTUS)) {
            return BlockPathTypes.DAMAGE_CACTUS;
        }
        if ($$2.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DAMAGE_OTHER;
        }
        if ($$2.is(Blocks.HONEY_BLOCK)) {
            return BlockPathTypes.STICKY_HONEY;
        }
        if ($$2.is(Blocks.COCOA)) {
            return BlockPathTypes.COCOA;
        }
        FluidState $$5 = $$0.getFluidState($$1);
        if ($$5.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }
        if (WalkNodeEvaluator.isBurningBlock($$2)) {
            return BlockPathTypes.DAMAGE_FIRE;
        }
        if (DoorBlock.isWoodenDoor($$2) && !$$2.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
        }
        if ($$3 instanceof DoorBlock && $$4 == Material.METAL && !$$2.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
        }
        if ($$3 instanceof DoorBlock && $$2.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_OPEN;
        }
        if ($$3 instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
        }
        if ($$3 instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
        }
        if ($$2.is(BlockTags.FENCES) || $$2.is(BlockTags.WALLS) || $$3 instanceof FenceGateBlock && !$$2.getValue(FenceGateBlock.OPEN).booleanValue()) {
            return BlockPathTypes.FENCE;
        }
        if (!$$2.isPathfindable($$0, $$1, PathComputationType.LAND)) {
            return BlockPathTypes.BLOCKED;
        }
        if ($$5.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }
        return BlockPathTypes.OPEN;
    }

    public static boolean isBurningBlock(BlockState $$0) {
        return $$0.is(BlockTags.FIRE) || $$0.is(Blocks.LAVA) || $$0.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire($$0) || $$0.is(Blocks.LAVA_CAULDRON);
    }
}