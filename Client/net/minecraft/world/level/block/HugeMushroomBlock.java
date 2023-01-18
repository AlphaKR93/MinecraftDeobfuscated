/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock
extends Block {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public HugeMushroomBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, true)).setValue(EAST, true)).setValue(SOUTH, true)).setValue(WEST, true)).setValue(UP, true)).setValue(DOWN, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, !$$1.getBlockState((BlockPos)$$2.below()).is(this))).setValue(UP, !$$1.getBlockState((BlockPos)$$2.above()).is(this))).setValue(NORTH, !$$1.getBlockState((BlockPos)$$2.north()).is(this))).setValue(EAST, !$$1.getBlockState((BlockPos)$$2.east()).is(this))).setValue(SOUTH, !$$1.getBlockState((BlockPos)$$2.south()).is(this))).setValue(WEST, !$$1.getBlockState((BlockPos)$$2.west()).is(this));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$2.is(this)) {
            return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), false);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.NORTH)), $$0.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.SOUTH)), $$0.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.EAST)), $$0.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.WEST)), $$0.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.UP)), $$0.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.rotate(Direction.DOWN)), $$0.getValue(DOWN));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.NORTH)), $$0.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.SOUTH)), $$0.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.EAST)), $$0.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.WEST)), $$0.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.UP)), $$0.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1.mirror(Direction.DOWN)), $$0.getValue(DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}