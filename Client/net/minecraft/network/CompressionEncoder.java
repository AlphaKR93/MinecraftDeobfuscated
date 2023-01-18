/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  java.lang.Object
 *  java.util.zip.Deflater
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import net.minecraft.network.FriendlyByteBuf;

public class CompressionEncoder
extends MessageToByteEncoder<ByteBuf> {
    private final byte[] encodeBuf = new byte[8192];
    private final Deflater deflater;
    private int threshold;

    public CompressionEncoder(int $$0) {
        this.threshold = $$0;
        this.deflater = new Deflater();
    }

    protected void encode(ChannelHandlerContext $$0, ByteBuf $$1, ByteBuf $$2) {
        int $$3 = $$1.readableBytes();
        FriendlyByteBuf $$4 = new FriendlyByteBuf($$2);
        if ($$3 < this.threshold) {
            $$4.writeVarInt(0);
            $$4.writeBytes($$1);
        } else {
            byte[] $$5 = new byte[$$3];
            $$1.readBytes($$5);
            $$4.writeVarInt($$5.length);
            this.deflater.setInput($$5, 0, $$3);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int $$6 = this.deflater.deflate(this.encodeBuf);
                $$4.writeBytes(this.encodeBuf, 0, $$6);
            }
            this.deflater.reset();
        }
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int $$0) {
        this.threshold = $$0;
    }
}