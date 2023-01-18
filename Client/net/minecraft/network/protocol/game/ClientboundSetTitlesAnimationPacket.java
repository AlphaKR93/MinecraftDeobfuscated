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
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTitlesAnimationPacket
implements Packet<ClientGamePacketListener> {
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public ClientboundSetTitlesAnimationPacket(int $$0, int $$1, int $$2) {
        this.fadeIn = $$0;
        this.stay = $$1;
        this.fadeOut = $$2;
    }

    public ClientboundSetTitlesAnimationPacket(FriendlyByteBuf $$0) {
        this.fadeIn = $$0.readInt();
        this.stay = $$0.readInt();
        this.fadeOut = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.fadeIn);
        $$0.writeInt(this.stay);
        $$0.writeInt(this.fadeOut);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.setTitlesAnimation(this);
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }
}