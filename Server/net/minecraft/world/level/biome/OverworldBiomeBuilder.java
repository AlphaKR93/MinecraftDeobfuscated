/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public final class OverworldBiomeBuilder {
    private static final float VALLEY_SIZE = 0.05f;
    private static final float LOW_START = 0.26666668f;
    public static final float HIGH_START = 0.4f;
    private static final float HIGH_END = 0.93333334f;
    private static final float PEAK_SIZE = 0.1f;
    public static final float PEAK_START = 0.56666666f;
    private static final float PEAK_END = 0.7666667f;
    public static final float NEAR_INLAND_START = -0.11f;
    public static final float MID_INLAND_START = 0.03f;
    public static final float FAR_INLAND_START = 0.3f;
    public static final float EROSION_INDEX_1_START = -0.78f;
    public static final float EROSION_INDEX_2_START = -0.375f;
    private static final float EROSION_DEEP_DARK_DRYNESS_THRESHOLD = -0.225f;
    private static final float DEPTH_DEEP_DARK_DRYNESS_THRESHOLD = 0.9f;
    private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0f, 1.0f);
    private final Climate.Parameter[] temperatures = new Climate.Parameter[]{Climate.Parameter.span(-1.0f, -0.45f), Climate.Parameter.span(-0.45f, -0.15f), Climate.Parameter.span(-0.15f, 0.2f), Climate.Parameter.span(0.2f, 0.55f), Climate.Parameter.span(0.55f, 1.0f)};
    private final Climate.Parameter[] humidities = new Climate.Parameter[]{Climate.Parameter.span(-1.0f, -0.35f), Climate.Parameter.span(-0.35f, -0.1f), Climate.Parameter.span(-0.1f, 0.1f), Climate.Parameter.span(0.1f, 0.3f), Climate.Parameter.span(0.3f, 1.0f)};
    private final Climate.Parameter[] erosions = new Climate.Parameter[]{Climate.Parameter.span(-1.0f, -0.78f), Climate.Parameter.span(-0.78f, -0.375f), Climate.Parameter.span(-0.375f, -0.2225f), Climate.Parameter.span(-0.2225f, 0.05f), Climate.Parameter.span(0.05f, 0.45f), Climate.Parameter.span(0.45f, 0.55f), Climate.Parameter.span(0.55f, 1.0f)};
    private final Climate.Parameter FROZEN_RANGE = this.temperatures[0];
    private final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(this.temperatures[1], this.temperatures[4]);
    private final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2f, -1.05f);
    private final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05f, -0.455f);
    private final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455f, -0.19f);
    private final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19f, -0.11f);
    private final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11f, 0.55f);
    private final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11f, 0.03f);
    private final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03f, 0.3f);
    private final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3f, 1.0f);
    private final ResourceKey<Biome>[][] OCEANS = new ResourceKey[][]{{Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN}, {Biomes.FROZEN_OCEAN, Biomes.COLD_OCEAN, Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN}};
    private final ResourceKey<Biome>[][] MIDDLE_BIOMES = new ResourceKey[][]{{Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.TAIGA}, {Biomes.PLAINS, Biomes.PLAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA}, {Biomes.FLOWER_FOREST, Biomes.PLAINS, Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST}, {Biomes.SAVANNA, Biomes.SAVANNA, Biomes.FOREST, Biomes.JUNGLE, Biomes.JUNGLE}, {Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT}};
    private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT = new ResourceKey[][]{{Biomes.ICE_SPIKES, null, Biomes.SNOWY_TAIGA, null, null}, {null, null, null, null, Biomes.OLD_GROWTH_PINE_TAIGA}, {Biomes.SUNFLOWER_PLAINS, null, null, Biomes.OLD_GROWTH_BIRCH_FOREST, null}, {null, null, Biomes.PLAINS, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE}, {null, null, null, null, null}};
    private final ResourceKey<Biome>[][] PLATEAU_BIOMES = new ResourceKey[][]{{Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA}, {Biomes.MEADOW, Biomes.MEADOW, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA}, {Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.DARK_FOREST}, {Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA_PLATEAU, Biomes.FOREST, Biomes.FOREST, Biomes.JUNGLE}, {Biomes.BADLANDS, Biomes.BADLANDS, Biomes.BADLANDS, Biomes.WOODED_BADLANDS, Biomes.WOODED_BADLANDS}};
    private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT = new ResourceKey[][]{{Biomes.ICE_SPIKES, null, null, null, null}, {null, null, Biomes.MEADOW, Biomes.MEADOW, Biomes.OLD_GROWTH_PINE_TAIGA}, {null, null, Biomes.FOREST, Biomes.BIRCH_FOREST, null}, {null, null, null, null, null}, {Biomes.ERODED_BADLANDS, Biomes.ERODED_BADLANDS, null, null, null}};
    private final ResourceKey<Biome>[][] SHATTERED_BIOMES = new ResourceKey[][]{{Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {null, null, null, null, null}, {null, null, null, null, null}};

    public List<Climate.ParameterPoint> spawnTarget() {
        Climate.Parameter $$0 = Climate.Parameter.point(0.0f);
        float $$1 = 0.16f;
        return List.of((Object)((Object)new Climate.ParameterPoint(this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, $$0, Climate.Parameter.span(-1.0f, -0.16f), 0L)), (Object)((Object)new Climate.ParameterPoint(this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, $$0, Climate.Parameter.span(0.16f, 1.0f), 0L)));
    }

    protected void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0) {
        if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
            this.addDebugBiomes($$0);
            return;
        }
        this.addOffCoastBiomes($$0);
        this.addInlandBiomes($$0);
        this.addUndergroundBiomes($$0);
    }

    private void addDebugBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0) {
        CubicSpline $$10;
        HolderLookup.Provider $$1 = VanillaRegistries.createLookup();
        HolderLookup.RegistryLookup<DensityFunction> $$2 = $$1.lookupOrThrow(Registries.DENSITY_FUNCTION);
        DensityFunctions.Spline.Coordinate $$3 = new DensityFunctions.Spline.Coordinate($$2.getOrThrow(NoiseRouterData.CONTINENTS));
        DensityFunctions.Spline.Coordinate $$4 = new DensityFunctions.Spline.Coordinate($$2.getOrThrow(NoiseRouterData.EROSION));
        DensityFunctions.Spline.Coordinate $$5 = new DensityFunctions.Spline.Coordinate($$2.getOrThrow(NoiseRouterData.RIDGES_FOLDED));
        $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters(this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point(0.0f), this.FULL_RANGE, 0.01f)), Biomes.PLAINS));
        CubicSpline $$6 = TerrainProvider.buildErosionOffsetSpline($$4, $$5, -0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, ToFloatFunction.IDENTITY);
        if ($$6 instanceof CubicSpline.Multipoint) {
            CubicSpline.Multipoint $$7 = (CubicSpline.Multipoint)$$6;
            ResourceKey<Biome> $$8 = Biomes.DESERT;
            for (float $$9 : $$7.locations()) {
                $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters(this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point($$9), Climate.Parameter.point(0.0f), this.FULL_RANGE, 0.0f)), $$8));
                $$8 = $$8 == Biomes.DESERT ? Biomes.BADLANDS : Biomes.DESERT;
            }
        }
        if (($$10 = TerrainProvider.overworldOffset($$3, $$4, $$5, false)) instanceof CubicSpline.Multipoint) {
            CubicSpline.Multipoint $$11 = (CubicSpline.Multipoint)$$10;
            for (float $$12 : $$11.locations()) {
                $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters(this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point($$12), this.FULL_RANGE, Climate.Parameter.point(0.0f), this.FULL_RANGE, 0.0f)), Biomes.SNOWY_TAIGA));
            }
        }
    }

    private void addOffCoastBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0) {
        this.addSurfaceBiome($$0, this.FULL_RANGE, this.FULL_RANGE, this.mushroomFieldsContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0f, Biomes.MUSHROOM_FIELDS);
        for (int $$1 = 0; $$1 < this.temperatures.length; ++$$1) {
            Climate.Parameter $$2 = this.temperatures[$$1];
            this.addSurfaceBiome($$0, $$2, this.FULL_RANGE, this.deepOceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0f, this.OCEANS[0][$$1]);
            this.addSurfaceBiome($$0, $$2, this.FULL_RANGE, this.oceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0f, this.OCEANS[1][$$1]);
        }
    }

    private void addInlandBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0) {
        this.addMidSlice($$0, Climate.Parameter.span(-1.0f, -0.93333334f));
        this.addHighSlice($$0, Climate.Parameter.span(-0.93333334f, -0.7666667f));
        this.addPeaks($$0, Climate.Parameter.span(-0.7666667f, -0.56666666f));
        this.addHighSlice($$0, Climate.Parameter.span(-0.56666666f, -0.4f));
        this.addMidSlice($$0, Climate.Parameter.span(-0.4f, -0.26666668f));
        this.addLowSlice($$0, Climate.Parameter.span(-0.26666668f, -0.05f));
        this.addValleys($$0, Climate.Parameter.span(-0.05f, 0.05f));
        this.addLowSlice($$0, Climate.Parameter.span(0.05f, 0.26666668f));
        this.addMidSlice($$0, Climate.Parameter.span(0.26666668f, 0.4f));
        this.addHighSlice($$0, Climate.Parameter.span(0.4f, 0.56666666f));
        this.addPeaks($$0, Climate.Parameter.span(0.56666666f, 0.7666667f));
        this.addHighSlice($$0, Climate.Parameter.span(0.7666667f, 0.93333334f));
        this.addMidSlice($$0, Climate.Parameter.span(0.93333334f, 1.0f));
    }

    private void addPeaks(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1) {
        for (int $$2 = 0; $$2 < this.temperatures.length; ++$$2) {
            Climate.Parameter $$3 = this.temperatures[$$2];
            for (int $$4 = 0; $$4 < this.humidities.length; ++$$4) {
                Climate.Parameter $$5 = this.humidities[$$4];
                ResourceKey<Biome> $$6 = this.pickMiddleBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$7 = this.pickMiddleBiomeOrBadlandsIfHot($$2, $$4, $$1);
                ResourceKey<Biome> $$8 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold($$2, $$4, $$1);
                ResourceKey<Biome> $$9 = this.pickPlateauBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$10 = this.pickShatteredBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$11 = this.maybePickWindsweptSavannaBiome($$2, $$4, $$1, $$10);
                ResourceKey<Biome> $$12 = this.pickPeakBiome($$2, $$4, $$1);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[0], $$1, 0.0f, $$12);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[1], $$1, 0.0f, $$8);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], $$1, 0.0f, $$12);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], $$1, 0.0f, $$9);
                this.addSurfaceBiome($$0, $$3, $$5, this.midInlandContinentalness, this.erosions[3], $$1, 0.0f, $$7);
                this.addSurfaceBiome($$0, $$3, $$5, this.farInlandContinentalness, this.erosions[3], $$1, 0.0f, $$9);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], $$1, 0.0f, $$11);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], $$1, 0.0f, $$10);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, $$6);
            }
        }
    }

    private void addHighSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1) {
        for (int $$2 = 0; $$2 < this.temperatures.length; ++$$2) {
            Climate.Parameter $$3 = this.temperatures[$$2];
            for (int $$4 = 0; $$4 < this.humidities.length; ++$$4) {
                Climate.Parameter $$5 = this.humidities[$$4];
                ResourceKey<Biome> $$6 = this.pickMiddleBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$7 = this.pickMiddleBiomeOrBadlandsIfHot($$2, $$4, $$1);
                ResourceKey<Biome> $$8 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold($$2, $$4, $$1);
                ResourceKey<Biome> $$9 = this.pickPlateauBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$10 = this.pickShatteredBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$11 = this.maybePickWindsweptSavannaBiome($$2, $$4, $$1, $$6);
                ResourceKey<Biome> $$12 = this.pickSlopeBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$13 = this.pickPeakBiome($$2, $$4, $$1);
                this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, this.erosions[0], $$1, 0.0f, $$12);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[0], $$1, 0.0f, $$13);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, this.erosions[1], $$1, 0.0f, $$8);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], $$1, 0.0f, $$12);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], $$1, 0.0f, $$9);
                this.addSurfaceBiome($$0, $$3, $$5, this.midInlandContinentalness, this.erosions[3], $$1, 0.0f, $$7);
                this.addSurfaceBiome($$0, $$3, $$5, this.farInlandContinentalness, this.erosions[3], $$1, 0.0f, $$9);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], $$1, 0.0f, $$11);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], $$1, 0.0f, $$10);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, $$6);
            }
        }
    }

    private void addMidSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1) {
        this.addSurfaceBiome($$0, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), $$1, 0.0f, Biomes.STONY_SHORE);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[1], this.temperatures[2]), this.FULL_RANGE, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.SWAMP);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[3], this.temperatures[4]), this.FULL_RANGE, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.MANGROVE_SWAMP);
        for (int $$2 = 0; $$2 < this.temperatures.length; ++$$2) {
            Climate.Parameter $$3 = this.temperatures[$$2];
            for (int $$4 = 0; $$4 < this.humidities.length; ++$$4) {
                Climate.Parameter $$5 = this.humidities[$$4];
                ResourceKey<Biome> $$6 = this.pickMiddleBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$7 = this.pickMiddleBiomeOrBadlandsIfHot($$2, $$4, $$1);
                ResourceKey<Biome> $$8 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold($$2, $$4, $$1);
                ResourceKey<Biome> $$9 = this.pickShatteredBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$10 = this.pickPlateauBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$11 = this.pickBeachBiome($$2, $$4);
                ResourceKey<Biome> $$12 = this.maybePickWindsweptSavannaBiome($$2, $$4, $$1, $$6);
                ResourceKey<Biome> $$13 = this.pickShatteredCoastBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$14 = this.pickSlopeBiome($$2, $$4, $$1);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[0], $$1, 0.0f, $$14);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosions[1], $$1, 0.0f, $$8);
                this.addSurfaceBiome($$0, $$3, $$5, this.farInlandContinentalness, this.erosions[1], $$1, 0.0f, $$2 == 0 ? $$14 : $$10);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, this.erosions[2], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, this.midInlandContinentalness, this.erosions[2], $$1, 0.0f, $$7);
                this.addSurfaceBiome($$0, $$3, $$5, this.farInlandContinentalness, this.erosions[2], $$1, 0.0f, $$10);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[3], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[3], $$1, 0.0f, $$7);
                if ($$1.max() < 0L) {
                    this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[4], $$1, 0.0f, $$11);
                    this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], $$1, 0.0f, $$6);
                } else {
                    this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], $$1, 0.0f, $$6);
                }
                this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[5], $$1, 0.0f, $$13);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, this.erosions[5], $$1, 0.0f, $$12);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], $$1, 0.0f, $$9);
                if ($$1.max() < 0L) {
                    this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[6], $$1, 0.0f, $$11);
                } else {
                    this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[6], $$1, 0.0f, $$6);
                }
                if ($$2 != 0) continue;
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, $$6);
            }
        }
    }

    private void addLowSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1) {
        this.addSurfaceBiome($$0, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), $$1, 0.0f, Biomes.STONY_SHORE);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[1], this.temperatures[2]), this.FULL_RANGE, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.SWAMP);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[3], this.temperatures[4]), this.FULL_RANGE, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.MANGROVE_SWAMP);
        for (int $$2 = 0; $$2 < this.temperatures.length; ++$$2) {
            Climate.Parameter $$3 = this.temperatures[$$2];
            for (int $$4 = 0; $$4 < this.humidities.length; ++$$4) {
                Climate.Parameter $$5 = this.humidities[$$4];
                ResourceKey<Biome> $$6 = this.pickMiddleBiome($$2, $$4, $$1);
                ResourceKey<Biome> $$7 = this.pickMiddleBiomeOrBadlandsIfHot($$2, $$4, $$1);
                ResourceKey<Biome> $$8 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold($$2, $$4, $$1);
                ResourceKey<Biome> $$9 = this.pickBeachBiome($$2, $$4);
                ResourceKey<Biome> $$10 = this.maybePickWindsweptSavannaBiome($$2, $$4, $$1, $$6);
                ResourceKey<Biome> $$11 = this.pickShatteredCoastBiome($$2, $$4, $$1);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$7);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$8);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[2], this.erosions[3]), $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), $$1, 0.0f, $$7);
                this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, Climate.Parameter.span(this.erosions[3], this.erosions[4]), $$1, 0.0f, $$9);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[5], $$1, 0.0f, $$11);
                this.addSurfaceBiome($$0, $$3, $$5, this.nearInlandContinentalness, this.erosions[5], $$1, 0.0f, $$10);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], $$1, 0.0f, $$6);
                this.addSurfaceBiome($$0, $$3, $$5, this.coastContinentalness, this.erosions[6], $$1, 0.0f, $$9);
                if ($$2 != 0) continue;
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, $$6);
            }
        }
    }

    private void addValleys(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1) {
        this.addSurfaceBiome($$0, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$1.max() < 0L ? Biomes.STONY_SHORE : Biomes.FROZEN_RIVER);
        this.addSurfaceBiome($$0, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$1.max() < 0L ? Biomes.STONY_SHORE : Biomes.RIVER);
        this.addSurfaceBiome($$0, this.FROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome($$0, this.UNFROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, Biomes.RIVER);
        this.addSurfaceBiome($$0, this.FROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[5]), $$1, 0.0f, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome($$0, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[5]), $$1, 0.0f, Biomes.RIVER);
        this.addSurfaceBiome($$0, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], $$1, 0.0f, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome($$0, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], $$1, 0.0f, Biomes.RIVER);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[1], this.temperatures[2]), this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.SWAMP);
        this.addSurfaceBiome($$0, Climate.Parameter.span(this.temperatures[3], this.temperatures[4]), this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.MANGROVE_SWAMP);
        this.addSurfaceBiome($$0, this.FROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], $$1, 0.0f, Biomes.FROZEN_RIVER);
        for (int $$2 = 0; $$2 < this.temperatures.length; ++$$2) {
            Climate.Parameter $$3 = this.temperatures[$$2];
            for (int $$4 = 0; $$4 < this.humidities.length; ++$$4) {
                Climate.Parameter $$5 = this.humidities[$$4];
                ResourceKey<Biome> $$6 = this.pickMiddleBiomeOrBadlandsIfHot($$2, $$4, $$1);
                this.addSurfaceBiome($$0, $$3, $$5, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), $$1, 0.0f, $$6);
            }
        }
    }

    private void addUndergroundBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0) {
        this.addUndergroundBiome($$0, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(0.8f, 1.0f), this.FULL_RANGE, this.FULL_RANGE, 0.0f, Biomes.DRIPSTONE_CAVES);
        this.addUndergroundBiome($$0, this.FULL_RANGE, Climate.Parameter.span(0.7f, 1.0f), this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0f, Biomes.LUSH_CAVES);
        this.addBottomBiome($$0, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.erosions[0], this.erosions[1]), this.FULL_RANGE, 0.0f, Biomes.DEEP_DARK);
    }

    private ResourceKey<Biome> pickMiddleBiome(int $$0, int $$1, Climate.Parameter $$2) {
        if ($$2.max() < 0L) {
            return this.MIDDLE_BIOMES[$$0][$$1];
        }
        ResourceKey<Biome> $$3 = this.MIDDLE_BIOMES_VARIANT[$$0][$$1];
        return $$3 == null ? this.MIDDLE_BIOMES[$$0][$$1] : $$3;
    }

    private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHot(int $$0, int $$1, Climate.Parameter $$2) {
        return $$0 == 4 ? this.pickBadlandsBiome($$1, $$2) : this.pickMiddleBiome($$0, $$1, $$2);
    }

    private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int $$0, int $$1, Climate.Parameter $$2) {
        return $$0 == 0 ? this.pickSlopeBiome($$0, $$1, $$2) : this.pickMiddleBiomeOrBadlandsIfHot($$0, $$1, $$2);
    }

    private ResourceKey<Biome> maybePickWindsweptSavannaBiome(int $$0, int $$1, Climate.Parameter $$2, ResourceKey<Biome> $$3) {
        if ($$0 > 1 && $$1 < 4 && $$2.max() >= 0L) {
            return Biomes.WINDSWEPT_SAVANNA;
        }
        return $$3;
    }

    private ResourceKey<Biome> pickShatteredCoastBiome(int $$0, int $$1, Climate.Parameter $$2) {
        ResourceKey<Biome> $$3 = $$2.max() >= 0L ? this.pickMiddleBiome($$0, $$1, $$2) : this.pickBeachBiome($$0, $$1);
        return this.maybePickWindsweptSavannaBiome($$0, $$1, $$2, $$3);
    }

    private ResourceKey<Biome> pickBeachBiome(int $$0, int $$1) {
        if ($$0 == 0) {
            return Biomes.SNOWY_BEACH;
        }
        if ($$0 == 4) {
            return Biomes.DESERT;
        }
        return Biomes.BEACH;
    }

    private ResourceKey<Biome> pickBadlandsBiome(int $$0, Climate.Parameter $$1) {
        if ($$0 < 2) {
            return $$1.max() < 0L ? Biomes.BADLANDS : Biomes.ERODED_BADLANDS;
        }
        if ($$0 < 3) {
            return Biomes.BADLANDS;
        }
        return Biomes.WOODED_BADLANDS;
    }

    private ResourceKey<Biome> pickPlateauBiome(int $$0, int $$1, Climate.Parameter $$2) {
        if ($$2.max() < 0L) {
            return this.PLATEAU_BIOMES[$$0][$$1];
        }
        ResourceKey<Biome> $$3 = this.PLATEAU_BIOMES_VARIANT[$$0][$$1];
        return $$3 == null ? this.PLATEAU_BIOMES[$$0][$$1] : $$3;
    }

    private ResourceKey<Biome> pickPeakBiome(int $$0, int $$1, Climate.Parameter $$2) {
        if ($$0 <= 2) {
            return $$2.max() < 0L ? Biomes.JAGGED_PEAKS : Biomes.FROZEN_PEAKS;
        }
        if ($$0 == 3) {
            return Biomes.STONY_PEAKS;
        }
        return this.pickBadlandsBiome($$1, $$2);
    }

    private ResourceKey<Biome> pickSlopeBiome(int $$0, int $$1, Climate.Parameter $$2) {
        if ($$0 >= 3) {
            return this.pickPlateauBiome($$0, $$1, $$2);
        }
        if ($$1 <= 1) {
            return Biomes.SNOWY_SLOPES;
        }
        return Biomes.GROVE;
    }

    private ResourceKey<Biome> pickShatteredBiome(int $$0, int $$1, Climate.Parameter $$2) {
        ResourceKey<Biome> $$3 = this.SHATTERED_BIOMES[$$0][$$1];
        return $$3 == null ? this.pickMiddleBiome($$0, $$1, $$2) : $$3;
    }

    private void addSurfaceBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1, Climate.Parameter $$2, Climate.Parameter $$3, Climate.Parameter $$4, Climate.Parameter $$5, float $$6, ResourceKey<Biome> $$7) {
        $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters($$1, $$2, $$3, $$4, Climate.Parameter.point(0.0f), $$5, $$6)), $$7));
        $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters($$1, $$2, $$3, $$4, Climate.Parameter.point(1.0f), $$5, $$6)), $$7));
    }

    private void addUndergroundBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1, Climate.Parameter $$2, Climate.Parameter $$3, Climate.Parameter $$4, Climate.Parameter $$5, float $$6, ResourceKey<Biome> $$7) {
        $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters($$1, $$2, $$3, $$4, Climate.Parameter.span(0.2f, 0.9f), $$5, $$6)), $$7));
    }

    private void addBottomBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> $$0, Climate.Parameter $$1, Climate.Parameter $$2, Climate.Parameter $$3, Climate.Parameter $$4, Climate.Parameter $$5, float $$6, ResourceKey<Biome> $$7) {
        $$0.accept((Object)Pair.of((Object)((Object)Climate.parameters($$1, $$2, $$3, $$4, Climate.Parameter.point(1.1f), $$5, $$6)), $$7));
    }

    public static boolean isDeepDarkRegion(DensityFunction $$0, DensityFunction $$1, DensityFunction.FunctionContext $$2) {
        return $$0.compute($$2) < (double)-0.225f && $$1.compute($$2) > (double)0.9f;
    }

    public static String getDebugStringForPeaksAndValleys(double $$0) {
        if ($$0 < (double)NoiseRouterData.peaksAndValleys(0.05f)) {
            return "Valley";
        }
        if ($$0 < (double)NoiseRouterData.peaksAndValleys(0.26666668f)) {
            return "Low";
        }
        if ($$0 < (double)NoiseRouterData.peaksAndValleys(0.4f)) {
            return "Mid";
        }
        if ($$0 < (double)NoiseRouterData.peaksAndValleys(0.56666666f)) {
            return "High";
        }
        return "Peak";
    }

    public String getDebugStringForContinentalness(double $$0) {
        double $$1 = Climate.quantizeCoord((float)$$0);
        if ($$1 < (double)this.mushroomFieldsContinentalness.max()) {
            return "Mushroom fields";
        }
        if ($$1 < (double)this.deepOceanContinentalness.max()) {
            return "Deep ocean";
        }
        if ($$1 < (double)this.oceanContinentalness.max()) {
            return "Ocean";
        }
        if ($$1 < (double)this.coastContinentalness.max()) {
            return "Coast";
        }
        if ($$1 < (double)this.nearInlandContinentalness.max()) {
            return "Near inland";
        }
        if ($$1 < (double)this.midInlandContinentalness.max()) {
            return "Mid inland";
        }
        return "Far inland";
    }

    public String getDebugStringForErosion(double $$0) {
        return OverworldBiomeBuilder.getDebugStringForNoiseValue($$0, this.erosions);
    }

    public String getDebugStringForTemperature(double $$0) {
        return OverworldBiomeBuilder.getDebugStringForNoiseValue($$0, this.temperatures);
    }

    public String getDebugStringForHumidity(double $$0) {
        return OverworldBiomeBuilder.getDebugStringForNoiseValue($$0, this.humidities);
    }

    private static String getDebugStringForNoiseValue(double $$0, Climate.Parameter[] $$1) {
        double $$2 = Climate.quantizeCoord((float)$$0);
        for (int $$3 = 0; $$3 < $$1.length; ++$$3) {
            if (!($$2 < (double)$$1[$$3].max())) continue;
            return "" + $$3;
        }
        return "?";
    }

    @VisibleForDebug
    public Climate.Parameter[] getTemperatureThresholds() {
        return this.temperatures;
    }

    @VisibleForDebug
    public Climate.Parameter[] getHumidityThresholds() {
        return this.humidities;
    }

    @VisibleForDebug
    public Climate.Parameter[] getErosionThresholds() {
        return this.erosions;
    }

    @VisibleForDebug
    public Climate.Parameter[] getContinentalnessThresholds() {
        return new Climate.Parameter[]{this.mushroomFieldsContinentalness, this.deepOceanContinentalness, this.oceanContinentalness, this.coastContinentalness, this.nearInlandContinentalness, this.midInlandContinentalness, this.farInlandContinentalness};
    }

    @VisibleForDebug
    public Climate.Parameter[] getPeaksAndValleysThresholds() {
        return new Climate.Parameter[]{Climate.Parameter.span(-2.0f, NoiseRouterData.peaksAndValleys(0.05f)), Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.05f), NoiseRouterData.peaksAndValleys(0.26666668f)), Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.26666668f), NoiseRouterData.peaksAndValleys(0.4f)), Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.4f), NoiseRouterData.peaksAndValleys(0.56666666f)), Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.56666666f), 2.0f)};
    }

    @VisibleForDebug
    public Climate.Parameter[] getWeirdnessThresholds() {
        return new Climate.Parameter[]{Climate.Parameter.span(-2.0f, 0.0f), Climate.Parameter.span(0.0f, 2.0f)};
    }
}