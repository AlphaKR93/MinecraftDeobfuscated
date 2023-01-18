/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.time.Instant
 */
package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.Update lastSeenMessages) implements Packet<ServerGamePacketListener>
{
    public ServerboundChatCommandPacket(FriendlyByteBuf $$0) {
        this($$0.readUtf(256), $$0.readInstant(), $$0.readLong(), new ArgumentSignatures($$0), new LastSeenMessages.Update($$0));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.command, 256);
        $$0.writeInstant(this.timeStamp);
        $$0.writeLong(this.salt);
        this.argumentSignatures.write($$0);
        this.lastSeenMessages.write($$0);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChatCommand(this);
    }
}