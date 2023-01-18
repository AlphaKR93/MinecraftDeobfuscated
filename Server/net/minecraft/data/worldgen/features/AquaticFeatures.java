/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.data.worldgen.features;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class AquaticFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEAGRASS_SHORT = FeatureUtils.createKey("seagrass_short");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEAGRASS_SLIGHTLY_LESS_SHORT = FeatureUtils.createKey("seagrass_slightly_less_short");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEAGRASS_MID = FeatureUtils.createKey("seagrass_mid");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEAGRASS_TALL = FeatureUtils.createKey("seagrass_tall");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEA_PICKLE = FeatureUtils.createKey("sea_pickle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SEAGRASS_SIMPLE = FeatureUtils.createKey("seagrass_simple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> KELP = FeatureUtils.createKey("kelp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WARM_OCEAN_VEGETATION = FeatureUtils.createKey("warm_ocean_vegetation");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> $$0) {
        FeatureUtils.register($$0, SEAGRASS_SHORT, Feature.SEAGRASS, new ProbabilityFeatureConfiguration(0.3f));
        FeatureUtils.register($$0, SEAGRASS_SLIGHTLY_LESS_SHORT, Feature.SEAGRASS, new ProbabilityFeatureConfiguration(0.4f));
        FeatureUtils.register($$0, SEAGRASS_MID, Feature.SEAGRASS, new ProbabilityFeatureConfiguration(0.6f));
        FeatureUtils.register($$0, SEAGRASS_TALL, Feature.SEAGRASS, new ProbabilityFeatureConfiguration(0.8f));
        FeatureUtils.register($$0, SEA_PICKLE, Feature.SEA_PICKLE, new CountConfiguration(20));
        FeatureUtils.register($$0, SEAGRASS_SIMPLE, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SEAGRASS)));
        FeatureUtils.register($$0, KELP, Feature.KELP);
        FeatureUtils.register($$0, WARM_OCEAN_VEGETATION, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(PlacementUtils.inlinePlaced(Feature.CORAL_TREE, FeatureConfiguration.NONE, new PlacementModifier[0]), PlacementUtils.inlinePlaced(Feature.CORAL_CLAW, FeatureConfiguration.NONE, new PlacementModifier[0]), PlacementUtils.inlinePlaced(Feature.CORAL_MUSHROOM, FeatureConfiguration.NONE, new PlacementModifier[0]))));
    }
}