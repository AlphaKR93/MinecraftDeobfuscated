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
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

public class ClampedNormalFloat
extends FloatProvider {
    public static final Codec<ClampedNormalFloat> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("mean").forGetter($$0 -> Float.valueOf((float)$$0.mean)), (App)Codec.FLOAT.fieldOf("deviation").forGetter($$0 -> Float.valueOf((float)$$0.deviation)), (App)Codec.FLOAT.fieldOf("min").forGetter($$0 -> Float.valueOf((float)$$0.min)), (App)Codec.FLOAT.fieldOf("max").forGetter($$0 -> Float.valueOf((float)$$0.max))).apply((Applicative)$$02, ClampedNormalFloat::new)).comapFlatMap($$0 -> {
        if ($$0.max < $$0.min) {
            return DataResult.error((String)("Max must be larger than min: [" + $$0.min + ", " + $$0.max + "]"));
        }
        return DataResult.success((Object)$$0);
    }, Function.identity());
    private final float mean;
    private final float deviation;
    private final float min;
    private final float max;

    public static ClampedNormalFloat of(float $$0, float $$1, float $$2, float $$3) {
        return new ClampedNormalFloat($$0, $$1, $$2, $$3);
    }

    private ClampedNormalFloat(float $$0, float $$1, float $$2, float $$3) {
        this.mean = $$0;
        this.deviation = $$1;
        this.min = $$2;
        this.max = $$3;
    }

    @Override
    public float sample(RandomSource $$0) {
        return ClampedNormalFloat.sample($$0, this.mean, this.deviation, this.min, this.max);
    }

    public static float sample(RandomSource $$0, float $$1, float $$2, float $$3, float $$4) {
        return Mth.clamp(Mth.normal($$0, $$1, $$2), $$3, $$4);
    }

    @Override
    public float getMinValue() {
        return this.min;
    }

    @Override
    public float getMaxValue() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}