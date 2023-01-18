/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 */
package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.minecraft.client.gui.navigation.ScreenAxis;

public enum ScreenDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private final IntComparator coordinateValueComparator = ($$0, $$1) -> $$0 == $$1 ? 0 : (this.isBefore($$0, $$1) ? -1 : 1);

    public ScreenAxis getAxis() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, DOWN -> ScreenAxis.VERTICAL;
            case LEFT, RIGHT -> ScreenAxis.HORIZONTAL;
        };
    }

    public ScreenDirection getOpposite() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, LEFT -> false;
            case DOWN, RIGHT -> true;
        };
    }

    public boolean isAfter(int $$0, int $$1) {
        if (this.isPositive()) {
            return $$0 > $$1;
        }
        return $$1 > $$0;
    }

    public boolean isBefore(int $$0, int $$1) {
        if (this.isPositive()) {
            return $$0 < $$1;
        }
        return $$1 < $$0;
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }
}