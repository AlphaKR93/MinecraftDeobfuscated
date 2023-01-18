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
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkSensorBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final int ACTIVE_TICKS = 40;
    public static final int COOLDOWN_TICKS = 1;
    public static final EnumProperty<SculkSensorPhase> PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final int listenerRange;

    public SculkSensorBlock(BlockBehaviour.Properties $$0, int $$1) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PHASE, SculkSensorPhase.INACTIVE)).setValue(POWER, 0)).setValue(WATERLOGGED, false));
        this.listenerRange = $$1;
    }

    public int getListenerRange() {
        return this.listenerRange;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        FluidState $$2 = $$0.getLevel().getFluidState($$1);
        return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (SculkSensorBlock.getPhase($$0) != SculkSensorPhase.ACTIVE) {
            if (SculkSensorBlock.getPhase($$0) == SculkSensorPhase.COOLDOWN) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
            }
            return;
        }
        SculkSensorBlock.deactivate($$1, $$2, $$0);
    }

    @Override
    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
        BlockEntity $$4;
        if (!$$0.isClientSide() && SculkSensorBlock.canActivate($$2) && $$3.getType() != EntityType.WARDEN && ($$4 = $$0.getBlockEntity($$1)) instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity $$5 = (SculkSensorBlockEntity)$$4;
            if ($$0 instanceof ServerLevel) {
                ServerLevel $$6 = (ServerLevel)$$0;
                $$5.getListener().forceGameEvent($$6, GameEvent.STEP, GameEvent.Context.of($$3), $$3.position());
            }
        }
        super.stepOn($$0, $$1, $$2, $$3);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$1.isClientSide() || $$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(POWER) > 0 && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWER, 0), 18);
        }
        $$1.scheduleTick(new BlockPos($$2), $$0.getBlock(), 1);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if (SculkSensorBlock.getPhase($$0) == SculkSensorPhase.ACTIVE) {
            SculkSensorBlock.updateNeighbours($$1, $$2);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static void updateNeighbours(Level $$0, BlockPos $$1) {
        $$0.updateNeighborsAt($$1, Blocks.SCULK_SENSOR);
        $$0.updateNeighborsAt((BlockPos)$$1.relative(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new SculkSensorBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel $$0, T $$1) {
        if ($$1 instanceof SculkSensorBlockEntity) {
            return ((SculkSensorBlockEntity)$$1).getListener();
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$02, BlockState $$12, BlockEntityType<T> $$22) {
        if (!$$02.isClientSide) {
            return SculkSensorBlock.createTickerHelper($$22, BlockEntityType.SCULK_SENSOR, ($$0, $$1, $$2, $$3) -> $$3.getListener().tick($$0));
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWER);
    }

    public static SculkSensorPhase getPhase(BlockState $$0) {
        return $$0.getValue(PHASE);
    }

    public static boolean canActivate(BlockState $$0) {
        return SculkSensorBlock.getPhase($$0) == SculkSensorPhase.INACTIVE;
    }

    public static void deactivate(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, (BlockState)((BlockState)$$2.setValue(PHASE, SculkSensorPhase.COOLDOWN)).setValue(POWER, 0), 3);
        $$0.scheduleTick($$1, $$2.getBlock(), 1);
        if (!$$2.getValue(WATERLOGGED).booleanValue()) {
            $$0.playSound(null, $$1, SoundEvents.SCULK_CLICKING_STOP, SoundSource.BLOCKS, 1.0f, $$0.random.nextFloat() * 0.2f + 0.8f);
        }
        SculkSensorBlock.updateNeighbours($$0, $$1);
    }

    public static void activate(@Nullable Entity $$0, Level $$1, BlockPos $$2, BlockState $$3, int $$4) {
        $$1.setBlock($$2, (BlockState)((BlockState)$$3.setValue(PHASE, SculkSensorPhase.ACTIVE)).setValue(POWER, $$4), 3);
        $$1.scheduleTick($$2, $$3.getBlock(), 40);
        SculkSensorBlock.updateNeighbours($$1, $$2);
        $$1.gameEvent($$0, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, $$2);
        if (!$$3.getValue(WATERLOGGED).booleanValue()) {
            $$1.playSound(null, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0f, $$1.random.nextFloat() * 0.2f + 0.8f);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (SculkSensorBlock.getPhase($$0) != SculkSensorPhase.ACTIVE) {
            return;
        }
        Direction $$4 = Direction.getRandom($$3);
        if ($$4 == Direction.UP || $$4 == Direction.DOWN) {
            return;
        }
        double $$5 = (double)$$2.getX() + 0.5 + ($$4.getStepX() == 0 ? 0.5 - $$3.nextDouble() : (double)$$4.getStepX() * 0.6);
        double $$6 = (double)$$2.getY() + 0.25;
        double $$7 = (double)$$2.getZ() + 0.5 + ($$4.getStepZ() == 0 ? 0.5 - $$3.nextDouble() : (double)$$4.getStepZ() * 0.6);
        double $$8 = (double)$$3.nextFloat() * 0.04;
        $$1.addParticle(DustColorTransitionOptions.SCULK_TO_REDSTONE, $$5, $$6, $$7, 0.0, $$8, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(PHASE, POWER, WATERLOGGED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity $$4 = (SculkSensorBlockEntity)$$3;
            return SculkSensorBlock.getPhase($$0) == SculkSensorPhase.ACTIVE ? $$4.getLastVibrationFrequency() : 0;
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$4) {
            this.tryDropExperience($$1, $$2, $$3, ConstantInt.of(5));
        }
    }
}