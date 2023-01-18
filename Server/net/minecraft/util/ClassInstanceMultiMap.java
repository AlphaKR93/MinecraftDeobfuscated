/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.util.AbstractCollection
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.stream.Collectors
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassInstanceMultiMap<T>
extends AbstractCollection<T> {
    private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
    private final Class<T> baseClass;
    private final List<T> allInstances = Lists.newArrayList();

    public ClassInstanceMultiMap(Class<T> $$0) {
        this.baseClass = $$0;
        this.byClass.put($$0, this.allInstances);
    }

    public boolean add(T $$0) {
        boolean $$1 = false;
        for (Map.Entry $$2 : this.byClass.entrySet()) {
            if (!((Class)$$2.getKey()).isInstance($$0)) continue;
            $$1 |= ((List)$$2.getValue()).add($$0);
        }
        return $$1;
    }

    public boolean remove(Object $$0) {
        boolean $$1 = false;
        for (Map.Entry $$2 : this.byClass.entrySet()) {
            if (!((Class)$$2.getKey()).isInstance($$0)) continue;
            List $$3 = (List)$$2.getValue();
            $$1 |= $$3.remove($$0);
        }
        return $$1;
    }

    public boolean contains(Object $$0) {
        return this.find($$0.getClass()).contains($$0);
    }

    public <S> Collection<S> find(Class<S> $$02) {
        if (!this.baseClass.isAssignableFrom($$02)) {
            throw new IllegalArgumentException("Don't know how to search for " + $$02);
        }
        List $$1 = (List)this.byClass.computeIfAbsent($$02, $$0 -> (List)this.allInstances.stream().filter(arg_0 -> ((Class)$$0).isInstance(arg_0)).collect(Collectors.toList()));
        return Collections.unmodifiableCollection((Collection)$$1);
    }

    public Iterator<T> iterator() {
        if (this.allInstances.isEmpty()) {
            return Collections.emptyIterator();
        }
        return Iterators.unmodifiableIterator((Iterator)this.allInstances.iterator());
    }

    public List<T> getAllInstances() {
        return ImmutableList.copyOf(this.allInstances);
    }

    public int size() {
        return this.allInstances.size();
    }
}