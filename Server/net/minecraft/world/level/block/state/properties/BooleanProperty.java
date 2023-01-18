/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Optional
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;

public class BooleanProperty
extends Property<Boolean> {
    private final ImmutableSet<Boolean> values = ImmutableSet.of((Object)true, (Object)false);

    protected BooleanProperty(String $$0) {
        super($$0, Boolean.class);
    }

    @Override
    public Collection<Boolean> getPossibleValues() {
        return this.values;
    }

    public static BooleanProperty create(String $$0) {
        return new BooleanProperty($$0);
    }

    @Override
    public Optional<Boolean> getValue(String $$0) {
        if ("true".equals((Object)$$0) || "false".equals((Object)$$0)) {
            return Optional.of((Object)Boolean.valueOf((String)$$0));
        }
        return Optional.empty();
    }

    @Override
    public String getName(Boolean $$0) {
        return $$0.toString();
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof BooleanProperty && super.equals($$0)) {
            BooleanProperty $$1 = (BooleanProperty)$$0;
            return this.values.equals($$1.values);
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }
}