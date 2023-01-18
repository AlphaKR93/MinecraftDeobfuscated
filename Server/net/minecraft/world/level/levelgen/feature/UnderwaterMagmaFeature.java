/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
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
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.phys.AABB;

public class UnderwaterMagmaFeature
extends Feature<UnderwaterMagmaConfiguration> {
    public UnderwaterMagmaFeature(Codec<UnderwaterMagmaConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<UnderwaterMagmaConfiguration> $$0) {
        Vec3i $$7;
        WorldGenLevel $$12 = $$0.level();
        BlockPos $$22 = $$0.origin();
        UnderwaterMagmaConfiguration $$3 = $$0.config();
        RandomSource $$4 = $$0.random();
        OptionalInt $$5 = UnderwaterMagmaFeature.getFloorY($$12, $$22, $$3);
        if (!$$5.isPresent()) {
            return false;
        }
        BlockPos $$6 = $$22.atY($$5.getAsInt());
        AABB $$8 = new AABB((BlockPos)$$6.subtract($$7 = new Vec3i($$3.placementRadiusAroundFloor, $$3.placementRadiusAroundFloor, $$3.placementRadiusAroundFloor)), (BlockPos)$$6.offset($$7));
        return BlockPos.betweenClosedStream($$8).filter($$2 -> $$4.nextFloat() < $$1.placementProbabilityPerValidPosition).filter($$1 -> this.isValidPlacement($$12, (BlockPos)$$1)).mapToInt($$1 -> {
            $$12.setBlock((BlockPos)$$1, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
            return 1;
        }).sum() > 0;
    }

    private static OptionalInt getFloorY(WorldGenLevel $$02, BlockPos $$1, UnderwaterMagmaConfiguration $$2) {
        Predicate $$3 = $$0 -> $$0.is(Blocks.WATER);
        Predicate $$4 = $$0 -> !$$0.is(Blocks.WATER);
        Optional<Column> $$5 = Column.scan($$02, $$1, $$2.floorSearchRange, (Predicate<BlockState>)$$3, (Predicate<BlockState>)$$4);
        return (OptionalInt)$$5.map(Column::getFloor).orElseGet(OptionalInt::empty);
    }

    private boolean isValidPlacement(WorldGenLevel $$0, BlockPos $$1) {
        if (this.isWaterOrAir($$0, $$1) || this.isWaterOrAir($$0, (BlockPos)$$1.below())) {
            return false;
        }
        for (Direction $$2 : Direction.Plane.HORIZONTAL) {
            if (!this.isWaterOrAir($$0, (BlockPos)$$1.relative($$2))) continue;
            return false;
        }
        return true;
    }

    private boolean isWaterOrAir(LevelAccessor $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return $$2.is(Blocks.WATER) || $$2.isAir();
    }
}