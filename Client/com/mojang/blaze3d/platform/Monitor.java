/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.Optional
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.glfw.GLFWVidMode$Buffer
 */
package com.mojang.blaze3d.platform;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public final class Monitor {
    private final long monitor;
    private final List<VideoMode> videoModes;
    private VideoMode currentMode;
    private int x;
    private int y;

    public Monitor(long $$0) {
        this.monitor = $$0;
        this.videoModes = Lists.newArrayList();
        this.refreshVideoModes();
    }

    public void refreshVideoModes() {
        RenderSystem.assertInInitPhase();
        this.videoModes.clear();
        GLFWVidMode.Buffer $$0 = GLFW.glfwGetVideoModes((long)this.monitor);
        for (int $$1 = $$0.limit() - 1; $$1 >= 0; --$$1) {
            $$0.position($$1);
            VideoMode $$2 = new VideoMode($$0);
            if ($$2.getRedBits() < 8 || $$2.getGreenBits() < 8 || $$2.getBlueBits() < 8) continue;
            this.videoModes.add((Object)$$2);
        }
        int[] $$3 = new int[1];
        int[] $$4 = new int[1];
        GLFW.glfwGetMonitorPos((long)this.monitor, (int[])$$3, (int[])$$4);
        this.x = $$3[0];
        this.y = $$4[0];
        GLFWVidMode $$5 = GLFW.glfwGetVideoMode((long)this.monitor);
        this.currentMode = new VideoMode($$5);
    }

    public VideoMode getPreferredVidMode(Optional<VideoMode> $$0) {
        RenderSystem.assertInInitPhase();
        if ($$0.isPresent()) {
            VideoMode $$1 = (VideoMode)$$0.get();
            for (VideoMode $$2 : this.videoModes) {
                if (!$$2.equals($$1)) continue;
                return $$2;
            }
        }
        return this.getCurrentMode();
    }

    public int getVideoModeIndex(VideoMode $$0) {
        RenderSystem.assertInInitPhase();
        return this.videoModes.indexOf((Object)$$0);
    }

    public VideoMode getCurrentMode() {
        return this.currentMode;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public VideoMode getMode(int $$0) {
        return (VideoMode)this.videoModes.get($$0);
    }

    public int getModeCount() {
        return this.videoModes.size();
    }

    public long getMonitor() {
        return this.monitor;
    }

    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"Monitor[%s %sx%s %s]", (Object[])new Object[]{this.monitor, this.x, this.y, this.currentMode});
    }
}