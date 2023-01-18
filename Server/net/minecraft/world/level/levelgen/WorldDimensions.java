/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
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
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Registry<LevelStem> dimensions) {
    public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RegistryCodecs.fullCodec(Registries.LEVEL_STEM, Lifecycle.stable(), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply((Applicative)$$0, $$0.stable(WorldDimensions::new)));
    private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
    private static final int VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();

    public WorldDimensions {
        LevelStem $$1 = $$0.get(LevelStem.OVERWORLD);
        if ($$1 == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> $$02) {
        return Stream.concat((Stream)BUILTIN_ORDER.stream(), (Stream)$$02.filter($$0 -> !BUILTIN_ORDER.contains($$0)));
    }

    public WorldDimensions replaceOverworldGenerator(RegistryAccess $$0, ChunkGenerator $$1) {
        Registry<DimensionType> $$2 = $$0.registryOrThrow(Registries.DIMENSION_TYPE);
        Registry<LevelStem> $$3 = WorldDimensions.withOverworld($$2, this.dimensions, $$1);
        return new WorldDimensions($$3);
    }

    public static Registry<LevelStem> withOverworld(Registry<DimensionType> $$0, Registry<LevelStem> $$1, ChunkGenerator $$2) {
        LevelStem $$3 = $$1.get(LevelStem.OVERWORLD);
        Holder<DimensionType> $$4 = $$3 == null ? $$0.getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : $$3.type();
        return WorldDimensions.withOverworld($$1, $$4, $$2);
    }

    public static Registry<LevelStem> withOverworld(Registry<LevelStem> $$0, Holder<DimensionType> $$1, ChunkGenerator $$2) {
        MappedRegistry<LevelStem> $$3 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.experimental());
        $$3.register(LevelStem.OVERWORLD, new LevelStem($$1, $$2), Lifecycle.stable());
        for (Map.Entry $$4 : $$0.entrySet()) {
            ResourceKey $$5 = (ResourceKey)$$4.getKey();
            if ($$5 == LevelStem.OVERWORLD) continue;
            $$3.register($$5, (LevelStem)((Object)$$4.getValue()), $$0.lifecycle((LevelStem)((Object)$$4.getValue())));
        }
        return $$3.freeze();
    }

    public ChunkGenerator overworld() {
        LevelStem $$0 = this.dimensions.get(LevelStem.OVERWORLD);
        if ($$0 == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
        return $$0.generator();
    }

    public Optional<LevelStem> get(ResourceKey<LevelStem> $$0) {
        return this.dimensions.getOptional($$0);
    }

    public ImmutableSet<ResourceKey<Level>> levels() {
        return (ImmutableSet)this.dimensions().entrySet().stream().map(Map.Entry::getKey).map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebug() {
        return this.overworld() instanceof DebugLevelSource;
    }

    private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> $$02) {
        return (PrimaryLevelData.SpecialWorldProperty)((Object)$$02.getOptional(LevelStem.OVERWORLD).map($$0 -> {
            ChunkGenerator $$1 = $$0.generator();
            if ($$1 instanceof DebugLevelSource) {
                return PrimaryLevelData.SpecialWorldProperty.DEBUG;
            }
            if ($$1 instanceof FlatLevelSource) {
                return PrimaryLevelData.SpecialWorldProperty.FLAT;
            }
            return PrimaryLevelData.SpecialWorldProperty.NONE;
        }).orElse((Object)PrimaryLevelData.SpecialWorldProperty.NONE));
    }

    static Lifecycle checkStability(ResourceKey<LevelStem> $$0, LevelStem $$1) {
        return WorldDimensions.isVanillaLike($$0, $$1) ? Lifecycle.stable() : Lifecycle.experimental();
    }

    private static boolean isVanillaLike(ResourceKey<LevelStem> $$0, LevelStem $$1) {
        if ($$0 == LevelStem.OVERWORLD) {
            return WorldDimensions.isStableOverworld($$1);
        }
        if ($$0 == LevelStem.NETHER) {
            return WorldDimensions.isStableNether($$1);
        }
        if ($$0 == LevelStem.END) {
            return WorldDimensions.isStableEnd($$1);
        }
        return false;
    }

    private static boolean isStableOverworld(LevelStem $$0) {
        MultiNoiseBiomeSource $$2;
        Holder<DimensionType> $$1 = $$0.type();
        if (!$$1.is(BuiltinDimensionTypes.OVERWORLD) && !$$1.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
            return false;
        }
        BiomeSource biomeSource = $$0.generator().getBiomeSource();
        return !(biomeSource instanceof MultiNoiseBiomeSource) || ($$2 = (MultiNoiseBiomeSource)biomeSource).stable(MultiNoiseBiomeSource.Preset.OVERWORLD);
    }

    private static boolean isStableNether(LevelStem $$0) {
        MultiNoiseBiomeSource $$2;
        NoiseBasedChunkGenerator $$1;
        Object object;
        return $$0.type().is(BuiltinDimensionTypes.NETHER) && (object = $$0.generator()) instanceof NoiseBasedChunkGenerator && ($$1 = (NoiseBasedChunkGenerator)object).stable(NoiseGeneratorSettings.NETHER) && (object = $$1.getBiomeSource()) instanceof MultiNoiseBiomeSource && ($$2 = (MultiNoiseBiomeSource)object).stable(MultiNoiseBiomeSource.Preset.NETHER);
    }

    private static boolean isStableEnd(LevelStem $$0) {
        NoiseBasedChunkGenerator $$1;
        ChunkGenerator chunkGenerator;
        return $$0.type().is(BuiltinDimensionTypes.END) && (chunkGenerator = $$0.generator()) instanceof NoiseBasedChunkGenerator && ($$1 = (NoiseBasedChunkGenerator)chunkGenerator).stable(NoiseGeneratorSettings.END) && $$1.getBiomeSource() instanceof TheEndBiomeSource;
    }

    public Complete bake(Registry<LevelStem> $$0) {
        Stream $$12 = Stream.concat((Stream)$$0.registryKeySet().stream(), (Stream)this.dimensions.registryKeySet().stream()).distinct();
        ArrayList $$2 = new ArrayList();
        WorldDimensions.keysInOrder((Stream<ResourceKey<LevelStem>>)$$12).forEach(arg_0 -> this.lambda$bake$5($$0, (List)$$2, arg_0));
        Lifecycle $$3 = $$2.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
        MappedRegistry<LevelStem> $$4 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, $$3);
        $$2.forEach($$1 -> {
            record Entry(ResourceKey<LevelStem> key, LevelStem value) {
                Lifecycle lifecycle() {
                    return WorldDimensions.checkStability(this.key, this.value);
                }
            }
            $$4.register($$1.key, $$1.value, $$1.lifecycle());
        });
        Registry $$5 = $$4.freeze();
        PrimaryLevelData.SpecialWorldProperty $$6 = WorldDimensions.specialWorldProperty($$5);
        return new Complete($$5.freeze(), $$6);
    }

    private /* synthetic */ void lambda$bake$5(Registry $$0, List $$1, ResourceKey $$22) {
        $$0.getOptional($$22).or(() -> this.dimensions.getOptional($$22)).ifPresent($$2 -> {
            record Entry(ResourceKey<LevelStem> key, LevelStem value) {
                Lifecycle lifecycle() {
                    return WorldDimensions.checkStability(this.key, this.value);
                }
            }
            $$1.add((Object)new Entry($$22, (LevelStem)((Object)$$2)));
        });
    }

    public record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
        public Lifecycle lifecycle() {
            return this.dimensions.registryLifecycle();
        }

        public RegistryAccess.Frozen dimensionsRegistryAccess() {
            return new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions)).freeze();
        }
    }
}