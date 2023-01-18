/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public class BiomeGenerationSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings((Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>)ImmutableMap.of(), (List<HolderSet<PlacedFeature>>)ImmutableList.of());
    public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.simpleMap(GenerationStep.Carving.CODEC, (Codec)ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0)))), (Keyable)StringRepresentable.keys(GenerationStep.Carving.values())).fieldOf("carvers").forGetter($$0 -> $$0.carvers), (App)PlacedFeature.LIST_OF_LISTS_CODEC.promotePartial(Util.prefix("Features: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0)))).fieldOf("features").forGetter($$0 -> $$0.features)).apply((Applicative)$$02, BiomeGenerationSettings::new));
    private final Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers;
    private final List<HolderSet<PlacedFeature>> features;
    private final Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures;
    private final Supplier<Set<PlacedFeature>> featureSet;

    BiomeGenerationSettings(Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> $$0, List<HolderSet<PlacedFeature>> $$1) {
        this.carvers = $$0;
        this.features = $$1;
        this.flowerFeatures = Suppliers.memoize(() -> (List)$$1.stream().flatMap(HolderSet::stream).map(Holder::value).flatMap(PlacedFeature::getFeatures).filter($$0 -> $$0.feature() == Feature.FLOWER).collect(ImmutableList.toImmutableList()));
        this.featureSet = Suppliers.memoize(() -> (Set)$$1.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet()));
    }

    public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving $$0) {
        return (Iterable)Objects.requireNonNullElseGet((Object)((Iterable)this.carvers.get((Object)$$0)), List::of);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return (List)this.flowerFeatures.get();
    }

    public List<HolderSet<PlacedFeature>> features() {
        return this.features;
    }

    public boolean hasFeature(PlacedFeature $$0) {
        return ((Set)this.featureSet.get()).contains((Object)$$0);
    }

    public static class Builder
    extends PlainBuilder {
        private final HolderGetter<PlacedFeature> placedFeatures;
        private final HolderGetter<ConfiguredWorldCarver<?>> worldCarvers;

        public Builder(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
            this.placedFeatures = $$0;
            this.worldCarvers = $$1;
        }

        public Builder addFeature(GenerationStep.Decoration $$0, ResourceKey<PlacedFeature> $$1) {
            this.addFeature($$0.ordinal(), this.placedFeatures.getOrThrow($$1));
            return this;
        }

        public Builder addCarver(GenerationStep.Carving $$0, ResourceKey<ConfiguredWorldCarver<?>> $$1) {
            this.addCarver($$0, this.worldCarvers.getOrThrow($$1));
            return this;
        }
    }

    public static class PlainBuilder {
        private final Map<GenerationStep.Carving, List<Holder<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<Holder<PlacedFeature>>> features = Lists.newArrayList();

        public PlainBuilder addFeature(GenerationStep.Decoration $$0, Holder<PlacedFeature> $$1) {
            return this.addFeature($$0.ordinal(), $$1);
        }

        public PlainBuilder addFeature(int $$0, Holder<PlacedFeature> $$1) {
            this.addFeatureStepsUpTo($$0);
            ((List)this.features.get($$0)).add($$1);
            return this;
        }

        public PlainBuilder addCarver(GenerationStep.Carving $$02, Holder<ConfiguredWorldCarver<?>> $$1) {
            ((List)this.carvers.computeIfAbsent((Object)$$02, $$0 -> Lists.newArrayList())).add($$1);
            return this;
        }

        private void addFeatureStepsUpTo(int $$0) {
            while (this.features.size() <= $$0) {
                this.features.add((Object)Lists.newArrayList());
            }
        }

        public BiomeGenerationSettings build() {
            return new BiomeGenerationSettings((Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> HolderSet.direct((List)$$0.getValue()))), (List<HolderSet<PlacedFeature>>)((List)this.features.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList())));
        }
    }
}