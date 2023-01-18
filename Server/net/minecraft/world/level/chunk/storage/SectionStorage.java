/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.function.BooleanSupplier
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.slf4j.Logger;

public class SectionStorage<R>
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SECTIONS_TAG = "Sections";
    private final IOWorker worker;
    private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codec;
    private final Function<Runnable, R> factory;
    private final DataFixer fixerUpper;
    private final DataFixTypes type;
    private final RegistryAccess registryAccess;
    protected final LevelHeightAccessor levelHeightAccessor;

    public SectionStorage(Path $$0, Function<Runnable, Codec<R>> $$1, Function<Runnable, R> $$2, DataFixer $$3, DataFixTypes $$4, boolean $$5, RegistryAccess $$6, LevelHeightAccessor $$7) {
        this.codec = $$1;
        this.factory = $$2;
        this.fixerUpper = $$3;
        this.type = $$4;
        this.registryAccess = $$6;
        this.levelHeightAccessor = $$7;
        this.worker = new IOWorker($$0, $$5, $$0.getFileName().toString());
    }

    protected void tick(BooleanSupplier $$0) {
        while (this.hasWork() && $$0.getAsBoolean()) {
            ChunkPos $$1 = SectionPos.of(this.dirty.firstLong()).chunk();
            this.writeColumn($$1);
        }
    }

    public boolean hasWork() {
        return !this.dirty.isEmpty();
    }

    @Nullable
    protected Optional<R> get(long $$0) {
        return (Optional)this.storage.get($$0);
    }

    protected Optional<R> getOrLoad(long $$0) {
        if (this.outsideStoredRange($$0)) {
            return Optional.empty();
        }
        Optional<R> $$1 = this.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        this.readColumn(SectionPos.of($$0).chunk());
        $$1 = this.get($$0);
        if ($$1 == null) {
            throw Util.pauseInIde(new IllegalStateException());
        }
        return $$1;
    }

    protected boolean outsideStoredRange(long $$0) {
        int $$1 = SectionPos.sectionToBlockCoord(SectionPos.y($$0));
        return this.levelHeightAccessor.isOutsideBuildHeight($$1);
    }

    protected R getOrCreate(long $$0) {
        if (this.outsideStoredRange($$0)) {
            throw Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
        }
        Optional<R> $$1 = this.getOrLoad($$0);
        if ($$1.isPresent()) {
            return (R)$$1.get();
        }
        Object $$2 = this.factory.apply(() -> this.setDirty($$0));
        this.storage.put($$0, (Object)Optional.of((Object)$$2));
        return (R)$$2;
    }

    private void readColumn(ChunkPos $$0) {
        Optional $$1 = (Optional)this.tryRead($$0).join();
        RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
        this.readColumn($$0, $$2, (Tag)$$1.orElse(null));
    }

    private CompletableFuture<Optional<CompoundTag>> tryRead(ChunkPos $$0) {
        return this.worker.loadAsync($$0).exceptionally($$1 -> {
            if ($$1 instanceof IOException) {
                IOException $$2 = (IOException)$$1;
                LOGGER.error("Error reading chunk {} data from disk", (Object)$$0, (Object)$$2);
                return Optional.empty();
            }
            throw new CompletionException($$1);
        });
    }

    private <T> void readColumn(ChunkPos $$0, DynamicOps<T> $$12, @Nullable T $$22) {
        if ($$22 == null) {
            for (int $$3 = this.levelHeightAccessor.getMinSection(); $$3 < this.levelHeightAccessor.getMaxSection(); ++$$3) {
                this.storage.put(SectionStorage.getKey($$0, $$3), (Object)Optional.empty());
            }
        } else {
            int $$6;
            Dynamic $$4 = new Dynamic($$12, $$22);
            int $$5 = SectionStorage.getVersion($$4);
            boolean $$7 = $$5 != ($$6 = SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            Dynamic $$8 = this.type.update(this.fixerUpper, $$4, $$5, $$6);
            OptionalDynamic $$9 = $$8.get(SECTIONS_TAG);
            for (int $$10 = this.levelHeightAccessor.getMinSection(); $$10 < this.levelHeightAccessor.getMaxSection(); ++$$10) {
                long $$11 = SectionStorage.getKey($$0, $$10);
                Optional $$122 = $$9.get(Integer.toString((int)$$10)).result().flatMap($$1 -> ((Codec)this.codec.apply(() -> this.setDirty($$11))).parse($$1).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)));
                this.storage.put($$11, (Object)$$122);
                $$122.ifPresent($$2 -> {
                    this.onSectionLoad($$11);
                    if ($$7) {
                        this.setDirty($$11);
                    }
                });
            }
        }
    }

    private void writeColumn(ChunkPos $$0) {
        RegistryOps<Tag> $$1 = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
        Dynamic<Tag> $$2 = this.writeColumn($$0, $$1);
        Tag $$3 = (Tag)$$2.getValue();
        if ($$3 instanceof CompoundTag) {
            this.worker.store($$0, (CompoundTag)$$3);
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)$$3);
        }
    }

    private <T> Dynamic<T> writeColumn(ChunkPos $$0, DynamicOps<T> $$1) {
        HashMap $$2 = Maps.newHashMap();
        for (int $$3 = this.levelHeightAccessor.getMinSection(); $$3 < this.levelHeightAccessor.getMaxSection(); ++$$3) {
            long $$4 = SectionStorage.getKey($$0, $$3);
            this.dirty.remove($$4);
            Optional $$5 = (Optional)this.storage.get($$4);
            if ($$5 == null || !$$5.isPresent()) continue;
            DataResult $$6 = ((Codec)this.codec.apply(() -> this.setDirty($$4))).encodeStart($$1, $$5.get());
            String $$7 = Integer.toString((int)$$3);
            $$6.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(arg_0 -> SectionStorage.lambda$writeColumn$6((Map)$$2, $$1, $$7, arg_0));
        }
        return new Dynamic($$1, $$1.createMap((Map)ImmutableMap.of((Object)$$1.createString(SECTIONS_TAG), (Object)$$1.createMap((Map)$$2), (Object)$$1.createString("DataVersion"), (Object)$$1.createInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))));
    }

    private static long getKey(ChunkPos $$0, int $$1) {
        return SectionPos.asLong($$0.x, $$1, $$0.z);
    }

    protected void onSectionLoad(long $$0) {
    }

    protected void setDirty(long $$0) {
        Optional $$1 = (Optional)this.storage.get($$0);
        if ($$1 == null || !$$1.isPresent()) {
            LOGGER.warn("No data for position: {}", (Object)SectionPos.of($$0));
            return;
        }
        this.dirty.add($$0);
    }

    private static int getVersion(Dynamic<?> $$0) {
        return $$0.get("DataVersion").asInt(1945);
    }

    public void flush(ChunkPos $$0) {
        if (this.hasWork()) {
            for (int $$1 = this.levelHeightAccessor.getMinSection(); $$1 < this.levelHeightAccessor.getMaxSection(); ++$$1) {
                long $$2 = SectionStorage.getKey($$0, $$1);
                if (!this.dirty.contains($$2)) continue;
                this.writeColumn($$0);
                return;
            }
        }
    }

    public void close() throws IOException {
        this.worker.close();
    }

    private static /* synthetic */ void lambda$writeColumn$6(Map $$0, DynamicOps $$1, String $$2, Object $$3) {
        $$0.put($$1.createString($$2), $$3);
    }
}