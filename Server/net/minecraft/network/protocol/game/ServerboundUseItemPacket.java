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

public class ServerboundUseItemPacket
implements Packet<ServerGamePacketListener> {
    private final InteractionHand hand;
    private final int sequence;

    public ServerboundUseItemPacket(InteractionHand $$0, int $$1) {
        this.hand = $$0;
        this.sequence = $$1;
    }

    public ServerboundUseItemPacket(FriendlyByteBuf $$0) {
        this.hand = $$0.readEnum(InteractionHand.class);
        this.sequence = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.hand);
        $$0.writeVarInt(this.sequence);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleUseItem(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getSequence() {
        return this.sequence;
    }
}