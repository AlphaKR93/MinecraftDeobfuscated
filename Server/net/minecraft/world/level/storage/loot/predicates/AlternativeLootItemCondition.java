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
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class AlternativeLootItemCondition
implements LootItemCondition {
    final LootItemCondition[] terms;
    private final Predicate<LootContext> composedPredicate;

    AlternativeLootItemCondition(LootItemCondition[] $$0) {
        this.terms = $$0;
        this.composedPredicate = LootItemConditions.orConditions($$0);
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ALTERNATIVE;
    }

    public final boolean test(LootContext $$0) {
        return this.composedPredicate.test((Object)$$0);
    }

    @Override
    public void validate(ValidationContext $$0) {
        LootItemCondition.super.validate($$0);
        for (int $$1 = 0; $$1 < this.terms.length; ++$$1) {
            this.terms[$$1].validate($$0.forChild(".term[" + $$1 + "]"));
        }
    }

    public static Builder alternative(LootItemCondition.Builder ... $$0) {
        return new Builder($$0);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final List<LootItemCondition> terms = Lists.newArrayList();

        public Builder(LootItemCondition.Builder ... $$0) {
            for (LootItemCondition.Builder $$1 : $$0) {
                this.terms.add((Object)$$1.build());
            }
        }

        @Override
        public Builder or(LootItemCondition.Builder $$0) {
            this.terms.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new AlternativeLootItemCondition((LootItemCondition[])this.terms.toArray((Object[])new LootItemCondition[0]));
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<AlternativeLootItemCondition> {
        @Override
        public void serialize(JsonObject $$0, AlternativeLootItemCondition $$1, JsonSerializationContext $$2) {
            $$0.add("terms", $$2.serialize((Object)$$1.terms));
        }

        @Override
        public AlternativeLootItemCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LootItemCondition[] $$2 = GsonHelper.getAsObject($$0, "terms", $$1, LootItemCondition[].class);
            return new AlternativeLootItemCondition($$2);
        }
    }
}