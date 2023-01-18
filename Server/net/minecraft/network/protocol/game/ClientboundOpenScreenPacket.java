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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final MenuType<?> type;
    private final Component title;

    public ClientboundOpenScreenPacket(int $$0, MenuType<?> $$1, Component $$2) {
        this.containerId = $$0;
        this.type = $$1;
        this.title = $$2;
    }

    public ClientboundOpenScreenPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readVarInt();
        this.type = $$0.readById(BuiltInRegistries.MENU);
        this.title = $$0.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.containerId);
        $$0.writeId(BuiltInRegistries.MENU, this.type);
        $$0.writeComponent(this.title);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleOpenScreen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    @Nullable
    public MenuType<?> getType() {
        return this.type;
    }

    public Component getTitle() {
        return this.title;
    }
}