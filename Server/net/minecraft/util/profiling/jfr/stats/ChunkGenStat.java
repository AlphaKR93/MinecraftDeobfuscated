/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  jdk.jfr.consumer.RecordedEvent
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.profiling.jfr.stats.TimedStat;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public record ChunkGenStat(Duration duration, ChunkPos chunkPos, ColumnPos worldPos, ChunkStatus status, String level) implements TimedStat
{
    public static ChunkGenStat from(RecordedEvent $$0) {
        return new ChunkGenStat($$0.getDuration(), new ChunkPos($$0.getInt("chunkPosX"), $$0.getInt("chunkPosX")), new ColumnPos($$0.getInt("worldPosX"), $$0.getInt("worldPosZ")), ChunkStatus.byName($$0.getString("status")), $$0.getString("level"));
    }
}