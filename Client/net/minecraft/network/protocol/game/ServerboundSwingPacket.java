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
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket
implements Packet<ServerGamePacketListener> {
    private final InteractionHand hand;

    public ServerboundSwingPacket(InteractionHand $$0) {
        this.hand = $$0;
    }

    public ServerboundSwingPacket(FriendlyByteBuf $$0) {
        this.hand = $$0.readEnum(InteractionHand.class);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.hand);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleAnimate(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}