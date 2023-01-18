/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerboundTeleportToEntityPacket
implements Packet<ServerGamePacketListener> {
    private final UUID uuid;

    public ServerboundTeleportToEntityPacket(UUID $$0) {
        this.uuid = $$0;
    }

    public ServerboundTeleportToEntityPacket(FriendlyByteBuf $$0) {
        this.uuid = $$0.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUUID(this.uuid);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleTeleportToEntityPacket(this);
    }

    @Nullable
    public Entity getEntity(ServerLevel $$0) {
        return $$0.getEntity(this.uuid);
    }
}