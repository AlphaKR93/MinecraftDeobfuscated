/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Float
 *  java.lang.Number
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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootItemRandomChanceWithLootingCondition
implements LootItemCondition {
    final float percent;
    final float lootingMultiplier;

    LootItemRandomChanceWithLootingCondition(float $$0, float $$1) {
        this.percent = $$0;
        this.lootingMultiplier = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE_WITH_LOOTING;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
    }

    public boolean test(LootContext $$0) {
        Entity $$1 = $$0.getParamOrNull(LootContextParams.KILLER_ENTITY);
        int $$2 = 0;
        if ($$1 instanceof LivingEntity) {
            $$2 = EnchantmentHelper.getMobLooting((LivingEntity)$$1);
        }
        return $$0.getRandom().nextFloat() < this.percent + (float)$$2 * this.lootingMultiplier;
    }

    public static LootItemCondition.Builder randomChanceAndLootingBoost(float $$0, float $$1) {
        return () -> new LootItemRandomChanceWithLootingCondition($$0, $$1);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceWithLootingCondition> {
        @Override
        public void serialize(JsonObject $$0, LootItemRandomChanceWithLootingCondition $$1, JsonSerializationContext $$2) {
            $$0.addProperty("chance", (Number)Float.valueOf((float)$$1.percent));
            $$0.addProperty("looting_multiplier", (Number)Float.valueOf((float)$$1.lootingMultiplier));
        }

        @Override
        public LootItemRandomChanceWithLootingCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat($$0, "chance"), GsonHelper.getAsFloat($$0, "looting_multiplier"));
        }
    }
}