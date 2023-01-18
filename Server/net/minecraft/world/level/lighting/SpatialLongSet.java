/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.HashCommon
 *  it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.UnsupportedOperationException
 *  java.util.NoSuchElementException
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.NoSuchElementException;
import net.minecraft.util.Mth;

public class SpatialLongSet
extends LongLinkedOpenHashSet {
    private final InternalMap map;

    public SpatialLongSet(int $$0, float $$1) {
        super($$0, $$1);
        this.map = new InternalMap($$0 / 64, $$1);
    }

    public boolean add(long $$0) {
        return this.map.addBit($$0);
    }

    public boolean rem(long $$0) {
        return this.map.removeBit($$0);
    }

    public long removeFirstLong() {
        return this.map.removeFirstBit();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    protected static class InternalMap
    extends Long2LongLinkedOpenHashMap {
        private static final int X_BITS = Mth.log2(60000000);
        private static final int Z_BITS = Mth.log2(60000000);
        private static final int Y_BITS;
        private static final int Y_OFFSET = 0;
        private static final int Z_OFFSET;
        private static final int X_OFFSET;
        private static final long OUTER_MASK;
        private int lastPos = -1;
        private long lastOuterKey;
        private final int minSize;

        public InternalMap(int $$0, float $$1) {
            super($$0, $$1);
            this.minSize = $$0;
        }

        static long getOuterKey(long $$0) {
            return $$0 & (OUTER_MASK ^ 0xFFFFFFFFFFFFFFFFL);
        }

        static int getInnerKey(long $$0) {
            int $$1 = (int)($$0 >>> X_OFFSET & 3L);
            int $$2 = (int)($$0 >>> 0 & 3L);
            int $$3 = (int)($$0 >>> Z_OFFSET & 3L);
            return $$1 << 4 | $$3 << 2 | $$2;
        }

        static long getFullKey(long $$0, int $$1) {
            $$0 |= (long)($$1 >>> 4 & 3) << X_OFFSET;
            $$0 |= (long)($$1 >>> 2 & 3) << Z_OFFSET;
            return $$0 |= (long)($$1 >>> 0 & 3) << 0;
        }

        public boolean addBit(long $$0) {
            int $$6;
            long $$1 = InternalMap.getOuterKey($$0);
            int $$2 = InternalMap.getInnerKey($$0);
            long $$3 = 1L << $$2;
            if ($$1 == 0L) {
                if (this.containsNullKey) {
                    return this.replaceBit(this.n, $$3);
                }
                this.containsNullKey = true;
                int $$4 = this.n;
            } else {
                if (this.lastPos != -1 && $$1 == this.lastOuterKey) {
                    return this.replaceBit(this.lastPos, $$3);
                }
                long[] $$5 = this.key;
                $$6 = (int)HashCommon.mix((long)$$1) & this.mask;
                long $$7 = $$5[$$6];
                while ($$7 != 0L) {
                    if ($$7 == $$1) {
                        this.lastPos = $$6;
                        this.lastOuterKey = $$1;
                        return this.replaceBit($$6, $$3);
                    }
                    $$6 = $$6 + 1 & this.mask;
                    $$7 = $$5[$$6];
                }
            }
            this.key[$$6] = $$1;
            this.value[$$6] = $$3;
            if (this.size == 0) {
                this.first = this.last = $$6;
                this.link[$$6] = -1L;
            } else {
                int n = this.last;
                this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)$$6 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                this.link[$$6] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
                this.last = $$6;
            }
            if (this.size++ >= this.maxFill) {
                this.rehash(HashCommon.arraySize((int)(this.size + 1), (float)this.f));
            }
            return false;
        }

        private boolean replaceBit(int $$0, long $$1) {
            boolean $$2 = (this.value[$$0] & $$1) != 0L;
            int n = $$0;
            this.value[n] = this.value[n] | $$1;
            return $$2;
        }

        public boolean removeBit(long $$0) {
            long $$1 = InternalMap.getOuterKey($$0);
            int $$2 = InternalMap.getInnerKey($$0);
            long $$3 = 1L << $$2;
            if ($$1 == 0L) {
                if (this.containsNullKey) {
                    return this.removeFromNullEntry($$3);
                }
                return false;
            }
            if (this.lastPos != -1 && $$1 == this.lastOuterKey) {
                return this.removeFromEntry(this.lastPos, $$3);
            }
            long[] $$4 = this.key;
            int $$5 = (int)HashCommon.mix((long)$$1) & this.mask;
            long $$6 = $$4[$$5];
            while ($$6 != 0L) {
                if ($$1 == $$6) {
                    this.lastPos = $$5;
                    this.lastOuterKey = $$1;
                    return this.removeFromEntry($$5, $$3);
                }
                $$5 = $$5 + 1 & this.mask;
                $$6 = $$4[$$5];
            }
            return false;
        }

        private boolean removeFromNullEntry(long $$0) {
            if ((this.value[this.n] & $$0) == 0L) {
                return false;
            }
            int n = this.n;
            this.value[n] = this.value[n] & ($$0 ^ 0xFFFFFFFFFFFFFFFFL);
            if (this.value[this.n] != 0L) {
                return true;
            }
            this.containsNullKey = false;
            --this.size;
            this.fixPointers(this.n);
            if (this.size < this.maxFill / 4 && this.n > 16) {
                this.rehash(this.n / 2);
            }
            return true;
        }

        private boolean removeFromEntry(int $$0, long $$1) {
            if ((this.value[$$0] & $$1) == 0L) {
                return false;
            }
            int n = $$0;
            this.value[n] = this.value[n] & ($$1 ^ 0xFFFFFFFFFFFFFFFFL);
            if (this.value[$$0] != 0L) {
                return true;
            }
            this.lastPos = -1;
            --this.size;
            this.fixPointers($$0);
            this.shiftKeys($$0);
            if (this.size < this.maxFill / 4 && this.n > 16) {
                this.rehash(this.n / 2);
            }
            return true;
        }

        public long removeFirstBit() {
            if (this.size == 0) {
                throw new NoSuchElementException();
            }
            int $$0 = this.first;
            long $$1 = this.key[$$0];
            int $$2 = Long.numberOfTrailingZeros((long)this.value[$$0]);
            int n = $$0;
            this.value[n] = this.value[n] & (1L << $$2 ^ 0xFFFFFFFFFFFFFFFFL);
            if (this.value[$$0] == 0L) {
                this.removeFirstLong();
                this.lastPos = -1;
            }
            return InternalMap.getFullKey($$1, $$2);
        }

        protected void rehash(int $$0) {
            if ($$0 > this.minSize) {
                super.rehash($$0);
            }
        }

        static {
            Z_OFFSET = Y_BITS = 64 - X_BITS - Z_BITS;
            X_OFFSET = Y_BITS + Z_BITS;
            OUTER_MASK = 3L << X_OFFSET | 3L | 3L << Z_OFFSET;
        }
    }
}