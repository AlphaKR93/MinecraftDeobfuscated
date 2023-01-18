/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
    public static final ToFloatFunction<Float> IDENTITY = ToFloatFunction.createUnlimited($$0 -> $$0);

    public float apply(C var1);

    public float minValue();

    public float maxValue();

    public static ToFloatFunction<Float> createUnlimited(final Float2FloatFunction $$0) {
        return new ToFloatFunction<Float>(){

            @Override
            public float apply(Float $$02) {
                return ((Float)$$0.apply((Object)$$02)).floatValue();
            }

            @Override
            public float minValue() {
                return Float.NEGATIVE_INFINITY;
            }

            @Override
            public float maxValue() {
                return Float.POSITIVE_INFINITY;
            }
        };
    }

    default public <C2> ToFloatFunction<C2> comap(final Function<C2, C> $$0) {
        final ToFloatFunction $$1 = this;
        return new ToFloatFunction<C2>(){

            @Override
            public float apply(C2 $$02) {
                return $$1.apply($$0.apply($$02));
            }

            @Override
            public float minValue() {
                return $$1.minValue();
            }

            @Override
            public float maxValue() {
                return $$1.maxValue();
            }
        };
    }
}