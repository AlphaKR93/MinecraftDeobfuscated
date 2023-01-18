/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 */
package net.minecraft.world.phys.shapes;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;

public abstract class DiscreteVoxelShape {
    private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    protected DiscreteVoxelShape(int $$0, int $$1, int $$2) {
        if ($$0 < 0 || $$1 < 0 || $$2 < 0) {
            throw new IllegalArgumentException("Need all positive sizes: x: " + $$0 + ", y: " + $$1 + ", z: " + $$2);
        }
        this.xSize = $$0;
        this.ySize = $$1;
        this.zSize = $$2;
    }

    public boolean isFullWide(AxisCycle $$0, int $$1, int $$2, int $$3) {
        return this.isFullWide($$0.cycle($$1, $$2, $$3, Direction.Axis.X), $$0.cycle($$1, $$2, $$3, Direction.Axis.Y), $$0.cycle($$1, $$2, $$3, Direction.Axis.Z));
    }

    public boolean isFullWide(int $$0, int $$1, int $$2) {
        if ($$0 < 0 || $$1 < 0 || $$2 < 0) {
            return false;
        }
        if ($$0 >= this.xSize || $$1 >= this.ySize || $$2 >= this.zSize) {
            return false;
        }
        return this.isFull($$0, $$1, $$2);
    }

    public boolean isFull(AxisCycle $$0, int $$1, int $$2, int $$3) {
        return this.isFull($$0.cycle($$1, $$2, $$3, Direction.Axis.X), $$0.cycle($$1, $$2, $$3, Direction.Axis.Y), $$0.cycle($$1, $$2, $$3, Direction.Axis.Z));
    }

    public abstract boolean isFull(int var1, int var2, int var3);

    public abstract void fill(int var1, int var2, int var3);

    public boolean isEmpty() {
        for (Direction.Axis $$0 : AXIS_VALUES) {
            if (this.firstFull($$0) < this.lastFull($$0)) continue;
            return true;
        }
        return false;
    }

    public abstract int firstFull(Direction.Axis var1);

    public abstract int lastFull(Direction.Axis var1);

    public int firstFull(Direction.Axis $$0, int $$1, int $$2) {
        int $$3 = this.getSize($$0);
        if ($$1 < 0 || $$2 < 0) {
            return $$3;
        }
        Direction.Axis $$4 = AxisCycle.FORWARD.cycle($$0);
        Direction.Axis $$5 = AxisCycle.BACKWARD.cycle($$0);
        if ($$1 >= this.getSize($$4) || $$2 >= this.getSize($$5)) {
            return $$3;
        }
        AxisCycle $$6 = AxisCycle.between(Direction.Axis.X, $$0);
        for (int $$7 = 0; $$7 < $$3; ++$$7) {
            if (!this.isFull($$6, $$7, $$1, $$2)) continue;
            return $$7;
        }
        return $$3;
    }

    public int lastFull(Direction.Axis $$0, int $$1, int $$2) {
        if ($$1 < 0 || $$2 < 0) {
            return 0;
        }
        Direction.Axis $$3 = AxisCycle.FORWARD.cycle($$0);
        Direction.Axis $$4 = AxisCycle.BACKWARD.cycle($$0);
        if ($$1 >= this.getSize($$3) || $$2 >= this.getSize($$4)) {
            return 0;
        }
        int $$5 = this.getSize($$0);
        AxisCycle $$6 = AxisCycle.between(Direction.Axis.X, $$0);
        for (int $$7 = $$5 - 1; $$7 >= 0; --$$7) {
            if (!this.isFull($$6, $$7, $$1, $$2)) continue;
            return $$7 + 1;
        }
        return 0;
    }

