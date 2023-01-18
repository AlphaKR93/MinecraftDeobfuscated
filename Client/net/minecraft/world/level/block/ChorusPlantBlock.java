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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock
extends PipeBlock {
    protected ChorusPlantBlock(BlockBehaviour.Properties $$0) {
        super(0.3125f, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.getStateForPlacement($$0.getLevel(), $$0.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState((BlockPos)$$1.below());
        BlockState $$3 = $$0.getBlockState((BlockPos)$$1.above());
        BlockState $$4 = $$0.getBlockState((BlockPos)$$1.north());
        BlockState $$5 = $$0.getBlockState((BlockPos)$$1.east());
        BlockState $$6 = $$0.getBlockState((BlockPos)$$1.south());
        BlockState $$7 = $$0.getBlockState((BlockPos)$$1.west());
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, $$2.is(this) || $$2.is(Blocks.CHORUS_FLOWER) || $$2.is(Blocks.END_STONE))).setValue(UP, $$3.is(this) || $$3.is(Blocks.CHORUS_FLOWER))).setValue(NORTH, $$4.is(this) || $$4.is(Blocks.CHORUS_FLOWER))).setValue(EAST, $$5.is(this) || $$5.is(Blocks.CHORUS_FLOWER))).setValue(SOUTH, $$6.is(this) || $$6.is(Blocks.CHORUS_FLOWER))).setValue(WEST, $$7.is(this) || $$7.is(Blocks.CHORUS_FLOWER));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
        }
        boolean $$6 = $$2.is(this) || $$2.is(Blocks.CHORUS_FLOWER) || $$1 == Direction.DOWN && $$2.is(Blocks.END_STONE);
        return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), $$6);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.below());
        boolean $$4 = !$$1.getBlockState((BlockPos)$$2.above()).isAir() && !$$3.isAir();
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            Vec3i $$6 = $$2.relative($$5);
            BlockState $$7 = $$1.getBlockState((BlockPos)$$6);
            if (!$$7.is(this)) continue;
            if ($$4) {
                return false;
            }
            BlockState $$8 = $$1.getBlockState((BlockPos)((BlockPos)$$6).below());
            if (!$$8.is(this) && !$$8.is(Blocks.END_STONE)) continue;
            return true;
        }
        return $$3.is(this) || $$3.is(Blocks.END_STONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}