/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.SpriteTicker;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class SpriteContents
implements Stitcher.Entry,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation name;
    final int width;
    final int height;
    private final NativeImage originalImage;
    NativeImage[] byMipLevel;
    @Nullable
    private final AnimatedTexture animatedTexture;

    public SpriteContents(ResourceLocation $$0, FrameSize $$1, NativeImage $$2, AnimationMetadataSection $$3) {
        this.name = $$0;
        this.width = $$1.width();
        this.height = $$1.height();
        this.animatedTexture = this.createAnimatedTexture($$1, $$2.getWidth(), $$2.getHeight(), $$3);
        this.originalImage = $$2;
        this.byMipLevel = new NativeImage[]{this.originalImage};
    }

    public void increaseMipLevel(int $$0) {
        try {
            this.byMipLevel = MipmapGenerator.generateMipLevels(this.byMipLevel, $$0);
        }
        catch (Throwable $$1) {
            CrashReport $$2 = CrashReport.forThrowable($$1, "Generating mipmaps for frame");
            CrashReportCategory $$3 = $$2.addCategory("Sprite being mipmapped");
            $$3.setDetail("First frame", () -> {
                StringBuilder $$0 = new StringBuilder();
                if ($$0.length() > 0) {
                    $$0.append(", ");
                }
                $$0.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
                return $$0.toString();
            });
            CrashReportCategory $$4 = $$2.addCategory("Frame being iterated");
            $$4.setDetail("Sprite name", this.name);
            $$4.setDetail("Sprite size", () -> this.width + " x " + this.height);
            $$4.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
            $$4.setDetail("Mipmap levels", $$0);
            throw new ReportedException($$2);
        }
    }

    private int getFrameCount() {
        return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
    }

    @Nullable
    private AnimatedTexture createAnimatedTexture(FrameSize $$0, int $$1, int $$2, AnimationMetadataSection $$3) {
        int $$4 = $$1 / $$0.width();
        int $$5 = $$2 / $$0.height();
        int $$6 = $$4 * $$5;
        ArrayList $$7 = new ArrayList();
        $$3.forEachFrame((arg_0, arg_1) -> SpriteContents.lambda$createAnimatedTexture$3((List)$$7, arg_0, arg_1));
        if ($$7.isEmpty()) {
            for (int $$8 = 0; $$8 < $$6; ++$$8) {
                $$7.add((Object)new FrameInfo($$8, $$3.getDefaultFrameTime()));
            }
        } else {
            int $$9 = 0;
            IntOpenHashSet $$10 = new IntOpenHashSet();
            Iterator $$11 = $$7.iterator();
            while ($$11.hasNext()) {
                FrameInfo $$12 = (FrameInfo)$$11.next();
                boolean $$13 = true;
                if ($$12.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, $$9, $$12.time});
                    $$13 = false;
                }
                if ($$12.index < 0 || $$12.index >= $$6) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, $$9, $$12.index});
                    $$13 = false;
                }
                if ($$13) {
                    $$10.add($$12.index);
                } else {
                    $$11.remove();
                }
                ++$$9;
            }
            int[] $$14 = IntStream.range((int)0, (int)$$6).filter(arg_0 -> SpriteContents.lambda$createAnimatedTexture$4((IntSet)$$10, arg_0)).toArray();
            if ($$14.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", (Object)this.name, (Object)Arrays.toString((int[])$$14));
            }
        }
        if ($$7.size() <= 1) {
            return null;
        }
        return new AnimatedTexture((List<FrameInfo>)ImmutableList.copyOf((Collection)$$7), $$4, $$3.isInterpolatedFrames());
    }

    void upload(int $$0, int $$1, int $$2, int $$3, NativeImage[] $$4) {
        for (int $$5 = 0; $$5 < this.byMipLevel.length; ++$$5) {
            $$4[$$5].upload($$5, $$0 >> $$5, $$1 >> $$5, $$2 >> $$5, $$3 >> $$5, this.width >> $$5, this.height >> $$5, this.byMipLevel.length > 1, false);
        }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public ResourceLocation name() {
        return this.name;
    }

    public IntStream getUniqueFrames() {
        return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of((int)1);
    }

    @Nullable
    public SpriteTicker createTicker() {
        return this.animatedTexture != null ? this.animatedTexture.createTicker() : null;
    }

    public void close() {
        for (NativeImage $$0 : this.byMipLevel) {
            $$0.close();
        }
    }

    public String toString() {
        return "SpriteContents{name=" + this.name + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isTransparent(int $$0, int $$1, int $$2) {
        int $$3 = $$1;
        int $$4 = $$2;
        if (this.animatedTexture != null) {
            $$3 += this.animatedTexture.getFrameX($$0) * this.width;
            $$4 += this.animatedTexture.getFrameY($$0) * this.height;
        }
        return (this.originalImage.getPixelRGBA($$3, $$4) >> 24 & 0xFF) == 0;
    }

    public void uploadFirstFrame(int $$0, int $$1) {
        if (this.animatedTexture != null) {
            this.animatedTexture.uploadFirstFrame($$0, $$1);
        } else {
            this.upload($$0, $$1, 0, 0, this.byMipLevel);
        }
    }

    private static /* synthetic */ boolean lambda$createAnimatedTexture$4(IntSet $$0, int $$1) {
        return !$$0.contains($$1);
    }

    private static /* synthetic */ void lambda$createAnimatedTexture$3(List $$0, int $$1, int $$2) {
        $$0.add((Object)new FrameInfo($$1, $$2));
    }

    class AnimatedTexture {
        final List<FrameInfo> frames;
        private final int frameRowSize;
        private final boolean interpolateFrames;

        AnimatedTexture(List<FrameInfo> $$0, int $$1, boolean $$2) {
            this.frames = $$0;
            this.frameRowSize = $$1;
            this.interpolateFrames = $$2;
        }

        int getFrameX(int $$0) {
            return $$0 % this.frameRowSize;
        }

        int getFrameY(int $$0) {
            return $$0 / this.frameRowSize;
        }

        void uploadFrame(int $$0, int $$1, int $$2) {
            int $$3 = this.getFrameX($$2) * SpriteContents.this.width;
            int $$4 = this.getFrameY($$2) * SpriteContents.this.height;
            SpriteContents.this.upload($$0, $$1, $$3, $$4, SpriteContents.this.byMipLevel);
        }

        public SpriteTicker createTicker() {
            return new Ticker(this, this.interpolateFrames ? new InterpolationData() : null);
        }

        public void uploadFirstFrame(int $$0, int $$1) {
            this.uploadFrame($$0, $$1, ((FrameInfo)this.frames.get((int)0)).index);
        }

        public IntStream getUniqueFrames() {
            return this.frames.stream().mapToInt($$0 -> $$0.index).distinct();
        }
    }

    static class FrameInfo {
        final int index;
        final int time;

        FrameInfo(int $$0, int $$1) {
            this.index = $$0;
            this.time = $$1;
        }
    }

    class Ticker
    implements SpriteTicker {
        int frame;
        int subFrame;
        final AnimatedTexture animationInfo;
        @Nullable
        private final InterpolationData interpolationData;

        Ticker(@Nullable AnimatedTexture $$0, InterpolationData $$1) {
            this.animationInfo = $$0;
            this.interpolationData = $$1;
        }

        @Override
        public void tickAndUpload(int $$0, int $$1) {
            ++this.subFrame;
            FrameInfo $$2 = (FrameInfo)this.animationInfo.frames.get(this.frame);
            if (this.subFrame >= $$2.time) {
                int $$3 = $$2.index;
                this.frame = (this.frame + 1) % this.animationInfo.frames.size();
                this.subFrame = 0;
                int $$4 = ((FrameInfo)this.animationInfo.frames.get((int)this.frame)).index;
                if ($$3 != $$4) {
                    this.animationInfo.uploadFrame($$0, $$1, $$4);
                }
            } else if (this.interpolationData != null) {
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(() -> this.interpolationData.uploadInterpolatedFrame($$0, $$1, this));
                } else {
                    this.interpolationData.uploadInterpolatedFrame($$0, $$1, this);
                }
            }
        }

        @Override
        public void close() {
            if (this.interpolationData != null) {
                this.interpolationData.close();
            }
        }
    }

    final class InterpolationData
    implements AutoCloseable {
        private final NativeImage[] activeFrame;

        InterpolationData() {
            this.activeFrame = new NativeImage[SpriteContents.this.byMipLevel.length];
            for (int $$0 = 0; $$0 < this.activeFrame.length; ++$$0) {
                int $$1 = SpriteContents.this.width >> $$0;
                int $$2 = SpriteContents.this.height >> $$0;
                this.activeFrame[$$0] = new NativeImage($$1, $$2, false);
            }
        }

        void uploadInterpolatedFrame(int $$0, int $$1, Ticker $$2) {
            AnimatedTexture $$3 = $$2.animationInfo;
            List<FrameInfo> $$4 = $$3.frames;
            FrameInfo $$5 = (FrameInfo)$$4.get($$2.frame);
            double $$6 = 1.0 - (double)$$2.subFrame / (double)$$5.time;
            int $$7 = $$5.index;
            int $$8 = ((FrameInfo)$$4.get((int)(($$2.frame + 1) % $$4.size()))).index;
            if ($$7 != $$8) {
                for (int $$9 = 0; $$9 < this.activeFrame.length; ++$$9) {
                    int $$10 = SpriteContents.this.width >> $$9;
                    int $$11 = SpriteContents.this.height >> $$9;
                    for (int $$12 = 0; $$12 < $$11; ++$$12) {
                        for (int $$13 = 0; $$13 < $$10; ++$$13) {
                            int $$14 = this.getPixel($$3, $$7, $$9, $$13, $$12);
                            int $$15 = this.getPixel($$3, $$8, $$9, $$13, $$12);
                            int $$16 = this.mix($$6, $$14 >> 16 & 0xFF, $$15 >> 16 & 0xFF);
                            int $$17 = this.mix($$6, $$14 >> 8 & 0xFF, $$15 >> 8 & 0xFF);
                            int $$18 = this.mix($$6, $$14 & 0xFF, $$15 & 0xFF);
                            this.activeFrame[$$9].setPixelRGBA($$13, $$12, $$14 & 0xFF000000 | $$16 << 16 | $$17 << 8 | $$18);
                        }
                    }
                }
                SpriteContents.this.upload($$0, $$1, 0, 0, this.activeFrame);
            }
        }

        private int getPixel(AnimatedTexture $$0, int $$1, int $$2, int $$3, int $$4) {
            return SpriteContents.this.byMipLevel[$$2].getPixelRGBA($$3 + ($$0.getFrameX($$1) * SpriteContents.this.width >> $$2), $$4 + ($$0.getFrameY($$1) * SpriteContents.this.height >> $$2));
        }

        private int mix(double $$0, int $$1, int $$2) {
            return (int)($$0 * (double)$$1 + (1.0 - $$0) * (double)$$2);
        }

        public void close() {
            for (NativeImage $$0 : this.activeFrame) {
                $$0.close();
            }
        }
    }
}