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

public class ClientboundSetExperiencePacket
implements Packet<ClientGamePacketListener> {
    private final float experienceProgress;
    private final int totalExperience;
    private final int experienceLevel;

    public ClientboundSetExperiencePacket(float $$0, int $$1, int $$2) {
        this.experienceProgress = $$0;
        this.totalExperience = $$1;
        this.experienceLevel = $$2;
    }

    public ClientboundSetExperiencePacket(FriendlyByteBuf $$0) {
        this.experienceProgress = $$0.readFloat();
        this.experienceLevel = $$0.readVarInt();
        this.totalExperience = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.experienceProgress);
        $$0.writeVarInt(this.experienceLevel);
        $$0.writeVarInt(this.totalExperience);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetExperience(this);
    }

    public float getExperienceProgress() {
        return this.experienceProgress;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getExperienceLevel() {
        return this.experienceLevel;
    }
}