    public int getSize(Direction.Axis $$0) {
        return $$0.choose(this.xSize, this.ySize, this.zSize);
    }

    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }

    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }

    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }

    public void forAllEdges(IntLineConsumer $$0, boolean $$1) {
        this.forAllAxisEdges($$0, AxisCycle.NONE, $$1);
        this.forAllAxisEdges($$0, AxisCycle.FORWARD, $$1);
        this.forAllAxisEdges($$0, AxisCycle.BACKWARD, $$1);
    }

    private void forAllAxisEdges(IntLineConsumer $$0, AxisCycle $$1, boolean $$2) {
        AxisCycle $$3 = $$1.inverse();
        int $$4 = this.getSize($$3.cycle(Direction.Axis.X));
        int $$5 = this.getSize($$3.cycle(Direction.Axis.Y));
        int $$6 = this.getSize($$3.cycle(Direction.Axis.Z));
        for (int $$7 = 0; $$7 <= $$4; ++$$7) {
            for (int $$8 = 0; $$8 <= $$5; ++$$8) {
                int $$9 = -1;
                for (int $$10 = 0; $$10 <= $$6; ++$$10) {
                    int $$11 = 0;
                    int $$12 = 0;
                    for (int $$13 = 0; $$13 <= 1; ++$$13) {
                        for (int $$14 = 0; $$14 <= 1; ++$$14) {
                            if (!this.isFullWide($$3, $$7 + $$13 - 1, $$8 + $$14 - 1, $$10)) continue;
                            ++$$11;
                            $$12 ^= $$13 ^ $$14;
                        }
                    }
                    if ($$11 == 1 || $$11 == 3 || $$11 == 2 && !($$12 & true)) {
                        if ($$2) {
                            if ($$9 != -1) continue;
                            $$9 = $$10;
                            continue;
                        }
                        $$0.consume($$3.cycle($$7, $$8, $$10, Direction.Axis.X), $$3.cycle($$7, $$8, $$10, Direction.Axis.Y), $$3.cycle($$7, $$8, $$10, Direction.Axis.Z), $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.X), $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.Y), $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.Z));
                        continue;
                    }
                    if ($$9 == -1) continue;
                    $$0.consume($$3.cycle($$7, $$8, $$9, Direction.Axis.X), $$3.cycle($$7, $$8, $$9, Direction.Axis.Y), $$3.cycle($$7, $$8, $$9, Direction.Axis.Z), $$3.cycle($$7, $$8, $$10, Direction.Axis.X), $$3.cycle($$7, $$8, $$10, Direction.Axis.Y), $$3.cycle($$7, $$8, $$10, Direction.Axis.Z));
                    $$9 = -1;
                }
            }
        }
    }

    public void forAllBoxes(IntLineConsumer $$0, boolean $$1) {
        BitSetDiscreteVoxelShape.forAllBoxes(this, $$0, $$1);
    }

    public void forAllFaces(IntFaceConsumer $$0) {
        this.forAllAxisFaces($$0, AxisCycle.NONE);
        this.forAllAxisFaces($$0, AxisCycle.FORWARD);
        this.forAllAxisFaces($$0, AxisCycle.BACKWARD);
    }

    private void forAllAxisFaces(IntFaceConsumer $$0, AxisCycle $$1) {
        AxisCycle $$2 = $$1.inverse();
        Direction.Axis $$3 = $$2.cycle(Direction.Axis.Z);
        int $$4 = this.getSize($$2.cycle(Direction.Axis.X));
        int $$5 = this.getSize($$2.cycle(Direction.Axis.Y));
        int $$6 = this.getSize($$3);
        Direction $$7 = Direction.fromAxisAndDirection($$3, Direction.AxisDirection.NEGATIVE);
        Direction $$8 = Direction.fromAxisAndDirection($$3, Direction.AxisDirection.POSITIVE);
        for (int $$9 = 0; $$9 < $$4; ++$$9) {
            for (int $$10 = 0; $$10 < $$5; ++$$10) {
                boolean $$11 = false;
                for (int $$12 = 0; $$12 <= $$6; ++$$12) {
                    boolean $$13;
                    boolean bl = $$13 = $$12 != $$6 && this.isFull($$2, $$9, $$10, $$12);
                    if (!$$11 && $$13) {
                        $$0.consume($$7, $$2.cycle($$9, $$10, $$12, Direction.Axis.X), $$2.cycle($$9, $$10, $$12, Direction.Axis.Y), $$2.cycle($$9, $$10, $$12, Direction.Axis.Z));
                    }
                    if ($$11 && !$$13) {
                        $$0.consume($$8, $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.X), $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.Y), $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.Z));
                    }
                    $$11 = $$13;
                }
            }
        }
    }

    public static interface IntLineConsumer {
        public void consume(int var1, int var2, int var3, int var4, int var5, int var6);
    }

    public static interface IntFaceConsumer {
        public void consume(Direction var1, int var2, int var3, int var4);
    }
}