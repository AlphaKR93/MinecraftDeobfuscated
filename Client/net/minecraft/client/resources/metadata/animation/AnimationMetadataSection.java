/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.FunctionalInterface
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public class AnimationMetadataSection {
    public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
    public static final String SECTION_NAME = "animation";
    public static final int DEFAULT_FRAME_TIME = 1;
    public static final int UNKNOWN_SIZE = -1;
    public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection((List)Lists.newArrayList(), -1, -1, 1, false){

        @Override
        public FrameSize calculateFrameSize(int $$0, int $$1) {
            return new FrameSize($$0, $$1);
        }
    };
    private final List<AnimationFrame> frames;
    private final int frameWidth;
    private final int frameHeight;
    private final int defaultFrameTime;
    private final boolean interpolatedFrames;

    public AnimationMetadataSection(List<AnimationFrame> $$0, int $$1, int $$2, int $$3, boolean $$4) {
        this.frames = $$0;
        this.frameWidth = $$1;
        this.frameHeight = $$2;
        this.defaultFrameTime = $$3;
        this.interpolatedFrames = $$4;
    }

    public FrameSize calculateFrameSize(int $$0, int $$1) {
        if (this.frameWidth != -1) {
            if (this.frameHeight != -1) {
                return new FrameSize(this.frameWidth, this.frameHeight);
            }
            return new FrameSize(this.frameWidth, $$1);
        }
        if (this.frameHeight != -1) {
            return new FrameSize($$0, this.frameHeight);
        }
        int $$2 = Math.min((int)$$0, (int)$$1);
        return new FrameSize($$2, $$2);
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean isInterpolatedFrames() {
        return this.interpolatedFrames;
    }

    public void forEachFrame(FrameOutput $$0) {
        for (AnimationFrame $$1 : this.frames) {
            $$0.accept($$1.getIndex(), $$1.getTime(this.defaultFrameTime));
        }
    }

    @FunctionalInterface
    public static interface FrameOutput {
        public void accept(int var1, int var2);
    }
}