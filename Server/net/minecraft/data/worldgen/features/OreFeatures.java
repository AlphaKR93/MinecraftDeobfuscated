/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class OreFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MAGMA = FeatureUtils.createKey("ore_magma");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SOUL_SAND = FeatureUtils.createKey("ore_soul_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_NETHER_GOLD = FeatureUtils.createKey("ore_nether_gold");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_QUARTZ = FeatureUtils.createKey("ore_quartz");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRAVEL_NETHER = FeatureUtils.createKey("ore_gravel_nether");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_BLACKSTONE = FeatureUtils.createKey("ore_blackstone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIRT = FeatureUtils.createKey("ore_dirt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRAVEL = FeatureUtils.createKey("ore_gravel");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRANITE = FeatureUtils.createKey("ore_granite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIORITE = FeatureUtils.createKey("ore_diorite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANDESITE = FeatureUtils.createKey("ore_andesite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TUFF = FeatureUtils.createKey("ore_tuff");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COAL = FeatureUtils.createKey("ore_coal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COAL_BURIED = FeatureUtils.createKey("ore_coal_buried");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_IRON = FeatureUtils.createKey("ore_iron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_IRON_SMALL = FeatureUtils.createKey("ore_iron_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GOLD = FeatureUtils.createKey("ore_gold");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GOLD_BURIED = FeatureUtils.createKey("ore_gold_buried");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_REDSTONE = FeatureUtils.createKey("ore_redstone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_SMALL = FeatureUtils.createKey("ore_diamond_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_LARGE = FeatureUtils.createKey("ore_diamond_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_BURIED = FeatureUtils.createKey("ore_diamond_buried");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LAPIS = FeatureUtils.createKey("ore_lapis");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LAPIS_BURIED = FeatureUtils.createKey("ore_lapis_buried");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_INFESTED = FeatureUtils.createKey("ore_infested");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_EMERALD = FeatureUtils.createKey("ore_emerald");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_LARGE = FeatureUtils.createKey("ore_ancient_debris_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_SMALL = FeatureUtils.createKey("ore_ancient_debris_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPPER_SMALL = FeatureUtils.createKey("ore_copper_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE = FeatureUtils.createKey("ore_copper_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CLAY = FeatureUtils.createKey("ore_clay");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> $$0) {
        TagMatchTest $$1 = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
        TagMatchTest $$2 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        TagMatchTest $$3 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        BlockMatchTest $$4 = new BlockMatchTest(Blocks.NETHERRACK);
        TagMatchTest $$5 = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
        List $$6 = List.of((Object)OreConfiguration.target($$2, Blocks.IRON_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_IRON_ORE.defaultBlockState()));
        List $$7 = List.of((Object)OreConfiguration.target($$2, Blocks.GOLD_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState()));
        List $$8 = List.of((Object)OreConfiguration.target($$2, Blocks.DIAMOND_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()));
        List $$9 = List.of((Object)OreConfiguration.target($$2, Blocks.LAPIS_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState()));
        List $$10 = List.of((Object)OreConfiguration.target($$2, Blocks.COPPER_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState()));
        List $$11 = List.of((Object)OreConfiguration.target($$2, Blocks.COAL_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState()));
        FeatureUtils.register($$0, ORE_MAGMA, Feature.ORE, new OreConfiguration($$4, Blocks.MAGMA_BLOCK.defaultBlockState(), 33));
        FeatureUtils.register($$0, ORE_SOUL_SAND, Feature.ORE, new OreConfiguration($$4, Blocks.SOUL_SAND.defaultBlockState(), 12));
        FeatureUtils.register($$0, ORE_NETHER_GOLD, Feature.ORE, new OreConfiguration($$4, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 10));
        FeatureUtils.register($$0, ORE_QUARTZ, Feature.ORE, new OreConfiguration($$4, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14));
        FeatureUtils.register($$0, ORE_GRAVEL_NETHER, Feature.ORE, new OreConfiguration($$4, Blocks.GRAVEL.defaultBlockState(), 33));
        FeatureUtils.register($$0, ORE_BLACKSTONE, Feature.ORE, new OreConfiguration($$4, Blocks.BLACKSTONE.defaultBlockState(), 33));
        FeatureUtils.register($$0, ORE_DIRT, Feature.ORE, new OreConfiguration($$1, Blocks.DIRT.defaultBlockState(), 33));
        FeatureUtils.register($$0, ORE_GRAVEL, Feature.ORE, new OreConfiguration($$1, Blocks.GRAVEL.defaultBlockState(), 33));
        FeatureUtils.register($$0, ORE_GRANITE, Feature.ORE, new OreConfiguration($$1, Blocks.GRANITE.defaultBlockState(), 64));
        FeatureUtils.register($$0, ORE_DIORITE, Feature.ORE, new OreConfiguration($$1, Blocks.DIORITE.defaultBlockState(), 64));
        FeatureUtils.register($$0, ORE_ANDESITE, Feature.ORE, new OreConfiguration($$1, Blocks.ANDESITE.defaultBlockState(), 64));
        FeatureUtils.register($$0, ORE_TUFF, Feature.ORE, new OreConfiguration($$1, Blocks.TUFF.defaultBlockState(), 64));
        FeatureUtils.register($$0, ORE_COAL, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$11, 17));
        FeatureUtils.register($$0, ORE_COAL_BURIED, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$11, 17, 0.5f));
        FeatureUtils.register($$0, ORE_IRON, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$6, 9));
        FeatureUtils.register($$0, ORE_IRON_SMALL, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$6, 4));
        FeatureUtils.register($$0, ORE_GOLD, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$7, 9));
        FeatureUtils.register($$0, ORE_GOLD_BURIED, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$7, 9, 0.5f));
        FeatureUtils.register($$0, ORE_REDSTONE, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)List.of((Object)OreConfiguration.target($$2, Blocks.REDSTONE_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState())), 8));
        FeatureUtils.register($$0, ORE_DIAMOND_SMALL, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$8, 4, 0.5f));
        FeatureUtils.register($$0, ORE_DIAMOND_LARGE, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$8, 12, 0.7f));
        FeatureUtils.register($$0, ORE_DIAMOND_BURIED, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$8, 8, 1.0f));
        FeatureUtils.register($$0, ORE_LAPIS, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$9, 7));
        FeatureUtils.register($$0, ORE_LAPIS_BURIED, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$9, 7, 1.0f));
        FeatureUtils.register($$0, ORE_INFESTED, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)List.of((Object)OreConfiguration.target($$2, Blocks.INFESTED_STONE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.INFESTED_DEEPSLATE.defaultBlockState())), 9));
        FeatureUtils.register($$0, ORE_EMERALD, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)List.of((Object)OreConfiguration.target($$2, Blocks.EMERALD_ORE.defaultBlockState()), (Object)OreConfiguration.target($$3, Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState())), 3));
        FeatureUtils.register($$0, ORE_ANCIENT_DEBRIS_LARGE, Feature.SCATTERED_ORE, new OreConfiguration($$5, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 3, 1.0f));
        FeatureUtils.register($$0, ORE_ANCIENT_DEBRIS_SMALL, Feature.SCATTERED_ORE, new OreConfiguration($$5, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2, 1.0f));
        FeatureUtils.register($$0, ORE_COPPPER_SMALL, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$10, 10));
        FeatureUtils.register($$0, ORE_COPPER_LARGE, Feature.ORE, new OreConfiguration((List<OreConfiguration.TargetBlockState>)$$10, 20));
        FeatureUtils.register($$0, ORE_CLAY, Feature.ORE, new OreConfiguration($$1, Blocks.CLAY.defaultBlockState(), 33));
    }
}