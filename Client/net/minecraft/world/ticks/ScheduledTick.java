/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  java.lang.Long
 *  java.lang.Object
 *  java.util.Comparator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.TickPriority;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
    public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = ($$0, $$1) -> {
        int $$2 = Long.compare((long)$$0.triggerTick, (long)$$1.triggerTick);
        if ($$2 != 0) {
            return $$2;
        }
        $$2 = $$0.priority.compareTo($$1.priority);
        if ($$2 != 0) {
            return $$2;
        }
        return Long.compare((long)$$0.subTickOrder, (long)$$1.subTickOrder);
    };
    public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = ($$0, $$1) -> {
        int $$2 = $$0.priority.compareTo($$1.priority);
        if ($$2 != 0) {
            return $$2;
        }
        return Long.compare((long)$$0.subTickOrder, (long)$$1.subTickOrder);
    };
    public static final Hash.Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<ScheduledTick<?>>(){

        public int hashCode(ScheduledTick<?> $$0) {
            return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
        }

        public boolean equals(@Nullable ScheduledTick<?> $$0, @Nullable ScheduledTick<?> $$1) {
            if ($$0 == $$1) {
                return true;
            }
            if ($$0 == null || $$1 == null) {
                return false;
            }
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
        }
    };

    public ScheduledTick(T $$0, BlockPos $$1, long $$2, long $$3) {
        this($$0, $$1, $$2, TickPriority.NORMAL, $$3);
    }

    public ScheduledTick {
        $$1 = $$1.immutable();
    }

    public static <T> ScheduledTick<T> probe(T $$0, BlockPos $$1) {
        return new ScheduledTick<T>($$0, $$1, 0L, TickPriority.NORMAL, 0L);
    }
}