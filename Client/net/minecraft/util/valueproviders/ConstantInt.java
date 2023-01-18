/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Integer
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
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class ConstantInt
extends IntProvider {
    public static final ConstantInt ZERO = new ConstantInt(0);
    public static final Codec<ConstantInt> CODEC = Codec.either((Codec)Codec.INT, (Codec)RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("value").forGetter($$0 -> $$0.value)).apply((Applicative)$$02, ConstantInt::new))).xmap($$02 -> (ConstantInt)$$02.map(ConstantInt::of, $$0 -> $$0), $$0 -> Either.left((Object)$$0.value));
    private final int value;

    public static ConstantInt of(int $$0) {
        if ($$0 == 0) {
            return ZERO;
        }
        return new ConstantInt($$0);
    }

    private ConstantInt(int $$0) {
        this.value = $$0;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int sample(RandomSource $$0) {
        return this.value;
    }

    @Override
    public int getMinValue() {
        return this.value;
    }

    @Override
    public int getMaxValue() {
        return this.value;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CONSTANT;
    }

    public String toString() {
        return Integer.toString((int)this.value);
    }
}