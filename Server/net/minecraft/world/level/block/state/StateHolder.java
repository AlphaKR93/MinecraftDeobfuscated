/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayTable
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Table
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class StateHolder<O, S> {
    public static final String NAME_TAG = "Name";
    public static final String PROPERTIES_TAG = "Properties";
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> $$0) {
            if ($$0 == null) {
                return "<NULL>";
            }
            Property $$1 = (Property)$$0.getKey();
            return $$1.getName() + "=" + this.getName($$1, (Comparable)$$0.getValue());
        }

        private <T extends Comparable<T>> String getName(Property<T> $$0, Comparable<?> $$1) {
            return $$0.getName($$1);
        }
    };
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> values;
    private Table<Property<?>, Comparable<?>, S> neighbours;
    protected final MapCodec<S> propertiesCodec;

    protected StateHolder(O $$0, ImmutableMap<Property<?>, Comparable<?>> $$1, MapCodec<S> $$2) {
        this.owner = $$0;
        this.values = $$1;
        this.propertiesCodec = $$2;
    }

    public <T extends Comparable<T>> S cycle(Property<T> $$0) {
        return this.setValue($$0, (Comparable)StateHolder.findNextInCollection($$0.getPossibleValues(), this.getValue($$0)));
    }

    protected static <T> T findNextInCollection(Collection<T> $$0, T $$1) {
        Iterator $$2 = $$0.iterator();
        while ($$2.hasNext()) {
            if (!$$2.next().equals($$1)) continue;
            if ($$2.hasNext()) {
                return (T)$$2.next();
            }
            return (T)$$0.iterator().next();
        }
        return (T)$$2.next();
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append(this.owner);
        if (!this.getValues().isEmpty()) {
            $$0.append('[');
            $$0.append((String)this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining((CharSequence)",")));
            $$0.append(']');
        }
        return $$0.toString();
    }

    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection((Collection)this.values.keySet());
    }

    public <T extends Comparable<T>> boolean hasProperty(Property<T> $$0) {
        return this.values.containsKey($$0);
    }

    public <T extends Comparable<T>> T getValue(Property<T> $$0) {
        Comparable $$1 = (Comparable)this.values.get($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("Cannot get property " + $$0 + " as it does not exist in " + this.owner);
        }
        return (T)((Comparable)$$0.getValueClass().cast((Object)$$1));
    }

    public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> $$0) {
        Comparable $$1 = (Comparable)this.values.get($$0);
        if ($$1 == null) {
            return Optional.empty();
        }
        return Optional.of((Object)((Comparable)$$0.getValueClass().cast((Object)$$1)));
    }

    public <T extends Comparable<T>, V extends T> S setValue(Property<T> $$0, V $$1) {
        Comparable $$2 = (Comparable)this.values.get($$0);
        if ($$2 == null) {
            throw new IllegalArgumentException("Cannot set property " + $$0 + " as it does not exist in " + this.owner);
        }
        if ($$2 == $$1) {
            return (S)this;
        }
        Object $$3 = this.neighbours.get($$0, $$1);
        if ($$3 == null) {
            throw new IllegalArgumentException("Cannot set property " + $$0 + " to " + $$1 + " on " + this.owner + ", it is not an allowed value");
        }
        return (S)$$3;
    }

    public <T extends Comparable<T>, V extends T> S trySetValue(Property<T> $$0, V $$1) {
        Comparable $$2 = (Comparable)this.values.get($$0);
        if ($$2 == null || $$2 == $$1) {
            return (S)this;
        }
        Object $$3 = this.neighbours.get($$0, $$1);
        if ($$3 == null) {
            throw new IllegalArgumentException("Cannot set property " + $$0 + " to " + $$1 + " on " + this.owner + ", it is not an allowed value");
        }
        return (S)$$3;
    }

    public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> $$0) {
        if (this.neighbours != null) {
            throw new IllegalStateException();
        }
        HashBasedTable $$1 = HashBasedTable.create();
        for (Map.Entry $$2 : this.values.entrySet()) {
            Property $$3 = (Property)$$2.getKey();
            for (Comparable $$4 : $$3.getPossibleValues()) {
                if ($$4 == $$2.getValue()) continue;
                $$1.put((Object)$$3, (Object)$$4, $$0.get(this.makeNeighbourValues($$3, $$4)));
            }
        }
        this.neighbours = $$1.isEmpty() ? $$1 : ArrayTable.create((Table)$$1);
    }

    private Map<Property<?>, Comparable<?>> makeNeighbourValues(Property<?> $$0, Comparable<?> $$1) {
        HashMap $$2 = Maps.newHashMap(this.values);
        $$2.put($$0, $$1);
        return $$2;
    }

    public ImmutableMap<Property<?>, Comparable<?>> getValues() {
        return this.values;
    }

    protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> $$02, Function<O, S> $$1) {
        return $$02.dispatch(NAME_TAG, $$0 -> $$0.owner, $$12 -> {
            StateHolder $$2 = (StateHolder)$$1.apply($$12);
            if ($$2.getValues().isEmpty()) {
                return Codec.unit((Object)$$2);
            }
            return $$2.propertiesCodec.codec().optionalFieldOf(PROPERTIES_TAG).xmap($$1 -> (StateHolder)$$1.orElse((Object)$$2), Optional::of).codec();
        });
    }
}