/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.DecoderException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.List
 *  java.util.zip.Inflater
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;
import net.minecraft.network.FriendlyByteBuf;

public class CompressionDecoder
extends ByteToMessageDecoder {
    public static final int MAXIMUM_COMPRESSED_LENGTH = 0x200000;
    public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 0x800000;
    private final Inflater inflater;
    private int threshold;
    private boolean validateDecompressed;

    public CompressionDecoder(int $$0, boolean $$1) {
        this.threshold = $$0;
        this.validateDecompressed = $$1;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext $$0, ByteBuf $$1, List<Object> $$2) throws Exception {
        if ($$1.readableBytes() == 0) {
            return;
        }
        FriendlyByteBuf $$3 = new FriendlyByteBuf($$1);
        int $$4 = $$3.readVarInt();
        if ($$4 == 0) {
            $$2.add((Object)$$3.readBytes($$3.readableBytes()));
            return;
        }
        if (this.validateDecompressed) {
            if ($$4 < this.threshold) {
                throw new DecoderException("Badly compressed packet - size of " + $$4 + " is below server threshold of " + this.threshold);
            }
            if ($$4 > 0x800000) {
                throw new DecoderException("Badly compressed packet - size of " + $$4 + " is larger than protocol maximum of 8388608");
            }
        }
        byte[] $$5 = new byte[$$3.readableBytes()];
        $$3.readBytes($$5);
        this.inflater.setInput($$5);
        byte[] $$6 = new byte[$$4];
        this.inflater.inflate($$6);
        $$2.add((Object)Unpooled.wrappedBuffer((byte[])$$6));
        this.inflater.reset();
    }

    public void setThreshold(int $$0, boolean $$1) {
        this.threshold = $$0;
        this.validateDecompressed = $$1;
    }
}