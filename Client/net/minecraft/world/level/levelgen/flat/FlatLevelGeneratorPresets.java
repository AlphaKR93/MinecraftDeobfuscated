/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Collectors
 */
package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelGeneratorPresets {
    public static final ResourceKey<FlatLevelGeneratorPreset> CLASSIC_FLAT = FlatLevelGeneratorPresets.register("classic_flat");
    public static final ResourceKey<FlatLevelGeneratorPreset> TUNNELERS_DREAM = FlatLevelGeneratorPresets.register("tunnelers_dream");
    public static final ResourceKey<FlatLevelGeneratorPreset> WATER_WORLD = FlatLevelGeneratorPresets.register("water_world");
    public static final ResourceKey<FlatLevelGeneratorPreset> OVERWORLD = FlatLevelGeneratorPresets.register("overworld");
    public static final ResourceKey<FlatLevelGeneratorPreset> SNOWY_KINGDOM = FlatLevelGeneratorPresets.register("snowy_kingdom");
    public static final ResourceKey<FlatLevelGeneratorPreset> BOTTOMLESS_PIT = FlatLevelGeneratorPresets.register("bottomless_pit");
    public static final ResourceKey<FlatLevelGeneratorPreset> DESERT = FlatLevelGeneratorPresets.register("desert");
    public static final ResourceKey<FlatLevelGeneratorPreset> REDSTONE_READY = FlatLevelGeneratorPresets.register("redstone_ready");
    public static final ResourceKey<FlatLevelGeneratorPreset> THE_VOID = FlatLevelGeneratorPresets.register("the_void");

    public static void bootstrap(BootstapContext<FlatLevelGeneratorPreset> $$0) {
        new Bootstrap($$0).run();
    }

    private static ResourceKey<FlatLevelGeneratorPreset> register(String $$0) {
        return ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, new ResourceLocation($$0));
    }

    static class Bootstrap {
        private final BootstapContext<FlatLevelGeneratorPreset> context;

        Bootstrap(BootstapContext<FlatLevelGeneratorPreset> $$0) {
            this.context = $$0;
        }

        private void register(ResourceKey<FlatLevelGeneratorPreset> $$0, ItemLike $$1, ResourceKey<Biome> $$2, Set<ResourceKey<StructureSet>> $$3, boolean $$4, boolean $$5, FlatLayerInfo ... $$6) {
            HolderGetter<StructureSet> $$7 = this.context.lookup(Registries.STRUCTURE_SET);
            HolderGetter<PlacedFeature> $$8 = this.context.lookup(Registries.PLACED_FEATURE);
            HolderGetter<Biome> $$9 = this.context.lookup(Registries.BIOME);
            HolderSet.Direct $$10 = HolderSet.direct((List)$$3.stream().map($$7::getOrThrow).collect(Collectors.toList()));
            FlatLevelGeneratorSettings $$11 = new FlatLevelGeneratorSettings((Optional<HolderSet<StructureSet>>)Optional.of($$10), $$9.getOrThrow($$2), FlatLevelGeneratorSettings.createLakesList($$8));
            if ($$4) {
                $$11.setDecoration();
            }
            if ($$5) {
                $$11.setAddLakes();
            }
            for (int $$12 = $$6.length - 1; $$12 >= 0; --$$12) {
                $$11.getLayersInfo().add((Object)$$6[$$12]);
            }
            this.context.register($$0, new FlatLevelGeneratorPreset($$1.asItem().builtInRegistryHolder(), $$11));
        }

        public void run() {
            this.register(CLASSIC_FLAT, Blocks.GRASS_BLOCK, Biomes.PLAINS, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.VILLAGES), false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(TUNNELERS_DREAM, Blocks.STONE, Biomes.WINDSWEPT_HILLS, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(WATER_WORLD, Items.WATER_BUCKET, Biomes.DEEP_OCEAN, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.OCEAN_RUINS, BuiltinStructureSets.SHIPWRECKS, BuiltinStructureSets.OCEAN_MONUMENTS), false, false, new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.GRAVEL), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(64, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(OVERWORLD, Blocks.GRASS, Biomes.PLAINS, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.PILLAGER_OUTPOSTS, BuiltinStructureSets.RUINED_PORTALS, BuiltinStructureSets.STRONGHOLDS), true, true, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(SNOWY_KINGDOM, Blocks.SNOW, Biomes.SNOWY_PLAINS, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.IGLOOS), false, false, new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(BOTTOMLESS_PIT, Items.FEATHER, Biomes.PLAINS, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.VILLAGES), false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
            this.register(DESERT, Blocks.SAND, Biomes.DESERT, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.DESERT_PYRAMIDS, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(REDSTONE_READY, Items.REDSTONE, Biomes.DESERT, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(), false, false, new FlatLayerInfo(116, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.register(THE_VOID, Blocks.BARRIER, Biomes.THE_VOID, (Set<ResourceKey<StructureSet>>)ImmutableSet.of(), true, false, new FlatLayerInfo(1, Blocks.AIR));
        }
    }
}