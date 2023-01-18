/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.world.level.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class AmbientParticleSettings {
    public static final Codec<AmbientParticleSettings> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ParticleTypes.CODEC.fieldOf("options").forGetter($$0 -> $$0.options), (App)Codec.FLOAT.fieldOf("probability").forGetter($$0 -> Float.valueOf((float)$$0.probability))).apply((Applicative)$$02, AmbientParticleSettings::new));
    private final ParticleOptions options;
    private final float probability;

    public AmbientParticleSettings(ParticleOptions $$0, float $$1) {
        this.options = $$0;
        this.probability = $$1;
    }

    public ParticleOptions getOptions() {
        return this.options;
    }

    public boolean canSpawn(RandomSource $$0) {
        return $$0.nextFloat() <= this.probability;
    }
}