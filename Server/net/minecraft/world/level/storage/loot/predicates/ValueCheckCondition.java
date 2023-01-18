/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ValueCheckCondition
implements LootItemCondition {
    final NumberProvider provider;
    final IntRange range;

    ValueCheckCondition(NumberProvider $$0, IntRange $$1) {
        this.provider = $$0;
        this.range = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.VALUE_CHECK;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union((Set)this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
    }

    public boolean test(LootContext $$0) {
        return this.range.test($$0, this.provider.getInt($$0));
    }

    public static LootItemCondition.Builder hasValue(NumberProvider $$0, IntRange $$1) {
        return () -> new ValueCheckCondition($$0, $$1);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ValueCheckCondition> {
        @Override
        public void serialize(JsonObject $$0, ValueCheckCondition $$1, JsonSerializationContext $$2) {
            $$0.add("value", $$2.serialize((Object)$$1.provider));
            $$0.add("range", $$2.serialize((Object)$$1.range));
        }

        @Override
        public ValueCheckCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            NumberProvider $$2 = GsonHelper.getAsObject($$0, "value", $$1, NumberProvider.class);
            IntRange $$3 = GsonHelper.getAsObject($$0, "range", $$1, IntRange.class);
            return new ValueCheckCondition($$2, $$3);
        }
    }
}