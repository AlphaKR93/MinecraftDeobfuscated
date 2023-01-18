/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.net.InetSocketAddress
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.realms;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import org.slf4j.Logger;

public class RealmsConnect {
    static final Logger LOGGER = LogUtils.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    @Nullable
    Connection connection;

    public RealmsConnect(Screen $$0) {
        this.onlineScreen = $$0;
    }

    public void connect(final RealmsServer $$0, ServerAddress $$1) {
        final Minecraft $$2 = Minecraft.getInstance();
        $$2.setConnectedToRealms(true);
        $$2.prepareForMultiplayer();
        $$2.getNarrator().sayNow(Component.translatable("mco.connect.success"));
        final String $$3 = $$1.getHost();
        final int $$4 = $$1.getPort();
        new Thread("Realms-connect-task"){

            public void run() {
                InetSocketAddress $$02 = null;
                try {
                    $$02 = new InetSocketAddress($$3, $$4);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection = Connection.connectToServer($$02, $$2.options.useNativeTransport());
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.setListener(new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, $$2, $$0.toServerData($$3), RealmsConnect.this.onlineScreen, false, null, (Consumer<Component>)((Consumer)$$0 -> {})));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new ClientIntentionPacket($$3, $$4, ConnectionProtocol.LOGIN));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    String $$1 = $$2.getUser().getName();
                    UUID $$22 = $$2.getUser().getProfileId();
                    RealmsConnect.this.connection.send(new ServerboundHelloPacket($$1, (Optional<UUID>)Optional.ofNullable((Object)$$22)));
                    $$2.updateReportEnvironment(ReportEnvironment.realm($$0));
                }
                catch (Exception $$32) {
                    $$2.getDownloadedPackSource().clearServerPack();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", (Throwable)$$32);
                    String $$42 = $$32.toString();
                    if ($$02 != null) {
                        String $$5 = $$02 + ":" + $$4;
                        $$42 = $$42.replaceAll($$5, "");
                    }
                    DisconnectedRealmsScreen $$6 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, CommonComponents.CONNECT_FAILED, Component.translatable("disconnect.genericReason", $$42));
                    $$2.execute(() -> $$2.setScreen($$6));
                }
            }
        }.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isConnected()) {
            this.connection.disconnect(Component.translatable("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}