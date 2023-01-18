/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.metrics.MetricCategory;

public interface ProfilerFiller {
    public static final String ROOT = "root";

    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void popPush(String var1);

    public void popPush(Supplier<String> var1);

    public void markForCharting(MetricCategory var1);

    default public void incrementCounter(String $$0) {
        this.incrementCounter($$0, 1);
    }

    public void incrementCounter(String var1, int var2);

    default public void incrementCounter(Supplier<String> $$0) {
        this.incrementCounter($$0, 1);
    }

    public void incrementCounter(Supplier<String> var1, int var2);

    public static ProfilerFiller tee(final ProfilerFiller $$0, final ProfilerFiller $$1) {
        if ($$0 == InactiveProfiler.INSTANCE) {
            return $$1;
        }
        if ($$1 == InactiveProfiler.INSTANCE) {
            return $$0;
        }
        return new ProfilerFiller(){

            @Override
            public void startTick() {
                $$0.startTick();
                $$1.startTick();
            }

            @Override
            public void endTick() {
                $$0.endTick();
                $$1.endTick();
            }

            @Override
            public void push(String $$02) {
                $$0.push($$02);
                $$1.push($$02);
            }

            @Override
            public void push(Supplier<String> $$02) {
                $$0.push($$02);
                $$1.push($$02);
            }

            @Override
            public void markForCharting(MetricCategory $$02) {
                $$0.markForCharting($$02);
                $$1.markForCharting($$02);
            }

            @Override
            public void pop() {
                $$0.pop();
                $$1.pop();
            }

            @Override
            public void popPush(String $$02) {
                $$0.popPush($$02);
                $$1.popPush($$02);
            }

            @Override
            public void popPush(Supplier<String> $$02) {
                $$0.popPush($$02);
                $$1.popPush($$02);
            }

            @Override
            public void incrementCounter(String $$02, int $$12) {
                $$0.incrementCounter($$02, $$12);
                $$1.incrementCounter($$02, $$12);
            }

            @Override
            public void incrementCounter(Supplier<String> $$02, int $$12) {
                $$0.incrementCounter($$02, $$12);
                $$1.incrementCounter($$02, $$12);
            }
        };
    }
}