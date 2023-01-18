/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Runnable
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import javax.annotation.Nullable;

class GameTestEvent {
    @Nullable
    public final Long expectedDelay;
    public final Runnable assertion;

    private GameTestEvent(@Nullable Long $$0, Runnable $$1) {
        this.expectedDelay = $$0;
        this.assertion = $$1;
    }

    static GameTestEvent create(Runnable $$0) {
        return new GameTestEvent(null, $$0);
    }

    static GameTestEvent create(long $$0, Runnable $$1) {
        return new GameTestEvent($$0, $$1);
    }
}