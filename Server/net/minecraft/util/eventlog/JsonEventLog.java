/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.Closeable
 *  java.io.IOException
 *  java.io.Writer
 *  java.lang.Appendable
 *  java.lang.Object
 *  java.lang.Override
 *  java.nio.channels.Channels
 *  java.nio.channels.FileChannel
 *  java.nio.channels.ReadableByteChannel
 *  java.nio.channels.WritableByteChannel
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.StandardOpenOption
 *  java.util.concurrent.atomic.AtomicInteger
 *  javax.annotation.Nullable
 */
package net.minecraft.util.eventlog;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.eventlog.JsonEventLogReader;

public class JsonEventLog<T>
implements Closeable {
    private static final Gson GSON = new Gson();
    private final Codec<T> codec;
    final FileChannel channel;
    private final AtomicInteger referenceCount = new AtomicInteger(1);

    public JsonEventLog(Codec<T> $$0, FileChannel $$1) {
        this.codec = $$0;
        this.channel = $$1;
    }

    public static <T> JsonEventLog<T> open(Codec<T> $$0, Path $$1) throws IOException {
        FileChannel $$2 = FileChannel.open((Path)$$1, (OpenOption[])new OpenOption[]{StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE});
        return new JsonEventLog<T>($$0, $$2);
    }

    public void write(T $$0) throws IOException, JsonIOException {
        JsonElement $$1 = (JsonElement)Util.getOrThrow(this.codec.encodeStart((DynamicOps)JsonOps.INSTANCE, $$0), IOException::new);
        this.channel.position(this.channel.size());
        Writer $$2 = Channels.newWriter((WritableByteChannel)this.channel, (Charset)StandardCharsets.UTF_8);
        GSON.toJson($$1, (Appendable)$$2);
        $$2.write(10);
        $$2.flush();
    }

    public JsonEventLogReader<T> openReader() throws IOException {
        if (this.referenceCount.get() <= 0) {
            throw new IOException("Event log has already been closed");
        }
        this.referenceCount.incrementAndGet();
        final JsonEventLogReader<T> $$0 = JsonEventLogReader.create(this.codec, Channels.newReader((ReadableByteChannel)this.channel, (Charset)StandardCharsets.UTF_8));
        return new JsonEventLogReader<T>(){
            private volatile long position;

            @Override
            @Nullable
            public T next() throws IOException {
                try {
                    JsonEventLog.this.channel.position(this.position);
                    Object t = $$0.next();
                    return t;
                }
                finally {
                    this.position = JsonEventLog.this.channel.position();
                }
            }

            public void close() throws IOException {
                JsonEventLog.this.releaseReference();
            }
        };
    }

    public void close() throws IOException {
        this.releaseReference();
    }

    void releaseReference() throws IOException {
        if (this.referenceCount.decrementAndGet() <= 0) {
            this.channel.close();
        }
    }
}