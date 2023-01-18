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
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;

public abstract class SpreadingSnowyDirtBlock
extends SnowyDirtBlock {
    protected SpreadingSnowyDirtBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    private static boolean canBeGrass(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.above();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        if ($$4.is(Blocks.SNOW) && $$4.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        }
        if ($$4.getFluidState().getAmount() == 8) {
            return false;
        }
        int $$5 = LayerLightEngine.getLightBlockInto($$1, $$0, $$2, $$4, (BlockPos)$$3, Direction.UP, $$4.getLightBlock($$1, (BlockPos)$$3));
        return $$5 < $$1.getMaxLightLevel();
    }

    private static boolean canPropagate(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.above();
        return SpreadingSnowyDirtBlock.canBeGrass($$0, $$1, $$2) && !$$1.getFluidState((BlockPos)$$3).is(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!SpreadingSnowyDirtBlock.canBeGrass($$0, $$1, $$2)) {
            $$1.setBlockAndUpdate($$2, Blocks.DIRT.defaultBlockState());
            return;
        }
        if ($$1.getMaxLocalRawBrightness((BlockPos)$$2.above()) >= 9) {
            BlockState $$4 = this.defaultBlockState();
            for (int $$5 = 0; $$5 < 4; ++$$5) {
                BlockPos $$6 = $$2.offset($$3.nextInt(3) - 1, $$3.nextInt(5) - 3, $$3.nextInt(3) - 1);
                if (!$$1.getBlockState($$6).is(Blocks.DIRT) || !SpreadingSnowyDirtBlock.canPropagate($$4, $$1, $$6)) continue;
                $$1.setBlockAndUpdate($$6, (BlockState)$$4.setValue(SNOWY, $$1.getBlockState((BlockPos)$$6.above()).is(Blocks.SNOW)));
            }
        }
    }
}