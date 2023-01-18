/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class ScatteredOreFeature
extends Feature<OreConfiguration> {
    private static final int MAX_DIST_FROM_ORIGIN = 7;

    ScatteredOreFeature(Codec<OreConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        RandomSource $$2 = $$0.random();
        OreConfiguration $$3 = $$0.config();
        BlockPos $$4 = $$0.origin();
        int $$5 = $$2.nextInt($$3.size + 1);
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        block0: for (int $$7 = 0; $$7 < $$5; ++$$7) {
            this.offsetTargetPos($$6, $$2, $$4, Math.min((int)$$7, (int)7));
            BlockState $$8 = $$1.getBlockState($$6);
            for (OreConfiguration.TargetBlockState $$9 : $$3.targetStates) {
                if (!OreFeature.canPlaceOre($$8, (Function<BlockPos, BlockState>)((Function)$$1::getBlockState), $$2, $$3, $$9, $$6)) continue;
                $$1.setBlock($$6, $$9.state, 2);
                continue block0;
            }
        }
        return true;
    }

    private void offsetTargetPos(BlockPos.MutableBlockPos $$0, RandomSource $$1, BlockPos $$2, int $$3) {
        int $$4 = this.getRandomPlacementInOneAxisRelativeToOrigin($$1, $$3);
        int $$5 = this.getRandomPlacementInOneAxisRelativeToOrigin($$1, $$3);
        int $$6 = this.getRandomPlacementInOneAxisRelativeToOrigin($$1, $$3);
        $$0.setWithOffset($$2, $$4, $$5, $$6);
    }

    private int getRandomPlacementInOneAxisRelativeToOrigin(RandomSource $$0, int $$1) {
        return Math.round((float)(($$0.nextFloat() - $$0.nextFloat()) * (float)$$1));
    }
}