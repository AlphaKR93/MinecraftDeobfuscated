/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  org.slf4j.Logger
 */
package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;

public class PacketUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> $$0, T $$1, ServerLevel $$2) throws RunningOnDifferentThreadException {
        PacketUtils.ensureRunningOnSameThread($$0, $$1, $$2.getServer());
    }

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> $$0, T $$1, BlockableEventLoop<?> $$2) throws RunningOnDifferentThreadException {
        if (!$$2.isSameThread()) {
            $$2.executeIfPossible(() -> {
                if ($$1.getConnection().isConnected()) {
                    try {
                        $$0.handle($$1);
                    }
                    catch (Exception $$2) {
                        if ($$1.shouldPropagateHandlingExceptions()) {
                            throw $$2;
                        }
                        LOGGER.error("Failed to handle packet {}, suppressing error", (Object)$$0, (Object)$$2);
                    }
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: {}", (Object)$$0);
                }
            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
        }
    }
}