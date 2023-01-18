/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Object
 */
package net.minecraft.server.packs.resources;

import net.minecraft.server.packs.resources.ResourceManager;

public interface CloseableResourceManager
extends ResourceManager,
AutoCloseable {
    public void close();
}