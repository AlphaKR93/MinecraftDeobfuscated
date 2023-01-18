/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Set
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket
implements Packet<ClientGamePacketListener> {
    private final boolean reset;
    private final Map<ResourceLocation, Advancement.Builder> added;
    private final Set<ResourceLocation> removed;
    private final Map<ResourceLocation, AdvancementProgress> progress;

    public ClientboundUpdateAdvancementsPacket(boolean $$0, Collection<Advancement> $$1, Set<ResourceLocation> $$2, Map<ResourceLocation, AdvancementProgress> $$3) {
        this.reset = $$0;
        ImmutableMap.Builder $$4 = ImmutableMap.builder();
        for (Advancement $$5 : $$1) {
            $$4.put((Object)$$5.getId(), (Object)$$5.deconstruct());
        }
        this.added = $$4.build();
        this.removed = ImmutableSet.copyOf($$2);
        this.progress = ImmutableMap.copyOf($$3);
    }

    public ClientboundUpdateAdvancementsPacket(FriendlyByteBuf $$0) {
        this.reset = $$0.readBoolean();
        this.added = $$0.readMap(FriendlyByteBuf::readResourceLocation, Advancement.Builder::fromNetwork);
        this.removed = (Set)$$0.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readResourceLocation);
        this.progress = $$0.readMap(FriendlyByteBuf::readResourceLocation, AdvancementProgress::fromNetwork);
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeBoolean(this.reset);
        $$02.writeMap(this.added, FriendlyByteBuf::writeResourceLocation, ($$0, $$1) -> $$1.serializeToNetwork((FriendlyByteBuf)((Object)$$0)));
        $$02.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
        $$02.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, ($$0, $$1) -> $$1.serializeToNetwork((FriendlyByteBuf)((Object)$$0)));
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleUpdateAdvancementsPacket(this);
    }

    public Map<ResourceLocation, Advancement.Builder> getAdded() {
        return this.added;
    }

    public Set<ResourceLocation> getRemoved() {
        return this.removed;
    }

    public Map<ResourceLocation, AdvancementProgress> getProgress() {
        return this.progress;
    }

    public boolean shouldReset() {
        return this.reset;
    }
}