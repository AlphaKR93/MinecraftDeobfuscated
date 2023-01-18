/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.stream.IntStream
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.slf4j.Logger;

public class BoundingBox {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<BoundingBox> CODEC = Codec.INT_STREAM.comapFlatMap($$02 -> Util.fixedSize($$02, 6).map($$0 -> new BoundingBox($$0[0], $$0[1], $$0[2], $$0[3], $$0[4], $$0[5])), $$0 -> IntStream.of((int[])new int[]{$$0.minX, $$0.minY, $$0.minZ, $$0.maxX, $$0.maxY, $$0.maxZ})).stable();
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public BoundingBox(BlockPos $$0) {
        this($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getX(), $$0.getY(), $$0.getZ());
    }

    public BoundingBox(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        this.minX = $$0;
        this.minY = $$1;
        this.minZ = $$2;
        this.maxX = $$3;
        this.maxY = $$4;
        this.maxZ = $$5;
        if ($$3 < $$0 || $$4 < $$1 || $$5 < $$2) {
            String $$6 = "Invalid bounding box data, inverted bounds for: " + this;
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw new IllegalStateException($$6);
            }
            LOGGER.error($$6);
            this.minX = Math.min((int)$$0, (int)$$3);
            this.minY = Math.min((int)$$1, (int)$$4);
            this.minZ = Math.min((int)$$2, (int)$$5);
            this.maxX = Math.max((int)$$0, (int)$$3);
            this.maxY = Math.max((int)$$1, (int)$$4);
            this.maxZ = Math.max((int)$$2, (int)$$5);
        }
    }

    public static BoundingBox fromCorners(Vec3i $$0, Vec3i $$1) {
        return new BoundingBox(Math.min((int)$$0.getX(), (int)$$1.getX()), Math.min((int)$$0.getY(), (int)$$1.getY()), Math.min((int)$$0.getZ(), (int)$$1.getZ()), Math.max((int)$$0.getX(), (int)$$1.getX()), Math.max((int)$$0.getY(), (int)$$1.getY()), Math.max((int)$$0.getZ(), (int)$$1.getZ()));
    }

    public static BoundingBox infinite() {
        return new BoundingBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static BoundingBox orientBox(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, Direction $$9) {
        switch ($$9) {
            default: {
                return new BoundingBox($$0 + $$3, $$1 + $$4, $$2 + $$5, $$0 + $$6 - 1 + $$3, $$1 + $$7 - 1 + $$4, $$2 + $$8 - 1 + $$5);
            }
            case NORTH: {
                return new BoundingBox($$0 + $$3, $$1 + $$4, $$2 - $$8 + 1 + $$5, $$0 + $$6 - 1 + $$3, $$1 + $$7 - 1 + $$4, $$2 + $$5);
            }
            case WEST: {
                return new BoundingBox($$0 - $$8 + 1 + $$5, $$1 + $$4, $$2 + $$3, $$0 + $$5, $$1 + $$7 - 1 + $$4, $$2 + $$6 - 1 + $$3);
            }
            case EAST: 
        }
        return new BoundingBox($$0 + $$5, $$1 + $$4, $$2 + $$3, $$0 + $$8 - 1 + $$5, $$1 + $$7 - 1 + $$4, $$2 + $$6 - 1 + $$3);
    }

    public boolean intersects(BoundingBox $$0) {
        return this.maxX >= $$0.minX && this.minX <= $$0.maxX && this.maxZ >= $$0.minZ && this.minZ <= $$0.maxZ && this.maxY >= $$0.minY && this.minY <= $$0.maxY;
    }

    public boolean intersects(int $$0, int $$1, int $$2, int $$3) {
        return this.maxX >= $$0 && this.minX <= $$2 && this.maxZ >= $$1 && this.minZ <= $$3;
    }

    public static Optional<BoundingBox> encapsulatingPositions(Iterable<BlockPos> $$0) {
        Iterator $$1 = $$0.iterator();
        if (!$$1.hasNext()) {
            return Optional.empty();
        }
        BoundingBox $$2 = new BoundingBox((BlockPos)$$1.next());
        $$1.forEachRemaining($$2::encapsulate);
        return Optional.of((Object)$$2);
    }

    public static Optional<BoundingBox> encapsulatingBoxes(Iterable<BoundingBox> $$0) {
        Iterator $$1 = $$0.iterator();
        if (!$$1.hasNext()) {
            return Optional.empty();
        }
        BoundingBox $$2 = (BoundingBox)$$1.next();
        BoundingBox $$3 = new BoundingBox($$2.minX, $$2.minY, $$2.minZ, $$2.maxX, $$2.maxY, $$2.maxZ);
        $$1.forEachRemaining($$3::encapsulate);
        return Optional.of((Object)$$3);
    }

    @Deprecated
    public BoundingBox encapsulate(BoundingBox $$0) {
        this.minX = Math.min((int)this.minX, (int)$$0.minX);
        this.minY = Math.min((int)this.minY, (int)$$0.minY);
        this.minZ = Math.min((int)this.minZ, (int)$$0.minZ);
        this.maxX = Math.max((int)this.maxX, (int)$$0.maxX);
        this.maxY = Math.max((int)this.maxY, (int)$$0.maxY);
        this.maxZ = Math.max((int)this.maxZ, (int)$$0.maxZ);
        return this;
    }

    @Deprecated
    public BoundingBox encapsulate(BlockPos $$0) {
        this.minX = Math.min((int)this.minX, (int)$$0.getX());
        this.minY = Math.min((int)this.minY, (int)$$0.getY());
        this.minZ = Math.min((int)this.minZ, (int)$$0.getZ());
        this.maxX = Math.max((int)this.maxX, (int)$$0.getX());
        this.maxY = Math.max((int)this.maxY, (int)$$0.getY());
        this.maxZ = Math.max((int)this.maxZ, (int)$$0.getZ());
        return this;
    }

    @Deprecated
    public BoundingBox move(int $$0, int $$1, int $$2) {
        this.minX += $$0;
        this.minY += $$1;
        this.minZ += $$2;
        this.maxX += $$0;
        this.maxY += $$1;
        this.maxZ += $$2;
        return this;
    }

    @Deprecated
    public BoundingBox move(Vec3i $$0) {
        return this.move($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public BoundingBox moved(int $$0, int $$1, int $$2) {
        return new BoundingBox(this.minX + $$0, this.minY + $$1, this.minZ + $$2, this.maxX + $$0, this.maxY + $$1, this.maxZ + $$2);
    }

    public BoundingBox inflatedBy(int $$0) {
        return new BoundingBox(this.minX() - $$0, this.minY() - $$0, this.minZ() - $$0, this.maxX() + $$0, this.maxY() + $$0, this.maxZ() + $$0);
    }

    public boolean isInside(Vec3i $$0) {
        return this.isInside($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public boolean isInside(int $$0, int $$1, int $$2) {
        return $$0 >= this.minX && $$0 <= this.maxX && $$2 >= this.minZ && $$2 <= this.maxZ && $$1 >= this.minY && $$1 <= this.maxY;
    }

    public Vec3i getLength() {
        return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
    }

    public int getXSpan() {
        return this.maxX - this.minX + 1;
    }

    public int getYSpan() {
        return this.maxY - this.minY + 1;
    }

    public int getZSpan() {
        return this.maxZ - this.minZ + 1;
    }

    public BlockPos getCenter() {
        return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
    }

    public void forAllCorners(Consumer<BlockPos> $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        $$0.accept((Object)$$1.set(this.maxX, this.maxY, this.maxZ));
        $$0.accept((Object)$$1.set(this.minX, this.maxY, this.maxZ));
        $$0.accept((Object)$$1.set(this.maxX, this.minY, this.maxZ));
        $$0.accept((Object)$$1.set(this.minX, this.minY, this.maxZ));
        $$0.accept((Object)$$1.set(this.maxX, this.maxY, this.minZ));
        $$0.accept((Object)$$1.set(this.minX, this.maxY, this.minZ));
        $$0.accept((Object)$$1.set(this.maxX, this.minY, this.minZ));
        $$0.accept((Object)$$1.set(this.minX, this.minY, this.minZ));
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("minX", this.minX).add("minY", this.minY).add("minZ", this.minZ).add("maxX", this.maxX).add("maxY", this.maxY).add("maxZ", this.maxZ).toString();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof BoundingBox) {
            BoundingBox $$1 = (BoundingBox)$$0;
            return this.minX == $$1.minX && this.minY == $$1.minY && this.minZ == $$1.minZ && this.maxX == $$1.maxX && this.maxY == $$1.maxY && this.maxZ == $$1.maxZ;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }

    public int minX() {
        return this.minX;
    }

    public int minY() {
        return this.minY;
    }

    public int minZ() {
        return this.minZ;
    }

    public int maxX() {
        return this.maxX;
    }

    public int maxY() {
        return this.maxY;
    }

    public int maxZ() {
        return this.maxZ;
    }
}