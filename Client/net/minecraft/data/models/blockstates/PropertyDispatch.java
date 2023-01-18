/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Comparable
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  java.util.Map
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.models.blockstates.Selector;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch {
    private final Map<Selector, List<Variant>> values = Maps.newHashMap();

    protected void putValue(Selector $$0, List<Variant> $$1) {
        List $$2 = (List)this.values.put((Object)$$0, $$1);
        if ($$2 != null) {
            throw new IllegalStateException("Value " + $$0 + " is already defined");
        }
    }

    Map<Selector, List<Variant>> getEntries() {
        this.verifyComplete();
        return ImmutableMap.copyOf(this.values);
    }

    private void verifyComplete() {
        List<Property<?>> $$02 = this.getDefinedProperties();
        Stream $$12 = Stream.of((Object)Selector.empty());
        for (Property $$2 : $$02) {
            $$12 = $$12.flatMap($$1 -> $$2.getAllValues().map($$1::extend));
        }
        List $$3 = (List)$$12.filter($$0 -> !this.values.containsKey($$0)).collect(Collectors.toList());
        if (!$$3.isEmpty()) {
            throw new IllegalStateException("Missing definition for properties: " + $$3);
        }
    }

    abstract List<Property<?>> getDefinedProperties();

    public static <T1 extends Comparable<T1>> C1<T1> property(Property<T1> $$0) {
        return new C1<T1>($$0);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<T1, T2> properties(Property<T1> $$0, Property<T2> $$1) {
        return new C2<T1, T2>($$0, $$1);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<T1, T2, T3> properties(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2) {
        return new C3<T1, T2, T3>($$0, $$1, $$2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<T1, T2, T3, T4> properties(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3) {
        return new C4<T1, T2, T3, T4>($$0, $$1, $$2, $$3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<T1, T2, T3, T4, T5> properties(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3, Property<T5> $$4) {
        return new C5<T1, T2, T3, T4, T5>($$0, $$1, $$2, $$3, $$4);
    }

    public static class C1<T1 extends Comparable<T1>>
    extends PropertyDispatch {
        private final Property<T1> property1;

        C1(Property<T1> $$0) {
            this.property1 = $$0;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1);
        }

        public C1<T1> select(T1 $$0, List<Variant> $$1) {
            Selector $$2 = Selector.of(this.property1.value($$0));
            this.putValue($$2, $$1);
            return this;
        }

        public C1<T1> select(T1 $$0, Variant $$1) {
            return this.select($$0, (List<Variant>)Collections.singletonList((Object)$$1));
        }

        public PropertyDispatch generate(Function<T1, Variant> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.select($$1, (Variant)$$0.apply($$1)));
            return this;
        }

        public PropertyDispatch generateList(Function<T1, List<Variant>> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.select($$1, (List<Variant>)((List)$$0.apply($$1))));
            return this;
        }
    }

    public static class C2<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;

        C2(Property<T1> $$0, Property<T2> $$1) {
            this.property1 = $$0;
            this.property2 = $$1;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2);
        }

        public C2<T1, T2> select(T1 $$0, T2 $$1, List<Variant> $$2) {
            Selector $$3 = Selector.of(this.property1.value($$0), this.property2.value($$1));
            this.putValue($$3, $$2);
            return this;
        }

        public C2<T1, T2> select(T1 $$0, T2 $$1, Variant $$2) {
            return this.select($$0, $$1, (List<Variant>)Collections.singletonList((Object)$$2));
        }

        public PropertyDispatch generate(BiFunction<T1, T2, Variant> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.select($$1, $$2, (Variant)$$0.apply($$1, $$2))));
            return this;
        }

        public PropertyDispatch generateList(BiFunction<T1, T2, List<Variant>> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.select($$1, $$2, (List<Variant>)((List)$$0.apply($$1, $$2)))));
            return this;
        }
    }

    public static class C3<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;

        C3(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3);
        }

        public C3<T1, T2, T3> select(T1 $$0, T2 $$1, T3 $$2, List<Variant> $$3) {
            Selector $$4 = Selector.of(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2));
            this.putValue($$4, $$3);
            return this;
        }

        public C3<T1, T2, T3> select(T1 $$0, T2 $$1, T3 $$2, Variant $$3) {
            return this.select($$0, $$1, $$2, (List<Variant>)Collections.singletonList((Object)$$3));
        }

        public PropertyDispatch generate(TriFunction<T1, T2, T3, Variant> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.select($$1, $$2, $$3, (Variant)$$0.apply($$1, $$2, $$3)))));
            return this;
        }

        public PropertyDispatch generateList(TriFunction<T1, T2, T3, List<Variant>> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.select($$1, $$2, $$3, (List<Variant>)((List)$$0.apply($$1, $$2, $$3))))));
            return this;
        }
    }

    public static class C4<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;

        C4(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
            this.property4 = $$3;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3, this.property4);
        }

        public C4<T1, T2, T3, T4> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, List<Variant> $$4) {
            Selector $$5 = Selector.of(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2), this.property4.value($$3));
            this.putValue($$5, $$4);
            return this;
        }

        public C4<T1, T2, T3, T4> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, Variant $$4) {
            return this.select($$0, $$1, $$2, $$3, (List<Variant>)Collections.singletonList((Object)$$4));
        }

        public PropertyDispatch generate(QuadFunction<T1, T2, T3, T4, Variant> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.select($$1, $$2, $$3, $$4, (Variant)$$0.apply($$1, $$2, $$3, $$4))))));
            return this;
        }

        public PropertyDispatch generateList(QuadFunction<T1, T2, T3, T4, List<Variant>> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.select($$1, $$2, $$3, $$4, (List<Variant>)((List)$$0.apply($$1, $$2, $$3, $$4)))))));
            return this;
        }
    }

    public static class C5<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;
        private final Property<T5> property5;

        C5(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3, Property<T5> $$4) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
            this.property4 = $$3;
            this.property5 = $$4;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3, this.property4, this.property5);
        }

        public C5<T1, T2, T3, T4, T5> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, T5 $$4, List<Variant> $$5) {
            Selector $$6 = Selector.of(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2), this.property4.value($$3), this.property5.value($$4));
            this.putValue($$6, $$5);
            return this;
        }

        public C5<T1, T2, T3, T4, T5> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, T5 $$4, Variant $$5) {
            return this.select($$0, $$1, $$2, $$3, $$4, (List<Variant>)Collections.singletonList((Object)$$5));
        }

        public PropertyDispatch generate(PentaFunction<T1, T2, T3, T4, T5, Variant> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.property5.getPossibleValues().forEach($$5 -> this.select($$1, $$2, $$3, $$4, $$5, (Variant)$$0.apply($$1, $$2, $$3, $$4, $$5)))))));
            return this;
        }

        public PropertyDispatch generateList(PentaFunction<T1, T2, T3, T4, T5, List<Variant>> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.property5.getPossibleValues().forEach($$5 -> this.select($$1, $$2, $$3, $$4, $$5, (List<Variant>)((List)$$0.apply($$1, $$2, $$3, $$4, $$5))))))));
            return this;
        }
    }

    @FunctionalInterface
    public static interface PentaFunction<P1, P2, P3, P4, P5, R> {
        public R apply(P1 var1, P2 var2, P3 var3, P4 var4, P5 var5);
    }

    @FunctionalInterface
    public static interface QuadFunction<P1, P2, P3, P4, R> {
        public R apply(P1 var1, P2 var2, P3 var3, P4 var4);
    }

    @FunctionalInterface
    public static interface TriFunction<P1, P2, P3, R> {
        public R apply(P1 var1, P2 var2, P3 var3);
    }
}