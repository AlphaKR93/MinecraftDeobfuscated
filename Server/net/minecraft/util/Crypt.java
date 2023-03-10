/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Longs
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  it.unimi.dsi.fastutil.bytes.ByteArrays
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  java.security.Key
 *  java.security.KeyFactory
 *  java.security.KeyPair
 *  java.security.KeyPairGenerator
 *  java.security.MessageDigest
 *  java.security.PrivateKey
 *  java.security.PublicKey
 *  java.security.SecureRandom
 *  java.security.spec.AlgorithmParameterSpec
 *  java.security.spec.KeySpec
 *  java.security.spec.PKCS8EncodedKeySpec
 *  java.security.spec.X509EncodedKeySpec
 *  java.util.Base64
 *  java.util.Base64$Encoder
 *  javax.crypto.Cipher
 *  javax.crypto.KeyGenerator
 *  javax.crypto.SecretKey
 *  javax.crypto.spec.IvParameterSpec
 *  javax.crypto.spec.SecretKeySpec
 */
package net.minecraft.util;

import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CryptException;

public class Crypt {
    private static final String SYMMETRIC_ALGORITHM = "AES";
    private static final int SYMMETRIC_BITS = 128;
    private static final String ASYMMETRIC_ALGORITHM = "RSA";
    private static final int ASYMMETRIC_BITS = 1024;
    private static final String BYTE_ENCODING = "ISO_8859_1";
    private static final String HASH_ALGORITHM = "SHA-1";
    public static final String SIGNING_ALGORITHM = "SHA256withRSA";
    public static final int SIGNATURE_BYTES = 256;
    private static final String PEM_RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----";
    public static final String RSA_PUBLIC_KEY_HEADER = "-----BEGIN RSA PUBLIC KEY-----";
    private static final String RSA_PUBLIC_KEY_FOOTER = "-----END RSA PUBLIC KEY-----";
    public static final String MIME_LINE_SEPARATOR = "\n";
    public static final Base64.Encoder MIME_ENCODER = Base64.getMimeEncoder((int)76, (byte[])"\n".getBytes(StandardCharsets.UTF_8));
    public static final Codec<PublicKey> PUBLIC_KEY_CODEC = Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)Crypt.stringToRsaPublicKey($$0));
        }
        catch (CryptException $$1) {
            return DataResult.error((String)$$1.getMessage());
        }
    }, Crypt::rsaPublicKeyToString);
    public static final Codec<PrivateKey> PRIVATE_KEY_CODEC = Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)Crypt.stringToPemRsaPrivateKey($$0));
        }
        catch (CryptException $$1) {
            return DataResult.error((String)$$1.getMessage());
        }
    }, Crypt::pemRsaPrivateKeyToString);

    public static SecretKey generateSecretKey() throws CryptException {
        try {
            KeyGenerator $$0 = KeyGenerator.getInstance((String)SYMMETRIC_ALGORITHM);
            $$0.init(128);
            return $$0.generateKey();
        }
        catch (Exception $$1) {
            throw new CryptException($$1);
        }
    }

    public static KeyPair generateKeyPair() throws CryptException {
        try {
            KeyPairGenerator $$0 = KeyPairGenerator.getInstance((String)ASYMMETRIC_ALGORITHM);
            $$0.initialize(1024);
            return $$0.generateKeyPair();
        }
        catch (Exception $$1) {
            throw new CryptException($$1);
        }
    }

    public static byte[] digestData(String $$0, PublicKey $$1, SecretKey $$2) throws CryptException {
        try {
            return Crypt.digestData($$0.getBytes(BYTE_ENCODING), $$2.getEncoded(), $$1.getEncoded());
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    private static byte[] digestData(byte[] ... $$0) throws Exception {
        MessageDigest $$1 = MessageDigest.getInstance((String)HASH_ALGORITHM);
        for (byte[] $$2 : $$0) {
            $$1.update($$2);
        }
        return $$1.digest();
    }

    private static <T extends Key> T rsaStringToKey(String $$0, String $$1, String $$2, ByteArrayToKeyFunction<T> $$3) throws CryptException {
        int $$4 = $$0.indexOf($$1);
        if ($$4 != -1) {
            int $$5 = $$0.indexOf($$2, $$4 += $$1.length());
            $$0 = $$0.substring($$4, $$5 + 1);
        }
        try {
            return $$3.apply(Base64.getMimeDecoder().decode($$0));
        }
        catch (IllegalArgumentException $$6) {
            throw new CryptException($$6);
        }
    }

    public static PrivateKey stringToPemRsaPrivateKey(String $$0) throws CryptException {
        return Crypt.rsaStringToKey($$0, PEM_RSA_PRIVATE_KEY_HEADER, PEM_RSA_PRIVATE_KEY_FOOTER, Crypt::byteToPrivateKey);
    }

    public static PublicKey stringToRsaPublicKey(String $$0) throws CryptException {
        return Crypt.rsaStringToKey($$0, RSA_PUBLIC_KEY_HEADER, RSA_PUBLIC_KEY_FOOTER, Crypt::byteToPublicKey);
    }

    public static String rsaPublicKeyToString(PublicKey $$0) {
        if (!ASYMMETRIC_ALGORITHM.equals((Object)$$0.getAlgorithm())) {
            throw new IllegalArgumentException("Public key must be RSA");
        }
        return "-----BEGIN RSA PUBLIC KEY-----\n" + MIME_ENCODER.encodeToString($$0.getEncoded()) + "\n-----END RSA PUBLIC KEY-----\n";
    }

    public static String pemRsaPrivateKeyToString(PrivateKey $$0) {
        if (!ASYMMETRIC_ALGORITHM.equals((Object)$$0.getAlgorithm())) {
            throw new IllegalArgumentException("Private key must be RSA");
        }
        return "-----BEGIN RSA PRIVATE KEY-----\n" + MIME_ENCODER.encodeToString($$0.getEncoded()) + "\n-----END RSA PRIVATE KEY-----\n";
    }

    private static PrivateKey byteToPrivateKey(byte[] $$0) throws CryptException {
        try {
            PKCS8EncodedKeySpec $$1 = new PKCS8EncodedKeySpec($$0);
            KeyFactory $$2 = KeyFactory.getInstance((String)ASYMMETRIC_ALGORITHM);
            return $$2.generatePrivate((KeySpec)$$1);
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    public static PublicKey byteToPublicKey(byte[] $$0) throws CryptException {
        try {
            X509EncodedKeySpec $$1 = new X509EncodedKeySpec($$0);
            KeyFactory $$2 = KeyFactory.getInstance((String)ASYMMETRIC_ALGORITHM);
            return $$2.generatePublic((KeySpec)$$1);
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    public static SecretKey decryptByteToSecretKey(PrivateKey $$0, byte[] $$1) throws CryptException {
        byte[] $$2 = Crypt.decryptUsingKey((Key)$$0, $$1);
        try {
            return new SecretKeySpec($$2, SYMMETRIC_ALGORITHM);
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    public static byte[] encryptUsingKey(Key $$0, byte[] $$1) throws CryptException {
        return Crypt.cipherData(1, $$0, $$1);
    }

    public static byte[] decryptUsingKey(Key $$0, byte[] $$1) throws CryptException {
        return Crypt.cipherData(2, $$0, $$1);
    }

    private static byte[] cipherData(int $$0, Key $$1, byte[] $$2) throws CryptException {
        try {
            return Crypt.setupCipher($$0, $$1.getAlgorithm(), $$1).doFinal($$2);
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    private static Cipher setupCipher(int $$0, String $$1, Key $$2) throws Exception {
        Cipher $$3 = Cipher.getInstance((String)$$1);
        $$3.init($$0, $$2);
        return $$3;
    }

    public static Cipher getCipher(int $$0, Key $$1) throws CryptException {
        try {
            Cipher $$2 = Cipher.getInstance((String)"AES/CFB8/NoPadding");
            $$2.init($$0, $$1, (AlgorithmParameterSpec)new IvParameterSpec($$1.getEncoded()));
            return $$2;
        }
        catch (Exception $$3) {
            throw new CryptException($$3);
        }
    }

    static interface ByteArrayToKeyFunction<T extends Key> {
        public T apply(byte[] var1) throws CryptException;
    }

    public record SaltSignaturePair(long salt, byte[] signature) {
        public static final SaltSignaturePair EMPTY = new SaltSignaturePair(0L, ByteArrays.EMPTY_ARRAY);

        public SaltSignaturePair(FriendlyByteBuf $$0) {
            this($$0.readLong(), $$0.readByteArray());
        }

        public boolean isValid() {
            return this.signature.length > 0;
        }

        public static void write(FriendlyByteBuf $$0, SaltSignaturePair $$1) {
            $$0.writeLong($$1.salt);
            $$0.writeByteArray($$1.signature);
        }

        public byte[] saltAsBytes() {
            return Longs.toByteArray((long)this.salt);
        }
    }

    public static class SaltSupplier {
        private static final SecureRandom secureRandom = new SecureRandom();

        public static long getLong() {
            return secureRandom.nextLong();
        }
    }
}