/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  java.lang.Object
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(Codec<A> codec) {
    public static <A> KeyDispatchDataCodec<A> of(Codec<A> $$0) {
        return new KeyDispatchDataCodec<A>($$0);
    }

    public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> $$0) {
        return new KeyDispatchDataCodec<A>($$0.codec());
    }
}