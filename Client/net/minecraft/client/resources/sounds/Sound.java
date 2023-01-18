/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;

public class Sound
implements Weighted<Sound> {
    public static final FileToIdConverter SOUND_LISTER = new FileToIdConverter("sounds", ".ogg");
    private final ResourceLocation location;
    private final SampledFloat volume;
    private final SampledFloat pitch;
    private final int weight;
    private final Type type;
    private final boolean stream;
    private final boolean preload;
    private final int attenuationDistance;

    public Sound(String $$0, SampledFloat $$1, SampledFloat $$2, int $$3, Type $$4, boolean $$5, boolean $$6, int $$7) {
        this.location = new ResourceLocation($$0);
        this.volume = $$1;
        this.pitch = $$2;
        this.weight = $$3;
        this.type = $$4;
        this.stream = $$5;
        this.preload = $$6;
        this.attenuationDistance = $$7;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public ResourceLocation getPath() {
        return SOUND_LISTER.idToFile(this.location);
    }

    public SampledFloat getVolume() {
        return this.volume;
    }

    public SampledFloat getPitch() {
        return this.pitch;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Sound getSound(RandomSource $$0) {
        return this;
    }

    @Override
    public void preloadIfRequired(SoundEngine $$0) {
        if (this.preload) {
            $$0.requestPreload(this);
        }
    }

    public Type getType() {
        return this.type;
    }

    public boolean shouldStream() {
        return this.stream;
    }

    public boolean shouldPreload() {
        return this.preload;
    }

    public int getAttenuationDistance() {
        return this.attenuationDistance;
    }

    public String toString() {
        return "Sound[" + this.location + "]";
    }

    public static enum Type {
        FILE("file"),
        SOUND_EVENT("event");

        private final String name;

        private Type(String $$0) {
            this.name = $$0;
        }

        @Nullable
        public static Type getByName(String $$0) {
            for (Type $$1 : Type.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            return null;
        }
    }
}