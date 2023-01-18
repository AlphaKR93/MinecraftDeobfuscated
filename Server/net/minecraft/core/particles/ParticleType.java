/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 */
package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;

public abstract class ParticleType<T extends ParticleOptions> {
    private final boolean overrideLimiter;
    private final ParticleOptions.Deserializer<T> deserializer;

    protected ParticleType(boolean $$0, ParticleOptions.Deserializer<T> $$1) {
        this.overrideLimiter = $$0;
        this.deserializer = $$1;
    }

    public boolean getOverrideLimiter() {
        return this.overrideLimiter;
    }

    public ParticleOptions.Deserializer<T> getDeserializer() {
        return this.deserializer;
    }

    public abstract Codec<T> codec();
}