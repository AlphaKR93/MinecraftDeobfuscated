/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.util.concurrent.GenericFutureListener
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.InetSocketAddress
 *  java.net.UnknownHostException
 *  java.nio.charset.StandardCharsets
 *  java.text.ParseException
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.UnaryOperator
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class ServerStatusPinger {
    static final Splitter SPLITTER = Splitter.on((char)'\u0000').limit(6);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withColor(-65536)));
    private final List<Connection> connections = Collections.synchronizedList((List)Lists.newArrayList());

    public void pingServer(final ServerData $$0, final Runnable $$1) throws UnknownHostException {
        ServerAddress $$2 = ServerAddress.parseString($$0.ip);
        Optional $$3 = ServerNameResolver.DEFAULT.resolveAddress($$2).map(ResolvedServerAddress::asInetSocketAddress);
        if (!$$3.isPresent()) {
            this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, $$0);
            return;
        }
        final InetSocketAddress $$4 = (InetSocketAddress)$$3.get();
        final Connection $$5 = Connection.connectToServer($$4, false);
        this.connections.add((Object)$$5);
        $$0.motd = Component.translatable("multiplayer.status.pinging");
        $$0.ping = -1L;
        $$0.playerList = Collections.emptyList();
        $$5.setListener(new ClientStatusPacketListener(){
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket $$02) {
                if (this.receivedPing) {
                    $$5.disconnect(Component.translatable("multiplayer.status.unrequested"));
                    return;
                }
                this.receivedPing = true;
                ServerStatus $$12 = $$02.getStatus();
                $$0.motd = $$12.getDescription() != null ? $$12.getDescription() : CommonComponents.EMPTY;
                if ($$12.getVersion() != null) {
                    $$0.version = Component.literal($$12.getVersion().getName());
                    $$0.protocol = $$12.getVersion().getProtocol();
                } else {
                    $$0.version = Component.translatable("multiplayer.status.old");
                    $$0.protocol = 0;
                }
                if ($$12.getPlayers() != null) {
                    $$0.status = ServerStatusPinger.formatPlayerCount($$12.getPlayers().getNumPlayers(), $$12.getPlayers().getMaxPlayers());
                    $$0.players = $$12.getPlayers();
                    ArrayList $$2 = Lists.newArrayList();
                    GameProfile[] $$3 = $$12.getPlayers().getSample();
                    if ($$3 != null && $$3.length > 0) {
                        for (GameProfile $$42 : $$3) {
                            $$2.add((Object)Component.literal($$42.getName()));
                        }
                        if ($$3.length < $$12.getPlayers().getNumPlayers()) {
                            $$2.add((Object)Component.translatable("multiplayer.status.and_more", $$12.getPlayers().getNumPlayers() - $$3.length));
                        }
                        $$0.playerList = $$2;
                    }
                } else {
                    $$0.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY);
                }
                String $$52 = $$12.getFavicon();
                if ($$52 != null) {
                    try {
                        $$52 = ServerData.parseFavicon($$52);
                    }
                    catch (ParseException $$6) {
                        LOGGER.error("Invalid server icon", (Throwable)$$6);
                    }
                }
                if (!Objects.equals((Object)$$52, (Object)$$0.getIconB64())) {
                    $$0.setIconB64($$52);
                    $$1.run();
                }
                this.pingStart = Util.getMillis();
                $$5.send(new ServerboundPingRequestPacket(this.pingStart));
                this.success = true;
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket $$02) {
                long $$12 = this.pingStart;
                long $$2 = Util.getMillis();
                $$0.ping = $$2 - $$12;
                $$5.disconnect(Component.translatable("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnect(Component $$02) {
                if (!this.success) {
                    ServerStatusPinger.this.onPingFailed($$02, $$0);
                    ServerStatusPinger.this.pingLegacyServer($$4, $$0);
                }
            }

            @Override
            public boolean isAcceptingMessages() {
                return $$5.isConnected();
            }
        });
        try {
            $$5.send(new ClientIntentionPacket($$2.getHost(), $$2.getPort(), ConnectionProtocol.STATUS));
            $$5.send(new ServerboundStatusRequestPacket());
        }
        catch (Throwable $$6) {
            LOGGER.error("Failed to ping server {}", (Object)$$2, (Object)$$6);
        }
    }

    void onPingFailed(Component $$0, ServerData $$1) {
        LOGGER.error("Can't ping {}: {}", (Object)$$1.ip, (Object)$$0.getString());
        $$1.motd = CANT_CONNECT_MESSAGE;
        $$1.status = CommonComponents.EMPTY;
    }

    void pingLegacyServer(final InetSocketAddress $$0, final ServerData $$1) {
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel $$02) {
                try {
                    $$02.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                $$02.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void channelActive(ChannelHandlerContext $$0) throws Exception {
                        super.channelActive($$0);
                        ByteBuf $$1 = Unpooled.buffer();
                        try {
                            $$1.writeByte(254);
                            $$1.writeByte(1);
                            $$1.writeByte(250);
                            char[] $$2 = "MC|PingHost".toCharArray();
                            $$1.writeShort($$2.length);
                            for (char $$3 : $$2) {
                                $$1.writeChar((int)$$3);
                            }
                            $$1.writeShort(7 + 2 * $$0.getHostName().length());
                            $$1.writeByte(127);
                            $$2 = $$0.getHostName().toCharArray();
                            $$1.writeShort($$2.length);
                            for (char $$4 : $$2) {
                                $$1.writeChar((int)$$4);
                            }
                            $$1.writeInt($$0.getPort());
                            $$0.channel().writeAndFlush((Object)$$1).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally {
                            $$1.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext $$0, ByteBuf $$1) {
                        String $$3;
                        String[] $$4;
                        short $$2 = $$1.readUnsignedByte();
                        if ($$2 == 255 && "\u00a71".equals((Object)($$4 = (String[])Iterables.toArray((Iterable)SPLITTER.split((CharSequence)($$3 = new String($$1.readBytes($$1.readShort() * 2).array(), StandardCharsets.UTF_16BE))), String.class))[0])) {
                            int $$5 = Mth.getInt($$4[1], 0);
                            String $$6 = $$4[2];
                            String $$7 = $$4[3];
                            int $$8 = Mth.getInt($$4[4], -1);
                            int $$9 = Mth.getInt($$4[5], -1);
                            $$1.protocol = -1;
                            $$1.version = Component.literal($$6);
                            $$1.motd = Component.literal($$7);
                            $$1.status = ServerStatusPinger.formatPlayerCount($$8, $$9);
                            $$1.players = new ServerStatus.Players($$9, $$8);
                        }
                        $$0.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext $$0, Throwable $$1) {
                        $$0.close();
                    }
                }});
            }
        })).channel(NioSocketChannel.class)).connect($$0.getAddress(), $$0.getPort());
    }

    static Component formatPlayerCount(int $$0, int $$1) {
        return Component.literal(Integer.toString((int)$$0)).append(Component.literal("/").withStyle(ChatFormatting.DARK_GRAY)).append(Integer.toString((int)$$1)).withStyle(ChatFormatting.GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator $$0 = this.connections.iterator();
            while ($$0.hasNext()) {
                Connection $$1 = (Connection)((Object)$$0.next());
                if ($$1.isConnected()) {
                    $$1.tick();
                    continue;
                }
                $$0.remove();
                $$1.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator $$0 = this.connections.iterator();
            while ($$0.hasNext()) {
                Connection $$1 = (Connection)((Object)$$0.next());
                if (!$$1.isConnected()) continue;
                $$0.remove();
                $$1.disconnect(Component.translatable("multiplayer.status.cancelled"));
            }
        }
    }
}