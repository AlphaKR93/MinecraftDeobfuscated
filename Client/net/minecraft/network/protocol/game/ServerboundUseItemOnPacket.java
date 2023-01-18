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
import net.minecraft.world.phys.BlockHitResult;

public class ServerboundUseItemOnPacket
implements Packet<ServerGamePacketListener> {
    private final BlockHitResult blockHit;
    private final InteractionHand hand;
    private final int sequence;

    public ServerboundUseItemOnPacket(InteractionHand $$0, BlockHitResult $$1, int $$2) {
        this.hand = $$0;
        this.blockHit = $$1;
        this.sequence = $$2;
    }

    public ServerboundUseItemOnPacket(FriendlyByteBuf $$0) {
        this.hand = $$0.readEnum(InteractionHand.class);
        this.blockHit = $$0.readBlockHitResult();
        this.sequence = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.hand);
        $$0.writeBlockHitResult(this.blockHit);
        $$0.writeVarInt(this.sequence);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleUseItemOn(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public BlockHitResult getHitResult() {
        return this.blockHit;
    }

    public int getSequence() {
        return this.sequence;
    }
}