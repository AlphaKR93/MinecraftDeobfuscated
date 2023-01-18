/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public class StatePropertiesPredicate {
    public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate((List<PropertyMatcher>)ImmutableList.of());
    private final List<PropertyMatcher> properties;

    private static PropertyMatcher fromJson(String $$0, JsonElement $$1) {
        if ($$1.isJsonPrimitive()) {
            String $$2 = $$1.getAsString();
            return new ExactPropertyMatcher($$0, $$2);
        }
        JsonObject $$3 = GsonHelper.convertToJsonObject($$1, "value");
        String $$4 = $$3.has("min") ? StatePropertiesPredicate.getStringOrNull($$3.get("min")) : null;
        String $$5 = $$3.has("max") ? StatePropertiesPredicate.getStringOrNull($$3.get("max")) : null;
        return $$4 != null && $$4.equals((Object)$$5) ? new ExactPropertyMatcher($$0, $$4) : new RangedPropertyMatcher($$0, $$4, $$5);
    }

    @Nullable
    private static String getStringOrNull(JsonElement $$0) {
        if ($$0.isJsonNull()) {
            return null;
        }
        return $$0.getAsString();
    }

    StatePropertiesPredicate(List<PropertyMatcher> $$0) {
        this.properties = ImmutableList.copyOf($$0);
    }

    public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> $$0, S $$1) {
        for (PropertyMatcher $$2 : this.properties) {
            if ($$2.match($$0, $$1)) continue;
            return false;
        }
        return true;
    }

    public boolean matches(BlockState $$0) {
        return this.matches($$0.getBlock().getStateDefinition(), $$0);
    }

    public boolean matches(FluidState $$0) {
        return this.matches($$0.getType().getStateDefinition(), $$0);
    }

    public void checkState(StateDefinition<?, ?> $$0, Consumer<String> $$1) {
        this.properties.forEach($$2 -> $$2.checkState($$0, $$1));
    }

    public static StatePropertiesPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "properties");
        ArrayList $$2 = Lists.newArrayList();
        for (Map.Entry $$3 : $$1.entrySet()) {
            $$2.add((Object)StatePropertiesPredicate.fromJson((String)$$3.getKey(), (JsonElement)$$3.getValue()));
        }
        return new StatePropertiesPredicate((List<PropertyMatcher>)$$2);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (!this.properties.isEmpty()) {
            this.properties.forEach($$1 -> $$0.add($$1.getName(), $$1.toJson()));
        }
        return $$0;
    }

    static class ExactPropertyMatcher
    extends PropertyMatcher {
        private final String value;

        public ExactPropertyMatcher(String $$0, String $$1) {
            super($$0);
            this.value = $$1;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> $$0, Property<T> $$1) {
            T $$2 = $$0.getValue($$1);
            Optional<T> $$3 = $$1.getValue(this.value);
            return $$3.isPresent() && $$2.compareTo((Object)((Comparable)$$3.get())) == 0;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.value);
        }
    }

    static class RangedPropertyMatcher
    extends PropertyMatcher {
        @Nullable
        private final String minValue;
        @Nullable
        private final String maxValue;

        public RangedPropertyMatcher(String $$0, @Nullable String $$1, @Nullable String $$2) {
            super($$0);
            this.minValue = $$1;
            this.maxValue = $$2;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> $$0, Property<T> $$1) {
            Optional<T> $$4;
            Optional<T> $$3;
            T $$2 = $$0.getValue($$1);
            if (!(this.minValue == null || ($$3 = $$1.getValue(this.minValue)).isPresent() && $$2.compareTo((Object)((Comparable)$$3.get())) >= 0)) {
                return false;
            }
            return this.maxValue == null || ($$4 = $$1.getValue(this.maxValue)).isPresent() && $$2.compareTo((Object)((Comparable)$$4.get())) <= 0;
        }

        @Override
        public JsonElement toJson() {
            JsonObject $$0 = new JsonObject();
            if (this.minValue != null) {
                $$0.addProperty("min", this.minValue);
            }
            if (this.maxValue != null) {
                $$0.addProperty("max", this.maxValue);
            }
            return $$0;
        }
    }

    static abstract class PropertyMatcher {
        private final String name;

        public PropertyMatcher(String $$0) {
            this.name = $$0;
        }

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> $$0, S $$1) {
            Property<?> $$2 = $$0.getProperty(this.name);
            if ($$2 == null) {
                return false;
            }
            return this.match($$1, $$2);
        }

        protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);

        public abstract JsonElement toJson();

        public String getName() {
            return this.name;
        }

        public void checkState(StateDefinition<?, ?> $$0, Consumer<String> $$1) {
            Property<?> $$2 = $$0.getProperty(this.name);
            if ($$2 == null) {
                $$1.accept((Object)this.name);
            }
        }
    }

    public static class Builder {
        private final List<PropertyMatcher> matchers = Lists.newArrayList();

        private Builder() {
        }

        public static Builder properties() {
            return new Builder();
        }

        public Builder hasProperty(Property<?> $$0, String $$1) {
            this.matchers.add((Object)new ExactPropertyMatcher($$0.getName(), $$1));
            return this;
        }

        public Builder hasProperty(Property<Integer> $$0, int $$1) {
            return this.hasProperty((Property)$$0, (Comparable<T> & StringRepresentable)Integer.toString((int)$$1));
        }

        public Builder hasProperty(Property<Boolean> $$0, boolean $$1) {
            return this.hasProperty((Property)$$0, (Comparable<T> & StringRepresentable)Boolean.toString((boolean)$$1));
        }

        public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> $$0, T $$1) {
            return this.hasProperty($$0, (T)((StringRepresentable)$$1).getSerializedName());
        }

        public StatePropertiesPredicate build() {
            return new StatePropertiesPredicate(this.matchers);
        }
    }
}