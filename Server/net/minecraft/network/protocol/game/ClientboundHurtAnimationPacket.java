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
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int id, float yaw) implements Packet<ClientGamePacketListener>
{
    public ClientboundHurtAnimationPacket(LivingEntity $$0) {
        this($$0.getId(), $$0.getHurtDir());
    }

    public ClientboundHurtAnimationPacket(FriendlyByteBuf $$0) {
        this($$0.readVarInt(), $$0.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeFloat(this.yaw);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleHurtAnimation(this);
    }
}