/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.common.primitives.UnsignedLong
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.PriorityQueue
 *  java.util.Queue
 *  java.util.Set
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerCallbacks;
import org.slf4j.Logger;

public class TimerQueue<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CALLBACK_DATA_TAG = "Callback";
    private static final String TIMER_NAME_TAG = "Name";
    private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
    private final TimerCallbacks<T> callbacksRegistry;
    private final Queue<Event<T>> queue = new PriorityQueue(TimerQueue.createComparator());
    private UnsignedLong sequentialId = UnsignedLong.ZERO;
    private final Table<String, Long, Event<T>> events = HashBasedTable.create();

    private static <T> Comparator<Event<T>> createComparator() {
        return Comparator.comparingLong($$0 -> $$0.triggerTime).thenComparing($$0 -> $$0.sequentialId);
    }

    public TimerQueue(TimerCallbacks<T> $$02, Stream<Dynamic<Tag>> $$1) {
        this($$02);
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        $$1.forEach($$0 -> {
            if (!($$0.getValue() instanceof CompoundTag)) {
                LOGGER.warn("Invalid format of events: {}", $$0);
                return;
            }
            this.loadEvent((CompoundTag)$$0.getValue());
        });
    }

    public TimerQueue(TimerCallbacks<T> $$0) {
        this.callbacksRegistry = $$0;
    }

    public void tick(T $$0, long $$1) {
        Event $$2;
        while (($$2 = (Event)this.queue.peek()) != null && $$2.triggerTime <= $$1) {
            this.queue.remove();
            this.events.remove((Object)$$2.id, (Object)$$1);
            $$2.callback.handle($$0, this, $$1);
        }
    }

    public void schedule(String $$0, long $$1, TimerCallback<T> $$2) {
        if (this.events.contains((Object)$$0, (Object)$$1)) {
            return;
        }
        this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
        Event<T> $$3 = new Event<T>($$1, this.sequentialId, $$0, $$2);
        this.events.put((Object)$$0, (Object)$$1, $$3);
        this.queue.add($$3);
    }

    public int remove(String $$0) {
        Collection $$1 = this.events.row((Object)$$0).values();
        $$1.forEach(arg_0 -> this.queue.remove(arg_0));
        int $$2 = $$1.size();
        $$1.clear();
        return $$2;
    }

    public Set<String> getEventsIds() {
        return Collections.unmodifiableSet((Set)this.events.rowKeySet());
    }

    private void loadEvent(CompoundTag $$0) {
        CompoundTag $$1 = $$0.getCompound(CALLBACK_DATA_TAG);
        TimerCallback<T> $$2 = this.callbacksRegistry.deserialize($$1);
        if ($$2 != null) {
            String $$3 = $$0.getString(TIMER_NAME_TAG);
            long $$4 = $$0.getLong(TIMER_TRIGGER_TIME_TAG);
            this.schedule($$3, $$4, $$2);
        }
    }

    private CompoundTag storeEvent(Event<T> $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString(TIMER_NAME_TAG, $$0.id);
        $$1.putLong(TIMER_TRIGGER_TIME_TAG, $$0.triggerTime);
        $$1.put(CALLBACK_DATA_TAG, this.callbacksRegistry.serialize($$0.callback));
        return $$1;
    }

    public ListTag store() {
        ListTag $$0 = new ListTag();
        this.queue.stream().sorted(TimerQueue.createComparator()).map(this::storeEvent).forEach(arg_0 -> ((ListTag)$$0).add(arg_0));
        return $$0;
    }

    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final TimerCallback<T> callback;

        Event(long $$0, UnsignedLong $$1, String $$2, TimerCallback<T> $$3) {
            this.triggerTime = $$0;
            this.sequentialId = $$1;
            this.id = $$2;
            this.callback = $$3;
        }
    }
}