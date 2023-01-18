/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class DefaultedVertexConsumer
implements VertexConsumer {
    protected boolean defaultColorSet;
    protected int defaultR = 255;
    protected int defaultG = 255;
    protected int defaultB = 255;
    protected int defaultA = 255;

    @Override
    public void defaultColor(int $$0, int $$1, int $$2, int $$3) {
        this.defaultR = $$0;
        this.defaultG = $$1;
        this.defaultB = $$2;
        this.defaultA = $$3;
        this.defaultColorSet = true;
    }

    @Override
    public void unsetDefaultColor() {
        this.defaultColorSet = false;
    }
}