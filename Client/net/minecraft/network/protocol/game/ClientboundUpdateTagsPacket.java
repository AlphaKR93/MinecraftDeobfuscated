/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.network.protocol.game;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

public class ClientboundUpdateTagsPacket
implements Packet<ClientGamePacketListener> {
    private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags;

    public ClientboundUpdateTagsPacket(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> $$0) {
        this.tags = $$0;
    }

    public ClientboundUpdateTagsPacket(FriendlyByteBuf $$02) {
        this.tags = $$02.readMap($$0 -> ResourceKey.createRegistryKey($$0.readResourceLocation()), TagNetworkSerialization.NetworkPayload::read);
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeMap(this.tags, ($$0, $$1) -> $$0.writeResourceLocation($$1.location()), ($$0, $$1) -> $$1.write((FriendlyByteBuf)((Object)$$0)));
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleUpdateTags(this);
    }

    public Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> getTags() {
        return this.tags;
    }
}