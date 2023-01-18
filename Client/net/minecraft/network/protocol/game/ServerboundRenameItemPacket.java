/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundRenameItemPacket
implements Packet<ServerGamePacketListener> {
    private final String name;

    public ServerboundRenameItemPacket(String $$0) {
        this.name = $$0;
    }

    public ServerboundRenameItemPacket(FriendlyByteBuf $$0) {
        this.name = $$0.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.name);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleRenameItem(this);
    }

    public String getName() {
        return this.name;
    }
}