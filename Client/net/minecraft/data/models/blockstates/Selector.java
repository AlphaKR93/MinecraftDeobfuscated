/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.List
 *  java.util.stream.Collectors
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.properties.Property;

public final class Selector {
    private static final Selector EMPTY = new Selector((List<Property.Value<?>>)ImmutableList.of());
    private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing($$0 -> $$0.property().getName());
    private final List<Property.Value<?>> values;

    public Selector extend(Property.Value<?> $$0) {
        return new Selector((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).add($$0).build());
    }

    public Selector extend(Selector $$0) {
        return new Selector((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).addAll($$0.values).build());
    }

    private Selector(List<Property.Value<?>> $$0) {
        this.values = $$0;
    }

    public static Selector empty() {
        return EMPTY;
    }

    public static Selector of(Property.Value<?> ... $$0) {
        return new Selector((List<Property.Value<?>>)ImmutableList.copyOf($$0));
    }

    public boolean equals(Object $$0) {
        return this == $$0 || $$0 instanceof Selector && this.values.equals(((Selector)$$0).values);
    }

    public int hashCode() {
        return this.values.hashCode();
    }

    public String getKey() {
        return (String)this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining((CharSequence)","));
    }

    public String toString() {
        return this.getKey();
    }
}