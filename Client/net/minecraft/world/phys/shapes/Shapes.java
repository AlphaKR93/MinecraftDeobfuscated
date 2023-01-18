/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.math.DoubleMath
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.Objects
 */
package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteCubeMerger;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IdenticalMerger;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.IndirectMerger;
import net.minecraft.world.phys.shapes.NonOverlappingMerger;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Shapes {
    public static final double EPSILON = 1.0E-7;
    public static final double BIG_EPSILON = 1.0E-6;
    private static final VoxelShape BLOCK = (VoxelShape)Util.make(() -> {
        BitSetDiscreteVoxelShape $$0 = new BitSetDiscreteVoxelShape(1, 1, 1);
        ((DiscreteVoxelShape)$$0).fill(0, 0, 0);
        return new CubeVoxelShape($$0);
    });
    public static final VoxelShape INFINITY = Shapes.box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new ArrayVoxelShape((DiscreteVoxelShape)new BitSetDiscreteVoxelShape(0, 0, 0), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}));

    public static VoxelShape empty() {
        return EMPTY;
    }

    public static VoxelShape block() {
        return BLOCK;
    }

    public static VoxelShape box(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        if ($$0 > $$3 || $$1 > $$4 || $$2 > $$5) {
            throw new IllegalArgumentException("The min values need to be smaller or equals to the max values");
        }
        return Shapes.create($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public static VoxelShape create(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        if ($$3 - $$0 < 1.0E-7 || $$4 - $$1 < 1.0E-7 || $$5 - $$2 < 1.0E-7) {
            return Shapes.empty();
        }
        int $$6 = Shapes.findBits($$0, $$3);
        int $$7 = Shapes.findBits($$1, $$4);
        int $$8 = Shapes.findBits($$2, $$5);
        if ($$6 < 0 || $$7 < 0 || $$8 < 0) {
            return new ArrayVoxelShape(Shapes.BLOCK.shape, (DoubleList)DoubleArrayList.wrap((double[])new double[]{$$0, $$3}), (DoubleList)DoubleArrayList.wrap((double[])new double[]{$$1, $$4}), (DoubleList)DoubleArrayList.wrap((double[])new double[]{$$2, $$5}));
        }
        if ($$6 == 0 && $$7 == 0 && $$8 == 0) {
            return Shapes.block();
        }
        int $$9 = 1 << $$6;
        int $$10 = 1 << $$7;
        int $$11 = 1 << $$8;
        BitSetDiscreteVoxelShape $$12 = BitSetDiscreteVoxelShape.withFilledBounds($$9, $$10, $$11, (int)Math.round((double)($$0 * (double)$$9)), (int)Math.round((double)($$1 * (double)$$10)), (int)Math.round((double)($$2 * (double)$$11)), (int)Math.round((double)($$3 * (double)$$9)), (int)Math.round((double)($$4 * (double)$$10)), (int)Math.round((double)($$5 * (double)$$11)));
        return new CubeVoxelShape($$12);
    }

    public static VoxelShape create(AABB $$0) {
        return Shapes.create($$0.minX, $$0.minY, $$0.minZ, $$0.maxX, $$0.maxY, $$0.maxZ);
    }

    @VisibleForTesting
    protected static int findBits(double $$0, double $$1) {
        if ($$0 < -1.0E-7 || $$1 > 1.0000001) {
            return -1;
        }
        for (int $$2 = 0; $$2 <= 3; ++$$2) {
            boolean $$7;
            int $$3 = 1 << $$2;
            double $$4 = $$0 * (double)$$3;
            double $$5 = $$1 * (double)$$3;
            boolean $$6 = Math.abs((double)($$4 - (double)Math.round((double)$$4))) < 1.0E-7 * (double)$$3;
            boolean bl = $$7 = Math.abs((double)($$5 - (double)Math.round((double)$$5))) < 1.0E-7 * (double)$$3;
            if (!$$6 || !$$7) continue;
            return $$2;
        }
        return -1;
    }

    protected static long lcm(int $$0, int $$1) {
        return (long)$$0 * (long)($$1 / IntMath.gcd((int)$$0, (int)$$1));
    }

    public static VoxelShape or(VoxelShape $$0, VoxelShape $$1) {
        return Shapes.join($$0, $$1, BooleanOp.OR);
    }

    public static VoxelShape or(VoxelShape $$0, VoxelShape ... $$1) {
        return (VoxelShape)Arrays.stream((Object[])$$1).reduce((Object)$$0, Shapes::or);
    }

    public static VoxelShape join(VoxelShape $$0, VoxelShape $$1, BooleanOp $$2) {
        return Shapes.joinUnoptimized($$0, $$1, $$2).optimize();
    }

    public static VoxelShape joinUnoptimized(VoxelShape $$0, VoxelShape $$1, BooleanOp $$2) {
        if ($$2.apply(false, false)) {
            throw Util.pauseInIde(new IllegalArgumentException());
        }
        if ($$0 == $$1) {
            return $$2.apply(true, true) ? $$0 : Shapes.empty();
        }
        boolean $$3 = $$2.apply(true, false);
        boolean $$4 = $$2.apply(false, true);
        if ($$0.isEmpty()) {
            return $$4 ? $$1 : Shapes.empty();
        }
        if ($$1.isEmpty()) {
            return $$3 ? $$0 : Shapes.empty();
        }
        IndexMerger $$5 = Shapes.createIndexMerger(1, $$0.getCoords(Direction.Axis.X), $$1.getCoords(Direction.Axis.X), $$3, $$4);
        IndexMerger $$6 = Shapes.createIndexMerger($$5.size() - 1, $$0.getCoords(Direction.Axis.Y), $$1.getCoords(Direction.Axis.Y), $$3, $$4);
        IndexMerger $$7 = Shapes.createIndexMerger(($$5.size() - 1) * ($$6.size() - 1), $$0.getCoords(Direction.Axis.Z), $$1.getCoords(Direction.Axis.Z), $$3, $$4);
        BitSetDiscreteVoxelShape $$8 = BitSetDiscreteVoxelShape.join($$0.shape, $$1.shape, $$5, $$6, $$7, $$2);
        if ($$5 instanceof DiscreteCubeMerger && $$6 instanceof DiscreteCubeMerger && $$7 instanceof DiscreteCubeMerger) {
            return new CubeVoxelShape($$8);
        }
        return new ArrayVoxelShape((DiscreteVoxelShape)$$8, $$5.getList(), $$6.getList(), $$7.getList());
    }

    public static boolean joinIsNotEmpty(VoxelShape $$0, VoxelShape $$1, BooleanOp $$2) {
        if ($$2.apply(false, false)) {
            throw Util.pauseInIde(new IllegalArgumentException());
        }
        boolean $$3 = $$0.isEmpty();
        boolean $$4 = $$1.isEmpty();
        if ($$3 || $$4) {
            return $$2.apply(!$$3, !$$4);
        }
        if ($$0 == $$1) {
            return $$2.apply(true, true);
        }
        boolean $$5 = $$2.apply(true, false);
        boolean $$6 = $$2.apply(false, true);
        for (Direction.Axis $$7 : AxisCycle.AXIS_VALUES) {
            if ($$0.max($$7) < $$1.min($$7) - 1.0E-7) {
                return $$5 || $$6;
            }
            if (!($$1.max($$7) < $$0.min($$7) - 1.0E-7)) continue;
            return $$5 || $$6;
        }
        IndexMerger $$8 = Shapes.createIndexMerger(1, $$0.getCoords(Direction.Axis.X), $$1.getCoords(Direction.Axis.X), $$5, $$6);
        IndexMerger $$9 = Shapes.createIndexMerger($$8.size() - 1, $$0.getCoords(Direction.Axis.Y), $$1.getCoords(Direction.Axis.Y), $$5, $$6);
        IndexMerger $$10 = Shapes.createIndexMerger(($$8.size() - 1) * ($$9.size() - 1), $$0.getCoords(Direction.Axis.Z), $$1.getCoords(Direction.Axis.Z), $$5, $$6);
        return Shapes.joinIsNotEmpty($$8, $$9, $$10, $$0.shape, $$1.shape, $$2);
    }

    private static boolean joinIsNotEmpty(IndexMerger $$0, IndexMerger $$1, IndexMerger $$2, DiscreteVoxelShape $$3, DiscreteVoxelShape $$4, BooleanOp $$52) {
        return !$$0.forMergedIndexes(($$5, $$62, $$7) -> $$1.forMergedIndexes(($$6, $$72, $$82) -> $$2.forMergedIndexes(($$7, $$8, $$9) -> !$$52.apply($$3.isFullWide($$5, $$6, $$7), $$4.isFullWide($$62, $$72, $$8)))));
    }

    public static double collide(Direction.Axis $$0, AABB $$1, Iterable<VoxelShape> $$2, double $$3) {
        for (VoxelShape $$4 : $$2) {
            if (Math.abs((double)$$3) < 1.0E-7) {
                return 0.0;
            }
            $$3 = $$4.collide($$0, $$1, $$3);
        }
        return $$3;
    }

    public static boolean blockOccudes(VoxelShape $$0, VoxelShape $$1, Direction $$2) {
        if ($$0 == Shapes.block() && $$1 == Shapes.block()) {
            return true;
        }
        if ($$1.isEmpty()) {
            return false;
        }
        Direction.Axis $$3 = $$2.getAxis();
        Direction.AxisDirection $$4 = $$2.getAxisDirection();
        VoxelShape $$5 = $$4 == Direction.AxisDirection.POSITIVE ? $$0 : $$1;
        VoxelShape $$6 = $$4 == Direction.AxisDirection.POSITIVE ? $$1 : $$0;
        BooleanOp $$7 = $$4 == Direction.AxisDirection.POSITIVE ? BooleanOp.ONLY_FIRST : BooleanOp.ONLY_SECOND;
        return DoubleMath.fuzzyEquals((double)$$5.max($$3), (double)1.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)$$6.min($$3), (double)0.0, (double)1.0E-7) && !Shapes.joinIsNotEmpty(new SliceShape($$5, $$3, $$5.shape.getSize($$3) - 1), new SliceShape($$6, $$3, 0), $$7);
    }

    public static VoxelShape getFaceShape(VoxelShape $$0, Direction $$1) {
        int $$6;
        boolean $$5;
        if ($$0 == Shapes.block()) {
            return Shapes.block();
        }
        Direction.Axis $$2 = $$1.getAxis();
        if ($$1.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            boolean $$3 = DoubleMath.fuzzyEquals((double)$$0.max($$2), (double)1.0, (double)1.0E-7);
            int $$4 = $$0.shape.getSize($$2) - 1;
        } else {
            $$5 = DoubleMath.fuzzyEquals((double)$$0.min($$2), (double)0.0, (double)1.0E-7);
            $$6 = 0;
        }
        if (!$$5) {
            return Shapes.empty();
        }
        return new SliceShape($$0, $$2, $$6);
    }

    public static boolean mergedFaceOccludes(VoxelShape $$0, VoxelShape $$1, Direction $$2) {
        VoxelShape $$6;
        if ($$0 == Shapes.block() || $$1 == Shapes.block()) {
            return true;
        }
        Direction.Axis $$3 = $$2.getAxis();
        Direction.AxisDirection $$4 = $$2.getAxisDirection();
        VoxelShape $$5 = $$4 == Direction.AxisDirection.POSITIVE ? $$0 : $$1;
        VoxelShape voxelShape = $$6 = $$4 == Direction.AxisDirection.POSITIVE ? $$1 : $$0;
        if (!DoubleMath.fuzzyEquals((double)$$5.max($$3), (double)1.0, (double)1.0E-7)) {
            $$5 = Shapes.empty();
        }
        if (!DoubleMath.fuzzyEquals((double)$$6.min($$3), (double)0.0, (double)1.0E-7)) {
            $$6 = Shapes.empty();
        }
        return !Shapes.joinIsNotEmpty(Shapes.block(), Shapes.joinUnoptimized(new SliceShape($$5, $$3, $$5.shape.getSize($$3) - 1), new SliceShape($$6, $$3, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
    }

    public static boolean faceShapeOccludes(VoxelShape $$0, VoxelShape $$1) {
        if ($$0 == Shapes.block() || $$1 == Shapes.block()) {
            return true;
        }
        if ($$0.isEmpty() && $$1.isEmpty()) {
            return false;
        }
        return !Shapes.joinIsNotEmpty(Shapes.block(), Shapes.joinUnoptimized($$0, $$1, BooleanOp.OR), BooleanOp.ONLY_FIRST);
    }

    @VisibleForTesting
    protected static IndexMerger createIndexMerger(int $$0, DoubleList $$1, DoubleList $$2, boolean $$3, boolean $$4) {
        long $$7;
        int $$5 = $$1.size() - 1;
        int $$6 = $$2.size() - 1;
        if ($$1 instanceof CubePointRange && $$2 instanceof CubePointRange && (long)$$0 * ($$7 = Shapes.lcm($$5, $$6)) <= 256L) {
            return new DiscreteCubeMerger($$5, $$6);
        }
        if ($$1.getDouble($$5) < $$2.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger($$1, $$2, false);
        }
        if ($$2.getDouble($$6) < $$1.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger($$2, $$1, true);
        }
        if ($$5 == $$6 && Objects.equals((Object)$$1, (Object)$$2)) {
            return new IdenticalMerger($$1);
        }
        return new IndirectMerger($$1, $$2, $$3, $$4);
    }

    public static interface DoubleLineConsumer {
        public void consume(double var1, double var3, double var5, double var7, double var9, double var11);
    }
}