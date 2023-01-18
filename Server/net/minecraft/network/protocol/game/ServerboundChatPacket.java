/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.time.Instant
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages) implements Packet<ServerGamePacketListener>
{
    public ServerboundChatPacket(FriendlyByteBuf $$0) {
        this($$0.readUtf(256), $$0.readInstant(), $$0.readLong(), (MessageSignature)((Object)$$0.readNullable(MessageSignature::read)), new LastSeenMessages.Update($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.message, 256);
        $$0.writeInstant(this.timeStamp);
        $$0.writeLong(this.salt);
        $$0.writeNullable(this.signature, MessageSignature::write);
        this.lastSeenMessages.write($$0);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChat(this);
    }
}