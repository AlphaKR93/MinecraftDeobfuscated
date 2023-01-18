/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Object
 */
package net.minecraft.client.renderer.texture;

public interface SpriteTicker
extends AutoCloseable {
    public void tickAndUpload(int var1, int var2);

    public void close();
}