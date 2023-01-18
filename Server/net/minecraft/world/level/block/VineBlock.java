/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineBlock
extends Block {
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter($$0 -> $$0.getKey() != Direction.DOWN).collect(Util.toMap());
    protected static final float AABB_OFFSET = 1.0f;
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesCache;

    public VineBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
        this.shapesCache = ImmutableMap.copyOf((Map)((Map)this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap((Function)Function.identity(), VineBlock::calculateShape))));
    }

    private static VoxelShape calculateShape(BlockState $$0) {
        VoxelShape $$1 = Shapes.empty();
        if ($$0.getValue(UP).booleanValue()) {
            $$1 = UP_AABB;
        }
        if ($$0.getValue(NORTH).booleanValue()) {
            $$1 = Shapes.or($$1, NORTH_AABB);
        }
        if ($$0.getValue(SOUTH).booleanValue()) {
            $$1 = Shapes.or($$1, SOUTH_AABB);
        }
        if ($$0.getValue(EAST).booleanValue()) {
            $$1 = Shapes.or($$1, EAST_AABB);
        }
        if ($$0.getValue(WEST).booleanValue()) {
            $$1 = Shapes.or($$1, WEST_AABB);
        }
        return $$1.isEmpty() ? Shapes.block() : $$1;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)this.shapesCache.get((Object)$$0);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return this.hasFaces(this.getUpdatedState($$0, $$1, $$2));
    }

    private boolean hasFaces(BlockState $$0) {
        return this.countFaces($$0) > 0;
    }

    private int countFaces(BlockState $$0) {
        int $$1 = 0;
        for (BooleanProperty $$2 : PROPERTY_BY_DIRECTION.values()) {
            if (!$$0.getValue($$2).booleanValue()) continue;
            ++$$1;
        }
        return $$1;
    }

    private boolean canSupportAtFace(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        if ($$2 == Direction.DOWN) {
            return false;
        }
        Vec3i $$3 = $$1.relative($$2);
        if (VineBlock.isAcceptableNeighbour($$0, (BlockPos)$$3, $$2)) {
            return true;
        }
        if ($$2.getAxis() != Direction.Axis.Y) {
            BooleanProperty $$4 = (BooleanProperty)PROPERTY_BY_DIRECTION.get((Object)$$2);
            BlockState $$5 = $$0.getBlockState((BlockPos)$$1.above());
            return $$5.is(this) && $$5.getValue($$4) != false;
        }
        return false;
    }

    public static boolean isAcceptableNeighbour(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        return MultifaceBlock.canAttachTo($$0, $$2, $$1, $$0.getBlockState($$1));
    }

    private BlockState getUpdatedState(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.above();
        if ($$0.getValue(UP).booleanValue()) {
            $$0 = (BlockState)$$0.setValue(UP, VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$3, Direction.DOWN));
        }
        BlockBehaviour.BlockStateBase $$4 = null;
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            BooleanProperty $$6 = VineBlock.getPropertyForFace($$5);
            if (!$$0.getValue($$6).booleanValue()) continue;
            boolean $$7 = this.canSupportAtFace($$1, $$2, $$5);
            if (!$$7) {
                if ($$4 == null) {
                    $$4 = $$1.getBlockState((BlockPos)$$3);
                }
                $$7 = $$4.is(this) && $$4.getValue($$6) != false;
            }
            $$0 = (BlockState)$$0.setValue($$6, $$7);
        }
        return $$0;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
        }
        BlockState $$6 = this.getUpdatedState($$0, $$3, $$4);
        if (!this.hasFaces($$6)) {
            return Blocks.AIR.defaultBlockState();
        }
        return $$6;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$20;
        BlockState $$19;
        Vec3i $$17;
        BlockState $$18;
        if ($$3.nextInt(4) != 0) {
            return;
        }
        Direction $$4 = Direction.getRandom($$3);
        Vec3i $$5 = $$2.above();
        if ($$4.getAxis().isHorizontal() && !$$0.getValue(VineBlock.getPropertyForFace($$4)).booleanValue()) {
            if (!this.canSpread($$1, $$2)) {
                return;
            }
            Vec3i $$6 = $$2.relative($$4);
            BlockState $$7 = $$1.getBlockState((BlockPos)$$6);
            if ($$7.isAir()) {
                Direction $$8 = $$4.getClockWise();
                Direction $$9 = $$4.getCounterClockWise();
                boolean $$10 = $$0.getValue(VineBlock.getPropertyForFace($$8));
                boolean $$11 = $$0.getValue(VineBlock.getPropertyForFace($$9));
                Vec3i $$12 = ((BlockPos)$$6).relative($$8);
                Vec3i $$13 = ((BlockPos)$$6).relative($$9);
                if ($$10 && VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$12, $$8)) {
                    $$1.setBlock((BlockPos)$$6, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace($$8), true), 2);
                } else if ($$11 && VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$13, $$9)) {
                    $$1.setBlock((BlockPos)$$6, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace($$9), true), 2);
                } else {
                    Direction $$14 = $$4.getOpposite();
                    if ($$10 && $$1.isEmptyBlock((BlockPos)$$12) && VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$2.relative($$8), $$14)) {
                        $$1.setBlock((BlockPos)$$12, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace($$14), true), 2);
                    } else if ($$11 && $$1.isEmptyBlock((BlockPos)$$13) && VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$2.relative($$9), $$14)) {
                        $$1.setBlock((BlockPos)$$13, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace($$14), true), 2);
                    } else if ((double)$$3.nextFloat() < 0.05 && VineBlock.isAcceptableNeighbour($$1, (BlockPos)((BlockPos)$$6).above(), Direction.UP)) {
                        $$1.setBlock((BlockPos)$$6, (BlockState)this.defaultBlockState().setValue(UP, true), 2);
                    }
                }
            } else if (VineBlock.isAcceptableNeighbour($$1, (BlockPos)$$6, $$4)) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(VineBlock.getPropertyForFace($$4), true), 2);
            }
            return;
        }
        if ($$4 == Direction.UP && $$2.getY() < $$1.getMaxBuildHeight() - 1) {
            if (this.canSupportAtFace($$1, $$2, $$4)) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(UP, true), 2);
                return;
            }
            if ($$1.isEmptyBlock((BlockPos)$$5)) {
                if (!this.canSpread($$1, $$2)) {
                    return;
                }
                BlockState $$15 = $$0;
                for (Direction $$16 : Direction.Plane.HORIZONTAL) {
                    if (!$$3.nextBoolean() && VineBlock.isAcceptableNeighbour($$1, (BlockPos)((BlockPos)$$5).relative($$16), $$16)) continue;
                    $$15 = (BlockState)$$15.setValue(VineBlock.getPropertyForFace($$16), false);
                }
                if (this.hasHorizontalConnection($$15)) {
                    $$1.setBlock((BlockPos)$$5, $$15, 2);
                }
                return;
            }
        }
        if ($$2.getY() > $$1.getMinBuildHeight() && (($$18 = $$1.getBlockState((BlockPos)($$17 = $$2.below()))).isAir() || $$18.is(this)) && ($$19 = $$18.isAir() ? this.defaultBlockState() : $$18) != ($$20 = this.copyRandomFaces($$0, $$19, $$3)) && this.hasHorizontalConnection($$20)) {
            $$1.setBlock((BlockPos)$$17, $$20, 2);
        }
    }

    private BlockState copyRandomFaces(BlockState $$0, BlockState $$1, RandomSource $$2) {
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            BooleanProperty $$4;
            if (!$$2.nextBoolean() || !$$0.getValue($$4 = VineBlock.getPropertyForFace($$3)).booleanValue()) continue;
            $$1 = (BlockState)$$1.setValue($$4, true);
        }
        return $$1;
    }

    private boolean hasHorizontalConnection(BlockState $$0) {
        return $$0.getValue(NORTH) != false || $$0.getValue(EAST) != false || $$0.getValue(SOUTH) != false || $$0.getValue(WEST) != false;
    }

    private boolean canSpread(BlockGetter $$0, BlockPos $$1) {
        int $$2 = 4;
        Iterable<BlockPos> $$3 = BlockPos.betweenClosed($$1.getX() - 4, $$1.getY() - 1, $$1.getZ() - 4, $$1.getX() + 4, $$1.getY() + 1, $$1.getZ() + 4);
        int $$4 = 5;
        for (BlockPos $$5 : $$3) {
            if (!$$0.getBlockState($$5).is(this) || --$$4 > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        BlockState $$2 = $$1.getLevel().getBlockState($$1.getClickedPos());
        if ($$2.is(this)) {
            return this.countFaces($$2) < PROPERTY_BY_DIRECTION.size();
        }
        return super.canBeReplaced($$0, $$1);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        boolean $$2 = $$1.is(this);
        BlockState $$3 = $$2 ? $$1 : this.defaultBlockState();
        for (Direction $$4 : $$0.getNearestLookingDirections()) {
            boolean $$6;
            if ($$4 == Direction.DOWN) continue;
            BooleanProperty $$5 = VineBlock.getPropertyForFace($$4);
            boolean bl = $$6 = $$2 && $$1.getValue($$5) != false;
            if ($$6 || !this.canSupportAtFace($$0.getLevel(), $$0.getClickedPos(), $$4)) continue;
            return (BlockState)$$3.setValue($$5, true);
        }
        return $$2 ? $$3 : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(UP, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(EAST, $$0.getValue(WEST))).setValue(SOUTH, $$0.getValue(NORTH))).setValue(WEST, $$0.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(EAST))).setValue(EAST, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(WEST))).setValue(EAST, $$0.getValue(NORTH))).setValue(SOUTH, $$0.getValue(EAST))).setValue(WEST, $$0.getValue(SOUTH));
            }
        }
        return $$0;
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(EAST));
            }
        }
        return super.mirror($$0, $$1);
    }

    public static BooleanProperty getPropertyForFace(Direction $$0) {
        return (BooleanProperty)PROPERTY_BY_DIRECTION.get((Object)$$0);
    }
}