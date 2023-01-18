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
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

public class LootItemEntityPropertyCondition
implements LootItemCondition {
    final EntityPredicate predicate;
    final LootContext.EntityTarget entityTarget;

    LootItemEntityPropertyCondition(EntityPredicate $$0, LootContext.EntityTarget $$1) {
        this.predicate = $$0;
        this.entityTarget = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ENTITY_PROPERTIES;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN, this.entityTarget.getParam());
    }

    public boolean test(LootContext $$0) {
        Entity $$1 = $$0.getParamOrNull(this.entityTarget.getParam());
        Vec3 $$2 = $$0.getParamOrNull(LootContextParams.ORIGIN);
        return this.predicate.matches($$0.getLevel(), $$2, $$1);
    }

    public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget $$0) {
        return LootItemEntityPropertyCondition.hasProperties($$0, EntityPredicate.Builder.entity());
    }

    public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget $$0, EntityPredicate.Builder $$1) {
        return () -> new LootItemEntityPropertyCondition($$1.build(), $$0);
    }

    public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget $$0, EntityPredicate $$1) {
        return () -> new LootItemEntityPropertyCondition($$1, $$0);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemEntityPropertyCondition> {
        @Override
        public void serialize(JsonObject $$0, LootItemEntityPropertyCondition $$1, JsonSerializationContext $$2) {
            $$0.add("predicate", $$1.predicate.serializeToJson());
            $$0.add("entity", $$2.serialize((Object)$$1.entityTarget));
        }

        @Override
        public LootItemEntityPropertyCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            EntityPredicate $$2 = EntityPredicate.fromJson($$0.get("predicate"));
            return new LootItemEntityPropertyCondition($$2, GsonHelper.getAsObject($$0, "entity", $$1, LootContext.EntityTarget.class));
        }
    }
}