/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class MatchTool
implements LootItemCondition {
    final ItemPredicate predicate;

    public MatchTool(ItemPredicate $$0) {
        this.predicate = $$0;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.MATCH_TOOL;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext $$0) {
        ItemStack $$1 = $$0.getParamOrNull(LootContextParams.TOOL);
        return $$1 != null && this.predicate.matches($$1);
    }

    public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder $$0) {
        return () -> new MatchTool($$0.build());
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<MatchTool> {
        @Override
        public void serialize(JsonObject $$0, MatchTool $$1, JsonSerializationContext $$2) {
            $$0.add("predicate", $$1.predicate.serializeToJson());
        }

        @Override
        public MatchTool deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            ItemPredicate $$2 = ItemPredicate.fromJson($$0.get("predicate"));
            return new MatchTool($$2);
        }
    }
}