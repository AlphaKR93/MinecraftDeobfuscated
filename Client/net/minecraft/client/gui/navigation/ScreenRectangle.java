/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.client.gui.navigation;

import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenPosition;

public record ScreenRectangle(ScreenPosition position, int width, int height) {
    private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);

    public ScreenRectangle(int $$0, int $$1, int $$2, int $$3) {
        this(new ScreenPosition($$0, $$1), $$2, $$3);
    }

    public static ScreenRectangle empty() {
        return EMPTY;
    }

    public static ScreenRectangle of(ScreenAxis $$0, int $$1, int $$2, int $$3, int $$4) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case ScreenAxis.HORIZONTAL -> new ScreenRectangle($$1, $$2, $$3, $$4);
            case ScreenAxis.VERTICAL -> new ScreenRectangle($$2, $$1, $$4, $$3);
        };
    }

    public ScreenRectangle step(ScreenDirection $$0) {
        return new ScreenRectangle(this.position.step($$0), this.width, this.height);
    }

    public int getLength(ScreenAxis $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case ScreenAxis.HORIZONTAL -> this.width;
            case ScreenAxis.VERTICAL -> this.height;
        };
    }

    public int getBoundInDirection(ScreenDirection $$0) {
        ScreenAxis $$1 = $$0.getAxis();
        if ($$0.isPositive()) {
            return this.position.getCoordinate($$1) + this.getLength($$1) - 1;
        }
        return this.position.getCoordinate($$1);
    }

    public ScreenRectangle getBorder(ScreenDirection $$0) {
        int $$1 = this.getBoundInDirection($$0);
        ScreenAxis $$2 = $$0.getAxis().orthogonal();
        int $$3 = this.getBoundInDirection($$2.getNegative());
        int $$4 = this.getLength($$2);
        return ScreenRectangle.of($$0.getAxis(), $$1, $$3, 1, $$4).step($$0);
    }

    public boolean overlaps(ScreenRectangle $$0) {
        return this.overlapsInAxis($$0, ScreenAxis.HORIZONTAL) && this.overlapsInAxis($$0, ScreenAxis.VERTICAL);
    }

    public boolean overlapsInAxis(ScreenRectangle $$0, ScreenAxis $$1) {
        int $$2 = this.getBoundInDirection($$1.getNegative());
        int $$3 = $$0.getBoundInDirection($$1.getNegative());
        int $$4 = this.getBoundInDirection($$1.getPositive());
        int $$5 = $$0.getBoundInDirection($$1.getPositive());
        return Math.max((int)$$2, (int)$$3) <= Math.min((int)$$4, (int)$$5);
    }

    public int getCenterInAxis(ScreenAxis $$0) {
        return (this.getBoundInDirection($$0.getPositive()) + this.getBoundInDirection($$0.getNegative())) / 2;
    }
}