/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.InsufficientPrivilegesException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.exceptions.UserBannedException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.math.BigInteger
 *  java.security.Key
 *  java.security.PublicKey
 *  java.time.Duration
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  javax.crypto.Cipher
 *  javax.crypto.SecretKey
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.Key;
import java.security.PublicKey;
import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Crypt;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;

public class ClientHandshakePacketListenerImpl
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    @Nullable
    private final ServerData serverData;
    @Nullable
    private final Screen parent;
    private final Consumer<Component> updateStatus;
    private final Connection connection;
    private GameProfile localGameProfile;
    private final boolean newWorld;
    @Nullable
    private final Duration worldLoadDuration;

    public ClientHandshakePacketListenerImpl(Connection $$0, Minecraft $$1, @Nullable ServerData $$2, @Nullable Screen $$3, boolean $$4, @Nullable Duration $$5, Consumer<Component> $$6) {
        this.connection = $$0;
        this.minecraft = $$1;
        this.serverData = $$2;
        this.parent = $$3;
        this.updateStatus = $$6;
        this.newWorld = $$4;
        this.worldLoadDuration = $$5;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handleHello(ClientboundHelloPacket $$0) {
        void $$10;
        void $$9;
        void $$12;
        void $$11;
        try {
            SecretKey $$1 = Crypt.generateSecretKey();
            PublicKey $$2 = $$0.getPublicKey();
            String $$3 = new BigInteger(Crypt.digestData($$0.getServerId(), $$2, $$1)).toString(16);
            Cipher $$4 = Crypt.getCipher(2, (Key)$$1);
            Cipher $$5 = Crypt.getCipher(1, (Key)$$1);
            byte[] $$6 = $$0.getChallenge();
            ServerboundKeyPacket $$7 = new ServerboundKeyPacket($$1, $$2, $$6);
        }
        catch (Exception $$8) {
            throw new IllegalStateException("Protocol error", (Throwable)$$8);
        }
        this.updateStatus.accept((Object)Component.translatable("connect.authorizing"));
        HttpUtil.DOWNLOAD_EXECUTOR.submit(() -> this.lambda$handleHello$1((String)$$11, (ServerboundKeyPacket)$$12, (Cipher)$$9, (Cipher)$$10));
    }

    @Nullable
    private Component authenticateServer(String $$0) {
        try {
            this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), $$0);
        }
        catch (AuthenticationUnavailableException $$1) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.serversUnavailable"));
        }
        catch (InvalidCredentialsException $$2) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.invalidSession"));
        }
        catch (InsufficientPrivilegesException $$3) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
        }
        catch (UserBannedException $$4) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.userBanned"));
        }
        catch (AuthenticationException $$5) {
            return Component.translatable("disconnect.loginFailedInfo", $$5.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getMinecraftSessionService() {
        return this.minecraft.getMinecraftSessionService();
    }

    @Override
    public void handleGameProfile(ClientboundGameProfilePacket $$0) {
        this.updateStatus.accept((Object)Component.translatable("connect.joining"));
        this.localGameProfile = $$0.getGameProfile();
        this.connection.setProtocol(ConnectionProtocol.PLAY);
        this.connection.setListener(new ClientPacketListener(this.minecraft, this.parent, this.connection, this.serverData, this.localGameProfile, this.minecraft.getTelemetryManager().createWorldSessionManager(this.newWorld, this.worldLoadDuration)));
    }

    @Override
    public void onDisconnect(Component $$0) {
        if (this.parent != null && this.parent instanceof RealmsScreen) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, CommonComponents.CONNECT_FAILED, $$0));
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, CommonComponents.CONNECT_FAILED, $$0));
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void handleDisconnect(ClientboundLoginDisconnectPacket $$0) {
        this.connection.disconnect($$0.getReason());
    }

    @Override
    public void handleCompression(ClientboundLoginCompressionPacket $$0) {
        if (!this.connection.isMemoryConnection()) {
            this.connection.setupCompression($$0.getCompressionThreshold(), false);
        }
    }

    @Override
    public void handleCustomQuery(ClientboundCustomQueryPacket $$0) {
        this.updateStatus.accept((Object)Component.translatable("connect.negotiating"));
        this.connection.send(new ServerboundCustomQueryPacket($$0.getTransactionId(), null));
    }

    private /* synthetic */ void lambda$handleHello$1(String $$0, ServerboundKeyPacket $$1, Cipher $$2, Cipher $$3) {
        Component $$4 = this.authenticateServer($$0);
        if ($$4 != null) {
            if (this.serverData != null && this.serverData.isLan()) {
                LOGGER.warn($$4.getString());
            } else {
                this.connection.disconnect($$4);
                return;
            }
        }
        this.updateStatus.accept((Object)Component.translatable("connect.encrypting"));
        this.connection.send($$1, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey($$2, $$3)));
    }
}