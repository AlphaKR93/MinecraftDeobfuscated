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

public class ServerboundLockDifficultyPacket
implements Packet<ServerGamePacketListener> {
    private final boolean locked;

    public ServerboundLockDifficultyPacket(boolean $$0) {
        this.locked = $$0;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleLockDifficulty(this);
    }

    public ServerboundLockDifficultyPacket(FriendlyByteBuf $$0) {
        this.locked = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBoolean(this.locked);
    }

    public boolean isLocked() {
        return this.locked;
    }
}