/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.Objects
 */
package net.minecraft;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {
    public boolean test(char var1);

    default public CharPredicate and(CharPredicate $$0) {
        Objects.requireNonNull((Object)$$0);
        return $$1 -> this.test($$1) && $$0.test($$1);
    }

    default public CharPredicate negate() {
        return $$0 -> !this.test($$0);
    }

    default public CharPredicate or(CharPredicate $$0) {
        Objects.requireNonNull((Object)$$0);
        return $$1 -> this.test($$1) || $$0.test($$1);
    }
}