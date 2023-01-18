/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock
extends HorizontalDirectionalBlock
implements SimpleWaterloggedBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final int AABB_THICKNESS = 3;
    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    private final SoundEvent closeSound;
    private final SoundEvent openSound;

    protected TrapDoorBlock(BlockBehaviour.Properties $$0, SoundEvent $$1, SoundEvent $$2) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HALF, Half.BOTTOM)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
        this.closeSound = $$1;
        this.openSound = $$2;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if (!$$0.getValue(OPEN).booleanValue()) {
            return $$0.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        }
        switch ($$0.getValue(FACING)) {
            default: {
                return NORTH_OPEN_AABB;
            }
            case SOUTH: {
                return SOUTH_OPEN_AABB;
            }
            case WEST: {
                return WEST_OPEN_AABB;
            }
            case EAST: 
        }
        return EAST_OPEN_AABB;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        switch ($$3) {
            case LAND: {
                return $$0.getValue(OPEN);
            }
            case WATER: {
                return $$0.getValue(WATERLOGGED);
            }
            case AIR: {
                return $$0.getValue(OPEN);
            }
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (this.material == Material.METAL) {
            return InteractionResult.PASS;
        }
        $$0 = (BlockState)$$0.cycle(OPEN);
        $$1.setBlock($$2, $$0, 2);
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$1.scheduleTick($$2, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        this.playSound($$3, $$1, $$2, $$0.getValue(OPEN));
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    protected void playSound(@Nullable Player $$0, Level $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound($$0, $$2, $$3 ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
        $$1.gameEvent($$0, $$3 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        boolean $$6 = $$1.hasNeighborSignal($$2);
        if ($$6 != $$0.getValue(POWERED)) {
            if ($$0.getValue(OPEN) != $$6) {
                $$0 = (BlockState)$$0.setValue(OPEN, $$6);
                this.playSound(null, $$1, $$2, $$6);
            }
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$6), 2);
            if ($$0.getValue(WATERLOGGED).booleanValue()) {
                $$1.scheduleTick($$2, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Direction $$3 = $$0.getClickedFace();
        $$1 = $$0.replacingClickedOnBlock() || !$$3.getAxis().isHorizontal() ? (BlockState)((BlockState)$$1.setValue(FACING, $$0.getHorizontalDirection().getOpposite())).setValue(HALF, $$3 == Direction.UP ? Half.BOTTOM : Half.TOP) : (BlockState)((BlockState)$$1.setValue(FACING, $$3)).setValue(HALF, $$0.getClickLocation().y - (double)$$0.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        if ($$0.getLevel().hasNeighborSignal($$0.getClickedPos())) {
            $$1 = (BlockState)((BlockState)$$1.setValue(OPEN, true)).setValue(POWERED, true);
        }
        return (BlockState)$$1.setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }
}