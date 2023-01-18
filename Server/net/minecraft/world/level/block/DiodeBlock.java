/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

public abstract class DiodeBlock
extends HorizontalDirectionalBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected DiodeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return DiodeBlock.canSupportRigidBlock($$1, (BlockPos)$$2.below());
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.isLocked($$1, $$2, $$0)) {
            return;
        }
        boolean $$4 = $$0.getValue(POWERED);
        boolean $$5 = this.shouldTurnOn($$1, $$2, $$0);
        if ($$4 && !$$5) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, false), 2);
        } else if (!$$4) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, true), 2);
            if (!$$5) {
                $$1.scheduleTick($$2, this, this.getDelay($$0), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getSignal($$1, $$2, $$3);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return 0;
        }
        if ($$0.getValue(FACING) == $$3) {
            return this.getOutputSignal($$1, $$2, $$0);
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$0.canSurvive($$1, $$2)) {
            this.checkTickOnNeighbor($$1, $$2, $$0);
            return;
        }
        BlockEntity $$6 = $$0.hasBlockEntity() ? $$1.getBlockEntity($$2) : null;
        DiodeBlock.dropResources($$0, $$1, $$2, $$6);
        $$1.removeBlock($$2, false);
        for (Direction $$7 : Direction.values()) {
            $$1.updateNeighborsAt((BlockPos)$$2.relative($$7), this);
        }
    }

    protected void checkTickOnNeighbor(Level $$0, BlockPos $$1, BlockState $$2) {
        boolean $$4;
        if (this.isLocked($$0, $$1, $$2)) {
            return;
        }
        boolean $$3 = $$2.getValue(POWERED);
        if ($$3 != ($$4 = this.shouldTurnOn($$0, $$1, $$2)) && !$$0.getBlockTicks().willTickThisTick($$1, this)) {
            TickPriority $$5 = TickPriority.HIGH;
            if (this.shouldPrioritize($$0, $$1, $$2)) {
                $$5 = TickPriority.EXTREMELY_HIGH;
            } else if ($$3) {
                $$5 = TickPriority.VERY_HIGH;
            }
            $$0.scheduleTick($$1, this, this.getDelay($$2), $$5);
        }
    }

    public boolean isLocked(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return false;
    }

    protected boolean shouldTurnOn(Level $$0, BlockPos $$1, BlockState $$2) {
        return this.getInputSignal($$0, $$1, $$2) > 0;
    }

    protected int getInputSignal(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING);
        Vec3i $$4 = $$1.relative($$3);
        int $$5 = $$0.getSignal((BlockPos)$$4, $$3);
        if ($$5 >= 15) {
            return $$5;
        }
        BlockState $$6 = $$0.getBlockState((BlockPos)$$4);
        return Math.max((int)$$5, (int)($$6.is(Blocks.REDSTONE_WIRE) ? $$6.getValue(RedStoneWireBlock.POWER) : 0));
    }

    protected int getAlternateSignal(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING);
        Direction $$4 = $$3.getClockWise();
        Direction $$5 = $$3.getCounterClockWise();
        return Math.max((int)this.getAlternateSignalAt($$0, (BlockPos)$$1.relative($$4), $$4), (int)this.getAlternateSignalAt($$0, (BlockPos)$$1.relative($$5), $$5));
    }

    protected int getAlternateSignalAt(LevelReader $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        if (this.isAlternateInput($$3)) {
            if ($$3.is(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            if ($$3.is(Blocks.REDSTONE_WIRE)) {
                return $$3.getValue(RedStoneWireBlock.POWER);
            }
            return $$0.getDirectSignal($$1, $$2);
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        if (this.shouldTurnOn($$0, $$1, $$2)) {
            $$0.scheduleTick($$1, this, 1);
        }
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        this.updateNeighborsInFront($$1, $$2, $$0);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
        this.updateNeighborsInFront($$1, $$2, $$0);
    }

    protected void updateNeighborsInFront(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING);
        Vec3i $$4 = $$1.relative($$3.getOpposite());
        $$0.neighborChanged((BlockPos)$$4, this, $$1);
        $$0.updateNeighborsAtExceptFromFacing((BlockPos)$$4, this, $$3);
    }

    protected boolean isAlternateInput(BlockState $$0) {
        return $$0.isSignalSource();
    }

    protected int getOutputSignal(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return 15;
    }

    public static boolean isDiode(BlockState $$0) {
        return $$0.getBlock() instanceof DiodeBlock;
    }

    public boolean shouldPrioritize(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING).getOpposite();
        BlockState $$4 = $$0.getBlockState((BlockPos)$$1.relative($$3));
        return DiodeBlock.isDiode($$4) && $$4.getValue(FACING) != $$3;
    }

    protected abstract int getDelay(BlockState var1);
}