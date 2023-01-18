/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;

public class BiomeSpecialEffects {
    public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("fog_color").forGetter($$0 -> $$0.fogColor), (App)Codec.INT.fieldOf("water_color").forGetter($$0 -> $$0.waterColor), (App)Codec.INT.fieldOf("water_fog_color").forGetter($$0 -> $$0.waterFogColor), (App)Codec.INT.fieldOf("sky_color").forGetter($$0 -> $$0.skyColor), (App)Codec.INT.optionalFieldOf("foliage_color").forGetter($$0 -> $$0.foliageColorOverride), (App)Codec.INT.optionalFieldOf("grass_color").forGetter($$0 -> $$0.grassColorOverride), (App)GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", (Object)GrassColorModifier.NONE).forGetter($$0 -> $$0.grassColorModifier), (App)AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter($$0 -> $$0.ambientParticleSettings), (App)SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter($$0 -> $$0.ambientLoopSoundEvent), (App)AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter($$0 -> $$0.ambientMoodSettings), (App)AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter($$0 -> $$0.ambientAdditionsSettings), (App)Music.CODEC.optionalFieldOf("music").forGetter($$0 -> $$0.backgroundMusic)).apply((Applicative)$$02, BiomeSpecialEffects::new));
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final int skyColor;
    private final Optional<Integer> foliageColorOverride;
    private final Optional<Integer> grassColorOverride;
    private final GrassColorModifier grassColorModifier;
    private final Optional<AmbientParticleSettings> ambientParticleSettings;
    private final Optional<Holder<SoundEvent>> ambientLoopSoundEvent;
    private final Optional<AmbientMoodSettings> ambientMoodSettings;
    private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
    private final Optional<Music> backgroundMusic;

    BiomeSpecialEffects(int $$0, int $$1, int $$2, int $$3, Optional<Integer> $$4, Optional<Integer> $$5, GrassColorModifier $$6, Optional<AmbientParticleSettings> $$7, Optional<Holder<SoundEvent>> $$8, Optional<AmbientMoodSettings> $$9, Optional<AmbientAdditionsSettings> $$10, Optional<Music> $$11) {
        this.fogColor = $$0;
        this.waterColor = $$1;
        this.waterFogColor = $$2;
        this.skyColor = $$3;
        this.foliageColorOverride = $$4;
        this.grassColorOverride = $$5;
        this.grassColorModifier = $$6;
        this.ambientParticleSettings = $$7;
        this.ambientLoopSoundEvent = $$8;
        this.ambientMoodSettings = $$9;
        this.ambientAdditionsSettings = $$10;
        this.backgroundMusic = $$11;
    }

    public int getFogColor() {
        return this.fogColor;
    }

    public int getWaterColor() {
        return this.waterColor;
    }

    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    public int getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColorOverride() {
        return this.foliageColorOverride;
    }

    public Optional<Integer> getGrassColorOverride() {
        return this.grassColorOverride;
    }

    public GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.ambientParticleSettings;
    }

    public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
        return this.ambientLoopSoundEvent;
    }

    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.ambientMoodSettings;
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.ambientAdditionsSettings;
    }

    public Optional<Music> getBackgroundMusic() {
        return this.backgroundMusic;
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum GrassColorModifier implements StringRepresentable
    {
        NONE("none"){

            @Override
            public int modifyColor(double $$0, double $$1, int $$2) {
                return $$2;
            }
        }
        ,
        DARK_FOREST("dark_forest"){

            @Override
            public int modifyColor(double $$0, double $$1, int $$2) {
                return ($$2 & 0xFEFEFE) + 2634762 >> 1;
            }
        }
        ,
        SWAMP("swamp"){

            @Override
            public int modifyColor(double $$0, double $$1, int $$2) {
                double $$3 = Biome.BIOME_INFO_NOISE.getValue($$0 * 0.0225, $$1 * 0.0225, false);
                if ($$3 < -0.1) {
                    return 5011004;
                }
                return 6975545;
            }
        };

        private final String name;
        public static final Codec<GrassColorModifier> CODEC;

        public abstract int modifyColor(double var1, double var3, int var5);

        GrassColorModifier(String $$0) {
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
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)GrassColorModifier::values));
        }
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColorOverride = Optional.empty();
        private Optional<Integer> grassColorOverride = Optional.empty();
        private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;
        private Optional<AmbientParticleSettings> ambientParticle = Optional.empty();
        private Optional<Holder<SoundEvent>> ambientLoopSoundEvent = Optional.empty();
        private Optional<AmbientMoodSettings> ambientMoodSettings = Optional.empty();
        private Optional<AmbientAdditionsSettings> ambientAdditionsSettings = Optional.empty();
        private Optional<Music> backgroundMusic = Optional.empty();

        public Builder fogColor(int $$0) {
            this.fogColor = OptionalInt.of((int)$$0);
            return this;
        }

        public Builder waterColor(int $$0) {
            this.waterColor = OptionalInt.of((int)$$0);
            return this;
        }

        public Builder waterFogColor(int $$0) {
            this.waterFogColor = OptionalInt.of((int)$$0);
            return this;
        }

        public Builder skyColor(int $$0) {
            this.skyColor = OptionalInt.of((int)$$0);
            return this;
        }

        public Builder foliageColorOverride(int $$0) {
            this.foliageColorOverride = Optional.of((Object)$$0);
            return this;
        }

        public Builder grassColorOverride(int $$0) {
            this.grassColorOverride = Optional.of((Object)$$0);
            return this;
        }

        public Builder grassColorModifier(GrassColorModifier $$0) {
            this.grassColorModifier = $$0;
            return this;
        }

        public Builder ambientParticle(AmbientParticleSettings $$0) {
            this.ambientParticle = Optional.of((Object)$$0);
            return this;
        }

        public Builder ambientLoopSound(Holder<SoundEvent> $$0) {
            this.ambientLoopSoundEvent = Optional.of($$0);
            return this;
        }

        public Builder ambientMoodSound(AmbientMoodSettings $$0) {
            this.ambientMoodSettings = Optional.of((Object)$$0);
            return this;
        }

        public Builder ambientAdditionsSound(AmbientAdditionsSettings $$0) {
            this.ambientAdditionsSettings = Optional.of((Object)$$0);
            return this;
        }

        public Builder backgroundMusic(@Nullable Music $$0) {
            this.backgroundMusic = Optional.ofNullable((Object)$$0);
            return this;
        }

        public BiomeSpecialEffects build() {
            return new BiomeSpecialEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
        }
    }
}