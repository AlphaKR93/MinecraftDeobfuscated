/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.net.DatagramPacket
 *  java.net.DatagramSocket
 *  java.net.InetAddress
 *  java.nio.charset.StandardCharsets
 *  java.util.concurrent.atomic.AtomicInteger
 *  org.slf4j.Logger
 */
package net.minecraft.client.server;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import org.slf4j.Logger;

public class LanServerPinger
extends Thread {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MULTICAST_GROUP = "224.0.2.60";
    public static final int PING_PORT = 4445;
    private static final long PING_INTERVAL = 1500L;
    private final String motd;
    private final DatagramSocket socket;
    private boolean isRunning = true;
    private final String serverAddress;

    public LanServerPinger(String $$0, String $$1) throws IOException {
        super("LanServerPinger #" + UNIQUE_THREAD_ID.incrementAndGet());
        this.motd = $$0;
        this.serverAddress = $$1;
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        this.socket = new DatagramSocket();
    }

    public void run() {
        String $$0 = LanServerPinger.createPingString(this.motd, this.serverAddress);
        byte[] $$1 = $$0.getBytes(StandardCharsets.UTF_8);
        while (!this.isInterrupted() && this.isRunning) {
            try {
                InetAddress $$2 = InetAddress.getByName((String)MULTICAST_GROUP);
                DatagramPacket $$3 = new DatagramPacket($$1, $$1.length, $$2, 4445);
                this.socket.send($$3);
            }
            catch (IOException $$4) {
                LOGGER.warn("LanServerPinger: {}", (Object)$$4.getMessage());
                break;
            }
            try {
                LanServerPinger.sleep((long)1500L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public void interrupt() {
        super.interrupt();
        this.isRunning = false;
    }

    public static String createPingString(String $$0, String $$1) {
        return "[MOTD]" + $$0 + "[/MOTD][AD]" + $$1 + "[/AD]";
    }

    public static String parseMotd(String $$0) {
        int $$1 = $$0.indexOf("[MOTD]");
        if ($$1 < 0) {
            return "missing no";
        }
        int $$2 = $$0.indexOf("[/MOTD]", $$1 + "[MOTD]".length());
        if ($$2 < $$1) {
            return "missing no";
        }
        return $$0.substring($$1 + "[MOTD]".length(), $$2);
    }

    public static String parseAddress(String $$0) {
        int $$1 = $$0.indexOf("[/MOTD]");
        if ($$1 < 0) {
            return null;
        }
        int $$2 = $$0.indexOf("[/MOTD]", $$1 + "[/MOTD]".length());
        if ($$2 >= 0) {
            return null;
        }
        int $$3 = $$0.indexOf("[AD]", $$1 + "[/MOTD]".length());
        if ($$3 < 0) {
            return null;
        }
        int $$4 = $$0.indexOf("[/AD]", $$3 + "[AD]".length());
        if ($$4 < $$3) {
            return null;
        }
        return $$0.substring($$3 + "[AD]".length(), $$4);
    }
}