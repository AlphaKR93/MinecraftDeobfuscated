/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.IntFunction
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.IntFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket
implements Packet<ServerGamePacketListener> {
    private static final int MAX_SLOT_COUNT = 128;
    private final int containerId;
    private final int stateId;
    private final int slotNum;
    private final int buttonNum;
    private final ClickType clickType;
    private final ItemStack carriedItem;
    private final Int2ObjectMap<ItemStack> changedSlots;

    public ServerboundContainerClickPacket(int $$0, int $$1, int $$2, int $$3, ClickType $$4, ItemStack $$5, Int2ObjectMap<ItemStack> $$6) {
        this.containerId = $$0;
        this.stateId = $$1;
        this.slotNum = $$2;
        this.buttonNum = $$3;
        this.clickType = $$4;
        this.carriedItem = $$5;
        this.changedSlots = Int2ObjectMaps.unmodifiable($$6);
    }

    public ServerboundContainerClickPacket(FriendlyByteBuf $$02) {
        this.containerId = $$02.readByte();
        this.stateId = $$02.readVarInt();
        this.slotNum = $$02.readShort();
        this.buttonNum = $$02.readByte();
        this.clickType = $$02.readEnum(ClickType.class);
        IntFunction $$1 = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
        this.changedSlots = Int2ObjectMaps.unmodifiable((Int2ObjectMap)((Int2ObjectMap)$$02.readMap($$1, $$0 -> $$0.readShort(), FriendlyByteBuf::readItem)));
        this.carriedItem = $$02.readItem();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.stateId);
        $$0.writeShort(this.slotNum);
        $$0.writeByte(this.buttonNum);
        $$0.writeEnum(this.clickType);
        $$0.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, FriendlyByteBuf::writeItem);
        $$0.writeItem(this.carriedItem);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleContainerClick(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public int getButtonNum() {
        return this.buttonNum;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return this.changedSlots;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public int getStateId() {
        return this.stateId;
    }
}