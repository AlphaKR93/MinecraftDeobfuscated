/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level.progress;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class ProcessorChunkProgressListener
implements ChunkProgressListener {
    private final ChunkProgressListener delegate;
    private final ProcessorMailbox<Runnable> mailbox;

    private ProcessorChunkProgressListener(ChunkProgressListener $$0, Executor $$1) {
        this.delegate = $$0;
        this.mailbox = ProcessorMailbox.create($$1, "progressListener");
    }

    public static ProcessorChunkProgressListener createStarted(ChunkProgressListener $$0, Executor $$1) {
        ProcessorChunkProgressListener $$2 = new ProcessorChunkProgressListener($$0, $$1);
        $$2.start();
        return $$2;
    }

    @Override
    public void updateSpawnPos(ChunkPos $$0) {
        this.mailbox.tell(() -> this.delegate.updateSpawnPos($$0));
    }

    @Override
    public void onStatusChange(ChunkPos $$0, @Nullable ChunkStatus $$1) {
        this.mailbox.tell(() -> this.delegate.onStatusChange($$0, $$1));
    }

    @Override
    public void start() {
        this.mailbox.tell(this.delegate::start);
    }

    @Override
    public void stop() {
        this.mailbox.tell(this.delegate::stop);
    }
}