/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T>
extends Iterable<T> {
    public static final int DEFAULT = -1;

    public int getId(T var1);

    @Nullable
    public T byId(int var1);

    default public T byIdOrThrow(int $$0) {
        T $$1 = this.byId($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("No value with id " + $$0);
        }
        return $$1;
    }

    public int size();
}