/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class WorldCreationTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final String motd;
    private final long worldId;
    private final Screen lastScreen;

    public WorldCreationTask(long $$0, String $$1, String $$2, Screen $$3) {
        this.worldId = $$0;
        this.name = $$1;
        this.motd = $$2;
        this.lastScreen = $$3;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.create.world.wait"));
        RealmsClient $$0 = RealmsClient.create();
        try {
            $$0.initializeWorld(this.worldId, this.name, this.motd);
            WorldCreationTask.setScreen(this.lastScreen);
        }
        catch (RealmsServiceException $$1) {
            LOGGER.error("Couldn't create world");
            this.error($$1.toString());
        }
        catch (Exception $$2) {
            LOGGER.error("Could not create world");
            this.error($$2.getLocalizedMessage());
        }
    }
}