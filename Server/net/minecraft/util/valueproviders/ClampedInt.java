/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class ClampedInt
extends IntProvider {
    public static final Codec<ClampedInt> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)IntProvider.CODEC.fieldOf("source").forGetter($$0 -> $$0.source), (App)Codec.INT.fieldOf("min_inclusive").forGetter($$0 -> $$0.minInclusive), (App)Codec.INT.fieldOf("max_inclusive").forGetter($$0 -> $$0.maxInclusive)).apply((Applicative)$$02, ClampedInt::new)).comapFlatMap($$0 -> {
        if ($$0.maxInclusive < $$0.minInclusive) {
            return DataResult.error((String)("Max must be at least min, min_inclusive: " + $$0.minInclusive + ", max_inclusive: " + $$0.maxInclusive));
        }
        return DataResult.success((Object)$$0);
    }, Function.identity());
    private final IntProvider source;
    private final int minInclusive;
    private final int maxInclusive;

    public static ClampedInt of(IntProvider $$0, int $$1, int $$2) {
        return new ClampedInt($$0, $$1, $$2);
    }

    public ClampedInt(IntProvider $$0, int $$1, int $$2) {
        this.source = $$0;
        this.minInclusive = $$1;
        this.maxInclusive = $$2;
    }

    @Override
    public int sample(RandomSource $$0) {
        return Mth.clamp(this.source.sample($$0), this.minInclusive, this.maxInclusive);
    }

    @Override
    public int getMinValue() {
        return Math.max((int)this.minInclusive, (int)this.source.getMinValue());
    }

    @Override
    public int getMaxValue() {
        return Math.min((int)this.maxInclusive, (int)this.source.getMaxValue());
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED;
    }
}