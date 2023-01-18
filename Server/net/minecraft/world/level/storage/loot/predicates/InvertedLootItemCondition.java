/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class InvertedLootItemCondition
implements LootItemCondition {
    final LootItemCondition term;

    InvertedLootItemCondition(LootItemCondition $$0) {
        this.term = $$0;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.INVERTED;
    }

    public final boolean test(LootContext $$0) {
        return !this.term.test($$0);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.term.getReferencedContextParams();
    }

    @Override
    public void validate(ValidationContext $$0) {
        LootItemCondition.super.validate($$0);
        this.term.validate($$0);
    }

    public static LootItemCondition.Builder invert(LootItemCondition.Builder $$0) {
        InvertedLootItemCondition $$1 = new InvertedLootItemCondition($$0.build());
        return () -> $$1;
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<InvertedLootItemCondition> {
        @Override
        public void serialize(JsonObject $$0, InvertedLootItemCondition $$1, JsonSerializationContext $$2) {
            $$0.add("term", $$2.serialize((Object)$$1.term));
        }

        @Override
        public InvertedLootItemCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LootItemCondition $$2 = GsonHelper.getAsObject($$0, "term", $$1, LootItemCondition.class);
            return new InvertedLootItemCondition($$2);
        }
    }
}