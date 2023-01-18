/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundLoginDisconnectPacket
implements Packet<ClientLoginPacketListener> {
    private final Component reason;

    public ClientboundLoginDisconnectPacket(Component $$0) {
        this.reason = $$0;
    }

    public ClientboundLoginDisconnectPacket(FriendlyByteBuf $$0) {
        this.reason = Component.Serializer.fromJsonLenient($$0.readUtf(262144));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.reason);
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleDisconnect(this);
    }

    public Component getReason() {
        return this.reason;
    }
}