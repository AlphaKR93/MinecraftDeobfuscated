/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.Throwable
 *  java.nio.channels.FileChannel
 *  java.util.concurrent.Executor
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.minecraft.client.telemetry.TelemetryEventInstance;
import net.minecraft.client.telemetry.TelemetryEventLogger;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ProcessorMailbox;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TelemetryEventLog
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final JsonEventLog<TelemetryEventInstance> log;
    private final ProcessorMailbox<Runnable> mailbox;

    public TelemetryEventLog(FileChannel $$0, Executor $$1) {
        this.log = new JsonEventLog<TelemetryEventInstance>(TelemetryEventInstance.CODEC, $$0);
        this.mailbox = ProcessorMailbox.create($$1, "telemetry-event-log");
    }

    public TelemetryEventLogger logger() {
        return $$0 -> this.mailbox.tell(() -> {
            try {
                this.log.write($$0);
            }
            catch (IOException $$1) {
                LOGGER.error("Failed to write telemetry event to log", (Throwable)$$1);
            }
        });
    }

    public void close() {
        this.mailbox.tell(() -> IOUtils.closeQuietly(this.log));
        this.mailbox.close();
    }
}