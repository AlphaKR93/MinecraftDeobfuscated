/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
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

public class ClampedNormalInt
extends IntProvider {
    public static final Codec<ClampedNormalInt> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("mean").forGetter($$0 -> Float.valueOf((float)$$0.mean)), (App)Codec.FLOAT.fieldOf("deviation").forGetter($$0 -> Float.valueOf((float)$$0.deviation)), (App)Codec.INT.fieldOf("min_inclusive").forGetter($$0 -> $$0.min_inclusive), (App)Codec.INT.fieldOf("max_inclusive").forGetter($$0 -> $$0.max_inclusive)).apply((Applicative)$$02, ClampedNormalInt::new)).comapFlatMap($$0 -> {
        if ($$0.max_inclusive < $$0.min_inclusive) {
            return DataResult.error((String)("Max must be larger than min: [" + $$0.min_inclusive + ", " + $$0.max_inclusive + "]"));
        }
        return DataResult.success((Object)$$0);
    }, Function.identity());
    private final float mean;
    private final float deviation;
    private final int min_inclusive;
    private final int max_inclusive;

    public static ClampedNormalInt of(float $$0, float $$1, int $$2, int $$3) {
        return new ClampedNormalInt($$0, $$1, $$2, $$3);
    }

    private ClampedNormalInt(float $$0, float $$1, int $$2, int $$3) {
        this.mean = $$0;
        this.deviation = $$1;
        this.min_inclusive = $$2;
        this.max_inclusive = $$3;
    }

    @Override
    public int sample(RandomSource $$0) {
        return ClampedNormalInt.sample($$0, this.mean, this.deviation, this.min_inclusive, this.max_inclusive);
    }

    public static int sample(RandomSource $$0, float $$1, float $$2, float $$3, float $$4) {
        return (int)Mth.clamp(Mth.normal($$0, $$1, $$2), $$3, $$4);
    }

    @Override
    public int getMinValue() {
        return this.min_inclusive;
    }

    @Override
    public int getMaxValue() {
        return this.max_inclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min_inclusive + "-" + this.max_inclusive + "]";
    }
}