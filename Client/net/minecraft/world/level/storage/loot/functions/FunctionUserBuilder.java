/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
    public T apply(LootItemFunction.Builder var1);

    default public <E> T apply(Iterable<E> $$0, Function<E, LootItemFunction.Builder> $$1) {
        T $$2 = this.unwrap();
        for (Object $$3 : $$0) {
            $$2 = $$2.apply((LootItemFunction.Builder)$$1.apply($$3));
        }
        return $$2;
    }

    default public <E> T apply(E[] $$0, Function<E, LootItemFunction.Builder> $$1) {
        return this.apply((Iterable<E>)Arrays.asList((Object[])$$0), $$1);
    }

    public T unwrap();
}