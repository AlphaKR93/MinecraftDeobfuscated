/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Runnable
 */
package net.minecraft.server;

public class TickTask
implements Runnable {
    private final int tick;
    private final Runnable runnable;

    public TickTask(int $$0, Runnable $$1) {
        this.tick = $$0;
        this.runnable = $$1;
    }

    public int getTick() {
        return this.tick;
    }

    public void run() {
        this.runnable.run();
    }
}