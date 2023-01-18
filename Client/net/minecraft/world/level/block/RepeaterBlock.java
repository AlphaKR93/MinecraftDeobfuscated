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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock
extends DiodeBlock {
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

    protected RepeaterBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(DELAY, 1)).setValue(LOCKED, false)).setValue(POWERED, false));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (!$$3.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        $$1.setBlock($$2, (BlockState)$$0.cycle(DELAY), 3);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    @Override
    protected int getDelay(BlockState $$0) {
        return $$0.getValue(DELAY) * 2;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = super.getStateForPlacement($$0);
        return (BlockState)$$1.setValue(LOCKED, this.isLocked($$0.getLevel(), $$0.getClickedPos(), $$1));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!$$3.isClientSide() && $$1.getAxis() != $$0.getValue(FACING).getAxis()) {
            return (BlockState)$$0.setValue(LOCKED, this.isLocked($$3, $$4, $$0));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean isLocked(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return this.getAlternateSignal($$0, $$1, $$2) > 0;
    }

    @Override
    protected boolean isAlternateInput(BlockState $$0) {
        return RepeaterBlock.isDiode($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return;
        }
        Direction $$4 = $$0.getValue(FACING);
        double $$5 = (double)$$2.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2;
        double $$6 = (double)$$2.getY() + 0.4 + ($$3.nextDouble() - 0.5) * 0.2;
        double $$7 = (double)$$2.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2;
        float $$8 = -5.0f;
        if ($$3.nextBoolean()) {
            $$8 = $$0.getValue(DELAY) * 2 - 1;
        }
        double $$9 = ($$8 /= 16.0f) * (float)$$4.getStepX();
        double $$10 = $$8 * (float)$$4.getStepZ();
        $$1.addParticle(DustParticleOptions.REDSTONE, $$5 + $$9, $$6, $$7 + $$10, 0.0, 0.0, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, DELAY, LOCKED, POWERED);
    }
}