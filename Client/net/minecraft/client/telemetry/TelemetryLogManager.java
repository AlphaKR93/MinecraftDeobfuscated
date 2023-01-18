/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.channels.FileChannel
 *  java.nio.file.Path
 *  java.time.LocalDate
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.telemetry.TelemetryEventLog;
import net.minecraft.client.telemetry.TelemetryEventLogger;
import net.minecraft.util.eventlog.EventLogDirectory;
import org.slf4j.Logger;

public class TelemetryLogManager
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RAW_EXTENSION = ".json";
    private static final int EXPIRY_DAYS = 7;
    private final EventLogDirectory directory;
    @Nullable
    private CompletableFuture<Optional<TelemetryEventLog>> sessionLog;

    private TelemetryLogManager(EventLogDirectory $$0) {
        this.directory = $$0;
    }

    public static CompletableFuture<Optional<TelemetryLogManager>> open(Path $$0) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                EventLogDirectory $$1 = EventLogDirectory.open($$0, RAW_EXTENSION);
                $$1.listFiles().prune(LocalDate.now(), 7).compressAll();
                return Optional.of((Object)new TelemetryLogManager($$1));
            }
            catch (Exception $$2) {
                LOGGER.error("Failed to create telemetry log manager", (Throwable)$$2);
                return Optional.empty();
            }
        }, (Executor)Util.backgroundExecutor());
    }

    public CompletableFuture<Optional<TelemetryEventLogger>> openLogger() {
        if (this.sessionLog == null) {
            this.sessionLog = CompletableFuture.supplyAsync(() -> {
                try {
                    EventLogDirectory.RawFile $$0 = this.directory.createNewFile(LocalDate.now());
                    FileChannel $$1 = $$0.openChannel();
                    return Optional.of((Object)new TelemetryEventLog($$1, (Executor)Util.backgroundExecutor()));
                }
                catch (IOException $$2) {
                    LOGGER.error("Failed to open channel for telemetry event log", (Throwable)$$2);
                    return Optional.empty();
                }
            }, (Executor)Util.backgroundExecutor());
        }
        return this.sessionLog.thenApply($$0 -> $$0.map(TelemetryEventLog::logger));
    }

    public void close() {
        if (this.sessionLog != null) {
            this.sessionLog.thenAccept($$0 -> $$0.ifPresent(TelemetryEventLog::close));
        }
    }
}