/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundDisguisedChatPacket(Component message, ChatType.BoundNetwork chatType) implements Packet<ClientGamePacketListener>
{
    public ClientboundDisguisedChatPacket(FriendlyByteBuf $$0) {
        this($$0.readComponent(), new ChatType.BoundNetwork($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.message);
        this.chatType.write($$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleDisguisedChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}