/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.BitSet
 */
package net.minecraft.world.phys.shapes;

import java.util.BitSet;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IndexMerger;

public final class BitSetDiscreteVoxelShape
extends DiscreteVoxelShape {
    private final BitSet storage;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public BitSetDiscreteVoxelShape(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
        this.storage = new BitSet($$0 * $$1 * $$2);
        this.xMin = $$0;
        this.yMin = $$1;
        this.zMin = $$2;
    }

    public static BitSetDiscreteVoxelShape withFilledBounds(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        BitSetDiscreteVoxelShape $$9 = new BitSetDiscreteVoxelShape($$0, $$1, $$2);
        $$9.xMin = $$3;
        $$9.yMin = $$4;
        $$9.zMin = $$5;
        $$9.xMax = $$6;
        $$9.yMax = $$7;
        $$9.zMax = $$8;
        for (int $$10 = $$3; $$10 < $$6; ++$$10) {
            for (int $$11 = $$4; $$11 < $$7; ++$$11) {
                for (int $$12 = $$5; $$12 < $$8; ++$$12) {
                    $$9.fillUpdateBounds($$10, $$11, $$12, false);
                }
            }
        }
        return $$9;
    }

    public BitSetDiscreteVoxelShape(DiscreteVoxelShape $$0) {
        super($$0.xSize, $$0.ySize, $$0.zSize);
        if ($$0 instanceof BitSetDiscreteVoxelShape) {
            this.storage = (BitSet)((BitSetDiscreteVoxelShape)$$0).storage.clone();
        } else {
            this.storage = new BitSet(this.xSize * this.ySize * this.zSize);
            for (int $$1 = 0; $$1 < this.xSize; ++$$1) {
                for (int $$2 = 0; $$2 < this.ySize; ++$$2) {
                    for (int $$3 = 0; $$3 < this.zSize; ++$$3) {
                        if (!$$0.isFull($$1, $$2, $$3)) continue;
                        this.storage.set(this.getIndex($$1, $$2, $$3));
                    }
                }
            }
        }
        this.xMin = $$0.firstFull(Direction.Axis.X);
        this.yMin = $$0.firstFull(Direction.Axis.Y);
        this.zMin = $$0.firstFull(Direction.Axis.Z);
        this.xMax = $$0.lastFull(Direction.Axis.X);
        this.yMax = $$0.lastFull(Direction.Axis.Y);
        this.zMax = $$0.lastFull(Direction.Axis.Z);
    }

    protected int getIndex(int $$0, int $$1, int $$2) {
        return ($$0 * this.ySize + $$1) * this.zSize + $$2;
    }

    @Override
    public boolean isFull(int $$0, int $$1, int $$2) {
        return this.storage.get(this.getIndex($$0, $$1, $$2));
    }

    private void fillUpdateBounds(int $$0, int $$1, int $$2, boolean $$3) {
        this.storage.set(this.getIndex($$0, $$1, $$2));
        if ($$3) {
            this.xMin = Math.min((int)this.xMin, (int)$$0);
            this.yMin = Math.min((int)this.yMin, (int)$$1);
            this.zMin = Math.min((int)this.zMin, (int)$$2);
            this.xMax = Math.max((int)this.xMax, (int)($$0 + 1));
            this.yMax = Math.max((int)this.yMax, (int)($$1 + 1));
            this.zMax = Math.max((int)this.zMax, (int)($$2 + 1));
        }
    }

    @Override
    public void fill(int $$0, int $$1, int $$2) {
        this.fillUpdateBounds($$0, $$1, $$2, true);
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Override
    public int firstFull(Direction.Axis $$0) {
        return $$0.choose(this.xMin, this.yMin, this.zMin);
    }

    @Override
    public int lastFull(Direction.Axis $$0) {
        return $$0.choose(this.xMax, this.yMax, this.zMax);
    }

    static BitSetDiscreteVoxelShape join(DiscreteVoxelShape $$0, DiscreteVoxelShape $$1, IndexMerger $$2, IndexMerger $$3, IndexMerger $$4, BooleanOp $$5) {
        BitSetDiscreteVoxelShape $$6 = new BitSetDiscreteVoxelShape($$2.size() - 1, $$3.size() - 1, $$4.size() - 1);
        int[] $$72 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        $$2.forMergedIndexes(($$7, $$8, $$9) -> {
            boolean[] $$102 = new boolean[]{false};
            $$3.forMergedIndexes(($$10, $$11, $$122) -> {
                boolean[] $$132 = new boolean[]{false};
                $$4.forMergedIndexes(($$12, $$13, $$14) -> {
                    if ($$5.apply($$0.isFullWide($$7, $$10, $$12), $$1.isFullWide($$8, $$11, $$13))) {
                        $$7.storage.set($$6.getIndex($$9, $$122, $$14));
                        $$10[2] = Math.min((int)$$72[2], (int)$$14);
                        $$10[5] = Math.max((int)$$72[5], (int)$$14);
                        $$11[0] = true;
                    }
                    return true;
                });
                if ($$132[0]) {
                    $$8[1] = Math.min((int)$$72[1], (int)$$122);
                    $$8[4] = Math.max((int)$$72[4], (int)$$122);
                    $$9[0] = true;
                }
                return true;
            });
            if ($$102[0]) {
                $$6[0] = Math.min((int)$$72[0], (int)$$9);
                $$6[3] = Math.max((int)$$72[3], (int)$$9);
            }
            return true;
        });
        $$6.xMin = $$72[0];
        $$6.yMin = $$72[1];
        $$6.zMin = $$72[2];
        $$6.xMax = $$72[3] + 1;
        $$6.yMax = $$72[4] + 1;
        $$6.zMax = $$72[5] + 1;
        return $$6;
    }

    protected static void forAllBoxes(DiscreteVoxelShape $$0, DiscreteVoxelShape.IntLineConsumer $$1, boolean $$2) {
        BitSetDiscreteVoxelShape $$3 = new BitSetDiscreteVoxelShape($$0);
        for (int $$4 = 0; $$4 < $$3.ySize; ++$$4) {
            for (int $$5 = 0; $$5 < $$3.xSize; ++$$5) {
                int $$6 = -1;
                for (int $$7 = 0; $$7 <= $$3.zSize; ++$$7) {
                    if ($$3.isFullWide($$5, $$4, $$7)) {
                        if ($$2) {
                            if ($$6 != -1) continue;
                            $$6 = $$7;
                            continue;
                        }
                        $$1.consume($$5, $$4, $$7, $$5 + 1, $$4 + 1, $$7 + 1);
                        continue;
                    }
                    if ($$6 == -1) continue;
                    int $$8 = $$5;
                    int $$9 = $$4;
                    $$3.clearZStrip($$6, $$7, $$5, $$4);
                    while ($$3.isZStripFull($$6, $$7, $$8 + 1, $$4)) {
                        $$3.clearZStrip($$6, $$7, $$8 + 1, $$4);
                        ++$$8;
                    }
                    while ($$3.isXZRectangleFull($$5, $$8 + 1, $$6, $$7, $$9 + 1)) {
                        for (int $$10 = $$5; $$10 <= $$8; ++$$10) {
                            $$3.clearZStrip($$6, $$7, $$10, $$9 + 1);
                        }
                        ++$$9;
                    }
                    $$1.consume($$5, $$4, $$6, $$8 + 1, $$9 + 1, $$7);
                    $$6 = -1;
                }
            }
        }
    }

    private boolean isZStripFull(int $$0, int $$1, int $$2, int $$3) {
        if ($$2 >= this.xSize || $$3 >= this.ySize) {
            return false;
        }
        return this.storage.nextClearBit(this.getIndex($$2, $$3, $$0)) >= this.getIndex($$2, $$3, $$1);
    }

    private boolean isXZRectangleFull(int $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$0; $$5 < $$1; ++$$5) {
            if (this.isZStripFull($$2, $$3, $$5, $$4)) continue;
            return false;
        }
        return true;
    }

    private void clearZStrip(int $$0, int $$1, int $$2, int $$3) {
        this.storage.clear(this.getIndex($$2, $$3, $$0), this.getIndex($$2, $$3, $$1));
    }
}