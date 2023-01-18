/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.DefaultEventLoopGroup
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollSocketChannel
 *  io.netty.channel.local.LocalChannel
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  io.netty.handler.timeout.TimeoutException
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.GenericFutureListener
 *  java.lang.Class
 *  java.lang.ClassCastException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.InetSocketAddress
 *  java.net.SocketAddress
 *  java.util.Queue
 *  java.util.concurrent.RejectedExecutionException
 *  javax.annotation.Nullable
 *  javax.crypto.Cipher
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.Util;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection
extends SimpleChannelInboundHandler<Packet<?>> {
    private static final float AVERAGE_PACKETS_SMOOTHING = 0.75f;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Marker ROOT_MARKER = MarkerFactory.getMarker((String)"NETWORK");
    public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker((String)"NETWORK_PACKETS"), $$0 -> $$0.add(ROOT_MARKER));
    public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker((String)"PACKET_RECEIVED"), $$0 -> $$0.add(PACKET_MARKER));
    public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker((String)"PACKET_SENT"), $$0 -> $$0.add(PACKET_MARKER));
    public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf((String)"protocol");
    public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
    private final PacketFlow receiving;
    private final Queue<PacketHolder> queue = Queues.newConcurrentLinkedQueue();
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private Component disconnectedReason;
    private boolean encrypted;
    private boolean disconnectionHandled;
    private int receivedPackets;
    private int sentPackets;
    private float averageReceivedPackets;
    private float averageSentPackets;
    private int tickCount;
    private boolean handlingFault;

    public Connection(PacketFlow $$0) {
        this.receiving = $$0;
    }

    public void channelActive(ChannelHandlerContext $$0) throws Exception {
        super.channelActive($$0);
        this.channel = $$0.channel();
        this.address = this.channel.remoteAddress();
        try {
            this.setProtocol(ConnectionProtocol.HANDSHAKING);
        }
        catch (Throwable $$1) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to change protocol to handshake", $$1);
        }
    }

    public void setProtocol(ConnectionProtocol $$0) {
        this.channel.attr(ATTRIBUTE_PROTOCOL).set((Object)$$0);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext $$0) {
        this.disconnect(Component.translatable("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext $$0, Throwable $$1) {
        if ($$1 instanceof SkipPacketException) {
            LOGGER.debug("Skipping packet due to errors", $$1.getCause());
            return;
        }
        boolean $$2 = !this.handlingFault;
        this.handlingFault = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if ($$1 instanceof TimeoutException) {
            LOGGER.debug("Timeout", $$1);
            this.disconnect(Component.translatable("disconnect.timeout"));
        } else {
            MutableComponent $$3 = Component.translatable("disconnect.genericReason", "Internal Exception: " + $$1);
            if ($$2) {
                LOGGER.debug("Failed to sent packet", $$1);
                ConnectionProtocol $$4 = this.getCurrentProtocol();
                Packet<ClientLoginPacketListener> $$5 = $$4 == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket($$3) : new ClientboundDisconnectPacket($$3);
                this.send($$5, PacketSendListener.thenRun(() -> this.disconnect($$3)));
                this.setReadOnly();
            } else {
                LOGGER.debug("Double fault", $$1);
                this.disconnect($$3);
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext $$0, Packet<?> $$1) {
        if (this.channel.isOpen()) {
            try {
                Connection.genericsFtw($$1, this.packetListener);
            }
            catch (RunningOnDifferentThreadException runningOnDifferentThreadException) {
            }
            catch (RejectedExecutionException $$2) {
                this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
            }
            catch (ClassCastException $$3) {
                LOGGER.error("Received {} that couldn't be processed", (Object)$$1.getClass(), (Object)$$3);
                this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
            }
            ++this.receivedPackets;
        }
    }

    private static <T extends PacketListener> void genericsFtw(Packet<T> $$0, PacketListener $$1) {
        $$0.handle($$1);
    }

    public void setListener(PacketListener $$0) {
        Validate.notNull((Object)$$0, (String)"packetListener", (Object[])new Object[0]);
        this.packetListener = $$0;
    }

    public void send(Packet<?> $$0) {
        this.send($$0, null);
    }

    public void send(Packet<?> $$0, @Nullable PacketSendListener $$1) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacket($$0, $$1);
        } else {
            this.queue.add((Object)new PacketHolder($$0, $$1));
        }
    }

    private void sendPacket(Packet<?> $$0, @Nullable PacketSendListener $$1) {
        ConnectionProtocol $$2 = ConnectionProtocol.getProtocolForPacket($$0);
        ConnectionProtocol $$3 = this.getCurrentProtocol();
        ++this.sentPackets;
        if ($$3 != $$2) {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            this.doSendPacket($$0, $$1, $$2, $$3);
        } else {
            this.channel.eventLoop().execute(() -> this.doSendPacket($$0, $$1, $$2, $$3));
        }
    }

    private void doSendPacket(Packet<?> $$0, @Nullable PacketSendListener $$12, ConnectionProtocol $$2, ConnectionProtocol $$3) {
        if ($$2 != $$3) {
            this.setProtocol($$2);
        }
        ChannelFuture $$4 = this.channel.writeAndFlush($$0);
        if ($$12 != null) {
            $$4.addListener($$1 -> {
                if ($$1.isSuccess()) {
                    $$12.onSuccess();
                } else {
                    Packet<?> $$2 = $$12.onFailure();
                    if ($$2 != null) {
                        ChannelFuture $$3 = this.channel.writeAndFlush($$2);
                        $$3.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                }
            });
        }
        $$4.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private ConnectionProtocol getCurrentProtocol() {
        return (ConnectionProtocol)((Object)this.channel.attr(ATTRIBUTE_PROTOCOL).get());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flushQueue() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        Queue<PacketHolder> queue = this.queue;
        synchronized (queue) {
            PacketHolder $$0;
            while (($$0 = (PacketHolder)this.queue.poll()) != null) {
                this.sendPacket($$0.packet, $$0.listener);
            }
        }
    }

    public void tick() {
        this.flushQueue();
        PacketListener packetListener = this.packetListener;
        if (packetListener instanceof TickablePacketListener) {
            TickablePacketListener $$0 = (TickablePacketListener)packetListener;
            $$0.tick();
        }
        if (!this.isConnected() && !this.disconnectionHandled) {
            this.handleDisconnection();
        }
        if (this.channel != null) {
            this.channel.flush();
        }
        if (this.tickCount++ % 20 == 0) {
            this.tickSecond();
        }
    }

    protected void tickSecond() {
        this.averageSentPackets = Mth.lerp(0.75f, this.sentPackets, this.averageSentPackets);
        this.averageReceivedPackets = Mth.lerp(0.75f, this.receivedPackets, this.averageReceivedPackets);
        this.sentPackets = 0;
        this.receivedPackets = 0;
    }

    public SocketAddress getRemoteAddress() {
        return this.address;
    }

    public void disconnect(Component $$0) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = $$0;
        }
    }

    public boolean isMemoryConnection() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public PacketFlow getReceiving() {
        return this.receiving;
    }

    public PacketFlow getSending() {
        return this.receiving.getOpposite();
    }

    public static Connection connectToServer(InetSocketAddress $$0, boolean $$1) {
        LazyLoadedValue<NioEventLoopGroup> $$6;
        Class<NioSocketChannel> $$5;
        final Connection $$2 = new Connection(PacketFlow.CLIENTBOUND);
        if (Epoll.isAvailable() && $$1) {
            Class<EpollSocketChannel> $$3 = EpollSocketChannel.class;
            LazyLoadedValue<EpollEventLoopGroup> $$4 = NETWORK_EPOLL_WORKER_GROUP;
        } else {
            $$5 = NioSocketChannel.class;
            $$6 = NETWORK_WORKER_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)$$6.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel $$0) {
                try {
                    $$0.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                $$0.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new Varint21FrameDecoder()).addLast("decoder", (ChannelHandler)new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new Varint21LengthFieldPrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)$$2);
            }
        })).channel($$5)).connect($$0.getAddress(), $$0.getPort()).syncUninterruptibly();
        return $$2;
    }

    public static Connection connectToLocalServer(SocketAddress $$0) {
        final Connection $$1 = new Connection(PacketFlow.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel $$0) {
                $$0.pipeline().addLast("packet_handler", (ChannelHandler)$$1);
            }
        })).channel(LocalChannel.class)).connect($$0).syncUninterruptibly();
        return $$1;
    }

    public void setEncryptionKey(Cipher $$0, Cipher $$1) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new CipherDecoder($$0));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new CipherEncoder($$1));
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public Component getDisconnectedReason() {
        return this.disconnectedReason;
    }

    public void setReadOnly() {
        this.channel.config().setAutoRead(false);
    }

    public void setupCompression(int $$0, boolean $$1) {
        if ($$0 >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold($$0, $$1);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new CompressionDecoder($$0, $$1));
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold($$0);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new CompressionEncoder($$0));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection() {
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
        } else {
            this.disconnectionHandled = true;
            if (this.getDisconnectedReason() != null) {
                this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnect(Component.translatable("multiplayer.disconnect.generic"));
            }
        }
    }

    public float getAverageReceivedPackets() {
        return this.averageReceivedPackets;
    }

    public float getAverageSentPackets() {
        return this.averageSentPackets;
    }

    static class PacketHolder {
        final Packet<?> packet;
        @Nullable
        final PacketSendListener listener;

        public PacketHolder(Packet<?> $$0, @Nullable PacketSendListener $$1) {
            this.packet = $$0;
            this.listener = $$1;
        }
    }
}