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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantHeadBlock
extends GrowingPlantBlock
implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    public static final int MAX_AGE = 25;
    private final double growPerTickProbability;

    protected GrowingPlantHeadBlock(BlockBehaviour.Properties $$0, Direction $$1, VoxelShape $$2, boolean $$3, double $$4) {
        super($$0, $$1, $$2, $$3);
        this.growPerTickProbability = $$4;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public BlockState getStateForPlacement(LevelAccessor $$0) {
        return (BlockState)this.defaultBlockState().setValue(AGE, $$0.getRandom().nextInt(25));
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(AGE) < 25;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        Vec3i $$4;
        if ($$0.getValue(AGE) < 25 && $$3.nextDouble() < this.growPerTickProbability && this.canGrowInto($$1.getBlockState((BlockPos)($$4 = $$2.relative(this.growthDirection))))) {
            $$1.setBlockAndUpdate((BlockPos)$$4, this.getGrowIntoState($$0, $$1.random));
        }
    }

    protected BlockState getGrowIntoState(BlockState $$0, RandomSource $$1) {
        return (BlockState)$$0.cycle(AGE);
    }

    public BlockState getMaxAgeState(BlockState $$0) {
        return (BlockState)$$0.setValue(AGE, 25);
    }

    public boolean isMaxAge(BlockState $$0) {
        return $$0.getValue(AGE) == 25;
    }

    protected BlockState updateBodyAfterConvertedFromHead(BlockState $$0, BlockState $$1) {
        return $$1;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == this.growthDirection.getOpposite() && !$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
        }
        if ($$1 == this.growthDirection && ($$2.is(this) || $$2.is(this.getBodyBlock()))) {
            return this.updateBodyAfterConvertedFromHead($$0, this.getBodyBlock().defaultBlockState());
        }
        if (this.scheduleFluidTicks) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return this.canGrowInto($$0.getBlockState((BlockPos)$$1.relative(this.growthDirection)));
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        Vec3i $$4 = $$2.relative(this.growthDirection);
        int $$5 = Math.min((int)($$3.getValue(AGE) + 1), (int)25);
        int $$6 = this.getBlocksToGrowWhenBonemealed($$1);
        for (int $$7 = 0; $$7 < $$6 && this.canGrowInto($$0.getBlockState((BlockPos)$$4)); ++$$7) {
            $$0.setBlockAndUpdate((BlockPos)$$4, (BlockState)$$3.setValue(AGE, $$5));
            $$4 = ((BlockPos)$$4).relative(this.growthDirection);
            $$5 = Math.min((int)($$5 + 1), (int)25);
        }
    }

    protected abstract int getBlocksToGrowWhenBonemealed(RandomSource var1);

    protected abstract boolean canGrowInto(BlockState var1);

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return this;
    }
}