/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.concurrent.GenericFutureListener
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.net.InetSocketAddress
 *  java.nio.charset.StandardCharsets
 *  java.util.Locale
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.slf4j.Logger;

public class LegacyQueryHandler
extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int FAKE_PROTOCOL_VERSION = 127;
    private final ServerConnectionListener serverConnectionListener;

    public LegacyQueryHandler(ServerConnectionListener $$0) {
        this.serverConnectionListener = $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void channelRead(ChannelHandlerContext $$0, Object $$1) {
        ByteBuf $$2 = (ByteBuf)$$1;
        $$2.markReaderIndex();
        boolean $$3 = true;
        try {
            if ($$2.readUnsignedByte() != 254) {
                return;
            }
            InetSocketAddress $$4 = (InetSocketAddress)$$0.channel().remoteAddress();
            MinecraftServer $$5 = this.serverConnectionListener.getServer();
            int $$6 = $$2.readableBytes();
            switch ($$6) {
                case 0: {
                    LOGGER.debug("Ping: (<1.3.x) from {}:{}", (Object)$$4.getAddress(), (Object)$$4.getPort());
                    String $$7 = String.format((Locale)Locale.ROOT, (String)"%s\u00a7%d\u00a7%d", (Object[])new Object[]{$$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers()});
                    this.sendFlushAndClose($$0, this.createReply($$7));
                    break;
                }
                case 1: {
                    if ($$2.readUnsignedByte() != 1) {
                        return;
                    }
                    LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", (Object)$$4.getAddress(), (Object)$$4.getPort());
                    String $$8 = String.format((Locale)Locale.ROOT, (String)"\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", (Object[])new Object[]{127, $$5.getServerVersion(), $$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers()});
                    this.sendFlushAndClose($$0, this.createReply($$8));
                    break;
                }
                default: {
                    boolean $$9 = $$2.readUnsignedByte() == 1;
                    $$9 &= $$2.readUnsignedByte() == 250;
                    $$9 &= "MC|PingHost".equals((Object)new String($$2.readBytes($$2.readShort() * 2).array(), StandardCharsets.UTF_16BE));
                    int $$10 = $$2.readUnsignedShort();
                    $$9 &= $$2.readUnsignedByte() >= 73;
                    $$9 &= 3 + $$2.readBytes($$2.readShort() * 2).array().length + 4 == $$10;
                    $$9 &= $$2.readInt() <= 65535;
                    if (!($$9 &= $$2.readableBytes() == 0)) {
                        return;
                    }
                    LOGGER.debug("Ping: (1.6) from {}:{}", (Object)$$4.getAddress(), (Object)$$4.getPort());
                    String $$11 = String.format((Locale)Locale.ROOT, (String)"\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", (Object[])new Object[]{127, $$5.getServerVersion(), $$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers()});
                    ByteBuf $$12 = this.createReply($$11);
                    try {
                        this.sendFlushAndClose($$0, $$12);
                        break;
                    }
                    finally {
                        $$12.release();
                    }
                }
            }
            $$2.release();
            $$3 = false;
        }
        catch (RuntimeException runtimeException) {
        }
        finally {
            if ($$3) {
                $$2.resetReaderIndex();
                $$0.channel().pipeline().remove("legacy_query");
                $$0.fireChannelRead($$1);
            }
        }
    }

    private void sendFlushAndClose(ChannelHandlerContext $$0, ByteBuf $$1) {
        $$0.pipeline().firstContext().writeAndFlush((Object)$$1).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }

    private ByteBuf createReply(String $$0) {
        ByteBuf $$1 = Unpooled.buffer();
        $$1.writeByte(255);
        char[] $$2 = $$0.toCharArray();
        $$1.writeShort($$2.length);
        for (char $$3 : $$2) {
            $$1.writeChar((int)$$3);
        }
        return $$1;
    }
}