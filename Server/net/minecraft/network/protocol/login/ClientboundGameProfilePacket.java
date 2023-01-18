/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundGameProfilePacket
implements Packet<ClientLoginPacketListener> {
    private final GameProfile gameProfile;

    public ClientboundGameProfilePacket(GameProfile $$0) {
        this.gameProfile = $$0;
    }

    public ClientboundGameProfilePacket(FriendlyByteBuf $$0) {
        this.gameProfile = $$0.readGameProfile();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeGameProfile(this.gameProfile);
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleGameProfile(this);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}