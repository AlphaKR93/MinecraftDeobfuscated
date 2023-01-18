/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Optional
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;

public class IntegerProperty
extends Property<Integer> {
    private final ImmutableSet<Integer> values;
    private final int min;
    private final int max;

    protected IntegerProperty(String $$0, int $$1, int $$2) {
        super($$0, Integer.class);
        if ($$1 < 0) {
            throw new IllegalArgumentException("Min value of " + $$0 + " must be 0 or greater");
        }
        if ($$2 <= $$1) {
            throw new IllegalArgumentException("Max value of " + $$0 + " must be greater than min (" + $$1 + ")");
        }
        this.min = $$1;
        this.max = $$2;
        HashSet $$3 = Sets.newHashSet();
        for (int $$4 = $$1; $$4 <= $$2; ++$$4) {
            $$3.add((Object)$$4);
        }
        this.values = ImmutableSet.copyOf((Collection)$$3);
    }

    @Override
    public Collection<Integer> getPossibleValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof IntegerProperty && super.equals($$0)) {
            IntegerProperty $$1 = (IntegerProperty)$$0;
            return this.values.equals($$1.values);
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }

    public static IntegerProperty create(String $$0, int $$1, int $$2) {
        return new IntegerProperty($$0, $$1, $$2);
    }

    @Override
    public Optional<Integer> getValue(String $$0) {
        try {
            Integer $$1 = Integer.valueOf((String)$$0);
            return $$1 >= this.min && $$1 <= this.max ? Optional.of((Object)$$1) : Optional.empty();
        }
        catch (NumberFormatException $$2) {
            return Optional.empty();
        }
    }

    @Override
    public String getName(Integer $$0) {
        return $$0.toString();
    }
}