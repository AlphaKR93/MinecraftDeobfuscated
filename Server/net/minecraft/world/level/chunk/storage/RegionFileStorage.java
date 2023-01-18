/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  java.io.DataInput
 *  java.io.DataInputStream
 *  java.io.DataOutput
 *  java.io.DataOutputStream
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Path
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;

public final class RegionFileStorage
implements AutoCloseable {
    public static final String ANVIL_EXTENSION = ".mca";
    private static final int MAX_CACHE_SIZE = 256;
    private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
    private final Path folder;
    private final boolean sync;

    RegionFileStorage(Path $$0, boolean $$1) {
        this.folder = $$0;
        this.sync = $$1;
    }

    private RegionFile getRegionFile(ChunkPos $$0) throws IOException {
        long $$1 = ChunkPos.asLong($$0.getRegionX(), $$0.getRegionZ());
        RegionFile $$2 = (RegionFile)this.regionCache.getAndMoveToFirst($$1);
        if ($$2 != null) {
            return $$2;
        }
        if (this.regionCache.size() >= 256) {
            ((RegionFile)this.regionCache.removeLast()).close();
        }
        FileUtil.createDirectoriesSafe(this.folder);
        Path $$3 = this.folder.resolve("r." + $$0.getRegionX() + "." + $$0.getRegionZ() + ANVIL_EXTENSION);
        RegionFile $$4 = new RegionFile($$3, this.folder, this.sync);
        this.regionCache.putAndMoveToFirst($$1, (Object)$$4);
        return $$4;
    }

    @Nullable
    public CompoundTag read(ChunkPos $$0) throws IOException {
        RegionFile $$1 = this.getRegionFile($$0);
        try (DataInputStream $$2 = $$1.getChunkDataInputStream($$0);){
            if ($$2 == null) {
                CompoundTag compoundTag = null;
                return compoundTag;
            }
            CompoundTag compoundTag = NbtIo.read((DataInput)$$2);
            return compoundTag;
        }
    }

    public void scanChunk(ChunkPos $$0, StreamTagVisitor $$1) throws IOException {
        RegionFile $$2 = this.getRegionFile($$0);
        try (DataInputStream $$3 = $$2.getChunkDataInputStream($$0);){
            if ($$3 != null) {
                NbtIo.parse((DataInput)$$3, $$1);
            }
        }
    }

    protected void write(ChunkPos $$0, @Nullable CompoundTag $$1) throws IOException {
        RegionFile $$2 = this.getRegionFile($$0);
        if ($$1 == null) {
            $$2.clear($$0);
        } else {
            try (DataOutputStream $$3 = $$2.getChunkDataOutputStream($$0);){
                NbtIo.write($$1, (DataOutput)$$3);
            }
        }
    }

    public void close() throws IOException {
        ExceptionCollector<IOException> $$0 = new ExceptionCollector<IOException>();
        for (RegionFile $$1 : this.regionCache.values()) {
            try {
                $$1.close();
            }
            catch (IOException $$2) {
                $$0.add($$2);
            }
        }
        $$0.throwIfPresent();
    }

    public void flush() throws IOException {
        for (RegionFile $$0 : this.regionCache.values()) {
            $$0.flush();
        }
    }
}