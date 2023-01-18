/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.presets;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class WorldPresets {
    public static final ResourceKey<WorldPreset> NORMAL = WorldPresets.register("normal");
    public static final ResourceKey<WorldPreset> FLAT = WorldPresets.register("flat");
    public static final ResourceKey<WorldPreset> LARGE_BIOMES = WorldPresets.register("large_biomes");
    public static final ResourceKey<WorldPreset> AMPLIFIED = WorldPresets.register("amplified");
    public static final ResourceKey<WorldPreset> SINGLE_BIOME_SURFACE = WorldPresets.register("single_biome_surface");
    public static final ResourceKey<WorldPreset> DEBUG = WorldPresets.register("debug_all_block_states");

    public static void bootstrap(BootstapContext<WorldPreset> $$0) {
        new Bootstrap($$0).run();
    }

    private static ResourceKey<WorldPreset> register(String $$0) {
        return ResourceKey.create(Registries.WORLD_PRESET, new ResourceLocation($$0));
    }

    public static Optional<ResourceKey<WorldPreset>> fromSettings(Registry<LevelStem> $$02) {
        return $$02.getOptional(LevelStem.OVERWORLD).flatMap($$0 -> {
            ChunkGenerator $$1 = $$0.generator();
            if ($$1 instanceof FlatLevelSource) {
                return Optional.of(FLAT);
            }
            if ($$1 instanceof DebugLevelSource) {
                return Optional.of(DEBUG);
            }
            return Optional.empty();
        });
    }

    public static WorldDimensions createNormalWorldDimensions(RegistryAccess $$0) {
        return $$0.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(NORMAL).value().createWorldDimensions();
    }

    public static LevelStem getNormalOverworld(RegistryAccess $$0) {
        return (LevelStem)((Object)$$0.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(NORMAL).value().overworld().orElseThrow());
    }

    static class Bootstrap {
        private final BootstapContext<WorldPreset> context;
        private final HolderGetter<NoiseGeneratorSettings> noiseSettings;
        private final HolderGetter<Biome> biomes;
        private final HolderGetter<PlacedFeature> placedFeatures;
        private final HolderGetter<StructureSet> structureSets;
        private final Holder<DimensionType> overworldDimensionType;
        private final LevelStem netherStem;
        private final LevelStem endStem;

        Bootstrap(BootstapContext<WorldPreset> $$0) {
            this.context = $$0;
            HolderGetter<DimensionType> $$1 = $$0.lookup(Registries.DIMENSION_TYPE);
            this.noiseSettings = $$0.lookup(Registries.NOISE_SETTINGS);
            this.biomes = $$0.lookup(Registries.BIOME);
            this.placedFeatures = $$0.lookup(Registries.PLACED_FEATURE);
            this.structureSets = $$0.lookup(Registries.STRUCTURE_SET);
            this.overworldDimensionType = $$1.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
            Holder.Reference<DimensionType> $$2 = $$1.getOrThrow(BuiltinDimensionTypes.NETHER);
            Holder.Reference<NoiseGeneratorSettings> $$3 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
            this.netherStem = new LevelStem($$2, new NoiseBasedChunkGenerator((BiomeSource)MultiNoiseBiomeSource.Preset.NETHER.biomeSource(this.biomes), $$3));
            Holder.Reference<DimensionType> $$4 = $$1.getOrThrow(BuiltinDimensionTypes.END);
            Holder.Reference<NoiseGeneratorSettings> $$5 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
            this.endStem = new LevelStem($$4, new NoiseBasedChunkGenerator((BiomeSource)TheEndBiomeSource.create(this.biomes), $$5));
        }

        private LevelStem makeOverworld(ChunkGenerator $$0) {
            return new LevelStem(this.overworldDimensionType, $$0);
        }

        private LevelStem makeNoiseBasedOverworld(BiomeSource $$0, Holder<NoiseGeneratorSettings> $$1) {
            return this.makeOverworld(new NoiseBasedChunkGenerator($$0, $$1));
        }

        private WorldPreset createPresetWithCustomOverworld(LevelStem $$0) {
            return new WorldPreset((Map<ResourceKey<LevelStem>, LevelStem>)Map.of(LevelStem.OVERWORLD, (Object)((Object)$$0), LevelStem.NETHER, (Object)((Object)this.netherStem), LevelStem.END, (Object)((Object)this.endStem)));
        }

        private void registerCustomOverworldPreset(ResourceKey<WorldPreset> $$0, LevelStem $$1) {
            this.context.register($$0, this.createPresetWithCustomOverworld($$1));
        }

        public void run() {
            MultiNoiseBiomeSource $$0 = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(this.biomes);
            Holder.Reference<NoiseGeneratorSettings> $$1 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
            this.registerCustomOverworldPreset(NORMAL, this.makeNoiseBasedOverworld($$0, $$1));
            Holder.Reference<NoiseGeneratorSettings> $$2 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
            this.registerCustomOverworldPreset(LARGE_BIOMES, this.makeNoiseBasedOverworld($$0, $$2));
            Holder.Reference<NoiseGeneratorSettings> $$3 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
            this.registerCustomOverworldPreset(AMPLIFIED, this.makeNoiseBasedOverworld($$0, $$3));
            Holder.Reference<Biome> $$4 = this.biomes.getOrThrow(Biomes.PLAINS);
            this.registerCustomOverworldPreset(SINGLE_BIOME_SURFACE, this.makeNoiseBasedOverworld(new FixedBiomeSource($$4), $$1));
            this.registerCustomOverworldPreset(FLAT, this.makeOverworld(new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(this.biomes, this.structureSets, this.placedFeatures))));
            this.registerCustomOverworldPreset(DEBUG, this.makeOverworld(new DebugLevelSource($$4)));
        }
    }
}