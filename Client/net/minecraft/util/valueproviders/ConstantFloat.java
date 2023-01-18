/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.util.valueproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

public class ConstantFloat
extends FloatProvider {
    public static final ConstantFloat ZERO = new ConstantFloat(0.0f);
    public static final Codec<ConstantFloat> CODEC = Codec.either((Codec)Codec.FLOAT, (Codec)RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("value").forGetter($$0 -> Float.valueOf((float)$$0.value))).apply((Applicative)$$02, ConstantFloat::new))).xmap($$02 -> (ConstantFloat)$$02.map(ConstantFloat::of, $$0 -> $$0), $$0 -> Either.left((Object)Float.valueOf((float)$$0.value)));
    private final float value;

    public static ConstantFloat of(float $$0) {
        if ($$0 == 0.0f) {
            return ZERO;
        }
        return new ConstantFloat($$0);
    }

    private ConstantFloat(float $$0) {
        this.value = $$0;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public float sample(RandomSource $$0) {
        return this.value;
    }

    @Override
    public float getMinValue() {
        return this.value;
    }

    @Override
    public float getMaxValue() {
        return this.value + 1.0f;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CONSTANT;
    }

    public String toString() {
        return Float.toString((float)this.value);
    }
}