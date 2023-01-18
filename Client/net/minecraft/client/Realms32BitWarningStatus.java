/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.Realms32bitWarningScreen;
import org.slf4j.Logger;

public class Realms32BitWarningStatus {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    @Nullable
    private CompletableFuture<Boolean> subscriptionCheck;
    private boolean warningScreenShown;

    public Realms32BitWarningStatus(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void showRealms32BitWarningIfNeeded(Screen $$0) {
        if (!this.minecraft.is64Bit() && !this.minecraft.options.skipRealms32bitWarning && !this.warningScreenShown && this.checkForRealmsSubscription().booleanValue()) {
            this.minecraft.setScreen(new Realms32bitWarningScreen($$0));
            this.warningScreenShown = true;
        }
    }

    private Boolean checkForRealmsSubscription() {
        if (this.subscriptionCheck == null) {
            this.subscriptionCheck = CompletableFuture.supplyAsync(this::hasRealmsSubscription, (Executor)Util.backgroundExecutor());
        }
        try {
            return (Boolean)this.subscriptionCheck.getNow((Object)false);
        }
        catch (CompletionException $$0) {
            LOGGER.warn("Failed to retrieve realms subscriptions", (Throwable)$$0);
            this.warningScreenShown = true;
            return false;
        }
    }

    private boolean hasRealmsSubscription() {
        try {
            return RealmsClient.create((Minecraft)this.minecraft).listWorlds().servers.stream().anyMatch($$0 -> $$0.ownerUUID != null && !$$0.expired && $$0.ownerUUID.equals((Object)this.minecraft.getUser().getUuid()));
        }
        catch (RealmsServiceException $$02) {
            return false;
        }
    }
}