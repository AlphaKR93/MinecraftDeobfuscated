/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class BlockPileFeature
extends Feature<BlockPileConfiguration> {
    public BlockPileFeature(Codec<BlockPileConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockPileConfiguration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        BlockPileConfiguration $$4 = $$0.config();
        if ($$1.getY() < $$2.getMinBuildHeight() + 5) {
            return false;
        }
        int $$5 = 2 + $$3.nextInt(2);
        int $$6 = 2 + $$3.nextInt(2);
        for (BlockPos $$7 : BlockPos.betweenClosed($$1.offset(-$$5, 0, -$$6), $$1.offset($$5, 1, $$6))) {
            int $$9;
            int $$8 = $$1.getX() - $$7.getX();
            if ((float)($$8 * $$8 + ($$9 = $$1.getZ() - $$7.getZ()) * $$9) <= $$3.nextFloat() * 10.0f - $$3.nextFloat() * 6.0f) {
                this.tryPlaceBlock($$2, $$7, $$3, $$4);
                continue;
            }
            if (!((double)$$3.nextFloat() < 0.031)) continue;
            this.tryPlaceBlock($$2, $$7, $$3, $$4);
        }
        return true;
    }

    private boolean mayPlaceOn(LevelAccessor $$0, BlockPos $$1, RandomSource $$2) {
        Vec3i $$3 = $$1.below();
        BlockState $$4 = $$0.getBlockState((BlockPos)$$3);
        if ($$4.is(Blocks.DIRT_PATH)) {
            return $$2.nextBoolean();
        }
        return $$4.isFaceSturdy($$0, (BlockPos)$$3, Direction.UP);
    }

    private void tryPlaceBlock(LevelAccessor $$0, BlockPos $$1, RandomSource $$2, BlockPileConfiguration $$3) {
        if ($$0.isEmptyBlock($$1) && this.mayPlaceOn($$0, $$1, $$2)) {
            $$0.setBlock($$1, $$3.stateProvider.getState($$2, $$1), 4);
        }
    }
}