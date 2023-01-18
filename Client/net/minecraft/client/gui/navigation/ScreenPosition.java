/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 */
package net.minecraft.client.gui.navigation;

import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;

public record ScreenPosition(int x, int y) {
    public static ScreenPosition of(ScreenAxis $$0, int $$1, int $$2) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case ScreenAxis.HORIZONTAL -> new ScreenPosition($$1, $$2);
            case ScreenAxis.VERTICAL -> new ScreenPosition($$2, $$1);
        };
    }

    public ScreenPosition step(ScreenDirection $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case ScreenDirection.DOWN -> new ScreenPosition(this.x, this.y + 1);
            case ScreenDirection.UP -> new ScreenPosition(this.x, this.y - 1);
            case ScreenDirection.LEFT -> new ScreenPosition(this.x - 1, this.y);
            case ScreenDirection.RIGHT -> new ScreenPosition(this.x + 1, this.y);
        };
    }

    public int getCoordinate(ScreenAxis $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case ScreenAxis.HORIZONTAL -> this.x;
            case ScreenAxis.VERTICAL -> this.y;
        };
    }
}