/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket
implements Packet<ClientGamePacketListener> {
    private final int cameraId;

    public ClientboundSetCameraPacket(Entity $$0) {
        this.cameraId = $$0.getId();
    }

    public ClientboundSetCameraPacket(FriendlyByteBuf $$0) {
        this.cameraId = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.cameraId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetCamera(this);
    }

    @Nullable
    public Entity getEntity(Level $$0) {
        return $$0.getEntity(this.cameraId);
    }
}