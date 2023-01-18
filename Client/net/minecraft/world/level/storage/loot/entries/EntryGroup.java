/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup
extends CompositeEntryBase {
    EntryGroup(LootPoolEntryContainer[] $$0, LootItemCondition[] $$1) {
        super($$0, $$1);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.GROUP;
    }

    @Override
    protected ComposableEntryContainer compose(ComposableEntryContainer[] $$0) {
        switch ($$0.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return $$0[0];
            }
            case 2: {
                ComposableEntryContainer $$12 = $$0[0];
                ComposableEntryContainer $$22 = $$0[1];
                return ($$2, $$3) -> {
                    $$12.expand($$2, (Consumer<LootPoolEntry>)$$3);
                    $$22.expand($$2, (Consumer<LootPoolEntry>)$$3);
                    return true;
                };
            }
        }
        return ($$1, $$2) -> {
            for (ComposableEntryContainer $$3 : $$0) {
                $$3.expand($$1, (Consumer<LootPoolEntry>)$$2);
            }
            return true;
        };
    }

    public static Builder list(LootPoolEntryContainer.Builder<?> ... $$0) {
        return new Builder($$0);
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
        public Builder append(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new EntryGroup((LootPoolEntryContainer[])this.entries.toArray((Object[])new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}