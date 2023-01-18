/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
    private static final String TAG_ID = "i";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_DELAY = "t";
    private static final String TAG_PRIORITY = "p";
    public static final Hash.Strategy<SavedTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<SavedTick<?>>(){

        public int hashCode(SavedTick<?> $$0) {
            return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
        }

        public boolean equals(@Nullable SavedTick<?> $$0, @Nullable SavedTick<?> $$1) {
            if ($$0 == $$1) {
                return true;
            }
            if ($$0 == null || $$1 == null) {
                return false;
            }
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
        }
    };

    public static <T> void loadTickList(ListTag $$0, Function<String, Optional<T>> $$1, ChunkPos $$22, Consumer<SavedTick<T>> $$3) {
        long $$4 = $$22.toLong();
        for (int $$5 = 0; $$5 < $$0.size(); ++$$5) {
            CompoundTag $$6 = $$0.getCompound($$5);
            SavedTick.loadTick($$6, $$1).ifPresent($$2 -> {
                if (ChunkPos.asLong($$2.pos()) == $$4) {
                    $$3.accept((Object)$$2);
                }
            });
        }
    }

    public static <T> Optional<SavedTick<T>> loadTick(CompoundTag $$0, Function<String, Optional<T>> $$12) {
        return ((Optional)$$12.apply((Object)$$0.getString(TAG_ID))).map($$1 -> {
            BlockPos $$2 = new BlockPos($$0.getInt(TAG_X), $$0.getInt(TAG_Y), $$0.getInt(TAG_Z));
            return new SavedTick<Object>($$1, $$2, $$0.getInt(TAG_DELAY), TickPriority.byValue($$0.getInt(TAG_PRIORITY)));
        });
    }

    private static CompoundTag saveTick(String $$0, BlockPos $$1, int $$2, TickPriority $$3) {
        CompoundTag $$4 = new CompoundTag();
        $$4.putString(TAG_ID, $$0);
        $$4.putInt(TAG_X, $$1.getX());
        $$4.putInt(TAG_Y, $$1.getY());
        $$4.putInt(TAG_Z, $$1.getZ());
        $$4.putInt(TAG_DELAY, $$2);
        $$4.putInt(TAG_PRIORITY, $$3.getValue());
        return $$4;
    }

    public static <T> CompoundTag saveTick(ScheduledTick<T> $$0, Function<T, String> $$1, long $$2) {
        return SavedTick.saveTick((String)$$1.apply($$0.type()), $$0.pos(), (int)($$0.triggerTick() - $$2), $$0.priority());
    }

    public CompoundTag save(Function<T, String> $$0) {
        return SavedTick.saveTick((String)$$0.apply(this.type), this.pos, this.delay, this.priority);
    }

    public ScheduledTick<T> unpack(long $$0, long $$1) {
        return new ScheduledTick<T>(this.type, this.pos, $$0 + (long)this.delay, this.priority, $$1);
    }

    public static <T> SavedTick<T> probe(T $$0, BlockPos $$1) {
        return new SavedTick<T>($$0, $$1, 0, TickPriority.NORMAL);
    }
}