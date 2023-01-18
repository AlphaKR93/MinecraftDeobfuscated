/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.MemoryUtil$MemoryAllocator
 */
package com.mojang.blaze3d.platform;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class MemoryTracker {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator((boolean)false);

    public static ByteBuffer create(int $$0) {
        long $$1 = ALLOCATOR.malloc((long)$$0);
        if ($$1 == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + $$0 + " bytes");
        }
        return MemoryUtil.memByteBuffer((long)$$1, (int)$$0);
    }

    public static ByteBuffer resize(ByteBuffer $$0, int $$1) {
        long $$2 = ALLOCATOR.realloc(MemoryUtil.memAddress0((Buffer)$$0), (long)$$1);
        if ($$2 == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + $$0.capacity() + " bytes to " + $$1 + " bytes");
        }
        return MemoryUtil.memByteBuffer((long)$$2, (int)$$1);
    }
}