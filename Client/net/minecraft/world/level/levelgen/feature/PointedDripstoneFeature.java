/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

public class PointedDripstoneFeature
extends Feature<PointedDripstoneConfiguration> {
    public PointedDripstoneFeature(Codec<PointedDripstoneConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<PointedDripstoneConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        RandomSource $$3 = $$0.random();
        PointedDripstoneConfiguration $$4 = $$0.config();
        Optional<Direction> $$5 = PointedDripstoneFeature.getTipDirection($$1, $$2, $$3);
        if ($$5.isEmpty()) {
            return false;
        }
        Vec3i $$6 = $$2.relative(((Direction)$$5.get()).getOpposite());
        PointedDripstoneFeature.createPatchOfDripstoneBlocks($$1, $$3, (BlockPos)$$6, $$4);
        int $$7 = $$3.nextFloat() < $$4.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater($$1.getBlockState((BlockPos)$$2.relative((Direction)$$5.get()))) ? 2 : 1;
        DripstoneUtils.growPointedDripstone($$1, $$2, (Direction)$$5.get(), $$7, false);
        return true;
    }

    private static Optional<Direction> getTipDirection(LevelAccessor $$0, BlockPos $$1, RandomSource $$2) {
        boolean $$3 = DripstoneUtils.isDripstoneBase($$0.getBlockState((BlockPos)$$1.above()));
        boolean $$4 = DripstoneUtils.isDripstoneBase($$0.getBlockState((BlockPos)$$1.below()));
        if ($$3 && $$4) {
            return Optional.of((Object)($$2.nextBoolean() ? Direction.DOWN : Direction.UP));
        }
        if ($$3) {
            return Optional.of((Object)Direction.DOWN);
        }
        if ($$4) {
            return Optional.of((Object)Direction.UP);
        }
        return Optional.empty();
    }

    private static void createPatchOfDripstoneBlocks(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, PointedDripstoneConfiguration $$3) {
        DripstoneUtils.placeDripstoneBlockIfPossible($$0, $$2);
        for (Direction $$4 : Direction.Plane.HORIZONTAL) {
            if ($$1.nextFloat() > $$3.chanceOfDirectionalSpread) continue;
            Vec3i $$5 = $$2.relative($$4);
            DripstoneUtils.placeDripstoneBlockIfPossible($$0, (BlockPos)$$5);
            if ($$1.nextFloat() > $$3.chanceOfSpreadRadius2) continue;
            Vec3i $$6 = ((BlockPos)$$5).relative(Direction.getRandom($$1));
            DripstoneUtils.placeDripstoneBlockIfPossible($$0, (BlockPos)$$6);
            if ($$1.nextFloat() > $$3.chanceOfSpreadRadius3) continue;
            Vec3i $$7 = ((BlockPos)$$6).relative(Direction.getRandom($$1));
            DripstoneUtils.placeDripstoneBlockIfPossible($$0, (BlockPos)$$7);
        }
    }
}