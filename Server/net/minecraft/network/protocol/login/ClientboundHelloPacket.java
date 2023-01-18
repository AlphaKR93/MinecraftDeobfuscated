/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.security.PublicKey
 */
package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket
implements Packet<ClientLoginPacketListener> {
    private final String serverId;
    private final byte[] publicKey;
    private final byte[] challenge;

    public ClientboundHelloPacket(String $$0, byte[] $$1, byte[] $$2) {
        this.serverId = $$0;
        this.publicKey = $$1;
        this.challenge = $$2;
    }

    public ClientboundHelloPacket(FriendlyByteBuf $$0) {
        this.serverId = $$0.readUtf(20);
        this.publicKey = $$0.readByteArray();
        this.challenge = $$0.readByteArray();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.serverId);
        $$0.writeByteArray(this.publicKey);
        $$0.writeByteArray(this.challenge);
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws CryptException {
        return Crypt.byteToPublicKey(this.publicKey);
    }

    public byte[] getChallenge() {
        return this.challenge;
    }
}