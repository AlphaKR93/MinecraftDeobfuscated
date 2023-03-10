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
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket
implements Packet<ServerGamePacketListener> {
    private static final int FLAG_FLYING = 2;
    private final boolean isFlying;

    public ServerboundPlayerAbilitiesPacket(Abilities $$0) {
        this.isFlying = $$0.flying;
    }

    public ServerboundPlayerAbilitiesPacket(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        this.isFlying = ($$1 & 2) != 0;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        int $$1 = 0;
        if (this.isFlying) {
            $$1 = (byte)($$1 | 2);
        }
        $$0.writeByte($$1);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlayerAbilities(this);
    }

    public boolean isFlying() {
        return this.isFlying;
    }
}