/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.net.InetAddress
 *  java.net.InetSocketAddress
 *  java.net.UnknownHostException
 *  java.util.Optional
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.slf4j.Logger;

@FunctionalInterface
public interface ServerAddressResolver {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ServerAddressResolver SYSTEM = $$0 -> {
        try {
            InetAddress $$1 = InetAddress.getByName((String)$$0.getHost());
            return Optional.of((Object)ResolvedServerAddress.from(new InetSocketAddress($$1, $$0.getPort())));
        }
        catch (UnknownHostException $$2) {
            LOGGER.debug("Couldn't resolve server {} address", (Object)$$0.getHost(), (Object)$$2);
            return Optional.empty();
        }
    };

    public Optional<ResolvedServerAddress> resolve(ServerAddress var1);
}