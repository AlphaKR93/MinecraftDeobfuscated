/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootItemRandomChanceCondition
implements LootItemCondition {
    final float probability;

    LootItemRandomChanceCondition(float $$0) {
        this.probability = $$0;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE;
    }

    public boolean test(LootContext $$0) {
        return $$0.getRandom().nextFloat() < this.probability;
    }

    public static LootItemCondition.Builder randomChance(float $$0) {
        return () -> new LootItemRandomChanceCondition($$0);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceCondition> {
        @Override
        public void serialize(JsonObject $$0, LootItemRandomChanceCondition $$1, JsonSerializationContext $$2) {
            $$0.addProperty("chance", (Number)Float.valueOf((float)$$1.probability));
        }

        @Override
        public LootItemRandomChanceCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            return new LootItemRandomChanceCondition(GsonHelper.getAsFloat($$0, "chance"));
        }
    }
}