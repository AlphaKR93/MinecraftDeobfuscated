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
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket
implements Packet<ClientGamePacketListener> {
    private final int x;
    private final int z;
    private final ClientboundLevelChunkPacketData chunkData;
    private final ClientboundLightUpdatePacketData lightData;

    public ClientboundLevelChunkWithLightPacket(LevelChunk $$0, LevelLightEngine $$1, @Nullable BitSet $$2, @Nullable BitSet $$3, boolean $$4) {
        ChunkPos $$5 = $$0.getPos();
        this.x = $$5.x;
        this.z = $$5.z;
        this.chunkData = new ClientboundLevelChunkPacketData($$0);
        this.lightData = new ClientboundLightUpdatePacketData($$5, $$1, $$2, $$3, $$4);
    }

    public ClientboundLevelChunkWithLightPacket(FriendlyByteBuf $$0) {
        this.x = $$0.readInt();
        this.z = $$0.readInt();
        this.chunkData = new ClientboundLevelChunkPacketData($$0, this.x, this.z);
        this.lightData = new ClientboundLightUpdatePacketData($$0, this.x, this.z);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.x);
        $$0.writeInt(this.z);
        this.chunkData.write($$0);
        this.lightData.write($$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleLevelChunkWithLight(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ClientboundLevelChunkPacketData getChunkData() {
        return this.chunkData;
    }

    public ClientboundLightUpdatePacketData getLightData() {
        return this.lightData;
    }
}