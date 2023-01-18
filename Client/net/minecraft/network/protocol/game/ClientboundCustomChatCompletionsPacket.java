/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) implements Packet<ClientGamePacketListener>
{
    public ClientboundCustomChatCompletionsPacket(FriendlyByteBuf $$0) {
        this($$0.readEnum(Action.class), $$0.readList(FriendlyByteBuf::readUtf));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
        $$0.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleCustomChatCompletions(this);
    }

    public static enum Action {
        ADD,
        REMOVE,
        SET;

    }
}