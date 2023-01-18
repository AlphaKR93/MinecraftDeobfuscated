/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OverworldBiomes {
    protected static final int NORMAL_WATER_COLOR = 4159204;
    protected static final int NORMAL_WATER_FOG_COLOR = 329011;
    private static final int OVERWORLD_FOG_COLOR = 12638463;
    @Nullable
    private static final Music NORMAL_MUSIC = null;

    protected static int calculateSkyColor(float $$0) {
        float $$1 = $$0;
        $$1 /= 3.0f;
        $$1 = Mth.clamp($$1, -1.0f, 1.0f);
        return Mth.hsvToRgb(0.62222224f - $$1 * 0.05f, 0.5f + $$1 * 0.1f, 1.0f);
    }

    private static Biome biome(Biome.Precipitation $$0, float $$1, float $$2, MobSpawnSettings.Builder $$3, BiomeGenerationSettings.Builder $$4, @Nullable Music $$5) {
        return OverworldBiomes.biome($$0, $$1, $$2, 4159204, 329011, $$3, $$4, $$5);
    }

    private static Biome biome(Biome.Precipitation $$0, float $$1, float $$2, int $$3, int $$4, MobSpawnSettings.Builder $$5, BiomeGenerationSettings.Builder $$6, @Nullable Music $$7) {
        return new Biome.BiomeBuilder().precipitation($$0).temperature($$1).downfall($$2).specialEffects(new BiomeSpecialEffects.Builder().waterColor($$3).waterFogColor($$4).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor($$1)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic($$7).build()).mobSpawnSettings($$5.build()).generationSettings($$6.build()).build();
    }

    private static void globalOverworldGeneration(BiomeGenerationSettings.Builder $$0) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes($$0);
        BiomeDefaultFeatures.addDefaultCrystalFormations($$0);
        BiomeDefaultFeatures.addDefaultMonsterRoom($$0);
        BiomeDefaultFeatures.addDefaultUndergroundVariety($$0);
        BiomeDefaultFeatures.addDefaultSprings($$0);
        BiomeDefaultFeatures.addSurfaceFreezing($$0);
    }

    public static Biome oldGrowthTaiga(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$3);
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
        if ($$2) {
            BiomeDefaultFeatures.commonSpawns($$3);
        } else {
            BiomeDefaultFeatures.caveSpawns($$3);
            BiomeDefaultFeatures.monsters($$3, 100, 25, 100, false);
        }
        BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$4);
        BiomeDefaultFeatures.addMossyStoneBlock($$4);
        BiomeDefaultFeatures.addFerns($$4);
        BiomeDefaultFeatures.addDefaultOres($$4);
        BiomeDefaultFeatures.addDefaultSoftDisks($$4);
        $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA);
        BiomeDefaultFeatures.addDefaultFlowers($$4);
        BiomeDefaultFeatures.addGiantTaigaVegetation($$4);
        BiomeDefaultFeatures.addDefaultMushrooms($$4);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$4);
        BiomeDefaultFeatures.addCommonBerryBushes($$4);
        Music $$5 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, $$2 ? 0.25f : 0.3f, 0.8f, $$3, $$4, $$5);
    }

    public static Biome sparseJungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.baseJungleSpawns($$2);
        return OverworldBiomes.baseJungle($$0, $$1, 0.8f, false, true, false, $$2);
    }

    public static Biome jungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.baseJungleSpawns($$2);
        $$2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 1, 2));
        return OverworldBiomes.baseJungle($$0, $$1, 0.9f, false, false, true, $$2);
    }

    public static Biome bambooJungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.baseJungleSpawns($$2);
        $$2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 80, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
        return OverworldBiomes.baseJungle($$0, $$1, 0.9f, true, false, true, $$2);
    }

    private static Biome baseJungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, float $$2, boolean $$3, boolean $$4, boolean $$5, MobSpawnSettings.Builder $$6) {
        BiomeGenerationSettings.Builder $$7 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$7);
        BiomeDefaultFeatures.addDefaultOres($$7);
        BiomeDefaultFeatures.addDefaultSoftDisks($$7);
        if ($$3) {
            BiomeDefaultFeatures.addBambooVegetation($$7);
        } else {
            if ($$5) {
                BiomeDefaultFeatures.addLightBambooVegetation($$7);
            }
            if ($$4) {
                BiomeDefaultFeatures.addSparseJungleTrees($$7);
            } else {
                BiomeDefaultFeatures.addJungleTrees($$7);
            }
        }
        BiomeDefaultFeatures.addWarmFlowers($$7);
        BiomeDefaultFeatures.addJungleGrass($$7);
        BiomeDefaultFeatures.addDefaultMushrooms($$7);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$7);
        BiomeDefaultFeatures.addJungleVines($$7);
        if ($$4) {
            BiomeDefaultFeatures.addSparseJungleMelons($$7);
        } else {
            BiomeDefaultFeatures.addJungleMelons($$7);
        }
        Music $$8 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.95f, $$2, $$6, $$7, $$8);
    }

    public static Biome windsweptHills(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$3);
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 5, 4, 6));
        BiomeDefaultFeatures.commonSpawns($$3);
        BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$4);
        BiomeDefaultFeatures.addDefaultOres($$4);
        BiomeDefaultFeatures.addDefaultSoftDisks($$4);
        if ($$2) {
            BiomeDefaultFeatures.addMountainForestTrees($$4);
        } else {
            BiomeDefaultFeatures.addMountainTrees($$4);
        }
        BiomeDefaultFeatures.addDefaultFlowers($$4);
        BiomeDefaultFeatures.addDefaultGrass($$4);
        BiomeDefaultFeatures.addDefaultMushrooms($$4);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$4);
        BiomeDefaultFeatures.addExtraEmeralds($$4);
        BiomeDefaultFeatures.addInfestedStone($$4);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.2f, 0.3f, $$3, $$4, NORMAL_MUSIC);
    }

    public static Biome desert(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.desertSpawns($$2);
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        BiomeDefaultFeatures.addFossilDecoration($$3);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addDefaultFlowers($$3);
        BiomeDefaultFeatures.addDefaultGrass($$3);
        BiomeDefaultFeatures.addDesertVegetation($$3);
        BiomeDefaultFeatures.addDefaultMushrooms($$3);
        BiomeDefaultFeatures.addDesertExtraVegetation($$3);
        BiomeDefaultFeatures.addDesertExtraDecoration($$3);
        return OverworldBiomes.biome(Biome.Precipitation.NONE, 2.0f, 0.0f, $$2, $$3, NORMAL_MUSIC);
    }

    public static Biome plains(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3, boolean $$4) {
        MobSpawnSettings.Builder $$5 = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder $$6 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$6);
        if ($$3) {
            $$5.creatureGenerationProbability(0.07f);
            BiomeDefaultFeatures.snowySpawns($$5);
            if ($$4) {
                $$6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
                $$6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
            }
        } else {
            BiomeDefaultFeatures.plainsSpawns($$5);
            BiomeDefaultFeatures.addPlainGrass($$6);
            if ($$2) {
                $$6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
            }
        }
        BiomeDefaultFeatures.addDefaultOres($$6);
        BiomeDefaultFeatures.addDefaultSoftDisks($$6);
        if ($$3) {
            BiomeDefaultFeatures.addSnowyTrees($$6);
            BiomeDefaultFeatures.addDefaultFlowers($$6);
            BiomeDefaultFeatures.addDefaultGrass($$6);
        } else {
            BiomeDefaultFeatures.addPlainVegetation($$6);
        }
        BiomeDefaultFeatures.addDefaultMushrooms($$6);
        if ($$2) {
            $$6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
            $$6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
        } else {
            BiomeDefaultFeatures.addDefaultExtraVegetation($$6);
        }
        float $$7 = $$3 ? 0.0f : 0.8f;
        return OverworldBiomes.biome($$3 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, $$7, $$3 ? 0.5f : 0.4f, $$5, $$6, NORMAL_MUSIC);
    }

    public static Biome mushroomFields(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.mooshroomSpawns($$2);
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addMushroomFieldVegetation($$3);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$3);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.9f, 1.0f, $$2, $$3, NORMAL_MUSIC);
    }

    public static Biome savanna(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3) {
        BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$4);
        if (!$$2) {
            BiomeDefaultFeatures.addSavannaGrass($$4);
        }
        BiomeDefaultFeatures.addDefaultOres($$4);
        BiomeDefaultFeatures.addDefaultSoftDisks($$4);
        if ($$2) {
            BiomeDefaultFeatures.addShatteredSavannaTrees($$4);
            BiomeDefaultFeatures.addDefaultFlowers($$4);
            BiomeDefaultFeatures.addShatteredSavannaGrass($$4);
        } else {
            BiomeDefaultFeatures.addSavannaTrees($$4);
            BiomeDefaultFeatures.addWarmFlowers($$4);
            BiomeDefaultFeatures.addSavannaExtraGrass($$4);
        }
        BiomeDefaultFeatures.addDefaultMushrooms($$4);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$4);
        MobSpawnSettings.Builder $$5 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$5);
        $$5.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
        BiomeDefaultFeatures.commonSpawns($$5);
        if ($$3) {
            $$5.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
        }
        return OverworldBiomes.biome(Biome.Precipitation.NONE, 2.0f, 0.0f, $$5, $$4, NORMAL_MUSIC);
    }

    public static Biome badlands(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns($$3);
        BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$4);
        BiomeDefaultFeatures.addDefaultOres($$4);
        BiomeDefaultFeatures.addExtraGold($$4);
        BiomeDefaultFeatures.addDefaultSoftDisks($$4);
        if ($$2) {
            BiomeDefaultFeatures.addBadlandsTrees($$4);
        }
        BiomeDefaultFeatures.addBadlandGrass($$4);
        BiomeDefaultFeatures.addDefaultMushrooms($$4);
        BiomeDefaultFeatures.addBadlandExtraVegetation($$4);
        return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.NONE).temperature(2.0f).downfall(0.0f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor(2.0f)).foliageColorOverride(10387789).grassColorOverride(9470285).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings($$3.build()).generationSettings($$4.build()).build();
    }

    private static Biome baseOcean(MobSpawnSettings.Builder $$0, int $$1, int $$2, BiomeGenerationSettings.Builder $$3) {
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.5f, 0.5f, $$1, $$2, $$0, $$3, NORMAL_MUSIC);
    }

    private static BiomeGenerationSettings.Builder baseOceanGeneration(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addWaterTrees($$2);
        BiomeDefaultFeatures.addDefaultFlowers($$2);
        BiomeDefaultFeatures.addDefaultGrass($$2);
        BiomeDefaultFeatures.addDefaultMushrooms($$2);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$2);
        return $$2;
    }

    public static Biome coldOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.oceanSpawns($$3, 3, 4, 15);
        $$3.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
        BiomeGenerationSettings.Builder $$4 = OverworldBiomes.baseOceanGeneration($$0, $$1);
        $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
        BiomeDefaultFeatures.addDefaultSeagrass($$4);
        BiomeDefaultFeatures.addColdOceanExtraVegetation($$4);
        return OverworldBiomes.baseOcean($$3, 4020182, 329011, $$4);
    }

    public static Biome ocean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.oceanSpawns($$3, 1, 4, 10);
        $$3.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 1, 2));
        BiomeGenerationSettings.Builder $$4 = OverworldBiomes.baseOceanGeneration($$0, $$1);
        $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
        BiomeDefaultFeatures.addDefaultSeagrass($$4);
        BiomeDefaultFeatures.addColdOceanExtraVegetation($$4);
        return OverworldBiomes.baseOcean($$3, 4159204, 329011, $$4);
    }

    public static Biome lukeWarmOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        if ($$2) {
            BiomeDefaultFeatures.oceanSpawns($$3, 8, 4, 8);
        } else {
            BiomeDefaultFeatures.oceanSpawns($$3, 10, 2, 15);
        }
        $$3.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 5, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
        BiomeGenerationSettings.Builder $$4 = OverworldBiomes.baseOceanGeneration($$0, $$1);
        $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
        if ($$2) {
            BiomeDefaultFeatures.addDefaultSeagrass($$4);
        }
        BiomeDefaultFeatures.addLukeWarmKelp($$4);
        return OverworldBiomes.baseOcean($$3, 4566514, 267827, $$4);
    }

    public static Biome warmOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder().addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
        BiomeDefaultFeatures.warmOceanSpawns($$2, 10, 4);
        BiomeGenerationSettings.Builder $$3 = OverworldBiomes.baseOceanGeneration($$0, $$1).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
        return OverworldBiomes.baseOcean($$2, 4445678, 270131, $$3);
    }

    public static Biome frozenOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder().addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
        BiomeDefaultFeatures.commonSpawns($$3);
        $$3.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
        float $$4 = $$2 ? 0.5f : 0.0f;
        BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
        BiomeDefaultFeatures.addIcebergs($$5);
        OverworldBiomes.globalOverworldGeneration($$5);
        BiomeDefaultFeatures.addBlueIce($$5);
        BiomeDefaultFeatures.addDefaultOres($$5);
        BiomeDefaultFeatures.addDefaultSoftDisks($$5);
        BiomeDefaultFeatures.addWaterTrees($$5);
        BiomeDefaultFeatures.addDefaultFlowers($$5);
        BiomeDefaultFeatures.addDefaultGrass($$5);
        BiomeDefaultFeatures.addDefaultMushrooms($$5);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$5);
        return new Biome.BiomeBuilder().precipitation($$2 ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).temperature($$4).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).downfall(0.5f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor($$4)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings($$3.build()).generationSettings($$5.build()).build();
    }

    public static Biome forest(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3, boolean $$4) {
        BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$5);
        if ($$4) {
            $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
        } else {
            BiomeDefaultFeatures.addForestFlowers($$5);
        }
        BiomeDefaultFeatures.addDefaultOres($$5);
        BiomeDefaultFeatures.addDefaultSoftDisks($$5);
        if ($$4) {
            $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
            $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
            BiomeDefaultFeatures.addDefaultGrass($$5);
        } else {
            if ($$2) {
                if ($$3) {
                    BiomeDefaultFeatures.addTallBirchTrees($$5);
                } else {
                    BiomeDefaultFeatures.addBirchTrees($$5);
                }
            } else {
                BiomeDefaultFeatures.addOtherBirchTrees($$5);
            }
            BiomeDefaultFeatures.addDefaultFlowers($$5);
            BiomeDefaultFeatures.addForestGrass($$5);
        }
        BiomeDefaultFeatures.addDefaultMushrooms($$5);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$5);
        MobSpawnSettings.Builder $$6 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$6);
        BiomeDefaultFeatures.commonSpawns($$6);
        if ($$4) {
            $$6.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        } else if (!$$2) {
            $$6.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        }
        float $$7 = $$2 ? 0.6f : 0.7f;
        Music $$8 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, $$7, $$2 ? 0.6f : 0.8f, $$6, $$5, $$8);
    }

    public static Biome taiga(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$3);
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
        BiomeDefaultFeatures.commonSpawns($$3);
        float $$4 = $$2 ? -0.5f : 0.25f;
        BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$5);
        BiomeDefaultFeatures.addFerns($$5);
        BiomeDefaultFeatures.addDefaultOres($$5);
        BiomeDefaultFeatures.addDefaultSoftDisks($$5);
        BiomeDefaultFeatures.addTaigaTrees($$5);
        BiomeDefaultFeatures.addDefaultFlowers($$5);
        BiomeDefaultFeatures.addTaigaGrass($$5);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$5);
        if ($$2) {
            BiomeDefaultFeatures.addRareBerryBushes($$5);
        } else {
            BiomeDefaultFeatures.addCommonBerryBushes($$5);
        }
        return OverworldBiomes.biome($$2 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, $$4, $$2 ? 0.4f : 0.8f, $$2 ? 4020182 : 4159204, 329011, $$3, $$5, NORMAL_MUSIC);
    }

    public static Biome darkForest(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$2);
        BiomeDefaultFeatures.commonSpawns($$2);
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$3);
        $$3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
        BiomeDefaultFeatures.addForestFlowers($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addDefaultFlowers($$3);
        BiomeDefaultFeatures.addForestGrass($$3);
        BiomeDefaultFeatures.addDefaultMushrooms($$3);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$3);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
        return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.RAIN).temperature(0.7f).downfall(0.8f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor(0.7f)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic($$4).build()).mobSpawnSettings($$2.build()).generationSettings($$3.build()).build();
    }

    public static Biome swamp(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$2);
        BiomeDefaultFeatures.commonSpawns($$2);
        $$2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
        $$2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        BiomeDefaultFeatures.addFossilDecoration($$3);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addSwampClayDisk($$3);
        BiomeDefaultFeatures.addSwampVegetation($$3);
        BiomeDefaultFeatures.addDefaultMushrooms($$3);
        BiomeDefaultFeatures.addSwampExtraVegetation($$3);
        $$3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
        return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.RAIN).temperature(0.8f).downfall(0.9f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor(0.8f)).foliageColorOverride(6975545).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic($$4).build()).mobSpawnSettings($$2.build()).generationSettings($$3.build()).build();
    }

    public static Biome mangroveSwamp(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns($$2);
        $$2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
        $$2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
        $$2.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        BiomeDefaultFeatures.addFossilDecoration($$3);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addMangroveSwampDisks($$3);
        BiomeDefaultFeatures.addMangroveSwampVegetation($$3);
        $$3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
        return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.RAIN).temperature(0.8f).downfall(0.9f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(3832426).waterFogColor(5077600).fogColor(12638463).skyColor(OverworldBiomes.calculateSkyColor(0.8f)).foliageColorOverride(9285927).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic($$4).build()).mobSpawnSettings($$2.build()).generationSettings($$3.build()).build();
    }

    public static Biome river(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder().addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 5, 1, 5));
        BiomeDefaultFeatures.commonSpawns($$3);
        $$3.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, $$2 ? 1 : 100, 1, 1));
        BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$4);
        BiomeDefaultFeatures.addDefaultOres($$4);
        BiomeDefaultFeatures.addDefaultSoftDisks($$4);
        BiomeDefaultFeatures.addWaterTrees($$4);
        BiomeDefaultFeatures.addDefaultFlowers($$4);
        BiomeDefaultFeatures.addDefaultGrass($$4);
        BiomeDefaultFeatures.addDefaultMushrooms($$4);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$4);
        if (!$$2) {
            $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
        }
        float $$5 = $$2 ? 0.0f : 0.5f;
        return OverworldBiomes.biome($$2 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, $$5, 0.5f, $$2 ? 3750089 : 4159204, 329011, $$3, $$4, NORMAL_MUSIC);
    }

    public static Biome beach(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3) {
        float $$9;
        boolean $$5;
        MobSpawnSettings.Builder $$4 = new MobSpawnSettings.Builder();
        boolean bl = $$5 = !$$3 && !$$2;
        if ($$5) {
            $$4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 5));
        }
        BiomeDefaultFeatures.commonSpawns($$4);
        BiomeGenerationSettings.Builder $$6 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$6);
        BiomeDefaultFeatures.addDefaultOres($$6);
        BiomeDefaultFeatures.addDefaultSoftDisks($$6);
        BiomeDefaultFeatures.addDefaultFlowers($$6);
        BiomeDefaultFeatures.addDefaultGrass($$6);
        BiomeDefaultFeatures.addDefaultMushrooms($$6);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$6);
        if ($$2) {
            float $$7 = 0.05f;
        } else if ($$3) {
            float $$8 = 0.2f;
        } else {
            $$9 = 0.8f;
        }
        return OverworldBiomes.biome($$2 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, $$9, $$5 ? 0.4f : 0.3f, $$2 ? 4020182 : 4159204, 329011, $$4, $$6, NORMAL_MUSIC);
    }

    public static Biome theVoid(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        $$2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
        return OverworldBiomes.biome(Biome.Precipitation.NONE, 0.5f, 0.5f, new MobSpawnSettings.Builder(), $$2, NORMAL_MUSIC);
    }

    public static Biome meadow(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 2, 4));
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addPlainGrass($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addMeadowVegetation($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.5f, 0.8f, 937679, 329011, $$3, $$2, $$4);
    }

    public static Biome frozenPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addFrozenSprings($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS);
        return OverworldBiomes.biome(Biome.Precipitation.SNOW, -0.7f, 0.9f, $$3, $$2, $$4);
    }

    public static Biome jaggedPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addFrozenSprings($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS);
        return OverworldBiomes.biome(Biome.Precipitation.SNOW, -0.7f, 0.9f, $$3, $$2, $$4);
    }

    public static Biome stonyPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 1.0f, 0.3f, $$3, $$2, $$4);
    }

    public static Biome snowySlopes(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addFrozenSprings($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES);
        return OverworldBiomes.biome(Biome.Precipitation.SNOW, -0.3f, 0.9f, $$3, $$2, $$4);
    }

    public static Biome grove(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
        MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals($$3);
        $$3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
        BiomeDefaultFeatures.commonSpawns($$3);
        OverworldBiomes.globalOverworldGeneration($$2);
        BiomeDefaultFeatures.addFrozenSprings($$2);
        BiomeDefaultFeatures.addDefaultOres($$2);
        BiomeDefaultFeatures.addDefaultSoftDisks($$2);
        BiomeDefaultFeatures.addGroveTrees($$2);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$2);
        BiomeDefaultFeatures.addExtraEmeralds($$2);
        BiomeDefaultFeatures.addInfestedStone($$2);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE);
        return OverworldBiomes.biome(Biome.Precipitation.SNOW, -0.2f, 0.8f, $$3, $$2, $$4);
    }

    public static Biome lushCaves(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        $$2.addSpawn(MobCategory.AXOLOTLS, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 10, 4, 6));
        $$2.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
        BiomeDefaultFeatures.commonSpawns($$2);
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addPlainGrass($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addLushCavesSpecialOres($$3);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addLushCavesVegetationFeatures($$3);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.5f, 0.5f, $$2, $$3, $$4);
    }

    public static Biome dripstoneCaves(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.dripstoneCavesSpawns($$2);
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        OverworldBiomes.globalOverworldGeneration($$3);
        BiomeDefaultFeatures.addPlainGrass($$3);
        BiomeDefaultFeatures.addDefaultOres($$3, true);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addPlainVegetation($$3);
        BiomeDefaultFeatures.addDefaultMushrooms($$3);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$3);
        BiomeDefaultFeatures.addDripstone($$3);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.8f, 0.4f, $$2, $$3, $$4);
    }

    public static Biome deepDark(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
        MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
        $$3.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        $$3.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
        $$3.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
        BiomeDefaultFeatures.addDefaultCrystalFormations($$3);
        BiomeDefaultFeatures.addDefaultMonsterRoom($$3);
        BiomeDefaultFeatures.addDefaultUndergroundVariety($$3);
        BiomeDefaultFeatures.addSurfaceFreezing($$3);
        BiomeDefaultFeatures.addPlainGrass($$3);
        BiomeDefaultFeatures.addDefaultOres($$3);
        BiomeDefaultFeatures.addDefaultSoftDisks($$3);
        BiomeDefaultFeatures.addPlainVegetation($$3);
        BiomeDefaultFeatures.addDefaultMushrooms($$3);
        BiomeDefaultFeatures.addDefaultExtraVegetation($$3);
        BiomeDefaultFeatures.addSculk($$3);
        Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK);
        return OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.8f, 0.4f, $$2, $$3, $$4);
    }
}