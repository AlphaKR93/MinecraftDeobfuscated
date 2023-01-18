/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class DripstoneUtils {
    protected static double getDripstoneHeight(double $$0, double $$1, double $$2, double $$3) {
        if ($$0 < $$3) {
            $$0 = $$3;
        }
        double $$4 = 0.384;
        double $$5 = $$0 / $$1 * 0.384;
        double $$6 = 0.75 * Math.pow((double)$$5, (double)1.3333333333333333);
        double $$7 = Math.pow((double)$$5, (double)0.6666666666666666);
        double $$8 = 0.3333333333333333 * Math.log((double)$$5);
        double $$9 = $$2 * ($$6 - $$7 - $$8);
        $$9 = Math.max((double)$$9, (double)0.0);
        return $$9 / 0.384 * $$1;
    }

    protected static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel $$0, BlockPos $$1, int $$2) {
        if (DripstoneUtils.isEmptyOrWaterOrLava($$0, $$1)) {
            return false;
        }
        float $$3 = 6.0f;
        float $$4 = 6.0f / (float)$$2;
        for (float $$5 = 0.0f; $$5 < (float)Math.PI * 2; $$5 += $$4) {
            int $$7;
            int $$6 = (int)(Mth.cos($$5) * (float)$$2);
            if (!DripstoneUtils.isEmptyOrWaterOrLava($$0, $$1.offset($$6, 0, $$7 = (int)(Mth.sin($$5) * (float)$$2)))) continue;
            return false;
        }
        return true;
    }

    protected static boolean isEmptyOrWater(LevelAccessor $$0, BlockPos $$1) {
        return $$0.isStateAtPosition($$1, DripstoneUtils::isEmptyOrWater);
    }

    protected static boolean isEmptyOrWaterOrLava(LevelAccessor $$0, BlockPos $$1) {
        return $$0.isStateAtPosition($$1, DripstoneUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(Direction $$0, int $$1, boolean $$2, Consumer<BlockState> $$3) {
        if ($$1 >= 3) {
            $$3.accept((Object)DripstoneUtils.createPointedDripstone($$0, DripstoneThickness.BASE));
            for (int $$4 = 0; $$4 < $$1 - 3; ++$$4) {
                $$3.accept((Object)DripstoneUtils.createPointedDripstone($$0, DripstoneThickness.MIDDLE));
            }
        }
        if ($$1 >= 2) {
            $$3.accept((Object)DripstoneUtils.createPointedDripstone($$0, DripstoneThickness.FRUSTUM));
        }
        if ($$1 >= 1) {
            $$3.accept((Object)DripstoneUtils.createPointedDripstone($$0, $$2 ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }
    }

    protected static void growPointedDripstone(LevelAccessor $$0, BlockPos $$1, Direction $$2, int $$32, boolean $$4) {
        if (!DripstoneUtils.isDripstoneBase($$0.getBlockState((BlockPos)$$1.relative($$2.getOpposite())))) {
            return;
        }
        BlockPos.MutableBlockPos $$5 = $$1.mutable();
        DripstoneUtils.buildBaseToTipColumn($$2, $$32, $$4, (Consumer<BlockState>)((Consumer)$$3 -> {
            if ($$3.is(Blocks.POINTED_DRIPSTONE)) {
                $$3 = (BlockState)$$3.setValue(PointedDripstoneBlock.WATERLOGGED, $$0.isWaterAt($$5));
            }
            $$0.setBlock($$5, (BlockState)$$3, 2);
            $$5.move($$2);
        }));
    }

    protected static boolean placeDripstoneBlockIfPossible(LevelAccessor $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        if ($$2.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
            $$0.setBlock($$1, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
            return true;
        }
        return false;
    }

    private static BlockState createPointedDripstone(Direction $$0, DripstoneThickness $$1) {
        return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, $$0)).setValue(PointedDripstoneBlock.THICKNESS, $$1);
    }

    public static boolean isDripstoneBaseOrLava(BlockState $$0) {
        return DripstoneUtils.isDripstoneBase($$0) || $$0.is(Blocks.LAVA);
    }

    public static boolean isDripstoneBase(BlockState $$0) {
        return $$0.is(Blocks.DRIPSTONE_BLOCK) || $$0.is(BlockTags.DRIPSTONE_REPLACEABLE);
    }

    public static boolean isEmptyOrWater(BlockState $$0) {
        return $$0.isAir() || $$0.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState $$0) {
        return !$$0.isAir() && !$$0.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState $$0) {
        return $$0.isAir() || $$0.is(Blocks.WATER) || $$0.is(Blocks.LAVA);
    }
}