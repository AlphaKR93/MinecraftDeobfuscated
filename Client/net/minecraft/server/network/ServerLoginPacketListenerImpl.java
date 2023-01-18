/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.math.BigInteger
 *  java.net.InetAddress
 *  java.net.InetSocketAddress
 *  java.net.SocketAddress
 *  java.security.Key
 *  java.security.PrivateKey
 *  java.util.UUID
 *  java.util.concurrent.atomic.AtomicInteger
 *  javax.annotation.Nullable
 *  javax.crypto.Cipher
 *  javax.crypto.SecretKey
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.Key;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl
implements ServerLoginPacketListener,
TickablePacketListener {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private static final RandomSource RANDOM = RandomSource.create();
    private final byte[] challenge;
    final MinecraftServer server;
    public final Connection connection;
    State state = State.HELLO;
    private int tick;
    @Nullable
    GameProfile gameProfile;
    private final String serverId = "";
    @Nullable
    private ServerPlayer delayedAcceptPlayer;

    public ServerLoginPacketListenerImpl(MinecraftServer $$0, Connection $$1) {
        this.server = $$0;
        this.connection = $$1;
        this.challenge = Ints.toByteArray((int)RANDOM.nextInt());
    }

    @Override
    public void tick() {
        ServerPlayer $$0;
        if (this.state == State.READY_TO_ACCEPT) {
            this.handleAcceptedLogin();
        } else if (this.state == State.DELAY_ACCEPT && ($$0 = this.server.getPlayerList().getPlayer(this.gameProfile.getId())) == null) {
            this.state = State.READY_TO_ACCEPT;
            this.placeNewPlayer(this.delayedAcceptPlayer);
            this.delayedAcceptPlayer = null;
        }
        if (this.tick++ == 600) {
            this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect(Component $$0) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getUserName(), (Object)$$0.getString());
            this.connection.send(new ClientboundLoginDisconnectPacket($$0));
            this.connection.disconnect($$0);
        }
        catch (Exception $$1) {
            LOGGER.error("Error whilst disconnecting player", (Throwable)$$1);
        }
    }

    public void handleAcceptedLogin() {
        Component $$0;
        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.createFakeProfile(this.gameProfile);
        }
        if (($$0 = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile)) != null) {
            this.disconnect($$0);
        } else {
            this.state = State.ACCEPTED;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> this.connection.setupCompression(this.server.getCompressionThreshold(), true)));
            }
            this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
            ServerPlayer $$1 = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
            try {
                ServerPlayer $$2 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
                if ($$1 != null) {
                    this.state = State.DELAY_ACCEPT;
                    this.delayedAcceptPlayer = $$2;
                } else {
                    this.placeNewPlayer($$2);
                }
            }
            catch (Exception $$3) {
                LOGGER.error("Couldn't place player in world", (Throwable)$$3);
                MutableComponent $$4 = Component.translatable("multiplayer.disconnect.invalid_player_data");
                this.connection.send(new ClientboundDisconnectPacket($$4));
                this.connection.disconnect($$4);
            }
        }
    }

    private void placeNewPlayer(ServerPlayer $$0) {
        this.server.getPlayerList().placeNewPlayer(this.connection, $$0);
    }

    @Override
    public void onDisconnect(Component $$0) {
        LOGGER.info("{} lost connection: {}", (Object)this.getUserName(), (Object)$$0.getString());
    }

    public String getUserName() {
        if (this.gameProfile != null) {
            return this.gameProfile + " (" + this.connection.getRemoteAddress() + ")";
        }
        return String.valueOf((Object)this.connection.getRemoteAddress());
    }

    @Override
    public void handleHello(ServerboundHelloPacket $$0) {
        Validate.validState((this.state == State.HELLO ? 1 : 0) != 0, (String)"Unexpected hello packet", (Object[])new Object[0]);
        Validate.validState((boolean)ServerLoginPacketListenerImpl.isValidUsername($$0.name()), (String)"Invalid characters in username", (Object[])new Object[0]);
        GameProfile $$1 = this.server.getSingleplayerProfile();
        if ($$1 != null && $$0.name().equalsIgnoreCase($$1.getName())) {
            this.gameProfile = $$1;
            this.state = State.READY_TO_ACCEPT;
            return;
        }
        this.gameProfile = new GameProfile(null, $$0.name());
        if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge));
        } else {
            this.state = State.READY_TO_ACCEPT;
        }
    }

    public static boolean isValidUsername(String $$02) {
        return $$02.chars().filter($$0 -> $$0 <= 32 || $$0 >= 127).findAny().isEmpty();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handleKey(ServerboundKeyPacket $$0) {
        void $$7;
        Validate.validState((this.state == State.KEY ? 1 : 0) != 0, (String)"Unexpected key packet", (Object[])new Object[0]);
        try {
            PrivateKey $$1 = this.server.getKeyPair().getPrivate();
            if (!$$0.isChallengeValid(this.challenge, $$1)) {
                throw new IllegalStateException("Protocol error");
            }
            SecretKey $$2 = $$0.getSecretKey($$1);
            Cipher $$3 = Crypt.getCipher(2, (Key)$$2);
            Cipher $$4 = Crypt.getCipher(1, (Key)$$2);
            String $$5 = new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), $$2)).toString(16);
            this.state = State.AUTHENTICATING;
            this.connection.setEncryptionKey($$3, $$4);
        }
        catch (CryptException $$6) {
            throw new IllegalStateException("Protocol error", (Throwable)$$6);
        }
        Thread $$8 = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet(), (String)$$7){
            final /* synthetic */ String val$digest;
            {
                this.val$digest = string;
                super($$1);
            }

            public void run() {
                GameProfile $$0 = ServerLoginPacketListenerImpl.this.gameProfile;
                try {
                    ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile(null, $$0.getName()), this.val$digest, this.getAddress());
                    if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                        LOGGER.info("UUID of player {} is {}", (Object)ServerLoginPacketListenerImpl.this.gameProfile.getName(), (Object)ServerLoginPacketListenerImpl.this.gameProfile.getId());
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = $$0;
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    } else {
                        ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)$$0.getName());
                    }
                }
                catch (AuthenticationUnavailableException $$1) {
                    if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = $$0;
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    }
                    ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress $$0 = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
                return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && $$0 instanceof InetSocketAddress ? ((InetSocketAddress)$$0).getAddress() : null;
            }
        };
        $$8.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
        $$8.start();
    }

    @Override
    public void handleCustomQueryPacket(ServerboundCustomQueryPacket $$0) {
        this.disconnect(Component.translatable("multiplayer.disconnect.unexpected_query_response"));
    }

    protected GameProfile createFakeProfile(GameProfile $$0) {
        UUID $$1 = UUIDUtil.createOfflinePlayerUUID($$0.getName());
        return new GameProfile($$1, $$0.getName());
    }

    static enum State {
        HELLO,
        KEY,
        AUTHENTICATING,
        NEGOTIATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;

    }
}