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
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class WeatherCheck
implements LootItemCondition {
    @Nullable
    final Boolean isRaining;
    @Nullable
    final Boolean isThundering;

    WeatherCheck(@Nullable Boolean $$0, @Nullable Boolean $$1) {
        this.isRaining = $$0;
        this.isThundering = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.WEATHER_CHECK;
    }

    public boolean test(LootContext $$0) {
        ServerLevel $$1 = $$0.getLevel();
        if (this.isRaining != null && this.isRaining.booleanValue() != $$1.isRaining()) {
            return false;
        }
        return this.isThundering == null || this.isThundering.booleanValue() == $$1.isThundering();
    }

    public static Builder weather() {
        return new Builder();
    }

    public static class Builder
    implements LootItemCondition.Builder {
        @Nullable
        private Boolean isRaining;
        @Nullable
        private Boolean isThundering;

        public Builder setRaining(@Nullable Boolean $$0) {
            this.isRaining = $$0;
            return this;
        }

        public Builder setThundering(@Nullable Boolean $$0) {
            this.isThundering = $$0;
            return this;
        }

        @Override
        public WeatherCheck build() {
            return new WeatherCheck(this.isRaining, this.isThundering);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<WeatherCheck> {
        @Override
        public void serialize(JsonObject $$0, WeatherCheck $$1, JsonSerializationContext $$2) {
            $$0.addProperty("raining", $$1.isRaining);
            $$0.addProperty("thundering", $$1.isThundering);
        }

        @Override
        public WeatherCheck deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            Boolean $$2 = $$0.has("raining") ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, "raining")) : null;
            Boolean $$3 = $$0.has("thundering") ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, "thundering")) : null;
            return new WeatherCheck($$2, $$3);
        }
    }
}