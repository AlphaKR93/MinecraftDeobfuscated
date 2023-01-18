/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Thread
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public abstract class LongRunningTask
implements ErrorCallback,
Runnable {
    protected static final int NUMBER_OF_RETRIES = 25;
    private static final Logger LOGGER = LogUtils.getLogger();
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

    protected static void pause(long $$0) {
        try {
            Thread.sleep((long)($$0 * 1000L));
        }
        catch (InterruptedException $$1) {
            Thread.currentThread().interrupt();
            LOGGER.error("", (Throwable)$$1);
        }
    }

    public static void setScreen(Screen $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        $$1.execute(() -> $$1.setScreen($$0));
    }

    public void setScreen(RealmsLongRunningMcoTaskScreen $$0) {
        this.longRunningMcoTaskScreen = $$0;
    }

    @Override
    public void error(Component $$0) {
        this.longRunningMcoTaskScreen.error($$0);
    }

    public void setTitle(Component $$0) {
        this.longRunningMcoTaskScreen.setTitle($$0);
    }

    public boolean aborted() {
        return this.longRunningMcoTaskScreen.aborted();
    }

    public void tick() {
    }

    public void init() {
    }

    public void abortTask() {
    }
}