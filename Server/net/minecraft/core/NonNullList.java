/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.SafeVarargs
 *  java.util.AbstractList
 *  java.util.Arrays
 *  java.util.List
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E>
extends AbstractList<E> {
    private final List<E> list;
    @Nullable
    private final E defaultValue;

    public static <E> NonNullList<E> create() {
        return new NonNullList<Object>((List<Object>)Lists.newArrayList(), null);
    }

    public static <E> NonNullList<E> createWithCapacity(int $$0) {
        return new NonNullList<Object>((List<Object>)Lists.newArrayListWithCapacity((int)$$0), null);
    }

    public static <E> NonNullList<E> withSize(int $$0, E $$1) {
        Validate.notNull($$1);
        Object[] $$2 = new Object[$$0];
        Arrays.fill((Object[])$$2, $$1);
        return new NonNullList<E>(Arrays.asList((Object[])$$2), $$1);
    }

    @SafeVarargs
    public static <E> NonNullList<E> of(E $$0, E ... $$1) {
        return new NonNullList<E>(Arrays.asList((Object[])$$1), $$0);
    }

    protected NonNullList(List<E> $$0, @Nullable E $$1) {
        this.list = $$0;
        this.defaultValue = $$1;
    }

    @Nonnull
    public E get(int $$0) {
        return (E)this.list.get($$0);
    }

    public E set(int $$0, E $$1) {
        Validate.notNull($$1);
        return (E)this.list.set($$0, $$1);
    }

    public void add(int $$0, E $$1) {
        Validate.notNull($$1);
        this.list.add($$0, $$1);
    }

    public E remove(int $$0) {
        return (E)this.list.remove($$0);
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        if (this.defaultValue == null) {
            super.clear();
        } else {
            for (int $$0 = 0; $$0 < this.size(); ++$$0) {
                this.set($$0, this.defaultValue);
            }
        }
    }
}