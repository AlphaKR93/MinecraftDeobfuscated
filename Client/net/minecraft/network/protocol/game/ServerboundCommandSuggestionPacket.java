/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundCommandSuggestionPacket
implements Packet<ServerGamePacketListener> {
    private final int id;
    private final String command;

    public ServerboundCommandSuggestionPacket(int $$0, String $$1) {
        this.id = $$0;
        this.command = $$1;
    }

    public ServerboundCommandSuggestionPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.command = $$0.readUtf(32500);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeUtf(this.command, 32500);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleCustomCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public String getCommand() {
        return this.command;
    }
}