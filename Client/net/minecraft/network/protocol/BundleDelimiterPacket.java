/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public class BundleDelimiterPacket<T extends PacketListener>
implements Packet<T> {
    @Override
    public final void write(FriendlyByteBuf $$0) {
    }

    @Override
    public final void handle(T $$0) {
        throw new AssertionError((Object)"This packet should be handled by pipeline");
    }
}