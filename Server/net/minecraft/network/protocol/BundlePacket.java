/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public abstract class BundlePacket<T extends PacketListener>
implements Packet<T> {
    private final Iterable<Packet<T>> packets;

    protected BundlePacket(Iterable<Packet<T>> $$0) {
        this.packets = $$0;
    }

    public final Iterable<Packet<T>> subPackets() {
        return this.packets;
    }

    @Override
    public final void write(FriendlyByteBuf $$0) {
    }
}