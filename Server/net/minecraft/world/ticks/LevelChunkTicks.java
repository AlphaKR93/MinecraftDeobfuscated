/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.PriorityQueue
 *  java.util.Queue
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.ticks;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;

public class LevelChunkTicks<T>
implements SerializableTickContainer<T>,
TickContainerAccess<T> {
    private final Queue<ScheduledTick<T>> tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
    @Nullable
    private List<SavedTick<T>> pendingTicks;
    private final Set<ScheduledTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
    @Nullable
    private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

    public LevelChunkTicks() {
    }

    public LevelChunkTicks(List<SavedTick<T>> $$0) {
        this.pendingTicks = $$0;
        for (SavedTick $$1 : $$0) {
            this.ticksPerPosition.add(ScheduledTick.probe($$1.type(), $$1.pos()));
        }
    }

    public void setOnTickAdded(@Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> $$0) {
        this.onTickAdded = $$0;
    }

    @Nullable
    public ScheduledTick<T> peek() {
        return (ScheduledTick)((Object)this.tickQueue.peek());
    }

    @Nullable
    public ScheduledTick<T> poll() {
        ScheduledTick $$0 = (ScheduledTick)((Object)this.tickQueue.poll());
        if ($$0 != null) {
            this.ticksPerPosition.remove((Object)$$0);
        }
        return $$0;
    }

    @Override
    public void schedule(ScheduledTick<T> $$0) {
        if (this.ticksPerPosition.add($$0)) {
            this.scheduleUnchecked($$0);
        }
    }

    private void scheduleUnchecked(ScheduledTick<T> $$0) {
        this.tickQueue.add($$0);
        if (this.onTickAdded != null) {
            this.onTickAdded.accept((Object)this, $$0);
        }
    }

    @Override
    public boolean hasScheduledTick(BlockPos $$0, T $$1) {
        return this.ticksPerPosition.contains(ScheduledTick.probe($$1, $$0));
    }

    public void removeIf(Predicate<ScheduledTick<T>> $$0) {
        Iterator $$1 = this.tickQueue.iterator();
        while ($$1.hasNext()) {
            ScheduledTick $$2 = (ScheduledTick)((Object)$$1.next());
            if (!$$0.test((Object)$$2)) continue;
            $$1.remove();
            this.ticksPerPosition.remove((Object)$$2);
        }
    }

    public Stream<ScheduledTick<T>> getAll() {
        return this.tickQueue.stream();
    }

    @Override
    public int count() {
        return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
    }

    @Override
    public ListTag save(long $$0, Function<T, String> $$1) {
        ListTag $$2 = new ListTag();
        if (this.pendingTicks != null) {
            for (SavedTick $$3 : this.pendingTicks) {
                $$2.add($$3.save($$1));
            }
        }
        for (ScheduledTick $$4 : this.tickQueue) {
            $$2.add(SavedTick.saveTick($$4, $$1, $$0));
        }
        return $$2;
    }

    public void unpack(long $$0) {
        if (this.pendingTicks != null) {
            int $$1 = -this.pendingTicks.size();
            for (SavedTick $$2 : this.pendingTicks) {
                this.scheduleUnchecked($$2.unpack($$0, $$1++));
            }
        }
        this.pendingTicks = null;
    }

    public static <T> LevelChunkTicks<T> load(ListTag $$0, Function<String, Optional<T>> $$1, ChunkPos $$2) {
        ImmutableList.Builder $$3 = ImmutableList.builder();
        SavedTick.loadTickList($$0, $$1, $$2, arg_0 -> ((ImmutableList.Builder)$$3).add(arg_0));
        return new LevelChunkTicks<T>($$3.build());
    }
}