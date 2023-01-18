/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.LinkedHashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition
implements LootItemCondition {
    final Map<String, IntRange> scores;
    final LootContext.EntityTarget entityTarget;

    EntityHasScoreCondition(Map<String, IntRange> $$0, LootContext.EntityTarget $$1) {
        this.scores = ImmutableMap.copyOf($$0);
        this.entityTarget = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ENTITY_SCORES;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)Stream.concat((Stream)Stream.of(this.entityTarget.getParam()), (Stream)this.scores.values().stream().flatMap($$0 -> $$0.getReferencedContextParams().stream())).collect(ImmutableSet.toImmutableSet());
    }

    public boolean test(LootContext $$0) {
        Entity $$1 = $$0.getParamOrNull(this.entityTarget.getParam());
        if ($$1 == null) {
            return false;
        }
        Scoreboard $$2 = $$1.level.getScoreboard();
        for (Map.Entry $$3 : this.scores.entrySet()) {
            if (this.hasScore($$0, $$1, $$2, (String)$$3.getKey(), (IntRange)$$3.getValue())) continue;
            return false;
        }
        return true;
    }

    protected boolean hasScore(LootContext $$0, Entity $$1, Scoreboard $$2, String $$3, IntRange $$4) {
        Objective $$5 = $$2.getObjective($$3);
        if ($$5 == null) {
            return false;
        }
        String $$6 = $$1.getScoreboardName();
        if (!$$2.hasPlayerScore($$6, $$5)) {
            return false;
        }
        return $$4.test($$0, $$2.getOrCreatePlayerScore($$6, $$5).getScore());
    }

    public static Builder hasScores(LootContext.EntityTarget $$0) {
        return new Builder($$0);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final Map<String, IntRange> scores = Maps.newHashMap();
        private final LootContext.EntityTarget entityTarget;

        public Builder(LootContext.EntityTarget $$0) {
            this.entityTarget = $$0;
        }

        public Builder withScore(String $$0, IntRange $$1) {
            this.scores.put((Object)$$0, (Object)$$1);
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new EntityHasScoreCondition(this.scores, this.entityTarget);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<EntityHasScoreCondition> {
        @Override
        public void serialize(JsonObject $$0, EntityHasScoreCondition $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            for (Map.Entry $$4 : $$1.scores.entrySet()) {
                $$3.add((String)$$4.getKey(), $$2.serialize($$4.getValue()));
            }
            $$0.add("scores", (JsonElement)$$3);
            $$0.add("entity", $$2.serialize((Object)$$1.entityTarget));
        }

        @Override
        public EntityHasScoreCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            Set $$2 = GsonHelper.getAsJsonObject($$0, "scores").entrySet();
            LinkedHashMap $$3 = Maps.newLinkedHashMap();
            for (Map.Entry $$4 : $$2) {
                $$3.put((Object)((String)$$4.getKey()), (Object)GsonHelper.convertToObject((JsonElement)$$4.getValue(), "score", $$1, IntRange.class));
            }
            return new EntityHasScoreCondition((Map<String, IntRange>)$$3, GsonHelper.getAsObject($$0, "entity", $$1, LootContext.EntityTarget.class));
        }
    }
}