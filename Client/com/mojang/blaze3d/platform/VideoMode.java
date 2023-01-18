/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.glfw.GLFWVidMode$Buffer
 */
package com.mojang.blaze3d.platform;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWVidMode;

public final class VideoMode {
    private final int width;
    private final int height;
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int refreshRate;
    private static final Pattern PATTERN = Pattern.compile((String)"(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

    public VideoMode(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        this.width = $$0;
        this.height = $$1;
        this.redBits = $$2;
        this.greenBits = $$3;
        this.blueBits = $$4;
        this.refreshRate = $$5;
    }

    public VideoMode(GLFWVidMode.Buffer $$0) {
        this.width = $$0.width();
        this.height = $$0.height();
        this.redBits = $$0.redBits();
        this.greenBits = $$0.greenBits();
        this.blueBits = $$0.blueBits();
        this.refreshRate = $$0.refreshRate();
    }

    public VideoMode(GLFWVidMode $$0) {
        this.width = $$0.width();
        this.height = $$0.height();
        this.redBits = $$0.redBits();
        this.greenBits = $$0.greenBits();
        this.blueBits = $$0.blueBits();
        this.refreshRate = $$0.refreshRate();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRedBits() {
        return this.redBits;
    }

    public int getGreenBits() {
        return this.greenBits;
    }

    public int getBlueBits() {
        return this.blueBits;
    }

    public int getRefreshRate() {
        return this.refreshRate;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        VideoMode $$1 = (VideoMode)$$0;
        return this.width == $$1.width && this.height == $$1.height && this.redBits == $$1.redBits && this.greenBits == $$1.greenBits && this.blueBits == $$1.blueBits && this.refreshRate == $$1.refreshRate;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate});
    }

    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"%sx%s@%s (%sbit)", (Object[])new Object[]{this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits});
    }

    public static Optional<VideoMode> read(@Nullable String $$0) {
        if ($$0 == null) {
            return Optional.empty();
        }
        try {
            Matcher $$1 = PATTERN.matcher((CharSequence)$$0);
            if ($$1.matches()) {
                int $$9;
                int $$6;
                int $$2 = Integer.parseInt((String)$$1.group(1));
                int $$3 = Integer.parseInt((String)$$1.group(2));
                String $$4 = $$1.group(3);
                if ($$4 == null) {
                    int $$5 = 60;
                } else {
                    $$6 = Integer.parseInt((String)$$4);
                }
                String $$7 = $$1.group(4);
                if ($$7 == null) {
                    int $$8 = 24;
                } else {
                    $$9 = Integer.parseInt((String)$$7);
                }
                int $$10 = $$9 / 3;
                return Optional.of((Object)new VideoMode($$2, $$3, $$10, $$10, $$10, $$6));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return Optional.empty();
    }

    public String write() {
        return String.format((Locale)Locale.ROOT, (String)"%sx%s@%s:%s", (Object[])new Object[]{this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits});
    }
}