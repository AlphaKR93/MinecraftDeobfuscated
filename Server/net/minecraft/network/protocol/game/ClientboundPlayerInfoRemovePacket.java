/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.UUID
 */
package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener>
{
    public ClientboundPlayerInfoRemovePacket(FriendlyByteBuf $$0) {
        this($$0.readList(FriendlyByteBuf::readUUID));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.profileIds, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerInfoRemove(this);
    }
}