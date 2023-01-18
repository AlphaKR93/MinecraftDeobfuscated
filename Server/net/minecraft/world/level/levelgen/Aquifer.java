/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableDouble
 */
package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {
    public static Aquifer create(NoiseChunk $$0, ChunkPos $$1, NoiseRouter $$2, PositionalRandomFactory $$3, int $$4, int $$5, FluidPicker $$6) {
        return new NoiseBasedAquifer($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public static Aquifer createDisabled(final FluidPicker $$0) {
        return new Aquifer(){

            @Override
            @Nullable
            public BlockState computeSubstance(DensityFunction.FunctionContext $$02, double $$1) {
                if ($$1 > 0.0) {
                    return null;
                }
                return $$0.computeFluid($$02.blockX(), $$02.blockY(), $$02.blockZ()).at($$02.blockY());
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
    }

    @Nullable
    public BlockState computeSubstance(DensityFunction.FunctionContext var1, double var2);

    public boolean shouldScheduleFluidUpdate();

    public static class NoiseBasedAquifer
    implements Aquifer {
        private static final int X_RANGE = 10;
        private static final int Y_RANGE = 9;
        private static final int Z_RANGE = 10;
        private static final int X_SEPARATION = 6;
        private static final int Y_SEPARATION = 3;
        private static final int Z_SEPARATION = 6;
        private static final int X_SPACING = 16;
        private static final int Y_SPACING = 12;
        private static final int Z_SPACING = 16;
        private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
        private static final double FLOWING_UPDATE_SIMULARITY = NoiseBasedAquifer.similarity(Mth.square(10), Mth.square(12));
        private final NoiseChunk noiseChunk;
        private final DensityFunction barrierNoise;
        private final DensityFunction fluidLevelFloodednessNoise;
        private final DensityFunction fluidLevelSpreadNoise;
        private final DensityFunction lavaNoise;
        private final PositionalRandomFactory positionalRandomFactory;
        private final FluidStatus[] aquiferCache;
        private final long[] aquiferLocationCache;
        private final FluidPicker globalFluidPicker;
        private final DensityFunction erosion;
        private final DensityFunction depth;
        private boolean shouldScheduleFluidUpdate;
        private final int minGridX;
        private final int minGridY;
        private final int minGridZ;
        private final int gridSizeX;
        private final int gridSizeZ;
        private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

        NoiseBasedAquifer(NoiseChunk $$0, ChunkPos $$1, NoiseRouter $$2, PositionalRandomFactory $$3, int $$4, int $$5, FluidPicker $$6) {
            this.noiseChunk = $$0;
            this.barrierNoise = $$2.barrierNoise();
            this.fluidLevelFloodednessNoise = $$2.fluidLevelFloodednessNoise();
            this.fluidLevelSpreadNoise = $$2.fluidLevelSpreadNoise();
            this.lavaNoise = $$2.lavaNoise();
            this.erosion = $$2.erosion();
            this.depth = $$2.depth();
            this.positionalRandomFactory = $$3;
            this.minGridX = this.gridX($$1.getMinBlockX()) - 1;
            this.globalFluidPicker = $$6;
            int $$7 = this.gridX($$1.getMaxBlockX()) + 1;
            this.gridSizeX = $$7 - this.minGridX + 1;
            this.minGridY = this.gridY($$4) - 1;
            int $$8 = this.gridY($$4 + $$5) + 1;
            int $$9 = $$8 - this.minGridY + 1;
            this.minGridZ = this.gridZ($$1.getMinBlockZ()) - 1;
            int $$10 = this.gridZ($$1.getMaxBlockZ()) + 1;
            this.gridSizeZ = $$10 - this.minGridZ + 1;
            int $$11 = this.gridSizeX * $$9 * this.gridSizeZ;
            this.aquiferCache = new FluidStatus[$$11];
            this.aquiferLocationCache = new long[$$11];
            Arrays.fill((long[])this.aquiferLocationCache, (long)Long.MAX_VALUE);
        }

        private int getIndex(int $$0, int $$1, int $$2) {
            int $$3 = $$0 - this.minGridX;
            int $$4 = $$1 - this.minGridY;
            int $$5 = $$2 - this.minGridZ;
            return ($$4 * this.gridSizeZ + $$5) * this.gridSizeX + $$3;
        }

        @Override
        @Nullable
        public BlockState computeSubstance(DensityFunction.FunctionContext $$0, double $$1) {
            double $$41;
            double $$39;
            BlockState $$32;
            int $$2 = $$0.blockX();
            int $$3 = $$0.blockY();
            int $$4 = $$0.blockZ();
            if ($$1 > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return null;
            }
            FluidStatus $$5 = this.globalFluidPicker.computeFluid($$2, $$3, $$4);
            if ($$5.at($$3).is(Blocks.LAVA)) {
                this.shouldScheduleFluidUpdate = false;
                return Blocks.LAVA.defaultBlockState();
            }
            int $$6 = Math.floorDiv((int)($$2 - 5), (int)16);
            int $$7 = Math.floorDiv((int)($$3 + 1), (int)12);
            int $$8 = Math.floorDiv((int)($$4 - 5), (int)16);
            int $$9 = Integer.MAX_VALUE;
            int $$10 = Integer.MAX_VALUE;
            int $$11 = Integer.MAX_VALUE;
            long $$12 = 0L;
            long $$13 = 0L;
            long $$14 = 0L;
            for (int $$15 = 0; $$15 <= 1; ++$$15) {
                for (int $$16 = -1; $$16 <= 1; ++$$16) {
                    for (int $$17 = 0; $$17 <= 1; ++$$17) {
                        long $$25;
                        int $$18 = $$6 + $$15;
                        int $$19 = $$7 + $$16;
                        int $$20 = $$8 + $$17;
                        int $$21 = this.getIndex($$18, $$19, $$20);
                        long $$22 = this.aquiferLocationCache[$$21];
                        if ($$22 != Long.MAX_VALUE) {
                            long $$23 = $$22;
                        } else {
                            RandomSource $$24 = this.positionalRandomFactory.at($$18, $$19, $$20);
                            this.aquiferLocationCache[$$21] = $$25 = BlockPos.asLong($$18 * 16 + $$24.nextInt(10), $$19 * 12 + $$24.nextInt(9), $$20 * 16 + $$24.nextInt(10));
                        }
                        int $$26 = BlockPos.getX($$25) - $$2;
                        int $$27 = BlockPos.getY($$25) - $$3;
                        int $$28 = BlockPos.getZ($$25) - $$4;
                        int $$29 = $$26 * $$26 + $$27 * $$27 + $$28 * $$28;
                        if ($$9 >= $$29) {
                            $$14 = $$13;
                            $$13 = $$12;
                            $$12 = $$25;
                            $$11 = $$10;
                            $$10 = $$9;
                            $$9 = $$29;
                            continue;
                        }
                        if ($$10 >= $$29) {
                            $$14 = $$13;
                            $$13 = $$25;
                            $$11 = $$10;
                            $$10 = $$29;
                            continue;
                        }
                        if ($$11 < $$29) continue;
                        $$14 = $$25;
                        $$11 = $$29;
                    }
                }
            }
            FluidStatus $$30 = this.getAquiferStatus($$12);
            double $$31 = NoiseBasedAquifer.similarity($$9, $$10);
            BlockState $$33 = $$32 = $$30.at($$3);
            if ($$31 <= 0.0) {
                this.shouldScheduleFluidUpdate = $$31 >= FLOWING_UPDATE_SIMULARITY;
                return $$33;
            }
            if ($$32.is(Blocks.WATER) && this.globalFluidPicker.computeFluid($$2, $$3 - 1, $$4).at($$3 - 1).is(Blocks.LAVA)) {
                this.shouldScheduleFluidUpdate = true;
                return $$33;
            }
            MutableDouble $$34 = new MutableDouble(Double.NaN);
            FluidStatus $$35 = this.getAquiferStatus($$13);
            double $$36 = $$31 * this.calculatePressure($$0, $$34, $$30, $$35);
            if ($$1 + $$36 > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return null;
            }
            FluidStatus $$37 = this.getAquiferStatus($$14);
            double $$38 = NoiseBasedAquifer.similarity($$9, $$11);
            if ($$38 > 0.0 && $$1 + ($$39 = $$31 * $$38 * this.calculatePressure($$0, $$34, $$30, $$37)) > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return null;
            }
            double $$40 = NoiseBasedAquifer.similarity($$10, $$11);
            if ($$40 > 0.0 && $$1 + ($$41 = $$31 * $$40 * this.calculatePressure($$0, $$34, $$35, $$37)) > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return null;
            }
            this.shouldScheduleFluidUpdate = true;
            return $$33;
        }

        @Override
        public boolean shouldScheduleFluidUpdate() {
            return this.shouldScheduleFluidUpdate;
        }

        private static double similarity(int $$0, int $$1) {
            double $$2 = 25.0;
            return 1.0 - (double)Math.abs((int)($$1 - $$0)) / 25.0;
        }

        private double calculatePressure(DensityFunction.FunctionContext $$0, MutableDouble $$1, FluidStatus $$2, FluidStatus $$3) {
            double $$29;
            double $$23;
            int $$4 = $$0.blockY();
            BlockState $$5 = $$2.at($$4);
            BlockState $$6 = $$3.at($$4);
            if ($$5.is(Blocks.LAVA) && $$6.is(Blocks.WATER) || $$5.is(Blocks.WATER) && $$6.is(Blocks.LAVA)) {
                return 2.0;
            }
            int $$7 = Math.abs((int)($$2.fluidLevel - $$3.fluidLevel));
            if ($$7 == 0) {
                return 0.0;
            }
            double $$8 = 0.5 * (double)($$2.fluidLevel + $$3.fluidLevel);
            double $$9 = (double)$$4 + 0.5 - $$8;
            double $$10 = (double)$$7 / 2.0;
            double $$11 = 0.0;
            double $$12 = 2.5;
            double $$13 = 1.5;
            double $$14 = 3.0;
            double $$15 = 10.0;
            double $$16 = 3.0;
            double $$17 = $$10 - Math.abs((double)$$9);
            if ($$9 > 0.0) {
                double $$18 = 0.0 + $$17;
                if ($$18 > 0.0) {
                    double $$19 = $$18 / 1.5;
                } else {
                    double $$20 = $$18 / 2.5;
                }
            } else {
                double $$21 = 3.0 + $$17;
                if ($$21 > 0.0) {
                    double $$22 = $$21 / 3.0;
                } else {
                    $$23 = $$21 / 10.0;
                }
            }
            double $$24 = 2.0;
            if ($$23 < -2.0 || $$23 > 2.0) {
                double $$25 = 0.0;
            } else {
                double $$26 = $$1.getValue();
                if (Double.isNaN((double)$$26)) {
                    double $$27 = this.barrierNoise.compute($$0);
                    $$1.setValue($$27);
                    double $$28 = $$27;
                } else {
                    $$29 = $$26;
                }
            }
            return 2.0 * ($$29 + $$23);
        }

        private int gridX(int $$0) {
            return Math.floorDiv((int)$$0, (int)16);
        }

        private int gridY(int $$0) {
            return Math.floorDiv((int)$$0, (int)12);
        }

        private int gridZ(int $$0) {
            return Math.floorDiv((int)$$0, (int)16);
        }

        private FluidStatus getAquiferStatus(long $$0) {
            FluidStatus $$9;
            int $$6;
            int $$5;
            int $$1 = BlockPos.getX($$0);
            int $$2 = BlockPos.getY($$0);
            int $$3 = BlockPos.getZ($$0);
            int $$4 = this.gridX($$1);
            int $$7 = this.getIndex($$4, $$5 = this.gridY($$2), $$6 = this.gridZ($$3));
            FluidStatus $$8 = this.aquiferCache[$$7];
            if ($$8 != null) {
                return $$8;
            }
            this.aquiferCache[$$7] = $$9 = this.computeFluid($$1, $$2, $$3);
            return $$9;
        }

        private FluidStatus computeFluid(int $$0, int $$1, int $$2) {
            FluidStatus $$3 = this.globalFluidPicker.computeFluid($$0, $$1, $$2);
            int $$4 = Integer.MAX_VALUE;
            int $$5 = $$1 + 12;
            int $$6 = $$1 - 12;
            boolean $$7 = false;
            for (int[] $$8 : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
                FluidStatus $$15;
                boolean $$14;
                boolean $$13;
                int $$9 = $$0 + SectionPos.sectionToBlockCoord($$8[0]);
                int $$10 = $$2 + SectionPos.sectionToBlockCoord($$8[1]);
                int $$11 = this.noiseChunk.preliminarySurfaceLevel($$9, $$10);
                int $$12 = $$11 + 8;
                boolean bl = $$13 = $$8[0] == 0 && $$8[1] == 0;
                if ($$13 && $$6 > $$12) {
                    return $$3;
                }
                boolean bl2 = $$14 = $$5 > $$12;
                if (($$14 || $$13) && !($$15 = this.globalFluidPicker.computeFluid($$9, $$12, $$10)).at($$12).isAir()) {
                    if ($$13) {
                        $$7 = true;
                    }
                    if ($$14) {
                        return $$15;
                    }
                }
                $$4 = Math.min((int)$$4, (int)$$11);
            }
            int $$16 = this.computeSurfaceLevel($$0, $$1, $$2, $$3, $$4, $$7);
            return new FluidStatus($$16, this.computeFluidType($$0, $$1, $$2, $$3, $$16));
        }

        private int computeSurfaceLevel(int $$0, int $$1, int $$2, FluidStatus $$3, int $$4, boolean $$5) {
            int $$19;
            double $$16;
            double $$15;
            DensityFunction.SinglePointContext $$6 = new DensityFunction.SinglePointContext($$0, $$1, $$2);
            if (OverworldBiomeBuilder.isDeepDarkRegion(this.erosion, this.depth, $$6)) {
                double $$7 = -1.0;
                double $$8 = -1.0;
            } else {
                int $$9 = $$4 + 8 - $$1;
                int $$10 = 64;
                double $$11 = $$5 ? Mth.clampedMap((double)$$9, 0.0, 64.0, 1.0, 0.0) : 0.0;
                double $$12 = Mth.clamp(this.fluidLevelFloodednessNoise.compute($$6), -1.0, 1.0);
                double $$13 = Mth.map($$11, 1.0, 0.0, -0.3, 0.8);
                double $$14 = Mth.map($$11, 1.0, 0.0, -0.8, 0.4);
                $$15 = $$12 - $$14;
                $$16 = $$12 - $$13;
            }
            if ($$16 > 0.0) {
                int $$17 = $$3.fluidLevel;
            } else if ($$15 > 0.0) {
                int $$18 = this.computeRandomizedFluidSurfaceLevel($$0, $$1, $$2, $$4);
            } else {
                $$19 = DimensionType.WAY_BELOW_MIN_Y;
            }
            return $$19;
        }

        private int computeRandomizedFluidSurfaceLevel(int $$0, int $$1, int $$2, int $$3) {
            int $$4 = 16;
            int $$5 = 40;
            int $$6 = Math.floorDiv((int)$$0, (int)16);
            int $$7 = Math.floorDiv((int)$$1, (int)40);
            int $$8 = Math.floorDiv((int)$$2, (int)16);
            int $$9 = $$7 * 40 + 20;
            int $$10 = 10;
            double $$11 = this.fluidLevelSpreadNoise.compute(new DensityFunction.SinglePointContext($$6, $$7, $$8)) * 10.0;
            int $$12 = Mth.quantize($$11, 3);
            int $$13 = $$9 + $$12;
            return Math.min((int)$$3, (int)$$13);
        }

        private BlockState computeFluidType(int $$0, int $$1, int $$2, FluidStatus $$3, int $$4) {
            BlockState $$5 = $$3.fluidType;
            if ($$4 <= -10 && $$4 != DimensionType.WAY_BELOW_MIN_Y && $$3.fluidType != Blocks.LAVA.defaultBlockState()) {
                int $$10;
                int $$9;
                int $$6 = 64;
                int $$7 = 40;
                int $$8 = Math.floorDiv((int)$$0, (int)64);
                double $$11 = this.lavaNoise.compute(new DensityFunction.SinglePointContext($$8, $$9 = Math.floorDiv((int)$$1, (int)40), $$10 = Math.floorDiv((int)$$2, (int)64)));
                if (Math.abs((double)$$11) > 0.3) {
                    $$5 = Blocks.LAVA.defaultBlockState();
                }
            }
            return $$5;
        }
    }

    public static interface FluidPicker {
        public FluidStatus computeFluid(int var1, int var2, int var3);
    }

    public static final class FluidStatus {
        final int fluidLevel;
        final BlockState fluidType;

        public FluidStatus(int $$0, BlockState $$1) {
            this.fluidLevel = $$0;
            this.fluidType = $$1;
        }

        public BlockState at(int $$0) {
            return $$0 < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
        }
    }
}