/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 */
package net.minecraft.world.level.block;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock
extends Block
implements SimpleWaterloggedBlock {
    private static final int TICK_DELAY = 1;
    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_BOTTOM;
    private static final VoxelShape BELOW_BLOCK;
    public static final int STABILITY_MAX_DISTANCE = 7;
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;

    protected ScaffoldingBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(WATERLOGGED, false)).setValue(BOTTOM, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if (!$$3.isHoldingItem($$0.getBlock().asItem())) {
            return $$0.getValue(BOTTOM) != false ? UNSTABLE_SHAPE : STABLE_SHAPE;
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getInteractionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.block();
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return $$1.getItemInHand().is(this.asItem());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        int $$3 = ScaffoldingBlock.getDistance($$2, $$1);
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$2.getFluidState($$1).getType() == Fluids.WATER)).setValue(DISTANCE, $$3)).setValue(BOTTOM, this.isBottom($$2, $$1, $$3));
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if (!$$1.isClientSide) {
            $$1.scheduleTick($$2, this, 1);
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if (!$$3.isClientSide()) {
            $$3.scheduleTick($$4, this, 1);
        }
        return $$0;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = ScaffoldingBlock.getDistance($$1, $$2);
        BlockState $$5 = (BlockState)((BlockState)$$0.setValue(DISTANCE, $$4)).setValue(BOTTOM, this.isBottom($$1, $$2, $$4));
        if ($$5.getValue(DISTANCE) == 7) {
            if ($$0.getValue(DISTANCE) == 7) {
                FallingBlockEntity.fall($$1, $$2, $$5);
            } else {
                $$1.destroyBlock($$2, true);
            }
        } else if ($$0 != $$5) {
            $$1.setBlock($$2, $$5, 3);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return ScaffoldingBlock.getDistance($$1, $$2) < 7;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if (!$$3.isAbove(Shapes.block(), $$2, true) || $$3.isDescending()) {
            if ($$0.getValue(DISTANCE) != 0 && $$0.getValue(BOTTOM).booleanValue() && $$3.isAbove(BELOW_BLOCK, $$2, true)) {
                return UNSTABLE_SHAPE_BOTTOM;
            }
            return Shapes.empty();
        }
        return STABLE_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    private boolean isBottom(BlockGetter $$0, BlockPos $$1, int $$2) {
        return $$2 > 0 && !$$0.getBlockState((BlockPos)$$1.below()).is(this);
    }

    public static int getDistance(BlockGetter $$0, BlockPos $$1) {
        Direction $$5;
        BlockState $$6;
        BlockPos.MutableBlockPos $$2 = $$1.mutable().move(Direction.DOWN);
        BlockState $$3 = $$0.getBlockState($$2);
        int $$4 = 7;
        if ($$3.is(Blocks.SCAFFOLDING)) {
            $$4 = $$3.getValue(DISTANCE);
        } else if ($$3.isFaceSturdy($$0, $$2, Direction.UP)) {
            return 0;
        }
        Iterator<Direction> iterator = Direction.Plane.HORIZONTAL.iterator();
        while (iterator.hasNext() && (!($$6 = $$0.getBlockState($$2.setWithOffset((Vec3i)$$1, $$5 = (Direction)iterator.next()))).is(Blocks.SCAFFOLDING) || ($$4 = Math.min((int)$$4, (int)($$6.getValue(DISTANCE) + 1))) != 1)) {
        }
        return $$4;
    }

    static {
        UNSTABLE_SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        BELOW_BLOCK = Shapes.block().move(0.0, -1.0, 0.0);
        DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        BOTTOM = BlockStateProperties.BOTTOM;
        VoxelShape $$0 = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape $$1 = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        VoxelShape $$2 = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        VoxelShape $$3 = Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        VoxelShape $$4 = Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        STABLE_SHAPE = Shapes.or($$0, $$1, $$2, $$3, $$4);
        VoxelShape $$5 = Block.box(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        VoxelShape $$6 = Block.box(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        VoxelShape $$7 = Block.box(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        VoxelShape $$8 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        UNSTABLE_SHAPE = Shapes.or(UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, $$6, $$5, $$8, $$7);
    }
}