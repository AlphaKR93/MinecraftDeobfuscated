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
import net.minecraft.world.Difficulty;

public class ClientboundChangeDifficultyPacket
implements Packet<ClientGamePacketListener> {
    private final Difficulty difficulty;
    private final boolean locked;

    public ClientboundChangeDifficultyPacket(Difficulty $$0, boolean $$1) {
        this.difficulty = $$0;
        this.locked = $$1;
    }

    public ClientboundChangeDifficultyPacket(FriendlyByteBuf $$0) {
        this.difficulty = Difficulty.byId($$0.readUnsignedByte());
        this.locked = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.difficulty.getId());
        $$0.writeBoolean(this.locked);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleChangeDifficulty(this);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}