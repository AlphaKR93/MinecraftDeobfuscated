/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRemoveMobEffectPacket
implements Packet<ClientGamePacketListener> {
    private final int entityId;
    private final MobEffect effect;

    public ClientboundRemoveMobEffectPacket(int $$0, MobEffect $$1) {
        this.entityId = $$0;
        this.effect = $$1;
    }

    public ClientboundRemoveMobEffectPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readVarInt();
        this.effect = $$0.readById(BuiltInRegistries.MOB_EFFECT);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entityId);
        $$0.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRemoveMobEffect(this);
    }

    @Nullable
    public Entity getEntity(Level $$0) {
        return $$0.getEntity(this.entityId);
    }

    @Nullable
    public MobEffect getEffect() {
        return this.effect;
    }
}