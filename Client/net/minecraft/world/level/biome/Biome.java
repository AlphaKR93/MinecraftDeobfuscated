/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  java.lang.Deprecated
 *  java.lang.Float
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.ThreadLocal
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class Biome {
    public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ClimateSettings.CODEC.forGetter($$0 -> $$0.climateSettings), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter($$0 -> $$0.specialEffects), (App)BiomeGenerationSettings.CODEC.forGetter($$0 -> $$0.generationSettings), (App)MobSpawnSettings.CODEC.forGetter($$0 -> $$0.mobSettings)).apply((Applicative)$$02, Biome::new));
    public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ClimateSettings.CODEC.forGetter($$0 -> $$0.climateSettings), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter($$0 -> $$0.specialEffects)).apply((Applicative)$$02, ($$0, $$1) -> new Biome((ClimateSettings)((Object)((Object)$$0)), (BiomeSpecialEffects)$$1, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY)));
    public static final Codec<Holder<Biome>> CODEC = RegistryFileCodec.create(Registries.BIOME, DIRECT_CODEC);
    public static final Codec<HolderSet<Biome>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.BIOME, DIRECT_CODEC);
    private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(1234L)), (List<Integer>)ImmutableList.of((Object)0));
    static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(3456L)), (List<Integer>)ImmutableList.of((Object)-2, (Object)-1, (Object)0));
    @Deprecated(forRemoval=true)
    public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(2345L)), (List<Integer>)ImmutableList.of((Object)0));
    private static final int TEMPERATURE_CACHE_SIZE = 1024;
    private final ClimateSettings climateSettings;
    private final BiomeGenerationSettings generationSettings;
    private final MobSpawnSettings mobSettings;
    private final BiomeSpecialEffects specialEffects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> (Long2FloatLinkedOpenHashMap)Util.make(() -> {
        Long2FloatLinkedOpenHashMap $$0 = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue(Float.NaN);
        return $$0;
    }));

    Biome(ClimateSettings $$0, BiomeSpecialEffects $$1, BiomeGenerationSettings $$2, MobSpawnSettings $$3) {
        this.climateSettings = $$0;
        this.generationSettings = $$2;
        this.mobSettings = $$3;
        this.specialEffects = $$1;
    }

    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    public MobSpawnSettings getMobSettings() {
        return this.mobSettings;
    }

    public Precipitation getPrecipitation() {
        return this.climateSettings.precipitation;
    }

    public boolean isHumid() {
        return this.getDownfall() > 0.85f;
    }

    private float getHeightAdjustedTemperature(BlockPos $$0) {
        float $$1 = this.climateSettings.temperatureModifier.modifyTemperature($$0, this.getBaseTemperature());
        if ($$0.getY() > 80) {
            float $$2 = (float)(TEMPERATURE_NOISE.getValue((float)$$0.getX() / 8.0f, (float)$$0.getZ() / 8.0f, false) * 8.0);
            return $$1 - ($$2 + (float)$$0.getY() - 80.0f) * 0.05f / 40.0f;
        }
        return $$1;
    }

    @Deprecated
    private float getTemperature(BlockPos $$0) {
        long $$1 = $$0.asLong();
        Long2FloatLinkedOpenHashMap $$2 = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
        float $$3 = $$2.get($$1);
        if (!Float.isNaN((float)$$3)) {
            return $$3;
        }
        float $$4 = this.getHeightAdjustedTemperature($$0);
        if ($$2.size() == 1024) {
            $$2.removeFirstFloat();
        }
        $$2.put($$1, $$4);
        return $$4;
    }

    public boolean shouldFreeze(LevelReader $$0, BlockPos $$1) {
        return this.shouldFreeze($$0, $$1, true);
    }

    public boolean shouldFreeze(LevelReader $$0, BlockPos $$1, boolean $$2) {
        if (this.warmEnoughToRain($$1)) {
            return false;
        }
        if ($$1.getY() >= $$0.getMinBuildHeight() && $$1.getY() < $$0.getMaxBuildHeight() && $$0.getBrightness(LightLayer.BLOCK, $$1) < 10) {
            BlockState $$3 = $$0.getBlockState($$1);
            FluidState $$4 = $$0.getFluidState($$1);
            if ($$4.getType() == Fluids.WATER && $$3.getBlock() instanceof LiquidBlock) {
                boolean $$5;
                if (!$$2) {
                    return true;
                }
                boolean bl = $$5 = $$0.isWaterAt((BlockPos)$$1.west()) && $$0.isWaterAt((BlockPos)$$1.east()) && $$0.isWaterAt((BlockPos)$$1.north()) && $$0.isWaterAt((BlockPos)$$1.south());
                if (!$$5) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean coldEnoughToSnow(BlockPos $$0) {
        return !this.warmEnoughToRain($$0);
    }

    public boolean warmEnoughToRain(BlockPos $$0) {
        return this.getTemperature($$0) >= 0.15f;
    }

    public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPos $$0) {
        return this.getTemperature($$0) > 0.1f;
    }

    public boolean shouldSnowGolemBurn(BlockPos $$0) {
        return this.getTemperature($$0) > 1.0f;
    }

    public boolean shouldSnow(LevelReader $$0, BlockPos $$1) {
        BlockState $$2;
        if (this.warmEnoughToRain($$1)) {
            return false;
        }
        return $$1.getY() >= $$0.getMinBuildHeight() && $$1.getY() < $$0.getMaxBuildHeight() && $$0.getBrightness(LightLayer.BLOCK, $$1) < 10 && (($$2 = $$0.getBlockState($$1)).isAir() || $$2.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive($$0, $$1);
    }

    public BiomeGenerationSettings getGenerationSettings() {
        return this.generationSettings;
    }

    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    public int getGrassColor(double $$0, double $$1) {
        int $$2 = (Integer)this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
        return this.specialEffects.getGrassColorModifier().modifyColor($$0, $$1, $$2);
    }

    private int getGrassColorFromTexture() {
        double $$0 = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double $$1 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return GrassColor.get($$0, $$1);
    }

    public int getFoliageColor() {
        return (Integer)this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
    }

    private int getFoliageColorFromTexture() {
        double $$0 = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double $$1 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return FoliageColor.get($$0, $$1);
    }

    public float getDownfall() {
        return this.climateSettings.downfall;
    }

    public float getBaseTemperature() {
        return this.climateSettings.temperature;
    }

    public BiomeSpecialEffects getSpecialEffects() {
        return this.specialEffects;
    }

    public int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    public int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    public Optional<AmbientParticleSettings> getAmbientParticle() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    public Optional<Holder<SoundEvent>> getAmbientLoop() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    public Optional<AmbientMoodSettings> getAmbientMood() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditions() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    public Optional<Music> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }

    record ClimateSettings(Precipitation precipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
        public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Precipitation.CODEC.fieldOf("precipitation").forGetter($$0 -> $$0.precipitation), (App)Codec.FLOAT.fieldOf("temperature").forGetter($$0 -> Float.valueOf((float)$$0.temperature)), (App)TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", (Object)TemperatureModifier.NONE).forGetter($$0 -> $$0.temperatureModifier), (App)Codec.FLOAT.fieldOf("downfall").forGetter($$0 -> Float.valueOf((float)$$0.downfall))).apply((Applicative)$$02, ClimateSettings::new));
    }

    public static enum Precipitation implements StringRepresentable
    {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<Precipitation> CODEC;
        private final String name;

        private Precipitation(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Precipitation::values));
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum TemperatureModifier implements StringRepresentable
    {
        NONE("none"){

            @Override
            public float modifyTemperature(BlockPos $$0, float $$1) {
                return $$1;
            }
        }
        ,
        FROZEN("frozen"){

            @Override
            public float modifyTemperature(BlockPos $$0, float $$1) {
                double $$5;
                double $$3;
                double $$2 = FROZEN_TEMPERATURE_NOISE.getValue((double)$$0.getX() * 0.05, (double)$$0.getZ() * 0.05, false) * 7.0;
                double $$4 = $$2 + ($$3 = BIOME_INFO_NOISE.getValue((double)$$0.getX() * 0.2, (double)$$0.getZ() * 0.2, false));
                if ($$4 < 0.3 && ($$5 = BIOME_INFO_NOISE.getValue((double)$$0.getX() * 0.09, (double)$$0.getZ() * 0.09, false)) < 0.8) {
                    return 0.2f;
                }
                return $$1;
            }
        };

        private final String name;
        public static final Codec<TemperatureModifier> CODEC;

        public abstract float modifyTemperature(BlockPos var1, float var2);

        TemperatureModifier(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)TemperatureModifier::values));
        }
    }

    public static class BiomeBuilder {
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private Float temperature;
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeSpecialEffects specialEffects;
        @Nullable
        private MobSpawnSettings mobSpawnSettings;
        @Nullable
        private BiomeGenerationSettings generationSettings;

        public BiomeBuilder precipitation(Precipitation $$0) {
            this.precipitation = $$0;
            return this;
        }

        public BiomeBuilder temperature(float $$0) {
            this.temperature = Float.valueOf((float)$$0);
            return this;
        }

        public BiomeBuilder downfall(float $$0) {
            this.downfall = Float.valueOf((float)$$0);
            return this;
        }

        public BiomeBuilder specialEffects(BiomeSpecialEffects $$0) {
            this.specialEffects = $$0;
            return this;
        }

        public BiomeBuilder mobSpawnSettings(MobSpawnSettings $$0) {
            this.mobSpawnSettings = $$0;
            return this;
        }

        public BiomeBuilder generationSettings(BiomeGenerationSettings $$0) {
            this.generationSettings = $$0;
            return this;
        }

        public BiomeBuilder temperatureAdjustment(TemperatureModifier $$0) {
            this.temperatureModifier = $$0;
            return this;
        }

        public Biome build() {
            if (this.precipitation == null || this.temperature == null || this.downfall == null || this.specialEffects == null || this.mobSpawnSettings == null || this.generationSettings == null) {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
            return new Biome(new ClimateSettings(this.precipitation, this.temperature.floatValue(), this.temperatureModifier, this.downfall.floatValue()), this.specialEffects, this.generationSettings, this.mobSpawnSettings);
        }

        public String toString() {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
        }
    }
}