/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Column {
    public static Range around(int $$0, int $$1) {
        return new Range($$0 - 1, $$1 + 1);
    }

    public static Range inside(int $$0, int $$1) {
        return new Range($$0, $$1);
    }

    public static Column below(int $$0) {
        return new Ray($$0, false);
    }

    public static Column fromHighest(int $$0) {
        return new Ray($$0 + 1, false);
    }

    public static Column above(int $$0) {
        return new Ray($$0, true);
    }

    public static Column fromLowest(int $$0) {
        return new Ray($$0 - 1, true);
    }

    public static Column line() {
        return Line.INSTANCE;
    }

    public static Column create(OptionalInt $$0, OptionalInt $$1) {
        if ($$0.isPresent() && $$1.isPresent()) {
            return Column.inside($$0.getAsInt(), $$1.getAsInt());
        }
        if ($$0.isPresent()) {
            return Column.above($$0.getAsInt());
        }
        if ($$1.isPresent()) {
            return Column.below($$1.getAsInt());
        }
        return Column.line();
    }

    public abstract OptionalInt getCeiling();

    public abstract OptionalInt getFloor();

    public abstract OptionalInt getHeight();

    public Column withFloor(OptionalInt $$0) {
        return Column.create($$0, this.getCeiling());
    }

    public Column withCeiling(OptionalInt $$0) {
        return Column.create(this.getFloor(), $$0);
    }

    public static Optional<Column> scan(LevelSimulatedReader $$0, BlockPos $$1, int $$2, Predicate<BlockState> $$3, Predicate<BlockState> $$4) {
        BlockPos.MutableBlockPos $$5 = $$1.mutable();
        if (!$$0.isStateAtPosition($$1, $$3)) {
            return Optional.empty();
        }
        int $$6 = $$1.getY();
        OptionalInt $$7 = Column.scanDirection($$0, $$2, $$3, $$4, $$5, $$6, Direction.UP);
        OptionalInt $$8 = Column.scanDirection($$0, $$2, $$3, $$4, $$5, $$6, Direction.DOWN);
        return Optional.of((Object)Column.create($$8, $$7));
    }

    private static OptionalInt scanDirection(LevelSimulatedReader $$0, int $$1, Predicate<BlockState> $$2, Predicate<BlockState> $$3, BlockPos.MutableBlockPos $$4, int $$5, Direction $$6) {
        $$4.setY($$5);
        for (int $$7 = 1; $$7 < $$1 && $$0.isStateAtPosition($$4, $$2); ++$$7) {
            $$4.move($$6);
        }
        return $$0.isStateAtPosition($$4, $$3) ? OptionalInt.of((int)$$4.getY()) : OptionalInt.empty();
    }

    public static final class Range
    extends Column {
        private final int floor;
        private final int ceiling;

        protected Range(int $$0, int $$1) {
            this.floor = $$0;
            this.ceiling = $$1;
            if (this.height() < 0) {
                throw new IllegalArgumentException("Column of negative height: " + this);
            }
        }

        @Override
        public OptionalInt getCeiling() {
            return OptionalInt.of((int)this.ceiling);
        }

        @Override
        public OptionalInt getFloor() {
            return OptionalInt.of((int)this.floor);
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.of((int)this.height());
        }

        public int ceiling() {
            return this.ceiling;
        }

        public int floor() {
            return this.floor;
        }

        public int height() {
            return this.ceiling - this.floor - 1;
        }

        public String toString() {
            return "C(" + this.ceiling + "-" + this.floor + ")";
        }
    }

    public static final class Ray
    extends Column {
        private final int edge;
        private final boolean pointingUp;

        public Ray(int $$0, boolean $$1) {
            this.edge = $$0;
            this.pointingUp = $$1;
        }

        @Override
        public OptionalInt getCeiling() {
            return this.pointingUp ? OptionalInt.empty() : OptionalInt.of((int)this.edge);
        }

        @Override
        public OptionalInt getFloor() {
            return this.pointingUp ? OptionalInt.of((int)this.edge) : OptionalInt.empty();
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }

        public String toString() {
            return this.pointingUp ? "C(" + this.edge + "-)" : "C(-" + this.edge + ")";
        }
    }

    public static final class Line
    extends Column {
        static final Line INSTANCE = new Line();

        private Line() {
        }

        @Override
        public OptionalInt getCeiling() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt getFloor() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }

        public String toString() {
            return "C(-)";
        }
    }
}