/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock
extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
    protected static final VoxelShape AABB = Block.box(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
    protected static final VoxelShape NOT_ATTACHED_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final int RECHECK_PERIOD = 10;
    private final TripWireHookBlock hook;

    public TripWireBlock(TripWireHookBlock $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(ATTACHED, false)).setValue(DISARMED, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
        this.hook = $$0;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return $$0.getValue(ATTACHED) != false ? AABB : NOT_ATTACHED_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, this.shouldConnectTo($$1.getBlockState((BlockPos)$$2.north()), Direction.NORTH))).setValue(EAST, this.shouldConnectTo($$1.getBlockState((BlockPos)$$2.east()), Direction.EAST))).setValue(SOUTH, this.shouldConnectTo($$1.getBlockState((BlockPos)$$2.south()), Direction.SOUTH))).setValue(WEST, this.shouldConnectTo($$1.getBlockState((BlockPos)$$2.west()), Direction.WEST));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1.getAxis().isHorizontal()) {
            return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), this.shouldConnectTo($$2, $$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.updateSource($$1, $$2, $$0);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        this.updateSource($$1, $$2, (BlockState)$$0.setValue(POWERED, true));
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide && !$$3.getMainHandItem().isEmpty() && $$3.getMainHandItem().is(Items.SHEARS)) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(DISARMED, true), 4);
            $$0.gameEvent($$3, GameEvent.SHEAR, $$1);
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    private void updateSource(Level $$0, BlockPos $$1, BlockState $$2) {
        block0: for (Direction $$3 : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int $$4 = 1; $$4 < 42; ++$$4) {
                BlockPos $$5 = $$1.relative($$3, $$4);
                BlockState $$6 = $$0.getBlockState($$5);
                if ($$6.is(this.hook)) {
                    if ($$6.getValue(TripWireHookBlock.FACING) != $$3.getOpposite()) continue block0;
                    this.hook.calculateState($$0, $$5, $$6, false, true, $$4, $$2);
                    continue block0;
                }
                if (!$$6.is(this)) continue block0;
            }
        }
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.getBlockState($$2).getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2);
    }

    private void checkPressed(Level $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        boolean $$3 = $$2.getValue(POWERED);
        boolean $$4 = false;
        List $$5 = $$0.getEntities(null, $$2.getShape($$0, $$1).bounds().move($$1));
        if (!$$5.isEmpty()) {
            for (Entity $$6 : $$5) {
                if ($$6.isIgnoringBlockTriggers()) continue;
                $$4 = true;
                break;
            }
        }
        if ($$4 != $$3) {
            $$2 = (BlockState)$$2.setValue(POWERED, $$4);
            $$0.setBlock($$1, $$2, 3);
            this.updateSource($$0, $$1, $$2);
        }
        if ($$4) {
            $$0.scheduleTick(new BlockPos($$1), this, 10);
        }
    }

    public boolean shouldConnectTo(BlockState $$0, Direction $$1) {
        if ($$0.is(this.hook)) {
            return $$0.getValue(TripWireHookBlock.FACING) == $$1.getOpposite();
        }
        return $$0.is(this);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(EAST, $$0.getValue(WEST))).setValue(SOUTH, $$0.getValue(NORTH))).setValue(WEST, $$0.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(EAST))).setValue(EAST, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(WEST))).setValue(EAST, $$0.getValue(NORTH))).setValue(SOUTH, $$0.getValue(EAST))).setValue(WEST, $$0.getValue(SOUTH));
            }
        }
        return $$0;
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(EAST));
            }
        }
        return super.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}