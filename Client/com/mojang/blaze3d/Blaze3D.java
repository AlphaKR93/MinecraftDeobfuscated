/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.concurrent.ConcurrentLinkedQueue
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class Blaze3D {
    public static void process(RenderPipeline $$0, float $$1) {
        ConcurrentLinkedQueue<RenderCall> $$2 = $$0.getRecordingQueue();
    }

    public static void render(RenderPipeline $$0, float $$1) {
        ConcurrentLinkedQueue<RenderCall> $$2 = $$0.getProcessedQueue();
    }

    public static void youJustLostTheGame() {
        MemoryUtil.memSet((long)0L, (int)0, (long)1L);
    }

    public static double getTime() {
        return GLFW.glfwGetTime();
    }
}