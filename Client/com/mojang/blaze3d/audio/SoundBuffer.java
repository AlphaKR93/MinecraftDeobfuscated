/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.nio.ByteBuffer
 *  java.util.OptionalInt
 *  javax.annotation.Nullable
 *  javax.sound.sampled.AudioFormat
 *  org.lwjgl.openal.AL10
 */
package com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.OpenAlUtil;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;

public class SoundBuffer {
    @Nullable
    private ByteBuffer data;
    private final AudioFormat format;
    private boolean hasAlBuffer;
    private int alBuffer;

    public SoundBuffer(ByteBuffer $$0, AudioFormat $$1) {
        this.data = $$0;
        this.format = $$1;
    }

    OptionalInt getAlBuffer() {
        if (!this.hasAlBuffer) {
            if (this.data == null) {
                return OptionalInt.empty();
            }
            int $$0 = OpenAlUtil.audioFormatToOpenAl(this.format);
            int[] $$1 = new int[1];
            AL10.alGenBuffers((int[])$$1);
            if (OpenAlUtil.checkALError("Creating buffer")) {
                return OptionalInt.empty();
            }
            AL10.alBufferData((int)$$1[0], (int)$$0, (ByteBuffer)this.data, (int)((int)this.format.getSampleRate()));
            if (OpenAlUtil.checkALError("Assigning buffer data")) {
                return OptionalInt.empty();
            }
            this.alBuffer = $$1[0];
            this.hasAlBuffer = true;
            this.data = null;
        }
        return OptionalInt.of((int)this.alBuffer);
    }

    public void discardAlBuffer() {
        if (this.hasAlBuffer) {
            AL10.alDeleteBuffers((int[])new int[]{this.alBuffer});
            if (OpenAlUtil.checkALError("Deleting stream buffers")) {
                return;
            }
        }
        this.hasAlBuffer = false;
    }

    public OptionalInt releaseAlBuffer() {
        OptionalInt $$0 = this.getAlBuffer();
        this.hasAlBuffer = false;
        return $$0;
    }
}