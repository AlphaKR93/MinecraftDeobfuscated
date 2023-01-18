/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry
extends CompositeEntryBase {
    AlternativesEntry(LootPoolEntryContainer[] $$0, LootItemCondition[] $$1) {
        super($$0, $$1);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ALTERNATIVES;
    }

    @Override
    protected ComposableEntryContainer compose(ComposableEntryContainer[] $$0) {
        switch ($$0.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return $$0[0];
            }
            case 2: {
                return $$0[0].or($$0[1]);
            }
        }
        return ($$1, $$2) -> {
            for (ComposableEntryContainer $$3 : $$0) {
                if (!$$3.expand($$1, (Consumer<LootPoolEntry>)$$2)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.children.length - 1; ++$$1) {
            if (!ArrayUtils.isEmpty((Object[])this.children[$$1].conditions)) continue;
            $$0.reportProblem("Unreachable entry!");
        }
    }

    public static Builder alternatives(LootPoolEntryContainer.Builder<?> ... $$0) {
        return new Builder($$0);
    }

    public static <E> Builder alternatives(Collection<E> $$0, Function<E, LootPoolEntryContainer.Builder<?>> $$1) {
        return new Builder((LootPoolEntryContainer.Builder[])$$0.stream().map(arg_0 -> $$1.apply(arg_0)).toArray(LootPoolEntryContainer.Builder[]::new));
    }

    public static class Builder
    extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        public Builder(LootPoolEntryContainer.Builder<?> ... $$0) {
            for (LootPoolEntryContainer.Builder<?> $$1 : $$0) {
                this.entries.add((Object)$$1.build());
            }
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public Builder otherwise(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new AlternativesEntry((LootPoolEntryContainer[])this.entries.toArray((Object[])new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}