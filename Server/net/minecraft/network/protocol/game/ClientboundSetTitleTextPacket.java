/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTitleTextPacket
implements Packet<ClientGamePacketListener> {
    private final Component text;

    public ClientboundSetTitleTextPacket(Component $$0) {
        this.text = $$0;
    }

    public ClientboundSetTitleTextPacket(FriendlyByteBuf $$0) {
        this.text = $$0.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.text);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.setTitleText(this);
    }

    public Component getText() {
        return this.text;
    }
}