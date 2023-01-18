/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Function;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
    public T when(LootItemCondition.Builder var1);

    default public <E> T when(Iterable<E> $$0, Function<E, LootItemCondition.Builder> $$1) {
        T $$2 = this.unwrap();
        for (Object $$3 : $$0) {
            $$2 = $$2.when((LootItemCondition.Builder)$$1.apply($$3));
        }
        return $$2;
    }

    public T unwrap();
}