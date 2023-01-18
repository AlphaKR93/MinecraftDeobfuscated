/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class TimeCheck
implements LootItemCondition {
    @Nullable
    final Long period;
    final IntRange value;

    TimeCheck(@Nullable Long $$0, IntRange $$1) {
        this.period = $$0;
        this.value = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.TIME_CHECK;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.value.getReferencedContextParams();
    }

    public boolean test(LootContext $$0) {
        ServerLevel $$1 = $$0.getLevel();
        long $$2 = $$1.getDayTime();
        if (this.period != null) {
            $$2 %= this.period.longValue();
        }
        return this.value.test($$0, (int)$$2);
    }

    public static Builder time(IntRange $$0) {
        return new Builder($$0);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        @Nullable
        private Long period;
        private final IntRange value;

        public Builder(IntRange $$0) {
            this.value = $$0;
        }

        public Builder setPeriod(long $$0) {
            this.period = $$0;
            return this;
        }

        @Override
        public TimeCheck build() {
            return new TimeCheck(this.period, this.value);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<TimeCheck> {
        @Override
        public void serialize(JsonObject $$0, TimeCheck $$1, JsonSerializationContext $$2) {
            $$0.addProperty("period", (Number)$$1.period);
            $$0.add("value", $$2.serialize((Object)$$1.value));
        }

        @Override
        public TimeCheck deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            Long $$2 = $$0.has("period") ? Long.valueOf((long)GsonHelper.getAsLong($$0, "period")) : null;
            IntRange $$3 = GsonHelper.getAsObject($$0, "value", $$1, IntRange.class);
            return new TimeCheck($$2, $$3);
        }
    }
}