/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 */
package net.minecraft.client.gui.navigation;

import net.minecraft.client.gui.navigation.ScreenDirection;

public enum ScreenAxis {
    HORIZONTAL,
    VERTICAL;


    public ScreenAxis orthogonal() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> VERTICAL;
            case VERTICAL -> HORIZONTAL;
        };
    }

    public ScreenDirection getPositive() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> ScreenDirection.RIGHT;
            case VERTICAL -> ScreenDirection.DOWN;
        };
    }

    public ScreenDirection getNegative() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> ScreenDirection.LEFT;
            case VERTICAL -> ScreenDirection.UP;
        };
    }

    public ScreenDirection getDirection(boolean $$0) {
        return $$0 ? this.getPositive() : this.getNegative();
    }
}