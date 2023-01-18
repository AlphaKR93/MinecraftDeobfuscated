/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  java.lang.Object
 *  java.time.Duration
 *  java.util.concurrent.atomic.AtomicReference
 */
package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;

public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<Params> params = new AtomicReference();

    public RepeatedNarrator(Duration $$0) {
        this.permitsPerSecond = 1000.0f / (float)$$0.toMillis();
    }

    public void narrate(GameNarrator $$0, Component $$12) {
        Params $$2 = (Params)this.params.updateAndGet($$1 -> {
            if ($$1 == null || !$$12.equals($$1.narration)) {
                return new Params($$12, RateLimiter.create((double)this.permitsPerSecond));
            }
            return $$1;
        });
        if ($$2.rateLimiter.tryAcquire(1)) {
            $$0.sayNow($$12);
        }
    }

    static class Params {
        final Component narration;
        final RateLimiter rateLimiter;

        Params(Component $$0, RateLimiter $$1) {
            this.narration = $$0;
            this.rateLimiter = $$1;
        }
    }
}