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
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final float AABB_DOOR_THICKNESS = 3.0f;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    private final SoundEvent closeSound;
    private final SoundEvent openSound;

    protected DoorBlock(BlockBehaviour.Properties $$0, SoundEvent $$1, SoundEvent $$2) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HINGE, DoorHingeSide.LEFT)).setValue(POWERED, false)).setValue(HALF, DoubleBlockHalf.LOWER));
        this.closeSound = $$1;
        this.openSound = $$2;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction $$4 = $$0.getValue(FACING);
        boolean $$5 = $$0.getValue(OPEN) == false;
        boolean $$6 = $$0.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch ($$4) {
            default: {
                return $$5 ? EAST_AABB : ($$6 ? NORTH_AABB : SOUTH_AABB);
            }
            case SOUTH: {
                return $$5 ? SOUTH_AABB : ($$6 ? EAST_AABB : WEST_AABB);
            }
            case WEST: {
                return $$5 ? WEST_AABB : ($$6 ? SOUTH_AABB : NORTH_AABB);
            }
            case NORTH: 
        }
        return $$5 ? NORTH_AABB : ($$6 ? WEST_AABB : EAST_AABB);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        DoubleBlockHalf $$6 = $$0.getValue(HALF);
        if ($$1.getAxis() == Direction.Axis.Y && $$6 == DoubleBlockHalf.LOWER == ($$1 == Direction.UP)) {
            if ($$2.is(this) && $$2.getValue(HALF) != $$6) {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(FACING, $$2.getValue(FACING))).setValue(OPEN, $$2.getValue(OPEN))).setValue(HINGE, $$2.getValue(HINGE))).setValue(POWERED, $$2.getValue(POWERED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        if ($$6 == DoubleBlockHalf.LOWER && $$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide && $$3.isCreative()) {
            DoublePlantBlock.preventCreativeDropFromBottomPart($$0, $$1, $$2, $$3);
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        switch ($$3) {
            case LAND: {
                return $$0.getValue(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return $$0.getValue(OPEN);
            }
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        if ($$1.getY() < $$2.getMaxBuildHeight() - 1 && $$2.getBlockState((BlockPos)$$1.above()).canBeReplaced($$0)) {
            boolean $$3 = $$2.hasNeighborSignal($$1) || $$2.hasNeighborSignal((BlockPos)$$1.above());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection())).setValue(HINGE, this.getHinge($$0))).setValue(POWERED, $$3)).setValue(OPEN, $$3)).setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        $$0.setBlock((BlockPos)$$1.above(), (BlockState)$$2.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHingeSide getHinge(BlockPlaceContext $$0) {
        boolean $$17;
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Direction $$3 = $$0.getHorizontalDirection();
        Vec3i $$4 = $$2.above();
        Direction $$5 = $$3.getCounterClockWise();
        Vec3i $$6 = $$2.relative($$5);
        BlockState $$7 = $$1.getBlockState((BlockPos)$$6);
        Vec3i $$8 = ((BlockPos)$$4).relative($$5);
        BlockState $$9 = $$1.getBlockState((BlockPos)$$8);
        Direction $$10 = $$3.getClockWise();
        Vec3i $$11 = $$2.relative($$10);
        BlockState $$12 = $$1.getBlockState((BlockPos)$$11);
        Vec3i $$13 = ((BlockPos)$$4).relative($$10);
        BlockState $$14 = $$1.getBlockState((BlockPos)$$13);
        int $$15 = ($$7.isCollisionShapeFullBlock($$1, (BlockPos)$$6) ? -1 : 0) + ($$9.isCollisionShapeFullBlock($$1, (BlockPos)$$8) ? -1 : 0) + ($$12.isCollisionShapeFullBlock($$1, (BlockPos)$$11) ? 1 : 0) + ($$14.isCollisionShapeFullBlock($$1, (BlockPos)$$13) ? 1 : 0);
        boolean $$16 = $$7.is(this) && $$7.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean bl = $$17 = $$12.is(this) && $$12.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ($$16 && !$$17 || $$15 > 0) {
            return DoorHingeSide.RIGHT;
        }
        if ($$17 && !$$16 || $$15 < 0) {
            return DoorHingeSide.LEFT;
        }
        int $$18 = $$3.getStepX();
        int $$19 = $$3.getStepZ();
        Vec3 $$20 = $$0.getClickLocation();
        double $$21 = $$20.x - (double)$$2.getX();
        double $$22 = $$20.z - (double)$$2.getZ();
        return $$18 < 0 && $$22 < 0.5 || $$18 > 0 && $$22 > 0.5 || $$19 < 0 && $$21 > 0.5 || $$19 > 0 && $$21 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (this.material == Material.METAL) {
            return InteractionResult.PASS;
        }
        $$0 = (BlockState)$$0.cycle(OPEN);
        $$1.setBlock($$2, $$0, 10);
        this.playSound($$3, $$1, $$2, $$0.getValue(OPEN));
        $$1.gameEvent($$3, this.isOpen($$0) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    public boolean isOpen(BlockState $$0) {
        return $$0.getValue(OPEN);
    }

    public void setOpen(@Nullable Entity $$0, Level $$1, BlockState $$2, BlockPos $$3, boolean $$4) {
        if (!$$2.is(this) || $$2.getValue(OPEN) == $$4) {
            return;
        }
        $$1.setBlock($$3, (BlockState)$$2.setValue(OPEN, $$4), 10);
        this.playSound($$0, $$1, $$3, $$4);
        $$1.gameEvent($$0, $$4 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$3);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        boolean $$6;
        boolean bl = $$1.hasNeighborSignal($$2) || $$1.hasNeighborSignal((BlockPos)$$2.relative($$0.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)) ? true : ($$6 = false);
        if (!this.defaultBlockState().is($$3) && $$6 != $$0.getValue(POWERED)) {
            if ($$6 != $$0.getValue(OPEN)) {
                this.playSound(null, $$1, $$2, $$6);
                $$1.gameEvent(null, $$6 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
            }
            $$1.setBlock($$2, (BlockState)((BlockState)$$0.setValue(POWERED, $$6)).setValue(OPEN, $$6), 2);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        if ($$0.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return $$4.isFaceSturdy($$1, (BlockPos)$$3, Direction.UP);
        }
        return $$4.is(this);
    }

    private void playSound(@Nullable Entity $$0, Level $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound($$0, $$2, $$3 ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        if ($$1 == Mirror.NONE) {
            return $$0;
        }
        return (BlockState)$$0.rotate($$1.getRotation($$0.getValue(FACING))).cycle(HINGE);
    }

    @Override
    public long getSeed(BlockState $$0, BlockPos $$1) {
        return Mth.getSeed($$1.getX(), $$1.below($$0.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), $$1.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static boolean isWoodenDoor(Level $$0, BlockPos $$1) {
        return DoorBlock.isWoodenDoor($$0.getBlockState($$1));
    }

    public static boolean isWoodenDoor(BlockState $$0) {
        return $$0.getBlock() instanceof DoorBlock && ($$0.getMaterial() == Material.WOOD || $$0.getMaterial() == Material.NETHER_WOOD);
    }
}