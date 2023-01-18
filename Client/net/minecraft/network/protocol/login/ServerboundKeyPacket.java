/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.security.Key
 *  java.security.PrivateKey
 *  java.security.PublicKey
 *  java.util.Arrays
 *  javax.crypto.SecretKey
 */
package net.minecraft.network.protocol.login;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ServerboundKeyPacket
implements Packet<ServerLoginPacketListener> {
    private final byte[] keybytes;
    private final byte[] encryptedChallenge;

    public ServerboundKeyPacket(SecretKey $$0, PublicKey $$1, byte[] $$2) throws CryptException {
        this.keybytes = Crypt.encryptUsingKey((Key)$$1, $$0.getEncoded());
        this.encryptedChallenge = Crypt.encryptUsingKey((Key)$$1, $$2);
    }

    public ServerboundKeyPacket(FriendlyByteBuf $$0) {
        this.keybytes = $$0.readByteArray();
        this.encryptedChallenge = $$0.readByteArray();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByteArray(this.keybytes);
        $$0.writeByteArray(this.encryptedChallenge);
    }

    @Override
    public void handle(ServerLoginPacketListener $$0) {
        $$0.handleKey(this);
    }

    public SecretKey getSecretKey(PrivateKey $$0) throws CryptException {
        return Crypt.decryptByteToSecretKey($$0, this.keybytes);
    }

    public boolean isChallengeValid(byte[] $$0, PrivateKey $$1) {
        try {
            return Arrays.equals((byte[])$$0, (byte[])Crypt.decryptUsingKey((Key)$$1, this.encryptedChallenge));
        }
        catch (CryptException $$2) {
            return false;
        }
    }
}