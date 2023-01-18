/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
    public static final float GLOBAL_OFFSET = -0.50375f;
    private static final float ORE_THICKNESS = 0.08f;
    private static final double VEININESS_FREQUENCY = 1.5;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5;
    private static final double SURFACE_DENSITY_THRESHOLD = 1.5625;
    private static final double CHEESE_NOISE_TARGET = -0.703125;
    public static final int ISLAND_CHUNK_DISTANCE = 64;
    public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = NoiseRouterData.createKey("zero");
    private static final ResourceKey<DensityFunction> Y = NoiseRouterData.createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = NoiseRouterData.createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = NoiseRouterData.createKey("shift_z");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = NoiseRouterData.createKey("overworld/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = NoiseRouterData.createKey("nether/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = NoiseRouterData.createKey("end/base_3d_noise");
    public static final ResourceKey<DensityFunction> CONTINENTS = NoiseRouterData.createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = NoiseRouterData.createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> RIDGES = NoiseRouterData.createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = NoiseRouterData.createKey("overworld/ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = NoiseRouterData.createKey("overworld/offset");
    public static final ResourceKey<DensityFunction> FACTOR = NoiseRouterData.createKey("overworld/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = NoiseRouterData.createKey("overworld/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = NoiseRouterData.createKey("overworld/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = NoiseRouterData.createKey("overworld/sloped_cheese");
    public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = NoiseRouterData.createKey("overworld_large_biomes/continents");
    public static final ResourceKey<DensityFunction> EROSION_LARGE = NoiseRouterData.createKey("overworld_large_biomes/erosion");
    private static final ResourceKey<DensityFunction> OFFSET_LARGE = NoiseRouterData.createKey("overworld_large_biomes/offset");
    private static final ResourceKey<DensityFunction> FACTOR_LARGE = NoiseRouterData.createKey("overworld_large_biomes/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_LARGE = NoiseRouterData.createKey("overworld_large_biomes/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_LARGE = NoiseRouterData.createKey("overworld_large_biomes/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = NoiseRouterData.createKey("overworld_large_biomes/sloped_cheese");
    private static final ResourceKey<DensityFunction> OFFSET_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/offset");
    private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/sloped_cheese");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = NoiseRouterData.createKey("end/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = NoiseRouterData.createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = NoiseRouterData.createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = NoiseRouterData.createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = NoiseRouterData.createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = NoiseRouterData.createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = NoiseRouterData.createKey("overworld/caves/spaghetti_2d");

    private static ResourceKey<DensityFunction> createKey(String $$0) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation($$0));
    }

    public static Holder<? extends DensityFunction> bootstrap(BootstapContext<DensityFunction> $$0) {
        HolderGetter<NormalNoise.NoiseParameters> $$1 = $$0.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> $$2 = $$0.lookup(Registries.DENSITY_FUNCTION);
        $$0.register(ZERO, DensityFunctions.zero());
        int $$3 = DimensionType.MIN_Y * 2;
        int $$4 = DimensionType.MAX_Y * 2;
        $$0.register(Y, DensityFunctions.yClampedGradient($$3, $$4, $$3, $$4));
        DensityFunction $$5 = NoiseRouterData.registerAndWrap($$0, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA($$1.getOrThrow(Noises.SHIFT)))));
        DensityFunction $$6 = NoiseRouterData.registerAndWrap($$0, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB($$1.getOrThrow(Noises.SHIFT)))));
        $$0.register(BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded(0.25, 0.125, 80.0, 160.0, 8.0));
        $$0.register(BASE_3D_NOISE_NETHER, BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 60.0, 8.0));
        $$0.register(BASE_3D_NOISE_END, BlendedNoise.createUnseeded(0.25, 0.25, 80.0, 160.0, 4.0));
        Holder.Reference<DensityFunction> $$7 = $$0.register(CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, 0.25, $$1.getOrThrow(Noises.CONTINENTALNESS))));
        Holder.Reference<DensityFunction> $$8 = $$0.register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, 0.25, $$1.getOrThrow(Noises.EROSION))));
        DensityFunction $$9 = NoiseRouterData.registerAndWrap($$0, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, 0.25, $$1.getOrThrow(Noises.RIDGE))));
        $$0.register(RIDGES_FOLDED, NoiseRouterData.peaksAndValleys($$9));
        DensityFunction $$10 = DensityFunctions.noise($$1.getOrThrow(Noises.JAGGED), 1500.0, 0.0);
        NoiseRouterData.registerTerrainNoises($$0, $$2, $$10, $$7, $$8, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);
        Holder.Reference<DensityFunction> $$11 = $$0.register(CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, 0.25, $$1.getOrThrow(Noises.CONTINENTALNESS_LARGE))));
        Holder.Reference<DensityFunction> $$12 = $$0.register(EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, 0.25, $$1.getOrThrow(Noises.EROSION_LARGE))));
        NoiseRouterData.registerTerrainNoises($$0, $$2, $$10, $$11, $$12, OFFSET_LARGE, FACTOR_LARGE, JAGGEDNESS_LARGE, DEPTH_LARGE, SLOPED_CHEESE_LARGE, false);
        NoiseRouterData.registerTerrainNoises($$0, $$2, $$10, $$7, $$8, OFFSET_AMPLIFIED, FACTOR_AMPLIFIED, JAGGEDNESS_AMPLIFIED, DEPTH_AMPLIFIED, SLOPED_CHEESE_AMPLIFIED, true);
        $$0.register(SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), NoiseRouterData.getFunction($$2, BASE_3D_NOISE_END)));
        $$0.register(SPAGHETTI_ROUGHNESS_FUNCTION, NoiseRouterData.spaghettiRoughnessFunction($$1));
        $$0.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise($$1.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        $$0.register(SPAGHETTI_2D, NoiseRouterData.spaghetti2D($$2, $$1));
        $$0.register(ENTRANCES, NoiseRouterData.entrances($$2, $$1));
        $$0.register(NOODLE, NoiseRouterData.noodle($$2, $$1));
        return $$0.register(PILLARS, NoiseRouterData.pillars($$1));
    }

    private static void registerTerrainNoises(BootstapContext<DensityFunction> $$0, HolderGetter<DensityFunction> $$1, DensityFunction $$2, Holder<DensityFunction> $$3, Holder<DensityFunction> $$4, ResourceKey<DensityFunction> $$5, ResourceKey<DensityFunction> $$6, ResourceKey<DensityFunction> $$7, ResourceKey<DensityFunction> $$8, ResourceKey<DensityFunction> $$9, boolean $$10) {
        DensityFunctions.Spline.Coordinate $$11 = new DensityFunctions.Spline.Coordinate($$3);
        DensityFunctions.Spline.Coordinate $$12 = new DensityFunctions.Spline.Coordinate($$4);
        DensityFunctions.Spline.Coordinate $$13 = new DensityFunctions.Spline.Coordinate($$1.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate $$14 = new DensityFunctions.Spline.Coordinate($$1.getOrThrow(RIDGES_FOLDED));
        DensityFunction $$15 = NoiseRouterData.registerAndWrap($$0, $$5, NoiseRouterData.splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.50375f), DensityFunctions.spline(TerrainProvider.overworldOffset($$11, $$12, $$14, $$10))), DensityFunctions.blendOffset()));
        DensityFunction $$16 = NoiseRouterData.registerAndWrap($$0, $$6, NoiseRouterData.splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor($$11, $$12, $$13, $$14, $$10)), BLENDING_FACTOR));
        DensityFunction $$17 = NoiseRouterData.registerAndWrap($$0, $$8, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), $$15));
        DensityFunction $$18 = NoiseRouterData.registerAndWrap($$0, $$7, NoiseRouterData.splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness($$11, $$12, $$13, $$14, $$10)), BLENDING_JAGGEDNESS));
        DensityFunction $$19 = DensityFunctions.mul($$18, $$2.halfNegative());
        DensityFunction $$20 = NoiseRouterData.noiseGradientDensity($$16, DensityFunctions.add($$17, $$19));
        $$0.register($$9, DensityFunctions.add($$20, NoiseRouterData.getFunction($$1, BASE_3D_NOISE_OVERWORLD)));
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> $$0, ResourceKey<DensityFunction> $$1, DensityFunction $$2) {
        return new DensityFunctions.HolderHolder($$0.register($$1, $$2));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> $$0, ResourceKey<DensityFunction> $$1) {
        return new DensityFunctions.HolderHolder($$0.getOrThrow($$1));
    }

    private static DensityFunction peaksAndValleys(DensityFunction $$0) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add($$0.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
    }

    public static float peaksAndValleys(float $$0) {
        return -(Math.abs((float)(Math.abs((float)$$0) - 0.6666667f)) - 0.33333334f) * 3.0f;
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> $$0) {
        DensityFunction $$1 = DensityFunctions.noise($$0.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction $$2 = DensityFunctions.mappedNoise($$0.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul($$2, DensityFunctions.add($$1.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction entrances(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        DensityFunction $$2 = DensityFunctions.cacheOnce(DensityFunctions.noise($$1.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction $$3 = DensityFunctions.mappedNoise($$1.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction $$4 = DensityFunctions.weirdScaledSampler($$2, $$1.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction $$5 = DensityFunctions.weirdScaledSampler($$2, $$1.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction $$6 = DensityFunctions.add(DensityFunctions.max($$4, $$5), $$3).clamp(-1.0, 1.0);
        DensityFunction $$7 = NoiseRouterData.getFunction($$0, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction $$8 = DensityFunctions.noise($$1.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction $$9 = DensityFunctions.add(DensityFunctions.add($$8, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctions.cacheOnce(DensityFunctions.min($$9, DensityFunctions.add($$7, $$6)));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        DensityFunction $$2 = NoiseRouterData.getFunction($$0, Y);
        int $$3 = -64;
        int $$4 = -60;
        int $$5 = 320;
        DensityFunction $$6 = NoiseRouterData.yLimitedInterpolatable($$2, DensityFunctions.noise($$1.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction $$7 = NoiseRouterData.yLimitedInterpolatable($$2, DensityFunctions.mappedNoise($$1.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        double $$8 = 2.6666666666666665;
        DensityFunction $$9 = NoiseRouterData.yLimitedInterpolatable($$2, DensityFunctions.noise($$1.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction $$10 = NoiseRouterData.yLimitedInterpolatable($$2, DensityFunctions.noise($$1.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction $$11 = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max($$9.abs(), $$10.abs()));
        return DensityFunctions.rangeChoice($$6, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add($$7, $$11));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> $$0) {
        double $$1 = 25.0;
        double $$2 = 0.3;
        DensityFunction $$3 = DensityFunctions.noise($$0.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction $$4 = DensityFunctions.mappedNoise($$0.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction $$5 = DensityFunctions.mappedNoise($$0.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction $$6 = DensityFunctions.add(DensityFunctions.mul($$3, DensityFunctions.constant(2.0)), $$4);
        return DensityFunctions.cacheOnce(DensityFunctions.mul($$6, $$5.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        DensityFunction $$2 = DensityFunctions.noise($$1.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction $$3 = DensityFunctions.weirdScaledSampler($$2, $$1.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction $$4 = DensityFunctions.mappedNoise($$1.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv((int)-64, (int)8), 8.0);
        DensityFunction $$5 = NoiseRouterData.getFunction($$0, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction $$6 = DensityFunctions.add($$4, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction $$7 = DensityFunctions.add($$6, $$5).cube();
        double $$8 = 0.083;
        DensityFunction $$9 = DensityFunctions.add($$3, DensityFunctions.mul(DensityFunctions.constant(0.083), $$5));
        return DensityFunctions.max($$9, $$7).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1, DensityFunction $$2) {
        DensityFunction $$3 = NoiseRouterData.getFunction($$0, SPAGHETTI_2D);
        DensityFunction $$4 = NoiseRouterData.getFunction($$0, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction $$5 = DensityFunctions.noise($$1.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction $$6 = DensityFunctions.mul(DensityFunctions.constant(4.0), $$5.square());
        DensityFunction $$7 = DensityFunctions.noise($$1.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction $$8 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), $$7).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), $$2)).clamp(0.0, 0.5));
        DensityFunction $$9 = DensityFunctions.add($$6, $$8);
        DensityFunction $$10 = DensityFunctions.min(DensityFunctions.min($$9, NoiseRouterData.getFunction($$0, ENTRANCES)), DensityFunctions.add($$3, $$4));
        DensityFunction $$11 = NoiseRouterData.getFunction($$0, PILLARS);
        DensityFunction $$12 = DensityFunctions.rangeChoice($$11, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), $$11);
        return DensityFunctions.max($$10, $$12);
    }

    private static DensityFunction postProcess(DensityFunction $$0) {
        DensityFunction $$1 = DensityFunctions.blendDensity($$0);
        return DensityFunctions.mul(DensityFunctions.interpolated($$1), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> $$02, HolderGetter<NormalNoise.NoiseParameters> $$1, boolean $$2, boolean $$3) {
        DensityFunction $$4 = DensityFunctions.noise($$1.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction $$5 = DensityFunctions.noise($$1.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction $$6 = DensityFunctions.noise($$1.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction $$7 = DensityFunctions.noise($$1.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction $$8 = NoiseRouterData.getFunction($$02, SHIFT_X);
        DensityFunction $$9 = NoiseRouterData.getFunction($$02, SHIFT_Z);
        DensityFunction $$10 = DensityFunctions.shiftedNoise2d($$8, $$9, 0.25, $$1.getOrThrow($$2 ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction $$11 = DensityFunctions.shiftedNoise2d($$8, $$9, 0.25, $$1.getOrThrow($$2 ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction $$12 = NoiseRouterData.getFunction($$02, $$2 ? FACTOR_LARGE : ($$3 ? FACTOR_AMPLIFIED : FACTOR));
        DensityFunction $$13 = NoiseRouterData.getFunction($$02, $$2 ? DEPTH_LARGE : ($$3 ? DEPTH_AMPLIFIED : DEPTH));
        DensityFunction $$14 = NoiseRouterData.noiseGradientDensity(DensityFunctions.cache2d($$12), $$13);
        DensityFunction $$15 = NoiseRouterData.getFunction($$02, $$2 ? SLOPED_CHEESE_LARGE : ($$3 ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
        DensityFunction $$16 = DensityFunctions.min($$15, DensityFunctions.mul(DensityFunctions.constant(5.0), NoiseRouterData.getFunction($$02, ENTRANCES)));
        DensityFunction $$17 = DensityFunctions.rangeChoice($$15, -1000000.0, 1.5625, $$16, NoiseRouterData.underground($$02, $$1, $$15));
        DensityFunction $$18 = DensityFunctions.min(NoiseRouterData.postProcess(NoiseRouterData.slideOverworld($$3, $$17)), NoiseRouterData.getFunction($$02, NOODLE));
        DensityFunction $$19 = NoiseRouterData.getFunction($$02, Y);
        int $$20 = Stream.of((Object[])OreVeinifier.VeinType.values()).mapToInt($$0 -> $$0.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int $$21 = Stream.of((Object[])OreVeinifier.VeinType.values()).mapToInt($$0 -> $$0.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction $$22 = NoiseRouterData.yLimitedInterpolatable($$19, DensityFunctions.noise($$1.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), $$20, $$21, 0);
        float $$23 = 4.0f;
        DensityFunction $$24 = NoiseRouterData.yLimitedInterpolatable($$19, DensityFunctions.noise($$1.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), $$20, $$21, 0).abs();
        DensityFunction $$25 = NoiseRouterData.yLimitedInterpolatable($$19, DensityFunctions.noise($$1.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), $$20, $$21, 0).abs();
        DensityFunction $$26 = DensityFunctions.add(DensityFunctions.constant(-0.08f), DensityFunctions.max($$24, $$25));
        DensityFunction $$27 = DensityFunctions.noise($$1.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter($$4, $$5, $$6, $$7, $$10, $$11, NoiseRouterData.getFunction($$02, $$2 ? CONTINENTS_LARGE : CONTINENTS), NoiseRouterData.getFunction($$02, $$2 ? EROSION_LARGE : EROSION), $$13, NoiseRouterData.getFunction($$02, RIDGES), NoiseRouterData.slideOverworld($$3, DensityFunctions.add($$14, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)), $$18, $$22, $$26, $$27);
    }

    private static NoiseRouter noNewCaves(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1, DensityFunction $$2) {
        DensityFunction $$3 = NoiseRouterData.getFunction($$0, SHIFT_X);
        DensityFunction $$4 = NoiseRouterData.getFunction($$0, SHIFT_Z);
        DensityFunction $$5 = DensityFunctions.shiftedNoise2d($$3, $$4, 0.25, $$1.getOrThrow(Noises.TEMPERATURE));
        DensityFunction $$6 = DensityFunctions.shiftedNoise2d($$3, $$4, 0.25, $$1.getOrThrow(Noises.VEGETATION));
        DensityFunction $$7 = NoiseRouterData.postProcess($$2);
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$5, $$6, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$7, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction slideOverworld(boolean $$0, DensityFunction $$1) {
        return NoiseRouterData.slide($$1, -64, 384, $$0 ? 16 : 80, $$0 ? 0 : 64, -0.078125, 0, 24, $$0 ? 0.4 : 0.1171875);
    }

    private static DensityFunction slideNetherLike(HolderGetter<DensityFunction> $$0, int $$1, int $$2) {
        return NoiseRouterData.slide(NoiseRouterData.getFunction($$0, BASE_3D_NOISE_NETHER), $$1, $$2, 24, 0, 0.9375, -8, 24, 2.5);
    }

    private static DensityFunction slideEndLike(DensityFunction $$0, int $$1, int $$2) {
        return NoiseRouterData.slide($$0, $$1, $$2, 72, -184, -23.4375, 4, 32, -0.234375);
    }

    protected static NoiseRouter nether(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        return NoiseRouterData.noNewCaves($$0, $$1, NoiseRouterData.slideNetherLike($$0, 0, 128));
    }

    protected static NoiseRouter caves(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        return NoiseRouterData.noNewCaves($$0, $$1, NoiseRouterData.slideNetherLike($$0, -64, 192));
    }

    protected static NoiseRouter floatingIslands(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1) {
        return NoiseRouterData.noNewCaves($$0, $$1, NoiseRouterData.slideEndLike(NoiseRouterData.getFunction($$0, BASE_3D_NOISE_END), 0, 256));
    }

    private static DensityFunction slideEnd(DensityFunction $$0) {
        return NoiseRouterData.slideEndLike($$0, 0, 128);
    }

    protected static NoiseRouter end(HolderGetter<DensityFunction> $$0) {
        DensityFunction $$1 = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
        DensityFunction $$2 = NoiseRouterData.postProcess(NoiseRouterData.slideEnd(NoiseRouterData.getFunction($$0, SLOPED_CHEESE_END)));
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$1, DensityFunctions.zero(), DensityFunctions.zero(), NoiseRouterData.slideEnd(DensityFunctions.add($$1, DensityFunctions.constant(-0.703125))), $$2, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    protected static NoiseRouter none() {
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction splineWithBlending(DensityFunction $$0, DensityFunction $$1) {
        DensityFunction $$2 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), $$1, $$0);
        return DensityFunctions.flatCache(DensityFunctions.cache2d($$2));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction $$0, DensityFunction $$1) {
        DensityFunction $$2 = DensityFunctions.mul($$1, $$0);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), $$2.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction $$0, DensityFunction $$1, int $$2, int $$3, int $$4) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice($$0, $$2, $$3 + 1, $$1, DensityFunctions.constant($$4)));
    }

    private static DensityFunction slide(DensityFunction $$0, int $$1, int $$2, int $$3, int $$4, double $$5, int $$6, int $$7, double $$8) {
        DensityFunction $$9 = $$0;
        DensityFunction $$10 = DensityFunctions.yClampedGradient($$1 + $$2 - $$3, $$1 + $$2 - $$4, 1.0, 0.0);
        $$9 = DensityFunctions.lerp($$10, $$5, $$9);
        DensityFunction $$11 = DensityFunctions.yClampedGradient($$1 + $$6, $$1 + $$7, 0.0, 1.0);
        $$9 = DensityFunctions.lerp($$11, $$8, $$9);
        return $$9;
    }

    protected static final class QuantizedSpaghettiRarity {
        protected QuantizedSpaghettiRarity() {
        }

        protected static double getSphaghettiRarity2D(double $$0) {
            if ($$0 < -0.75) {
                return 0.5;
            }
            if ($$0 < -0.5) {
                return 0.75;
            }
            if ($$0 < 0.5) {
                return 1.0;
            }
            if ($$0 < 0.75) {
                return 2.0;
            }
            return 3.0;
        }

        protected static double getSpaghettiRarity3D(double $$0) {
            if ($$0 < -0.5) {
                return 0.75;
            }
            if ($$0 < 0.0) {
                return 1.0;
            }
            if ($$0 < 0.5) {
                return 1.5;
            }
            return 2.0;
        }
    }
}