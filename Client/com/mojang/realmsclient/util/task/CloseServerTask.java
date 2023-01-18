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
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class CloseServerTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer $$0, RealmsConfigureWorldScreen $$1) {
        this.serverData = $$0;
        this.configureScreen = $$1;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.configure.world.closing"));
        RealmsClient $$0 = RealmsClient.create();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean $$2 = $$0.close(this.serverData.id);
                if (!$$2) continue;
                this.configureScreen.stateChanged();
                this.serverData.state = RealmsServer.State.CLOSED;
                CloseServerTask.setScreen(this.configureScreen);
                break;
            }
            catch (RetryCallException $$3) {
                if (this.aborted()) {
                    return;
                }
                CloseServerTask.pause($$3.delaySeconds);
                continue;
            }
            catch (Exception $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to close server", (Throwable)$$4);
                this.error("Failed to close the server");
            }
        }
    }
}