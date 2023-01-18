/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.SafeVarargs
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Condition
extends Supplier<JsonElement> {
    public void validate(StateDefinition<?, ?> var1);

    public static TerminalCondition condition() {
        return new TerminalCondition();
    }

    public static Condition and(Condition ... $$0) {
        return new CompositeCondition(Operation.AND, (List<Condition>)Arrays.asList((Object[])$$0));
    }

    public static Condition or(Condition ... $$0) {
        return new CompositeCondition(Operation.OR, (List<Condition>)Arrays.asList((Object[])$$0));
    }

    public static class TerminalCondition
    implements Condition {
        private final Map<Property<?>, String> terms = Maps.newHashMap();

        private static <T extends Comparable<T>> String joinValues(Property<T> $$0, Stream<T> $$1) {
            return (String)$$1.map($$0::getName).collect(Collectors.joining((CharSequence)"|"));
        }

        private static <T extends Comparable<T>> String getTerm(Property<T> $$0, T $$1, T[] $$2) {
            return TerminalCondition.joinValues($$0, Stream.concat((Stream)Stream.of($$1), (Stream)Stream.of((Object[])$$2)));
        }

        private <T extends Comparable<T>> void putValue(Property<T> $$0, String $$1) {
            String $$2 = (String)this.terms.put($$0, (Object)$$1);
            if ($$2 != null) {
                throw new IllegalStateException("Tried to replace " + $$0 + " value from " + $$2 + " to " + $$1);
            }
        }

        public final <T extends Comparable<T>> TerminalCondition term(Property<T> $$0, T $$1) {
            this.putValue($$0, $$0.getName($$1));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> TerminalCondition term(Property<T> $$0, T $$1, T ... $$2) {
            this.putValue($$0, TerminalCondition.getTerm($$0, $$1, $$2));
            return this;
        }

        public final <T extends Comparable<T>> TerminalCondition negatedTerm(Property<T> $$0, T $$1) {
            this.putValue($$0, "!" + $$0.getName($$1));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> TerminalCondition negatedTerm(Property<T> $$0, T $$1, T ... $$2) {
            this.putValue($$0, "!" + TerminalCondition.getTerm($$0, $$1, $$2));
            return this;
        }

        public JsonElement get() {
            JsonObject $$0 = new JsonObject();
            this.terms.forEach(($$1, $$2) -> $$0.addProperty($$1.getName(), $$2));
            return $$0;
        }

        @Override
        public void validate(StateDefinition<?, ?> $$0) {
            List $$12 = (List)this.terms.keySet().stream().filter($$1 -> $$0.getProperty($$1.getName()) != $$1).collect(Collectors.toList());
            if (!$$12.isEmpty()) {
                throw new IllegalStateException("Properties " + $$12 + " are missing from " + $$0);
            }
        }
    }

    public static class CompositeCondition
    implements Condition {
        private final Operation operation;
        private final List<Condition> subconditions;

        CompositeCondition(Operation $$0, List<Condition> $$1) {
            this.operation = $$0;
            this.subconditions = $$1;
        }

        @Override
        public void validate(StateDefinition<?, ?> $$0) {
            this.subconditions.forEach($$1 -> $$1.validate($$0));
        }

        public JsonElement get() {
            JsonArray $$0 = new JsonArray();
            this.subconditions.stream().map(Supplier::get).forEach(arg_0 -> ((JsonArray)$$0).add(arg_0));
            JsonObject $$1 = new JsonObject();
            $$1.add(this.operation.id, (JsonElement)$$0);
            return $$1;
        }
    }

    public static enum Operation {
        AND("AND"),
        OR("OR");

        final String id;

        private Operation(String $$0) {
            this.id = $$0;
        }
    }
}