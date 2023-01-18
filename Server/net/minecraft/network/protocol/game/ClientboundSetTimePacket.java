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
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTimePacket
implements Packet<ClientGamePacketListener> {
    private final long gameTime;
    private final long dayTime;

    public ClientboundSetTimePacket(long $$0, long $$1, boolean $$2) {
        this.gameTime = $$0;
        long $$3 = $$1;
        if (!$$2 && ($$3 = -$$3) == 0L) {
            $$3 = -1L;
        }
        this.dayTime = $$3;
    }

    public ClientboundSetTimePacket(FriendlyByteBuf $$0) {
        this.gameTime = $$0.readLong();
        this.dayTime = $$0.readLong();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeLong(this.gameTime);
        $$0.writeLong(this.dayTime);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetTime(this);
    }

    public long getGameTime() {
        return this.gameTime;
    }

    public long getDayTime() {
        return this.dayTime;
    }
}