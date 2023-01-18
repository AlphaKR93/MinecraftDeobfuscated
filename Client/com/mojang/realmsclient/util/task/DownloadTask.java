/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class DownloadTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long worldId;
    private final int slot;
    private final Screen lastScreen;
    private final String downloadName;

    public DownloadTask(long $$0, int $$1, String $$2, Screen $$3) {
        this.worldId = $$0;
        this.slot = $$1;
        this.lastScreen = $$3;
        this.downloadName = $$2;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.download.preparing"));
        RealmsClient $$02 = RealmsClient.create();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                WorldDownload $$2 = $$02.requestDownloadInfo(this.worldId, this.slot);
                DownloadTask.pause(1L);
                if (this.aborted()) {
                    return;
                }
                DownloadTask.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, $$2, this.downloadName, $$0 -> {}));
                return;
            }
            catch (RetryCallException $$3) {
                if (this.aborted()) {
                    return;
                }
                DownloadTask.pause($$3.delaySeconds);
                continue;
            }
            catch (RealmsServiceException $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data");
                DownloadTask.setScreen(new RealmsGenericErrorScreen($$4, this.lastScreen));
                return;
            }
            catch (Exception $$5) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data", (Throwable)$$5);
                this.error($$5.getLocalizedMessage());
                return;
            }
        }
    }
}