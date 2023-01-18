/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Class
 *  java.lang.ClassNotFoundException
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.concurrent.locks.ReadWriteLock
 *  java.util.concurrent.locks.ReentrantReadWriteLock
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 */
package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap();
    private static final int MAX_ID_VALUE = 254;
    private final Entity entity;
    private final Int2ObjectMap<DataItem<?>> itemsById = new Int2ObjectOpenHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isDirty;

    public SynchedEntityData(Entity $$0) {
        this.entity = $$0;
    }

    public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> $$0, EntityDataSerializer<T> $$1) {
        int $$6;
        if (LOGGER.isDebugEnabled()) {
            try {
                Class $$2 = Class.forName((String)Thread.currentThread().getStackTrace()[2].getClassName());
                if (!$$2.equals($$0)) {
                    LOGGER.debug("defineId called for: {} from {}", new Object[]{$$0, $$2, new RuntimeException()});
                }
            }
            catch (ClassNotFoundException $$2) {
                // empty catch block
            }
        }
        if (ENTITY_ID_POOL.containsKey($$0)) {
            int $$3 = ENTITY_ID_POOL.getInt($$0) + 1;
        } else {
            int $$4 = 0;
            Class $$5 = $$0;
            while ($$5 != Entity.class) {
                if (!ENTITY_ID_POOL.containsKey((Object)($$5 = $$5.getSuperclass()))) continue;
                $$4 = ENTITY_ID_POOL.getInt((Object)$$5) + 1;
                break;
            }
            $$6 = $$4;
        }
        if ($$6 > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + $$6 + "! (Max is 254)");
        }
        ENTITY_ID_POOL.put((Object)$$0, $$6);
        return $$1.createAccessor($$6);
    }

    public <T> void define(EntityDataAccessor<T> $$0, T $$1) {
        int $$2 = $$0.getId();
        if ($$2 > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + $$2 + "! (Max is 254)");
        }
        if (this.itemsById.containsKey($$2)) {
            throw new IllegalArgumentException("Duplicate id value for " + $$2 + "!");
        }
        if (EntityDataSerializers.getSerializedId($$0.getSerializer()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + $$0.getSerializer() + " for " + $$2 + "!");
        }
        this.createDataItem($$0, $$1);
    }

    private <T> void createDataItem(EntityDataAccessor<T> $$0, T $$1) {
        DataItem<T> $$2 = new DataItem<T>($$0, $$1);
        this.lock.writeLock().lock();
        this.itemsById.put($$0.getId(), $$2);
        this.lock.writeLock().unlock();
    }

    /*
     * WARNING - void declaration
     */
    private <T> DataItem<T> getItem(EntityDataAccessor<T> $$0) {
        void $$5;
        this.lock.readLock().lock();
        try {
            DataItem $$1 = (DataItem)this.itemsById.get($$0.getId());
        }
        catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Getting synched entity data");
            CrashReportCategory $$4 = $$3.addCategory("Synched entity data");
            $$4.setDetail("Data ID", $$0);
            throw new ReportedException($$3);
        }
        finally {
            this.lock.readLock().unlock();
        }
        return $$5;
    }

    public <T> T get(EntityDataAccessor<T> $$0) {
        return this.getItem($$0).getValue();
    }

    public <T> void set(EntityDataAccessor<T> $$0, T $$1) {
        DataItem<T> $$2 = this.getItem($$0);
        if (ObjectUtils.notEqual($$1, $$2.getValue())) {
            $$2.setValue($$1);
            this.entity.onSyncedDataUpdated($$0);
            $$2.setDirty(true);
            this.isDirty = true;
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    @Nullable
    public List<DataValue<?>> packDirty() {
        ArrayList $$0 = null;
        if (this.isDirty) {
            this.lock.readLock().lock();
            for (DataItem $$1 : this.itemsById.values()) {
                if (!$$1.isDirty()) continue;
                $$1.setDirty(false);
                if ($$0 == null) {
                    $$0 = new ArrayList();
                }
                $$0.add($$1.value());
            }
            this.lock.readLock().unlock();
        }
        this.isDirty = false;
        return $$0;
    }

    @Nullable
    public List<DataValue<?>> getNonDefaultValues() {
        ArrayList $$0 = null;
        this.lock.readLock().lock();
        for (DataItem $$1 : this.itemsById.values()) {
            if ($$1.isSetToDefault()) continue;
            if ($$0 == null) {
                $$0 = new ArrayList();
            }
            $$0.add($$1.value());
        }
        this.lock.readLock().unlock();
        return $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void assignValues(List<DataValue<?>> $$0) {
        this.lock.writeLock().lock();
        try {
            for (DataValue $$1 : $$0) {
                DataItem $$2 = (DataItem)this.itemsById.get($$1.id);
                if ($$2 == null) continue;
                this.assignValue($$2, $$1);
                this.entity.onSyncedDataUpdated($$2.getAccessor());
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private <T> void assignValue(DataItem<T> $$0, DataValue<?> $$1) {
        if (!Objects.equals($$1.serializer(), $$0.accessor.getSerializer())) {
            throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", (Object[])new Object[]{$$0.accessor.getId(), this.entity, $$0.value, $$0.value.getClass(), $$1.value, $$1.value.getClass()}));
        }
        $$0.setValue($$1.value);
    }

    public boolean isEmpty() {
        return this.itemsById.isEmpty();
    }

    public static class DataItem<T> {
        final EntityDataAccessor<T> accessor;
        T value;
        private final T initialValue;
        private boolean dirty;

        public DataItem(EntityDataAccessor<T> $$0, T $$1) {
            this.accessor = $$0;
            this.initialValue = $$1;
            this.value = $$1;
        }

        public EntityDataAccessor<T> getAccessor() {
            return this.accessor;
        }

        public void setValue(T $$0) {
            this.value = $$0;
        }

        public T getValue() {
            return this.value;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean $$0) {
            this.dirty = $$0;
        }

        public boolean isSetToDefault() {
            return this.initialValue.equals(this.value);
        }

        public DataValue<T> value() {
            return DataValue.create(this.accessor, this.value);
        }
    }

    public record DataValue<T>(int id, EntityDataSerializer<T> serializer, T value) {
        public static <T> DataValue<T> create(EntityDataAccessor<T> $$0, T $$1) {
            EntityDataSerializer<T> $$2 = $$0.getSerializer();
            return new DataValue<T>($$0.getId(), $$2, $$2.copy($$1));
        }

        public void write(FriendlyByteBuf $$0) {
            int $$1 = EntityDataSerializers.getSerializedId(this.serializer);
            if ($$1 < 0) {
                throw new EncoderException("Unknown serializer type " + this.serializer);
            }
            $$0.writeByte(this.id);
            $$0.writeVarInt($$1);
            this.serializer.write($$0, this.value);
        }

        public static DataValue<?> read(FriendlyByteBuf $$0, int $$1) {
            int $$2 = $$0.readVarInt();
            EntityDataSerializer<?> $$3 = EntityDataSerializers.getSerializer($$2);
            if ($$3 == null) {
                throw new DecoderException("Unknown serializer type " + $$2);
            }
            return DataValue.read($$0, $$1, $$3);
        }

        private static <T> DataValue<T> read(FriendlyByteBuf $$0, int $$1, EntityDataSerializer<T> $$2) {
            return new DataValue<T>($$1, $$2, $$2.read($$0));
        }
    }
}