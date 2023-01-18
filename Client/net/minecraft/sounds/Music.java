/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 */
package net.minecraft.sounds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class Music {
    public static final Codec<Music> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter($$0 -> $$0.event), (App)Codec.INT.fieldOf("min_delay").forGetter($$0 -> $$0.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter($$0 -> $$0.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter($$0 -> $$0.replaceCurrentMusic)).apply((Applicative)$$02, Music::new));
    private final Holder<SoundEvent> event;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public Music(Holder<SoundEvent> $$0, int $$1, int $$2, boolean $$3) {
        this.event = $$0;
        this.minDelay = $$1;
        this.maxDelay = $$2;
        this.replaceCurrentMusic = $$3;
    }

    public Holder<SoundEvent> getEvent() {
        return this.event;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getMaxDelay() {
        return this.maxDelay;
    }

    public boolean replaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}