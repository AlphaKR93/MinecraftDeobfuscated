/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrays
 *  java.lang.Class
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.System
 *  java.util.AbstractSet
 *  java.util.Arrays
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.NoSuchElementException
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class SortedArraySet<T>
extends AbstractSet<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private final Comparator<T> comparator;
    T[] contents;
    int size;

    private SortedArraySet(int $$0, Comparator<T> $$1) {
        this.comparator = $$1;
        if ($$0 < 0) {
            throw new IllegalArgumentException("Initial capacity (" + $$0 + ") is negative");
        }
        this.contents = SortedArraySet.castRawArray(new Object[$$0]);
    }

    public static <T extends Comparable<T>> SortedArraySet<T> create() {
        return SortedArraySet.create(10);
    }

    public static <T extends Comparable<T>> SortedArraySet<T> create(int $$0) {
        return new SortedArraySet<T>($$0, Comparator.naturalOrder());
    }

    public static <T> SortedArraySet<T> create(Comparator<T> $$0) {
        return SortedArraySet.create($$0, 10);
    }

    public static <T> SortedArraySet<T> create(Comparator<T> $$0, int $$1) {
        return new SortedArraySet<T>($$1, $$0);
    }

    private static <T> T[] castRawArray(Object[] $$0) {
        return $$0;
    }

    private int findIndex(T $$0) {
        return Arrays.binarySearch((Object[])this.contents, (int)0, (int)this.size, $$0, this.comparator);
    }

    private static int getInsertionPosition(int $$0) {
        return -$$0 - 1;
    }

    public boolean add(T $$0) {
        int $$1 = this.findIndex($$0);
        if ($$1 >= 0) {
            return false;
        }
        int $$2 = SortedArraySet.getInsertionPosition($$1);
        this.addInternal($$0, $$2);
        return true;
    }

    private void grow(int $$0) {
        if ($$0 <= this.contents.length) {
            return;
        }
        if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            $$0 = (int)Math.max((long)Math.min((long)((long)this.contents.length + (long)(this.contents.length >> 1)), (long)0x7FFFFFF7L), (long)$$0);
        } else if ($$0 < 10) {
            $$0 = 10;
        }
        Object[] $$1 = new Object[$$0];
        System.arraycopy(this.contents, (int)0, (Object)$$1, (int)0, (int)this.size);
        this.contents = SortedArraySet.castRawArray($$1);
    }

    private void addInternal(T $$0, int $$1) {
        this.grow(this.size + 1);
        if ($$1 != this.size) {
            System.arraycopy(this.contents, (int)$$1, this.contents, (int)($$1 + 1), (int)(this.size - $$1));
        }
        this.contents[$$1] = $$0;
        ++this.size;
    }

    void removeInternal(int $$0) {
        --this.size;
        if ($$0 != this.size) {
            System.arraycopy(this.contents, (int)($$0 + 1), this.contents, (int)$$0, (int)(this.size - $$0));
        }
        this.contents[this.size] = null;
    }

    private T getInternal(int $$0) {
        return this.contents[$$0];
    }

    public T addOrGet(T $$0) {
        int $$1 = this.findIndex($$0);
        if ($$1 >= 0) {
            return this.getInternal($$1);
        }
        this.addInternal($$0, SortedArraySet.getInsertionPosition($$1));
        return $$0;
    }

    public boolean remove(Object $$0) {
        int $$1 = this.findIndex($$0);
        if ($$1 >= 0) {
            this.removeInternal($$1);
            return true;
        }
        return false;
    }

    @Nullable
    public T get(T $$0) {
        int $$1 = this.findIndex($$0);
        if ($$1 >= 0) {
            return this.getInternal($$1);
        }
        return null;
    }

    public T first() {
        return this.getInternal(0);
    }

    public T last() {
        return this.getInternal(this.size - 1);
    }

    public boolean contains(Object $$0) {
        int $$1 = this.findIndex($$0);
        return $$1 >= 0;
    }

    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    public int size() {
        return this.size;
    }

    public Object[] toArray() {
        return Arrays.copyOf((Object[])this.contents, (int)this.size, Object[].class);
    }

    public <U> U[] toArray(U[] $$0) {
        if ($$0.length < this.size) {
            return Arrays.copyOf((Object[])this.contents, (int)this.size, (Class)$$0.getClass());
        }
        System.arraycopy(this.contents, (int)0, $$0, (int)0, (int)this.size);
        if ($$0.length > this.size) {
            $$0[this.size] = null;
        }
        return $$0;
    }

    public void clear() {
        Arrays.fill((Object[])this.contents, (int)0, (int)this.size, null);
        this.size = 0;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof SortedArraySet) {
            SortedArraySet $$1 = (SortedArraySet)((Object)$$0);
            if (this.comparator.equals($$1.comparator)) {
                return this.size == $$1.size && Arrays.equals((Object[])this.contents, (Object[])$$1.contents);
            }
        }
        return super.equals($$0);
    }

    class ArrayIterator
    implements Iterator<T> {
        private int index;
        private int last = -1;

        ArrayIterator() {
        }

        public boolean hasNext() {
            return this.index < SortedArraySet.this.size;
        }

        public T next() {
            if (this.index >= SortedArraySet.this.size) {
                throw new NoSuchElementException();
            }
            this.last = this.index++;
            return SortedArraySet.this.contents[this.last];
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            SortedArraySet.this.removeInternal(this.last);
            --this.index;
            this.last = -1;
        }
    }
}