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

public class ServerboundContainerButtonClickPacket
implements Packet<ServerGamePacketListener> {
    private final int containerId;
    private final int buttonId;

    public ServerboundContainerButtonClickPacket(int $$0, int $$1) {
        this.containerId = $$0;
        this.buttonId = $$1;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleContainerButtonClick(this);
    }

    public ServerboundContainerButtonClickPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readByte();
        this.buttonId = $$0.readByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeByte(this.buttonId);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getButtonId() {
        return this.buttonId;
    }
}