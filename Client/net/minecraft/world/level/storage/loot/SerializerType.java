/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.storage.loot;

import net.minecraft.world.level.storage.loot.Serializer;

public class SerializerType<T> {
    private final Serializer<? extends T> serializer;

    public SerializerType(Serializer<? extends T> $$0) {
        this.serializer = $$0;
    }

    public Serializer<? extends T> getSerializer() {
        return this.serializer;
    }
}