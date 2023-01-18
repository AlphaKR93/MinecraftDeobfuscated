/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.FloatBuffer
 *  java.nio.IntBuffer
 *  java.util.List
 *  javax.sound.sampled.AudioFormat
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.stb.STBVorbis
 *  org.lwjgl.stb.STBVorbisInfo
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.util.Mth;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class OggAudioStream
implements AudioStream {
    private static final int EXPECTED_MAX_FRAME_SIZE = 8192;
    private long handle;
    private final AudioFormat audioFormat;
    private final InputStream input;
    private ByteBuffer buffer = MemoryUtil.memAlloc((int)8192);

    public OggAudioStream(InputStream $$0) throws IOException {
        this.input = $$0;
        this.buffer.limit(0);
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            IntBuffer $$2 = $$1.mallocInt(1);
            IntBuffer $$3 = $$1.mallocInt(1);
            while (this.handle == 0L) {
                if (!this.refillFromStream()) {
                    throw new IOException("Failed to find Ogg header");
                }
                int $$4 = this.buffer.position();
                this.buffer.position(0);
                this.handle = STBVorbis.stb_vorbis_open_pushdata((ByteBuffer)this.buffer, (IntBuffer)$$2, (IntBuffer)$$3, null);
                this.buffer.position($$4);
                int $$5 = $$3.get(0);
                if ($$5 == 1) {
                    this.forwardBuffer();
                    continue;
                }
                if ($$5 == 0) continue;
                throw new IOException("Failed to read Ogg file " + $$5);
            }
            this.buffer.position(this.buffer.position() + $$2.get(0));
            STBVorbisInfo $$6 = STBVorbisInfo.mallocStack((MemoryStack)$$1);
            STBVorbis.stb_vorbis_get_info((long)this.handle, (STBVorbisInfo)$$6);
            this.audioFormat = new AudioFormat((float)$$6.sample_rate(), 16, $$6.channels(), true, false);
        }
    }

    private boolean refillFromStream() throws IOException {
        int $$0 = this.buffer.limit();
        int $$1 = this.buffer.capacity() - $$0;
        if ($$1 == 0) {
            return true;
        }
        byte[] $$2 = new byte[$$1];
        int $$3 = this.input.read($$2);
        if ($$3 == -1) {
            return false;
        }
        int $$4 = this.buffer.position();
        this.buffer.limit($$0 + $$3);
        this.buffer.position($$0);
        this.buffer.put($$2, 0, $$3);
        this.buffer.position($$4);
        return true;
    }

    private void forwardBuffer() {
        boolean $$1;
        boolean $$0 = this.buffer.position() == 0;
        boolean bl = $$1 = this.buffer.position() == this.buffer.limit();
        if ($$1 && !$$0) {
            this.buffer.position(0);
            this.buffer.limit(0);
        } else {
            ByteBuffer $$2 = MemoryUtil.memAlloc((int)($$0 ? 2 * this.buffer.capacity() : this.buffer.capacity()));
            $$2.put(this.buffer);
            MemoryUtil.memFree((Buffer)this.buffer);
            $$2.flip();
            this.buffer = $$2;
        }
    }

    private boolean readFrame(OutputConcat $$0) throws IOException {
        if (this.handle == 0L) {
            return false;
        }
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            block14: {
                int $$7;
                PointerBuffer $$2 = $$1.mallocPointer(1);
                IntBuffer $$3 = $$1.mallocInt(1);
                IntBuffer $$4 = $$1.mallocInt(1);
                while (true) {
                    int $$5 = STBVorbis.stb_vorbis_decode_frame_pushdata((long)this.handle, (ByteBuffer)this.buffer, (IntBuffer)$$3, (PointerBuffer)$$2, (IntBuffer)$$4);
                    this.buffer.position(this.buffer.position() + $$5);
                    int $$6 = STBVorbis.stb_vorbis_get_error((long)this.handle);
                    if ($$6 == 1) {
                        this.forwardBuffer();
                        if (this.refillFromStream()) continue;
                        break block14;
                    }
                    if ($$6 != 0) {
                        throw new IOException("Failed to read Ogg file " + $$6);
                    }
                    $$7 = $$4.get(0);
                    if ($$7 != 0) break;
                }
                int $$8 = $$3.get(0);
                PointerBuffer $$9 = $$2.getPointerBuffer($$8);
                if ($$8 == 1) {
                    this.convertMono($$9.getFloatBuffer(0, $$7), $$0);
                    boolean bl = true;
                    return bl;
                }
                if ($$8 == 2) {
                    this.convertStereo($$9.getFloatBuffer(0, $$7), $$9.getFloatBuffer(1, $$7), $$0);
                    boolean bl = true;
                    return bl;
                }
                throw new IllegalStateException("Invalid number of channels: " + $$8);
            }
            boolean bl = false;
            return bl;
        }
    }

    private void convertMono(FloatBuffer $$0, OutputConcat $$1) {
        while ($$0.hasRemaining()) {
            $$1.put($$0.get());
        }
    }

    private void convertStereo(FloatBuffer $$0, FloatBuffer $$1, OutputConcat $$2) {
        while ($$0.hasRemaining() && $$1.hasRemaining()) {
            $$2.put($$0.get());
            $$2.put($$1.get());
        }
    }

    public void close() throws IOException {
        if (this.handle != 0L) {
            STBVorbis.stb_vorbis_close((long)this.handle);
            this.handle = 0L;
        }
        MemoryUtil.memFree((Buffer)this.buffer);
        this.input.close();
    }

    @Override
    public AudioFormat getFormat() {
        return this.audioFormat;
    }

    @Override
    public ByteBuffer read(int $$0) throws IOException {
        OutputConcat $$1 = new OutputConcat($$0 + 8192);
        while (this.readFrame($$1) && $$1.byteCount < $$0) {
        }
        return $$1.get();
    }

    public ByteBuffer readAll() throws IOException {
        OutputConcat $$0 = new OutputConcat(16384);
        while (this.readFrame($$0)) {
        }
        return $$0.get();
    }

    static class OutputConcat {
        private final List<ByteBuffer> buffers = Lists.newArrayList();
        private final int bufferSize;
        int byteCount;
        private ByteBuffer currentBuffer;

        public OutputConcat(int $$0) {
            this.bufferSize = $$0 + 1 & 0xFFFFFFFE;
            this.createNewBuffer();
        }

        private void createNewBuffer() {
            this.currentBuffer = BufferUtils.createByteBuffer((int)this.bufferSize);
        }

        public void put(float $$0) {
            if (this.currentBuffer.remaining() == 0) {
                this.currentBuffer.flip();
                this.buffers.add((Object)this.currentBuffer);
                this.createNewBuffer();
            }
            int $$1 = Mth.clamp((int)($$0 * 32767.5f - 0.5f), Short.MIN_VALUE, Short.MAX_VALUE);
            this.currentBuffer.putShort((short)$$1);
            this.byteCount += 2;
        }

        public ByteBuffer get() {
            this.currentBuffer.flip();
            if (this.buffers.isEmpty()) {
                return this.currentBuffer;
            }
            ByteBuffer $$0 = BufferUtils.createByteBuffer((int)this.byteCount);
            this.buffers.forEach(arg_0 -> ((ByteBuffer)$$0).put(arg_0));
            $$0.put(this.currentBuffer);
            $$0.flip();
            return $$0;
        }
    }
}