/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntryContainer
implements ComposableEntryContainer {
    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;

    protected LootPoolEntryContainer(LootItemCondition[] $$0) {
        this.conditions = $$0;
        this.compositeCondition = LootItemConditions.andConditions($$0);
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.conditions.length; ++$$1) {
            this.conditions[$$1].validate($$0.forChild(".condition[" + $$1 + "]"));
        }
    }

    protected final boolean canRun(LootContext $$0) {
        return this.compositeCondition.test((Object)$$0);
    }

    public abstract LootPoolEntryType getType();

    public static abstract class Serializer<T extends LootPoolEntryContainer>
    implements net.minecraft.world.level.storage.loot.Serializer<T> {
        @Override
        public final void serialize(JsonObject $$0, T $$1, JsonSerializationContext $$2) {
            if (!ArrayUtils.isEmpty((Object[])((LootPoolEntryContainer)$$1).conditions)) {
                $$0.add("conditions", $$2.serialize((Object)((LootPoolEntryContainer)$$1).conditions));
            }
            this.serialize($$0, $$1, $$2);
        }

        @Override
        public final T deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LootItemCondition[] $$2 = GsonHelper.getAsObject($$0, "conditions", new LootItemCondition[0], $$1, LootItemCondition[].class);
            return this.deserializeCustom($$0, $$1, $$2);
        }

        @Override
        public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
    }

    public static abstract class Builder<T extends Builder<T>>
    implements ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions = Lists.newArrayList();

        protected abstract T getThis();

        @Override
        public T when(LootItemCondition.Builder $$0) {
            this.conditions.add((Object)$$0.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[])this.conditions.toArray((Object[])new LootItemCondition[0]);
        }

        public AlternativesEntry.Builder otherwise(Builder<?> $$0) {
            return new AlternativesEntry.Builder(this, $$0);
        }

        public EntryGroup.Builder append(Builder<?> $$0) {
            return new EntryGroup.Builder(this, $$0);
        }

        public SequentialEntry.Builder then(Builder<?> $$0) {
            return new SequentialEntry.Builder(this, $$0);
        }

        public abstract LootPoolEntryContainer build();
    }
}