/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket
implements Packet<ClientGamePacketListener> {
    private static final byte CONTINUE_MASK = -128;
    private final int entity;
    private final List<Pair<EquipmentSlot, ItemStack>> slots;

    public ClientboundSetEquipmentPacket(int $$0, List<Pair<EquipmentSlot, ItemStack>> $$1) {
        this.entity = $$0;
        this.slots = $$1;
    }

    public ClientboundSetEquipmentPacket(FriendlyByteBuf $$0) {
        byte $$2;
        this.entity = $$0.readVarInt();
        EquipmentSlot[] $$1 = EquipmentSlot.values();
        this.slots = Lists.newArrayList();
        do {
            $$2 = $$0.readByte();
            EquipmentSlot $$3 = $$1[$$2 & 0x7F];
            ItemStack $$4 = $$0.readItem();
            this.slots.add((Object)Pair.of((Object)((Object)$$3), (Object)$$4));
        } while (($$2 & 0xFFFFFF80) != 0);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entity);
        int $$1 = this.slots.size();
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            Pair $$3 = (Pair)this.slots.get($$2);
            EquipmentSlot $$4 = (EquipmentSlot)((Object)$$3.getFirst());
            boolean $$5 = $$2 != $$1 - 1;
            int $$6 = $$4.ordinal();
            $$0.writeByte($$5 ? $$6 | 0xFFFFFF80 : $$6);
            $$0.writeItem((ItemStack)$$3.getSecond());
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetEquipment(this);
    }

    public int getEntity() {
        return this.entity;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
        return this.slots;
    }
}