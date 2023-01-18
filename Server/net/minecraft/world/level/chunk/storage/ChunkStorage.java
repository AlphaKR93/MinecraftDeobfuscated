/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Codec
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Math
 *  java.lang.Object
 *  java.nio.file.Path
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage
implements AutoCloseable {
    public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
    private final IOWorker worker;
    protected final DataFixer fixerUpper;
    @Nullable
    private volatile LegacyStructureDataHandler legacyStructureHandler;

    public ChunkStorage(Path $$0, DataFixer $$1, boolean $$2) {
        this.fixerUpper = $$1;
        this.worker = new IOWorker($$0, $$2, "chunk");
    }

    public boolean isOldChunkAround(ChunkPos $$0, int $$1) {
        return this.worker.isOldChunkAround($$0, $$1);
    }

    public CompoundTag upgradeChunkTag(ResourceKey<Level> $$0, Supplier<DimensionDataStorage> $$1, CompoundTag $$2, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> $$3) {
        int $$4 = ChunkStorage.getVersion($$2);
        if ($$4 < 1493 && ($$2 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, $$2, $$4, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            LegacyStructureDataHandler $$5 = this.getLegacyStructureHandler($$0, $$1);
            $$2 = $$5.updateFromLegacy($$2);
        }
        ChunkStorage.injectDatafixingContext($$2, $$0, $$3);
        $$2 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, $$2, Math.max((int)1493, (int)$$4));
        if ($$4 < SharedConstants.getCurrentVersion().getWorldVersion()) {
            $$2.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        }
        $$2.remove("__context");
        return $$2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> $$0, Supplier<DimensionDataStorage> $$1) {
        LegacyStructureDataHandler $$2 = this.legacyStructureHandler;
        if ($$2 == null) {
            ChunkStorage chunkStorage = this;
            synchronized (chunkStorage) {
                $$2 = this.legacyStructureHandler;
                if ($$2 == null) {
                    this.legacyStructureHandler = $$2 = LegacyStructureDataHandler.getLegacyStructureHandler($$0, (DimensionDataStorage)$$1.get());
                }
            }
        }
        return $$2;
    }

    public static void injectDatafixingContext(CompoundTag $$0, ResourceKey<Level> $$12, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> $$2) {
        CompoundTag $$3 = new CompoundTag();
        $$3.putString("dimension", $$12.location().toString());
        $$2.ifPresent($$1 -> $$3.putString("generator", $$1.location().toString()));
        $$0.put("__context", $$3);
    }

    public static int getVersion(CompoundTag $$0) {
        return $$0.contains("DataVersion", 99) ? $$0.getInt("DataVersion") : -1;
    }

    public CompletableFuture<Optional<CompoundTag>> read(ChunkPos $$0) {
        return this.worker.loadAsync($$0);
    }

    public void write(ChunkPos $$0, CompoundTag $$1) {
        this.worker.store($$0, $$1);
        if (this.legacyStructureHandler != null) {
            this.legacyStructureHandler.removeIndex($$0.toLong());
        }
    }

    public void flushWorker() {
        this.worker.synchronize(true).join();
    }

    public void close() throws IOException {
        this.worker.close();
    }

    public ChunkScanAccess chunkScanner() {
        return this.worker;
    }
}