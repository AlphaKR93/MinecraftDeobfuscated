/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class MultiNoiseBiomeSource
extends BiomeSource {
    public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ExtraCodecs.nonEmptyList(RecordCodecBuilder.create($$0 -> $$0.group((App)Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)$$0, Pair::of)).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter($$0 -> $$0.parameters)).apply((Applicative)$$02, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(PresetInstance.CODEC, DIRECT_CODEC).xmap($$0 -> (MultiNoiseBiomeSource)$$0.map(PresetInstance::biomeSource, Function.identity()), $$0 -> (Either)$$0.preset().map(Either::left).orElseGet(() -> Either.right((Object)$$0))).codec();
    private final Climate.ParameterList<Holder<Biome>> parameters;
    private final Optional<PresetInstance> preset;

    private MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> $$0) {
        this($$0, (Optional<PresetInstance>)Optional.empty());
    }

    MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> $$0, Optional<PresetInstance> $$1) {
        super((Stream<Holder<Biome>>)$$0.values().stream().map(Pair::getSecond));
        this.preset = $$1;
        this.parameters = $$0;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    private Optional<PresetInstance> preset() {
        return this.preset;
    }

    public boolean stable(Preset $$0) {
        return this.preset.isPresent() && Objects.equals((Object)((PresetInstance)((Object)this.preset.get())).preset(), (Object)$$0);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2, Climate.Sampler $$3) {
        return this.getNoiseBiome($$3.sample($$0, $$1, $$2));
    }

    @VisibleForDebug
    public Holder<Biome> getNoiseBiome(Climate.TargetPoint $$0) {
        return this.parameters.findValue($$0);
    }

    @Override
    public void addDebugInfo(List<String> $$0, BlockPos $$1, Climate.Sampler $$2) {
        int $$3 = QuartPos.fromBlock($$1.getX());
        int $$4 = QuartPos.fromBlock($$1.getY());
        int $$5 = QuartPos.fromBlock($$1.getZ());
        Climate.TargetPoint $$6 = $$2.sample($$3, $$4, $$5);
        float $$7 = Climate.unquantizeCoord($$6.continentalness());
        float $$8 = Climate.unquantizeCoord($$6.erosion());
        float $$9 = Climate.unquantizeCoord($$6.temperature());
        float $$10 = Climate.unquantizeCoord($$6.humidity());
        float $$11 = Climate.unquantizeCoord($$6.weirdness());
        double $$12 = NoiseRouterData.peaksAndValleys($$11);
        OverworldBiomeBuilder $$13 = new OverworldBiomeBuilder();
        $$0.add((Object)("Biome builder PV: " + OverworldBiomeBuilder.getDebugStringForPeaksAndValleys($$12) + " C: " + $$13.getDebugStringForContinentalness($$7) + " E: " + $$13.getDebugStringForErosion($$8) + " T: " + $$13.getDebugStringForTemperature($$9) + " H: " + $$13.getDebugStringForHumidity($$10)));
    }

    record PresetInstance(Preset preset, HolderGetter<Biome> biomes) {
        public static final MapCodec<PresetInstance> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceLocation.CODEC.flatXmap($$0 -> (DataResult)Optional.ofNullable((Object)((Preset)Preset.BY_NAME.get($$0))).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + $$0))), $$0 -> DataResult.success((Object)$$0.name)).fieldOf("preset").stable().forGetter(PresetInstance::preset), RegistryOps.retrieveGetter(Registries.BIOME)).apply((Applicative)$$02, $$02.stable(PresetInstance::new)));

        public MultiNoiseBiomeSource biomeSource() {
            return this.preset.biomeSource(this, true);
        }
    }

    public static class Preset {
        static final Map<ResourceLocation, Preset> BY_NAME = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new ResourceLocation("nether"), (Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>>)((Function)$$0 -> new Climate.ParameterList(ImmutableList.of((Object)Pair.of((Object)((Object)Climate.parameters(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.getOrThrow(Biomes.NETHER_WASTES)), (Object)Pair.of((Object)((Object)Climate.parameters(0.0f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.getOrThrow(Biomes.SOUL_SAND_VALLEY)), (Object)Pair.of((Object)((Object)Climate.parameters(0.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.getOrThrow(Biomes.CRIMSON_FOREST)), (Object)Pair.of((Object)((Object)Climate.parameters(0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f)), $$0.getOrThrow(Biomes.WARPED_FOREST)), (Object)Pair.of((Object)((Object)Climate.parameters(-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.175f)), $$0.getOrThrow(Biomes.BASALT_DELTAS))))));
        public static final Preset OVERWORLD = new Preset(new ResourceLocation("overworld"), (Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>>)((Function)$$0 -> {
            ImmutableList.Builder $$1 = ImmutableList.builder();
            new OverworldBiomeBuilder().addBiomes((Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>)((Consumer)$$2 -> $$1.add((Object)$$2.mapSecond($$0::getOrThrow))));
            return new Climate.ParameterList($$1.build());
        }));
        final ResourceLocation name;
        private final Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>> parameterSource;

        public Preset(ResourceLocation $$0, Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>> $$1) {
            this.name = $$0;
            this.parameterSource = $$1;
            BY_NAME.put((Object)$$0, (Object)this);
        }

        @VisibleForDebug
        public static Stream<Pair<ResourceLocation, Preset>> getPresets() {
            return BY_NAME.entrySet().stream().map($$0 -> Pair.of((Object)((ResourceLocation)$$0.getKey()), (Object)((Preset)$$0.getValue())));
        }

        MultiNoiseBiomeSource biomeSource(PresetInstance $$0, boolean $$1) {
            Climate.ParameterList $$2 = (Climate.ParameterList)this.parameterSource.apply($$0.biomes());
            return new MultiNoiseBiomeSource($$2, (Optional<PresetInstance>)($$1 ? Optional.of((Object)((Object)$$0)) : Optional.empty()));
        }

        public MultiNoiseBiomeSource biomeSource(HolderGetter<Biome> $$0, boolean $$1) {
            return this.biomeSource(new PresetInstance(this, $$0), $$1);
        }

        public MultiNoiseBiomeSource biomeSource(HolderGetter<Biome> $$0) {
            return this.biomeSource($$0, true);
        }

        public Stream<ResourceKey<Biome>> possibleBiomes(HolderGetter<Biome> $$02) {
            return this.biomeSource($$02).possibleBiomes().stream().flatMap($$0 -> $$0.unwrapKey().stream());
        }
    }
}