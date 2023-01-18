/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ForcedChunksSavedData
extends SavedData {
    public static final String FILE_ID = "chunks";
    private static final String TAG_FORCED = "Forced";
    private final LongSet chunks;

    private ForcedChunksSavedData(LongSet $$0) {
        this.chunks = $$0;
    }

    public ForcedChunksSavedData() {
        this((LongSet)new LongOpenHashSet());
    }

    public static ForcedChunksSavedData load(CompoundTag $$0) {
        return new ForcedChunksSavedData((LongSet)new LongOpenHashSet($$0.getLongArray(TAG_FORCED)));
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        $$0.putLongArray(TAG_FORCED, this.chunks.toLongArray());
        return $$0;
    }

    public LongSet getChunks() {
        return this.chunks;
    }
}