/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.BufferedInputStream
 *  java.io.FilterInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.nio.ByteBuffer
 *  javax.sound.sampled.AudioFormat
 */
package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;

public class LoopingAudioStream
implements AudioStream {
    private final AudioStreamProvider provider;
    private AudioStream stream;
    private final BufferedInputStream bufferedInputStream;

    public LoopingAudioStream(AudioStreamProvider $$0, InputStream $$1) throws IOException {
        this.provider = $$0;
        this.bufferedInputStream = new BufferedInputStream($$1);
        this.bufferedInputStream.mark(Integer.MAX_VALUE);
        this.stream = $$0.create((InputStream)new NoCloseBuffer((InputStream)this.bufferedInputStream));
    }

    @Override
    public AudioFormat getFormat() {
        return this.stream.getFormat();
    }

    @Override
    public ByteBuffer read(int $$0) throws IOException {
        ByteBuffer $$1 = this.stream.read($$0);
        if (!$$1.hasRemaining()) {
            this.stream.close();
            this.bufferedInputStream.reset();
            this.stream = this.provider.create((InputStream)new NoCloseBuffer((InputStream)this.bufferedInputStream));
            $$1 = this.stream.read($$0);
        }
        return $$1;
    }

    public void close() throws IOException {
        this.stream.close();
        this.bufferedInputStream.close();
    }

    @FunctionalInterface
    public static interface AudioStreamProvider {
        public AudioStream create(InputStream var1) throws IOException;
    }

    static class NoCloseBuffer
    extends FilterInputStream {
        NoCloseBuffer(InputStream $$0) {
            super($$0);
        }

        public void close() {
        }
    }
}