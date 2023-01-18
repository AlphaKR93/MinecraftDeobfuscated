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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BellAttachType> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    private static final VoxelShape BELL_TOP_SHAPE = Block.box(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
    private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
    private static final VoxelShape BELL_SHAPE = Shapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
    private static final VoxelShape NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
    private static final VoxelShape EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
    private static final VoxelShape TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
    private static final VoxelShape TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
    private static final VoxelShape CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));
    public static final int EVENT_BELL_RING = 1;

    public BellBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR)).setValue(POWERED, false));
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        boolean $$6 = $$1.hasNeighborSignal($$2);
        if ($$6 != $$0.getValue(POWERED)) {
            if ($$6) {
                this.attemptToRing($$1, $$2, null);
            }
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$6), 3);
        }
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        Entity $$4 = $$3.getOwner();
        Player $$5 = $$4 instanceof Player ? (Player)$$4 : null;
        this.onHit($$0, $$1, $$2, $$5, true);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        return this.onHit($$1, $$0, $$5, $$3, true) ? InteractionResult.sidedSuccess($$1.isClientSide) : InteractionResult.PASS;
    }

    public boolean onHit(Level $$0, BlockState $$1, BlockHitResult $$2, @Nullable Player $$3, boolean $$4) {
        boolean $$7;
        Direction $$5 = $$2.getDirection();
        BlockPos $$6 = $$2.getBlockPos();
        boolean bl = $$7 = !$$4 || this.isProperHit($$1, $$5, $$2.getLocation().y - (double)$$6.getY());
        if ($$7) {
            boolean $$8 = this.attemptToRing($$3, $$0, $$6, $$5);
            if ($$8 && $$3 != null) {
                $$3.awardStat(Stats.BELL_RING);
            }
            return true;
        }
        return false;
    }

    private boolean isProperHit(BlockState $$0, Direction $$1, double $$2) {
        if ($$1.getAxis() == Direction.Axis.Y || $$2 > (double)0.8124f) {
            return false;
        }
        Direction $$3 = $$0.getValue(FACING);
        BellAttachType $$4 = $$0.getValue(ATTACHMENT);
        switch ($$4) {
            case FLOOR: {
                return $$3.getAxis() == $$1.getAxis();
            }
            case SINGLE_WALL: 
            case DOUBLE_WALL: {
                return $$3.getAxis() != $$1.getAxis();
            }
            case CEILING: {
                return true;
            }
        }
        return false;
    }

    public boolean attemptToRing(Level $$0, BlockPos $$1, @Nullable Direction $$2) {
        return this.attemptToRing(null, $$0, $$1, $$2);
    }

    public boolean attemptToRing(@Nullable Entity $$0, Level $$1, BlockPos $$2, @Nullable Direction $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if (!$$1.isClientSide && $$4 instanceof BellBlockEntity) {
            if ($$3 == null) {
                $$3 = $$1.getBlockState($$2).getValue(FACING);
            }
            ((BellBlockEntity)$$4).onHit($$3);
            $$1.playSound(null, $$2, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f);
            $$1.gameEvent($$0, GameEvent.BLOCK_CHANGE, $$2);
            return true;
        }
        return false;
    }

    private VoxelShape getVoxelShape(BlockState $$0) {
        Direction $$1 = $$0.getValue(FACING);
        BellAttachType $$2 = $$0.getValue(ATTACHMENT);
        if ($$2 == BellAttachType.FLOOR) {
            if ($$1 == Direction.NORTH || $$1 == Direction.SOUTH) {
                return NORTH_SOUTH_FLOOR_SHAPE;
            }
            return EAST_WEST_FLOOR_SHAPE;
        }
        if ($$2 == BellAttachType.CEILING) {
            return CEILING_SHAPE;
        }
        if ($$2 == BellAttachType.DOUBLE_WALL) {
            if ($$1 == Direction.NORTH || $$1 == Direction.SOUTH) {
                return NORTH_SOUTH_BETWEEN;
            }
            return EAST_WEST_BETWEEN;
        }
        if ($$1 == Direction.NORTH) {
            return TO_NORTH;
        }
        if ($$1 == Direction.SOUTH) {
            return TO_SOUTH;
        }
        if ($$1 == Direction.EAST) {
            return TO_EAST;
        }
        return TO_WEST;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getVoxelShape($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getVoxelShape($$0);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getClickedFace();
        BlockPos $$2 = $$0.getClickedPos();
        Level $$3 = $$0.getLevel();
        Direction.Axis $$4 = $$1.getAxis();
        if ($$4 == Direction.Axis.Y) {
            BlockState $$5 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHMENT, $$1 == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)).setValue(FACING, $$0.getHorizontalDirection());
            if ($$5.canSurvive($$0.getLevel(), $$2)) {
                return $$5;
            }
        } else {
            boolean $$6 = $$4 == Direction.Axis.X && $$3.getBlockState((BlockPos)$$2.west()).isFaceSturdy($$3, (BlockPos)$$2.west(), Direction.EAST) && $$3.getBlockState((BlockPos)$$2.east()).isFaceSturdy($$3, (BlockPos)$$2.east(), Direction.WEST) || $$4 == Direction.Axis.Z && $$3.getBlockState((BlockPos)$$2.north()).isFaceSturdy($$3, (BlockPos)$$2.north(), Direction.SOUTH) && $$3.getBlockState((BlockPos)$$2.south()).isFaceSturdy($$3, (BlockPos)$$2.south(), Direction.NORTH);
            BlockState $$7 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$1.getOpposite())).setValue(ATTACHMENT, $$6 ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
            if ($$7.canSurvive($$0.getLevel(), $$0.getClickedPos())) {
                return $$7;
            }
            boolean $$8 = $$3.getBlockState((BlockPos)$$2.below()).isFaceSturdy($$3, (BlockPos)$$2.below(), Direction.UP);
            if (($$7 = (BlockState)$$7.setValue(ATTACHMENT, $$8 ? BellAttachType.FLOOR : BellAttachType.CEILING)).canSurvive($$0.getLevel(), $$0.getClickedPos())) {
                return $$7;
            }
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        BellAttachType $$6 = $$0.getValue(ATTACHMENT);
        Direction $$7 = BellBlock.getConnectedDirection($$0).getOpposite();
        if ($$7 == $$1 && !$$0.canSurvive($$3, $$4) && $$6 != BellAttachType.DOUBLE_WALL) {
            return Blocks.AIR.defaultBlockState();
        }
        if ($$1.getAxis() == $$0.getValue(FACING).getAxis()) {
            if ($$6 == BellAttachType.DOUBLE_WALL && !$$2.isFaceSturdy($$3, $$5, $$1)) {
                return (BlockState)((BlockState)$$0.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL)).setValue(FACING, $$1.getOpposite());
            }
            if ($$6 == BellAttachType.SINGLE_WALL && $$7.getOpposite() == $$1 && $$2.isFaceSturdy($$3, $$5, $$0.getValue(FACING))) {
                return (BlockState)$$0.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction $$3 = BellBlock.getConnectedDirection($$0).getOpposite();
        if ($$3 == Direction.UP) {
            return Block.canSupportCenter($$1, (BlockPos)$$2.above(), Direction.DOWN);
        }
        return FaceAttachedHorizontalDirectionalBlock.canAttach($$1, $$2, $$3);
    }

    private static Direction getConnectedDirection(BlockState $$0) {
        switch ($$0.getValue(ATTACHMENT)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return $$0.getValue(FACING).getOpposite();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, ATTACHMENT, POWERED);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BellBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return BellBlock.createTickerHelper($$2, BlockEntityType.BELL, $$0.isClientSide ? BellBlockEntity::clientTick : BellBlockEntity::serverTick);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}