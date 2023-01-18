/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundClientCommandPacket
implements Packet<ServerGamePacketListener> {
    private final Action action;

    public ServerboundClientCommandPacket(Action $$0) {
        this.action = $$0;
    }

    public ServerboundClientCommandPacket(FriendlyByteBuf $$0) {
        this.action = $$0.readEnum(Action.class);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleClientCommand(this);
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        PERFORM_RESPAWN,
        REQUEST_STATS;

    }
}