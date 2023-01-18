/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.CorruptedFrameException
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public class Varint21FrameDecoder
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decode(ChannelHandlerContext $$0, ByteBuf $$1, List<Object> $$2) {
        $$1.markReaderIndex();
        byte[] $$3 = new byte[3];
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            if (!$$1.isReadable()) {
                $$1.resetReaderIndex();
                return;
            }
            $$3[$$4] = $$1.readByte();
            if ($$3[$$4] < 0) continue;
            FriendlyByteBuf $$5 = new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])$$3));
            try {
                int $$6 = $$5.readVarInt();
                if ($$1.readableBytes() < $$6) {
                    $$1.resetReaderIndex();
                    return;
                }
                $$2.add((Object)$$1.readBytes($$6));
                return;
            }
            finally {
                $$5.release();
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}