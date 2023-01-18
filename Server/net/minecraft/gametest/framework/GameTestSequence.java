/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.Throwable
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestEvent;
import net.minecraft.gametest.framework.GameTestInfo;

public class GameTestSequence {
    final GameTestInfo parent;
    private final List<GameTestEvent> events = Lists.newArrayList();
    private long lastTick;

    GameTestSequence(GameTestInfo $$0) {
        this.parent = $$0;
        this.lastTick = $$0.getTick();
    }

    public GameTestSequence thenWaitUntil(Runnable $$0) {
        this.events.add((Object)GameTestEvent.create($$0));
        return this;
    }

    public GameTestSequence thenWaitUntil(long $$0, Runnable $$1) {
        this.events.add((Object)GameTestEvent.create($$0, $$1));
        return this;
    }

    public GameTestSequence thenIdle(int $$0) {
        return this.thenExecuteAfter($$0, () -> {});
    }

    public GameTestSequence thenExecute(Runnable $$0) {
        this.events.add((Object)GameTestEvent.create(() -> this.executeWithoutFail($$0)));
        return this;
    }

    public GameTestSequence thenExecuteAfter(int $$0, Runnable $$1) {
        this.events.add((Object)GameTestEvent.create(() -> {
            if (this.parent.getTick() < this.lastTick + (long)$$0) {
                throw new GameTestAssertException("Waiting");
            }
            this.executeWithoutFail($$1);
        }));
        return this;
    }

    public GameTestSequence thenExecuteFor(int $$0, Runnable $$1) {
        this.events.add((Object)GameTestEvent.create(() -> {
            if (this.parent.getTick() < this.lastTick + (long)$$0) {
                this.executeWithoutFail($$1);
                throw new GameTestAssertException("Waiting");
            }
        }));
        return this;
    }

    public void thenSucceed() {
        this.events.add((Object)GameTestEvent.create(this.parent::succeed));
    }

    public void thenFail(Supplier<Exception> $$0) {
        this.events.add((Object)GameTestEvent.create(() -> this.parent.fail((Throwable)$$0.get())));
    }

    public Condition thenTrigger() {
        Condition $$0 = new Condition();
        this.events.add((Object)GameTestEvent.create(() -> $$0.trigger(this.parent.getTick())));
        return $$0;
    }

    public void tickAndContinue(long $$0) {
        try {
            this.tick($$0);
        }
        catch (GameTestAssertException gameTestAssertException) {
            // empty catch block
        }
    }

    public void tickAndFailIfNotComplete(long $$0) {
        try {
            this.tick($$0);
        }
        catch (GameTestAssertException $$1) {
            this.parent.fail((Throwable)$$1);
        }
    }

    private void executeWithoutFail(Runnable $$0) {
        try {
            $$0.run();
        }
        catch (GameTestAssertException $$1) {
            this.parent.fail((Throwable)$$1);
        }
    }

    private void tick(long $$0) {
        Iterator $$1 = this.events.iterator();
        while ($$1.hasNext()) {
            GameTestEvent $$2 = (GameTestEvent)$$1.next();
            $$2.assertion.run();
            $$1.remove();
            long $$3 = $$0 - this.lastTick;
            long $$4 = this.lastTick;
            this.lastTick = $$0;
            if ($$2.expectedDelay == null || $$2.expectedDelay == $$3) continue;
            this.parent.fail((Throwable)new GameTestAssertException("Succeeded in invalid tick: expected " + ($$4 + $$2.expectedDelay) + ", but current tick is " + $$0));
            break;
        }
    }

    public class Condition {
        private static final long NOT_TRIGGERED = -1L;
        private long triggerTime = -1L;

        void trigger(long $$0) {
            if (this.triggerTime != -1L) {
                throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
            }
            this.triggerTime = $$0;
        }

        public void assertTriggeredThisTick() {
            long $$0 = GameTestSequence.this.parent.getTick();
            if (this.triggerTime != $$0) {
                if (this.triggerTime == -1L) {
                    throw new GameTestAssertException("Condition not triggered (t=" + $$0 + ")");
                }
                throw new GameTestAssertException("Condition triggered at " + this.triggerTime + ", (t=" + $$0 + ")");
            }
        }
    }
}