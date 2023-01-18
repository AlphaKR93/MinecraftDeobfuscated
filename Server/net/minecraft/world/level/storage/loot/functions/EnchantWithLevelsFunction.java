/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class EnchantWithLevelsFunction
extends LootItemConditionalFunction {
    final NumberProvider levels;
    final boolean treasure;

    EnchantWithLevelsFunction(LootItemCondition[] $$0, NumberProvider $$1, boolean $$2) {
        super($$0);
        this.levels = $$1;
        this.treasure = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_WITH_LEVELS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.levels.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        RandomSource $$2 = $$1.getRandom();
        return EnchantmentHelper.enchantItem($$2, $$0, this.levels.getInt($$1), this.treasure);
    }

    public static Builder enchantWithLevels(NumberProvider $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final NumberProvider levels;
        private boolean treasure;

        public Builder(NumberProvider $$0) {
            this.levels = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder allowTreasure() {
            this.treasure = true;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
        @Override
        public void serialize(JsonObject $$0, EnchantWithLevelsFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("levels", $$2.serialize((Object)$$1.levels));
            $$0.addProperty("treasure", Boolean.valueOf((boolean)$$1.treasure));
        }

        @Override
        public EnchantWithLevelsFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            NumberProvider $$3 = GsonHelper.getAsObject($$0, "levels", $$1, NumberProvider.class);
            boolean $$4 = GsonHelper.getAsBoolean($$0, "treasure", false);
            return new EnchantWithLevelsFunction($$2, $$3, $$4);
        }
    }
}