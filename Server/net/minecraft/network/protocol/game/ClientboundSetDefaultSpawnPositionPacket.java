/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetDefaultSpawnPositionPacket
implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;
    private final float angle;

    public ClientboundSetDefaultSpawnPositionPacket(BlockPos $$0, float $$1) {
        this.pos = $$0;
        this.angle = $$1;
    }

    public ClientboundSetDefaultSpawnPositionPacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.angle = $$0.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeFloat(this.angle);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetSpawn(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public float getAngle() {
        return this.angle;
    }
}