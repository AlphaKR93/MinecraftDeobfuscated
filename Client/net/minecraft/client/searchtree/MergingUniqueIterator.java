/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.PeekingIterator
 *  java.lang.Object
 *  java.util.Comparator
 *  java.util.Iterator
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class MergingUniqueIterator<T>
extends AbstractIterator<T> {
    private final PeekingIterator<T> firstIterator;
    private final PeekingIterator<T> secondIterator;
    private final Comparator<T> comparator;

    public MergingUniqueIterator(Iterator<T> $$0, Iterator<T> $$1, Comparator<T> $$2) {
        this.firstIterator = Iterators.peekingIterator($$0);
        this.secondIterator = Iterators.peekingIterator($$1);
        this.comparator = $$2;
    }

    protected T computeNext() {
        boolean $$1;
        boolean $$0 = !this.firstIterator.hasNext();
        boolean bl = $$1 = !this.secondIterator.hasNext();
        if ($$0 && $$1) {
            return (T)this.endOfData();
        }
        if ($$0) {
            return (T)this.secondIterator.next();
        }
        if ($$1) {
            return (T)this.firstIterator.next();
        }
        int $$2 = this.comparator.compare(this.firstIterator.peek(), this.secondIterator.peek());
        if ($$2 == 0) {
            this.secondIterator.next();
        }
        return (T)($$2 <= 0 ? this.firstIterator.next() : this.secondIterator.next());
    }
}