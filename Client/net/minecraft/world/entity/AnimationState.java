/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Long
 *  java.lang.Object
 *  java.util.function.Consumer
 */
package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class AnimationState {
    private static final long STOPPED = Long.MAX_VALUE;
    private long lastTime = Long.MAX_VALUE;
    private long accumulatedTime;

    public void start(int $$0) {
        this.lastTime = (long)$$0 * 1000L / 20L;
        this.accumulatedTime = 0L;
    }

    public void startIfStopped(int $$0) {
        if (!this.isStarted()) {
            this.start($$0);
        }
    }

    public void animateWhen(boolean $$0, int $$1) {
        if ($$0) {
            this.startIfStopped($$1);
        } else {
            this.stop();
        }
    }

    public void stop() {
        this.lastTime = Long.MAX_VALUE;
    }

    public void ifStarted(Consumer<AnimationState> $$0) {
        if (this.isStarted()) {
            $$0.accept((Object)this);
        }
    }

    public void updateTime(float $$0, float $$1) {
        if (!this.isStarted()) {
            return;
        }
        long $$2 = Mth.lfloor($$0 * 1000.0f / 20.0f);
        this.accumulatedTime += (long)((float)($$2 - this.lastTime) * $$1);
        this.lastTime = $$2;
    }

    public long getAccumulatedTime() {
        return this.accumulatedTime;
    }

    public boolean isStarted() {
        return this.lastTime != Long.MAX_VALUE;
    }
}