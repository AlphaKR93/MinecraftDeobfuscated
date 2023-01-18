/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundPlayerChatPacket(UUID sender, int index, @Nullable MessageSignature signature, SignedMessageBody.Packed body, @Nullable Component unsignedContent, FilterMask filterMask, ChatType.BoundNetwork chatType) implements Packet<ClientGamePacketListener>
{
    public ClientboundPlayerChatPacket(FriendlyByteBuf $$0) {
        this($$0.readUUID(), $$0.readVarInt(), (MessageSignature)((Object)$$0.readNullable(MessageSignature::read)), new SignedMessageBody.Packed($$0), (Component)$$0.readNullable(FriendlyByteBuf::readComponent), FilterMask.read($$0), new ChatType.BoundNetwork($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUUID(this.sender);
        $$0.writeVarInt(this.index);
        $$0.writeNullable(this.signature, MessageSignature::write);
        this.body.write($$0);
        $$0.writeNullable(this.unsignedContent, FriendlyByteBuf::writeComponent);
        FilterMask.write($$0, this.filterMask);
        this.chatType.write($$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}