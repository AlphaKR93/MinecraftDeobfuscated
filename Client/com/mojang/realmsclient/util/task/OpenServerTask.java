/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class OpenServerTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final RealmsMainScreen mainScreen;
    private final Minecraft minecraft;

    public OpenServerTask(RealmsServer $$0, Screen $$1, RealmsMainScreen $$2, boolean $$3, Minecraft $$4) {
        this.serverData = $$0;
        this.returnScreen = $$1;
        this.join = $$3;
        this.mainScreen = $$2;
        this.minecraft = $$4;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.configure.world.opening"));
        RealmsClient $$0 = RealmsClient.create();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean $$2 = $$0.open(this.serverData.id);
                if (!$$2) continue;
                this.minecraft.execute(() -> {
                    if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                        ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                    }
                    this.serverData.state = RealmsServer.State.OPEN;
                    if (this.join) {
                        this.mainScreen.play(this.serverData, this.returnScreen);
                    } else {
                        this.minecraft.setScreen(this.returnScreen);
                    }
                });
                break;
            }
            catch (RetryCallException $$3) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause($$3.delaySeconds);
                continue;
            }
            catch (Exception $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", (Throwable)$$4);
                this.error("Failed to open the server");
            }
        }
    }
}