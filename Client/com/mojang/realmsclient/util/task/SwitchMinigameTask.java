/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class SwitchMinigameTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long worldId;
    private final WorldTemplate worldTemplate;
    private final RealmsConfigureWorldScreen lastScreen;

    public SwitchMinigameTask(long $$0, WorldTemplate $$1, RealmsConfigureWorldScreen $$2) {
        this.worldId = $$0;
        this.worldTemplate = $$1;
        this.lastScreen = $$2;
    }

    public void run() {
        RealmsClient $$0 = RealmsClient.create();
        this.setTitle(Component.translatable("mco.minigame.world.starting.screen.title"));
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!$$0.putIntoMinigameMode(this.worldId, this.worldTemplate.id).booleanValue()) continue;
                SwitchMinigameTask.setScreen(this.lastScreen);
                break;
            }
            catch (RetryCallException $$2) {
                if (this.aborted()) {
                    return;
                }
                SwitchMinigameTask.pause($$2.delaySeconds);
                continue;
            }
            catch (Exception $$3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't start mini game!");
                this.error($$3.toString());
            }
        }
    }
}