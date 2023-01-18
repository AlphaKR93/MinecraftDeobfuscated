/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootingEnchantFunction
extends LootItemConditionalFunction {
    public static final int NO_LIMIT = 0;
    final NumberProvider value;
    final int limit;

    LootingEnchantFunction(LootItemCondition[] $$0, NumberProvider $$1, int $$2) {
        super($$0);
        this.value = $$1;
        this.limit = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.LOOTING_ENCHANT;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union((Set)ImmutableSet.of(LootContextParams.KILLER_ENTITY), (Set)this.value.getReferencedContextParams());
    }

    boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Entity $$2 = $$1.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if ($$2 instanceof LivingEntity) {
            int $$3 = EnchantmentHelper.getMobLooting((LivingEntity)$$2);
            if ($$3 == 0) {
                return $$0;
            }
            float $$4 = (float)$$3 * this.value.getFloat($$1);
            $$0.grow(Math.round((float)$$4));
            if (this.hasLimit() && $$0.getCount() > this.limit) {
                $$0.setCount(this.limit);
            }
        }
        return $$0;
    }

    public static Builder lootingMultiplier(NumberProvider $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final NumberProvider count;
        private int limit = 0;

        public Builder(NumberProvider $$0) {
            this.count = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder setLimit(int $$0) {
            this.limit = $$0;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<LootingEnchantFunction> {
        @Override
        public void serialize(JsonObject $$0, LootingEnchantFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("count", $$2.serialize((Object)$$1.value));
            if ($$1.hasLimit()) {
                $$0.add("limit", $$2.serialize((Object)$$1.limit));
            }
        }

        @Override
        public LootingEnchantFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            int $$3 = GsonHelper.getAsInt($$0, "limit", 0);
            return new LootingEnchantFunction($$2, GsonHelper.getAsObject($$0, "count", $$1, NumberProvider.class), $$3);
        }
    }
}