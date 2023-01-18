/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrossCollisionBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter($$0 -> ((Direction)$$0.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap();

    protected CrossCollisionBlock(float $$0, float $$1, float $$2, float $$3, float $$4, BlockBehaviour.Properties $$5) {
        super($$5);
        this.collisionShapeByIndex = this.makeShapes($$0, $$1, $$4, 0.0f, $$4);
        this.shapeByIndex = this.makeShapes($$0, $$1, $$2, 0.0f, $$3);
        for (BlockState $$6 : this.stateDefinition.getPossibleStates()) {
            this.getAABBIndex($$6);
        }
    }

    protected VoxelShape[] makeShapes(float $$0, float $$1, float $$2, float $$3, float $$4) {
        float $$5 = 8.0f - $$0;
        float $$6 = 8.0f + $$0;
        float $$7 = 8.0f - $$1;
        float $$8 = 8.0f + $$1;
        VoxelShape $$9 = Block.box($$5, 0.0, $$5, $$6, $$2, $$6);
        VoxelShape $$10 = Block.box($$7, $$3, 0.0, $$8, $$4, $$8);
        VoxelShape $$11 = Block.box($$7, $$3, $$7, $$8, $$4, 16.0);
        VoxelShape $$12 = Block.box(0.0, $$3, $$7, $$8, $$4, $$8);
        VoxelShape $$13 = Block.box($$7, $$3, $$7, 16.0, $$4, $$8);
        VoxelShape $$14 = Shapes.or($$10, $$13);
        VoxelShape $$15 = Shapes.or($$11, $$12);
        VoxelShape[] $$16 = new VoxelShape[]{Shapes.empty(), $$11, $$12, $$15, $$10, Shapes.or($$11, $$10), Shapes.or($$12, $$10), Shapes.or($$15, $$10), $$13, Shapes.or($$11, $$13), Shapes.or($$12, $$13), Shapes.or($$15, $$13), $$14, Shapes.or($$11, $$14), Shapes.or($$12, $$14), Shapes.or($$15, $$14)};
        for (int $$17 = 0; $$17 < 16; ++$$17) {
            $$16[$$17] = Shapes.or($$9, $$16[$$17]);
        }
        return $$16;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getValue(WATERLOGGED) == false;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapeByIndex[this.getAABBIndex($$0)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.collisionShapeByIndex[this.getAABBIndex($$0)];
    }

    private static int indexFor(Direction $$0) {
        return 1 << $$0.get2DDataValue();
    }

    protected int getAABBIndex(BlockState $$02) {
        return this.stateToIndex.computeIntIfAbsent((Object)$$02, $$0 -> {
            int $$1 = 0;
            if ($$0.getValue(NORTH).booleanValue()) {
                $$1 |= CrossCollisionBlock.indexFor(Direction.NORTH);
            }
            if ($$0.getValue(EAST).booleanValue()) {
                $$1 |= CrossCollisionBlock.indexFor(Direction.EAST);
            }
            if ($$0.getValue(SOUTH).booleanValue()) {
                $$1 |= CrossCollisionBlock.indexFor(Direction.SOUTH);
            }
            if ($$0.getValue(WEST).booleanValue()) {
                $$1 |= CrossCollisionBlock.indexFor(Direction.WEST);
            }
            return $$1;
        });
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
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
}