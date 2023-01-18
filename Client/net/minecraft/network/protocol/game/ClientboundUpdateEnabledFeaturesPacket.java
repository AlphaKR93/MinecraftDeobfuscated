/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.HashSet
 *  java.util.Set
 */
package net.minecraft.network.protocol.game;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientGamePacketListener>
{
    public ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf $$0) {
        this((Set<ResourceLocation>)((Set)$$0.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation)));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleEnabledFeatures(this);
    }
}