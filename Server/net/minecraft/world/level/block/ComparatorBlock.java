/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class ComparatorBlock
extends DiodeBlock
implements EntityBlock {
    public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.MODE_COMPARATOR;

    public ComparatorBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(MODE, ComparatorMode.COMPARE));
    }

    @Override
    protected int getDelay(BlockState $$0) {
        return 2;
    }

    @Override
    protected int getOutputSignal(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        BlockEntity $$3 = $$0.getBlockEntity($$1);
        if ($$3 instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)$$3).getOutputSignal();
        }
        return 0;
    }

    private int calculateOutputSignal(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = this.getInputSignal($$0, $$1, $$2);
        if ($$3 == 0) {
            return 0;
        }
        int $$4 = this.getAlternateSignal($$0, $$1, $$2);
        if ($$4 > $$3) {
            return 0;
        }
        if ($$2.getValue(MODE) == ComparatorMode.SUBTRACT) {
            return $$3 - $$4;
        }
        return $$3;
    }

    @Override
    protected boolean shouldTurnOn(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = this.getInputSignal($$0, $$1, $$2);
        if ($$3 == 0) {
            return false;
        }
        int $$4 = this.getAlternateSignal($$0, $$1, $$2);
        if ($$3 > $$4) {
            return true;
        }
        return $$3 == $$4 && $$2.getValue(MODE) == ComparatorMode.COMPARE;
    }

    @Override
    protected int getInputSignal(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = super.getInputSignal($$0, $$1, $$2);
        Direction $$4 = $$2.getValue(FACING);
        Vec3i $$5 = $$1.relative($$4);
        BlockState $$6 = $$0.getBlockState((BlockPos)$$5);
        if ($$6.hasAnalogOutputSignal()) {
            $$3 = $$6.getAnalogOutputSignal($$0, (BlockPos)$$5);
        } else if ($$3 < 15 && $$6.isRedstoneConductor($$0, (BlockPos)$$5)) {
            $$5 = ((BlockPos)$$5).relative($$4);
            $$6 = $$0.getBlockState((BlockPos)$$5);
            ItemFrame $$7 = this.getItemFrame($$0, $$4, (BlockPos)$$5);
            int $$8 = Math.max((int)($$7 == null ? Integer.MIN_VALUE : $$7.getAnalogOutput()), (int)($$6.hasAnalogOutputSignal() ? $$6.getAnalogOutputSignal($$0, (BlockPos)$$5) : Integer.MIN_VALUE));
            if ($$8 != Integer.MIN_VALUE) {
                $$3 = $$8;
            }
        }
        return $$3;
    }

    @Nullable
    private ItemFrame getItemFrame(Level $$0, Direction $$12, BlockPos $$2) {
        List $$3 = $$0.getEntitiesOfClass(ItemFrame.class, new AABB($$2.getX(), $$2.getY(), $$2.getZ(), $$2.getX() + 1, $$2.getY() + 1, $$2.getZ() + 1), $$1 -> $$1 != null && $$1.getDirection() == $$12);
        if ($$3.size() == 1) {
            return (ItemFrame)$$3.get(0);
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (!$$3.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        float $$6 = ($$0 = (BlockState)$$0.cycle(MODE)).getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        $$1.playSound($$3, $$2, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3f, $$6);
        $$1.setBlock($$2, $$0, 2);
        this.refreshOutputState($$1, $$2, $$0);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    @Override
    protected void checkTickOnNeighbor(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$5;
        if ($$0.getBlockTicks().willTickThisTick($$1, this)) {
            return;
        }
        int $$3 = this.calculateOutputSignal($$0, $$1, $$2);
        BlockEntity $$4 = $$0.getBlockEntity($$1);
        int n = $$5 = $$4 instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)$$4).getOutputSignal() : 0;
        if ($$3 != $$5 || $$2.getValue(POWERED).booleanValue() != this.shouldTurnOn($$0, $$1, $$2)) {
            TickPriority $$6 = this.shouldPrioritize($$0, $$1, $$2) ? TickPriority.HIGH : TickPriority.NORMAL;
            $$0.scheduleTick($$1, this, 2, $$6);
        }
    }

    private void refreshOutputState(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = this.calculateOutputSignal($$0, $$1, $$2);
        BlockEntity $$4 = $$0.getBlockEntity($$1);
        int $$5 = 0;
        if ($$4 instanceof ComparatorBlockEntity) {
            ComparatorBlockEntity $$6 = (ComparatorBlockEntity)$$4;
            $$5 = $$6.getOutputSignal();
            $$6.setOutputSignal($$3);
        }
        if ($$5 != $$3 || $$2.getValue(MODE) == ComparatorMode.COMPARE) {
            boolean $$7 = this.shouldTurnOn($$0, $$1, $$2);
            boolean $$8 = $$2.getValue(POWERED);
            if ($$8 && !$$7) {
                $$0.setBlock($$1, (BlockState)$$2.setValue(POWERED, false), 2);
            } else if (!$$8 && $$7) {
                $$0.setBlock($$1, (BlockState)$$2.setValue(POWERED, true), 2);
            }
            this.updateNeighborsInFront($$0, $$1, $$2);
        }
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.refreshOutputState($$1, $$2, $$0);
    }

    @Override
    public boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        super.triggerEvent($$0, $$1, $$2, $$3, $$4);
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        return $$5 != null && $$5.triggerEvent($$3, $$4);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ComparatorBlockEntity($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, MODE, POWERED);
    }
}