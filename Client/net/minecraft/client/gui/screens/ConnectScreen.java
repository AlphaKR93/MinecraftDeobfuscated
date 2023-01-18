/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.net.InetSocketAddress
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class ConnectScreen
extends Screen {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final long NARRATION_DELAY_MS = 2000L;
    public static final Component UNKNOWN_HOST_MESSAGE = Component.translatable("disconnect.genericReason", Component.translatable("disconnect.unknownHost"));
    @Nullable
    volatile Connection connection;
    volatile boolean aborted;
    final Screen parent;
    private Component status = Component.translatable("connect.connecting");
    private long lastNarration = -1L;

    private ConnectScreen(Screen $$0) {
        super(GameNarrator.NO_TITLE);
        this.parent = $$0;
    }

    public static void startConnecting(Screen $$0, Minecraft $$1, ServerAddress $$2, ServerData $$3) {
        ConnectScreen $$4 = new ConnectScreen($$0);
        $$1.clearLevel();
        $$1.prepareForMultiplayer();
        $$1.updateReportEnvironment(ReportEnvironment.thirdParty($$3 != null ? $$3.ip : $$2.getHost()));
        $$1.setScreen($$4);
        $$4.connect($$1, $$2, $$3);
    }

    private void connect(final Minecraft $$0, final ServerAddress $$1, final @Nullable ServerData $$2) {
        LOGGER.info("Connecting to {}, {}", (Object)$$1.getHost(), (Object)$$1.getPort());
        Thread $$3 = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()){

            public void run() {
                InetSocketAddress $$02 = null;
                try {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    Optional $$12 = ServerNameResolver.DEFAULT.resolveAddress($$1).map(ResolvedServerAddress::asInetSocketAddress);
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    if (!$$12.isPresent()) {
                        $$0.execute(() -> $$0.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, CommonComponents.CONNECT_FAILED, UNKNOWN_HOST_MESSAGE)));
                        return;
                    }
                    $$02 = (InetSocketAddress)$$12.get();
                    ConnectScreen.this.connection = Connection.connectToServer($$02, $$0.options.useNativeTransport());
                    ConnectScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, $$0, $$2, ConnectScreen.this.parent, false, null, (Consumer<Component>)((Consumer)ConnectScreen.this::updateStatus)));
                    ConnectScreen.this.connection.send(new ClientIntentionPacket($$02.getHostName(), $$02.getPort(), ConnectionProtocol.LOGIN));
                    ConnectScreen.this.connection.send(new ServerboundHelloPacket($$0.getUser().getName(), (Optional<UUID>)Optional.ofNullable((Object)$$0.getUser().getProfileId())));
                }
                catch (Exception $$22) {
                    Exception $$5;
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    Throwable throwable = $$22.getCause();
                    if (throwable instanceof Exception) {
                        Exception $$3;
                        Exception $$4 = $$3 = (Exception)throwable;
                    } else {
                        $$5 = $$22;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)$$22);
                    String $$6 = $$02 == null ? $$5.getMessage() : $$5.getMessage().replaceAll($$02.getHostName() + ":" + $$02.getPort(), "").replaceAll($$02.toString(), "");
                    $$0.execute(() -> $$0.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, CommonComponents.CONNECT_FAILED, Component.translatable("disconnect.genericReason", $$6))));
                }
            }
        };
        $$3.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
        $$3.start();
    }

    private void updateStatus(Component $$0) {
        this.status = $$0;
    }

    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.aborted = true;
            if (this.connection != null) {
                this.connection.disconnect(Component.translatable("connect.aborted"));
            }
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        long $$4 = Util.getMillis();
        if ($$4 - this.lastNarration > 2000L) {
            this.lastNarration = $$4;
            this.minecraft.getNarrator().sayNow(Component.translatable("narrator.joining"));
        }
        ConnectScreen.drawCenteredString($$0, this.font, this.status, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}