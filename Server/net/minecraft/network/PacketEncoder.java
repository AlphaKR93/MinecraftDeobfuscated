/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  java.io.IOException
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder
extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketFlow flow;

    public PacketEncoder(PacketFlow $$0) {
        this.flow = $$0;
    }

    protected void encode(ChannelHandlerContext $$0, Packet<?> $$1, ByteBuf $$2) throws Exception {
        ConnectionProtocol $$3 = (ConnectionProtocol)$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if ($$3 == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + $$1);
        }
        int $$4 = $$3.getPacketId(this.flow, $$1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", new Object[]{$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), $$4, $$1.getClass().getName()});
        }
        if ($$4 == -1) {
            throw new IOException("Can't serialize unregistered packet");
        }
        FriendlyByteBuf $$5 = new FriendlyByteBuf($$2);
        $$5.writeVarInt($$4);
        try {
            int $$6 = $$5.writerIndex();
            $$1.write($$5);
            int $$7 = $$5.writerIndex() - $$6;
            if ($$7 > 0x800000) {
                throw new IllegalArgumentException("Packet too big (is " + $$7 + ", should be less than 8388608): " + $$1);
            }
            int $$8 = ((ConnectionProtocol)$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
            JvmProfiler.INSTANCE.onPacketSent($$8, $$4, $$0.channel().remoteAddress(), $$7);
        }
        catch (Throwable $$9) {
            LOGGER.error("Error receiving packet {}", (Object)$$4, (Object)$$9);
            if ($$1.isSkippable()) {
                throw new SkipPacketException($$9);
            }
            throw $$9;
        }
    }
}