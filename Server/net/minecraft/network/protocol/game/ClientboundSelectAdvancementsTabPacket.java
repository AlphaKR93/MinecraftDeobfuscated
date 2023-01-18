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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket
implements Packet<ClientGamePacketListener> {
    @Nullable
    private final ResourceLocation tab;

    public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation $$0) {
        this.tab = $$0;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSelectAdvancementsTab(this);
    }

    public ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf $$0) {
        this.tab = (ResourceLocation)$$0.readNullable(FriendlyByteBuf::readResourceLocation);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeNullable(this.tab, FriendlyByteBuf::writeResourceLocation);
    }

    @Nullable
    public ResourceLocation getTab() {
        return this.tab;
    }
}