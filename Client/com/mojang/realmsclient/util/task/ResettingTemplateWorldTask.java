/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.ResettingWorldTask;
import net.minecraft.network.chat.Component;

public class ResettingTemplateWorldTask
extends ResettingWorldTask {
    private final WorldTemplate template;

    public ResettingTemplateWorldTask(WorldTemplate $$0, long $$1, Component $$2, Runnable $$3) {
        super($$1, $$2, $$3);
        this.template = $$0;
    }

    @Override
    protected void sendResetRequest(RealmsClient $$0, long $$1) throws RealmsServiceException {
        $$0.resetWorldWithTemplate($$1, this.template.id);
    }
}