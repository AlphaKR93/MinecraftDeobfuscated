/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.FriendlyByteBuf;

@ChannelHandler.Sharable
public class Varint21LengthFieldPrepender
extends MessageToByteEncoder<ByteBuf> {
    private static final int MAX_BYTES = 3;

    protected void encode(ChannelHandlerContext $$0, ByteBuf $$1, ByteBuf $$2) {
        int $$3 = $$1.readableBytes();
        int $$4 = FriendlyByteBuf.getVarIntSize($$3);
        if ($$4 > 3) {
            throw new IllegalArgumentException("unable to fit " + $$3 + " into 3");
        }
        FriendlyByteBuf $$5 = new FriendlyByteBuf($$2);
        $$5.ensureWritable($$4 + $$3);
        $$5.writeVarInt($$3);
        $$5.writeBytes($$1, $$1.readerIndex(), $$3);
    }
}