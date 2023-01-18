/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.net.InetSocketAddress
 */
package net.minecraft.client.multiplayer.resolver;

import java.net.InetSocketAddress;

public interface ResolvedServerAddress {
    public String getHostName();

    public String getHostIp();

    public int getPort();

    public InetSocketAddress asInetSocketAddress();

    public static ResolvedServerAddress from(final InetSocketAddress $$0) {
        return new ResolvedServerAddress(){

            @Override
            public String getHostName() {
                return $$0.getAddress().getHostName();
            }

            @Override
            public String getHostIp() {
                return $$0.getAddress().getHostAddress();
            }

            @Override
            public int getPort() {
                return $$0.getPort();
            }

            @Override
            public InetSocketAddress asInetSocketAddress() {
                return $$0;
            }
        };
    }
}