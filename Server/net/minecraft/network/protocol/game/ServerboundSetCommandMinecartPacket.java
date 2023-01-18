/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;

public class ServerboundSetCommandMinecartPacket
implements Packet<ServerGamePacketListener> {
    private final int entity;
    private final String command;
    private final boolean trackOutput;

    public ServerboundSetCommandMinecartPacket(int $$0, String $$1, boolean $$2) {
        this.entity = $$0;
        this.command = $$1;
        this.trackOutput = $$2;
    }

    public ServerboundSetCommandMinecartPacket(FriendlyByteBuf $$0) {
        this.entity = $$0.readVarInt();
        this.command = $$0.readUtf();
        this.trackOutput = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entity);
        $$0.writeUtf(this.command);
        $$0.writeBoolean(this.trackOutput);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSetCommandMinecart(this);
    }

    @Nullable
    public BaseCommandBlock getCommandBlock(Level $$0) {
        Entity $$1 = $$0.getEntity(this.entity);
        if ($$1 instanceof MinecartCommandBlock) {
            return ((MinecartCommandBlock)$$1).getCommandBlock();
        }
        return null;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }
}