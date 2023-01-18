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
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket
implements Packet<ServerGamePacketListener> {
    private final Difficulty difficulty;

    public ServerboundChangeDifficultyPacket(Difficulty $$0) {
        this.difficulty = $$0;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChangeDifficulty(this);
    }

    public ServerboundChangeDifficultyPacket(FriendlyByteBuf $$0) {
        this.difficulty = Difficulty.byId($$0.readUnsignedByte());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.difficulty.getId());
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}