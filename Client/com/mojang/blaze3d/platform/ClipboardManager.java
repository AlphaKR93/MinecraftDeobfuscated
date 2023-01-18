/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.util.StringDecomposer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class ClipboardManager {
    public static final int FORMAT_UNAVAILABLE = 65545;
    private final ByteBuffer clipboardScratchBuffer = BufferUtils.createByteBuffer((int)8192);

    public String getClipboard(long $$0, GLFWErrorCallbackI $$1) {
        GLFWErrorCallback $$2 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)$$1);
        String $$3 = GLFW.glfwGetClipboardString((long)$$0);
        $$3 = $$3 != null ? StringDecomposer.filterBrokenSurrogates($$3) : "";
        GLFWErrorCallback $$4 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)$$2);
        if ($$4 != null) {
            $$4.free();
        }
        return $$3;
    }

    private static void pushClipboard(long $$0, ByteBuffer $$1, byte[] $$2) {
        $$1.clear();
        $$1.put($$2);
        $$1.put((byte)0);
        $$1.flip();
        GLFW.glfwSetClipboardString((long)$$0, (ByteBuffer)$$1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setClipboard(long $$0, String $$1) {
        byte[] $$2 = $$1.getBytes(Charsets.UTF_8);
        int $$3 = $$2.length + 1;
        if ($$3 < this.clipboardScratchBuffer.capacity()) {
            ClipboardManager.pushClipboard($$0, this.clipboardScratchBuffer, $$2);
        } else {
            ByteBuffer $$4 = MemoryUtil.memAlloc((int)$$3);
            try {
                ClipboardManager.pushClipboard($$0, $$4, $$2);
            }
            finally {
                MemoryUtil.memFree((Buffer)$$4);
            }
        }
    }
}