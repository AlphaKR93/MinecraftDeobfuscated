/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  java.lang.Object
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

public class VegetationPlacements {
    public static final ResourceKey<PlacedFeature> BAMBOO_LIGHT = PlacementUtils.createKey("bamboo_light");
    public static final ResourceKey<PlacedFeature> BAMBOO = PlacementUtils.createKey("bamboo");
    public static final ResourceKey<PlacedFeature> VINES = PlacementUtils.createKey("vines");
    public static final ResourceKey<PlacedFeature> PATCH_SUNFLOWER = PlacementUtils.createKey("patch_sunflower");
    public static final ResourceKey<PlacedFeature> PATCH_PUMPKIN = PlacementUtils.createKey("patch_pumpkin");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_PLAIN = PlacementUtils.createKey("patch_grass_plain");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_FOREST = PlacementUtils.createKey("patch_grass_forest");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_BADLANDS = PlacementUtils.createKey("patch_grass_badlands");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_SAVANNA = PlacementUtils.createKey("patch_grass_savanna");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_NORMAL = PlacementUtils.createKey("patch_grass_normal");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA_2 = PlacementUtils.createKey("patch_grass_taiga_2");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA = PlacementUtils.createKey("patch_grass_taiga");
    public static final ResourceKey<PlacedFeature> PATCH_GRASS_JUNGLE = PlacementUtils.createKey("patch_grass_jungle");
    public static final ResourceKey<PlacedFeature> GRASS_BONEMEAL = PlacementUtils.createKey("grass_bonemeal");
    public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_2 = PlacementUtils.createKey("patch_dead_bush_2");
    public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH = PlacementUtils.createKey("patch_dead_bush");
    public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS = PlacementUtils.createKey("patch_dead_bush_badlands");
    public static final ResourceKey<PlacedFeature> PATCH_MELON = PlacementUtils.createKey("patch_melon");
    public static final ResourceKey<PlacedFeature> PATCH_MELON_SPARSE = PlacementUtils.createKey("patch_melon_sparse");
    public static final ResourceKey<PlacedFeature> PATCH_BERRY_COMMON = PlacementUtils.createKey("patch_berry_common");
    public static final ResourceKey<PlacedFeature> PATCH_BERRY_RARE = PlacementUtils.createKey("patch_berry_rare");
    public static final ResourceKey<PlacedFeature> PATCH_WATERLILY = PlacementUtils.createKey("patch_waterlily");
    public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS_2 = PlacementUtils.createKey("patch_tall_grass_2");
    public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS = PlacementUtils.createKey("patch_tall_grass");
    public static final ResourceKey<PlacedFeature> PATCH_LARGE_FERN = PlacementUtils.createKey("patch_large_fern");
    public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DESERT = PlacementUtils.createKey("patch_cactus_desert");
    public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DECORATED = PlacementUtils.createKey("patch_cactus_decorated");
    public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_SWAMP = PlacementUtils.createKey("patch_sugar_cane_swamp");
    public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_DESERT = PlacementUtils.createKey("patch_sugar_cane_desert");
    public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS = PlacementUtils.createKey("patch_sugar_cane_badlands");
    public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE = PlacementUtils.createKey("patch_sugar_cane");
    public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NETHER = PlacementUtils.createKey("brown_mushroom_nether");
    public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NETHER = PlacementUtils.createKey("red_mushroom_nether");
    public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NORMAL = PlacementUtils.createKey("brown_mushroom_normal");
    public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NORMAL = PlacementUtils.createKey("red_mushroom_normal");
    public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_TAIGA = PlacementUtils.createKey("brown_mushroom_taiga");
    public static final ResourceKey<PlacedFeature> RED_MUSHROOM_TAIGA = PlacementUtils.createKey("red_mushroom_taiga");
    public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("brown_mushroom_old_growth");
    public static final ResourceKey<PlacedFeature> RED_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("red_mushroom_old_growth");
    public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_SWAMP = PlacementUtils.createKey("brown_mushroom_swamp");
    public static final ResourceKey<PlacedFeature> RED_MUSHROOM_SWAMP = PlacementUtils.createKey("red_mushroom_swamp");
    public static final ResourceKey<PlacedFeature> FLOWER_WARM = PlacementUtils.createKey("flower_warm");
    public static final ResourceKey<PlacedFeature> FLOWER_DEFAULT = PlacementUtils.createKey("flower_default");
    public static final ResourceKey<PlacedFeature> FLOWER_FLOWER_FOREST = PlacementUtils.createKey("flower_flower_forest");
    public static final ResourceKey<PlacedFeature> FLOWER_SWAMP = PlacementUtils.createKey("flower_swamp");
    public static final ResourceKey<PlacedFeature> FLOWER_PLAINS = PlacementUtils.createKey("flower_plains");
    public static final ResourceKey<PlacedFeature> FLOWER_MEADOW = PlacementUtils.createKey("flower_meadow");
    public static final ResourceKey<PlacedFeature> TREES_PLAINS = PlacementUtils.createKey("trees_plains");
    public static final ResourceKey<PlacedFeature> DARK_FOREST_VEGETATION = PlacementUtils.createKey("dark_forest_vegetation");
    public static final ResourceKey<PlacedFeature> FLOWER_FOREST_FLOWERS = PlacementUtils.createKey("flower_forest_flowers");
    public static final ResourceKey<PlacedFeature> FOREST_FLOWERS = PlacementUtils.createKey("forest_flowers");
    public static final ResourceKey<PlacedFeature> TREES_FLOWER_FOREST = PlacementUtils.createKey("trees_flower_forest");
    public static final ResourceKey<PlacedFeature> TREES_MEADOW = PlacementUtils.createKey("trees_meadow");
    public static final ResourceKey<PlacedFeature> TREES_TAIGA = PlacementUtils.createKey("trees_taiga");
    public static final ResourceKey<PlacedFeature> TREES_GROVE = PlacementUtils.createKey("trees_grove");
    public static final ResourceKey<PlacedFeature> TREES_BADLANDS = PlacementUtils.createKey("trees_badlands");
    public static final ResourceKey<PlacedFeature> TREES_SNOWY = PlacementUtils.createKey("trees_snowy");
    public static final ResourceKey<PlacedFeature> TREES_SWAMP = PlacementUtils.createKey("trees_swamp");
    public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_SAVANNA = PlacementUtils.createKey("trees_windswept_savanna");
    public static final ResourceKey<PlacedFeature> TREES_SAVANNA = PlacementUtils.createKey("trees_savanna");
    public static final ResourceKey<PlacedFeature> BIRCH_TALL = PlacementUtils.createKey("birch_tall");
    public static final ResourceKey<PlacedFeature> TREES_BIRCH = PlacementUtils.createKey("trees_birch");
    public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_FOREST = PlacementUtils.createKey("trees_windswept_forest");
    public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_HILLS = PlacementUtils.createKey("trees_windswept_hills");
    public static final ResourceKey<PlacedFeature> TREES_WATER = PlacementUtils.createKey("trees_water");
    public static final ResourceKey<PlacedFeature> TREES_BIRCH_AND_OAK = PlacementUtils.createKey("trees_birch_and_oak");
    public static final ResourceKey<PlacedFeature> TREES_SPARSE_JUNGLE = PlacementUtils.createKey("trees_sparse_jungle");
    public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacementUtils.createKey("trees_old_growth_spruce_taiga");
    public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_PINE_TAIGA = PlacementUtils.createKey("trees_old_growth_pine_taiga");
    public static final ResourceKey<PlacedFeature> TREES_JUNGLE = PlacementUtils.createKey("trees_jungle");
    public static final ResourceKey<PlacedFeature> BAMBOO_VEGETATION = PlacementUtils.createKey("bamboo_vegetation");
    public static final ResourceKey<PlacedFeature> MUSHROOM_ISLAND_VEGETATION = PlacementUtils.createKey("mushroom_island_vegetation");
    public static final ResourceKey<PlacedFeature> TREES_MANGROVE = PlacementUtils.createKey("trees_mangrove");
    private static final PlacementModifier TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);

    public static List<PlacementModifier> worldSurfaceSquaredWithCount(int $$0) {
        return List.of((Object)CountPlacement.of($$0), (Object)InSquarePlacement.spread(), (Object)PlacementUtils.HEIGHTMAP_WORLD_SURFACE, (Object)BiomeFilter.biome());
    }

    private static List<PlacementModifier> getMushroomPlacement(int $$0, @Nullable PlacementModifier $$1) {
        ImmutableList.Builder $$2 = ImmutableList.builder();
        if ($$1 != null) {
            $$2.add((Object)$$1);
        }
        if ($$0 != 0) {
            $$2.add((Object)RarityFilter.onAverageOnceEvery($$0));
        }
        $$2.add((Object)InSquarePlacement.spread());
        $$2.add((Object)PlacementUtils.HEIGHTMAP);
        $$2.add((Object)BiomeFilter.biome());
        return $$2.build();
    }

    private static ImmutableList.Builder<PlacementModifier> treePlacementBase(PlacementModifier $$0) {
        return ImmutableList.builder().add((Object)$$0).add((Object)InSquarePlacement.spread()).add((Object)TREE_THRESHOLD).add((Object)PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add((Object)BiomeFilter.biome());
    }

    public static List<PlacementModifier> treePlacement(PlacementModifier $$0) {
        return VegetationPlacements.treePlacementBase($$0).build();
    }

    public static List<PlacementModifier> treePlacement(PlacementModifier $$0, Block $$1) {
        return VegetationPlacements.treePlacementBase($$0).add((Object)BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive($$1.defaultBlockState(), BlockPos.ZERO))).build();
    }

    public static void bootstrap(BootstapContext<PlacedFeature> $$0) {
        HolderGetter<ConfiguredFeature<?, ?>> $$1 = $$0.lookup(Registries.CONFIGURED_FEATURE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$2 = $$1.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL);
        Holder.Reference<ConfiguredFeature<?, ?>> $$3 = $$1.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL);
        Holder.Reference<ConfiguredFeature<?, ?>> $$4 = $$1.getOrThrow(VegetationFeatures.VINES);
        Holder.Reference<ConfiguredFeature<?, ?>> $$5 = $$1.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER);
        Holder.Reference<ConfiguredFeature<?, ?>> $$6 = $$1.getOrThrow(VegetationFeatures.PATCH_PUMPKIN);
        Holder.Reference<ConfiguredFeature<?, ?>> $$7 = $$1.getOrThrow(VegetationFeatures.PATCH_GRASS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$8 = $$1.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$9 = $$1.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$10 = $$1.getOrThrow(VegetationFeatures.SINGLE_PIECE_OF_GRASS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$11 = $$1.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH);
        Holder.Reference<ConfiguredFeature<?, ?>> $$12 = $$1.getOrThrow(VegetationFeatures.PATCH_MELON);
        Holder.Reference<ConfiguredFeature<?, ?>> $$13 = $$1.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
        Holder.Reference<ConfiguredFeature<?, ?>> $$14 = $$1.getOrThrow(VegetationFeatures.PATCH_WATERLILY);
        Holder.Reference<ConfiguredFeature<?, ?>> $$15 = $$1.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$16 = $$1.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN);
        Holder.Reference<ConfiguredFeature<?, ?>> $$17 = $$1.getOrThrow(VegetationFeatures.PATCH_CACTUS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$18 = $$1.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$19 = $$1.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$20 = $$1.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$21 = $$1.getOrThrow(VegetationFeatures.FLOWER_DEFAULT);
        Holder.Reference<ConfiguredFeature<?, ?>> $$22 = $$1.getOrThrow(VegetationFeatures.FLOWER_FLOWER_FOREST);
        Holder.Reference<ConfiguredFeature<?, ?>> $$23 = $$1.getOrThrow(VegetationFeatures.FLOWER_SWAMP);
        Holder.Reference<ConfiguredFeature<?, ?>> $$24 = $$1.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
        Holder.Reference<ConfiguredFeature<?, ?>> $$25 = $$1.getOrThrow(VegetationFeatures.FLOWER_MEADOW);
        Holder.Reference<ConfiguredFeature<?, ?>> $$26 = $$1.getOrThrow(VegetationFeatures.TREES_PLAINS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$27 = $$1.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION);
        Holder.Reference<ConfiguredFeature<?, ?>> $$28 = $$1.getOrThrow(VegetationFeatures.FOREST_FLOWERS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$29 = $$1.getOrThrow(VegetationFeatures.TREES_FLOWER_FOREST);
        Holder.Reference<ConfiguredFeature<?, ?>> $$30 = $$1.getOrThrow(VegetationFeatures.MEADOW_TREES);
        Holder.Reference<ConfiguredFeature<?, ?>> $$31 = $$1.getOrThrow(VegetationFeatures.TREES_TAIGA);
        Holder.Reference<ConfiguredFeature<?, ?>> $$32 = $$1.getOrThrow(VegetationFeatures.TREES_GROVE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$33 = $$1.getOrThrow(TreeFeatures.OAK);
        Holder.Reference<ConfiguredFeature<?, ?>> $$34 = $$1.getOrThrow(TreeFeatures.SPRUCE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$35 = $$1.getOrThrow(TreeFeatures.SWAMP_OAK);
        Holder.Reference<ConfiguredFeature<?, ?>> $$36 = $$1.getOrThrow(VegetationFeatures.TREES_SAVANNA);
        Holder.Reference<ConfiguredFeature<?, ?>> $$37 = $$1.getOrThrow(VegetationFeatures.BIRCH_TALL);
        Holder.Reference<ConfiguredFeature<?, ?>> $$38 = $$1.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
        Holder.Reference<ConfiguredFeature<?, ?>> $$39 = $$1.getOrThrow(VegetationFeatures.TREES_WINDSWEPT_HILLS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$40 = $$1.getOrThrow(VegetationFeatures.TREES_WATER);
        Holder.Reference<ConfiguredFeature<?, ?>> $$41 = $$1.getOrThrow(VegetationFeatures.TREES_BIRCH_AND_OAK);
        Holder.Reference<ConfiguredFeature<?, ?>> $$42 = $$1.getOrThrow(VegetationFeatures.TREES_SPARSE_JUNGLE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$43 = $$1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
        Holder.Reference<ConfiguredFeature<?, ?>> $$44 = $$1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
        Holder.Reference<ConfiguredFeature<?, ?>> $$45 = $$1.getOrThrow(VegetationFeatures.TREES_JUNGLE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$46 = $$1.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION);
        Holder.Reference<ConfiguredFeature<?, ?>> $$47 = $$1.getOrThrow(VegetationFeatures.MUSHROOM_ISLAND_VEGETATION);
        Holder.Reference<ConfiguredFeature<?, ?>> $$48 = $$1.getOrThrow(VegetationFeatures.MANGROVE_VEGETATION);
        PlacementUtils.register($$0, BAMBOO_LIGHT, $$2, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, BAMBOO, $$3, NoiseBasedCountPlacement.of(160, 80.0, 0.3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, VINES, $$4, CountPlacement.of(127), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)), BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_SUNFLOWER, $$5, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_PUMPKIN, $$6, RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_GRASS_PLAIN, $$7, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_GRASS_FOREST, $$7, VegetationPlacements.worldSurfaceSquaredWithCount(2));
        PlacementUtils.register($$0, PATCH_GRASS_BADLANDS, $$7, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_GRASS_SAVANNA, $$7, VegetationPlacements.worldSurfaceSquaredWithCount(20));
        PlacementUtils.register($$0, PATCH_GRASS_NORMAL, $$7, VegetationPlacements.worldSurfaceSquaredWithCount(5));
        PlacementUtils.register($$0, PATCH_GRASS_TAIGA_2, $$8, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_GRASS_TAIGA, $$8, VegetationPlacements.worldSurfaceSquaredWithCount(7));
        PlacementUtils.register($$0, PATCH_GRASS_JUNGLE, $$9, VegetationPlacements.worldSurfaceSquaredWithCount(25));
        PlacementUtils.register($$0, GRASS_BONEMEAL, $$10, PlacementUtils.isEmpty());
        PlacementUtils.register($$0, PATCH_DEAD_BUSH_2, $$11, VegetationPlacements.worldSurfaceSquaredWithCount(2));
        PlacementUtils.register($$0, PATCH_DEAD_BUSH, $$11, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_DEAD_BUSH_BADLANDS, $$11, VegetationPlacements.worldSurfaceSquaredWithCount(20));
        PlacementUtils.register($$0, PATCH_MELON, $$12, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_MELON_SPARSE, $$12, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_BERRY_COMMON, $$13, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_BERRY_RARE, $$13, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_WATERLILY, $$14, VegetationPlacements.worldSurfaceSquaredWithCount(4));
        PlacementUtils.register($$0, PATCH_TALL_GRASS_2, $$15, NoiseThresholdCountPlacement.of(-0.8, 0, 7), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_TALL_GRASS, $$15, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_LARGE_FERN, $$16, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_CACTUS_DESERT, $$17, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_CACTUS_DECORATED, $$17, RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_SUGAR_CANE_SWAMP, $$18, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_SUGAR_CANE_DESERT, $$18, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_SUGAR_CANE_BADLANDS, $$18, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, PATCH_SUGAR_CANE, $$18, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, BROWN_MUSHROOM_NETHER, $$19, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register($$0, RED_MUSHROOM_NETHER, $$20, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register($$0, BROWN_MUSHROOM_NORMAL, $$19, VegetationPlacements.getMushroomPlacement(256, null));
        PlacementUtils.register($$0, RED_MUSHROOM_NORMAL, $$20, VegetationPlacements.getMushroomPlacement(512, null));
        PlacementUtils.register($$0, BROWN_MUSHROOM_TAIGA, $$19, VegetationPlacements.getMushroomPlacement(4, null));
        PlacementUtils.register($$0, RED_MUSHROOM_TAIGA, $$20, VegetationPlacements.getMushroomPlacement(256, null));
        PlacementUtils.register($$0, BROWN_MUSHROOM_OLD_GROWTH, $$19, VegetationPlacements.getMushroomPlacement(4, CountPlacement.of(3)));
        PlacementUtils.register($$0, RED_MUSHROOM_OLD_GROWTH, $$20, VegetationPlacements.getMushroomPlacement(171, null));
        PlacementUtils.register($$0, BROWN_MUSHROOM_SWAMP, $$19, VegetationPlacements.getMushroomPlacement(0, CountPlacement.of(2)));
        PlacementUtils.register($$0, RED_MUSHROOM_SWAMP, $$20, VegetationPlacements.getMushroomPlacement(64, null));
        PlacementUtils.register($$0, FLOWER_WARM, $$21, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_DEFAULT, $$21, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_FLOWER_FOREST, $$22, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_SWAMP, $$23, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_PLAINS, $$24, NoiseThresholdCountPlacement.of(-0.8, 15, 4), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_MEADOW, $$25, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        SurfaceWaterDepthFilter $$49 = SurfaceWaterDepthFilter.forMaxDepth(0);
        PlacementUtils.register($$0, TREES_PLAINS, $$26, PlacementUtils.countExtra(0, 0.05f, 1), InSquarePlacement.spread(), $$49, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome());
        PlacementUtils.register($$0, DARK_FOREST_VEGETATION, $$27, CountPlacement.of(16), InSquarePlacement.spread(), $$49, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
        PlacementUtils.register($$0, FLOWER_FOREST_FLOWERS, $$28, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)), BiomeFilter.biome());
        PlacementUtils.register($$0, FOREST_FLOWERS, $$28, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)), BiomeFilter.biome());
        PlacementUtils.register($$0, TREES_FLOWER_FOREST, $$29, VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_MEADOW, $$30, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(100)));
        PlacementUtils.register($$0, TREES_TAIGA, $$31, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_GROVE, $$32, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_BADLANDS, $$33, VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, 0.1f, 1), Blocks.OAK_SAPLING));
        PlacementUtils.register($$0, TREES_SNOWY, $$34, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.1f, 1), Blocks.SPRUCE_SAPLING));
        PlacementUtils.register($$0, TREES_SWAMP, $$35, PlacementUtils.countExtra(2, 0.1f, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)));
        PlacementUtils.register($$0, TREES_WINDSWEPT_SAVANNA, $$36, VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_SAVANNA, $$36, VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1)));
        PlacementUtils.register($$0, BIRCH_TALL, $$37, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_BIRCH, $$38, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1), Blocks.BIRCH_SAPLING));
        PlacementUtils.register($$0, TREES_WINDSWEPT_FOREST, $$39, VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_WINDSWEPT_HILLS, $$39, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_WATER, $$40, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_BIRCH_AND_OAK, $$41, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_SPARSE_JUNGLE, $$42, VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_OLD_GROWTH_SPRUCE_TAIGA, $$43, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_OLD_GROWTH_PINE_TAIGA, $$44, VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)));
        PlacementUtils.register($$0, TREES_JUNGLE, $$45, VegetationPlacements.treePlacement(PlacementUtils.countExtra(50, 0.1f, 1)));
        PlacementUtils.register($$0, BAMBOO_VEGETATION, $$46, VegetationPlacements.treePlacement(PlacementUtils.countExtra(30, 0.1f, 1)));
        PlacementUtils.register($$0, MUSHROOM_ISLAND_VEGETATION, $$47, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register($$0, TREES_MANGROVE, $$48, CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO)));
    }
}