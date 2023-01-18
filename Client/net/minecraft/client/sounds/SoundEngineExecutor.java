/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Thread
 *  java.util.concurrent.locks.LockSupport
 */
package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.util.thread.BlockableEventLoop;

public class SoundEngineExecutor
extends BlockableEventLoop<Runnable> {
    private Thread thread = this.createThread();
    private volatile boolean shutdown;

    public SoundEngineExecutor() {
        super("Sound executor");
    }

    private Thread createThread() {
        Thread $$0 = new Thread(this::run);
        $$0.setDaemon(true);
        $$0.setName("Sound engine");
        $$0.start();
        return $$0;
    }

    @Override
    protected Runnable wrapRunnable(Runnable $$0) {
        return $$0;
    }

    @Override
    protected boolean shouldRun(Runnable $$0) {
        return !this.shutdown;
    }

    @Override
    protected Thread getRunningThread() {
        return this.thread;
    }

    private void run() {
        while (!this.shutdown) {
            this.managedBlock(() -> this.shutdown);
        }
    }

    @Override
    protected void waitForTasks() {
        LockSupport.park((Object)"waiting for tasks");
    }

    public void flush() {
        this.shutdown = true;
        this.thread.interrupt();
        try {
            this.thread.join();
        }
        catch (InterruptedException $$0) {
            Thread.currentThread().interrupt();
        }
        this.dropAllTasks();
        this.shutdown = false;
        this.thread = this.createThread();
    }
}