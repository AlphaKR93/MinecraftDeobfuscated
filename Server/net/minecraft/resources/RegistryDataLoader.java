/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  java.io.BufferedReader
 *  java.io.PrintWriter
 *  java.io.Reader
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.stream.Collector
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class RegistryDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final List<RegistryData<?>> WORLDGEN_REGISTRIES = List.of((Object[])new RegistryData[]{new RegistryData<DimensionType>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC), new RegistryData<Biome>(Registries.BIOME, Biome.DIRECT_CODEC), new RegistryData<ChatType>(Registries.CHAT_TYPE, ChatType.CODEC), new RegistryData(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC), new RegistryData(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC), new RegistryData<PlacedFeature>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC), new RegistryData<Structure>(Registries.STRUCTURE, Structure.DIRECT_CODEC), new RegistryData<StructureSet>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC), new RegistryData<StructureProcessorList>(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC), new RegistryData<StructureTemplatePool>(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC), new RegistryData<NoiseGeneratorSettings>(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC), new RegistryData<NormalNoise.NoiseParameters>(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC), new RegistryData<DensityFunction>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC), new RegistryData<WorldPreset>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC), new RegistryData<FlatLevelGeneratorPreset>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC), new RegistryData<TrimPattern>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryData<TrimMaterial>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC)});
    public static final List<RegistryData<?>> DIMENSION_REGISTRIES = List.of(new RegistryData<LevelStem>(Registries.LEVEL_STEM, LevelStem.CODEC));

    public static RegistryAccess.Frozen load(ResourceManager $$0, RegistryAccess $$1, List<RegistryData<?>> $$22) {
        HashMap $$3 = new HashMap();
        List $$4 = $$22.stream().map(arg_0 -> RegistryDataLoader.lambda$load$0((Map)$$3, arg_0)).toList();
        RegistryOps.RegistryInfoLookup $$5 = RegistryDataLoader.createContext($$1, $$4);
        $$4.forEach($$2 -> ((Loader)$$2.getSecond()).load($$0, $$5));
        $$4.forEach(arg_0 -> RegistryDataLoader.lambda$load$2((Map)$$3, arg_0));
        if (!$$3.isEmpty()) {
            RegistryDataLoader.logErrors($$3);
            throw new IllegalStateException("Failed to load registries due to above errors");
        }
        return new RegistryAccess.ImmutableRegistryAccess($$4.stream().map(Pair::getFirst).toList()).freeze();
    }

    private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess $$0, List<Pair<WritableRegistry<?>, Loader>> $$1) {
        HashMap $$2 = new HashMap();
        $$0.registries().forEach(arg_0 -> RegistryDataLoader.lambda$createContext$3((Map)$$2, arg_0));
        $$1.forEach(arg_0 -> RegistryDataLoader.lambda$createContext$4((Map)$$2, arg_0));
        return new RegistryOps.RegistryInfoLookup((Map)$$2){
            final /* synthetic */ Map val$result;
            {
                this.val$result = map;
            }

            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
                return Optional.ofNullable((Object)((Object)((RegistryOps.RegistryInfo)((Object)this.val$result.get($$0)))));
            }
        };
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> $$0) {
        return new RegistryOps.RegistryInfo<T>($$0.asLookup(), $$0.createRegistrationLookup(), $$0.registryLifecycle());
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(Registry<T> $$0) {
        return new RegistryOps.RegistryInfo<T>($$0.asLookup(), $$0.asTagAddingLookup(), $$0.registryLifecycle());
    }

    private static void logErrors(Map<ResourceKey<?>, Exception> $$02) {
        StringWriter $$1 = new StringWriter();
        PrintWriter $$2 = new PrintWriter((Writer)$$1);
        Map $$3 = (Map)$$02.entrySet().stream().collect(Collectors.groupingBy($$0 -> ((ResourceKey)$$0.getKey()).registry(), (Collector)Collectors.toMap($$0 -> ((ResourceKey)$$0.getKey()).location(), Map.Entry::getValue)));
        $$3.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach($$12 -> {
            $$2.printf("> Errors in registry %s:%n", new Object[]{$$12.getKey()});
            ((Map)$$12.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach($$1 -> {
                $$2.printf(">> Errors in element %s:%n", new Object[]{$$1.getKey()});
                ((Exception)((Object)$$1.getValue())).printStackTrace($$2);
            });
        });
        $$2.flush();
        LOGGER.error("Registry loading errors:\n{}", (Object)$$1);
    }

    private static String registryDirPath(ResourceLocation $$0) {
        return $$0.getPath();
    }

    static <E> void loadRegistryContents(RegistryOps.RegistryInfoLookup $$02, ResourceManager $$1, ResourceKey<? extends Registry<E>> $$2, WritableRegistry<E> $$3, Decoder<E> $$4, Map<ResourceKey<?>, Exception> $$5) {
        String $$6 = RegistryDataLoader.registryDirPath($$2.location());
        FileToIdConverter $$7 = FileToIdConverter.json($$6);
        RegistryOps $$8 = RegistryOps.create(JsonOps.INSTANCE, $$02);
        for (Map.Entry $$9 : $$7.listMatchingResources($$1).entrySet()) {
            ResourceLocation $$10 = (ResourceLocation)$$9.getKey();
            ResourceKey $$11 = ResourceKey.create($$2, $$7.fileToId($$10));
            Resource $$12 = (Resource)$$9.getValue();
            try {
                BufferedReader $$13 = $$12.openAsReader();
                try {
                    JsonElement $$14 = JsonParser.parseReader((Reader)$$13);
                    DataResult $$15 = $$4.parse($$8, (Object)$$14);
                    Object $$16 = $$15.getOrThrow(false, $$0 -> {});
                    $$3.register($$11, $$16, $$12.isBuiltin() ? Lifecycle.stable() : $$15.lifecycle());
                }
                finally {
                    if ($$13 == null) continue;
                    $$13.close();
                }
            }
            catch (Exception $$17) {
                $$5.put($$11, (Object)new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Failed to parse %s from pack %s", (Object[])new Object[]{$$10, $$12.sourcePackId()}), (Throwable)$$17));
            }
        }
    }

    private static /* synthetic */ void lambda$createContext$4(Map $$0, Pair $$1) {
        $$0.put((Object)((WritableRegistry)$$1.getFirst()).key(), RegistryDataLoader.createInfoForNewRegistry((WritableRegistry)$$1.getFirst()));
    }

    private static /* synthetic */ void lambda$createContext$3(Map $$0, RegistryAccess.RegistryEntry $$1) {
        $$0.put($$1.key(), RegistryDataLoader.createInfoForContextRegistry($$1.value()));
    }

    private static /* synthetic */ void lambda$load$2(Map $$0, Pair $$1) {
        Registry $$2 = (Registry)$$1.getFirst();
        try {
            $$2.freeze();
        }
        catch (Exception $$3) {
            $$0.put($$2.key(), (Object)$$3);
        }
    }

    private static /* synthetic */ Pair lambda$load$0(Map $$0, RegistryData $$1) {
        return $$1.create(Lifecycle.stable(), $$0);
    }

    static interface Loader {
        public void load(ResourceManager var1, RegistryOps.RegistryInfoLookup var2);
    }

    public record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec) {
        Pair<WritableRegistry<?>, Loader> create(Lifecycle $$0, Map<ResourceKey<?>, Exception> $$1) {
            MappedRegistry $$22 = new MappedRegistry(this.key, $$0);
            Loader $$32 = ($$2, $$3) -> RegistryDataLoader.loadRegistryContents($$3, $$2, this.key, $$22, this.elementCodec, $$1);
            return Pair.of($$22, (Object)$$32);
        }
    }
}