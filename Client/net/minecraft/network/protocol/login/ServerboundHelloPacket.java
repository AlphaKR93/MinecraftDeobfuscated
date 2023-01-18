/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.UUID
 */
package net.minecraft.network.protocol.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public record ServerboundHelloPacket(String name, Optional<UUID> profileId) implements Packet<ServerLoginPacketListener>
{
    public ServerboundHelloPacket(FriendlyByteBuf $$0) {
        this($$0.readUtf(16), $$0.readOptional(FriendlyByteBuf::readUUID));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.name, 16);
        $$0.writeOptional(this.profileId, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handle(ServerLoginPacketListener $$0) {
        $$0.handleHello(this);
    }
}