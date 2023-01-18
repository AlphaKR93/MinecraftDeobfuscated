/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.math.DoubleMath
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.phys.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.OffsetDoubleList;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;

public abstract class VoxelShape {
    protected final DiscreteVoxelShape shape;
    @Nullable
    private VoxelShape[] faces;

    VoxelShape(DiscreteVoxelShape $$0) {
        this.shape = $$0;
    }

    public double min(Direction.Axis $$0) {
        int $$1 = this.shape.firstFull($$0);
        if ($$1 >= this.shape.getSize($$0)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.get($$0, $$1);
    }

    public double max(Direction.Axis $$0) {
        int $$1 = this.shape.lastFull($$0);
        if ($$1 <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.get($$0, $$1);
    }

    public AABB bounds() {
        if (this.isEmpty()) {
            throw Util.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
        }
        return new AABB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
    }

    protected double get(Direction.Axis $$0, int $$1) {
        return this.getCoords($$0).getDouble($$1);
    }

    protected abstract DoubleList getCoords(Direction.Axis var1);

    public boolean isEmpty() {
        return this.shape.isEmpty();
    }

    public VoxelShape move(double $$0, double $$1, double $$2) {
        if (this.isEmpty()) {
            return Shapes.empty();
        }
        return new ArrayVoxelShape(this.shape, (DoubleList)new OffsetDoubleList(this.getCoords(Direction.Axis.X), $$0), (DoubleList)new OffsetDoubleList(this.getCoords(Direction.Axis.Y), $$1), (DoubleList)new OffsetDoubleList(this.getCoords(Direction.Axis.Z), $$2));
    }

    public VoxelShape optimize() {
        VoxelShape[] $$0 = new VoxelShape[]{Shapes.empty()};
        this.forAllBoxes(($$1, $$2, $$3, $$4, $$5, $$6) -> {
            $$0[0] = Shapes.joinUnoptimized($$0[0], Shapes.box($$1, $$2, $$3, $$4, $$5, $$6), BooleanOp.OR);
        });
        return $$0[0];
    }

    public void forAllEdges(Shapes.DoubleLineConsumer $$0) {
        this.shape.forAllEdges(($$1, $$2, $$3, $$4, $$5, $$6) -> $$0.consume(this.get(Direction.Axis.X, $$1), this.get(Direction.Axis.Y, $$2), this.get(Direction.Axis.Z, $$3), this.get(Direction.Axis.X, $$4), this.get(Direction.Axis.Y, $$5), this.get(Direction.Axis.Z, $$6)), true);
    }

    public void forAllBoxes(Shapes.DoubleLineConsumer $$0) {
        DoubleList $$1 = this.getCoords(Direction.Axis.X);
        DoubleList $$2 = this.getCoords(Direction.Axis.Y);
        DoubleList $$3 = this.getCoords(Direction.Axis.Z);
        this.shape.forAllBoxes(($$4, $$5, $$6, $$7, $$8, $$9) -> $$0.consume($$1.getDouble($$4), $$2.getDouble($$5), $$3.getDouble($$6), $$1.getDouble($$7), $$2.getDouble($$8), $$3.getDouble($$9)), true);
    }

    public List<AABB> toAabbs() {
        ArrayList $$0 = Lists.newArrayList();
        this.forAllBoxes((arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> VoxelShape.lambda$toAabbs$3((List)$$0, arg_0, arg_1, arg_2, arg_3, arg_4, arg_5));
        return $$0;
    }

    public double min(Direction.Axis $$0, double $$1, double $$2) {
        int $$6;
        Direction.Axis $$3 = AxisCycle.FORWARD.cycle($$0);
        Direction.Axis $$4 = AxisCycle.BACKWARD.cycle($$0);
        int $$5 = this.findIndex($$3, $$1);
        int $$7 = this.shape.firstFull($$0, $$5, $$6 = this.findIndex($$4, $$2));
        if ($$7 >= this.shape.getSize($$0)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.get($$0, $$7);
    }

    public double max(Direction.Axis $$0, double $$1, double $$2) {
        int $$6;
        Direction.Axis $$3 = AxisCycle.FORWARD.cycle($$0);
        Direction.Axis $$4 = AxisCycle.BACKWARD.cycle($$0);
        int $$5 = this.findIndex($$3, $$1);
        int $$7 = this.shape.lastFull($$0, $$5, $$6 = this.findIndex($$4, $$2));
        if ($$7 <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.get($$0, $$7);
    }

    protected int findIndex(Direction.Axis $$0, double $$1) {
        return Mth.binarySearch(0, this.shape.getSize($$0) + 1, $$2 -> $$1 < this.get($$0, $$2)) - 1;
    }

    @Nullable
    public BlockHitResult clip(Vec3 $$0, Vec3 $$1, BlockPos $$2) {
        if (this.isEmpty()) {
            return null;
        }
        Vec3 $$3 = $$1.subtract($$0);
        if ($$3.lengthSqr() < 1.0E-7) {
            return null;
        }
        Vec3 $$4 = $$0.add($$3.scale(0.001));
        if (this.shape.isFullWide(this.findIndex(Direction.Axis.X, $$4.x - (double)$$2.getX()), this.findIndex(Direction.Axis.Y, $$4.y - (double)$$2.getY()), this.findIndex(Direction.Axis.Z, $$4.z - (double)$$2.getZ()))) {
            return new BlockHitResult($$4, Direction.getNearest($$3.x, $$3.y, $$3.z).getOpposite(), $$2, true);
        }
        return AABB.clip(this.toAabbs(), $$0, $$1, $$2);
    }

    public Optional<Vec3> closestPointTo(Vec3 $$0) {
        if (this.isEmpty()) {
            return Optional.empty();
        }
        Vec3[] $$1 = new Vec3[1];
        this.forAllBoxes(($$2, $$3, $$4, $$5, $$6, $$7) -> {
            double $$8 = Mth.clamp($$0.x(), $$2, $$5);
            double $$9 = Mth.clamp($$0.y(), $$3, $$6);
            double $$10 = Mth.clamp($$0.z(), $$4, $$7);
            if ($$1[0] == null || $$0.distanceToSqr($$8, $$9, $$10) < $$0.distanceToSqr($$1[0])) {
                $$1[0] = new Vec3($$8, $$9, $$10);
            }
        });
        return Optional.of((Object)$$1[0]);
    }

    public VoxelShape getFaceShape(Direction $$0) {
        VoxelShape $$2;
        if (this.isEmpty() || this == Shapes.block()) {
            return this;
        }
        if (this.faces != null) {
            VoxelShape $$1 = this.faces[$$0.ordinal()];
            if ($$1 != null) {
                return $$1;
            }
        } else {
            this.faces = new VoxelShape[6];
        }
        this.faces[$$0.ordinal()] = $$2 = this.calculateFace($$0);
        return $$2;
    }

    private VoxelShape calculateFace(Direction $$0) {
        Direction.Axis $$1 = $$0.getAxis();
        DoubleList $$2 = this.getCoords($$1);
        if ($$2.size() == 2 && DoubleMath.fuzzyEquals((double)$$2.getDouble(0), (double)0.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)$$2.getDouble(1), (double)1.0, (double)1.0E-7)) {
            return this;
        }
        Direction.AxisDirection $$3 = $$0.getAxisDirection();
        int $$4 = this.findIndex($$1, $$3 == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
        return new SliceShape(this, $$1, $$4);
    }

    public double collide(Direction.Axis $$0, AABB $$1, double $$2) {
        return this.collideX(AxisCycle.between($$0, Direction.Axis.X), $$1, $$2);
    }

    protected double collideX(AxisCycle $$0, AABB $$1, double $$2) {
        block11: {
            int $$14;
            int $$12;
            double $$8;
            Direction.Axis $$4;
            AxisCycle $$3;
            block10: {
                if (this.isEmpty()) {
                    return $$2;
                }
                if (Math.abs((double)$$2) < 1.0E-7) {
                    return 0.0;
                }
                $$3 = $$0.inverse();
                $$4 = $$3.cycle(Direction.Axis.X);
                Direction.Axis $$5 = $$3.cycle(Direction.Axis.Y);
                Direction.Axis $$6 = $$3.cycle(Direction.Axis.Z);
                double $$7 = $$1.max($$4);
                $$8 = $$1.min($$4);
                int $$9 = this.findIndex($$4, $$8 + 1.0E-7);
                int $$10 = this.findIndex($$4, $$7 - 1.0E-7);
                int $$11 = Math.max((int)0, (int)this.findIndex($$5, $$1.min($$5) + 1.0E-7));
                $$12 = Math.min((int)this.shape.getSize($$5), (int)(this.findIndex($$5, $$1.max($$5) - 1.0E-7) + 1));
                int $$13 = Math.max((int)0, (int)this.findIndex($$6, $$1.min($$6) + 1.0E-7));
                $$14 = Math.min((int)this.shape.getSize($$6), (int)(this.findIndex($$6, $$1.max($$6) - 1.0E-7) + 1));
                int $$15 = this.shape.getSize($$4);
                if (!($$2 > 0.0)) break block10;
                for (int $$16 = $$10 + 1; $$16 < $$15; ++$$16) {
                    for (int $$17 = $$11; $$17 < $$12; ++$$17) {
                        for (int $$18 = $$13; $$18 < $$14; ++$$18) {
                            if (!this.shape.isFullWide($$3, $$16, $$17, $$18)) continue;
                            double $$19 = this.get($$4, $$16) - $$7;
                            if ($$19 >= -1.0E-7) {
                                $$2 = Math.min((double)$$2, (double)$$19);
                            }
                            return $$2;
                        }
                    }
                }
                break block11;
            }
            if (!($$2 < 0.0)) break block11;
            for (int $$20 = $$9 - 1; $$20 >= 0; --$$20) {
                for (int $$21 = $$11; $$21 < $$12; ++$$21) {
                    for (int $$22 = $$13; $$22 < $$14; ++$$22) {
                        if (!this.shape.isFullWide($$3, $$20, $$21, $$22)) continue;
                        double $$23 = this.get($$4, $$20 + 1) - $$8;
                        if ($$23 <= 1.0E-7) {
                            $$2 = Math.max((double)$$2, (double)$$23);
                        }
                        return $$2;
                    }
                }
            }
        }
        return $$2;
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.bounds() + "]";
    }

    private static /* synthetic */ void lambda$toAabbs$3(List $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        $$0.add((Object)new AABB($$1, $$2, $$3, $$4, $$5, $$6));
    }
}