/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  java.io.IOException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.List
 *  org.slf4j.Logger
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder
extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketFlow flow;

    public PacketDecoder(PacketFlow $$0) {
        this.flow = $$0;
    }

    protected void decode(ChannelHandlerContext $$0, ByteBuf $$1, List<Object> $$2) throws Exception {
        int $$3 = $$1.readableBytes();
        if ($$3 == 0) {
            return;
        }
        FriendlyByteBuf $$4 = new FriendlyByteBuf($$1);
        int $$5 = $$4.readVarInt();
        Packet<?> $$6 = ((ConnectionProtocol)$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).createPacket(this.flow, $$5, $$4);
        if ($$6 == null) {
            throw new IOException("Bad packet id " + $$5);
        }
        int $$7 = ((ConnectionProtocol)$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
        JvmProfiler.INSTANCE.onPacketReceived($$7, $$5, $$0.channel().remoteAddress(), $$3);
        if ($$4.readableBytes() > 0) {
            throw new IOException("Packet " + ((ConnectionProtocol)$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId() + "/" + $$5 + " (" + $$6.getClass().getSimpleName() + ") was larger than I expected, found " + $$4.readableBytes() + " bytes extra whilst reading packet " + $$5);
        }
        $$2.add($$6);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{$$0.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), $$5, $$6.getClass().getName()});
        }
    }
}