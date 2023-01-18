/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.IdMap;

public class IdMapper<T>
implements IdMap<T> {
    private int nextId;
    private final Object2IntMap<T> tToId;
    private final List<T> idToT;

    public IdMapper() {
        this(512);
    }

    public IdMapper(int $$0) {
        this.idToT = Lists.newArrayListWithExpectedSize((int)$$0);
        this.tToId = new Object2IntOpenCustomHashMap($$0, Util.identityStrategy());
        this.tToId.defaultReturnValue(-1);
    }

    public void addMapping(T $$0, int $$1) {
        this.tToId.put($$0, $$1);
        while (this.idToT.size() <= $$1) {
            this.idToT.add(null);
        }
        this.idToT.set($$1, $$0);
        if (this.nextId <= $$1) {
            this.nextId = $$1 + 1;
        }
    }

    public void add(T $$0) {
        this.addMapping($$0, this.nextId);
    }

    @Override
    public int getId(T $$0) {
        return this.tToId.getInt($$0);
    }

    @Override
    @Nullable
    public final T byId(int $$0) {
        if ($$0 >= 0 && $$0 < this.idToT.size()) {
            return (T)this.idToT.get($$0);
        }
        return null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter((Iterator)this.idToT.iterator(), Objects::nonNull);
    }

    public boolean contains(int $$0) {
        return this.byId($$0) != null;
    }

    @Override
    public int size() {
        return this.tToId.size();
    }
}