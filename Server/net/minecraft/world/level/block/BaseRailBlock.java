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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock
extends Block
implements SimpleWaterloggedBlock {
    protected static final VoxelShape FLAT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final boolean isStraight;

    public static boolean isRail(Level $$0, BlockPos $$1) {
        return BaseRailBlock.isRail($$0.getBlockState($$1));
    }

    public static boolean isRail(BlockState $$0) {
        return $$0.is(BlockTags.RAILS) && $$0.getBlock() instanceof BaseRailBlock;
    }

    protected BaseRailBlock(boolean $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.isStraight = $$0;
    }

    public boolean isStraight() {
        return this.isStraight;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        RailShape $$4;
        RailShape railShape = $$4 = $$0.is(this) ? $$0.getValue(this.getShapeProperty()) : null;
        if ($$4 != null && $$4.isAscending()) {
            return HALF_BLOCK_AABB;
        }
        return FLAT_AABB;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$2.below());
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.updateState($$0, $$1, $$2, $$4);
    }

    protected BlockState updateState(BlockState $$0, Level $$1, BlockPos $$2, boolean $$3) {
        $$0 = this.updateDir($$1, $$2, $$0, true);
        if (this.isStraight) {
            $$1.neighborChanged($$0, $$2, this, $$2, $$3);
        }
        return $$0;
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$1.isClientSide || !$$1.getBlockState($$2).is(this)) {
            return;
        }
        RailShape $$6 = $$0.getValue(this.getShapeProperty());
        if (BaseRailBlock.shouldBeRemoved($$2, $$1, $$6)) {
            BaseRailBlock.dropResources($$0, $$1, $$2);
            $$1.removeBlock($$2, $$5);
        } else {
            this.updateState($$0, $$1, $$2, $$3);
        }
    }

    private static boolean shouldBeRemoved(BlockPos $$0, Level $$1, RailShape $$2) {
        if (!BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$0.below())) {
            return true;
        }
        switch ($$2) {
            case ASCENDING_EAST: {
                return !BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$0.east());
            }
            case ASCENDING_WEST: {
                return !BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$0.west());
            }
            case ASCENDING_NORTH: {
                return !BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$0.north());
            }
            case ASCENDING_SOUTH: {
                return !BaseRailBlock.canSupportRigidBlock($$1, (BlockPos)$$0.south());
            }
        }
        return false;
    }

    protected void updateState(BlockState $$0, Level $$1, BlockPos $$2, Block $$3) {
    }

    protected BlockState updateDir(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        if ($$0.isClientSide) {
            return $$2;
        }
        RailShape $$4 = $$2.getValue(this.getShapeProperty());
        return new RailState($$0, $$1, $$2).place($$0.hasNeighborSignal($$1), $$3, $$4).getState();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.NORMAL;
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4) {
            return;
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
        if ($$0.getValue(this.getShapeProperty()).isAscending()) {
            $$1.updateNeighborsAt((BlockPos)$$2.above(), this);
        }
        if (this.isStraight) {
            $$1.updateNeighborsAt($$2, this);
            $$1.updateNeighborsAt((BlockPos)$$2.below(), this);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$2 = $$1.getType() == Fluids.WATER;
        BlockState $$3 = super.defaultBlockState();
        Direction $$4 = $$0.getHorizontalDirection();
        boolean $$5 = $$4 == Direction.EAST || $$4 == Direction.WEST;
        return (BlockState)((BlockState)$$3.setValue(this.getShapeProperty(), $$5 ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, $$2);
    }

    public abstract Property<RailShape> getShapeProperty();

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }
}