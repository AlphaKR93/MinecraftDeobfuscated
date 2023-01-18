/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.util.function.Supplier
 */
package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

@Deprecated
public class LazyLoadedValue<T> {
    private final Supplier<T> factory = Suppliers.memoize(() -> $$0.get());

    public LazyLoadedValue(Supplier<T> $$0) {
    }

    public T get() {
        return (T)this.factory.get();
    }
}