/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

public class DripstoneClusterFeature
extends Feature<DripstoneClusterConfiguration> {
    public DripstoneClusterFeature(Codec<DripstoneClusterConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<DripstoneClusterConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        DripstoneClusterConfiguration $$3 = $$0.config();
        RandomSource $$4 = $$0.random();
        if (!DripstoneUtils.isEmptyOrWater($$1, $$2)) {
            return false;
        }
        int $$5 = $$3.height.sample($$4);
        float $$6 = $$3.wetness.sample($$4);
        float $$7 = $$3.density.sample($$4);
        int $$8 = $$3.radius.sample($$4);
        int $$9 = $$3.radius.sample($$4);
        for (int $$10 = -$$8; $$10 <= $$8; ++$$10) {
            for (int $$11 = -$$9; $$11 <= $$9; ++$$11) {
                double $$12 = this.getChanceOfStalagmiteOrStalactite($$8, $$9, $$10, $$11, $$3);
                BlockPos $$13 = $$2.offset($$10, 0, $$11);
                this.placeColumn($$1, $$4, $$13, $$10, $$11, $$6, $$12, $$5, $$7, $$3);
            }
        }
        return true;
    }

    private void placeColumn(WorldGenLevel $$0, RandomSource $$1, BlockPos $$2, int $$3, int $$4, float $$5, double $$6, int $$7, float $$8, DripstoneClusterConfiguration $$9) {
        boolean $$39;
        int $$38;
        int $$37;
        int $$28;
        boolean $$24;
        int $$23;
        boolean $$18;
        Column $$16;
        boolean $$13;
        Optional<Column> $$10 = Column.scan($$0, $$2, $$9.floorToCeilingSearchRange, (Predicate<BlockState>)((Predicate)DripstoneUtils::isEmptyOrWater), (Predicate<BlockState>)((Predicate)DripstoneUtils::isNeitherEmptyNorWater));
        if (!$$10.isPresent()) {
            return;
        }
        OptionalInt $$11 = ((Column)$$10.get()).getCeiling();
        OptionalInt $$12 = ((Column)$$10.get()).getFloor();
        if (!$$11.isPresent() && !$$12.isPresent()) {
            return;
        }
        boolean bl = $$13 = $$1.nextFloat() < $$5;
        if ($$13 && $$12.isPresent() && this.canPlacePool($$0, $$2.atY($$12.getAsInt()))) {
            int $$14 = $$12.getAsInt();
            Column $$15 = ((Column)$$10.get()).withFloor(OptionalInt.of((int)($$14 - 1)));
            $$0.setBlock($$2.atY($$14), Blocks.WATER.defaultBlockState(), 2);
        } else {
            $$16 = (Column)$$10.get();
        }
        OptionalInt $$17 = $$16.getFloor();
        boolean bl2 = $$18 = $$1.nextDouble() < $$6;
        if ($$11.isPresent() && $$18 && !this.isLava($$0, $$2.atY($$11.getAsInt()))) {
            int $$21;
            int $$19 = $$9.dripstoneBlockLayerThickness.sample($$1);
            this.replaceBlocksWithDripstoneBlocks($$0, $$2.atY($$11.getAsInt()), $$19, Direction.UP);
            if ($$17.isPresent()) {
                int $$20 = Math.min((int)$$7, (int)($$11.getAsInt() - $$17.getAsInt()));
            } else {
                $$21 = $$7;
            }
            int $$22 = this.getDripstoneHeight($$1, $$3, $$4, $$8, $$21, $$9);
        } else {
            $$23 = 0;
        }
        boolean bl3 = $$24 = $$1.nextDouble() < $$6;
        if ($$17.isPresent() && $$24 && !this.isLava($$0, $$2.atY($$17.getAsInt()))) {
            int $$25 = $$9.dripstoneBlockLayerThickness.sample($$1);
            this.replaceBlocksWithDripstoneBlocks($$0, $$2.atY($$17.getAsInt()), $$25, Direction.DOWN);
            if ($$11.isPresent()) {
                int $$26 = Math.max((int)0, (int)($$23 + Mth.randomBetweenInclusive($$1, -$$9.maxStalagmiteStalactiteHeightDiff, $$9.maxStalagmiteStalactiteHeightDiff)));
            } else {
                int $$27 = this.getDripstoneHeight($$1, $$3, $$4, $$8, $$7, $$9);
            }
        } else {
            $$28 = 0;
        }
        if ($$11.isPresent() && $$17.isPresent() && $$11.getAsInt() - $$23 <= $$17.getAsInt() + $$28) {
            int $$29 = $$17.getAsInt();
            int $$30 = $$11.getAsInt();
            int $$31 = Math.max((int)($$30 - $$23), (int)($$29 + 1));
            int $$32 = Math.min((int)($$29 + $$28), (int)($$30 - 1));
            int $$33 = Mth.randomBetweenInclusive($$1, $$31, $$32 + 1);
            int $$34 = $$33 - 1;
            int $$35 = $$30 - $$33;
            int $$36 = $$34 - $$29;
        } else {
            $$37 = $$23;
            $$38 = $$28;
        }
        boolean bl4 = $$39 = $$1.nextBoolean() && $$37 > 0 && $$38 > 0 && $$16.getHeight().isPresent() && $$37 + $$38 == $$16.getHeight().getAsInt();
        if ($$11.isPresent()) {
            DripstoneUtils.growPointedDripstone($$0, $$2.atY($$11.getAsInt() - 1), Direction.DOWN, $$37, $$39);
        }
        if ($$17.isPresent()) {
            DripstoneUtils.growPointedDripstone($$0, $$2.atY($$17.getAsInt() + 1), Direction.UP, $$38, $$39);
        }
    }

    private boolean isLava(LevelReader $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).is(Blocks.LAVA);
    }

    private int getDripstoneHeight(RandomSource $$0, int $$1, int $$2, float $$3, int $$4, DripstoneClusterConfiguration $$5) {
        if ($$0.nextFloat() > $$3) {
            return 0;
        }
        int $$6 = Math.abs((int)$$1) + Math.abs((int)$$2);
        float $$7 = (float)Mth.clampedMap((double)$$6, 0.0, (double)$$5.maxDistanceFromCenterAffectingHeightBias, (double)$$4 / 2.0, 0.0);
        return (int)DripstoneClusterFeature.randomBetweenBiased($$0, 0.0f, $$4, $$7, $$5.heightDeviation);
    }

    private boolean canPlacePool(WorldGenLevel $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        if ($$2.is(Blocks.WATER) || $$2.is(Blocks.DRIPSTONE_BLOCK) || $$2.is(Blocks.POINTED_DRIPSTONE)) {
            return false;
        }
        if ($$0.getBlockState((BlockPos)$$1.above()).getFluidState().is(FluidTags.WATER)) {
            return false;
        }
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            if (this.canBeAdjacentToWater($$0, (BlockPos)$$1.relative($$3))) continue;
            return false;
        }
        return this.canBeAdjacentToWater($$0, (BlockPos)$$1.below());
    }

    private boolean canBeAdjacentToWater(LevelAccessor $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return $$2.is(BlockTags.BASE_STONE_OVERWORLD) || $$2.getFluidState().is(FluidTags.WATER);
    }

    private void replaceBlocksWithDripstoneBlocks(WorldGenLevel $$0, BlockPos $$1, int $$2, Direction $$3) {
        BlockPos.MutableBlockPos $$4 = $$1.mutable();
        for (int $$5 = 0; $$5 < $$2; ++$$5) {
            if (!DripstoneUtils.placeDripstoneBlockIfPossible($$0, $$4)) {
                return;
            }
            $$4.move($$3);
        }
    }

    private double getChanceOfStalagmiteOrStalactite(int $$0, int $$1, int $$2, int $$3, DripstoneClusterConfiguration $$4) {
        int $$5 = $$0 - Math.abs((int)$$2);
        int $$6 = $$1 - Math.abs((int)$$3);
        int $$7 = Math.min((int)$$5, (int)$$6);
        return Mth.clampedMap($$7, 0.0f, $$4.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, $$4.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0f);
    }

    private static float randomBetweenBiased(RandomSource $$0, float $$1, float $$2, float $$3, float $$4) {
        return ClampedNormalFloat.sample($$0, $$3, $$4, $$1, $$2);
    }
}