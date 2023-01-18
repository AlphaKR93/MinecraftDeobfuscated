/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.phys;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AABB {
    private static final double EPSILON = 1.0E-7;
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AABB(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        this.minX = Math.min((double)$$0, (double)$$3);
        this.minY = Math.min((double)$$1, (double)$$4);
        this.minZ = Math.min((double)$$2, (double)$$5);
        this.maxX = Math.max((double)$$0, (double)$$3);
        this.maxY = Math.max((double)$$1, (double)$$4);
        this.maxZ = Math.max((double)$$2, (double)$$5);
    }

    public AABB(BlockPos $$0) {
        this($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getX() + 1, $$0.getY() + 1, $$0.getZ() + 1);
    }

    public AABB(BlockPos $$0, BlockPos $$1) {
        this($$0.getX(), $$0.getY(), $$0.getZ(), $$1.getX(), $$1.getY(), $$1.getZ());
    }

    public AABB(Vec3 $$0, Vec3 $$1) {
        this($$0.x, $$0.y, $$0.z, $$1.x, $$1.y, $$1.z);
    }

    public static AABB of(BoundingBox $$0) {
        return new AABB($$0.minX(), $$0.minY(), $$0.minZ(), $$0.maxX() + 1, $$0.maxY() + 1, $$0.maxZ() + 1);
    }

    public static AABB unitCubeFromLowerCorner(Vec3 $$0) {
        return new AABB($$0.x, $$0.y, $$0.z, $$0.x + 1.0, $$0.y + 1.0, $$0.z + 1.0);
    }

    public AABB setMinX(double $$0) {
        return new AABB($$0, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMinY(double $$0) {
        return new AABB(this.minX, $$0, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMinZ(double $$0) {
        return new AABB(this.minX, this.minY, $$0, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMaxX(double $$0) {
        return new AABB(this.minX, this.minY, this.minZ, $$0, this.maxY, this.maxZ);
    }

    public AABB setMaxY(double $$0) {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, $$0, this.maxZ);
    }

    public AABB setMaxZ(double $$0) {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, $$0);
    }

    public double min(Direction.Axis $$0) {
        return $$0.choose(this.minX, this.minY, this.minZ);
    }

    public double max(Direction.Axis $$0) {
        return $$0.choose(this.maxX, this.maxY, this.maxZ);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof AABB)) {
            return false;
        }
        AABB $$1 = (AABB)$$0;
        if (Double.compare((double)$$1.minX, (double)this.minX) != 0) {
            return false;
        }
        if (Double.compare((double)$$1.minY, (double)this.minY) != 0) {
            return false;
        }
        if (Double.compare((double)$$1.minZ, (double)this.minZ) != 0) {
            return false;
        }
        if (Double.compare((double)$$1.maxX, (double)this.maxX) != 0) {
            return false;
        }
        if (Double.compare((double)$$1.maxY, (double)this.maxY) != 0) {
            return false;
        }
        return Double.compare((double)$$1.maxZ, (double)this.maxZ) == 0;
    }

    public int hashCode() {
        long $$0 = Double.doubleToLongBits((double)this.minX);
        int $$1 = (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits((double)this.minY);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits((double)this.minZ);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits((double)this.maxX);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits((double)this.maxY);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits((double)this.maxZ);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        return $$1;
    }

    public AABB contract(double $$0, double $$1, double $$2) {
        double $$3 = this.minX;
        double $$4 = this.minY;
        double $$5 = this.minZ;
        double $$6 = this.maxX;
        double $$7 = this.maxY;
        double $$8 = this.maxZ;
        if ($$0 < 0.0) {
            $$3 -= $$0;
        } else if ($$0 > 0.0) {
            $$6 -= $$0;
        }
        if ($$1 < 0.0) {
            $$4 -= $$1;
        } else if ($$1 > 0.0) {
            $$7 -= $$1;
        }
        if ($$2 < 0.0) {
            $$5 -= $$2;
        } else if ($$2 > 0.0) {
            $$8 -= $$2;
        }
        return new AABB($$3, $$4, $$5, $$6, $$7, $$8);
    }

    public AABB expandTowards(Vec3 $$0) {
        return this.expandTowards($$0.x, $$0.y, $$0.z);
    }

    public AABB expandTowards(double $$0, double $$1, double $$2) {
        double $$3 = this.minX;
        double $$4 = this.minY;
        double $$5 = this.minZ;
        double $$6 = this.maxX;
        double $$7 = this.maxY;
        double $$8 = this.maxZ;
        if ($$0 < 0.0) {
            $$3 += $$0;
        } else if ($$0 > 0.0) {
            $$6 += $$0;
        }
        if ($$1 < 0.0) {
            $$4 += $$1;
        } else if ($$1 > 0.0) {
            $$7 += $$1;
        }
        if ($$2 < 0.0) {
            $$5 += $$2;
        } else if ($$2 > 0.0) {
            $$8 += $$2;
        }
        return new AABB($$3, $$4, $$5, $$6, $$7, $$8);
    }

    public AABB inflate(double $$0, double $$1, double $$2) {
        double $$3 = this.minX - $$0;
        double $$4 = this.minY - $$1;
        double $$5 = this.minZ - $$2;
        double $$6 = this.maxX + $$0;
        double $$7 = this.maxY + $$1;
        double $$8 = this.maxZ + $$2;
        return new AABB($$3, $$4, $$5, $$6, $$7, $$8);
    }

    public AABB inflate(double $$0) {
        return this.inflate($$0, $$0, $$0);
    }

    public AABB intersect(AABB $$0) {
        double $$1 = Math.max((double)this.minX, (double)$$0.minX);
        double $$2 = Math.max((double)this.minY, (double)$$0.minY);
        double $$3 = Math.max((double)this.minZ, (double)$$0.minZ);
        double $$4 = Math.min((double)this.maxX, (double)$$0.maxX);
        double $$5 = Math.min((double)this.maxY, (double)$$0.maxY);
        double $$6 = Math.min((double)this.maxZ, (double)$$0.maxZ);
        return new AABB($$1, $$2, $$3, $$4, $$5, $$6);
    }

    public AABB minmax(AABB $$0) {
        double $$1 = Math.min((double)this.minX, (double)$$0.minX);
        double $$2 = Math.min((double)this.minY, (double)$$0.minY);
        double $$3 = Math.min((double)this.minZ, (double)$$0.minZ);
        double $$4 = Math.max((double)this.maxX, (double)$$0.maxX);
        double $$5 = Math.max((double)this.maxY, (double)$$0.maxY);
        double $$6 = Math.max((double)this.maxZ, (double)$$0.maxZ);
        return new AABB($$1, $$2, $$3, $$4, $$5, $$6);
    }

    public AABB move(double $$0, double $$1, double $$2) {
        return new AABB(this.minX + $$0, this.minY + $$1, this.minZ + $$2, this.maxX + $$0, this.maxY + $$1, this.maxZ + $$2);
    }

    public AABB move(BlockPos $$0) {
        return new AABB(this.minX + (double)$$0.getX(), this.minY + (double)$$0.getY(), this.minZ + (double)$$0.getZ(), this.maxX + (double)$$0.getX(), this.maxY + (double)$$0.getY(), this.maxZ + (double)$$0.getZ());
    }

    public AABB move(Vec3 $$0) {
        return this.move($$0.x, $$0.y, $$0.z);
    }

    public boolean intersects(AABB $$0) {
        return this.intersects($$0.minX, $$0.minY, $$0.minZ, $$0.maxX, $$0.maxY, $$0.maxZ);
    }

    public boolean intersects(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        return this.minX < $$3 && this.maxX > $$0 && this.minY < $$4 && this.maxY > $$1 && this.minZ < $$5 && this.maxZ > $$2;
    }

    public boolean intersects(Vec3 $$0, Vec3 $$1) {
        return this.intersects(Math.min((double)$$0.x, (double)$$1.x), Math.min((double)$$0.y, (double)$$1.y), Math.min((double)$$0.z, (double)$$1.z), Math.max((double)$$0.x, (double)$$1.x), Math.max((double)$$0.y, (double)$$1.y), Math.max((double)$$0.z, (double)$$1.z));
    }

    public boolean contains(Vec3 $$0) {
        return this.contains($$0.x, $$0.y, $$0.z);
    }

    public boolean contains(double $$0, double $$1, double $$2) {
        return $$0 >= this.minX && $$0 < this.maxX && $$1 >= this.minY && $$1 < this.maxY && $$2 >= this.minZ && $$2 < this.maxZ;
    }

    public double getSize() {
        double $$0 = this.getXsize();
        double $$1 = this.getYsize();
        double $$2 = this.getZsize();
        return ($$0 + $$1 + $$2) / 3.0;
    }

    public double getXsize() {
        return this.maxX - this.minX;
    }

    public double getYsize() {
        return this.maxY - this.minY;
    }

    public double getZsize() {
        return this.maxZ - this.minZ;
    }

    public AABB deflate(double $$0, double $$1, double $$2) {
        return this.inflate(-$$0, -$$1, -$$2);
    }

    public AABB deflate(double $$0) {
        return this.inflate(-$$0);
    }

    public Optional<Vec3> clip(Vec3 $$0, Vec3 $$1) {
        double[] $$2 = new double[]{1.0};
        double $$3 = $$1.x - $$0.x;
        double $$4 = $$1.y - $$0.y;
        double $$5 = $$1.z - $$0.z;
        Direction $$6 = AABB.getDirection(this, $$0, $$2, null, $$3, $$4, $$5);
        if ($$6 == null) {
            return Optional.empty();
        }
        double $$7 = $$2[0];
        return Optional.of((Object)$$0.add($$7 * $$3, $$7 * $$4, $$7 * $$5));
    }

    @Nullable
    public static BlockHitResult clip(Iterable<AABB> $$0, Vec3 $$1, Vec3 $$2, BlockPos $$3) {
        double[] $$4 = new double[]{1.0};
        Direction $$5 = null;
        double $$6 = $$2.x - $$1.x;
        double $$7 = $$2.y - $$1.y;
        double $$8 = $$2.z - $$1.z;
        for (AABB $$9 : $$0) {
            $$5 = AABB.getDirection($$9.move($$3), $$1, $$4, $$5, $$6, $$7, $$8);
        }
        if ($$5 == null) {
            return null;
        }
        double $$10 = $$4[0];
        return new BlockHitResult($$1.add($$10 * $$6, $$10 * $$7, $$10 * $$8), $$5, $$3, false);
    }

    @Nullable
    private static Direction getDirection(AABB $$0, Vec3 $$1, double[] $$2, @Nullable Direction $$3, double $$4, double $$5, double $$6) {
        if ($$4 > 1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$4, $$5, $$6, $$0.minX, $$0.minY, $$0.maxY, $$0.minZ, $$0.maxZ, Direction.WEST, $$1.x, $$1.y, $$1.z);
        } else if ($$4 < -1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$4, $$5, $$6, $$0.maxX, $$0.minY, $$0.maxY, $$0.minZ, $$0.maxZ, Direction.EAST, $$1.x, $$1.y, $$1.z);
        }
        if ($$5 > 1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$5, $$6, $$4, $$0.minY, $$0.minZ, $$0.maxZ, $$0.minX, $$0.maxX, Direction.DOWN, $$1.y, $$1.z, $$1.x);
        } else if ($$5 < -1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$5, $$6, $$4, $$0.maxY, $$0.minZ, $$0.maxZ, $$0.minX, $$0.maxX, Direction.UP, $$1.y, $$1.z, $$1.x);
        }
        if ($$6 > 1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$6, $$4, $$5, $$0.minZ, $$0.minX, $$0.maxX, $$0.minY, $$0.maxY, Direction.NORTH, $$1.z, $$1.x, $$1.y);
        } else if ($$6 < -1.0E-7) {
            $$3 = AABB.clipPoint($$2, $$3, $$6, $$4, $$5, $$0.maxZ, $$0.minX, $$0.maxX, $$0.minY, $$0.maxY, Direction.SOUTH, $$1.z, $$1.x, $$1.y);
        }
        return $$3;
    }

    @Nullable
    private static Direction clipPoint(double[] $$0, @Nullable Direction $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8, double $$9, Direction $$10, double $$11, double $$12, double $$13) {
        double $$14 = ($$5 - $$11) / $$2;
        double $$15 = $$12 + $$14 * $$3;
        double $$16 = $$13 + $$14 * $$4;
        if (0.0 < $$14 && $$14 < $$0[0] && $$6 - 1.0E-7 < $$15 && $$15 < $$7 + 1.0E-7 && $$8 - 1.0E-7 < $$16 && $$16 < $$9 + 1.0E-7) {
            $$0[0] = $$14;
            return $$10;
        }
        return $$1;
    }

    public String toString() {
        return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean hasNaN() {
        return Double.isNaN((double)this.minX) || Double.isNaN((double)this.minY) || Double.isNaN((double)this.minZ) || Double.isNaN((double)this.maxX) || Double.isNaN((double)this.maxY) || Double.isNaN((double)this.maxZ);
    }

    public Vec3 getCenter() {
        return new Vec3(Mth.lerp(0.5, this.minX, this.maxX), Mth.lerp(0.5, this.minY, this.maxY), Mth.lerp(0.5, this.minZ, this.maxZ));
    }

    public static AABB ofSize(Vec3 $$0, double $$1, double $$2, double $$3) {
        return new AABB($$0.x - $$1 / 2.0, $$0.y - $$2 / 2.0, $$0.z - $$3 / 2.0, $$0.x + $$1 / 2.0, $$0.y + $$2 / 2.0, $$0.z + $$3 / 2.0);
    }
}