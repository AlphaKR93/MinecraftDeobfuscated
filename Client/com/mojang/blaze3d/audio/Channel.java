/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  java.util.concurrent.atomic.AtomicBoolean
 *  javax.annotation.Nullable
 *  javax.sound.sampled.AudioFormat
 *  org.lwjgl.openal.AL10
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.OpenAlUtil;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.slf4j.Logger;

public class Channel {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int QUEUED_BUFFER_COUNT = 4;
    public static final int BUFFER_DURATION_SECONDS = 1;
    private final int source;
    private final AtomicBoolean initialized = new AtomicBoolean(true);
    private int streamingBufferSize = 16384;
    @Nullable
    private AudioStream stream;

    @Nullable
    static Channel create() {
        int[] $$0 = new int[1];
        AL10.alGenSources((int[])$$0);
        if (OpenAlUtil.checkALError("Allocate new source")) {
            return null;
        }
        return new Channel($$0[0]);
    }

    private Channel(int $$0) {
        this.source = $$0;
    }

    public void destroy() {
        if (this.initialized.compareAndSet(true, false)) {
            AL10.alSourceStop((int)this.source);
            OpenAlUtil.checkALError("Stop");
            if (this.stream != null) {
                try {
                    this.stream.close();
                }
                catch (IOException $$0) {
                    LOGGER.error("Failed to close audio stream", (Throwable)$$0);
                }
                this.removeProcessedBuffers();
                this.stream = null;
            }
            AL10.alDeleteSources((int[])new int[]{this.source});
            OpenAlUtil.checkALError("Cleanup");
        }
    }

    public void play() {
        AL10.alSourcePlay((int)this.source);
    }

    private int getState() {
        if (!this.initialized.get()) {
            return 4116;
        }
        return AL10.alGetSourcei((int)this.source, (int)4112);
    }

    public void pause() {
        if (this.getState() == 4114) {
            AL10.alSourcePause((int)this.source);
        }
    }

    public void unpause() {
        if (this.getState() == 4115) {
            AL10.alSourcePlay((int)this.source);
        }
    }

    public void stop() {
        if (this.initialized.get()) {
            AL10.alSourceStop((int)this.source);
            OpenAlUtil.checkALError("Stop");
        }
    }

    public boolean playing() {
        return this.getState() == 4114;
    }

    public boolean stopped() {
        return this.getState() == 4116;
    }

    public void setSelfPosition(Vec3 $$0) {
        AL10.alSourcefv((int)this.source, (int)4100, (float[])new float[]{(float)$$0.x, (float)$$0.y, (float)$$0.z});
    }

    public void setPitch(float $$0) {
        AL10.alSourcef((int)this.source, (int)4099, (float)$$0);
    }

    public void setLooping(boolean $$0) {
        AL10.alSourcei((int)this.source, (int)4103, (int)($$0 ? 1 : 0));
    }

    public void setVolume(float $$0) {
        AL10.alSourcef((int)this.source, (int)4106, (float)$$0);
    }

    public void disableAttenuation() {
        AL10.alSourcei((int)this.source, (int)53248, (int)0);
    }

    public void linearAttenuation(float $$0) {
        AL10.alSourcei((int)this.source, (int)53248, (int)53251);
        AL10.alSourcef((int)this.source, (int)4131, (float)$$0);
        AL10.alSourcef((int)this.source, (int)4129, (float)1.0f);
        AL10.alSourcef((int)this.source, (int)4128, (float)0.0f);
    }

    public void setRelative(boolean $$0) {
        AL10.alSourcei((int)this.source, (int)514, (int)($$0 ? 1 : 0));
    }

    public void attachStaticBuffer(SoundBuffer $$02) {
        $$02.getAlBuffer().ifPresent($$0 -> AL10.alSourcei((int)this.source, (int)4105, (int)$$0));
    }

    public void attachBufferStream(AudioStream $$0) {
        this.stream = $$0;
        AudioFormat $$1 = $$0.getFormat();
        this.streamingBufferSize = Channel.calculateBufferSize($$1, 1);
        this.pumpBuffers(4);
    }

    private static int calculateBufferSize(AudioFormat $$0, int $$1) {
        return (int)((float)($$1 * $$0.getSampleSizeInBits()) / 8.0f * (float)$$0.getChannels() * $$0.getSampleRate());
    }

    private void pumpBuffers(int $$02) {
        if (this.stream != null) {
            try {
                for (int $$1 = 0; $$1 < $$02; ++$$1) {
                    ByteBuffer $$2 = this.stream.read(this.streamingBufferSize);
                    if ($$2 == null) continue;
                    new SoundBuffer($$2, this.stream.getFormat()).releaseAlBuffer().ifPresent($$0 -> AL10.alSourceQueueBuffers((int)this.source, (int[])new int[]{$$0}));
                }
            }
            catch (IOException $$3) {
                LOGGER.error("Failed to read from audio stream", (Throwable)$$3);
            }
        }
    }

    public void updateStream() {
        if (this.stream != null) {
            int $$0 = this.removeProcessedBuffers();
            this.pumpBuffers($$0);
        }
    }

    private int removeProcessedBuffers() {
        int $$0 = AL10.alGetSourcei((int)this.source, (int)4118);
        if ($$0 > 0) {
            int[] $$1 = new int[$$0];
            AL10.alSourceUnqueueBuffers((int)this.source, (int[])$$1);
            OpenAlUtil.checkALError("Unqueue buffers");
            AL10.alDeleteBuffers((int[])$$1);
            OpenAlUtil.checkALError("Remove processed buffers");
        }
        return $$0;
    }
}