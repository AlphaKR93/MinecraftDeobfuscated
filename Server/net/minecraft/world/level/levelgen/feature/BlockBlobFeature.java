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
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature
extends Feature<BlockStateConfiguration> {
    public BlockBlobFeature(Codec<BlockStateConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> $$0) {
        BlockState $$5;
        Vec3i $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        BlockStateConfiguration $$4 = $$0.config();
        while ($$1.getY() > $$2.getMinBuildHeight() + 3 && ($$2.isEmptyBlock((BlockPos)((BlockPos)$$1).below()) || !BlockBlobFeature.isDirt($$5 = $$2.getBlockState((BlockPos)((BlockPos)$$1).below())) && !BlockBlobFeature.isStone($$5))) {
            $$1 = ((BlockPos)$$1).below();
        }
        if ($$1.getY() <= $$2.getMinBuildHeight() + 3) {
            return false;
        }
        for (int $$6 = 0; $$6 < 3; ++$$6) {
            int $$7 = $$3.nextInt(2);
            int $$8 = $$3.nextInt(2);
            int $$9 = $$3.nextInt(2);
            float $$10 = (float)($$7 + $$8 + $$9) * 0.333f + 0.5f;
            for (BlockPos $$11 : BlockPos.betweenClosed(((BlockPos)$$1).offset(-$$7, -$$8, -$$9), ((BlockPos)$$1).offset($$7, $$8, $$9))) {
                if (!($$11.distSqr($$1) <= (double)($$10 * $$10))) continue;
                $$2.setBlock($$11, $$4.state, 4);
            }
            $$1 = ((BlockPos)$$1).offset(-1 + $$3.nextInt(2), -$$3.nextInt(2), -1 + $$3.nextInt(2));
        }
        return true;
    }
}