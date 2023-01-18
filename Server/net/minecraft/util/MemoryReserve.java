/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import javax.annotation.Nullable;

public class MemoryReserve {
    @Nullable
    private static byte[] reserve = null;

    public static void allocate() {
        reserve = new byte[0xA00000];
    }

    public static void release() {
        reserve = new byte[0];
    }
}