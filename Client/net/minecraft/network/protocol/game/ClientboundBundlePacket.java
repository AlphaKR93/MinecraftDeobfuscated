/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBundlePacket
extends BundlePacket<ClientGamePacketListener> {
    public ClientboundBundlePacket(Iterable<Packet<ClientGamePacketListener>> $$0) {
        super($$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBundlePacket(this);
    }
}