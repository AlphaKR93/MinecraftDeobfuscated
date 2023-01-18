/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ObserverBlock
extends DirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ObserverBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH)).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(POWERED).booleanValue()) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, false), 2);
        } else {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, true), 2);
            $$1.scheduleTick($$2, this, 2);
        }
        this.updateNeighborsInFront($$1, $$2, $$0);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(FACING) == $$1 && !$$0.getValue(POWERED).booleanValue()) {
            this.startSignal($$3, $$4);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private void startSignal(LevelAccessor $$0, BlockPos $$1) {
        if (!$$0.isClientSide() && !$$0.getBlockTicks().hasScheduledTick($$1, this)) {
            $$0.scheduleTick($$1, this, 2);
        }
    }

    protected void updateNeighborsInFront(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING);
        Vec3i $$4 = $$1.relative($$3.getOpposite());
        $$0.neighborChanged((BlockPos)$$4, this, $$1);
        $$0.updateNeighborsAtExceptFromFacing((BlockPos)$$4, this, $$3);
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getSignal($$1, $$2, $$3);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(POWERED).booleanValue() && $$0.getValue(FACING) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if (!$$1.isClientSide() && $$0.getValue(POWERED).booleanValue() && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
            BlockState $$5 = (BlockState)$$0.setValue(POWERED, false);
            $$1.setBlock($$2, $$5, 18);
            this.updateNeighborsInFront($$1, $$2, $$5);
        }
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if (!$$1.isClientSide && $$0.getValue(POWERED).booleanValue() && $$1.getBlockTicks().hasScheduledTick($$2, this)) {
            this.updateNeighborsInFront($$1, $$2, (BlockState)$$0.setValue(POWERED, false));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite().getOpposite());
    }
}