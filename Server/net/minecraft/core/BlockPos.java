/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.Spliterator
 *  java.util.function.Predicate
 *  java.util.stream.IntStream
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

@Immutable
public class BlockPos
extends Vec3i {
    public static final Codec<BlockPos> CODEC = Codec.INT_STREAM.comapFlatMap($$02 -> Util.fixedSize($$02, 3).map($$0 -> new BlockPos($$0[0], $$0[1], $$0[2])), $$0 -> IntStream.of((int[])new int[]{$$0.getX(), $$0.getY(), $$0.getZ()})).stable();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);
    private static final int PACKED_X_LENGTH;
    private static final int PACKED_Z_LENGTH;
    public static final int PACKED_Y_LENGTH;
    private static final long PACKED_X_MASK;
    private static final long PACKED_Y_MASK;
    private static final long PACKED_Z_MASK;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET;
    private static final int X_OFFSET;

    public BlockPos(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    public BlockPos(double $$0, double $$1, double $$2) {
        super($$0, $$1, $$2);
    }

    public BlockPos(Vec3 $$0) {
        this($$0.x, $$0.y, $$0.z);
    }

    public BlockPos(Position $$0) {
        this($$0.x(), $$0.y(), $$0.z());
    }

    public BlockPos(Vec3i $$0) {
        this($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public static long offset(long $$0, Direction $$1) {
        return BlockPos.offset($$0, $$1.getStepX(), $$1.getStepY(), $$1.getStepZ());
    }

    public static long offset(long $$0, int $$1, int $$2, int $$3) {
        return BlockPos.asLong(BlockPos.getX($$0) + $$1, BlockPos.getY($$0) + $$2, BlockPos.getZ($$0) + $$3);
    }

    public static int getX(long $$0) {
        return (int)($$0 << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    public static int getY(long $$0) {
        return (int)($$0 << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    public static int getZ(long $$0) {
        return (int)($$0 << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public static BlockPos of(long $$0) {
        return new BlockPos(BlockPos.getX($$0), BlockPos.getY($$0), BlockPos.getZ($$0));
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int $$0, int $$1, int $$2) {
        long $$3 = 0L;
        $$3 |= ((long)$$0 & PACKED_X_MASK) << X_OFFSET;
        $$3 |= ((long)$$1 & PACKED_Y_MASK) << 0;
        return $$3 |= ((long)$$2 & PACKED_Z_MASK) << Z_OFFSET;
    }

    public static long getFlatIndex(long $$0) {
        return $$0 & 0xFFFFFFFFFFFFFFF0L;
    }

    @Override
    public BlockPos offset(double $$0, double $$1, double $$2) {
        if ($$0 == 0.0 && $$1 == 0.0 && $$2 == 0.0) {
            return this;
        }
        return new BlockPos((double)this.getX() + $$0, (double)this.getY() + $$1, (double)this.getZ() + $$2);
    }

    @Override
    public BlockPos offset(int $$0, int $$1, int $$2) {
        if ($$0 == 0 && $$1 == 0 && $$2 == 0) {
            return this;
        }
        return new BlockPos(this.getX() + $$0, this.getY() + $$1, this.getZ() + $$2);
    }

    public Vec3 getCenter() {
        return Vec3.atCenterOf(this);
    }

    public BlockPos f(Vec3i $$0) {
        return this.offset($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public BlockPos e(Vec3i $$0) {
        return this.offset(-$$0.getX(), -$$0.getY(), -$$0.getZ());
    }

    @Override
    public BlockPos multiply(int $$0) {
        if ($$0 == 1) {
            return this;
        }
        if ($$0 == 0) {
            return ZERO;
        }
        return new BlockPos(this.getX() * $$0, this.getY() * $$0, this.getZ() * $$0);
    }

    public BlockPos p() {
        return this.relative(Direction.UP);
    }

    public BlockPos n(int $$0) {
        return this.relative(Direction.UP, $$0);
    }

    public BlockPos o() {
        return this.relative(Direction.DOWN);
    }

    public BlockPos m(int $$0) {
        return this.relative(Direction.DOWN, $$0);
    }

    public BlockPos n() {
        return this.relative(Direction.NORTH);
    }

    public BlockPos l(int $$0) {
        return this.relative(Direction.NORTH, $$0);
    }

    public BlockPos m() {
        return this.relative(Direction.SOUTH);
    }

    public BlockPos k(int $$0) {
        return this.relative(Direction.SOUTH, $$0);
    }

    public BlockPos l() {
        return this.relative(Direction.WEST);
    }

    public BlockPos j(int $$0) {
        return this.relative(Direction.WEST, $$0);
    }

    public BlockPos k() {
        return this.relative(Direction.EAST);
    }

    public BlockPos i(int $$0) {
        return this.relative(Direction.EAST, $$0);
    }

    public BlockPos b(Direction $$0) {
        return new BlockPos(this.getX() + $$0.getStepX(), this.getY() + $$0.getStepY(), this.getZ() + $$0.getStepZ());
    }

    @Override
    public BlockPos relative(Direction $$0, int $$1) {
        if ($$1 == 0) {
            return this;
        }
        return new BlockPos(this.getX() + $$0.getStepX() * $$1, this.getY() + $$0.getStepY() * $$1, this.getZ() + $$0.getStepZ() * $$1);
    }

    @Override
    public BlockPos relative(Direction.Axis $$0, int $$1) {
        if ($$1 == 0) {
            return this;
        }
        int $$2 = $$0 == Direction.Axis.X ? $$1 : 0;
        int $$3 = $$0 == Direction.Axis.Y ? $$1 : 0;
        int $$4 = $$0 == Direction.Axis.Z ? $$1 : 0;
        return new BlockPos(this.getX() + $$2, this.getY() + $$3, this.getZ() + $$4);
    }

    public BlockPos rotate(Rotation $$0) {
        switch ($$0) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: 
        }
        return new BlockPos(this.getZ(), this.getY(), -this.getX());
    }

    public BlockPos d(Vec3i $$0) {
        return new BlockPos(this.getY() * $$0.getZ() - this.getZ() * $$0.getY(), this.getZ() * $$0.getX() - this.getX() * $$0.getZ(), this.getX() * $$0.getY() - this.getY() * $$0.getX());
    }

    public BlockPos atY(int $$0) {
        return new BlockPos(this.getX(), $$0, this.getZ());
    }

    public BlockPos immutable() {
        return this;
    }

    public MutableBlockPos mutable() {
        return new MutableBlockPos(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPos> randomInCube(RandomSource $$0, int $$1, BlockPos $$2, int $$3) {
        return BlockPos.randomBetweenClosed($$0, $$1, $$2.getX() - $$3, $$2.getY() - $$3, $$2.getZ() - $$3, $$2.getX() + $$3, $$2.getY() + $$3, $$2.getZ() + $$3);
    }

    public static Iterable<BlockPos> randomBetweenClosed(final RandomSource $$0, final int $$1, final int $$2, final int $$3, final int $$4, int $$5, int $$6, int $$7) {
        final int $$8 = $$5 - $$2 + 1;
        final int $$9 = $$6 - $$3 + 1;
        final int $$10 = $$7 - $$4 + 1;
        return () -> new AbstractIterator<BlockPos>(){
            final MutableBlockPos nextPos = new MutableBlockPos();
            int counter = $$1;

            protected BlockPos computeNext() {
                if (this.counter <= 0) {
                    return (BlockPos)this.endOfData();
                }
                MutableBlockPos $$02 = this.nextPos.set($$2 + $$0.nextInt($$8), $$3 + $$0.nextInt($$9), $$4 + $$0.nextInt($$10));
                --this.counter;
                return $$02;
            }
        };
    }

    public static Iterable<BlockPos> withinManhattan(BlockPos $$0, final int $$1, final int $$2, final int $$3) {
        final int $$4 = $$1 + $$2 + $$3;
        final int $$5 = $$0.getX();
        final int $$6 = $$0.getY();
        final int $$7 = $$0.getZ();
        return () -> new AbstractIterator<BlockPos>(){
            private final MutableBlockPos cursor = new MutableBlockPos();
            private int currentDepth;
            private int maxX;
            private int maxY;
            private int x;
            private int y;
            private boolean zMirror;

            protected BlockPos computeNext() {
                if (this.zMirror) {
                    this.zMirror = false;
                    this.cursor.setZ($$7 - (this.cursor.getZ() - $$7));
                    return this.cursor;
                }
                MutableBlockPos $$0 = null;
                while ($$0 == null) {
                    if (this.y > this.maxY) {
                        ++this.x;
                        if (this.x > this.maxX) {
                            ++this.currentDepth;
                            if (this.currentDepth > $$4) {
                                return (BlockPos)this.endOfData();
                            }
                            this.maxX = Math.min((int)$$1, (int)this.currentDepth);
                            this.x = -this.maxX;
                        }
                        this.maxY = Math.min((int)$$2, (int)(this.currentDepth - Math.abs((int)this.x)));
                        this.y = -this.maxY;
                    }
                    int $$12 = this.x;
                    int $$22 = this.y;
                    int $$32 = this.currentDepth - Math.abs((int)$$12) - Math.abs((int)$$22);
                    if ($$32 <= $$3) {
                        this.zMirror = $$32 != 0;
                        $$0 = this.cursor.set($$5 + $$12, $$6 + $$22, $$7 + $$32);
                    }
                    ++this.y;
                }
                return $$0;
            }
        };
    }

    public static Optional<BlockPos> findClosestMatch(BlockPos $$0, int $$1, int $$2, Predicate<BlockPos> $$3) {
        for (BlockPos $$4 : BlockPos.withinManhattan($$0, $$1, $$2, $$1)) {
            if (!$$3.test((Object)$$4)) continue;
            return Optional.of((Object)$$4);
        }
        return Optional.empty();
    }

    public static Stream<BlockPos> withinManhattanStream(BlockPos $$0, int $$1, int $$2, int $$3) {
        return StreamSupport.stream((Spliterator)BlockPos.withinManhattan($$0, $$1, $$2, $$3).spliterator(), (boolean)false);
    }

    public static Iterable<BlockPos> betweenClosed(BlockPos $$0, BlockPos $$1) {
        return BlockPos.betweenClosed(Math.min((int)$$0.getX(), (int)$$1.getX()), Math.min((int)$$0.getY(), (int)$$1.getY()), Math.min((int)$$0.getZ(), (int)$$1.getZ()), Math.max((int)$$0.getX(), (int)$$1.getX()), Math.max((int)$$0.getY(), (int)$$1.getY()), Math.max((int)$$0.getZ(), (int)$$1.getZ()));
    }

    public static Stream<BlockPos> betweenClosedStream(BlockPos $$0, BlockPos $$1) {
        return StreamSupport.stream((Spliterator)BlockPos.betweenClosed($$0, $$1).spliterator(), (boolean)false);
    }

    public static Stream<BlockPos> betweenClosedStream(BoundingBox $$0) {
        return BlockPos.betweenClosedStream(Math.min((int)$$0.minX(), (int)$$0.maxX()), Math.min((int)$$0.minY(), (int)$$0.maxY()), Math.min((int)$$0.minZ(), (int)$$0.maxZ()), Math.max((int)$$0.minX(), (int)$$0.maxX()), Math.max((int)$$0.minY(), (int)$$0.maxY()), Math.max((int)$$0.minZ(), (int)$$0.maxZ()));
    }

    public static Stream<BlockPos> betweenClosedStream(AABB $$0) {
        return BlockPos.betweenClosedStream(Mth.floor($$0.minX), Mth.floor($$0.minY), Mth.floor($$0.minZ), Mth.floor($$0.maxX), Mth.floor($$0.maxY), Mth.floor($$0.maxZ));
    }

    public static Stream<BlockPos> betweenClosedStream(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        return StreamSupport.stream((Spliterator)BlockPos.betweenClosed($$0, $$1, $$2, $$3, $$4, $$5).spliterator(), (boolean)false);
    }

    public static Iterable<BlockPos> betweenClosed(final int $$0, final int $$1, final int $$2, int $$3, int $$4, int $$5) {
        final int $$6 = $$3 - $$0 + 1;
        final int $$7 = $$4 - $$1 + 1;
        int $$8 = $$5 - $$2 + 1;
        final int $$9 = $$6 * $$7 * $$8;
        return () -> new AbstractIterator<BlockPos>(){
            private final MutableBlockPos cursor = new MutableBlockPos();
            private int index;

            protected BlockPos computeNext() {
                if (this.index == $$9) {
                    return (BlockPos)this.endOfData();
                }
                int $$02 = this.index % $$6;
                int $$12 = this.index / $$6;
                int $$22 = $$12 % $$7;
                int $$3 = $$12 / $$7;
                ++this.index;
                return this.cursor.set($$0 + $$02, $$1 + $$22, $$2 + $$3);
            }
        };
    }

    public static Iterable<MutableBlockPos> spiralAround(final BlockPos $$0, final int $$1, final Direction $$2, final Direction $$3) {
        Validate.validState(($$2.getAxis() != $$3.getAxis() ? 1 : 0) != 0, (String)"The two directions cannot be on the same axis", (Object[])new Object[0]);
        return () -> new AbstractIterator<MutableBlockPos>(){
            private final Direction[] directions;
            private final MutableBlockPos cursor;
            private final int legs;
            private int leg;
            private int legSize;
            private int legIndex;
            private int lastX;
            private int lastY;
            private int lastZ;
            {
                this.directions = new Direction[]{$$2, $$3, $$2.getOpposite(), $$3.getOpposite()};
                this.cursor = $$0.mutable().move($$3);
                this.legs = 4 * $$1;
                this.leg = -1;
                this.lastX = this.cursor.getX();
                this.lastY = this.cursor.getY();
                this.lastZ = this.cursor.getZ();
            }

            protected MutableBlockPos computeNext() {
                this.cursor.set(this.lastX, this.lastY, this.lastZ).move(this.directions[(this.leg + 4) % 4]);
                this.lastX = this.cursor.getX();
                this.lastY = this.cursor.getY();
                this.lastZ = this.cursor.getZ();
                if (this.legIndex >= this.legSize) {
                    if (this.leg >= this.legs) {
                        return (MutableBlockPos)this.endOfData();
                    }
                    ++this.leg;
                    this.legIndex = 0;
                    this.legSize = this.leg / 2 + 1;
                }
                ++this.legIndex;
                return this.cursor;
            }
        };
    }

    static {
        PACKED_Z_LENGTH = PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
        PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
        PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
        PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
        PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
        Z_OFFSET = PACKED_Y_LENGTH;
        X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;
    }

    public static class MutableBlockPos
    extends BlockPos {
        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(int $$0, int $$1, int $$2) {
            super($$0, $$1, $$2);
        }

        public MutableBlockPos(double $$0, double $$1, double $$2) {
            this(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2));
        }

        @Override
        public BlockPos offset(double $$0, double $$1, double $$2) {
            return super.offset($$0, $$1, $$2).immutable();
        }

        @Override
        public BlockPos offset(int $$0, int $$1, int $$2) {
            return super.offset($$0, $$1, $$2).immutable();
        }

        @Override
        public BlockPos multiply(int $$0) {
            return super.multiply($$0).immutable();
        }

        @Override
        public BlockPos relative(Direction $$0, int $$1) {
            return super.relative($$0, $$1).immutable();
        }

        @Override
        public BlockPos relative(Direction.Axis $$0, int $$1) {
            return super.relative($$0, $$1).immutable();
        }

        @Override
        public BlockPos rotate(Rotation $$0) {
            return super.rotate($$0).immutable();
        }

        public MutableBlockPos set(int $$0, int $$1, int $$2) {
            this.setX($$0);
            this.setY($$1);
            this.setZ($$2);
            return this;
        }

        public MutableBlockPos set(double $$0, double $$1, double $$2) {
            return this.set(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2));
        }

        public MutableBlockPos set(Vec3i $$0) {
            return this.set($$0.getX(), $$0.getY(), $$0.getZ());
        }

        public MutableBlockPos set(long $$0) {
            return this.set(MutableBlockPos.getX($$0), MutableBlockPos.getY($$0), MutableBlockPos.getZ($$0));
        }

        public MutableBlockPos set(AxisCycle $$0, int $$1, int $$2, int $$3) {
            return this.set($$0.cycle($$1, $$2, $$3, Direction.Axis.X), $$0.cycle($$1, $$2, $$3, Direction.Axis.Y), $$0.cycle($$1, $$2, $$3, Direction.Axis.Z));
        }

        public MutableBlockPos setWithOffset(Vec3i $$0, Direction $$1) {
            return this.set($$0.getX() + $$1.getStepX(), $$0.getY() + $$1.getStepY(), $$0.getZ() + $$1.getStepZ());
        }

        public MutableBlockPos setWithOffset(Vec3i $$0, int $$1, int $$2, int $$3) {
            return this.set($$0.getX() + $$1, $$0.getY() + $$2, $$0.getZ() + $$3);
        }

        public MutableBlockPos setWithOffset(Vec3i $$0, Vec3i $$1) {
            return this.set($$0.getX() + $$1.getX(), $$0.getY() + $$1.getY(), $$0.getZ() + $$1.getZ());
        }

        public MutableBlockPos move(Direction $$0) {
            return this.move($$0, 1);
        }

        public MutableBlockPos move(Direction $$0, int $$1) {
            return this.set(this.getX() + $$0.getStepX() * $$1, this.getY() + $$0.getStepY() * $$1, this.getZ() + $$0.getStepZ() * $$1);
        }

        public MutableBlockPos move(int $$0, int $$1, int $$2) {
            return this.set(this.getX() + $$0, this.getY() + $$1, this.getZ() + $$2);
        }

        public MutableBlockPos move(Vec3i $$0) {
            return this.set(this.getX() + $$0.getX(), this.getY() + $$0.getY(), this.getZ() + $$0.getZ());
        }

        public MutableBlockPos clamp(Direction.Axis $$0, int $$1, int $$2) {
            switch ($$0) {
                case X: {
                    return this.set(Mth.clamp(this.getX(), $$1, $$2), this.getY(), this.getZ());
                }
                case Y: {
                    return this.set(this.getX(), Mth.clamp(this.getY(), $$1, $$2), this.getZ());
                }
                case Z: {
                    return this.set(this.getX(), this.getY(), Mth.clamp(this.getZ(), $$1, $$2));
                }
            }
            throw new IllegalStateException("Unable to clamp axis " + $$0);
        }

        @Override
        public MutableBlockPos setX(int $$0) {
            super.setX($$0);
            return this;
        }

        @Override
        public MutableBlockPos setY(int $$0) {
            super.setY($$0);
            return this;
        }

        @Override
        public MutableBlockPos setZ(int $$0) {
            super.setZ($$0);
            return this;
        }

        @Override
        public BlockPos immutable() {
            return new BlockPos(this);
        }
    }
}