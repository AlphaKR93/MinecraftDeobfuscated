/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.network;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;

public interface PacketSendListener {
    public static PacketSendListener thenRun(final Runnable $$0) {
        return new PacketSendListener(){

            @Override
            public void onSuccess() {
                $$0.run();
            }

            @Override
            @Nullable
            public Packet<?> onFailure() {
                $$0.run();
                return null;
            }
        };
    }

    public static PacketSendListener exceptionallySend(final Supplier<Packet<?>> $$0) {
        return new PacketSendListener(){

            @Override
            @Nullable
            public Packet<?> onFailure() {
                return (Packet)$$0.get();
            }
        };
    }

    default public void onSuccess() {
    }

    @Nullable
    default public Packet<?> onFailure() {
        return null;
    }
}