/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class PacketBundlePacker
extends MessageToMessageDecoder<Packet<?>> {
    @Nullable
    private BundlerInfo.Bundler currentBundler;
    @Nullable
    private BundlerInfo infoForCurrentBundler;
    private final PacketFlow flow;

    public PacketBundlePacker(PacketFlow $$0) {
        this.flow = $$0;
    }

    protected void decode(ChannelHandlerContext $$0, Packet<?> $$1, List<Object> $$2) throws Exception {
        BundlerInfo.Provider $$3 = (BundlerInfo.Provider)$$0.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();
        if ($$3 == null) {
            throw new DecoderException("Bundler not configured: " + $$1);
        }
        BundlerInfo $$4 = $$3.getBundlerInfo(this.flow);
        if (this.currentBundler != null) {
            if (this.infoForCurrentBundler != $$4) {
                throw new DecoderException("Bundler handler changed during bundling");
            }
            Packet<?> $$5 = this.currentBundler.addPacket($$1);
            if ($$5 != null) {
                this.infoForCurrentBundler = null;
                this.currentBundler = null;
                $$2.add($$5);
            }
        } else {
            BundlerInfo.Bundler $$6 = $$4.startPacketBundling($$1);
            if ($$6 != null) {
                this.currentBundler = $$6;
                this.infoForCurrentBundler = $$4;
            } else {
                $$2.add($$1);
            }
        }
    }
}