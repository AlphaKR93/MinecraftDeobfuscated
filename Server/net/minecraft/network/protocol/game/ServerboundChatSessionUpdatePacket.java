/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener>
{
    public ServerboundChatSessionUpdatePacket(FriendlyByteBuf $$0) {
        this(RemoteChatSession.Data.read($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        RemoteChatSession.Data.write($$0, this.chatSession);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChatSessionUpdate(this);
    }
}