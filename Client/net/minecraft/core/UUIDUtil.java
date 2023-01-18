/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.util.UUIDTypeAdapter
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.nio.ByteOrder
 *  java.nio.charset.StandardCharsets
 *  java.util.Arrays
 *  java.util.UUID
 */
package net.minecraft.core;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UUIDTypeAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.Util;

public final class UUIDUtil {
    public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap($$0 -> Util.fixedSize($$0, 4).map(UUIDUtil::uuidFromIntArray), $$0 -> Arrays.stream((int[])UUIDUtil.uuidToIntArray($$0)));
    public static final Codec<UUID> STRING_CODEC = Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)UUID.fromString((String)$$0), (Lifecycle)Lifecycle.stable());
        }
        catch (IllegalArgumentException $$1) {
            return DataResult.error((String)("Invalid UUID " + $$0 + ": " + $$1.getMessage()));
        }
    }, UUID::toString);
    public static Codec<UUID> AUTHLIB_CODEC = Codec.either(CODEC, (Codec)Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)UUIDTypeAdapter.fromString((String)$$0), (Lifecycle)Lifecycle.stable());
        }
        catch (IllegalArgumentException $$1) {
            return DataResult.error((String)("Invalid UUID " + $$0 + ": " + $$1.getMessage()));
        }
    }, UUIDTypeAdapter::fromUUID)).xmap($$02 -> (UUID)$$02.map($$0 -> $$0, $$0 -> $$0), Either::right);
    public static final int UUID_BYTES = 16;
    private static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";

    private UUIDUtil() {
    }

    public static UUID uuidFromIntArray(int[] $$0) {
        return new UUID((long)$$0[0] << 32 | (long)$$0[1] & 0xFFFFFFFFL, (long)$$0[2] << 32 | (long)$$0[3] & 0xFFFFFFFFL);
    }

    public static int[] uuidToIntArray(UUID $$0) {
        long $$1 = $$0.getMostSignificantBits();
        long $$2 = $$0.getLeastSignificantBits();
        return UUIDUtil.leastMostToIntArray($$1, $$2);
    }

    private static int[] leastMostToIntArray(long $$0, long $$1) {
        return new int[]{(int)($$0 >> 32), (int)$$0, (int)($$1 >> 32), (int)$$1};
    }

    public static byte[] uuidToByteArray(UUID $$0) {
        byte[] $$1 = new byte[16];
        ByteBuffer.wrap((byte[])$$1).order(ByteOrder.BIG_ENDIAN).putLong($$0.getMostSignificantBits()).putLong($$0.getLeastSignificantBits());
        return $$1;
    }

    public static UUID readUUID(Dynamic<?> $$0) {
        int[] $$1 = $$0.asIntStream().toArray();
        if ($$1.length != 4) {
            throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + $$1.length + ".");
        }
        return UUIDUtil.uuidFromIntArray($$1);
    }

    public static UUID getOrCreatePlayerUUID(GameProfile $$0) {
        UUID $$1 = $$0.getId();
        if ($$1 == null) {
            $$1 = UUIDUtil.createOfflinePlayerUUID($$0.getName());
        }
        return $$1;
    }

    public static UUID createOfflinePlayerUUID(String $$0) {
        return UUID.nameUUIDFromBytes((byte[])(UUID_PREFIX_OFFLINE_PLAYER + $$0).getBytes(StandardCharsets.UTF_8));
    }
}