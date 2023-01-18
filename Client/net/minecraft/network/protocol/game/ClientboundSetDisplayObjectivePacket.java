/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket
implements Packet<ClientGamePacketListener> {
    private final int slot;
    private final String objectiveName;

    public ClientboundSetDisplayObjectivePacket(int $$0, @Nullable Objective $$1) {
        this.slot = $$0;
        this.objectiveName = $$1 == null ? "" : $$1.getName();
    }

    public ClientboundSetDisplayObjectivePacket(FriendlyByteBuf $$0) {
        this.slot = $$0.readByte();
        this.objectiveName = $$0.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.slot);
        $$0.writeUtf(this.objectiveName);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetDisplayObjective(this);
    }

    public int getSlot() {
        return this.slot;
    }

    @Nullable
    public String getObjectiveName() {
        return Objects.equals((Object)this.objectiveName, (Object)"") ? null : this.objectiveName;
    }
}