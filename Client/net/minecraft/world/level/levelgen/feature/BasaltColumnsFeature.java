/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;

public class BasaltColumnsFeature
extends Feature<ColumnFeatureConfiguration> {
    private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of((Object)Blocks.LAVA, (Object)Blocks.BEDROCK, (Object)Blocks.MAGMA_BLOCK, (Object)Blocks.SOUL_SAND, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);
    private static final int CLUSTERED_REACH = 5;
    private static final int CLUSTERED_SIZE = 50;
    private static final int UNCLUSTERED_REACH = 8;
    private static final int UNCLUSTERED_SIZE = 15;

    public BasaltColumnsFeature(Codec<ColumnFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<ColumnFeatureConfiguration> $$0) {
        int $$1 = $$0.chunkGenerator().getSeaLevel();
        BlockPos $$2 = $$0.origin();
        WorldGenLevel $$3 = $$0.level();
        RandomSource $$4 = $$0.random();
        ColumnFeatureConfiguration $$5 = $$0.config();
        if (!BasaltColumnsFeature.canPlaceAt($$3, $$1, $$2.mutable())) {
            return false;
        }
        int $$6 = $$5.height().sample($$4);
        boolean $$7 = $$4.nextFloat() < 0.9f;
        int $$8 = Math.min((int)$$6, (int)($$7 ? 5 : 8));
        int $$9 = $$7 ? 50 : 15;
        boolean $$10 = false;
        for (BlockPos $$11 : BlockPos.randomBetweenClosed($$4, $$9, $$2.getX() - $$8, $$2.getY(), $$2.getZ() - $$8, $$2.getX() + $$8, $$2.getY(), $$2.getZ() + $$8)) {
            int $$12 = $$6 - $$11.distManhattan($$2);
            if ($$12 < 0) continue;
            $$10 |= this.placeColumn($$3, $$1, $$11, $$12, $$5.reach().sample($$4));
        }
        return $$10;
    }

    private boolean placeColumn(LevelAccessor $$0, int $$1, BlockPos $$2, int $$3, int $$4) {
        boolean $$5 = false;
        block0: for (BlockPos $$6 : BlockPos.betweenClosed($$2.getX() - $$4, $$2.getY(), $$2.getZ() - $$4, $$2.getX() + $$4, $$2.getY(), $$2.getZ() + $$4)) {
            BlockPos $$8;
            int $$7 = $$6.distManhattan($$2);
            BlockPos blockPos = $$8 = BasaltColumnsFeature.isAirOrLavaOcean($$0, $$1, $$6) ? BasaltColumnsFeature.findSurface($$0, $$1, $$6.mutable(), $$7) : BasaltColumnsFeature.findAir($$0, $$6.mutable(), $$7);
            if ($$8 == null) continue;
            BlockPos.MutableBlockPos $$10 = $$8.mutable();
            for (int $$9 = $$3 - $$7 / 2; $$9 >= 0; --$$9) {
                if (BasaltColumnsFeature.isAirOrLavaOcean($$0, $$1, $$10)) {
                    this.setBlock($$0, $$10, Blocks.BASALT.defaultBlockState());
                    $$10.move(Direction.UP);
                    $$5 = true;
                    continue;
                }
                if (!$$0.getBlockState($$10).is(Blocks.BASALT)) continue block0;
                $$10.move(Direction.UP);
            }
        }
        return $$5;
    }

    @Nullable
    private static BlockPos findSurface(LevelAccessor $$0, int $$1, BlockPos.MutableBlockPos $$2, int $$3) {
        while ($$2.getY() > $$0.getMinBuildHeight() + 1 && $$3 > 0) {
            --$$3;
            if (BasaltColumnsFeature.canPlaceAt($$0, $$1, $$2)) {
                return $$2;
            }
            $$2.move(Direction.DOWN);
        }
        return null;
    }

    private static boolean canPlaceAt(LevelAccessor $$0, int $$1, BlockPos.MutableBlockPos $$2) {
        if (BasaltColumnsFeature.isAirOrLavaOcean($$0, $$1, $$2)) {
            BlockState $$3 = $$0.getBlockState($$2.move(Direction.DOWN));
            $$2.move(Direction.UP);
            return !$$3.isAir() && !CANNOT_PLACE_ON.contains((Object)$$3.getBlock());
        }
        return false;
    }

    @Nullable
    private static BlockPos findAir(LevelAccessor $$0, BlockPos.MutableBlockPos $$1, int $$2) {
        while ($$1.getY() < $$0.getMaxBuildHeight() && $$2 > 0) {
            --$$2;
            BlockState $$3 = $$0.getBlockState($$1);
            if (CANNOT_PLACE_ON.contains((Object)$$3.getBlock())) {
                return null;
            }
            if ($$3.isAir()) {
                return $$1;
            }
            $$1.move(Direction.UP);
        }
        return null;
    }

    private static boolean isAirOrLavaOcean(LevelAccessor $$0, int $$1, BlockPos $$2) {
        BlockState $$3 = $$0.getBlockState($$2);
        return $$3.isAir() || $$3.is(Blocks.LAVA) && $$2.getY() <= $$1;
    }
}