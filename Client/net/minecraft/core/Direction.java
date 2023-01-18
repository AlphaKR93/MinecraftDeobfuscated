/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum Direction implements StringRepresentable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    public static final StringRepresentable.EnumCodec<Direction> CODEC;
    public static final Codec<Direction> VERTICAL_CODEC;
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vec3i normal;
    private static final Direction[] VALUES;
    private static final Direction[] BY_3D_DATA;
    private static final Direction[] BY_2D_DATA;
    private static final Long2ObjectMap<Direction> BY_NORMAL;

    private Direction(int $$0, int $$1, int $$2, String $$3, AxisDirection $$4, Axis $$5, Vec3i $$6) {
        this.data3d = $$0;
        this.data2d = $$2;
        this.oppositeIndex = $$1;
        this.name = $$3;
        this.axis = $$5;
        this.axisDirection = $$4;
        this.normal = $$6;
    }

    public static Direction[] orderedByNearest(Entity $$0) {
        Direction $$17;
        float $$1 = $$0.getViewXRot(1.0f) * ((float)Math.PI / 180);
        float $$2 = -$$0.getViewYRot(1.0f) * ((float)Math.PI / 180);
        float $$3 = Mth.sin($$1);
        float $$4 = Mth.cos($$1);
        float $$5 = Mth.sin($$2);
        float $$6 = Mth.cos($$2);
        boolean $$7 = $$5 > 0.0f;
        boolean $$8 = $$3 < 0.0f;
        boolean $$9 = $$6 > 0.0f;
        float $$10 = $$7 ? $$5 : -$$5;
        float $$11 = $$8 ? -$$3 : $$3;
        float $$12 = $$9 ? $$6 : -$$6;
        float $$13 = $$10 * $$4;
        float $$14 = $$12 * $$4;
        Direction $$15 = $$7 ? EAST : WEST;
        Direction $$16 = $$8 ? UP : DOWN;
        Direction direction = $$17 = $$9 ? SOUTH : NORTH;
        if ($$10 > $$12) {
            if ($$11 > $$13) {
                return Direction.makeDirectionArray($$16, $$15, $$17);
            }
            if ($$14 > $$11) {
                return Direction.makeDirectionArray($$15, $$17, $$16);
            }
            return Direction.makeDirectionArray($$15, $$16, $$17);
        }
        if ($$11 > $$14) {
            return Direction.makeDirectionArray($$16, $$17, $$15);
        }
        if ($$13 > $$11) {
            return Direction.makeDirectionArray($$17, $$15, $$16);
        }
        return Direction.makeDirectionArray($$17, $$16, $$15);
    }

    private static Direction[] makeDirectionArray(Direction $$0, Direction $$1, Direction $$2) {
        return new Direction[]{$$0, $$1, $$2, $$2.getOpposite(), $$1.getOpposite(), $$0.getOpposite()};
    }

    public static Direction rotate(Matrix4f $$0, Direction $$1) {
        Vec3i $$2 = $$1.getNormal();
        Vector4f $$3 = $$0.transform(new Vector4f((float)$$2.getX(), (float)$$2.getY(), (float)$$2.getZ(), 0.0f));
        return Direction.getNearest($$3.x(), $$3.y(), $$3.z());
    }

    public static Collection<Direction> allShuffled(RandomSource $$0) {
        return Util.shuffledCopy(Direction.values(), $$0);
    }

    public static Stream<Direction> stream() {
        return Stream.of((Object[])VALUES);
    }

    public Quaternionf getRotation() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case DOWN -> new Quaternionf().rotationX((float)Math.PI);
            case UP -> new Quaternionf();
            case NORTH -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, (float)Math.PI);
            case SOUTH -> new Quaternionf().rotationX(1.5707964f);
            case WEST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, 1.5707964f);
            case EAST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, -1.5707964f);
        };
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public static Direction getFacingAxis(Entity $$0, Axis $$1) {
        return switch ($$1) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (EAST.isFacingAngle($$0.getViewYRot(1.0f))) {
                    yield EAST;
                }
                yield WEST;
            }
            case Axis.Z -> {
                if (SOUTH.isFacingAngle($$0.getViewYRot(1.0f))) {
                    yield SOUTH;
                }
                yield NORTH;
            }
            case Axis.Y -> $$0.getViewXRot(1.0f) < 0.0f ? UP : DOWN;
        };
    }

    public Direction getOpposite() {
        return Direction.from3DDataValue(this.oppositeIndex);
    }

    public Direction getClockWise(Axis $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield this.getClockWiseX();
            }
            case Axis.Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield this.getClockWise();
            }
            case Axis.Z -> this == NORTH || this == SOUTH ? this : this.getClockWiseZ();
        };
    }

    public Direction getCounterClockWise(Axis $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield this.getCounterClockWiseX();
            }
            case Axis.Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield this.getCounterClockWise();
            }
            case Axis.Z -> this == NORTH || this == SOUTH ? this : this.getCounterClockWiseZ();
        };
    }

    public Direction getClockWise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    private Direction getClockWiseX() {
        return switch (this) {
            case UP -> NORTH;
            case NORTH -> DOWN;
            case DOWN -> SOUTH;
            case SOUTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getCounterClockWiseX() {
        return switch (this) {
            case UP -> SOUTH;
            case SOUTH -> DOWN;
            case DOWN -> NORTH;
            case NORTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getClockWiseZ() {
        return switch (this) {
            case UP -> EAST;
            case EAST -> DOWN;
            case DOWN -> WEST;
            case WEST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    private Direction getCounterClockWiseZ() {
        return switch (this) {
            case UP -> WEST;
            case WEST -> DOWN;
            case DOWN -> EAST;
            case EAST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    public Direction getCounterClockWise() {
        return switch (this) {
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3f step() {
        return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static Direction byName(@Nullable String $$0) {
        return CODEC.byName($$0);
    }

    public static Direction from3DDataValue(int $$0) {
        return BY_3D_DATA[Mth.abs($$0 % BY_3D_DATA.length)];
    }

    public static Direction from2DDataValue(int $$0) {
        return BY_2D_DATA[Mth.abs($$0 % BY_2D_DATA.length)];
    }

    @Nullable
    public static Direction fromNormal(BlockPos $$0) {
        return (Direction)BY_NORMAL.get($$0.asLong());
    }

    @Nullable
    public static Direction fromNormal(int $$0, int $$1, int $$2) {
        return (Direction)BY_NORMAL.get(BlockPos.asLong($$0, $$1, $$2));
    }

    public static Direction fromYRot(double $$0) {
        return Direction.from2DDataValue(Mth.floor($$0 / 90.0 + 0.5) & 3);
    }

    public static Direction fromAxisAndDirection(Axis $$0, AxisDirection $$1) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if ($$1 == AxisDirection.POSITIVE) {
                    yield EAST;
                }
                yield WEST;
            }
            case Axis.Y -> {
                if ($$1 == AxisDirection.POSITIVE) {
                    yield UP;
                }
                yield DOWN;
            }
            case Axis.Z -> $$1 == AxisDirection.POSITIVE ? SOUTH : NORTH;
        };
    }

    public float toYRot() {
        return (this.data2d & 3) * 90;
    }

    public static Direction getRandom(RandomSource $$0) {
        return Util.getRandom(VALUES, $$0);
    }

    public static Direction getNearest(double $$0, double $$1, double $$2) {
        return Direction.getNearest((float)$$0, (float)$$1, (float)$$2);
    }

    public static Direction getNearest(float $$0, float $$1, float $$2) {
        Direction $$3 = NORTH;
        float $$4 = Float.MIN_VALUE;
        for (Direction $$5 : VALUES) {
            float $$6 = $$0 * (float)$$5.normal.getX() + $$1 * (float)$$5.normal.getY() + $$2 * (float)$$5.normal.getZ();
            if (!($$6 > $$4)) continue;
            $$4 = $$6;
            $$3 = $$5;
        }
        return $$3;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static DataResult<Direction> verifyVertical(Direction $$0) {
        return $$0.getAxis().isVertical() ? DataResult.success((Object)$$0) : DataResult.error((String)"Expected a vertical direction");
    }

    public static Direction get(AxisDirection $$0, Axis $$1) {
        for (Direction $$2 : VALUES) {
            if ($$2.getAxisDirection() != $$0 || $$2.getAxis() != $$1) continue;
            return $$2;
        }
        throw new IllegalArgumentException("No such direction: " + $$0 + " " + $$1);
    }

    public Vec3i getNormal() {
        return this.normal;
    }

    public boolean isFacingAngle(float $$0) {
        float $$1 = $$0 * ((float)Math.PI / 180);
        float $$2 = -Mth.sin($$1);
        float $$3 = Mth.cos($$1);
        return (float)this.normal.getX() * $$2 + (float)this.normal.getZ() * $$3 > 0.0f;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Direction::values));
        VERTICAL_CODEC = CODEC.flatXmap(Direction::verifyVertical, Direction::verifyVertical);
        VALUES = Direction.values();
        BY_3D_DATA = (Direction[])Arrays.stream((Object[])VALUES).sorted(Comparator.comparingInt($$0 -> $$0.data3d)).toArray(Direction[]::new);
        BY_2D_DATA = (Direction[])Arrays.stream((Object[])VALUES).filter($$0 -> $$0.getAxis().isHorizontal()).sorted(Comparator.comparingInt($$0 -> $$0.data2d)).toArray(Direction[]::new);
        BY_NORMAL = (Long2ObjectMap)Arrays.stream((Object[])VALUES).collect(Collectors.toMap($$0 -> new BlockPos($$0.getNormal()).asLong(), $$0 -> $$0, ($$0, $$1) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum Axis implements StringRepresentable,
    Predicate<Direction>
    {
        X("x"){

            @Override
            public int choose(int $$0, int $$1, int $$2) {
                return $$0;
            }

            @Override
            public double choose(double $$0, double $$1, double $$2) {
                return $$0;
            }
        }
        ,
        Y("y"){

            @Override
            public int choose(int $$0, int $$1, int $$2) {
                return $$1;
            }

            @Override
            public double choose(double $$0, double $$1, double $$2) {
                return $$1;
            }
        }
        ,
        Z("z"){

            @Override
            public int choose(int $$0, int $$1, int $$2) {
                return $$2;
            }

            @Override
            public double choose(double $$0, double $$1, double $$2) {
                return $$2;
            }
        };

        public static final Axis[] VALUES;
        public static final StringRepresentable.EnumCodec<Axis> CODEC;
        private final String name;

        Axis(String $$0) {
            this.name = $$0;
        }

        @Nullable
        public static Axis byName(String $$0) {
            return CODEC.byName($$0);
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public static Axis getRandom(RandomSource $$0) {
            return Util.getRandom(VALUES, $$0);
        }

        public boolean test(@Nullable Direction $$0) {
            return $$0 != null && $$0.getAxis() == this;
        }

        public Plane getPlane() {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case X, Z -> Plane.HORIZONTAL;
                case Y -> Plane.VERTICAL;
            };
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);

        static {
            VALUES = Axis.values();
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Axis::values));
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private AxisDirection(int $$0, String $$1) {
            this.step = $$0;
            this.name = $$1;
        }

        public int getStep() {
            return this.step;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Plane implements Iterable<Direction>,
    Predicate<Direction>
    {
        HORIZONTAL(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{UP, DOWN}, new Axis[]{Axis.Y});

        private final Direction[] faces;
        private final Axis[] axis;

        private Plane(Direction[] $$0, Axis[] $$1) {
            this.faces = $$0;
            this.axis = $$1;
        }

        public Direction getRandomDirection(RandomSource $$0) {
            return Util.getRandom(this.faces, $$0);
        }

        public Axis getRandomAxis(RandomSource $$0) {
            return Util.getRandom(this.axis, $$0);
        }

        public boolean test(@Nullable Direction $$0) {
            return $$0 != null && $$0.getAxis().getPlane() == this;
        }

        public Iterator<Direction> iterator() {
            return Iterators.forArray((Object[])this.faces);
        }

        public Stream<Direction> stream() {
            return Arrays.stream((Object[])this.faces);
        }

        public List<Direction> shuffledCopy(RandomSource $$0) {
            return Util.shuffledCopy(this.faces, $$0);
        }
    }
}