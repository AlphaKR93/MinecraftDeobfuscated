/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.net.HostAndPort
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.net.IDN
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer.resolver;

import com.google.common.net.HostAndPort;
import com.mojang.logging.LogUtils;
import java.net.IDN;
import org.slf4j.Logger;

public final class ServerAddress {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final HostAndPort hostAndPort;
    private static final ServerAddress INVALID = new ServerAddress(HostAndPort.fromParts((String)"server.invalid", (int)25565));

    public ServerAddress(String $$0, int $$1) {
        this(HostAndPort.fromParts((String)$$0, (int)$$1));
    }

    private ServerAddress(HostAndPort $$0) {
        this.hostAndPort = $$0;
    }

    public String getHost() {
        try {
            return IDN.toASCII((String)this.hostAndPort.getHost());
        }
        catch (IllegalArgumentException $$0) {
            return "";
        }
    }

    public int getPort() {
        return this.hostAndPort.getPort();
    }

    public static ServerAddress parseString(String $$0) {
        if ($$0 == null) {
            return INVALID;
        }
        try {
            HostAndPort $$1 = HostAndPort.fromString((String)$$0).withDefaultPort(25565);
            if ($$1.getHost().isEmpty()) {
                return INVALID;
            }
            return new ServerAddress($$1);
        }
        catch (IllegalArgumentException $$2) {
            LOGGER.info("Failed to parse URL {}", (Object)$$0, (Object)$$2);
            return INVALID;
        }
    }

    public static boolean isValidAddress(String $$0) {
        try {
            HostAndPort $$1 = HostAndPort.fromString((String)$$0);
            String $$2 = $$1.getHost();
            if (!$$2.isEmpty()) {
                IDN.toASCII((String)$$2);
                return true;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return false;
    }

    static int parsePort(String $$0) {
        try {
            return Integer.parseInt((String)$$0.trim());
        }
        catch (Exception exception) {
            return 25565;
        }
    }

    public String toString() {
        return this.hostAndPort.toString();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ServerAddress) {
            return this.hostAndPort.equals((Object)((ServerAddress)$$0).hostAndPort);
        }
        return false;
    }

    public int hashCode() {
        return this.hostAndPort.hashCode();
    }
}