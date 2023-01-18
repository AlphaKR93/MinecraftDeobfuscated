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
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

public class DamageSourceCondition
implements LootItemCondition {
    final DamageSourcePredicate predicate;

    DamageSourceCondition(DamageSourcePredicate $$0) {
        this.predicate = $$0;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
    }

    public boolean test(LootContext $$0) {
        DamageSource $$1 = $$0.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        Vec3 $$2 = $$0.getParamOrNull(LootContextParams.ORIGIN);
        return $$2 != null && $$1 != null && this.predicate.matches($$0.getLevel(), $$2, $$1);
    }

    public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder $$0) {
        return () -> new DamageSourceCondition($$0.build());
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<DamageSourceCondition> {
        @Override
        public void serialize(JsonObject $$0, DamageSourceCondition $$1, JsonSerializationContext $$2) {
            $$0.add("predicate", $$1.predicate.serializeToJson());
        }

        @Override
        public DamageSourceCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            DamageSourcePredicate $$2 = DamageSourcePredicate.fromJson($$0.get("predicate"));
            return new DamageSourceCondition($$2);
        }
    }
}