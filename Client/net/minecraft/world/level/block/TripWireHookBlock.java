/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final int WIRE_DIST_MIN = 1;
    protected static final int WIRE_DIST_MAX = 42;
    private static final int RECHECK_PERIOD = 10;
    protected static final int AABB_OFFSET = 3;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape WEST_AABB = Block.box(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripWireHookBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(ATTACHED, false));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(FACING)) {
            default: {
                return EAST_AABB;
            }
            case WEST: {
                return WEST_AABB;
            }
            case SOUTH: {
                return SOUTH_AABB;
            }
            case NORTH: 
        }
        return NORTH_AABB;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction $$3 = $$0.getValue(FACING);
        Vec3i $$4 = $$2.relative($$3.getOpposite());
        BlockState $$5 = $$1.getBlockState((BlockPos)$$4);
        return $$3.getAxis().isHorizontal() && $$5.isFaceSturdy($$1, (BlockPos)$$4, $$3);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1.getOpposite() == $$0.getValue(FACING) && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction[] $$4;
        BlockState $$1 = (BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, false)).setValue(ATTACHED, false);
        Level $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getClickedPos();
        for (Direction $$5 : $$4 = $$0.getNearestLookingDirections()) {
            Direction $$6;
            if (!$$5.getAxis().isHorizontal() || !($$1 = (BlockState)$$1.setValue(FACING, $$6 = $$5.getOpposite())).canSurvive($$2, $$3)) continue;
            return $$1;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        this.calculateState($$0, $$1, $$2, false, false, -1, null);
    }

    public void calculateState(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3, boolean $$4, int $$5, @Nullable BlockState $$6) {
        Direction $$7 = $$2.getValue(FACING);
        boolean $$8 = $$2.getValue(ATTACHED);
        boolean $$9 = $$2.getValue(POWERED);
        boolean $$10 = !$$3;
        boolean $$11 = false;
        int $$12 = 0;
        BlockState[] $$13 = new BlockState[42];
        for (int $$14 = 1; $$14 < 42; ++$$14) {
            BlockPos $$15 = $$1.relative($$7, $$14);
            BlockState $$16 = $$0.getBlockState($$15);
            if ($$16.is(Blocks.TRIPWIRE_HOOK)) {
                if ($$16.getValue(FACING) != $$7.getOpposite()) break;
                $$12 = $$14;
                break;
            }
            if ($$16.is(Blocks.TRIPWIRE) || $$14 == $$5) {
                if ($$14 == $$5) {
                    $$16 = (BlockState)MoreObjects.firstNonNull((Object)$$6, (Object)$$16);
                }
                boolean $$17 = $$16.getValue(TripWireBlock.DISARMED) == false;
                boolean $$18 = $$16.getValue(TripWireBlock.POWERED);
                $$11 |= $$17 && $$18;
                $$13[$$14] = $$16;
                if ($$14 != $$5) continue;
                $$0.scheduleTick($$1, this, 10);
                $$10 &= $$17;
                continue;
            }
            $$13[$$14] = null;
            $$10 = false;
        }
        BlockState $$19 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, $$10)).setValue(POWERED, $$11 &= ($$10 &= $$12 > 1));
        if ($$12 > 0) {
            BlockPos $$20 = $$1.relative($$7, $$12);
            Direction $$21 = $$7.getOpposite();
            $$0.setBlock($$20, (BlockState)$$19.setValue(FACING, $$21), 3);
            this.notifyNeighbors($$0, $$20, $$21);
            this.emitState($$0, $$20, $$10, $$11, $$8, $$9);
        }
        this.emitState($$0, $$1, $$10, $$11, $$8, $$9);
        if (!$$3) {
            $$0.setBlock($$1, (BlockState)$$19.setValue(FACING, $$7), 3);
            if ($$4) {
                this.notifyNeighbors($$0, $$1, $$7);
            }
        }
        if ($$8 != $$10) {
            for (int $$22 = 1; $$22 < $$12; ++$$22) {
                BlockPos $$23 = $$1.relative($$7, $$22);
                BlockState $$24 = $$13[$$22];
                if ($$24 == null) continue;
                $$0.setBlock($$23, (BlockState)$$24.setValue(ATTACHED, $$10), 3);
                if ($$0.getBlockState($$23).isAir()) continue;
            }
        }
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.calculateState($$1, $$2, $$0, false, true, -1, null);
    }

    private void emitState(Level $$0, BlockPos $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5) {
        if ($$3 && !$$5) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4f, 0.6f);
            $$0.gameEvent(null, GameEvent.BLOCK_ACTIVATE, $$1);
        } else if (!$$3 && $$5) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4f, 0.5f);
            $$0.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, $$1);
        } else if ($$2 && !$$4) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4f, 0.7f);
            $$0.gameEvent(null, GameEvent.BLOCK_ATTACH, $$1);
        } else if (!$$2 && $$4) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4f, 1.2f / ($$0.random.nextFloat() * 0.2f + 0.9f));
            $$0.gameEvent(null, GameEvent.BLOCK_DETACH, $$1);
        }
    }

    private void notifyNeighbors(Level $$0, BlockPos $$1, Direction $$2) {
        $$0.updateNeighborsAt($$1, this);
        $$0.updateNeighborsAt((BlockPos)$$1.relative($$2.getOpposite()), this);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        boolean $$5 = $$0.getValue(ATTACHED);
        boolean $$6 = $$0.getValue(POWERED);
        if ($$5 || $$6) {
            this.calculateState($$1, $$2, $$0, true, false, -1, null);
        }
        if ($$6) {
            $$1.updateNeighborsAt($$2, this);
            $$1.updateNeighborsAt((BlockPos)$$2.relative($$0.getValue(FACING).getOpposite()), this);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return 0;
        }
        if ($$0.getValue(FACING) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
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
        $$0.add(FACING, POWERED, ATTACHED);
    }
}