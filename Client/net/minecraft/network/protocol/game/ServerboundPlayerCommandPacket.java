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
import net.minecraft.world.entity.Entity;

public class ServerboundPlayerCommandPacket
implements Packet<ServerGamePacketListener> {
    private final int id;
    private final Action action;
    private final int data;

    public ServerboundPlayerCommandPacket(Entity $$0, Action $$1) {
        this($$0, $$1, 0);
    }

    public ServerboundPlayerCommandPacket(Entity $$0, Action $$1, int $$2) {
        this.id = $$0.getId();
        this.action = $$1;
        this.data = $$2;
    }

    public ServerboundPlayerCommandPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.action = $$0.readEnum(Action.class);
        this.data = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeEnum(this.action);
        $$0.writeVarInt(this.data);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlayerCommand(this);
    }

    public int getId() {
        return this.id;
    }

    public Action getAction() {
        return this.action;
    }

    public int getData() {
        return this.data;
    }

    public static enum Action {
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING;

    }
}