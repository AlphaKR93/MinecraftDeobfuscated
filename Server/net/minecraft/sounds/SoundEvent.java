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
 *  java.util.Optional
 */
package net.minecraft.sounds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
    public static final Codec<SoundEvent> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("sound_id").forGetter(SoundEvent::getLocation), (App)Codec.FLOAT.optionalFieldOf("range").forGetter(SoundEvent::fixedRange)).apply((Applicative)$$0, SoundEvent::create));
    public static final Codec<Holder<SoundEvent>> CODEC = RegistryFileCodec.create(Registries.SOUND_EVENT, DIRECT_CODEC);
    private static final float DEFAULT_RANGE = 16.0f;
    private final ResourceLocation location;
    private final float range;
    private final boolean newSystem;

    private static SoundEvent create(ResourceLocation $$0, Optional<Float> $$12) {
        return (SoundEvent)$$12.map($$1 -> SoundEvent.createFixedRangeEvent($$0, $$1.floatValue())).orElseGet(() -> SoundEvent.createVariableRangeEvent($$0));
    }

    public static SoundEvent createVariableRangeEvent(ResourceLocation $$0) {
        return new SoundEvent($$0, 16.0f, false);
    }

    public static SoundEvent createFixedRangeEvent(ResourceLocation $$0, float $$1) {
        return new SoundEvent($$0, $$1, true);
    }

    private SoundEvent(ResourceLocation $$0, float $$1, boolean $$2) {
        this.location = $$0;
        this.range = $$1;
        this.newSystem = $$2;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public float getRange(float $$0) {
        if (this.newSystem) {
            return this.range;
        }
        return $$0 > 1.0f ? 16.0f * $$0 : 16.0f;
    }

    private Optional<Float> fixedRange() {
        return this.newSystem ? Optional.of((Object)Float.valueOf((float)this.range)) : Optional.empty();
    }

    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.location);
        $$0.writeOptional(this.fixedRange(), FriendlyByteBuf::writeFloat);
    }

    public static SoundEvent readFromNetwork(FriendlyByteBuf $$0) {
        ResourceLocation $$1 = $$0.readResourceLocation();
        Optional $$2 = $$0.readOptional(FriendlyByteBuf::readFloat);
        return SoundEvent.create($$1, $$2);
    }
}