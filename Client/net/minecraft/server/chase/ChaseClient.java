/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.Float
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Readable
 *  java.lang.String
 *  java.lang.Thread
 *  java.net.Socket
 *  java.util.List
 *  java.util.Locale
 *  java.util.NoSuchElementException
 *  java.util.Optional
 *  java.util.Scanner
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.chase;

import com.google.common.base.Charsets;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ChaseClient {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int RECONNECT_INTERVAL_SECONDS = 5;
    private final String serverHost;
    private final int serverPort;
    private final MinecraftServer server;
    private volatile boolean wantsToRun;
    @Nullable
    private Socket socket;
    @Nullable
    private Thread thread;

    public ChaseClient(String $$0, int $$1, MinecraftServer $$2) {
        this.serverHost = $$0;
        this.serverPort = $$1;
        this.server = $$2;
    }

    public void start() {
        if (this.thread != null && this.thread.isAlive()) {
            LOGGER.warn("Remote control client was asked to start, but it is already running. Will ignore.");
        }
        this.wantsToRun = true;
        this.thread = new Thread(this::run, "chase-client");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void stop() {
        this.wantsToRun = false;
        IOUtils.closeQuietly((Socket)this.socket);
        this.socket = null;
        this.thread = null;
    }

    public void run() {
        String $$0 = this.serverHost + ":" + this.serverPort;
        while (this.wantsToRun) {
            try {
                LOGGER.info("Connecting to remote control server {}", (Object)$$0);
                this.socket = new Socket(this.serverHost, this.serverPort);
                LOGGER.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");
                try (BufferedReader $$1 = new BufferedReader((Reader)new InputStreamReader(this.socket.getInputStream(), Charsets.US_ASCII));){
                    while (this.wantsToRun) {
                        String $$2 = $$1.readLine();
                        if ($$2 == null) {
                            LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", (Object)$$0, (Object)5);
                            break;
                        }
                        this.handleMessage($$2);
                    }
                }
                catch (IOException $$3) {
                    LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", (Object)$$0, (Object)5);
                }
            }
            catch (IOException $$4) {
                LOGGER.warn("Failed to connect to remote control server {}. Will retry in {}s.", (Object)$$0, (Object)5);
            }
            if (!this.wantsToRun) continue;
            try {
                Thread.sleep((long)5000L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private void handleMessage(String $$0) {
        try (Scanner $$1 = new Scanner((Readable)new StringReader($$0));){
            $$1.useLocale(Locale.ROOT);
            String $$2 = $$1.next();
            if ("t".equals((Object)$$2)) {
                this.handleTeleport($$1);
            } else {
                LOGGER.warn("Unknown message type '{}'", (Object)$$2);
            }
        }
        catch (NoSuchElementException $$3) {
            LOGGER.warn("Could not parse message '{}', ignoring", (Object)$$0);
        }
    }

    private void handleTeleport(Scanner $$02) {
        this.parseTarget($$02).ifPresent($$0 -> this.executeCommand(String.format((Locale)Locale.ROOT, (String)"execute in %s run tp @s %.3f %.3f %.3f %.3f %.3f", (Object[])new Object[]{$$0.level.location(), $$0.pos.x, $$0.pos.y, $$0.pos.z, Float.valueOf((float)$$0.rot.y), Float.valueOf((float)$$0.rot.x)})));
    }

    private Optional<TeleportTarget> parseTarget(Scanner $$0) {
        ResourceKey $$1 = (ResourceKey)ChaseCommand.DIMENSION_NAMES.get((Object)$$0.next());
        if ($$1 == null) {
            return Optional.empty();
        }
        float $$2 = $$0.nextFloat();
        float $$3 = $$0.nextFloat();
        float $$4 = $$0.nextFloat();
        float $$5 = $$0.nextFloat();
        float $$6 = $$0.nextFloat();
        return Optional.of((Object)((Object)new TeleportTarget($$1, new Vec3($$2, $$3, $$4), new Vec2($$6, $$5))));
    }

    private void executeCommand(String $$0) {
        this.server.execute(() -> {
            List<ServerPlayer> $$1 = this.server.getPlayerList().getPlayers();
            if ($$1.isEmpty()) {
                return;
            }
            ServerPlayer $$2 = (ServerPlayer)$$1.get(0);
            ServerLevel $$3 = this.server.overworld();
            CommandSourceStack $$4 = new CommandSourceStack($$2, Vec3.atLowerCornerOf($$3.getSharedSpawnPos()), Vec2.ZERO, $$3, 4, "", CommonComponents.EMPTY, this.server, $$2);
            Commands $$5 = this.server.getCommands();
            $$5.performPrefixedCommand($$4, $$0);
        });
    }

    record TeleportTarget(ResourceKey<Level> level, Vec3 pos, Vec2 rot) {
    }
}