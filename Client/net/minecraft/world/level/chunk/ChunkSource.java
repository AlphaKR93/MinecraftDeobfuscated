/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.BooleanSupplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;

public abstract class ChunkSource
implements LightChunkGetter,
AutoCloseable {
    @Nullable
    public LevelChunk getChunk(int $$0, int $$1, boolean $$2) {
        return (LevelChunk)this.getChunk($$0, $$1, ChunkStatus.FULL, $$2);
    }

    @Nullable
    public LevelChunk getChunkNow(int $$0, int $$1) {
        return this.getChunk($$0, $$1, false);
    }

    @Override
    @Nullable
    public BlockGetter getChunkForLighting(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.EMPTY, false);
    }

    public boolean hasChunk(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract void tick(BooleanSupplier var1, boolean var2);

    public abstract String gatherStats();

    public abstract int getLoadedChunksCount();

    public void close() throws IOException {
    }

    public abstract LevelLightEngine getLightEngine();

    public void setSpawnSettings(boolean $$0, boolean $$1) {
    }

    public void updateChunkForced(ChunkPos $$0, boolean $$1) {
    }
}