/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToMessageEncoder
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class PacketBundleUnpacker
extends MessageToMessageEncoder<Packet<?>> {
    private final PacketFlow flow;

    public PacketBundleUnpacker(PacketFlow $$0) {
        this.flow = $$0;
    }

    protected void encode(ChannelHandlerContext $$0, Packet<?> $$1, List<Object> $$2) throws Exception {
        BundlerInfo.Provider $$3 = (BundlerInfo.Provider)$$0.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();
        if ($$3 == null) {
            throw new EncoderException("Bundler not configured: " + $$1);
        }
        $$3.getBundlerInfo(this.flow).unbundlePacket($$1, arg_0 -> $$2.add(arg_0));
    }
}