/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.mojang.blaze3d.shaders;

public enum FogShape {
    SPHERE(0),
    CYLINDER(1);

    private final int index;

    private FogShape(int $$0) {
        this.index = $$0;
    }

    public int getIndex() {
        return this.index;
    }
}