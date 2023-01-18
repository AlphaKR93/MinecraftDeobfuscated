/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket
implements Packet<ClientGamePacketListener> {
    private final int mapId;
    private final byte scale;
    private final boolean locked;
    @Nullable
    private final List<MapDecoration> decorations;
    @Nullable
    private final MapItemSavedData.MapPatch colorPatch;

    public ClientboundMapItemDataPacket(int $$0, byte $$1, boolean $$2, @Nullable Collection<MapDecoration> $$3, @Nullable MapItemSavedData.MapPatch $$4) {
        this.mapId = $$0;
        this.scale = $$1;
        this.locked = $$2;
        this.decorations = $$3 != null ? Lists.newArrayList($$3) : null;
        this.colorPatch = $$4;
    }

    public ClientboundMapItemDataPacket(FriendlyByteBuf $$0) {
        this.mapId = $$0.readVarInt();
        this.scale = $$0.readByte();
        this.locked = $$0.readBoolean();
        this.decorations = (List)$$0.readNullable($$02 -> $$02.readList($$0 -> {
            MapDecoration.Type $$1 = $$0.readEnum(MapDecoration.Type.class);
            byte $$2 = $$0.readByte();
            byte $$3 = $$0.readByte();
            byte $$4 = (byte)($$0.readByte() & 0xF);
            Component $$5 = (Component)$$0.readNullable(FriendlyByteBuf::readComponent);
            return new MapDecoration($$1, $$2, $$3, $$4, $$5);
        }));
        short $$1 = $$0.readUnsignedByte();
        if ($$1 > 0) {
            short $$2 = $$0.readUnsignedByte();
            short $$3 = $$0.readUnsignedByte();
            short $$4 = $$0.readUnsignedByte();
            byte[] $$5 = $$0.readByteArray();
            this.colorPatch = new MapItemSavedData.MapPatch($$3, $$4, $$1, $$2, $$5);
        } else {
            this.colorPatch = null;
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.mapId);
        $$0.writeByte(this.scale);
        $$0.writeBoolean(this.locked);
        $$0.writeNullable(this.decorations, ($$02, $$12) -> $$02.writeCollection($$12, ($$0, $$1) -> {
            $$0.writeEnum($$1.getType());
            $$0.writeByte($$1.getX());
            $$0.writeByte($$1.getY());
            $$0.writeByte($$1.getRot() & 0xF);
            $$0.writeNullable($$1.getName(), FriendlyByteBuf::writeComponent);
        }));
        if (this.colorPatch != null) {
            $$0.writeByte(this.colorPatch.width);
            $$0.writeByte(this.colorPatch.height);
            $$0.writeByte(this.colorPatch.startX);
            $$0.writeByte(this.colorPatch.startY);
            $$0.writeByteArray(this.colorPatch.mapColors);
        } else {
            $$0.writeByte(0);
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleMapItemData(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void applyToMap(MapItemSavedData $$0) {
        if (this.decorations != null) {
            $$0.addClientSideDecorations(this.decorations);
        }
        if (this.colorPatch != null) {
            this.colorPatch.applyToMap($$0);
        }
    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }
}