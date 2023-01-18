/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.Item;

public class ClientboundCooldownPacket
implements Packet<ClientGamePacketListener> {
    private final Item item;
    private final int duration;

    public ClientboundCooldownPacket(Item $$0, int $$1) {
        this.item = $$0;
        this.duration = $$1;
    }

    public ClientboundCooldownPacket(FriendlyByteBuf $$0) {
        this.item = $$0.readById(BuiltInRegistries.ITEM);
        this.duration = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeId(BuiltInRegistries.ITEM, this.item);
        $$0.writeVarInt(this.duration);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleItemCooldown(this);
    }

    public Item getItem() {
        return this.item;
    }

    public int getDuration() {
        return this.duration;
    }
}