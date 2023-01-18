/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ClientboundSystemChatPacket(Component content, boolean overlay) implements Packet<ClientGamePacketListener>
{
    public ClientboundSystemChatPacket(FriendlyByteBuf $$0) {
        this($$0.readComponent(), $$0.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.content);
        $$0.writeBoolean(this.overlay);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSystemChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}