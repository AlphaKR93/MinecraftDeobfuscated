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

public class ServerboundResourcePackPacket
implements Packet<ServerGamePacketListener> {
    private final Action action;

    public ServerboundResourcePackPacket(Action $$0) {
        this.action = $$0;
    }

    public ServerboundResourcePackPacket(FriendlyByteBuf $$0) {
        this.action = $$0.readEnum(Action.class);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleResourcePackResponse(this);
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}