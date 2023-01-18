/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.EndBiomes;
import net.minecraft.data.worldgen.biome.NetherBiomes;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public abstract class Biomes {
    public static void bootstrap(BootstapContext<Biome> $$0) {
        HolderGetter<PlacedFeature> $$1 = $$0.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> $$2 = $$0.lookup(Registries.CONFIGURED_CARVER);
        $$0.register(net.minecraft.world.level.biome.Biomes.THE_VOID, OverworldBiomes.theVoid($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.PLAINS, OverworldBiomes.plains($$1, $$2, false, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains($$1, $$2, true, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS, OverworldBiomes.plains($$1, $$2, false, true, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.ICE_SPIKES, OverworldBiomes.plains($$1, $$2, false, true, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.DESERT, OverworldBiomes.desert($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.SWAMP, OverworldBiomes.swamp($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.FOREST, OverworldBiomes.forest($$1, $$2, false, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.FLOWER_FOREST, OverworldBiomes.forest($$1, $$2, false, false, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.BIRCH_FOREST, OverworldBiomes.forest($$1, $$2, true, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.DARK_FOREST, OverworldBiomes.darkForest($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest($$1, $$2, true, true, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.TAIGA, OverworldBiomes.taiga($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA, OverworldBiomes.taiga($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.SAVANNA, OverworldBiomes.savanna($$1, $$2, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna($$1, $$2, false, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna($$1, $$2, true, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.JUNGLE, OverworldBiomes.jungle($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.BADLANDS, OverworldBiomes.badlands($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.ERODED_BADLANDS, OverworldBiomes.badlands($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.WOODED_BADLANDS, OverworldBiomes.badlands($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.MEADOW, OverworldBiomes.meadow($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.GROVE, OverworldBiomes.grove($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.RIVER, OverworldBiomes.river($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.FROZEN_RIVER, OverworldBiomes.river($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.BEACH, OverworldBiomes.beach($$1, $$2, false, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.SNOWY_BEACH, OverworldBiomes.beach($$1, $$2, true, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.STONY_SHORE, OverworldBiomes.beach($$1, $$2, false, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.WARM_OCEAN, OverworldBiomes.warmOcean($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.OCEAN, OverworldBiomes.ocean($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.DEEP_OCEAN, OverworldBiomes.ocean($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.COLD_OCEAN, OverworldBiomes.coldOcean($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean($$1, $$2, false));
        $$0.register(net.minecraft.world.level.biome.Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean($$1, $$2, true));
        $$0.register(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.LUSH_CAVES, OverworldBiomes.lushCaves($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.DEEP_DARK, OverworldBiomes.deepDark($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.NETHER_WASTES, NetherBiomes.netherWastes($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.WARPED_FOREST, NetherBiomes.warpedForest($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.THE_END, EndBiomes.theEnd($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.END_HIGHLANDS, EndBiomes.endHighlands($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.END_MIDLANDS, EndBiomes.endMidlands($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands($$1, $$2));
        $$0.register(net.minecraft.world.level.biome.Biomes.END_BARRENS, EndBiomes.endBarrens($$1, $$2));
    }
}