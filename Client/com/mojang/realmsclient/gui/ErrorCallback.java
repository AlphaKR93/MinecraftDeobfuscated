/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.gui;

import net.minecraft.network.chat.Component;

public interface ErrorCallback {
    public void error(Component var1);

    default public void error(String $$0) {
        this.error(Component.literal($$0));
    }
}