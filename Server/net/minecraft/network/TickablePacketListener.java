/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.network;

import net.minecraft.network.PacketListener;

public interface TickablePacketListener
extends PacketListener {
    public void tick();
}