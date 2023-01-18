/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  java.lang.FunctionalInterface
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.reflect.Type
 *  java.util.Objects
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class IntRange {
    @Nullable
    final NumberProvider min;
    @Nullable
    final NumberProvider max;
    private final IntLimiter limiter;
    private final IntChecker predicate;

    public Set<LootContextParam<?>> getReferencedContextParams() {
        ImmutableSet.Builder $$0 = ImmutableSet.builder();
        if (this.min != null) {
            $$0.addAll((Iterable)this.min.getReferencedContextParams());
        }
        if (this.max != null) {
            $$0.addAll((Iterable)this.max.getReferencedContextParams());
        }
        return $$0.build();
    }

    IntRange(@Nullable NumberProvider $$02, @Nullable NumberProvider $$12) {
        this.min = $$02;
        this.max = $$12;
        if ($$02 == null) {
            if ($$12 == null) {
                this.limiter = ($$0, $$1) -> $$1;
                this.predicate = ($$0, $$1) -> true;
            } else {
                this.limiter = ($$1, $$2) -> Math.min((int)$$12.getInt($$1), (int)$$2);
                this.predicate = ($$1, $$2) -> $$2 <= $$12.getInt($$1);
            }
        } else if ($$12 == null) {
            this.limiter = ($$1, $$2) -> Math.max((int)$$02.getInt($$1), (int)$$2);
            this.predicate = ($$1, $$2) -> $$2 >= $$02.getInt($$1);
        } else {
            this.limiter = ($$2, $$3) -> Mth.clamp($$3, $$02.getInt($$2), $$12.getInt($$2));
            this.predicate = ($$2, $$3) -> $$3 >= $$02.getInt($$2) && $$3 <= $$12.getInt($$2);
        }
    }

    public static IntRange exact(int $$0) {
        ConstantValue $$1 = ConstantValue.exactly($$0);
        return new IntRange($$1, $$1);
    }

    public static IntRange range(int $$0, int $$1) {
        return new IntRange(ConstantValue.exactly($$0), ConstantValue.exactly($$1));
    }

    public static IntRange lowerBound(int $$0) {
        return new IntRange(ConstantValue.exactly($$0), null);
    }

    public static IntRange upperBound(int $$0) {
        return new IntRange(null, ConstantValue.exactly($$0));
    }

    public int clamp(LootContext $$0, int $$1) {
        return this.limiter.apply($$0, $$1);
    }

    public boolean test(LootContext $$0, int $$1) {
        return this.predicate.test($$0, $$1);
    }

    @FunctionalInterface
    static interface IntLimiter {
        public int apply(LootContext var1, int var2);
    }

    @FunctionalInterface
    static interface IntChecker {
        public boolean test(LootContext var1, int var2);
    }

    public static class Serializer
    implements JsonDeserializer<IntRange>,
    JsonSerializer<IntRange> {
        public IntRange deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) {
            if ($$0.isJsonPrimitive()) {
                return IntRange.exact($$0.getAsInt());
            }
            JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "value");
            NumberProvider $$4 = $$3.has("min") ? GsonHelper.getAsObject($$3, "min", $$2, NumberProvider.class) : null;
            NumberProvider $$5 = $$3.has("max") ? GsonHelper.getAsObject($$3, "max", $$2, NumberProvider.class) : null;
            return new IntRange($$4, $$5);
        }

        public JsonElement serialize(IntRange $$0, Type $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            if (Objects.equals((Object)$$0.max, (Object)$$0.min)) {
                return $$2.serialize((Object)$$0.min);
            }
            if ($$0.max != null) {
                $$3.add("max", $$2.serialize((Object)$$0.max));
            }
            if ($$0.min != null) {
                $$3.add("min", $$2.serialize((Object)$$0.min));
            }
            return $$3;
        }
    }
}