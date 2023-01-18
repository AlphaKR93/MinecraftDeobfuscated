/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.BitSet
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket
implements Packet<ClientGamePacketListener> {
    private final int x;
    private final int z;
    private final ClientboundLightUpdatePacketData lightData;

    public ClientboundLightUpdatePacket(ChunkPos $$0, LevelLightEngine $$1, @Nullable BitSet $$2, @Nullable BitSet $$3, boolean $$4) {
        this.x = $$0.x;
        this.z = $$0.z;
        this.lightData = new ClientboundLightUpdatePacketData($$0, $$1, $$2, $$3, $$4);
    }

    public ClientboundLightUpdatePacket(FriendlyByteBuf $$0) {
        this.x = $$0.readVarInt();
        this.z = $$0.readVarInt();
        this.lightData = new ClientboundLightUpdatePacketData($$0, this.x, this.z);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.x);
        $$0.writeVarInt(this.z);
        this.lightData.write($$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleLightUpdatePacket(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ClientboundLightUpdatePacketData getLightData() {
        return this.lightData;
    }
}