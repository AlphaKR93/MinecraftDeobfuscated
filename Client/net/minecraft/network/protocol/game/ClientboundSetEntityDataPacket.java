/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener>
{
    public static final int EOF_MARKER = 255;

    public ClientboundSetEntityDataPacket(FriendlyByteBuf $$0) {
        this($$0.readVarInt(), ClientboundSetEntityDataPacket.unpack($$0));
    }

    private static void pack(List<SynchedEntityData.DataValue<?>> $$0, FriendlyByteBuf $$1) {
        for (SynchedEntityData.DataValue $$2 : $$0) {
            $$2.write($$1);
        }
        $$1.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf $$0) {
        short $$2;
        ArrayList $$1 = new ArrayList();
        while (($$2 = $$0.readUnsignedByte()) != 255) {
            $$1.add(SynchedEntityData.DataValue.read($$0, $$2));
        }
        return $$1;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        ClientboundSetEntityDataPacket.pack(this.packedItems, $$0);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetEntityData(this);
    }
}