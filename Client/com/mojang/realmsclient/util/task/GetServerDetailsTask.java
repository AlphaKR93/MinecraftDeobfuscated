/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.net.URL
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.TimeoutException
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import com.mojang.realmsclient.util.task.ConnectTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class GetServerDetailsTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer server;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final ReentrantLock connectLock;

    public GetServerDetailsTask(RealmsMainScreen $$0, Screen $$1, RealmsServer $$2, ReentrantLock $$3) {
        this.lastScreen = $$1;
        this.mainScreen = $$0;
        this.server = $$2;
        this.connectLock = $$3;
    }

    /*
     * WARNING - void declaration
     */
    public void run() {
        void $$6;
        this.setTitle(Component.translatable("mco.connect.connecting"));
        try {
            RealmsServerAddress $$0 = this.fetchServerAddress();
        }
        catch (CancellationException $$1) {
            LOGGER.info("User aborted connecting to realms");
            return;
        }
        catch (RealmsServiceException $$2) {
            switch ($$2.realmsErrorCodeOrDefault(-1)) {
                case 6002: {
                    GetServerDetailsTask.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
                    return;
                }
                case 6006: {
                    boolean $$3 = this.server.ownerUUID.equals((Object)Minecraft.getInstance().getUser().getUuid());
                    GetServerDetailsTask.setScreen($$3 ? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME) : new RealmsGenericErrorScreen(Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen));
                    return;
                }
            }
            this.error($$2.toString());
            LOGGER.error("Couldn't connect to world", (Throwable)$$2);
            return;
        }
        catch (TimeoutException $$4) {
            this.error(Component.translatable("mco.errorMessage.connectionFailure"));
            return;
        }
        catch (Exception $$5) {
            LOGGER.error("Couldn't connect to world", (Throwable)$$5);
            this.error($$5.getLocalizedMessage());
            return;
        }
        boolean $$7 = $$6.resourcePackUrl != null && $$6.resourcePackHash != null;
        RealmsLongRunningMcoTaskScreen $$8 = $$7 ? this.resourcePackDownloadConfirmationScreen((RealmsServerAddress)$$6, (Function<RealmsServerAddress, Screen>)((Function)this::connectScreen)) : this.connectScreen((RealmsServerAddress)$$6);
        GetServerDetailsTask.setScreen($$8);
    }

    private RealmsServerAddress fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
        RealmsClient $$0 = RealmsClient.create();
        for (int $$1 = 0; $$1 < 40; ++$$1) {
            if (this.aborted()) {
                throw new CancellationException();
            }
            try {
                return $$0.join(this.server.id);
            }
            catch (RetryCallException $$2) {
                GetServerDetailsTask.pause($$2.delaySeconds);
                continue;
            }
        }
        throw new TimeoutException();
    }

    public RealmsLongRunningMcoTaskScreen connectScreen(RealmsServerAddress $$0) {
        return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, $$0));
    }

    private RealmsLongConfirmationScreen resourcePackDownloadConfirmationScreen(RealmsServerAddress $$0, Function<RealmsServerAddress, Screen> $$1) {
        BooleanConsumer $$22 = $$2 -> {
            try {
                if (!$$2) {
                    GetServerDetailsTask.setScreen(this.lastScreen);
                    return;
                }
                this.scheduleResourcePackDownload($$0).thenRun(() -> GetServerDetailsTask.setScreen((Screen)$$1.apply((Object)$$0))).exceptionally($$1 -> {
                    Minecraft.getInstance().getDownloadedPackSource().clearServerPack();
                    LOGGER.error("Failed to download resource pack from {}", (Object)$$0, $$1);
                    GetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(Component.literal("Failed to download resource pack!"), this.lastScreen));
                    return null;
                });
            }
            finally {
                if (this.connectLock.isHeldByCurrentThread()) {
                    this.connectLock.unlock();
                }
            }
        };
        return new RealmsLongConfirmationScreen($$22, RealmsLongConfirmationScreen.Type.Info, Component.translatable("mco.configure.world.resourcepack.question.line1"), Component.translatable("mco.configure.world.resourcepack.question.line2"), true);
    }

    private CompletableFuture<?> scheduleResourcePackDownload(RealmsServerAddress $$0) {
        try {
            return Minecraft.getInstance().getDownloadedPackSource().downloadAndSelectResourcePack(new URL($$0.resourcePackUrl), $$0.resourcePackHash, false);
        }
        catch (Exception $$1) {
            CompletableFuture $$2 = new CompletableFuture();
            $$2.completeExceptionally((Throwable)$$1);
            return $$2;
        }
    }
}