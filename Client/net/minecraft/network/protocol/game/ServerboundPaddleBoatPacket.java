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

public class ServerboundPaddleBoatPacket
implements Packet<ServerGamePacketListener> {
    private final boolean left;
    private final boolean right;

    public ServerboundPaddleBoatPacket(boolean $$0, boolean $$1) {
        this.left = $$0;
        this.right = $$1;
    }

    public ServerboundPaddleBoatPacket(FriendlyByteBuf $$0) {
        this.left = $$0.readBoolean();
        this.right = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBoolean(this.left);
        $$0.writeBoolean(this.right);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePaddleBoat(this);
    }

    public boolean getLeft() {
        return this.left;
    }

    public boolean getRight() {
        return this.right;
    }
}