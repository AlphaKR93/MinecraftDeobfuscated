/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.resources.metadata.animation;

public class AnimationFrame {
    public static final int UNKNOWN_FRAME_TIME = -1;
    private final int index;
    private final int time;

    public AnimationFrame(int $$0) {
        this($$0, -1);
    }

    public AnimationFrame(int $$0, int $$1) {
        this.index = $$0;
        this.time = $$1;
    }

    public int getTime(int $$0) {
        return this.time == -1 ? $$0 : this.time;
    }

    public int getIndex() {
        return this.index;
    }
}