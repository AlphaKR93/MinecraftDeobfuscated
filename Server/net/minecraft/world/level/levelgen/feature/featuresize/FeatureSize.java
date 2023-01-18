/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.OptionalInt
 */
package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;

public abstract class FeatureSize {
    public static final Codec<FeatureSize> CODEC = BuiltInRegistries.FEATURE_SIZE_TYPE.byNameCodec().dispatch(FeatureSize::type, FeatureSizeType::codec);
    protected static final int MAX_WIDTH = 16;
    protected final OptionalInt minClippedHeight;

    protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> minClippedHeightCodec() {
        return Codec.intRange((int)0, (int)80).optionalFieldOf("min_clipped_height").xmap($$0 -> (OptionalInt)$$0.map(OptionalInt::of).orElse((Object)OptionalInt.empty()), $$0 -> $$0.isPresent() ? Optional.of((Object)$$0.getAsInt()) : Optional.empty()).forGetter($$0 -> $$0.minClippedHeight);
    }

    public FeatureSize(OptionalInt $$0) {
        this.minClippedHeight = $$0;
    }

    protected abstract FeatureSizeType<?> type();

    public abstract int getSizeAtHeight(int var1, int var2);

    public OptionalInt minClippedHeight() {
        return this.minClippedHeight;
    }
}