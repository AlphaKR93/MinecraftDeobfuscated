/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.Property;

public class EnumProperty<T extends Enum<T>>
extends Property<T> {
    private final ImmutableSet<T> values;
    private final Map<String, T> names = Maps.newHashMap();

    protected EnumProperty(String $$0, Class<T> $$1, Collection<T> $$2) {
        super($$0, $$1);
        this.values = ImmutableSet.copyOf($$2);
        for (Enum $$3 : $$2) {
            String $$4 = ((StringRepresentable)((Object)$$3)).getSerializedName();
            if (this.names.containsKey((Object)$$4)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + $$4 + "'");
            }
            this.names.put((Object)$$4, (Object)$$3);
        }
    }

    @Override
    public Collection<T> getPossibleValues() {
        return this.values;
    }

    @Override
    public Optional<T> getValue(String $$0) {
        return Optional.ofNullable((Object)((Enum)this.names.get((Object)$$0)));
    }

    @Override
    public String getName(T $$0) {
        return ((StringRepresentable)$$0).getSerializedName();
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof EnumProperty && super.equals($$0)) {
            EnumProperty $$1 = (EnumProperty)$$0;
            return this.values.equals($$1.values) && this.names.equals($$1.names);
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        int $$0 = super.generateHashCode();
        $$0 = 31 * $$0 + this.values.hashCode();
        $$0 = 31 * $$0 + this.names.hashCode();
        return $$0;
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$02, Class<T> $$1) {
        return EnumProperty.create($$02, $$1, $$0 -> true);
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$0, Class<T> $$1, Predicate<T> $$2) {
        return EnumProperty.create($$0, $$1, (Collection)Arrays.stream((Object[])((Enum[])$$1.getEnumConstants())).filter($$2).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$0, Class<T> $$1, T ... $$2) {
        return EnumProperty.create($$0, $$1, Lists.newArrayList((Object[])$$2));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$0, Class<T> $$1, Collection<T> $$2) {
        return new EnumProperty<T>($$0, $$1, $$2);
    }
}