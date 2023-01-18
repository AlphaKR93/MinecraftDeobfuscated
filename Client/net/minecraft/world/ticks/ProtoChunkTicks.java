/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunkTicks<T>
implements SerializableTickContainer<T>,
TickContainerAccess<T> {
    private final List<SavedTick<T>> ticks = Lists.newArrayList();
    private final Set<SavedTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(SavedTick.UNIQUE_TICK_HASH);

    @Override
    public void schedule(ScheduledTick<T> $$0) {
        SavedTick<T> $$1 = new SavedTick<T>($$0.type(), $$0.pos(), 0, $$0.priority());
        this.schedule($$1);
    }

    @Override
    private void schedule(SavedTick<T> $$0) {
        if (this.ticksPerPosition.add($$0)) {
            this.ticks.add($$0);
        }
    }

    @Override
    public boolean hasScheduledTick(BlockPos $$0, T $$1) {
        return this.ticksPerPosition.contains(SavedTick.probe($$1, $$0));
    }

    @Override
    public int count() {
        return this.ticks.size();
    }

    @Override
    public Tag save(long $$0, Function<T, String> $$1) {
        ListTag $$2 = new ListTag();
        for (SavedTick $$3 : this.ticks) {
            $$2.add($$3.save($$1));
        }
        return $$2;
    }

    public List<SavedTick<T>> scheduledTicks() {
        return List.copyOf(this.ticks);
    }

    public static <T> ProtoChunkTicks<T> load(ListTag $$0, Function<String, Optional<T>> $$1, ChunkPos $$2) {
        ProtoChunkTicks<T> $$3 = new ProtoChunkTicks<T>();
        SavedTick.loadTickList($$0, $$1, $$2, $$3::schedule);
        return $$3;
    }
}