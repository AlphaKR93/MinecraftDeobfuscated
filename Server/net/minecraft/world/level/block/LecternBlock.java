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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    public static final VoxelShape SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final VoxelShape SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), SHAPE_COMMON);
    public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), SHAPE_COMMON);
    public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0), Block.box(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0), Block.box(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0), SHAPE_COMMON);
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0), Block.box(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667), Block.box(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333), SHAPE_COMMON);
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    protected LecternBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return SHAPE_COMMON;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        CompoundTag $$5;
        Level $$1 = $$0.getLevel();
        ItemStack $$2 = $$0.getItemInHand();
        Player $$3 = $$0.getPlayer();
        boolean $$4 = false;
        if (!$$1.isClientSide && $$3 != null && $$3.canUseGameMasterBlocks() && ($$5 = BlockItem.getBlockEntityData($$2)) != null && $$5.contains("Book")) {
            $$4 = true;
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite())).setValue(HAS_BOOK, $$4);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_COLLISION;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(FACING)) {
            case NORTH: {
                return SHAPE_NORTH;
            }
            case SOUTH: {
                return SHAPE_SOUTH;
            }
            case EAST: {
                return SHAPE_EAST;
            }
            case WEST: {
                return SHAPE_WEST;
            }
        }
        return SHAPE_COMMON;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, POWERED, HAS_BOOK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new LecternBlockEntity($$0, $$1);
    }

    public static boolean tryPlaceBook(@Nullable Player $$0, Level $$1, BlockPos $$2, BlockState $$3, ItemStack $$4) {
        if (!$$3.getValue(HAS_BOOK).booleanValue()) {
            if (!$$1.isClientSide) {
                LecternBlock.placeBook($$0, $$1, $$2, $$3, $$4);
            }
            return true;
        }
        return false;
    }

    private static void placeBook(@Nullable Player $$0, Level $$1, BlockPos $$2, BlockState $$3, ItemStack $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof LecternBlockEntity) {
            LecternBlockEntity $$6 = (LecternBlockEntity)$$5;
            $$6.setBook($$4.split(1));
            LecternBlock.resetBookState($$1, $$2, $$3, true);
            $$1.playSound(null, $$2, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$1.gameEvent($$0, GameEvent.BLOCK_CHANGE, $$2);
        }
    }

    public static void resetBookState(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        $$0.setBlock($$1, (BlockState)((BlockState)$$2.setValue(POWERED, false)).setValue(HAS_BOOK, $$3), 3);
        LecternBlock.updateBelow($$0, $$1, $$2);
    }

    public static void signalPageChange(Level $$0, BlockPos $$1, BlockState $$2) {
        LecternBlock.changePowered($$0, $$1, $$2, true);
        $$0.scheduleTick($$1, $$2.getBlock(), 2);
        $$0.levelEvent(1043, $$1, 0);
    }

    private static void changePowered(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        $$0.setBlock($$1, (BlockState)$$2.setValue(POWERED, $$3), 3);
        LecternBlock.updateBelow($$0, $$1, $$2);
    }

    private static void updateBelow(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.updateNeighborsAt((BlockPos)$$1.below(), $$2.getBlock());
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        LecternBlock.changePowered($$1, $$2, $$0, false);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(HAS_BOOK).booleanValue()) {
            this.popBook($$0, $$1, $$2);
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            $$1.updateNeighborsAt((BlockPos)$$2.below(), this);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    private void popBook(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof LecternBlockEntity) {
            LecternBlockEntity $$4 = (LecternBlockEntity)$$3;
            Direction $$5 = $$0.getValue(FACING);
            ItemStack $$6 = $$4.getBook().copy();
            float $$7 = 0.25f * (float)$$5.getStepX();
            float $$8 = 0.25f * (float)$$5.getStepZ();
            ItemEntity $$9 = new ItemEntity($$1, (double)$$2.getX() + 0.5 + (double)$$7, $$2.getY() + 1, (double)$$2.getZ() + 0.5 + (double)$$8, $$6);
            $$9.setDefaultPickUpDelay();
            $$1.addFreshEntity($$9);
            $$4.clearContent();
        }
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$3 == Direction.UP && $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3;
        if ($$0.getValue(HAS_BOOK).booleanValue() && ($$3 = $$1.getBlockEntity($$2)) instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)$$3).getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$0.getValue(HAS_BOOK).booleanValue()) {
            if (!$$1.isClientSide) {
                this.openScreen($$1, $$2, $$3);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        ItemStack $$6 = $$3.getItemInHand($$4);
        if ($$6.isEmpty() || $$6.is(ItemTags.LECTERN_BOOKS)) {
            return InteractionResult.PASS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        if (!$$0.getValue(HAS_BOOK).booleanValue()) {
            return null;
        }
        return super.getMenuProvider($$0, $$1, $$2);
    }

    private void openScreen(Level $$0, BlockPos $$1, Player $$2) {
        BlockEntity $$3 = $$0.getBlockEntity($$1);
        if ($$3 instanceof LecternBlockEntity) {
            $$2.openMenu((LecternBlockEntity)$$3);
            $$2.awardStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}