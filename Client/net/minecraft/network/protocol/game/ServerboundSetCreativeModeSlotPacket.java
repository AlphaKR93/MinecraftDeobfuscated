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
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket
implements Packet<ServerGamePacketListener> {
    private final int slotNum;
    private final ItemStack itemStack;

    public ServerboundSetCreativeModeSlotPacket(int $$0, ItemStack $$1) {
        this.slotNum = $$0;
        this.itemStack = $$1.copy();
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSetCreativeModeSlot(this);
    }

    public ServerboundSetCreativeModeSlotPacket(FriendlyByteBuf $$0) {
        this.slotNum = $$0.readShort();
        this.itemStack = $$0.readItem();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeShort(this.slotNum);
        $$0.writeItem(this.itemStack);
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}