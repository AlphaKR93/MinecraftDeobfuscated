/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ReplaceBlobsFeature
extends Feature<ReplaceSphereConfiguration> {
    public ReplaceBlobsFeature(Codec<ReplaceSphereConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceSphereConfiguration> $$0) {
        BlockPos $$11;
        ReplaceSphereConfiguration $$1 = $$0.config();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        Block $$4 = $$1.targetState.getBlock();
        BlockPos $$5 = ReplaceBlobsFeature.findTarget($$2, $$0.origin().mutable().clamp(Direction.Axis.Y, $$2.getMinBuildHeight() + 1, $$2.getMaxBuildHeight() - 1), $$4);
        if ($$5 == null) {
            return false;
        }
        int $$6 = $$1.radius().sample($$3);
        int $$7 = $$1.radius().sample($$3);
        int $$8 = $$1.radius().sample($$3);
        int $$9 = Math.max((int)$$6, (int)Math.max((int)$$7, (int)$$8));
        boolean $$10 = false;
        Iterator iterator = BlockPos.withinManhattan($$5, $$6, $$7, $$8).iterator();
        while (iterator.hasNext() && ($$11 = (BlockPos)iterator.next()).distManhattan($$5) <= $$9) {
            BlockState $$12 = $$2.getBlockState($$11);
            if (!$$12.is($$4)) continue;
            this.setBlock($$2, $$11, $$1.replaceState);
            $$10 = true;
        }
        return $$10;
    }

    @Nullable
    private static BlockPos findTarget(LevelAccessor $$0, BlockPos.MutableBlockPos $$1, Block $$2) {
        while ($$1.getY() > $$0.getMinBuildHeight() + 1) {
            BlockState $$3 = $$0.getBlockState($$1);
            if ($$3.is($$2)) {
                return $$1;
            }
            $$1.move(Direction.DOWN);
        }
        return null;
    }
}