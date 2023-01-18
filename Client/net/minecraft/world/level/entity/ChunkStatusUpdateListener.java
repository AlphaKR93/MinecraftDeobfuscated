/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 */
package net.minecraft.world.level.entity;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;

@FunctionalInterface
public interface ChunkStatusUpdateListener {
    public void onChunkStatusChange(ChunkPos var1, ChunkHolder.FullChunkStatus var2);
}