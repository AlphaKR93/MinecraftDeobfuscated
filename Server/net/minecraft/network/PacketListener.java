/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

public interface PacketListener {
    public void onDisconnect(Component var1);

    public Connection getConnection();

    default public boolean shouldPropagateHandlingExceptions() {
        return true;
    }
}