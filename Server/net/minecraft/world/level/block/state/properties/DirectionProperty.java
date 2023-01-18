/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DirectionProperty
extends EnumProperty<Direction> {
    protected DirectionProperty(String $$0, Collection<Direction> $$1) {
        super($$0, Direction.class, $$1);
    }

    public static DirectionProperty create(String $$02) {
        return DirectionProperty.create($$02, (Predicate<Direction>)((Predicate)$$0 -> true));
    }

    public static DirectionProperty create(String $$0, Predicate<Direction> $$1) {
        return DirectionProperty.create($$0, (Collection<Direction>)((Collection)Arrays.stream((Object[])Direction.values()).filter($$1).collect(Collectors.toList())));
    }

    public static DirectionProperty create(String $$0, Direction ... $$1) {
        return DirectionProperty.create($$0, (Collection<Direction>)Lists.newArrayList((Object[])$$1));
    }

    public static DirectionProperty create(String $$0, Collection<Direction> $$1) {
        return new DirectionProperty($$0, $$1);
    }
}