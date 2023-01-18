/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetrySession
 *  com.mojang.authlib.minecraft.UserApiService
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.nio.file.Path
 *  java.time.Duration
 *  java.time.Instant
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.Executor
 *  java.util.concurrent.Executors
 *  java.util.concurrent.atomic.AtomicInteger
 *  javax.annotation.Nullable
 */
package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.telemetry.TelemetryEventInstance;
import net.minecraft.client.telemetry.TelemetryEventLogger;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryLogManager;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;

public class ClientTelemetryManager
implements AutoCloseable {
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor($$0 -> {
        Thread $$1 = new Thread($$0);
        $$1.setName("Telemetry-Sender-#" + THREAD_COUNT.getAndIncrement());
        return $$1;
    });
    private final UserApiService userApiService;
    private final TelemetryPropertyMap deviceSessionProperties;
    private final Path logDirectory;
    private final CompletableFuture<Optional<TelemetryLogManager>> logManager;

    public ClientTelemetryManager(Minecraft $$0, UserApiService $$12, User $$2) {
        this.userApiService = $$12;
        TelemetryPropertyMap.Builder $$3 = TelemetryPropertyMap.builder();
        $$2.getXuid().ifPresent($$1 -> $$3.put(TelemetryProperty.USER_ID, $$1));
        $$2.getClientId().ifPresent($$1 -> $$3.put(TelemetryProperty.CLIENT_ID, $$1));
        $$3.put(TelemetryProperty.MINECRAFT_SESSION_ID, UUID.randomUUID());
        $$3.put(TelemetryProperty.GAME_VERSION, SharedConstants.getCurrentVersion().getId());
        $$3.put(TelemetryProperty.OPERATING_SYSTEM, Util.getPlatform().telemetryName());
        $$3.put(TelemetryProperty.PLATFORM, System.getProperty((String)"os.name"));
        $$3.put(TelemetryProperty.CLIENT_MODDED, Minecraft.checkModStatus().shouldReportAsModified());
        this.deviceSessionProperties = $$3.build();
        this.logDirectory = $$0.gameDirectory.toPath().resolve("logs/telemetry");
        this.logManager = TelemetryLogManager.open(this.logDirectory);
    }

    public WorldSessionTelemetryManager createWorldSessionManager(boolean $$0, @Nullable Duration $$1) {
        return new WorldSessionTelemetryManager(this.createWorldSessionEventSender(), $$0, $$1);
    }

    private TelemetryEventSender createWorldSessionEventSender() {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return TelemetryEventSender.DISABLED;
        }
        TelemetrySession $$02 = this.userApiService.newTelemetrySession(EXECUTOR);
        if (!$$02.isEnabled()) {
            return TelemetryEventSender.DISABLED;
        }
        CompletableFuture $$1 = this.logManager.thenCompose($$0 -> (CompletionStage)$$0.map(TelemetryLogManager::openLogger).orElseGet(() -> CompletableFuture.completedFuture((Object)Optional.empty())));
        return ($$22, $$3) -> {
            if ($$22.isOptIn() && !Minecraft.getInstance().telemetryOptInExtra()) {
                return;
            }
            TelemetryPropertyMap.Builder $$4 = TelemetryPropertyMap.builder();
            $$4.putAll(this.deviceSessionProperties);
            $$4.put(TelemetryProperty.EVENT_TIMESTAMP_UTC, Instant.now());
            $$4.put(TelemetryProperty.OPT_IN, $$22.isOptIn());
            $$3.accept((Object)$$4);
            TelemetryEventInstance $$5 = new TelemetryEventInstance($$22, $$4.build());
            $$1.thenAccept($$2 -> {
                if ($$2.isEmpty()) {
                    return;
                }
                ((TelemetryEventLogger)$$2.get()).log($$5);
                $$5.export($$02).send();
            });
        };
    }

    public Path getLogDirectory() {
        return this.logDirectory;
    }

    public void close() {
        this.logManager.thenAccept($$0 -> $$0.ifPresent(TelemetryLogManager::close));
    }
}