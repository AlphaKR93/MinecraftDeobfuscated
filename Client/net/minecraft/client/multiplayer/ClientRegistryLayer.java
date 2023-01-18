/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.client.multiplayer;

import java.util.List;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public enum ClientRegistryLayer {
    STATIC,
    REMOTE;

    private static final List<ClientRegistryLayer> VALUES;
    private static final RegistryAccess.Frozen STATIC_ACCESS;

    public static LayeredRegistryAccess<ClientRegistryLayer> createRegistryAccess() {
        return new LayeredRegistryAccess<ClientRegistryLayer>(VALUES).replaceFrom(STATIC, STATIC_ACCESS);
    }

    static {
        VALUES = List.of((Object[])ClientRegistryLayer.values());
        STATIC_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }
}