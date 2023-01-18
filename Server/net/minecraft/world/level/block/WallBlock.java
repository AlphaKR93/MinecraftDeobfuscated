/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallSide> EAST_WALL = BlockStateProperties.EAST_WALL;
    public static final EnumProperty<WallSide> NORTH_WALL = BlockStateProperties.NORTH_WALL;
    public static final EnumProperty<WallSide> SOUTH_WALL = BlockStateProperties.SOUTH_WALL;
    public static final EnumProperty<WallSide> WEST_WALL = BlockStateProperties.WEST_WALL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeByIndex;
    private final Map<BlockState, VoxelShape> collisionShapeByIndex;
    private static final int WALL_WIDTH = 3;
    private static final int WALL_HEIGHT = 14;
    private static final int POST_WIDTH = 4;
    private static final int POST_COVER_WIDTH = 1;
    private static final int WALL_COVER_START = 7;
    private static final int WALL_COVER_END = 9;
    private static final VoxelShape POST_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape NORTH_TEST = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
    private static final VoxelShape SOUTH_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
    private static final VoxelShape WEST_TEST = Block.box(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape EAST_TEST = Block.box(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

    public WallBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH_WALL, WallSide.NONE)).setValue(EAST_WALL, WallSide.NONE)).setValue(SOUTH_WALL, WallSide.NONE)).setValue(WEST_WALL, WallSide.NONE)).setValue(WATERLOGGED, false));
        this.shapeByIndex = this.makeShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f, 16.0f);
        this.collisionShapeByIndex = this.makeShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f, 24.0f);
    }

    private static VoxelShape applyWallShape(VoxelShape $$0, WallSide $$1, VoxelShape $$2, VoxelShape $$3) {
        if ($$1 == WallSide.TALL) {
            return Shapes.or($$0, $$3);
        }
        if ($$1 == WallSide.LOW) {
            return Shapes.or($$0, $$2);
        }
        return $$0;
    }

    private Map<BlockState, VoxelShape> makeShapes(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = 8.0f - $$0;
        float $$7 = 8.0f + $$0;
        float $$8 = 8.0f - $$1;
        float $$9 = 8.0f + $$1;
        VoxelShape $$10 = Block.box($$6, 0.0, $$6, $$7, $$2, $$7);
        VoxelShape $$11 = Block.box($$8, $$3, 0.0, $$9, $$4, $$9);
        VoxelShape $$12 = Block.box($$8, $$3, $$8, $$9, $$4, 16.0);
        VoxelShape $$13 = Block.box(0.0, $$3, $$8, $$9, $$4, $$9);
        VoxelShape $$14 = Block.box($$8, $$3, $$8, 16.0, $$4, $$9);
        VoxelShape $$15 = Block.box($$8, $$3, 0.0, $$9, $$5, $$9);
        VoxelShape $$16 = Block.box($$8, $$3, $$8, $$9, $$5, 16.0);
        VoxelShape $$17 = Block.box(0.0, $$3, $$8, $$9, $$5, $$9);
        VoxelShape $$18 = Block.box($$8, $$3, $$8, 16.0, $$5, $$9);
        ImmutableMap.Builder $$19 = ImmutableMap.builder();
        for (Boolean $$20 : UP.getPossibleValues()) {
            for (WallSide $$21 : EAST_WALL.getPossibleValues()) {
                for (WallSide $$22 : NORTH_WALL.getPossibleValues()) {
                    for (WallSide $$23 : WEST_WALL.getPossibleValues()) {
                        for (WallSide $$24 : SOUTH_WALL.getPossibleValues()) {
                            VoxelShape $$25 = Shapes.empty();
                            $$25 = WallBlock.applyWallShape($$25, $$21, $$14, $$18);
                            $$25 = WallBlock.applyWallShape($$25, $$23, $$13, $$17);
                            $$25 = WallBlock.applyWallShape($$25, $$22, $$11, $$15);
                            $$25 = WallBlock.applyWallShape($$25, $$24, $$12, $$16);
                            if ($$20.booleanValue()) {
                                $$25 = Shapes.or($$25, $$10);
                            }
                            BlockState $$26 = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(UP, $$20)).setValue(EAST_WALL, $$21)).setValue(WEST_WALL, $$23)).setValue(NORTH_WALL, $$22)).setValue(SOUTH_WALL, $$24);
                            $$19.put((Object)((BlockState)$$26.setValue(WATERLOGGED, false)), (Object)$$25);
                            $$19.put((Object)((BlockState)$$26.setValue(WATERLOGGED, true)), (Object)$$25);
                        }
                    }
                }
            }
        }
        return $$19.build();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)this.shapeByIndex.get((Object)$$0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)this.collisionShapeByIndex.get((Object)$$0);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    private boolean connectsTo(BlockState $$0, boolean $$1, Direction $$2) {
        Block $$3 = $$0.getBlock();
        boolean $$4 = $$3 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection($$0, $$2);
        return $$0.is(BlockTags.WALLS) || !WallBlock.isExceptionForConnection($$0) && $$1 || $$3 instanceof IronBarsBlock || $$4;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        FluidState $$3 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Vec3i $$4 = $$2.north();
        Vec3i $$5 = $$2.east();
        Vec3i $$6 = $$2.south();
        Vec3i $$7 = $$2.west();
        Vec3i $$8 = $$2.above();
        BlockState $$9 = $$1.getBlockState((BlockPos)$$4);
        BlockState $$10 = $$1.getBlockState((BlockPos)$$5);
        BlockState $$11 = $$1.getBlockState((BlockPos)$$6);
        BlockState $$12 = $$1.getBlockState((BlockPos)$$7);
        BlockState $$13 = $$1.getBlockState((BlockPos)$$8);
        boolean $$14 = this.connectsTo($$9, $$9.isFaceSturdy($$1, (BlockPos)$$4, Direction.SOUTH), Direction.SOUTH);
        boolean $$15 = this.connectsTo($$10, $$10.isFaceSturdy($$1, (BlockPos)$$5, Direction.WEST), Direction.WEST);
        boolean $$16 = this.connectsTo($$11, $$11.isFaceSturdy($$1, (BlockPos)$$6, Direction.NORTH), Direction.NORTH);
        boolean $$17 = this.connectsTo($$12, $$12.isFaceSturdy($$1, (BlockPos)$$7, Direction.EAST), Direction.EAST);
        BlockState $$18 = (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
        return this.updateShape($$1, $$18, (BlockPos)$$8, $$13, $$14, $$15, $$16, $$17);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1 == Direction.DOWN) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
        }
        if ($$1 == Direction.UP) {
            return this.topUpdate($$3, $$0, $$5, $$2);
        }
        return this.sideUpdate($$3, $$4, $$0, $$5, $$2, $$1);
    }

    private static boolean isConnected(BlockState $$0, Property<WallSide> $$1) {
        return $$0.getValue($$1) != WallSide.NONE;
    }

    private static boolean isCovered(VoxelShape $$0, VoxelShape $$1) {
        return !Shapes.joinIsNotEmpty($$1, $$0, BooleanOp.ONLY_FIRST);
    }

    private BlockState topUpdate(LevelReader $$0, BlockState $$1, BlockPos $$2, BlockState $$3) {
        boolean $$4 = WallBlock.isConnected($$1, NORTH_WALL);
        boolean $$5 = WallBlock.isConnected($$1, EAST_WALL);
        boolean $$6 = WallBlock.isConnected($$1, SOUTH_WALL);
        boolean $$7 = WallBlock.isConnected($$1, WEST_WALL);
        return this.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private BlockState sideUpdate(LevelReader $$0, BlockPos $$1, BlockState $$2, BlockPos $$3, BlockState $$4, Direction $$5) {
        Direction $$6 = $$5.getOpposite();
        boolean $$7 = $$5 == Direction.NORTH ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, NORTH_WALL);
        boolean $$8 = $$5 == Direction.EAST ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, EAST_WALL);
        boolean $$9 = $$5 == Direction.SOUTH ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, SOUTH_WALL);
        boolean $$10 = $$5 == Direction.WEST ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, WEST_WALL);
        Vec3i $$11 = $$1.above();
        BlockState $$12 = $$0.getBlockState((BlockPos)$$11);
        return this.updateShape($$0, $$2, (BlockPos)$$11, $$12, $$7, $$8, $$9, $$10);
    }

    private BlockState updateShape(LevelReader $$0, BlockState $$1, BlockPos $$2, BlockState $$3, boolean $$4, boolean $$5, boolean $$6, boolean $$7) {
        VoxelShape $$8 = $$3.getCollisionShape($$0, $$2).getFaceShape(Direction.DOWN);
        BlockState $$9 = this.updateSides($$1, $$4, $$5, $$6, $$7, $$8);
        return (BlockState)$$9.setValue(UP, this.shouldRaisePost($$9, $$3, $$8));
    }

    private boolean shouldRaisePost(BlockState $$0, BlockState $$1, VoxelShape $$2) {
        boolean $$13;
        boolean $$12;
        boolean $$3;
        boolean bl = $$3 = $$1.getBlock() instanceof WallBlock && $$1.getValue(UP) != false;
        if ($$3) {
            return true;
        }
        WallSide $$4 = $$0.getValue(NORTH_WALL);
        WallSide $$5 = $$0.getValue(SOUTH_WALL);
        WallSide $$6 = $$0.getValue(EAST_WALL);
        WallSide $$7 = $$0.getValue(WEST_WALL);
        boolean $$8 = $$5 == WallSide.NONE;
        boolean $$9 = $$7 == WallSide.NONE;
        boolean $$10 = $$6 == WallSide.NONE;
        boolean $$11 = $$4 == WallSide.NONE;
        boolean bl2 = $$12 = $$11 && $$8 && $$9 && $$10 || $$11 != $$8 || $$9 != $$10;
        if ($$12) {
            return true;
        }
        boolean bl3 = $$13 = $$4 == WallSide.TALL && $$5 == WallSide.TALL || $$6 == WallSide.TALL && $$7 == WallSide.TALL;
        if ($$13) {
            return false;
        }
        return $$1.is(BlockTags.WALL_POST_OVERRIDE) || WallBlock.isCovered($$2, POST_TEST);
    }

    private BlockState updateSides(BlockState $$0, boolean $$1, boolean $$2, boolean $$3, boolean $$4, VoxelShape $$5) {
        return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH_WALL, this.makeWallState($$1, $$5, NORTH_TEST))).setValue(EAST_WALL, this.makeWallState($$2, $$5, EAST_TEST))).setValue(SOUTH_WALL, this.makeWallState($$3, $$5, SOUTH_TEST))).setValue(WEST_WALL, this.makeWallState($$4, $$5, WEST_TEST));
    }

    private WallSide makeWallState(boolean $$0, VoxelShape $$1, VoxelShape $$2) {
        if ($$0) {
            if (WallBlock.isCovered($$1, $$2)) {
                return WallSide.TALL;
            }
            return WallSide.LOW;
        }
        return WallSide.NONE;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getValue(WATERLOGGED) == false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH_WALL, $$0.getValue(SOUTH_WALL))).setValue(EAST_WALL, $$0.getValue(WEST_WALL))).setValue(SOUTH_WALL, $$0.getValue(NORTH_WALL))).setValue(WEST_WALL, $$0.getValue(EAST_WALL));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH_WALL, $$0.getValue(EAST_WALL))).setValue(EAST_WALL, $$0.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, $$0.getValue(WEST_WALL))).setValue(WEST_WALL, $$0.getValue(NORTH_WALL));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH_WALL, $$0.getValue(WEST_WALL))).setValue(EAST_WALL, $$0.getValue(NORTH_WALL))).setValue(SOUTH_WALL, $$0.getValue(EAST_WALL))).setValue(WEST_WALL, $$0.getValue(SOUTH_WALL));
            }
        }
        return $$0;
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH_WALL, $$0.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, $$0.getValue(NORTH_WALL));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST_WALL, $$0.getValue(WEST_WALL))).setValue(WEST_WALL, $$0.getValue(EAST_WALL));
            }
        }
        return super.mirror($$0, $$1);
    }
}