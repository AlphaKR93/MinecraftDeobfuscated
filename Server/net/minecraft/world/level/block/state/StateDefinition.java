/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.regex.Pattern
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
    static final Pattern NAME_PATTERN = Pattern.compile((String)"^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> propertiesByName;
    private final ImmutableList<S> states;

    protected StateDefinition(Function<O, S> $$0, O $$12, Factory<O, S> $$2, Map<String, Property<?>> $$3) {
        this.owner = $$12;
        this.propertiesByName = ImmutableSortedMap.copyOf($$3);
        Supplier $$4 = () -> (StateHolder)$$0.apply($$12);
        MapCodec<S> $$5 = MapCodec.of((MapEncoder)Encoder.empty(), (MapDecoder)Decoder.unit((Supplier)$$4));
        for (Map.Entry $$6 : this.propertiesByName.entrySet()) {
            $$5 = StateDefinition.appendPropertyCodec($$5, $$4, (String)$$6.getKey(), (Property)$$6.getValue());
        }
        MapCodec<S> $$7 = $$5;
        LinkedHashMap $$8 = Maps.newLinkedHashMap();
        ArrayList $$9 = Lists.newArrayList();
        Stream $$10 = Stream.of((Object)Collections.emptyList());
        for (Property $$11 : this.propertiesByName.values()) {
            $$10 = $$10.flatMap($$1 -> $$11.getPossibleValues().stream().map($$2 -> {
                ArrayList $$3 = Lists.newArrayList((Iterable)$$1);
                $$3.add((Object)Pair.of((Object)$$11, (Object)$$2));
                return $$3;
            }));
        }
        $$10.forEach(arg_0 -> StateDefinition.lambda$new$3($$2, $$12, $$7, (Map)$$8, (List)$$9, arg_0));
        for (StateHolder $$122 : $$9) {
            $$122.populateNeighbours($$8);
        }
        this.states = ImmutableList.copyOf((Collection)$$9);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(MapCodec<S> $$02, Supplier<S> $$12, String $$2, Property<T> $$3) {
        return Codec.mapPair($$02, (MapCodec)$$3.valueCodec().fieldOf($$2).orElseGet($$0 -> {}, () -> $$3.value((StateHolder)$$12.get()))).xmap($$1 -> (StateHolder)((StateHolder)$$1.getFirst()).setValue($$3, ((Property.Value)((Object)((Object)$$1.getSecond()))).value()), $$1 -> Pair.of((Object)$$1, $$3.value((StateHolder<?, ?>)$$1)));
    }

    public ImmutableList<S> getPossibleStates() {
        return this.states;
    }

    public S any() {
        return (S)((StateHolder)this.states.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.propertiesByName.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String $$0) {
        return (Property)this.propertiesByName.get((Object)$$0);
    }

    private static /* synthetic */ void lambda$new$3(Factory $$0, Object $$1, MapCodec $$2, Map $$3, List $$4, List $$5) {
        ImmutableMap $$6 = (ImmutableMap)$$5.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
        StateHolder $$7 = (StateHolder)$$0.create($$1, $$6, $$2);
        $$3.put((Object)$$6, (Object)$$7);
        $$4.add((Object)$$7);
    }

    public static interface Factory<O, S> {
        public S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

    public static class Builder<O, S extends StateHolder<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = Maps.newHashMap();

        public Builder(O $$0) {
            this.owner = $$0;
        }

        public Builder<O, S> add(Property<?> ... $$0) {
            for (Property<?> $$1 : $$0) {
                this.validateProperty($$1);
                this.properties.put((Object)$$1.getName(), $$1);
            }
            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> $$0) {
            String $$1 = $$0.getName();
            if (!NAME_PATTERN.matcher((CharSequence)$$1).matches()) {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + $$1);
            }
            Collection<Comparable> $$2 = $$0.getPossibleValues();
            if ($$2.size() <= 1) {
                throw new IllegalArgumentException(this.owner + " attempted use property " + $$1 + " with <= 1 possible values");
            }
            for (Comparable $$3 : $$2) {
                String $$4 = $$0.getName($$3);
                if (NAME_PATTERN.matcher((CharSequence)$$4).matches()) continue;
                throw new IllegalArgumentException(this.owner + " has property: " + $$1 + " with invalidly named value: " + $$4);
            }
            if (this.properties.containsKey((Object)$$1)) {
                throw new IllegalArgumentException(this.owner + " has duplicate property: " + $$1);
            }
        }

        public StateDefinition<O, S> create(Function<O, S> $$0, Factory<O, S> $$1) {
            return new StateDefinition<O, S>($$0, this.owner, $$1, this.properties);
        }
    }
}