/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.OptionalInt
 */
package com.mojang.blaze3d.platform;

import java.util.OptionalInt;

public class DisplayData {
    public final int width;
    public final int height;
    public final OptionalInt fullscreenWidth;
    public final OptionalInt fullscreenHeight;
    public final boolean isFullscreen;

    public DisplayData(int $$0, int $$1, OptionalInt $$2, OptionalInt $$3, boolean $$4) {
        this.width = $$0;
        this.height = $$1;
        this.fullscreenWidth = $$2;
        this.fullscreenHeight = $$3;
        this.isFullscreen = $$4;
    }
}