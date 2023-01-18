/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongList
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.function.LongPredicate
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.LongPredicate;
import net.minecraft.util.Mth;

public abstract class DynamicGraphMinFixedPoint {
    private static final int NO_COMPUTED_LEVEL = 255;
    private final int levelCount;
    private final LongLinkedOpenHashSet[] queues;
    private final Long2ByteMap computedLevels;
    private int firstQueuedLevel;
    private volatile boolean hasWork;

    protected DynamicGraphMinFixedPoint(int $$0, final int $$1, final int $$2) {
        if ($$0 >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = $$0;
        this.queues = new LongLinkedOpenHashSet[$$0];
        for (int $$3 = 0; $$3 < $$0; ++$$3) {
            this.queues[$$3] = new LongLinkedOpenHashSet($$1, 0.5f){

                protected void rehash(int $$0) {
                    if ($$0 > $$1) {
                        super.rehash($$0);
                    }
                }
            };
        }
        this.computedLevels = new Long2ByteOpenHashMap($$2, 0.5f){

            protected void rehash(int $$0) {
                if ($$0 > $$2) {
                    super.rehash($$0);
                }
            }
        };
        this.computedLevels.defaultReturnValue((byte)-1);
        this.firstQueuedLevel = $$0;
    }

    private int getKey(int $$0, int $$1) {
        int $$2 = $$0;
        if ($$2 > $$1) {
            $$2 = $$1;
        }
        if ($$2 > this.levelCount - 1) {
            $$2 = this.levelCount - 1;
        }
        return $$2;
    }

    private void checkFirstQueuedLevel(int $$0) {
        int $$1 = this.firstQueuedLevel;
        this.firstQueuedLevel = $$0;
        for (int $$2 = $$1 + 1; $$2 < $$0; ++$$2) {
            if (this.queues[$$2].isEmpty()) continue;
            this.firstQueuedLevel = $$2;
            break;
        }
    }

    protected void removeFromQueue(long $$0) {
        int $$1 = this.computedLevels.get($$0) & 0xFF;
        if ($$1 == 255) {
            return;
        }
        int $$2 = this.getLevel($$0);
        int $$3 = this.getKey($$2, $$1);
        this.dequeue($$0, $$3, this.levelCount, true);
        this.hasWork = this.firstQueuedLevel < this.levelCount;
    }

    public void removeIf(LongPredicate $$0) {
        LongArrayList $$1 = new LongArrayList();
        this.computedLevels.keySet().forEach(arg_0 -> DynamicGraphMinFixedPoint.lambda$removeIf$0($$0, (LongList)$$1, arg_0));
        $$1.forEach(this::removeFromQueue);
    }

    private void dequeue(long $$0, int $$1, int $$2, boolean $$3) {
        if ($$3) {
            this.computedLevels.remove($$0);
        }
        this.queues[$$1].remove($$0);
        if (this.queues[$$1].isEmpty() && this.firstQueuedLevel == $$1) {
            this.checkFirstQueuedLevel($$2);
        }
    }

    private void enqueue(long $$0, int $$1, int $$2) {
        this.computedLevels.put($$0, (byte)$$1);
        this.queues[$$2].add($$0);
        if (this.firstQueuedLevel > $$2) {
            this.firstQueuedLevel = $$2;
        }
    }

    protected void checkNode(long $$0) {
        this.checkEdge($$0, $$0, this.levelCount - 1, false);
    }

    protected void checkEdge(long $$0, long $$1, int $$2, boolean $$3) {
        this.checkEdge($$0, $$1, $$2, this.getLevel($$1), this.computedLevels.get($$1) & 0xFF, $$3);
        this.hasWork = this.firstQueuedLevel < this.levelCount;
    }

    private void checkEdge(long $$0, long $$1, int $$2, int $$3, int $$4, boolean $$5) {
        int $$9;
        boolean $$7;
        if (this.isSource($$1)) {
            return;
        }
        $$2 = Mth.clamp($$2, 0, this.levelCount - 1);
        $$3 = Mth.clamp($$3, 0, this.levelCount - 1);
        if ($$4 == 255) {
            boolean $$6 = true;
            $$4 = $$3;
        } else {
            $$7 = false;
        }
        if ($$5) {
            int $$8 = Math.min((int)$$4, (int)$$2);
        } else {
            $$9 = Mth.clamp(this.getComputedLevel($$1, $$0, $$2), 0, this.levelCount - 1);
        }
        int $$10 = this.getKey($$3, $$4);
        if ($$3 != $$9) {
            int $$11 = this.getKey($$3, $$9);
            if ($$10 != $$11 && !$$7) {
                this.dequeue($$1, $$10, $$11, false);
            }
            this.enqueue($$1, $$9, $$11);
        } else if (!$$7) {
            this.dequeue($$1, $$10, this.levelCount, true);
        }
    }

    protected final void checkNeighbor(long $$0, long $$1, int $$2, boolean $$3) {
        int $$4 = this.computedLevels.get($$1) & 0xFF;
        int $$5 = Mth.clamp(this.computeLevelFromNeighbor($$0, $$1, $$2), 0, this.levelCount - 1);
        if ($$3) {
            this.checkEdge($$0, $$1, $$5, this.getLevel($$1), $$4, true);
        } else {
            boolean $$9;
            int $$8;
            if ($$4 == 255) {
                boolean $$6 = true;
                int $$7 = Mth.clamp(this.getLevel($$1), 0, this.levelCount - 1);
            } else {
                $$8 = $$4;
                $$9 = false;
            }
            if ($$5 == $$8) {
                this.checkEdge($$0, $$1, this.levelCount - 1, $$9 ? $$8 : this.getLevel($$1), $$4, false);
            }
        }
    }

    protected final boolean hasWork() {
        return this.hasWork;
    }

    protected final int runUpdates(int $$0) {
        if (this.firstQueuedLevel >= this.levelCount) {
            return $$0;
        }
        while (this.firstQueuedLevel < this.levelCount && $$0 > 0) {
            int $$4;
            --$$0;
            LongLinkedOpenHashSet $$1 = this.queues[this.firstQueuedLevel];
            long $$2 = $$1.removeFirstLong();
            int $$3 = Mth.clamp(this.getLevel($$2), 0, this.levelCount - 1);
            if ($$1.isEmpty()) {
                this.checkFirstQueuedLevel(this.levelCount);
            }
            if (($$4 = this.computedLevels.remove($$2) & 0xFF) < $$3) {
                this.setLevel($$2, $$4);
                this.checkNeighborsAfterUpdate($$2, $$4, true);
                continue;
            }
            if ($$4 <= $$3) continue;
            this.enqueue($$2, $$4, this.getKey(this.levelCount - 1, $$4));
            this.setLevel($$2, this.levelCount - 1);
            this.checkNeighborsAfterUpdate($$2, $$3, false);
        }
        this.hasWork = this.firstQueuedLevel < this.levelCount;
        return $$0;
    }

    public int getQueueSize() {
        return this.computedLevels.size();
    }

    protected abstract boolean isSource(long var1);

    protected abstract int getComputedLevel(long var1, long var3, int var5);

    protected abstract void checkNeighborsAfterUpdate(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int computeLevelFromNeighbor(long var1, long var3, int var5);

    private static /* synthetic */ void lambda$removeIf$0(LongPredicate $$0, LongList $$1, long $$2) {
        if ($$0.test($$2)) {
            $$1.add($$2);
        }
    }
}