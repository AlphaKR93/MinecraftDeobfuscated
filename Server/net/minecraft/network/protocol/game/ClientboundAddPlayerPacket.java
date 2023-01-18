/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 */
package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.player.Player;

public class ClientboundAddPlayerPacket
implements Packet<ClientGamePacketListener> {
    private final int entityId;
    private final UUID playerId;
    private final double x;
    private final double y;
    private final double z;
    private final byte yRot;
    private final byte xRot;

    public ClientboundAddPlayerPacket(Player $$0) {
        this.entityId = $$0.getId();
        this.playerId = $$0.getGameProfile().getId();
        this.x = $$0.getX();
        this.y = $$0.getY();
        this.z = $$0.getZ();
        this.yRot = (byte)($$0.getYRot() * 256.0f / 360.0f);
        this.xRot = (byte)($$0.getXRot() * 256.0f / 360.0f);
    }

    public ClientboundAddPlayerPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readVarInt();
        this.playerId = $$0.readUUID();
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.yRot = $$0.readByte();
        this.xRot = $$0.readByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entityId);
        $$0.writeUUID(this.playerId);
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeByte(this.yRot);
        $$0.writeByte(this.xRot);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAddPlayer(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }
}