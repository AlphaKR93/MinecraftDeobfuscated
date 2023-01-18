/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet<ClientGamePacketListener>
{
    public ClientboundDeleteChatPacket(FriendlyByteBuf $$0) {
        this(MessageSignature.Packed.read($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        MessageSignature.Packed.write($$0, this.messageSignature);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleDeleteChat(this);
    }
}