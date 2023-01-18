/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.util.valueproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class BiasedToBottomInt
extends IntProvider {
    public static final Codec<BiasedToBottomInt> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("min_inclusive").forGetter($$0 -> $$0.minInclusive), (App)Codec.INT.fieldOf("max_inclusive").forGetter($$0 -> $$0.maxInclusive)).apply((Applicative)$$02, BiasedToBottomInt::new)).comapFlatMap($$0 -> {
        if ($$0.maxInclusive < $$0.minInclusive) {
            return DataResult.error((String)("Max must be at least min, min_inclusive: " + $$0.minInclusive + ", max_inclusive: " + $$0.maxInclusive));
        }
        return DataResult.success((Object)$$0);
    }, Function.identity());
    private final int minInclusive;
    private final int maxInclusive;

    private BiasedToBottomInt(int $$0, int $$1) {
        this.minInclusive = $$0;
        this.maxInclusive = $$1;
    }

    public static BiasedToBottomInt of(int $$0, int $$1) {
        return new BiasedToBottomInt($$0, $$1);
    }

    @Override
    public int sample(RandomSource $$0) {
        return this.minInclusive + $$0.nextInt($$0.nextInt(this.maxInclusive - this.minInclusive + 1) + 1);
    }

    @Override
    public int getMinValue() {
        return this.minInclusive;
    }

    @Override
    public int getMaxValue() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}