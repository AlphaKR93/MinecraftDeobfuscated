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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;

public class TwistingVinesFeature
extends Feature<TwistingVinesConfig> {
    public TwistingVinesFeature(Codec<TwistingVinesConfig> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<TwistingVinesConfig> $$0) {
        BlockPos $$2;
        WorldGenLevel $$1 = $$0.level();
        if (TwistingVinesFeature.isInvalidPlacementLocation($$1, $$2 = $$0.origin())) {
            return false;
        }
        RandomSource $$3 = $$0.random();
        TwistingVinesConfig $$4 = $$0.config();
        int $$5 = $$4.spreadWidth();
        int $$6 = $$4.spreadHeight();
        int $$7 = $$4.maxHeight();
        BlockPos.MutableBlockPos $$8 = new BlockPos.MutableBlockPos();
        for (int $$9 = 0; $$9 < $$5 * $$5; ++$$9) {
            $$8.set($$2).move(Mth.nextInt($$3, -$$5, $$5), Mth.nextInt($$3, -$$6, $$6), Mth.nextInt($$3, -$$5, $$5));
            if (!TwistingVinesFeature.findFirstAirBlockAboveGround($$1, $$8) || TwistingVinesFeature.isInvalidPlacementLocation($$1, $$8)) continue;
            int $$10 = Mth.nextInt($$3, 1, $$7);
            if ($$3.nextInt(6) == 0) {
                $$10 *= 2;
            }
            if ($$3.nextInt(5) == 0) {
                $$10 = 1;
            }
            int $$11 = 17;
            int $$12 = 25;
            TwistingVinesFeature.placeWeepingVinesColumn($$1, $$3, $$8, $$10, 17, 25);
        }
        return true;
    }

    private static boolean findFirstAirBlockAboveGround(LevelAccessor $$0, BlockPos.MutableBlockPos $$1) {
        do {
            $$1.move(0, -1, 0);
            if (!$$0.isOutsideBuildHeight($$1)) continue;
            return false;
        } while ($$0.getBlockState($$1).isAir());
        $$1.move(0, 1, 0);
        return true;
    }

    public static void placeWeepingVinesColumn(LevelAccessor $$0, RandomSource $$1, BlockPos.MutableBlockPos $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = 1; $$6 <= $$3; ++$$6) {
            if ($$0.isEmptyBlock($$2)) {
                if ($$6 == $$3 || !$$0.isEmptyBlock((BlockPos)$$2.above())) {
                    $$0.setBlock($$2, (BlockState)Blocks.TWISTING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt($$1, $$4, $$5)), 2);
                    break;
                }
                $$0.setBlock($$2, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
            }
            $$2.move(Direction.UP);
        }
    }

    private static boolean isInvalidPlacementLocation(LevelAccessor $$0, BlockPos $$1) {
        if (!$$0.isEmptyBlock($$1)) {
            return true;
        }
        BlockState $$2 = $$0.getBlockState((BlockPos)$$1.below());
        return !$$2.is(Blocks.NETHERRACK) && !$$2.is(Blocks.WARPED_NYLIUM) && !$$2.is(Blocks.WARPED_WART_BLOCK);
    }
}