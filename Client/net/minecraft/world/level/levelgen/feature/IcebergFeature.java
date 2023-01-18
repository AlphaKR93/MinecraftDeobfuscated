/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Material;

public class IcebergFeature
extends Feature<BlockStateConfiguration> {
    public IcebergFeature(Codec<BlockStateConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> $$0) {
        boolean $$24;
        int $$11;
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        $$1 = new BlockPos($$1.getX(), $$0.chunkGenerator().getSeaLevel(), $$1.getZ());
        RandomSource $$3 = $$0.random();
        boolean $$4 = $$3.nextDouble() > 0.7;
        BlockState $$5 = $$0.config().state;
        double $$6 = $$3.nextDouble() * 2.0 * Math.PI;
        int $$7 = 11 - $$3.nextInt(5);
        int $$8 = 3 + $$3.nextInt(3);
        boolean $$9 = $$3.nextDouble() > 0.7;
        int $$10 = 11;
        int n = $$11 = $$9 ? $$3.nextInt(6) + 6 : $$3.nextInt(15) + 3;
        if (!$$9 && $$3.nextDouble() > 0.9) {
            $$11 += $$3.nextInt(19) + 7;
        }
        int $$12 = Math.min((int)($$11 + $$3.nextInt(11)), (int)18);
        int $$13 = Math.min((int)($$11 + $$3.nextInt(7) - $$3.nextInt(5)), (int)11);
        int $$14 = $$9 ? $$7 : 11;
        for (int $$15 = -$$14; $$15 < $$14; ++$$15) {
            for (int $$16 = -$$14; $$16 < $$14; ++$$16) {
                for (int $$17 = 0; $$17 < $$11; ++$$17) {
                    int $$18;
                    int n2 = $$18 = $$9 ? this.heightDependentRadiusEllipse($$17, $$11, $$13) : this.heightDependentRadiusRound($$3, $$17, $$11, $$13);
                    if (!$$9 && $$15 >= $$18) continue;
                    this.generateIcebergBlock($$2, $$3, $$1, $$11, $$15, $$17, $$16, $$18, $$14, $$9, $$8, $$6, $$4, $$5);
                }
            }
        }
        this.smooth($$2, $$1, $$13, $$11, $$9, $$7);
        for (int $$19 = -$$14; $$19 < $$14; ++$$19) {
            for (int $$20 = -$$14; $$20 < $$14; ++$$20) {
                for (int $$21 = -1; $$21 > -$$12; --$$21) {
                    int $$22 = $$9 ? Mth.ceil((float)$$14 * (1.0f - (float)Math.pow((double)$$21, (double)2.0) / ((float)$$12 * 8.0f))) : $$14;
                    int $$23 = this.heightDependentRadiusSteep($$3, -$$21, $$12, $$13);
                    if ($$19 >= $$23) continue;
                    this.generateIcebergBlock($$2, $$3, $$1, $$12, $$19, $$21, $$20, $$23, $$22, $$9, $$8, $$6, $$4, $$5);
                }
            }
        }
        boolean bl = $$9 ? $$3.nextDouble() > 0.1 : ($$24 = $$3.nextDouble() > 0.7);
        if ($$24) {
            this.generateCutOut($$3, $$2, $$13, $$11, $$1, $$9, $$7, $$6, $$8);
        }
        return true;
    }

    private void generateCutOut(RandomSource $$0, LevelAccessor $$1, int $$2, int $$3, BlockPos $$4, boolean $$5, int $$6, double $$7, int $$8) {
        int $$9 = $$0.nextBoolean() ? -1 : 1;
        int $$10 = $$0.nextBoolean() ? -1 : 1;
        int $$11 = $$0.nextInt(Math.max((int)($$2 / 2 - 2), (int)1));
        if ($$0.nextBoolean()) {
            $$11 = $$2 / 2 + 1 - $$0.nextInt(Math.max((int)($$2 - $$2 / 2 - 1), (int)1));
        }
        int $$12 = $$0.nextInt(Math.max((int)($$2 / 2 - 2), (int)1));
        if ($$0.nextBoolean()) {
            $$12 = $$2 / 2 + 1 - $$0.nextInt(Math.max((int)($$2 - $$2 / 2 - 1), (int)1));
        }
        if ($$5) {
            $$11 = $$12 = $$0.nextInt(Math.max((int)($$6 - 5), (int)1));
        }
        BlockPos $$13 = new BlockPos($$9 * $$11, 0, $$10 * $$12);
        double $$14 = $$5 ? $$7 + 1.5707963267948966 : $$0.nextDouble() * 2.0 * Math.PI;
        for (int $$15 = 0; $$15 < $$3 - 3; ++$$15) {
            int $$16 = this.heightDependentRadiusRound($$0, $$15, $$3, $$2);
            this.carve($$16, $$15, $$4, $$1, false, $$14, $$13, $$6, $$8);
        }
        for (int $$17 = -1; $$17 > -$$3 + $$0.nextInt(5); --$$17) {
            int $$18 = this.heightDependentRadiusSteep($$0, -$$17, $$3, $$2);
            this.carve($$18, $$17, $$4, $$1, true, $$14, $$13, $$6, $$8);
        }
    }

    private void carve(int $$0, int $$1, BlockPos $$2, LevelAccessor $$3, boolean $$4, double $$5, BlockPos $$6, int $$7, int $$8) {
        int $$9 = $$0 + 1 + $$7 / 3;
        int $$10 = Math.min((int)($$0 - 3), (int)3) + $$8 / 2 - 1;
        for (int $$11 = -$$9; $$11 < $$9; ++$$11) {
            for (int $$12 = -$$9; $$12 < $$9; ++$$12) {
                BlockPos $$14;
                BlockState $$15;
                double $$13 = this.signedDistanceEllipse($$11, $$12, $$6, $$9, $$10, $$5);
                if (!($$13 < 0.0) || !IcebergFeature.isIcebergState($$15 = $$3.getBlockState($$14 = $$2.offset($$11, $$1, $$12))) && !$$15.is(Blocks.SNOW_BLOCK)) continue;
                if ($$4) {
                    this.setBlock($$3, $$14, Blocks.WATER.defaultBlockState());
                    continue;
                }
                this.setBlock($$3, $$14, Blocks.AIR.defaultBlockState());
                this.removeFloatingSnowLayer($$3, $$14);
            }
        }
    }

    private void removeFloatingSnowLayer(LevelAccessor $$0, BlockPos $$1) {
        if ($$0.getBlockState((BlockPos)$$1.above()).is(Blocks.SNOW)) {
            this.setBlock($$0, (BlockPos)$$1.above(), Blocks.AIR.defaultBlockState());
        }
    }

    private void generateIcebergBlock(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, boolean $$9, int $$10, double $$11, boolean $$12, BlockState $$13) {
        double $$14;
        double d = $$14 = $$9 ? this.signedDistanceEllipse($$4, $$6, BlockPos.ZERO, $$8, this.getEllipseC($$5, $$3, $$10), $$11) : this.signedDistanceCircle($$4, $$6, BlockPos.ZERO, $$7, $$1);
        if ($$14 < 0.0) {
            double $$16;
            BlockPos $$15 = $$2.offset($$4, $$5, $$6);
            double d2 = $$16 = $$9 ? -0.5 : (double)(-6 - $$1.nextInt(3));
            if ($$14 > $$16 && $$1.nextDouble() > 0.9) {
                return;
            }
            this.setIcebergBlock($$15, $$0, $$1, $$3 - $$5, $$3, $$9, $$12, $$13);
        }
    }

    private void setIcebergBlock(BlockPos $$0, LevelAccessor $$1, RandomSource $$2, int $$3, int $$4, boolean $$5, boolean $$6, BlockState $$7) {
        BlockState $$8 = $$1.getBlockState($$0);
        if ($$8.getMaterial() == Material.AIR || $$8.is(Blocks.SNOW_BLOCK) || $$8.is(Blocks.ICE) || $$8.is(Blocks.WATER)) {
            int $$10;
            boolean $$9 = !$$5 || $$2.nextDouble() > 0.05;
            int n = $$10 = $$5 ? 3 : 2;
            if ($$6 && !$$8.is(Blocks.WATER) && (double)$$3 <= (double)$$2.nextInt(Math.max((int)1, (int)($$4 / $$10))) + (double)$$4 * 0.6 && $$9) {
                this.setBlock($$1, $$0, Blocks.SNOW_BLOCK.defaultBlockState());
            } else {
                this.setBlock($$1, $$0, $$7);
            }
        }
    }

    private int getEllipseC(int $$0, int $$1, int $$2) {
        int $$3 = $$2;
        if ($$0 > 0 && $$1 - $$0 <= 3) {
            $$3 -= 4 - ($$1 - $$0);
        }
        return $$3;
    }

    private double signedDistanceCircle(int $$0, int $$1, BlockPos $$2, int $$3, RandomSource $$4) {
        float $$5 = 10.0f * Mth.clamp($$4.nextFloat(), 0.2f, 0.8f) / (float)$$3;
        return (double)$$5 + Math.pow((double)($$0 - $$2.getX()), (double)2.0) + Math.pow((double)($$1 - $$2.getZ()), (double)2.0) - Math.pow((double)$$3, (double)2.0);
    }

    private double signedDistanceEllipse(int $$0, int $$1, BlockPos $$2, int $$3, int $$4, double $$5) {
        return Math.pow((double)(((double)($$0 - $$2.getX()) * Math.cos((double)$$5) - (double)($$1 - $$2.getZ()) * Math.sin((double)$$5)) / (double)$$3), (double)2.0) + Math.pow((double)(((double)($$0 - $$2.getX()) * Math.sin((double)$$5) + (double)($$1 - $$2.getZ()) * Math.cos((double)$$5)) / (double)$$4), (double)2.0) - 1.0;
    }

    private int heightDependentRadiusRound(RandomSource $$0, int $$1, int $$2, int $$3) {
        float $$4 = 3.5f - $$0.nextFloat();
        float $$5 = (1.0f - (float)Math.pow((double)$$1, (double)2.0) / ((float)$$2 * $$4)) * (float)$$3;
        if ($$2 > 15 + $$0.nextInt(5)) {
            int $$6 = $$1 < 3 + $$0.nextInt(6) ? $$1 / 2 : $$1;
            $$5 = (1.0f - (float)$$6 / ((float)$$2 * $$4 * 0.4f)) * (float)$$3;
        }
        return Mth.ceil($$5 / 2.0f);
    }

    private int heightDependentRadiusEllipse(int $$0, int $$1, int $$2) {
        float $$3 = 1.0f;
        float $$4 = (1.0f - (float)Math.pow((double)$$0, (double)2.0) / ((float)$$1 * 1.0f)) * (float)$$2;
        return Mth.ceil($$4 / 2.0f);
    }

    private int heightDependentRadiusSteep(RandomSource $$0, int $$1, int $$2, int $$3) {
        float $$4 = 1.0f + $$0.nextFloat() / 2.0f;
        float $$5 = (1.0f - (float)$$1 / ((float)$$2 * $$4)) * (float)$$3;
        return Mth.ceil($$5 / 2.0f);
    }

    private static boolean isIcebergState(BlockState $$0) {
        return $$0.is(Blocks.PACKED_ICE) || $$0.is(Blocks.SNOW_BLOCK) || $$0.is(Blocks.BLUE_ICE);
    }

    private boolean belowIsAir(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState((BlockPos)$$1.below()).getMaterial() == Material.AIR;
    }

    private void smooth(LevelAccessor $$0, BlockPos $$1, int $$2, int $$3, boolean $$4, int $$5) {
        int $$6 = $$4 ? $$5 : $$2 / 2;
        for (int $$7 = -$$6; $$7 <= $$6; ++$$7) {
            for (int $$8 = -$$6; $$8 <= $$6; ++$$8) {
                for (int $$9 = 0; $$9 <= $$3; ++$$9) {
                    BlockPos $$10 = $$1.offset($$7, $$9, $$8);
                    BlockState $$11 = $$0.getBlockState($$10);
                    if (!IcebergFeature.isIcebergState($$11) && !$$11.is(Blocks.SNOW)) continue;
                    if (this.belowIsAir($$0, $$10)) {
                        this.setBlock($$0, $$10, Blocks.AIR.defaultBlockState());
                        this.setBlock($$0, (BlockPos)$$10.above(), Blocks.AIR.defaultBlockState());
                        continue;
                    }
                    if (!IcebergFeature.isIcebergState($$11)) continue;
                    BlockState[] $$12 = new BlockState[]{$$0.getBlockState((BlockPos)$$10.west()), $$0.getBlockState((BlockPos)$$10.east()), $$0.getBlockState((BlockPos)$$10.north()), $$0.getBlockState((BlockPos)$$10.south())};
                    int $$13 = 0;
                    for (BlockState $$14 : $$12) {
                        if (IcebergFeature.isIcebergState($$14)) continue;
                        ++$$13;
                    }
                    if ($$13 < 3) continue;
                    this.setBlock($$0, $$10, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}